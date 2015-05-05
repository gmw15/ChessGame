/**
 * 
 * 
 * 
 * 
 * 
 * The reason I have split up the swing user interface and the console interface as well was so I can play
 * them against each other for testing purposes etc and also because my computer vs computer mode does not play on the
 * board as my moves are quick for the program to repaint them all so the console represents the board in system.out.println statements.
 * 
 * 
 * 
 */

package ChessGame;

import ChessGame.ai.AiLogic;

import ChessGame.gui.ChessGui;
import ChessGame.rules.ChessGame;
import ChessGame.rules.Piece;
import javax.swing.JOptionPane;

/**
 *
 * @author Glen Ward
 */
public class MenuGui extends javax.swing.JFrame {
    
     
    static int humanAsWhite=-1;//1=human as white, 0=human as black
    static int difficulty=-1;
    static int difficultyEasy=-1;
    static int comcom=-1;
               
    /**
     * Creates new form MenuGui
     */
    public MenuGui() {
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EasyBtn = new javax.swing.JButton();
        MedBtn = new javax.swing.JButton();
        HardBtn = new javax.swing.JButton();
        ComVsComBtn = new javax.swing.JButton();
        AboutBtn = new javax.swing.JButton();
        ExitBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        TitleLbl = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        BackLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(401, 403));
        getContentPane().setLayout(null);

        EasyBtn.setText("Easy");
        EasyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EasyBtnActionPerformed(evt);
            }
        });
        getContentPane().add(EasyBtn);
        EasyBtn.setBounds(20, 70, 170, 30);

        MedBtn.setText("Medium");
        MedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MedBtnActionPerformed(evt);
            }
        });
        getContentPane().add(MedBtn);
        MedBtn.setBounds(20, 120, 170, 30);

        HardBtn.setText("Hard");
        HardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HardBtnActionPerformed(evt);
            }
        });
        getContentPane().add(HardBtn);
        HardBtn.setBounds(20, 170, 170, 30);

        ComVsComBtn.setText("Computer vs Computer");
        ComVsComBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComVsComBtnActionPerformed(evt);
            }
        });
        getContentPane().add(ComVsComBtn);
        ComVsComBtn.setBounds(210, 70, 170, 30);

        AboutBtn.setText("About");
        AboutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutBtnActionPerformed(evt);
            }
        });
        getContentPane().add(AboutBtn);
        AboutBtn.setBounds(210, 170, 170, 30);

        ExitBtn.setText("Exit");
        ExitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitBtnActionPerformed(evt);
            }
        });
        getContentPane().add(ExitBtn);
        ExitBtn.setBounds(120, 330, 170, 30);

        jButton1.setText("Human vs Human");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(210, 120, 170, 30);

        TitleLbl.setBackground(new java.awt.Color(0, 51, 204));
        TitleLbl.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        TitleLbl.setForeground(new java.awt.Color(255, 255, 255));
        TitleLbl.setText("Chess Game Project");
        getContentPane().add(TitleLbl);
        TitleLbl.setBounds(100, 0, 220, 30);

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("x12436692");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(140, 30, 130, 30);

        BackLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ChessGame/gui/images/chessbkd.jpg"))); // NOI18N
        getContentPane().add(BackLbl);
        BackLbl.setBounds(0, -10, 410, 380);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void EasyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EasyBtnActionPerformed
        // TODO add your handling code here:
           // yes = 0; no=1;

		// first we create the game
		ChessGame chessGame = new ChessGame();

		// then we create the clients/players
		ChessGui chessGui = new ChessGui(chessGame);
		
		AiLogic ai1 = new AiLogic(chessGame);

		// set easy strength of AI
		ai1.maxDepth = 0;

            //play against computer
            Object[] option={"Black","White"};
        humanAsWhite=JOptionPane.showOptionDialog(null, "Who would you like?", "ABC Options", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        
            //Start of easy difficulty
        if (humanAsWhite == 1) {
            //easy difficulty and computer
            chessGame.setPlayer(Piece.COLOR_BLACK, ai1);
            chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
        }
        else if(humanAsWhite == 0){
            //easy difficulty and human
            chessGame.setPlayer(Piece.COLOR_WHITE, ai1);
            chessGame.setPlayer(Piece.COLOR_BLACK, chessGui);
        }
 
		new Thread(chessGame).start();
                this.dispose();
    }//GEN-LAST:event_EasyBtnActionPerformed

    private void MedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MedBtnActionPerformed
        // TODO add your handling code here:
        // first we create the game
		ChessGame chessGame = new ChessGame();

		// then we create the clients/players
		ChessGui chessGui = new ChessGui(chessGame);
		
		AiLogic ai2 = new AiLogic(chessGame);
 
		// set medium strength of AI
		
		ai2.maxDepth = 1;

            //play against computer
            Object[] option={"Black","White"};
        humanAsWhite=JOptionPane.showOptionDialog(null, "Who would you like?", "ABC Options", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        
            //Start of medium difficulty
        if (humanAsWhite == 1) {
            //easy difficulty and human
            chessGame.setPlayer(Piece.COLOR_BLACK, ai2);
            chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
        }
        else if(humanAsWhite == 0){
            //medium difficulty and computer
            chessGame.setPlayer(Piece.COLOR_WHITE, ai2);
            chessGame.setPlayer(Piece.COLOR_BLACK, chessGui);
        }
 
		new Thread(chessGame).start();
                this.dispose();
    }//GEN-LAST:event_MedBtnActionPerformed

    private void HardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HardBtnActionPerformed
        // TODO add your handling code here:
        // first we create the game
		ChessGame chessGame = new ChessGame();

		// then we create the clients/players
		ChessGui chessGui = new ChessGui(chessGame);
		
		
                AiLogic ai3 = new AiLogic(chessGame);

		// set hard strength of AI
		
                ai3.maxDepth = 2;

 
            //play against computer
            Object[] option={"Black","White"};
        humanAsWhite=JOptionPane.showOptionDialog(null, "Who would you like?", "ABC Options", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        
            //Start of hard difficulty
        if (humanAsWhite == 1) {
            //hard difficulty and human
            chessGame.setPlayer(Piece.COLOR_BLACK, ai3);
            chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
        }
        else if(humanAsWhite == 0){
            //hard difficulty and computer as white
            chessGame.setPlayer(Piece.COLOR_WHITE, ai3);
            chessGame.setPlayer(Piece.COLOR_BLACK, chessGui);
        }
 
		new Thread(chessGame).start();
                this.dispose();
    }//GEN-LAST:event_HardBtnActionPerformed

    private void ComVsComBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComVsComBtnActionPerformed
        // TODO add your handling code here:
        ChessGame chessGame = new ChessGame();

		// then we create the clients/players
		ChessGui chessGui = new ChessGui(chessGame);
		
		
                AiLogic ai2 = new AiLogic(chessGame);
                AiLogic ai3 = new AiLogic(chessGame);

		// set strength of AIs
		ai2.maxDepth = 1;
                ai3.maxDepth = 2;

               JOptionPane.showMessageDialog(null,"Starting Computer Vs Computer...");
          
            chessGame.setPlayer(Piece.COLOR_WHITE, ai2);
            chessGame.setPlayer(Piece.COLOR_BLACK, ai3);
        
 
		new Thread(chessGame).start();
                this.dispose();
    }//GEN-LAST:event_ComVsComBtnActionPerformed

    private void AboutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutBtnActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null,"Student number: x12436692 \n Module: Introduction To AI \n Project: Chess Game");
    }//GEN-LAST:event_AboutBtnActionPerformed

    private void ExitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitBtnActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_ExitBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ChessGame chessGame = new ChessGame();

		// then we create the clients/players
		ChessGui chessGui = new ChessGui(chessGame);

               JOptionPane.showMessageDialog(null,"Starting Human Vs Human...");
          
            chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
            chessGame.setPlayer(Piece.COLOR_BLACK, chessGui);
 
		new Thread(chessGame).start();
                this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        ChessGame chessGame = new ChessGame();
         
           new Thread(chessGame).start();      
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AboutBtn;
    private javax.swing.JLabel BackLbl;
    private javax.swing.JButton ComVsComBtn;
    private javax.swing.JButton EasyBtn;
    private javax.swing.JButton ExitBtn;
    private javax.swing.JButton HardBtn;
    private javax.swing.JButton MedBtn;
    private javax.swing.JLabel TitleLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}