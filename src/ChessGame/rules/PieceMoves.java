/**
 * 
 * 
 * 
 * This class basics checks whether the move that the user is trying to make is valid or not.
 * I coded out the basic moves for each piece.
 * 
 * For the valid move method it will check if the piece is there or not, in the source
 * location. If it is then it checks the colour of the piece, this shall further analise this piece
 * and if this is still no problem then we shall check checkmate/stalemate etc.
 * 
 */

package ChessGame.rules;

import ChessGame.consoleReader.ChessConsoleReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;


public class PieceMoves {

	public ChessGame chessGame;
	private Piece sourcePiece;
	private Piece targetPiece;
	private boolean debug;
        
        //promotion from pawn
        int index;
        int location;
        String rook;
        String queen;
        JPanel main_pane;
        
        static int choosePiece=-1;

        public List<Integer> demoted = new ArrayList<Integer>();
        public List<Integer> promoted = new ArrayList<Integer>();

	public PieceMoves(ChessGame chessGame){
		this.chessGame = chessGame;
          
	}

	/**
	 * Checks if the specified move is valid
	 * return true if move is valid, false if move is invalid
	 */
	public boolean isMoveValid(Move move, boolean debug) {
		this.debug = debug;
		int sourceRow = move.sourceRow;
		int sourceColumn = move.sourceColumn;
		int targetRow = move.targetRow;
		int targetColumn = move.targetColumn;
		
		sourcePiece = this.chessGame.getNonCapturedPieceAtLocation(sourceRow, sourceColumn);
		targetPiece = this.chessGame.getNonCapturedPieceAtLocation(targetRow, targetColumn);
		
                //ROUGH WORK FOR CHECKMATE
                //checkmate
//                if(sourcePiece == this.chessGame.getNonCapturedPieceAtLocation(sourceRow, sourceColumn)){
//                    if(sourcePiece == targetPiece){
//                        System.out.println("piecececece");
//                    }
//                }

		// source piece does not exist
		if( sourcePiece == null ){
			log("source piece does not exist");
			return false;
		}
		
		// source piece has right color?
		if( sourcePiece.getColor() == Piece.COLOR_WHITE
				&& this.chessGame.getGameState() == ChessGame.GAME_STATE_WHITE){
			// ok
		}else if( sourcePiece.getColor() == Piece.COLOR_BLACK
				&& this.chessGame.getGameState() == ChessGame.GAME_STATE_BLACK){
			// ok
		}else{
			log("it's not your turn: "
					+"pieceColor="+Piece.getColorString(sourcePiece.getColor())
					+"gameState="+this.chessGame.getGameState());
			ChessConsoleReader.printCurrentGameState(this.chessGame);
			// it's not your turn
			return false;
		}
		
		// check if target location within boundaries
		if( targetRow < Piece.ROW_1 || targetRow > Piece.ROW_8
				|| targetColumn < Piece.COLUMN_A || targetColumn > Piece.COLUMN_H){
			//target row or column out of scope
			log("target row or column out of scope");
			return false;
		}
		
		// validate piece movement rules
		boolean validPieceMove = false;
		switch (sourcePiece.getType()) {
			case Piece.TYPE_BISHOP:
				validPieceMove = isValidBishopMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			case Piece.TYPE_KING:
				validPieceMove = isValidKingMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			case Piece.TYPE_KNIGHT:
				validPieceMove = isValidKnightMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			case Piece.TYPE_PAWN:
				validPieceMove = isValidPawnMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			case Piece.TYPE_QUEEN:
				validPieceMove = isValidQueenMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			case Piece.TYPE_ROOK:
				validPieceMove = isValidRookMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
			default: break;
		}
		if( !validPieceMove){
			return false;
		}else{
			// ok
		}

		return true;
	}

	private boolean isTargetLocationCaptureable() {
		if( targetPiece == null ){
			return false;
		}else if( targetPiece.getColor() != sourcePiece.getColor()){
			return true;
		}else{
			return false;
		}
	}

	private boolean isTargetLocationFree() {
		return targetPiece == null;
	}

	private boolean isValidBishopMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		//The bishop can move any number of squares diagonally, but may not leap
		//over other pieces.
		
		// check to see if target location possible
		if( isTargetLocationFree() || isTargetLocationCaptureable()){
			//ok
		}else{
			//target location not free and not capturable
			return false;
		}
		
		boolean isValid = false;
		// check to see if the path to the target is diagonally at all
		int diffRow = targetRow - sourceRow;
		int diffColumn = targetColumn - sourceColumn;
		
		if( diffRow==diffColumn && diffColumn > 0){
			// moving diagonally up-right
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,+1,+1);

		}else if( diffRow==-diffColumn && diffColumn > 0){
			// moving diagnoally down-right
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,-1,+1);
			
		}else if( diffRow==diffColumn && diffColumn < 0){
			// moving diagnoally down-left
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,-1,-1);

		}else if( diffRow==-diffColumn && diffColumn < 0){
			// moving diagnoally up-left
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,+1,-1);
			
		}else{
			// not moving diagonally
			isValid = false;
		}
		return isValid;
	}

	private boolean isValidQueenMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		// The queen combines the power of the rook and bishop and can move any number
		// of squares along rank, file, or diagonal, but it may not leap over other pieces.
		//
		boolean result = isValidBishopMove(sourceRow, sourceColumn, targetRow, targetColumn);
		result |= isValidRookMove(sourceRow, sourceColumn, targetRow, targetColumn);
		return result;
	}
      
	private boolean isValidPawnMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		
		boolean isValid = false;
		// The pawn may move forward to the unoccupied square immediately in front
		// of it on the same file, or on its first move it may advance two squares
		// along the same file provided both squares are unoccupied
           
                if( isTargetLocationFree() ){
                    
                        //promote pawn to queen white and black pieces
                             if( sourceRow == 7 || sourceRow == 0){
                 
                                 // WHEN THE PAWN IS IN THE PROMOTION ROWN IT HAS QUEEN MOVES BUT IN NO OTHER ROW HAS IT GOES THIS
                                 //queen moves for pawn
                                 boolean promotePwnQueen = isValidQueenMove(sourceRow, sourceColumn, targetRow, targetColumn);
                                 return promotePwnQueen;
                                 
                                 }
                                 //ATTEMPTS TO PROMOTE THE PAWN TO A QUEEN OR HAVE A CHOICE WHETHER TO PROMOTE THE PAWN TO ANOTHER PIECE.
                                 
//                                 //if choice is rook
//                                 boolean promotePwnRook = isValidRookMove(sourceRow, sourceColumn, targetRow, targetColumn);
//                                 
//                                 
//                                 Scanner scan = new Scanner(System.in);
//                                 
//                                 
//                                 System.out.println("What piece would you like to change to rook or queen");
//                                 String answer = scan.nextLine();
//                                 
//                                 System.out.println("Great you chose: "+ answer);
////                                Object[] option={"Rook","Queen"};
////                                choosePiece=System.out.print("Who piece would you like?", "ABC Options", JOptionPane.YES_NO_OPTION,
////                                JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
//                                
//                                
//                                
//
//                                    //Start of easy difficulty
//                                if (answer == queen) {
//                                    //Queen
//
//                                    
//                                    
//                                }
//                                
//                                else if(answer == rook){
//                                    //Rook
//                                    return promotePwnRook;
//                                }
                               
                                            
//                                    boolean changeToChoice = false;
//                                    switch (Piece.TYPE_PAWN) {
//                                    case Piece.TYPE_BISHOP:
//                                            changeToChoice = isValidBishopMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
//                                    case Piece.TYPE_KING:
//                                            changeToChoice = isValidKingMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
//                                    case Piece.TYPE_KNIGHT:
//                                            changeToChoice = isValidKnightMove(sourceRow,sourceColumn,targetRow,targetColumn);break;   
//                                    case Piece.TYPE_QUEEN:
//                                            changeToChoice = isValidQueenMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
//                                    case Piece.TYPE_ROOK:
//                                            changeToChoice = isValidRookMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
//                                    default: changeToChoice = isValidQueenMove(sourceRow,sourceColumn,targetRow,targetColumn);break;
                                  // result |= isValidRookMove(sourceRow, sourceColumn, targetRow, targetColumn);
                
                                   //else{
					//not moving one up
				//	isValid = false;
                                  //  }
                             
			if( sourceColumn == targetColumn){
				// same column
				if(  sourcePiece.getColor() == Piece.COLOR_WHITE ){
                                    //2 moves
                                    // change white pawn to white queen
            
                                    if(sourceRow==1){
                                        if( sourceRow+1 == targetRow || sourceRow+2 == targetRow){
						// move one up
                                            //blocks pawn from jumping other pieceswhen m,oving 2 squares
						isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,+1,0);
					}else{
						//not moving one up
						isValid = false;
					}
                                    }
                                    else{
                                        if( sourceRow+1 == targetRow){
						// move one up
						isValid = true;
					}else{
						//not moving one up
						isValid = false;
					}
                                    }
                                    //end of two moves white	
				}else{
                                    // change black pawn to black queen
                                    
                                    if(sourceRow==6){
                                        if( sourceRow-1 == targetRow || sourceRow-2 == targetRow ){
						// move one down
                                            //blocks pawn from jumping other pieceswhen m,oving 2 squares
						isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,-1,0);
					}else{
						//not moving one down
						isValid =  false;
					}
                                    }
                                    else{
                                       if( sourceRow-1 == targetRow ){
						// move one down
						isValid = true;
					}else{
						//not moving one down
						isValid =  false;
					} 
                                    }
					// black
					
				}
			}else{
				// not staying in the same column
				isValid = false;
			}
			
		// or it may move
		// to a square occupied by an opponents piece, which is diagonally in front
		// of it on an adjacent file, capturing that piece. 
		}else if( isTargetLocationCaptureable() ){
			
			if( sourceColumn+1 == targetColumn || sourceColumn-1 == targetColumn){
				// one column to the right or left
				if(  sourcePiece.getColor() == Piece.COLOR_WHITE ){
					// white
					if( sourceRow+1 == targetRow ){
						// move one up
						isValid = true;
					}else{
						//not moving one up
						isValid = false;
					}
				}else{
					// black
					if( sourceRow-1 == targetRow ){
						// move one down
						isValid = true;
					}else{
						//not moving one down
						isValid = false;
					}
				}
			}else{
				// note one column to the left or right
				isValid = false;
			}
		}

		return isValid;
	}

	private boolean isValidKnightMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		// The knight moves to any of the closest squares which are not on the same rank,
		// file or diagonal, thus the move forms an "L"-shape two squares long and one
		// square wide. The knight is the only piece which can leap over other pieces.
		
		// target location possible?
		if( isTargetLocationFree() || isTargetLocationCaptureable()){
			//runs fine
             
		}
                //checkmate
                
                else{
			
                        //target location not free and not captureable
			return false;
		}
          
		if( sourceRow+2 == targetRow && sourceColumn+1 == targetColumn){
			// move up up right
			return true;
		}else if( sourceRow+1 == targetRow && sourceColumn+2 == targetColumn){
			// move up right right
			return true;
		}else if( sourceRow-1 == targetRow && sourceColumn+2 == targetColumn){
			// move down right right
			return true;
		}else if( sourceRow-2 == targetRow && sourceColumn+1 == targetColumn){
			// move down down right
			return true;
		}else if( sourceRow-2 == targetRow && sourceColumn-1 == targetColumn){
			// move down down left
			return true;
		}else if( sourceRow-1 == targetRow && sourceColumn-2 == targetColumn){
			// move down left left
			return true;
		}else if( sourceRow+1 == targetRow && sourceColumn-2 == targetColumn){
			// move up left left
			return true;
		}else if( sourceRow+2 == targetRow && sourceColumn-1 == targetColumn){
			// move up up left
			return true;
		}else{
			return false;
		}
	}

	private boolean isValidKingMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {

		// target location possible?
		if( isTargetLocationFree() || isTargetLocationCaptureable()){
			 //runs fine
                    //ROUGH WORK
                    //king cant be in one square of each other
                         
                             
                             //king cant take another king
//                             if(isTargetLocationCaptureable()){
//                               if (Piece.COLOR_WHITE == 0 && Piece.TYPE_KING == 5){
//                                   return false;
//                               } 
//                                //first and second rows
//                                if(sourceRow==0 || sourceRow==1){
//                                     
//                                    if(targetRow == 1 || targetRow==0){
//                                            return false;      
//                                        } 
//                                }
//                                
//                                //second and third
//                                if(sourceRow==1 || sourceRow==2){
//                                    
//                                    if(targetRow == 1 || targetRow==2){
//                                    return false;      
//                                    }
//                                }
//                                
//                                //third and fourth
//                                if(sourceRow==2 || sourceRow==3){
//                                    
//                                    if(targetRow == 2 || targetRow==3){
//                                    return false;      
//                                    }
//                                }
//                                
//                                //fourth and fifth
//                                if(sourceRow==3 || sourceRow==4){
//                                    
//                                    if(targetRow == 3 || targetRow==4){
//                                    return false;      
//                                    }
//                                }
//                                
//                                //fifth and sixth
//                                if(sourceRow==4 || sourceRow==5){
//                                    
//                                    if(targetRow == 4 || targetRow==5){
//                                    return false;      
//                                    }
//                                }
//                                
//                                //sixth and seventh
//                                if(sourceRow==5 || sourceRow==6){
//                                    
//                                    if(targetRow == 5 || targetRow==6){
//                                    return false;      
//                                    }
//                                }
//                                
//                                //seventh and eight
//                                if(sourceRow==6 || sourceRow==7){
//                                    
//                                    if(targetRow == 6 || targetRow==7){
//                                    return false;      
//                                    }
//                                }
     
                        //    }
         
		}else{
			//target location not free and not captureable
			return false;
		}
		
		// The king moves one square in any direction, the king has also a special move which is
		// called castling and also involves a rook.
		boolean isValid = true;
		if( sourceRow+1 == targetRow && sourceColumn == targetColumn){
			//up
                    
			isValid = true;
		}else if( sourceRow+1 == targetRow && sourceColumn+1 == targetColumn){
			//up right
			isValid = true;
		}else if( sourceRow == targetRow && sourceColumn+1 == targetColumn){
			//right
			isValid = true;
                }else if( sourceRow+1 == targetRow && sourceColumn-1 == targetColumn){
			//up left
			isValid = true;
                }else if( sourceRow == targetRow && sourceColumn-1 == targetColumn){
			//left
			isValid = true;
		}else if( sourceRow-1 == targetRow && sourceColumn+1 == targetColumn){
			//down right
			isValid = true;
		}else if( sourceRow-1 == targetRow && sourceColumn == targetColumn){
			//down
			isValid = true;
		}else if( sourceRow-1 == targetRow && sourceColumn-1 == targetColumn){
			//down left
			isValid = true;
          
		}else{
			//moving too far
			isValid = false;
		}
		
		// castling
		// ..
		
		return isValid;
	}

	private boolean isValidRookMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		// The rook can move any number of squares along any rank or file, but
		// may not leap over other pieces. Along with the king, the rook is also
		// involved during the king's castling move.
		if( isTargetLocationFree() || isTargetLocationCaptureable()){
			//ok
		}else{
			//target location not free and not captureable
			return false;
		}
		
		boolean isValid = false;
		
		// first lets check if the path to the target is straight at all
		int diffRow = targetRow - sourceRow;
		int diffColumn = targetColumn - sourceColumn;
		
		if( diffRow == 0 && diffColumn > 0){
			// right
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,0,+1);

		}else if( diffRow == 0 && diffColumn < 0){
			// left
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,0,-1);
			
		}else if( diffRow > 0 && diffColumn == 0){
			// up
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,+1,0);

		}else if( diffRow < 0 && diffColumn == 0){
			// down
			isValid = !arePiecesBetweenSourceAndTarget(sourceRow,sourceColumn,targetRow,targetColumn,-1,0);
			
		}else{
			//not moving straight
			isValid = false;
		}
		
		return isValid;
	}

	private boolean arePiecesBetweenSourceAndTarget(int sourceRow, int sourceColumn,
			int targetRow, int targetColumn, int rowIncrementPerStep, int columnIncrementPerStep) {
		
		int currentRow = sourceRow + rowIncrementPerStep;
		int currentColumn = sourceColumn + columnIncrementPerStep;
		while(true){
			if(currentRow == targetRow && currentColumn == targetColumn){
				break;
			}
			if( currentRow < Piece.ROW_1 || currentRow > Piece.ROW_8
				|| currentColumn < Piece.COLUMN_A || currentColumn > Piece.COLUMN_H){
				break;
			}

			if( this.chessGame.isNonCapturedPieceAtLocation(currentRow, currentColumn)){
				// pieces in between source and target
				return true;
			}

			currentRow += rowIncrementPerStep;
			currentColumn += columnIncrementPerStep;
		}
		return false;
	}
	
	public static void main(String[] args) {
		ChessGame ch = new ChessGame();
		PieceMoves mo = new PieceMoves(ch);
		Move move = null;
		boolean isValid;
		
		int sourceRow; int sourceColumn; int targetRow; int targetColumn;
		int testCounter = 1;

		// ok
		sourceRow = Piece.ROW_2; sourceColumn = Piece.COLUMN_D;
		targetRow = Piece.ROW_3; targetColumn = Piece.COLUMN_D;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid( move,true);
		ch.movePiece(move);
		System.out.println(testCounter+". test result: "+(true==isValid));
		testCounter++;
		ch.changeGameState();
		
		// it's not white's turn
		sourceRow = Piece.ROW_2; sourceColumn = Piece.COLUMN_B;
		targetRow = Piece.ROW_3; targetColumn = Piece.COLUMN_B;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid( move, true);
		System.out.println(testCounter+". test result: "+(false==isValid));
		testCounter++;
		
		// ok
		sourceRow = Piece.ROW_7; sourceColumn = Piece.COLUMN_E;
		targetRow = Piece.ROW_6; targetColumn = Piece.COLUMN_E;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid( move, true);
		ch.movePiece( move );
		System.out.println(testCounter+". test result: "+(true==isValid));
		testCounter++;
		ch.changeGameState();
		
		// pieces in the way
		sourceRow = Piece.ROW_1; sourceColumn = Piece.COLUMN_F;
		targetRow = Piece.ROW_4; targetColumn = Piece.COLUMN_C;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid(move, true);
		System.out.println(testCounter+". test result: "+(false==isValid));
		testCounter++;
		
		// ok
		sourceRow = Piece.ROW_1; sourceColumn = Piece.COLUMN_C;
		targetRow = Piece.ROW_4; targetColumn = Piece.COLUMN_F;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid(move, true);
		ch.movePiece(move);
		System.out.println(testCounter+". test result: "+(true==isValid));
		testCounter++;
		ch.changeGameState();
		
		// ok
		sourceRow = Piece.ROW_8; sourceColumn = Piece.COLUMN_B;
		targetRow = Piece.ROW_6; targetColumn = Piece.COLUMN_C;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid( move, true);
		ch.movePiece(move);
		System.out.println(testCounter+". test result: "+(true==isValid));
		testCounter++;
		ch.changeGameState();
		
		// invalid knight move
		sourceRow = Piece.ROW_1; sourceColumn = Piece.COLUMN_G;
		targetRow = Piece.ROW_3; targetColumn = Piece.COLUMN_G;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid( move, true);
		System.out.println(testCounter+". test result: "+(false==isValid));
		testCounter++;
		
		// invalid knight move
		sourceRow = Piece.ROW_1; sourceColumn = Piece.COLUMN_G;
		targetRow = Piece.ROW_2; targetColumn = Piece.COLUMN_E;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid(move, true);
		System.out.println(testCounter+". test result: "+(false==isValid));
		testCounter++;
		
		// ok
		sourceRow = Piece.ROW_1; sourceColumn = Piece.COLUMN_G;
		targetRow = Piece.ROW_3; targetColumn = Piece.COLUMN_H;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid(move, true);
		ch.movePiece(move);
		System.out.println(testCounter+". test result: "+(true==isValid));
		testCounter++;
		ch.changeGameState();

		// pieces in between
		sourceRow = Piece.ROW_8; sourceColumn = Piece.COLUMN_A;
		targetRow = Piece.ROW_5; targetColumn = Piece.COLUMN_A;
		move = new Move(sourceRow, sourceColumn, targetRow, targetColumn);
		isValid = mo.isMoveValid(move, true);
		ch.movePiece(move);
		System.out.println(testCounter+". test result: "+(false==isValid));
		testCounter++;

	}

	private void log(String message) {
		if(debug) System.out.println(message);
		
	}
	
}
