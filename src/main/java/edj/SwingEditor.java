package edj;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

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
			for (String s : input.split("\n")) {
				list.add(s);
			}
			return list;
		}};
	protected JTextArea textView;
	protected JTextField commandField;
	protected JCheckBoxMenuItem lineNumsCB;
	final int XPAD = 5, YPAD = 5;

	SwingEditor(String fileName) {

		// Main window layout
		textView = new JTextArea(20, 80);
		// Will need textChangedListener and SelectionChangedListener
		//textView.addKeyListener(tvKeyListener);
		add(BorderLayout.CENTER, textView);
		commandField = new JTextField();
		commandField.addActionListener(e->doCommand(e));
		commandField.setBorder(BorderFactory.createTitledBorder("Command"));
		add(BorderLayout.SOUTH, commandField);
		pack();

		// File/Edit/View menu
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);

		JMenu fileMenu = new JMenu("File");
		mb.add(fileMenu);
		final JMenuItem openMI = new JMenuItem("Open");
		openMI.addActionListener(openFile);
		fileMenu.add(openMI);
		final JMenuItem saveMI = new JMenuItem("Save");
		saveMI.setEnabled(false);
		fileMenu.add(saveMI);
		final JMenuItem saveAsMI = new JMenuItem("SaveAs");
		saveAsMI.setEnabled(false);
		fileMenu.add(saveAsMI);
		final JMenuItem closeAsMI = new JMenuItem("Close");
		closeAsMI.setEnabled(false);
		fileMenu.add(closeAsMI);
		fileMenu.addSeparator();
		JMenuItem quitMI = new JMenuItem("Exit");
		quitMI.addActionListener(this::doQuit);
		fileMenu.add(quitMI);

		JMenu editMenu = new JMenu("Edit");
		mb.add(editMenu);
		final JMenuItem cutMI = new JMenuItem("Cut");
		cutMI.setEnabled(false);
		editMenu.add(cutMI);
		final JMenuItem copyMI = new JMenuItem("Copy");
		copyMI.setEnabled(false);
		editMenu.add(copyMI);
		final JMenuItem pasteMI = new JMenuItem("Paste");
		pasteMI.setEnabled(false);
		editMenu.add(pasteMI);

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

		/** Execute one command-line editor command */
	protected void doCommand(ActionEvent e) {
		// Old-time vi/vim users may type a : at start of command, strip it.
		String line = commandField.getText();
		if (line.charAt(0) == ':') {
			line = line.substring(1);
		}
		
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
			((JTextComponent)e.getSource()).setText("");
			refresh();
		}
	}

	protected void refresh() {
		commandField.setText("");
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