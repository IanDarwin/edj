package edj;

import java.awt.BorderLayout;
import java.awt.Graphics;
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
	final int XPAD = 5, YPAD = 5;

	SwingEditor(String fileName) {

		// Main window layout
		textView = new MyTextArea(20, 80);
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
		fileMenu.addSeparator();
		JMenuItem quitMI = new JMenuItem("Exit");
		quitMI.addActionListener(this::doQuit);
		fileMenu.add(quitMI);

		JMenu editMenu = new JMenu("Edit");
		mb.add(editMenu);
		editMenu.add(new JMenuItem("Not written yet"));

		JMenu viewMenu = new JMenu("View");
		mb.add(viewMenu);
		JCheckBoxMenuItem lineNumsCB = new JCheckBoxMenuItem("Show line numbers");
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
	}

	class MyTextArea extends JTextArea {
		private static final long serialVersionUID = 1L;

		MyTextArea(int rows, int columns) {
			super(rows, columns);
		}

		/**
		 * To get the text displayed, in paint() we grab the
		 * given lines from the bufferprims before calling
		 * super.paint(). Not salutory but works for v0.0.
		 */
		@Override
		public void paint(Graphics g) {
			// BufferPrim line nums start at 1, not zero
			int topLine = 1;
			int fh = getFontMetrics(getFont()).getHeight();
			int numLines = getHeight() / fh;
			// int y = YPAD;
			StringBuilder sb = new StringBuilder();
			for (int i = topLine; i <= buffer.size() && i <= topLine + numLines; i++) {
				//g.drawString(buffer.get(i), XPAD, y += fh);
				sb.append(buffer.getLine(i)).append("\n");
			}
			// return
			setText(sb.toString());
			super.paint(g);
		}
	}

	/** Execute one command-line editor command */
	protected void doCommand(ActionEvent e) {
		// Old-time vi/vim users may type a : at start of command, strip it.
		String line = commandField.getText();
		if (line.charAt(0) == ':') {
			line = line.substring(1);
		}
		
		System.out.println("Command: " + line);
		ParsedCommand pl = LineParser.parse(line, buffer);
		EditCommand c = commands.commands[pl.cmdLetter];
		if (c == null) {
			System.out.println("? Unknown command in " + line);
		} else {
			c.execute(pl);
			commandField.setText("");
			// Should add this to a finite stack for recall.
			textView.repaint();
		}
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
			} else {
				JOptionPane.showMessageDialog(this, "Not a file: " + file);
			}
		}else {
			JOptionPane.showMessageDialog(this, "You did not choose a file.");
		}
	};

	/** Common code path for leaving the application */
	@SuppressWarnings("")
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
