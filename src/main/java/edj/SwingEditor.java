package edj;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBoxMenuItem;
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
		new SwingEditor().setVisible(true);
	}

	protected BufferPrims buffer = new BufferPrimsWithUndo();
	protected JTextArea textView;
	protected JTextField commandField;
	final int XPAD = 5, YPAD = 5;

	SwingEditor() {

		// Main window layout
		textView = new MyTextArea(15, 80);
		textView.addKeyListener(tvKeyListener);
		add(BorderLayout.CENTER, textView);
		commandField = new JTextField();
		commandField.addActionListener(e->doCommand(e));
		add(BorderLayout.SOUTH, commandField);
		pack();

		// File/Edit/View menu
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);

		JMenu fileMenu = new JMenu("File");
		mb.add(fileMenu);
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

		// Temporary hack for early development
		buffer.addLine("This is the start");
		buffer.addLine("of a very very");
		buffer.addLine("short story.");
	}

	class MyTextArea extends JTextArea {
		private static final long serialVersionUID = 1L;

		MyTextArea(int rows, int columns) {
			super(rows, columns);
		}

		@Override
		public void paint(Graphics g) {
			// BufferPrim line nums start at 1, not zero
			int topLine = 1;
			//int fh = getFontMetrics(getFont()).getHeight();
			//int y = YPAD;
			StringBuilder sb = new StringBuilder();
			for (int i = topLine; i <= buffer.size() && i <= topLine + i; i++) {
				//g.drawString(buffer.get(i), XPAD, y += fh);
				sb.append(buffer.getLine(i)).append("\n");
			}
			setText(sb.toString());
			super.paint(g);
		}
	}

	protected void doCommand(ActionEvent e) {
		System.out.println("Command: " + commandField.getText());
	}

	protected void doQuit(ActionEvent e) {
		// TODO check for unsaved changes
		System.exit(0);
	}

	protected KeyListener tvKeyListener = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO: Map e.getX() into buffer, mark that line as stale
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// EMPTY
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// EMPTY
		}
	};
}
