package edj;

public interface UndoManager {

	/** Push an Undo - normally "for internal use only" */
	void pushUndo(String name, Runnable r);
	
	/** Ppop an undo - for authorized use only */
	void popUndo();

	/** If there are any undoable actions, pop the top one and run it. */
	void undo();

}