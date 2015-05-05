/**
 * This class is used to implement my computer controlled players using
 * AI. Here is also where I validated the current situation of the game and
 * search for the best possible move in that state and search for the best move using 2 layers
 * in my negamax algorithm, and generate the valid moves of course.
 */

package ChessGame.ai;

import java.util.ArrayList;
import java.util.List;

import ChessGame.consoleReader.ChessConsoleReader;
import ChessGame.rules.ChessGame;
import ChessGame.rules.HandlePlayers;
import ChessGame.rules.Move;
import ChessGame.rules.PieceMoves;
import ChessGame.rules.Piece;


/**
 * 
 * @author Glen
 */
public class AiLogic implements HandlePlayers {

	private ChessGame chessGame;
	private PieceMoves validator;
 
        
        
	
	/**
	 * number of moves that look into the future
	 */
	public int maxDepth = 2;

/**
 * This simply creates a new game player that will be controlled by the
 * computer
 */
	public AiLogic(ChessGame chessGame) {
		this.chessGame = chessGame;
		this.validator = this.chessGame.getMoveValidator();
	}

	@Override
	public Move getMove() {
		return getBestMove();
	}

	/**
	 * this generates all the possible moves that the computer could make
         * and then calculates the scores for each of the moves. And then retuns
         * the best possible move that was found.
         * 
         * 
	 */
	private Move getBestMove() {
		System.out.println("getting best move");
		ChessConsoleReader.printCurrentGameState(this.chessGame);
		System.out.println("thinking...");
		
		List<Move> validMoves = generateMoves(false);
		int bestResult = Integer.MIN_VALUE;
                //int randomResult = Integer.MAX_VALUE;
		Move bestMove = null;
		
		for (Move move : validMoves) {
			executeMove(move);
			//System.out.println("evaluate move: "+move+" =========================================");
			int evaluationResult = -1 * negaMax(this.maxDepth,"");
                        
			//System.out.println("result: "+evaluationResult);
			undoMove(move);
			if( evaluationResult > bestResult){
                            
				bestResult = evaluationResult;
				bestMove = move;
			}
                        //ROUGH WORK FOR RANDOM MOVES BUT I DECIDED TO GO WITH A REALLY EASY LEVEL OF AI INSTEAD
                      //  random moves
//                        if( (Math.random() * evaluationResult) > bestResult){
//                            if( (Math.random() * evaluationResult) > bestResult){
//                                randomResult = evaluationResult;
//				bestMove = move;
//                            }
//				bestResult = evaluationResult;
//				bestMove = move;
//			}
		}
		System.out.println("done thinking! best move is: "+bestMove);
		return bestMove;
	}

	@Override
	public void moveSuccessfullyExecuted(Move move) {
		// we are using the same chessGame instance, so no need to do anything here.
		System.out.println("executed: "+move);
	}

	/**
	 * evaluate current game state with my negamax algorithm
	 *
	 * depth - current depth level (number of counter moves that still need to be evaluated)
	 * indent - debug string, that is placed in front of each log message
	 * integer score of game state after looking at "depth" counter moves
	 */
	private int negaMax(int depth, String indent) {

		if (depth <= 0
			|| this.chessGame.getGameState() == ChessGame.GAME_STATE_END_WHITE_WON
			|| this.chessGame.getGameState() == ChessGame.GAME_STATE_END_BLACK_WON){
			
			return evaluateState();
		}
		
		List<Move> moves = generateMoves(false);
		int currentMax = Integer.MIN_VALUE;
		
		for(Move currentMove : moves){
			
			executeMove(currentMove);
			
			int score = -1 * negaMax(depth - 1, indent+" ");
			
			undoMove(currentMove);
			
			if( score > currentMax){
				currentMax = score;
			}
		}
		
		return currentMax;
	}

	/**
	 * undo specified moves
         * As I want to anticipate what moves could happen against me, I am going to make the moves
         * get a score and return what sequence of moves would be the best by searching a maximum
         * depth of 2 moves into the future. So this will do the moves and then undo them as i dont
         * want them all to happen.
	 */
	private void undoMove(Move move) {
		
		this.chessGame.undoMove(move);
		
	}

	/**
	 * Execute specified move. This will also change the game state after the
	 * move has been executed.
	 */
	private void executeMove(Move move) {
		
		this.chessGame.movePiece(move);
		this.chessGame.changeGameState();
	}

	/**
        * 
        * 
        * First i create a list that all the moves would be stored in, and then i iterate
        * through them and the places locations that the pieces can be moved to, to see if theyre
        * valid locations. I also use the PiecesMoves class to see if they are valid moves.
	*/
	private List<Move> generateMoves(boolean debug) {

		List<Piece> pieces = this.chessGame.getPieces();
		List<Move> validMoves = new ArrayList<Move>();
		Move testMove = new Move(0,0,0,0);
		
		int pieceColor = (this.chessGame.getGameState()==ChessGame.GAME_STATE_WHITE
			?Piece.COLOR_WHITE
			:Piece.COLOR_BLACK);

		// iterate over all non-captured pieces
		for (Piece piece : pieces) {

			// only look at pieces of current players color
			if (pieceColor == piece.getColor()) {
				// start generating move
				testMove.sourceRow = piece.getRow();
				testMove.sourceColumn = piece.getColumn();

				// iterate over all board rows and columns
				for (int targetRow = Piece.ROW_1; targetRow <= Piece.ROW_8; targetRow++) {
					for (int targetColumn = Piece.COLUMN_A; targetColumn <= Piece.COLUMN_H; targetColumn++) {

						// finish generating move
						testMove.targetRow = targetRow;
						testMove.targetColumn = targetColumn;

						if(debug) System.out.println("testing move: "+testMove);
						
						// check if generated move is valid
						if (this.validator.isMoveValid(testMove, true)) {
							// valid move
							validMoves.add(testMove.clone());
						} else {
							// all moves have been generated
						}
					}
				}

			}
		}
		return validMoves;
	}

	/**
	 * evaluate the current game state from the view of the
	 * current player. High numbers indicate a better situation for
	 * the current player.
         * 
         * This is where each of the pieces is worth a value e.g. pawn is worth 10
         * All the pieces scores is added up and the difference between them is the score.
         * This will only return a score from the player that is active at that time, so if it is
         * blacks turn it will only return blacks score, and if the queen is the only piece left it will
         * return +90 for black, and if this was the same scenario for white it would have returned
         * -90.
	 *
	 * integer score of current game state
         * 
         * 
         * 
	 */
	private int evaluateState() {

		// add up score
		//
		int scoreWhite = 0;
		int scoreBlack = 0;
		for (Piece piece : this.chessGame.getPieces()) {
			if(piece.getColor() == Piece.COLOR_BLACK){
				scoreBlack +=
					getScoreForPieceType(piece.getType());
				scoreBlack +=
					getScoreForPiecePosition(piece.getRow(),piece.getColumn());
			}else if( piece.getColor() == Piece.COLOR_WHITE){
				scoreWhite +=
					getScoreForPieceType(piece.getType());
				scoreWhite +=
					getScoreForPiecePosition(piece.getRow(),piece.getColumn());
			}else{
				throw new IllegalStateException(
						"unknown piece color found: "+piece.getColor());
			}
		}
		
		// return evaluation result depending on who's turn it is
		int gameState = this.chessGame.getGameState();
		
		if( gameState == ChessGame.GAME_STATE_BLACK){
			return scoreBlack - scoreWhite;
		
		}else if(gameState == ChessGame.GAME_STATE_WHITE){
			return scoreWhite - scoreBlack;
		
		}else if(gameState == ChessGame.GAME_STATE_END_WHITE_WON
				|| gameState == ChessGame.GAME_STATE_END_BLACK_WON){
			return Integer.MIN_VALUE + 1;
		
		}else{
			throw new IllegalStateException("unknown game state: "+gameState);
		}
	}
	
	/**
	 * get the evaluation bonus for the specified position
         * 
         * 
         * Also for the AI I would like it to control the centre of the board, so this will
         * add extra points to the pieces which are in the centre of the board. This is only a small
         * change but one which will add extra difficulty to the AI.
	 */
	private int getScoreForPiecePosition(int row, int column) {
		byte[][] positionWeight =
		{ {1,1,1,1,1,1,1,1}
		 ,{2,2,2,2,2,2,2,2}
		 ,{2,2,3,3,3,3,2,2}
		 ,{2,2,3,4,4,3,2,2}
		 ,{2,2,3,4,4,3,2,2}
		 ,{2,2,3,3,3,3,2,2}
		 ,{2,2,2,2,2,2,2,2}
		 ,{1,1,1,1,1,1,1,1}
		 };
		return positionWeight[row][column];
	}

	/**
	 * get the evaluation score for the specified piece type
	 */
	private int getScoreForPieceType(int type){
		switch (type) {
			case Piece.TYPE_BISHOP: return 30;
			case Piece.TYPE_KING: return 99999;
			case Piece.TYPE_KNIGHT: return 30;
			case Piece.TYPE_PAWN: return 10;
			case Piece.TYPE_QUEEN: return 90;
			case Piece.TYPE_ROOK: return 50;
			default: throw new IllegalArgumentException("unknown piece type: "+type);
		}
	}

	public static void main(String[] args) {
		ChessGame ch = new ChessGame();
		AiLogic ai = new AiLogic(ch);
		
		
		ch.pieces = new ArrayList<Piece>();
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_ROOK, Piece.ROW_8, Piece.COLUMN_A);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_ROOK, Piece.ROW_8, Piece.COLUMN_H);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_7, Piece.COLUMN_A);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_BISHOP, Piece.ROW_7, Piece.COLUMN_B);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_ROOK, Piece.ROW_7, Piece.COLUMN_C);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_KING, Piece.ROW_7, Piece.COLUMN_E);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_7, Piece.COLUMN_G);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_7, Piece.COLUMN_H);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_6, Piece.COLUMN_E);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_6, Piece.COLUMN_F);
		ch.createAndAddPiece(Piece.COLOR_BLACK, Piece.TYPE_PAWN, Piece.ROW_4, Piece.COLUMN_E);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_BISHOP, Piece.ROW_3, Piece.COLUMN_E);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_PAWN, Piece.ROW_3, Piece.COLUMN_F);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_PAWN, Piece.ROW_2, Piece.COLUMN_A);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_QUEEN, Piece.ROW_2, Piece.COLUMN_D);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_PAWN, Piece.ROW_2, Piece.COLUMN_H);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_KING, Piece.ROW_1, Piece.COLUMN_E);
		ch.createAndAddPiece(Piece.COLOR_WHITE, Piece.TYPE_ROOK, Piece.ROW_1, Piece.COLUMN_H);
		ch.gameState = ChessGame.GAME_STATE_BLACK;
		ChessConsoleReader.printCurrentGameState(ch);
		System.out.println("score: "+ai.evaluateState());
		System.out.println("move: "+ai.getBestMove()); //c4 b4
	}
}
