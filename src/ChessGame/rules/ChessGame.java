/**
 * This contains all the logic for setting up the game and moving the pieces.
 * This also gets the information about where all the pieces are, and the position of the game.
 * 
 * 
 * 
 * I am using a gamestate to enable the basic rules of chess. like for e.g. who won, who goes first and whos turn it is
 * 
 * The game flow logic will be controlled here, after the user makes their valid move, this will make the appropriate classes aware of this
 * and they will update the appropriate details.
 */

package ChessGame.rules;

import java.awt.event.MouseEvent;


import java.util.ArrayList;
import java.util.List;


import ChessGame.consoleReader.ChessConsoleReader;
import javax.swing.JOptionPane;

public class ChessGame implements Runnable{

	public int gameState = GAME_STATE_WHITE;
	public static final int GAME_STATE_WHITE = 0;
	public static final int GAME_STATE_BLACK = 1;
	public static final int GAME_STATE_END_BLACK_WON = 2;
	public static final int GAME_STATE_END_WHITE_WON = 3;

	// 0 = bottom, size = top
	public List<Piece> pieces = new ArrayList<Piece>();
	public List<Piece> capturedPieces = new ArrayList<Piece>();

	private PieceMoves moveValidator;
	private HandlePlayers blackPlayerHandler;
	private HandlePlayers whitePlayerHandler;
	private HandlePlayers activePlayerHandler;
        
        static int ChooseQueen=-1;
        static int ChooseKnight=-1;
        
        
        
        
	/**
	 * initialize game
	 */
	public ChessGame() {
            

		this.moveValidator = new PieceMoves(this);

		// create and place pieces
                //place all the pieces on the board
		// rook, knight, bishop, queen, king, bishop, knight, and rook
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_ROOK, Piece.ROW_1, Piece.COLUMN_A);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_KNIGHT, Piece.ROW_1,
				Piece.COLUMN_B);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_BISHOP, Piece.ROW_1,
				Piece.COLUMN_C);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_QUEEN, Piece.ROW_1,
				Piece.COLUMN_D);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_KING, Piece.ROW_1, Piece.COLUMN_E);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_BISHOP, Piece.ROW_1,
				Piece.COLUMN_F);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_KNIGHT, Piece.ROW_1,
				Piece.COLUMN_G);
		createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_ROOK, Piece.ROW_1, Piece.COLUMN_H);

		// pawns
		int currentColumn = Piece.COLUMN_A;
		for (int i = 0; i < 8; i++) {
			createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_PAWN, Piece.ROW_2,
					currentColumn);
			currentColumn++;
		}
                
                //black pieces
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_ROOK, Piece.ROW_8, Piece.COLUMN_A);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_KNIGHT, Piece.ROW_8,
				Piece.COLUMN_B);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_BISHOP, Piece.ROW_8,
				Piece.COLUMN_C);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_QUEEN, Piece.ROW_8,
				Piece.COLUMN_D);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_KING, Piece.ROW_8, Piece.COLUMN_E);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_BISHOP, Piece.ROW_8,
				Piece.COLUMN_F);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_KNIGHT, Piece.ROW_8,
				Piece.COLUMN_G);
		createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_ROOK, Piece.ROW_8, Piece.COLUMN_H);

		// pawns
		currentColumn = Piece.COLUMN_A;
		for (int i = 0; i < 8; i++) {
			createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_7,
					currentColumn);
			currentColumn++;
		}
                
	}
	
	/**
	 * set the client/player for the specified piece color
         * 
         * this is where we introduce my switch statement, which takes the status of the game,
         * for e.g. if its black turn it will take the case of blackPlayerHandler and COLOR_BLACK
         * and white will be whitePlayerHandler and COLOR_WHITE etc.
         * 
	 */
	public void setPlayer(int pieceColor, HandlePlayers playerHandler){
		switch (pieceColor) {
			case Piece.COLOR_BLACK: this.blackPlayerHandler = playerHandler; break;
			case Piece.COLOR_WHITE: this.whitePlayerHandler = playerHandler; break;
			default: throw new IllegalArgumentException("Invalid pieceColor: "+pieceColor);
		}
	}

	/**
	 * start main game flow
	 */
	public void startGame(){
		// check if all players are ready
		System.out.println("ChessGame: waiting for players");
		while (this.blackPlayerHandler == null || this.whitePlayerHandler == null){
			// if players are still missing
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// set start player
		this.activePlayerHandler = this.whitePlayerHandler;
		
		// start game flow
		System.out.println("ChessGame: starting game");
		while(!isGameEndConditionReached()){
			waitForMove();
			swapActivePlayer();
		}
		
		System.out.println("ChessGame: game ended");
		ChessConsoleReader.printCurrentGameState(this);
		if(this.gameState == ChessGame.GAME_STATE_END_BLACK_WON){
			
                        JOptionPane.showMessageDialog(null, "Black Has Won!");
			
		}else if(this.gameState == ChessGame.GAME_STATE_END_WHITE_WON){
			
                        JOptionPane.showMessageDialog(null, "White Has Won!");
			
		}else{
			throw new IllegalStateException("Illegal end state: "+this.gameState);
		}
	}

	/**
	 * swap what player is being used in the game
	 */
	private void swapActivePlayer() {
		if( this.activePlayerHandler == this.whitePlayerHandler ){
			this.activePlayerHandler = this.blackPlayerHandler;
		}else{
			this.activePlayerHandler = this.whitePlayerHandler;
		}
		
		this.changeGameState();
	}

	/**
	 * Wait for client/player move and execute it.
	 * Notify all clients/players about successful execution of move.
	 */
	private void waitForMove() {
		Move move = null;
		// wait for a valid move
		do{
			move = this.activePlayerHandler.getMove();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			if( move != null && this.moveValidator.isMoveValid(move, false) ){
				break;
			}else if( move != null && !this.moveValidator.isMoveValid(move,true)){
				System.out.println("provided move was invalid: "+move);
				
				ChessConsoleReader.printCurrentGameState(this);
				move=null;
				System.exit(0);
			}
		}while(move == null);
		
		//execute move
		boolean success = this.movePiece(move);
		if(success){
			this.blackPlayerHandler.moveSuccessfullyExecuted(move);
			this.whitePlayerHandler.moveSuccessfullyExecuted(move);
		}else{
			throw new IllegalStateException("move was valid, but failed to execute it");
		}
	}

	/**
	 * create piece instance and add it to the internal list of pieces
	 * 
	 * colour
         * type
         * row
         * column
	 */
	public void createAndAddPiece(int color, int type, int row, int column) {
		Piece piece = new Piece(color, type, row, column);
		this.pieces.add(piece);
                
	}

	/**
	 * Move piece to the specified location. If the target location is occupied
	 * by an opponent piece, that piece is marked as 'captured'. If the move
	 * could not be executed successfully, 'false' is returned and the game
	 * state does not change.
	 * 
	 * executes move
	 * return true if move was successfully
	 */
	public boolean movePiece(Move move) {
		//set captured piece in move
		// this information is needed in the undoMove() method.
		move.capturedPiece = this.getNonCapturedPieceAtLocation(move.targetRow, move.targetColumn);
		
		Piece piece = getNonCapturedPieceAtLocation(move.sourceRow, move.sourceColumn);

		// check if the move is capturing an opponent piece
		int opponentColor = (piece.getColor() == Piece.COLOR_BLACK ? Piece.COLOR_WHITE
				: Piece.COLOR_BLACK);
		if (isNonCapturedPieceAtLocation(opponentColor, move.targetRow, move.targetColumn)) {
			// handle captured piece
			Piece opponentPiece = getNonCapturedPieceAtLocation(move.targetRow, move.targetColumn);
                        
                       this.pieces.remove(opponentPiece);
			this.capturedPieces.add(opponentPiece);
                        
			opponentPiece.isCaptured(true);
		}

		// move piece to new position
		piece.setRow(move.targetRow);
		piece.setColumn(move.targetColumn);

		
		
		return true;
	} 
                        
                        //ROUGHWORK TO CHANGE PAWN TO QUEEN
                        
                        //change pawn to queen
//                        if(moveValidator.isValidPawnMove(gameState, opponentColor, gameState, opponentColor)){
 //                           if(moveValidator.isValidPawnMove(gameState, opponentColor, gameState, opponentColor)){
 //                              JOptionPane.showMessageDialog(null, "Pawn change to Queen");
//                                this.pieces.remove(opponentPiece);
//                                this.capturedPieces.add(opponentPiece);
//                                
//                                opponentPiece.isCaptured(true);
//                              //  this.pieces.add(Piece.TYPE_PAWN);
//                               // opponentPiece.isCaptured(true);
//                            }
//                            else{
//                            JOptionPane.showMessageDialog(null, "no queen");
//                            }
                                
                        
//                        if(move.sourceRow==7 && move.targetRow ==7)
//			{
//                            JOptionPane.showMessageDialog(null, "Pawn change to Queen");
////                            this.pieces.remove(Piece.TYPE_PAWN);
////                            this.capturedPieces.add(Piece.TYPE_PAWN);
//                        this.pieces.remove(Piece.TYPE_PAWN);
//			this.capturedPieces.add(Piece.TYPE_PAWN);
//                        
//			opponentPiece.isCaptured(true);
//                        createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_QUEEN, Piece.ROW_8,
//				Piece.COLUMN_D);
////                            Piece.TYPE_PAWN.isCaptured(true); 
//                        }
                        
                        
			
	
	/**
	 * Undo the specified move. It will also adjust the game state appropriately.
	 */
	public void undoMove(Move move){
		Piece piece = getNonCapturedPieceAtLocation(move.targetRow, move.targetColumn);
		
		piece.setRow(move.sourceRow);
		piece.setColumn(move.sourceColumn);
		
		if(move.capturedPiece != null){
			move.capturedPiece.setRow(move.targetRow);
			move.capturedPiece.setColumn(move.targetColumn);
			move.capturedPiece.isCaptured(false);
			this.capturedPieces.remove(move.capturedPiece);
			this.pieces.add(move.capturedPiece);
		}
		
		if(piece.getColor() == Piece.COLOR_BLACK){
			this.gameState = ChessGame.GAME_STATE_BLACK;
		}else{
			this.gameState = ChessGame.GAME_STATE_WHITE;
		}
	}

	/**
	 * checks to see if the end condition of the game is met
	 * returns true if the game end condition is met
         * 
         * GAME ENDED!
         * 
         * If the king exists in the list of all the captured pieces end the game.
         * 
	 */
	private boolean isGameEndConditionReached() {
		for (Piece piece : this.capturedPieces) {
			if (piece.getType() == Piece.TYPE_KING ) {
                            
				return true;
			} else {
				// continue iterating
			}
		}

		return false;
	}

	/**
	 * returns the first piece at the specified location that is not marked as
	 * captured
	 * 
	 * return the first not captured piece at the specified location
	 */
	public Piece getNonCapturedPieceAtLocation(int row, int column) {
		for (Piece piece : this.pieces) {
			if (piece.getRow() == row && piece.getColumn() == column) {
                            
				return piece;
			}
		}
		return null;
	}

	/**
	 * Checks whether there is a piece at the specified location that is not
	 * marked as 'captured' and has the specified colour.
	 * 
	 * return true, if the location contains a not-captured piece of the
	 *         specified color
	 */
	boolean isNonCapturedPieceAtLocation(int color, int row, int column) {
		for (Piece piece : this.pieces) {
			if (piece.getRow() == row && piece.getColumn() == column
					&& piece.getColor() == color) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether there is a non-captured piece at the specified location
	 * 
	 * return true, if the location contains a piece
	 */
	boolean isNonCapturedPieceAtLocation(int row, int column) {
		for (Piece piece : this.pieces) {
			if (piece.getRow() == row && piece.getColumn() == column) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return current game state (ChessGame.GAME_STATE_..)
	 */
	public int getGameState() {
		return this.gameState;
	}

	/**
	 * @return the internal list of pieces
	 */
	public List<Piece> getPieces() {
		return this.pieces;
	}

	/**
	 * switches the game state depending on the current board situation.
	 */
	public void changeGameState() {

		// check if game end condition has been reached
		// this method checks to see whether the end of the game has been reached
            // to see if black or white has won the game.
		if (this.isGameEndConditionReached()) {

			if (this.gameState == ChessGame.GAME_STATE_BLACK) {
				this.gameState = ChessGame.GAME_STATE_END_BLACK_WON;
			} else if(this.gameState == ChessGame.GAME_STATE_WHITE){
				this.gameState = ChessGame.GAME_STATE_END_WHITE_WON;
			}else{
				// leave game state as it is
			}
			return;
		}

		switch (this.gameState) {
			case GAME_STATE_BLACK:
				this.gameState = GAME_STATE_WHITE;
				break;
			case GAME_STATE_WHITE:
				this.gameState = GAME_STATE_BLACK;
				break;
			case GAME_STATE_END_WHITE_WON:
			case GAME_STATE_END_BLACK_WON:// don't change anymore
				break;
			default:
				throw new IllegalStateException("unknown game state:" + this.gameState);
		}
	}
        
	
	/**
	 * return the current move validator from the PieceMoves class
	 */
	public PieceMoves getMoveValidator(){
		return this.moveValidator;
	}

	@Override
	public void run() {
		this.startGame();
	}

}
