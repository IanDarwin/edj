package edj;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

/** This will someday evolve into
 * A simple but usable editor based on a Swing UI and the
 * "edj" command set accessible from a command window at the
 * bottom of the main screen.
 * @author Ian Darwin
 */
public class SwingEditor extends JFrame {

	private static final long serialVersionUID = 7029580204703610734L;

	public static void main(String[] args) {
		String fileName = (args.length == 1) ? args[0] : null;
		new SwingEditor(fileName).setVisible(true);
	}

	protected BufferPrims buffer = new BufferPrimsWithUndo();
	Commands commands = new Commands(buffer) {
		@Override
		protected List<String> gatherLines() {
			final String input = JOptionPane.showInputDialog("New line(s):");
			final List<String> list = new ArrayList<>();
			if (input != null && input.length() > 0)
				for (String s : input.split("\n")) {
					list.add(s);
				}
			return list;
		}};
	protected JTextArea textView;
	protected JCheckBoxMenuItem lineNumsCB;
	protected TitledBorder listBorder;
	protected JComboBox<String> history;
	final int XPAD = 5, YPAD = 5;

	SwingEditor(String fileName) {

		// Main window layout
		textView = new JTextArea(20, 80);
		// Will need textChangedListener and SelectionChangedListener
		//textView.addKeyListener(tvKeyListener);
		listBorder = BorderFactory.createTitledBorder("Editing");
		textView.setBorder(listBorder);
		add(BorderLayout.CENTER, textView);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createTitledBorder("Command"));
		history = new JComboBox<String>(new String[] {"# Commands Here"});
		history.removeAllItems();
		history.setEditable(true);
		bottomPanel.add(history);
		JButton goButton = new JButton("Go");
		history.addActionListener(e -> goButton.requestFocus());
		goButton.addActionListener(e -> doCommand(e));
		bottomPanel.add(goButton);
		add(BorderLayout.SOUTH, bottomPanel);

		// File/Edit/View menu
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);

		JMenu fileMenu = new JMenu("File");
		mb.add(fileMenu);
		final JMenuItem openMI = new JMenuItem("Open", 'O');
		final int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
		openMI.addActionListener(openFile);
		fileMenu.add(openMI);
		final JMenuItem saveMI = new JMenuItem("Save");
		saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
		saveMI.setEnabled(false);
		fileMenu.add(saveMI);
		final JMenuItem saveAsMI = new JMenuItem("SaveAs");
		saveAsMI.setEnabled(false);
		fileMenu.add(saveAsMI);
		final JMenuItem closeMI = new JMenuItem("Close");
		closeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
		closeMI.setEnabled(false);
		fileMenu.add(closeMI);
		fileMenu.addSeparator();
		JMenuItem quitMI = new JMenuItem("Exit");
		quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, mask));
		quitMI.addActionListener(this::doQuit);
		fileMenu.add(quitMI);

		JMenu editMenu = new JMenu("Edit");
		mb.add(editMenu);
		final JMenuItem cutMI = new JMenuItem("Cut");
		cutMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
		cutMI.addActionListener(e -> doCut());
		//cutMI.setEnabled(false);
		editMenu.add(cutMI);
		final JMenuItem copyMI = new JMenuItem("Copy");
		copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
		copyMI.setEnabled(false);
		editMenu.add(copyMI);
		final JMenuItem pasteMI = new JMenuItem("Paste");
		pasteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
		pasteMI.setEnabled(false);
		editMenu.add(pasteMI);
		editMenu.addSeparator();
		final JMenuItem undoMI = new JMenuItem("Undo");
		undoMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask));
		undoMI.addActionListener(e -> buffer.undo());
		editMenu.add(undoMI);

		JMenu viewMenu = new JMenu("View");
		mb.add(viewMenu);
		lineNumsCB = new JCheckBoxMenuItem("Show line numbers");
		viewMenu.add(lineNumsCB);

		JMenu helpMenu = new JMenu("Help");
		mb.add(helpMenu);
		JMenuItem aboutMI = new JMenuItem("About");
		aboutMI.addActionListener(e->JOptionPane.showMessageDialog(this, "SwingEditor v0.0"));
		helpMenu.add(aboutMI);

		// Main window listener
		addWindowListener(windowCloser);
		
		pack();

		if (fileName != null) {
			commands.readFile(fileName);
		} else {
			// Temporary hack for early development
			buffer.addLine("This is some dummy text to start you off.");
			buffer.addLine("lorem ipsem dolor");
			buffer.addLine("nunciat verbatim est");
			buffer.addLine("cualquieres.");
		}
		refresh();
	}
	
	protected void doCut() {
		System.out.println("Cut invoked");
	}

	/** Execute one command-line editor command, from 
	 * commandText or from history
	 */
	protected void doCommand(ActionEvent e) {
		String line = (String) history.getSelectedItem();		
		System.out.println("line = " + line);
		if (line.length() == 0)
			return;
		// Old-time vi/vim users may type a : at start of command, strip it.
		if (line.charAt(0) == ':') {
			line = line.substring(1);
		}
		
		history.addItem(line);
		
		ParsedCommand pl = LineParser.parse(line, buffer);
		if (pl == null) {
			JOptionPane.showMessageDialog(this, "Could not parse command");
			return;
		}
		// System.out.printf("Line: %s, Parsed to %s\n", line, pl);
		EditCommand c = commands.commands[pl.cmdLetter];
		if (c == null) {
			System.out.println("? Unknown command in " + line);
		} else {
			c.execute(pl);
			refresh();
		}
	}

	protected void refresh() {
		// commandField.setText("");
		// BufferPrim line nums start at 1, not zero
		int topLine = 1;
		int fh = getFontMetrics(getFont()).getHeight();
		int numLines = getHeight() / fh;
		// int y = YPAD;
		StringBuilder sb = new StringBuilder();
		for (int i = topLine; i <= buffer.size() && i <= topLine + numLines; i++) {
			//g.drawString(buffer.get(i), XPAD, y += fh);
			if (lineNumsCB.isSelected())
				sb.append(i).append(' ');
			sb.append(buffer.getLine(i)).append("\n");
		}
		// return
		textView.setText(sb.toString());
		textView.repaint();
	}

	/**
	 * Choose and open a file
	 */
	ActionListener openFile = e -> {
		final JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file.isFile()) {
				commands.readFile(file.getAbsolutePath());
				listBorder.setTitle(file.getName());
				refresh();
			} else {
				JOptionPane.showMessageDialog(this, "Not a file: " + file);
			}
		}else {
			JOptionPane.showMessageDialog(this, "You did not choose a file.");
		}
	};

	/** Common code path for leaving the application */
	protected void doQuit(ActionEvent e) {
		// TODO check for unsaved changes
		System.exit(0);
	}

	protected WindowListener windowCloser = new WindowAdapter() {
		public void windowClosing(WindowEvent we) {
			doQuit(null);
		};
	};
}
