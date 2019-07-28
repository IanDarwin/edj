package edj;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * A simple but usable editor based on a Swing UI and the
 * "edj" command set accessible from a command window at the
 * bottom of the main screen. This is a bit complicated
 * because there are 'two masters' or 'two truths'; both our bufprims
 * and the JTextArea have their own view of the text, and we have
 * to keep them in sync. Reloading them completely on every change
 * would be incredibly expensive on large files, since you may get, for
 * example, an InsertEvent on every character you type into the
 * JTextArea.
 * @author Ian Darwin
 */
public class SwingEditor extends JFrame {

	private static final long serialVersionUID = 7029580204703610734L;

	public static void main(String[] args) {
		String fileName = (args.length == 1) ? args[0] : null;
		new SwingEditor(fileName).setVisible(true);
	}

	protected BufferPrims buffer;
	protected boolean mUnsavedChanges;
	protected Commands commands;

	// GUI controls
	protected JTextArea textView;
	protected JComboBox<String> history;
	final int XPAD = 5, YPAD = 5;
	// Undo/Redo support using Swing's Undo Manager
	private UndoManager mUndoManager = new UndoManager();
	private UndoAction undoAction;
	private RedoAction redoAction;

	/** The only Constructor */
	SwingEditor(String fileName) {

		// Some things are best done before starting up Swing:
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// Main window layout
		textView = new JTextArea(20, 80);
		textView.setFont(new Font("lucida-sans", Font.BOLD, 12));
		// textView.setHint("Your text will appear here");
		add(BorderLayout.CENTER, new JScrollPane(textView));

		// A narrow column at the left for the line numbers
		Vector<Integer> x = new Vector<>();
		int[] nums = new int[] {0,1,2,3,4,5,6,7,8,9,10};
		for (int i : nums)
			x.add(i);
		JList<Integer> lineNumsColumn = new JList<>(x);
		lineNumsColumn.setFont(textView.getFont());
		lineNumsColumn.setFixedCellHeight(textView.getFont().getBaselineFor('A'));
		lineNumsColumn.setEnabled(false);	// no actions here
		lineNumsColumn.setVisible(false);	// initially off (maybe get from prefs?)
		add(BorderLayout.WEST, lineNumsColumn);

		// Main data structures
		buffer = new BufferPrimsJText(textView);
		commands = new Commands(buffer);
		// Redefine append: 'a' adds one line; for multi, just type on screen.
		commands.setCommand('a', pc -> {
			buffer.addLine(pc.operands);
		});
		// Redefine undo
		commands.setCommand('u', e -> undoAction.actionPerformed(null));
		// A Debug option
		commands.setCommand('D', pc -> {
			for (int i = 0; i < buffer.size(); i++) {
				System.out.println(i + " " + buffer.getLine(i));
			}
		});

		// Bottom panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createTitledBorder("Command"));
		history = new JComboBox<String>(new String[] {"# Commands Here"});
		history.setEditable(true);
		bottomPanel.add(history);
		JButton goButton = new JButton("Go");
		history.addActionListener(e -> goButton.requestFocus());
		goButton.addActionListener(e -> doCommand(e));
 
		bottomPanel.add(goButton);
		add(BorderLayout.SOUTH, bottomPanel);

		// MENU STUFF -- File/Edit/View menu
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
		cutMI.addActionListener(e -> textView.cut());
		editMenu.add(cutMI);
		final JMenuItem copyMI = new JMenuItem("Copy");
		copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
		copyMI.addActionListener(e -> textView.copy());
		editMenu.add(copyMI);
		final JMenuItem pasteMI = new JMenuItem("Paste");
		pasteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
		pasteMI.addActionListener(e -> textView.paste());
		editMenu.add(pasteMI);
		editMenu.addSeparator();

		// Edit Menu ends with Undo/Redo actions
		undoAction = new UndoAction();
		editMenu.add(undoAction);
		undoAction.updateGuiState();

		redoAction = new RedoAction();
		editMenu.add(redoAction);
		redoAction.updateGuiState();

		textView.getDocument().addUndoableEditListener(e -> {
			//Remember the edit and update the menus
			mUndoManager.addEdit(e.getEdit());
			undoAction.updateGuiState();
			redoAction.updateGuiState();
        });

		JMenu viewMenu = new JMenu("View");
		mb.add(viewMenu);
		JCheckBoxMenuItem lineNumsCB = new JCheckBoxMenuItem("Show line numbers");
		lineNumsCB.setEnabled(false);
		viewMenu.add(lineNumsCB);

		JMenu helpMenu = new JMenu("Help");
		mb.add(helpMenu);
		JMenuItem aboutMI = new JMenuItem("About");
		aboutMI.addActionListener(e->JOptionPane.showMessageDialog(this,
				"SwingEditor v0.0"));
		helpMenu.add(aboutMI);

		// Main window listener
		addWindowListener(windowCloser);

		pack();
		
		history.setPreferredSize(history.getSize());
		history.removeAllItems();

		if (fileName != null) {
			readFile(fileName);
		}
		textView.repaint();
	}

	/**
	 * Prompt the user to choose a file, then read it, replacing
	 * the buffer contents. Turns off the listeners around this so we don't
	 * get an insert event (might cause looping...).
	 * @param fileName
	 */
	private void openFile(String fileName) {
		textView.setText("");
		buffer.clearBuffer();
		commands.readFile(fileName);
	}

	/**
	 * Read the given file (append to buffer), turning off the listener so
	 * we don't get an insert event ("There's too much confusion here").
	 * @param fileName
	 */
	private void readFile(String fileName) {
		commands.readFile(fileName);
	}

	protected void doCut() {
		System.out.println("Cut invoked");
	}


	/** 
	 * Execute one command-line editor command, from 
	 * commandText or from history
	 */
	protected void doCommand(ActionEvent e) {
		String line = (String) history.getSelectedItem();
		// System.out.println("line = " + line);
		if (line == null || line.length() == 0)
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
		EditCommand c = Commands.commands[pl.cmdLetter];
		if (c == null) {
			System.out.println("? Unknown command in " + line);
		} else {
			c.execute(pl);
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
				openFile(file.getAbsolutePath());
				setTitle(file.getName());
				textView.repaint();
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

	class UndoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent evt) {
			try {
				mUndoManager.undo();
			} catch (CannotUndoException e) {
				JOptionPane.showMessageDialog(SwingEditor.this, "Unable to undo: " + e, "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			updateGuiState();
			redoAction.updateGuiState();
		}

		/** Could be inlined but must be called from RedoAction so must be a method */
		void updateGuiState() {
				final boolean canUndo = mUndoManager.canUndo();
				setEnabled(canUndo);
				putValue(NAME, canUndo ? mUndoManager.getUndoPresentationName() : "Undo");
				setUnsavedChanges(canUndo);
		}
	};

	class RedoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent evt) {
			try {
				mUndoManager.redo();
			} catch (CannotRedoException e) {
				JOptionPane.showMessageDialog(SwingEditor.this, "Unable to redo: " + e, "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			updateGuiState();
			undoAction.updateGuiState();
		}

		void updateGuiState() {
				setEnabled(mUndoManager.canRedo());
				putValue(NAME, mUndoManager.canRedo() ? mUndoManager.getRedoPresentationName() : "Redo");
		}
	};
	

	/**
	 * Set saved/unsaved status variable AND titlebar
	 */
	public void setUnsavedChanges(boolean unsavedChanges) {
		if (this.mUnsavedChanges == unsavedChanges) {
			// Redundant, so just ignore it.
			return;
		}
		if (unsavedChanges) {	// Add unsaved flag
			setTitle("*" + " " + getTitle());
		} else if (getTitle().startsWith("* ")) {		// Chop unsaved flag
			setTitle(getTitle().substring(2));
		}
		this.mUnsavedChanges = unsavedChanges;
	}
}
