/**
 * This class sets all the mouse listener events and the dragging and dropping of the pieces, so like when a piece is clicked
 * it will trigger the mouse clicked event and under the mouse clicked event is the code that
 * executes and highlights the squares that is available for that specific piece to move to.
 * checks to see if the user is hovering over a valid piece and if so it can be clicked and now
 * becomes a draggable piece. 
 */

package ChessGame.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import ChessGame.rules.ChessGame;
import ChessGame.rules.Piece;

import ChessGame.rules.PieceMoves;



public class HighlightListeners implements MouseListener, MouseMotionListener {

	private List<GuiPiece> guiPieces;
	private ChessGui chessGui;
	
	private int dragOffsetX;
	private int dragOffsetY;
        
        private PieceMoves pieceMoves;
         
        // change pawn to other piece when user reaches the end of the board

	public HighlightListeners(List<GuiPiece> guiPieces, ChessGui chessGui) {
		this.guiPieces = guiPieces;
		this.chessGui = chessGui;
               
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
            
           // ROUGH WORK TO CHANGE THE PAWN TO A QUEEN WHEN IT GETS RELEASED
        //    main_pane = new JPanel(new GridLayout(1,4,10,0));
//                                      
//
//                                   int[] cmdActions = {
//                                          Piece.TYPE_QUEEN,Piece.TYPE_ROOK,Piece.TYPE_BISHOP,Piece.TYPE_KNIGHT
//                                       };        
//                                        for(int i=0; i<cmdActions.length; i++){
//                                            JButton button = new JButton();
//                                          //  button.addActionListener(this);
//                                            button.setActionCommand(cmdActions[i]+"");
//                                            main_pane.add(button);
//                                        }
//                                        resumeGame(Piece.TYPE_QUEEN);
//                                        setContentPane(main_pane);        
//                                       setResizable(false);
//                                        addWindowListener(new WindowAdapter(){
//                                            public void windowClosing(WindowEvent e){
//                                                
//                                            }
//                                        }); 
//                                    }
      
        }

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent evt) {
		if( !this.chessGui.isDraggingGamePiecesEnabled()){
			return;
		}
		
                // get the point of which the curser is at
		int x = evt.getPoint().x;
		int y = evt.getPoint().y;
		

		// find out which piece to move.
		// we check the list from top to buttom
		// (therefore we itereate in reverse order)
		//
		for (int i = this.guiPieces.size()-1; i >= 0; i--) {
			GuiPiece guiPiece = this.guiPieces.get(i);
			if (guiPiece.isCaptured()) continue;

			if(mouseOverPiece(guiPiece,x,y)){
				
				if( (	this.chessGui.getGameState() == ChessGame.GAME_STATE_WHITE
						&& guiPiece.getColor() == Piece.COLOR_WHITE
					) ||
					(	this.chessGui.getGameState() == ChessGame.GAME_STATE_BLACK
							&& guiPiece.getColor() == Piece.COLOR_BLACK
						)
					){
					// calculate offset, because we do not want the drag piece
					// to jump with it's upper left corner to the current mouse
					// position
					//
					this.dragOffsetX = x - guiPiece.getX();
					this.dragOffsetY = y - guiPiece.getY();
					this.chessGui.setDragPiece(guiPiece);
					this.chessGui.repaint();
					break;
				}
			}
		}
		
		// move drag piece to the top of the list
		if(this.chessGui.getDragPiece() != null){
			this.guiPieces.remove( this.chessGui.getDragPiece() );
			this.guiPieces.add(this.chessGui.getDragPiece());
		}
	}

	/**
	 * check whether the mouse is currently over this piece
	 * true if mouse is over the piece
	 */
	private boolean mouseOverPiece(GuiPiece guiPiece, int x, int y) {

		return guiPiece.getX() <= x 
			&& guiPiece.getX()+guiPiece.getWidth() >= x
			&& guiPiece.getY() <= y
			&& guiPiece.getY()+guiPiece.getHeight() >= y;
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		if( this.chessGui.getDragPiece() != null){
                        
                    // get the point of which the curser is at
			int x = evt.getPoint().x - this.dragOffsetX;
			int y = evt.getPoint().y - this.dragOffsetY;
                       
			// set game piece to the new location if possible
			//
			chessGui.setNewPieceLocation(this.chessGui.getDragPiece(), x, y);
			this.chessGui.repaint();
			this.chessGui.setDragPiece(null);
                
                
                        
		}
               
                
                
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		if(this.chessGui.getDragPiece() != null){
			
                    // set game piece to the new location if possible
			int x = evt.getPoint().x - this.dragOffsetX;
			int y = evt.getPoint().y - this.dragOffsetY;
			
                        //drag the piece to the new location
			GuiPiece dragPiece = this.chessGui.getDragPiece();
			
                        // set the position of the piece
                        dragPiece.setX(x);
			dragPiece.setY(y);
			
                        //repaint the board
			this.chessGui.repaint();
                  
		}
                
                
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

}
