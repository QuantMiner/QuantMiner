/*                                             
 *Copyright 2007, 2011 CCLS Columbia University (USA), LIFO University of Orl��ans (France), BRGM (France)
 *
 *Authors: Cyril Nortet, Xiangrong Kong, Ansaf Salleb-Aouissi, Christel Vrain, Daniel Cassard
 *
 *This file is part of QuantMiner.
 *
 *QuantMiner is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *QuantMiner is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License along with QuantMiner.  If not, see <http://www.gnu.org/licenses/>.
 */
package src.interfaceGraphique;

import java.util.ArrayList;


import javax.print.DocFlavor.STRING;
import javax.swing.*;

import src.solver.*;
import src.utilitaires.*;



public class DialogAnalyseFichierRegles extends javax.swing.JDialog { //tools--> get information on a rule file
 
	private static final long serialVersionUID = 1L;
	/** Creates new form DialogAnalyseFichierRegles */
    public DialogAnalyseFichierRegles(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setLocationRelativeTo(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelGeneral = new javax.swing.JPanel();
        jButtonSelectionner = new javax.swing.JButton();
        jTextFieldChemin = new javax.swing.JTextField();
        jScrollPaneDescriptif = new javax.swing.JScrollPane();
        jEditorPaneDescriptif = new javax.swing.JEditorPane();
        jButtonFermer = new javax.swing.JButton();
        jButtonAide = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Information of a rule file \".QMR\"");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanelGeneral.setLayout(null);

        jPanelGeneral.setPreferredSize(new java.awt.Dimension(700, 500));
        jButtonSelectionner.setText("Select a rules file");
        jButtonSelectionner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectionnerActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonSelectionner);
        jButtonSelectionner.setBounds(10, 10, 240, 20);

        jTextFieldChemin.setEditable(false);
        jPanelGeneral.add(jTextFieldChemin);
        jTextFieldChemin.setBounds(260, 10, 370, 19);

        jEditorPaneDescriptif.setEditable(false);
        jEditorPaneDescriptif.setContentType("text/html");
        jScrollPaneDescriptif.setViewportView(jEditorPaneDescriptif);

        jPanelGeneral.add(jScrollPaneDescriptif);
        jScrollPaneDescriptif.setBounds(10, 40, 680, 410);

        jButtonFermer.setText("close");
        jButtonFermer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFermerActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonFermer);
        jButtonFermer.setBounds(300, 460, 100, 23);

        jButtonAide.setText("?");
        jButtonAide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAideActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonAide);
        jButtonAide.setBounds(640, 6, 50, 30);

        getContentPane().add(jPanelGeneral, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButtonAideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAideActionPerformed
        DialogFenetreAide dialogAide = new DialogFenetreAide(ENV.REPERTOIRE_AIDE+"rule_analysis.htm", null, true);
        dialogAide.show();
    }//GEN-LAST:event_jButtonAideActionPerformed
    
    private void jButtonFermerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFermerActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButtonFermerActionPerformed
    
    private void jButtonSelectionnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectionnerActionPerformed
        String sFichierChoisi = null;
        
        ArrayList<String> description = new ArrayList<String>();
        description.add("QuantMiner Files");
        ArrayList<String> extention = new ArrayList<String>();
        extention.add("qmr");  
        
        sFichierChoisi = UtilitairesInterface.DialogOuvertureFichier(this, ENV.REPERTOIRE_REGLES_QMR, description, extention);

        	if (sFichierChoisi != null) {
            jTextFieldChemin.setText(sFichierChoisi);
            jEditorPaneDescriptif.setText(ResolutionContext.EcrireDescriptionFichierReglesBinairesHTML(sFichierChoisi));
            jEditorPaneDescriptif.setCaretPosition(0);
        }
    }//GEN-LAST:event_jButtonSelectionnerActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("deprecation")
	public static void main(String args[]) {
        new DialogAnalyseFichierRegles(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAide;
    private javax.swing.JButton jButtonFermer;
    private javax.swing.JButton jButtonSelectionner;
    private javax.swing.JEditorPane jEditorPaneDescriptif;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JScrollPane jScrollPaneDescriptif;
    private javax.swing.JTextField jTextFieldChemin;
    // End of variables declaration//GEN-END:variables
    
}
