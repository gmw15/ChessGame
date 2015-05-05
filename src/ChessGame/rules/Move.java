/**
 * As all the pieces being moved is done by drawing, we just need to access the available
 * moves and paint the available squares a colour.
 */

package ChessGame.rules;

public class Move {
    // all the parameters for the move.
	public int sourceRow;
	public int sourceColumn;
	public int targetRow;
	public int targetColumn;
	
	public int score;
	public Piece capturedPiece;
	
	public Move(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		this.sourceRow = sourceRow;
		this.sourceColumn = sourceColumn;
		this.targetRow = targetRow;
		this.targetColumn = targetColumn;
	}
	
	@Override
	public String toString() {
		return Piece.getColumnString(sourceColumn)+"/"+Piece.getRowString(sourceRow)
		+" -> "+Piece.getColumnString(targetColumn)+"/"+Piece.getRowString(targetRow);
		//return sourceRow+"/"+sourceColumn+" -> "+targetRow+"/"+targetColumn;
	}

	public Move clone(){
		return new Move(sourceRow,sourceColumn,targetRow,targetColumn);
	}
}
