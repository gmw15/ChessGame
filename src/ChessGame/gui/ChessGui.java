package ChessGame.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChessGame.rules.ChessGame;
import ChessGame.rules.HandlePlayers;
import ChessGame.rules.Move;
import ChessGame.rules.PieceMoves;
import ChessGame.rules.Piece;

/**
 * all x and y coordinates point to the upper left position of a component all
 * lists are treated as 0 being the bottom and size-1 being the top piece
 * 
 * 
 * this is my user interface that i use for dragging my pieces around the board
 * the BOARD_START_X and BOARD_START_Y static int makes the board makes the board position
 * in the top left hand corner of the screen.
 * 
 * This ChessGui class is the main interface of my chess game, it is responsibile for showing applications
 * and repainting my pieces back onto the board after each move has been made.
 */
public class ChessGui extends JPanel implements HandlePlayers{
	
	
	
	private static final int BOARD_START_X = 0;
	private static final int BOARD_START_Y = 0;

	private static final int SQUARE_WIDTH = 50;
	private static final int SQUARE_HEIGHT = 50;

	private static final int PIECE_WIDTH = 48;
	private static final int PIECE_HEIGHT = 48;
	
	private static final int PIECES_START_X = BOARD_START_X + (int)(SQUARE_WIDTH/2.0 - PIECE_WIDTH/2.0);
	private static final int PIECES_START_Y = BOARD_START_Y + (int)(SQUARE_HEIGHT/2.0 - PIECE_HEIGHT/2.0);
	
	private static final int DRAG_TARGET_SQUARE_START_X = BOARD_START_X - (int)(PIECE_WIDTH/2.0);
	private static final int DRAG_TARGET_SQUARE_START_Y = BOARD_START_Y - (int)(PIECE_HEIGHT/2.0);

	private Image imgBackground;
	private JLabel lblGameState;
	
	private ChessGame chessGame;
	private List<GuiPiece> guiPieces = new ArrayList<GuiPiece>();

	private GuiPiece dragPiece;

	private Move lastMove;
	private Move currentMove;

	private boolean draggingGamePiecesEnabled;

	/**
	 * constructor - creating the user interface
	 * chessGame - the game to be presented
	 */
	public ChessGui(ChessGame chessGame) {
		this.setLayout(null);

		// background
		URL urlBackgroundImg = getClass().getResource("/ChessGame/gui/images/chessboard.png");
		this.imgBackground = new ImageIcon(urlBackgroundImg).getImage();
		
		// create chess game
		this.chessGame = chessGame;
		
		
		//wrap game pieces into their graphical representation
		for (Piece piece : this.chessGame.getPieces()) {
			createAndAddGuiPiece(piece);
		}
		

		// add listeners to enable drag and drop

		HighlightListeners listener = new HighlightListeners(this.guiPieces,
				this);
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);

		// label to display game state
		String labelText = this.getGameStateAsText();
		this.lblGameState = new JLabel(labelText);
		lblGameState.setBounds(0, 30, 140, 30);
		lblGameState.setForeground(Color.WHITE);
		this.add(lblGameState);

		// create application frame and set visible

		JFrame f = new JFrame();
		f.setSize(300, 140);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(this);
		f.setSize(417,437);
	}

	/**
	 * textual description of current game state
	 */
	private String getGameStateAsText() {
		String state = "unknown";
		switch (this.chessGame.getGameState()) {
			case ChessGame.GAME_STATE_BLACK: state = "It is blacks turn";break;
			case ChessGame.GAME_STATE_END_WHITE_WON: state = "white won";break;
			case ChessGame.GAME_STATE_END_BLACK_WON: state = "black won";break;
			case ChessGame.GAME_STATE_WHITE: state = "It is whites turn";break;
		}
		return state;
	}

	/**
	 * create a game piece
	 * 
	 * colour constant
	 * type constant
	 * x position of upper left corner
	 * y position of upper left corner
         * 
         * 
	 */
	private void createAndAddGuiPiece(Piece piece) {
		Image img = this.getImageForPiece(piece.getColor(), piece.getType());
		GuiPiece guiPiece = new GuiPiece(img, piece);
		this.guiPieces.add(guiPiece);
	}

	/**
	 * load image for given colour and type. This method translates the color and
	 * type information into a filename and loads that particular file.
	 * 
	 * colour constant
	 * type constant
	 * returns image
	 */
	private Image getImageForPiece(int color, int type) {

		String filename = "";

		filename += (color == Piece.COLOR_WHITE ? "w" : "b");
		switch (type) {
			case Piece.TYPE_BISHOP:
				filename += "b";
				break;
			case Piece.TYPE_KING:
				filename += "k";
				break;
			case Piece.TYPE_KNIGHT:
				filename += "n";
				break;
			case Piece.TYPE_PAWN:
				filename += "p";
				break;
			case Piece.TYPE_QUEEN:
				filename += "q";
				break;
			case Piece.TYPE_ROOK:
				filename += "r";
				break;
		}
		filename += ".png";

		URL urlPieceImg = getClass().getResource("/ChessGame/gui/images/" + filename);
		return new ImageIcon(urlPieceImg).getImage();
	}

	@Override
	protected void paintComponent(Graphics g) {
            /**
             * I am now drawing the board to iterate over all the possible locations to check whether
             * they are valid locations for the piece that is being dragged
             */

		// draw background
		g.drawImage(this.imgBackground, 0, 0, null);

		// draw pieces
		for (GuiPiece guiPiece : this.guiPieces) {
			if( !guiPiece.isCaptured()){
				g.drawImage(guiPiece.getImage(), guiPiece.getX(), guiPiece.getY(), null);
			}
		}
		
		// draw last move, if user is not dragging game piece
		if( !isUserDraggingPiece() && this.lastMove != null ){
			int highlightSourceX = convertColumnToX(this.lastMove.sourceColumn);
			int highlightSourceY = convertRowToY(this.lastMove.sourceRow);
			int highlightTargetX = convertColumnToX(this.lastMove.targetColumn);
			int highlightTargetY = convertRowToY(this.lastMove.targetRow);
			
			g.setColor(Color.RED);
			g.drawRoundRect( highlightSourceX+4, highlightSourceY+4, SQUARE_WIDTH-8, SQUARE_HEIGHT-8,10,10);
			g.drawRoundRect( highlightTargetX+4, highlightTargetY+4, SQUARE_WIDTH-8, SQUARE_HEIGHT-8,10,10);
			
		}
		
		//shows the location for when the user is making a move
		if( isUserDraggingPiece() ){
			
			PieceMoves moveValidator = this.chessGame.getMoveValidator();
			
			// iterate the complete board to check if target locations are valid
			for (int column = Piece.COLUMN_A; column <= Piece.COLUMN_H; column++) {
				for (int row = Piece.ROW_1; row <= Piece.ROW_8; row++) {
					int sourceRow = this.dragPiece.getPiece().getRow();
					int sourceColumn = this.dragPiece.getPiece().getColumn();
					
					// see if the move is available
					if( moveValidator.isMoveValid( new Move(sourceRow, sourceColumn, row, column), false) ){
						
						int highlightX = convertColumnToX(column);
						int highlightY = convertRowToY(row);
						
						// draw a black drop shadow by drawing a black rectangle with an offset of 1 pixel
						g.setColor(Color.BLACK);
						g.drawRoundRect( highlightX+5, highlightY+5, SQUARE_WIDTH-8, SQUARE_HEIGHT-8,10,10);
						// draw the highlight
						g.setColor(Color.GREEN);
						g.drawRoundRect( highlightX+4, highlightY+4, SQUARE_WIDTH-8, SQUARE_HEIGHT-8,10,10);
					}
				}
			}
		}
		
		
		// draw game state label
		this.lblGameState.setText(this.getGameStateAsText());
	}

	/**
	 * check if the user is currently dragging a game piece
	 * returns true - if the user is currently dragging a game piece
	 */
	private boolean isUserDraggingPiece() {
		return this.dragPiece != null;
	}

	/**
	 * returns current game state
	 */
	public int getGameState() {
		return this.chessGame.getGameState();
	}
	
	/**
	 * convert logical column into x coordinate
	 * param column
	 * return x coordinate for column
	 */
	public static int convertColumnToX(int column){
		return PIECES_START_X + SQUARE_WIDTH * column;
	}
	
	/**
	 * convert logical row into y coordinate
	 * param row
	 * return y coordinate for row
	 */
	public static int convertRowToY(int row){
		return PIECES_START_Y + SQUARE_HEIGHT * (Piece.ROW_8 - row);
	}
	
	/**
	 * convert x coordinate into logical column
	 * param x
	 * return logical column for x coordinate
	 */
	public static int convertXToColumn(int x){
		return (x - DRAG_TARGET_SQUARE_START_X)/SQUARE_WIDTH;
	}
	
	/**
	 * convert y coordinate into logical row
	 * param y
	 * return logical row for y coordinate
	 */
	public static int convertYToRow(int y){
		return Piece.ROW_8 - (y - DRAG_TARGET_SQUARE_START_Y)/SQUARE_HEIGHT;
	}

	/**
	 * change location of given piece, if the location is valid.
	 * If the location is not valid, move the piece back to its original
	 * position.
	 * param dragPiece
	 * param x
	 * param y
         * 
         * the users move is being stored here in a variable
         * 
	 */
	public void setNewPieceLocation(GuiPiece dragPiece, int x, int y) {
		int targetRow = ChessGui.convertYToRow(y);
		int targetColumn = ChessGui.convertXToColumn(x);
		
		Move move = new Move(dragPiece.getPiece().getRow(), dragPiece.getPiece().getColumn()
				, targetRow, targetColumn);
		if( this.chessGame.getMoveValidator().isMoveValid(move, true) ){
			this.currentMove = move;
		}else{
			dragPiece.resetToUnderlyingPiecePosition();
		}
	}

	/**
	 * set the game piece that is currently dragged by the user
	 */
	public void setDragPiece(GuiPiece guiPiece) {
		this.dragPiece = guiPiece;
	}
	
	/**
	 * return the gui piece that the user is currently dragging
	 */
	public GuiPiece getDragPiece(){
		return this.dragPiece;
	}
        
        /**
         * i interact with the user interface no matter when, even when its the computers turn
         * so the user interface us running on its own. 
         * the user interface returns a null value back, until its the computers turn
         */

	@Override
	public Move getMove() {
		this.draggingGamePiecesEnabled = true; 
		Move moveForExecution = this.currentMove;
		this.currentMove = null;
		return moveForExecution;
	}
        
        /**
         * the method moveSuccessfullyExecuted updates the moved piece which was stored in the 
         * lastMove method, which was also used for the highlighting the pieces.
         */

	@Override
	public void moveSuccessfullyExecuted(Move move) {
		// adjust GUI piece
		GuiPiece guiPiece = this.getGuiPieceAt(move.targetRow, move.targetColumn);
		if( guiPiece == null){
			throw new IllegalStateException("no guiPiece at "+move.targetRow+"/"+move.targetColumn);
		}
		guiPiece.resetToUnderlyingPiecePosition();
		
		// remember last move
		this.lastMove = move;
		
		// disable dragging until asked by ChessGame for the next move
		this.draggingGamePiecesEnabled = false;
				
		// repaint the new state
		this.repaint();
		
	}
	
	/**
	 * true - if the user is allowed to drag game pieces
	 */
	public boolean isDraggingGamePiecesEnabled(){
		return draggingGamePiecesEnabled;
	}

	/**
	 * get non-captured the gui piece at the specified position
	 * @return the gui piece at the specified position, null if there is no piece
	 */
	private GuiPiece getGuiPieceAt(int row, int column) {
		for (GuiPiece guiPiece : this.guiPieces) {
			if( guiPiece.getPiece().getRow() == row
					&& guiPiece.getPiece().getColumn() == column
					&& guiPiece.isCaptured() == false){
				return guiPiece;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		ChessGame chessGame = new ChessGame();
		ChessGui chessGui = new ChessGui(chessGame);
		chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
		chessGame.setPlayer(Piece.COLOR_BLACK, chessGui);
		new Thread(chessGame).start();
	}
}
