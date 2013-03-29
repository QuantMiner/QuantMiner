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
package src.graphicalInterface;

import javax.swing.*;

import src.solver.*;
import src.tools.*;



public class DialogSaveProfile extends javax.swing.JDialog { //profile--> save
    
    int m_iPanneauCourant = MainWindow.PANNEAU_AUCUN;
    int m_iMasqueEnregistrement = 0;
    
    

    public static final int SELECTION_UTILISATEUR_ANNULER = 0;          //user cancel saving profile
    public static final int SELECTION_UTILISATEUR_ENREGISTRER = 1;      //user save profile
 
    
    // Classe permettant de maintenir les donn�es internes � la bo�te de dialogue, m�me apr�s sa fermeture :
    public class DialogEnregistrementProfil_Donnees {
        public int m_iSelectionUtilisateur = 0;
        public int m_iMasqueEnregistrement = 0;
        
        public DialogEnregistrementProfil_Donnees() {
            m_iSelectionUtilisateur = SELECTION_UTILISATEUR_ANNULER;
            m_iMasqueEnregistrement = 0;
        }
    }
    
    public DialogEnregistrementProfil_Donnees m_donnees = null;
    
    
    
    //The dialog of save a profile
    public DialogSaveProfile(int iPanneauCourant, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        int iMasqueEnregistrement = 0;

        iMasqueEnregistrement = 0;
        m_iPanneauCourant = iPanneauCourant;
        
        initComponents();
                
        m_donnees = new DialogEnregistrementProfil_Donnees();
        
        switch (m_iPanneauCourant) {
            
            case MainWindow.PANNEAU_PRE_CHARGEMENT_BD :
                iMasqueEnregistrement = ResolutionContext.PROFIL_INFO_PRECHARGEMENT;
                break;
                
            case MainWindow.PANNEAU_PRE_EXTRACION :
                iMasqueEnregistrement =   ResolutionContext.PROFIL_INFO_PRECHARGEMENT
                                        | ResolutionContext.PROFIL_INFO_PREEXTRACTION;
                break;
                
            case MainWindow.PANNEAU_CONFIG_TECHNIQUE :
                iMasqueEnregistrement =   ResolutionContext.PROFIL_INFO_PRECHARGEMENT
                                        | ResolutionContext.PROFIL_INFO_PREEXTRACTION
                                        | ResolutionContext.PROFIL_INFO_ALGO_APRIORI
                                        | ResolutionContext.PROFIL_INFO_ALGO_GENETIQUE
                                        | ResolutionContext.PROFIL_INFO_ALGO_RECUIT
                                        | ResolutionContext.PROFIL_INFO_ALGO_CHARGEMENT;
                break;   
        }       
        
        jCheckBoxPreChargement.setEnabled( (iPanneauCourant==MainWindow.PANNEAU_PRE_CHARGEMENT_BD) || (iPanneauCourant==MainWindow.PANNEAU_PRE_EXTRACION) || (iPanneauCourant==MainWindow.PANNEAU_CONFIG_TECHNIQUE) );
        jCheckBoxPreExtraction.setEnabled( (iPanneauCourant==MainWindow.PANNEAU_PRE_EXTRACION) || (iPanneauCourant==MainWindow.PANNEAU_CONFIG_TECHNIQUE) );
        jCheckBoxApriori.setEnabled( iPanneauCourant == MainWindow.PANNEAU_CONFIG_TECHNIQUE );
        jCheckBoxGenetique.setEnabled( iPanneauCourant == MainWindow.PANNEAU_CONFIG_TECHNIQUE );
        jCheckBoxRecuit.setEnabled( iPanneauCourant == MainWindow.PANNEAU_CONFIG_TECHNIQUE );
        jCheckBoxChargementRegles.setEnabled( iPanneauCourant == MainWindow.PANNEAU_CONFIG_TECHNIQUE );

        jCheckBoxPreChargement.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_PRECHARGEMENT) != 0 );
        jCheckBoxPreExtraction.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_PREEXTRACTION) != 0 );
        jCheckBoxApriori.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_ALGO_APRIORI) != 0 );
        jCheckBoxGenetique.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_ALGO_GENETIQUE) != 0 );
        jCheckBoxRecuit.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_ALGO_RECUIT) != 0 );
        jCheckBoxChargementRegles.setSelected( (iMasqueEnregistrement & ResolutionContext.PROFIL_INFO_ALGO_CHARGEMENT) != 0 );
       
        setLocationRelativeTo(null);
    }
    
    
    
    public DialogEnregistrementProfil_Donnees LierStructureDonnees() {
        return m_donnees;
    }
    
    
    
    private boolean MemoriserSelectionsUtilisateur() {
        int iMasqueEnregistrement = 0;
        
        switch (m_iPanneauCourant) {
            
            case MainWindow.PANNEAU_PRE_CHARGEMENT_BD :
                if (jCheckBoxPreChargement.isSelected())    iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_PRECHARGEMENT;
                break;
                
            case MainWindow.PANNEAU_PRE_EXTRACION :
                if (jCheckBoxPreChargement.isSelected())    iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_PRECHARGEMENT;
                if (jCheckBoxPreExtraction.isSelected())    iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_PREEXTRACTION;
                break;
                
            case MainWindow.PANNEAU_CONFIG_TECHNIQUE :
                if (jCheckBoxPreChargement.isSelected())    iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_PRECHARGEMENT;
                if (jCheckBoxPreExtraction.isSelected())    iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_PREEXTRACTION;
                if (jCheckBoxApriori.isSelected())          iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_ALGO_APRIORI;
                if (jCheckBoxGenetique.isSelected())        iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_ALGO_GENETIQUE;
                if (jCheckBoxRecuit.isSelected())           iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_ALGO_RECUIT;
                if (jCheckBoxChargementRegles.isSelected()) iMasqueEnregistrement |= ResolutionContext.PROFIL_INFO_ALGO_CHARGEMENT;
                break;   
        }            
        
        m_donnees.m_iMasqueEnregistrement = iMasqueEnregistrement;
        
        return (iMasqueEnregistrement != 0);
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelGeneral = new javax.swing.JPanel();
        jPanelSelection = new javax.swing.JPanel();
        jCheckBoxPreExtraction = new javax.swing.JCheckBox();
        jCheckBoxPreChargement = new javax.swing.JCheckBox();
        jCheckBoxApriori = new javax.swing.JCheckBox();
        jLabelTechniques = new javax.swing.JLabel();
        jCheckBoxGenetique = new javax.swing.JCheckBox();
        jCheckBoxRecuit = new javax.swing.JCheckBox();
        jCheckBoxChargementRegles = new javax.swing.JCheckBox();
        jButtonSauvegarder = new javax.swing.JButton();
        jButtonAnnuler = new javax.swing.JButton();
        jButtonAide = new javax.swing.JButton();

        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanelGeneral.setLayout(null);

        jPanelGeneral.setPreferredSize(new java.awt.Dimension(460, 420));
        jPanelSelection.setLayout(null);

        jPanelSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Choice of the information to memorize in the profile file:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 3, 12)));
        jCheckBoxPreExtraction.setText("Filter for positioning attributes/values in rules");
        jPanelSelection.add(jCheckBoxPreExtraction);
        jCheckBoxPreExtraction.setBounds(10, 80, 410, 23);

        jCheckBoxPreChargement.setText("Filter for loading database attributes");
        jPanelSelection.add(jCheckBoxPreChargement);
        jCheckBoxPreChargement.setBounds(10, 40, 410, 23);

        jCheckBoxApriori.setText(" 'Apriori'algorithm qualitatif");
        jPanelSelection.add(jCheckBoxApriori);
        jCheckBoxApriori.setBounds(50, 150, 260, 23);

        jLabelTechniques.setFont(new java.awt.Font("Dialog", 3, 12));
        jLabelTechniques.setText("Rules mining parameters:");
        jPanelSelection.add(jLabelTechniques);
        jLabelTechniques.setBounds(20, 120, 360, 16);

        jCheckBoxGenetique.setText("Genetic algorithm");
        jPanelSelection.add(jCheckBoxGenetique);
        jCheckBoxGenetique.setBounds(50, 180, 190, 23);

        jCheckBoxRecuit.setText("Simulated annealing ");
        jPanelSelection.add(jCheckBoxRecuit);
        jCheckBoxRecuit.setBounds(50, 210, 180, 23);

        jCheckBoxChargementRegles.setText("Loading a rules file");
        jPanelSelection.add(jCheckBoxChargementRegles);
        jCheckBoxChargementRegles.setBounds(50, 240, 270, 23);

        jPanelGeneral.add(jPanelSelection);
        jPanelSelection.setBounds(10, 40, 440, 290);
        jPanelSelection.getAccessibleContext().setAccessibleName("Information to memorize in the profile:");

        jButtonSauvegarder.setText("Save profile");
        jButtonSauvegarder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSauvegarderActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonSauvegarder);
        jButtonSauvegarder.setBounds(110, 350, 270, 23);

        jButtonAnnuler.setText("Cancel");
        jButtonAnnuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnnulerActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonAnnuler);
        jButtonAnnuler.setBounds(200, 380, 100, 23);

        jButtonAide.setText("?");
        jButtonAide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAideActionPerformed(evt);
            }
        });

        jPanelGeneral.add(jButtonAide);
        jButtonAide.setBounds(398, 11, 50, 23);

        getContentPane().add(jPanelGeneral, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    private void jButtonAideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAideActionPerformed
        DialogHelp dialogAide = new DialogHelp(ENV.REPERTOIRE_AIDE+"profils.htm", null, true);
        dialogAide.show();
    }//GEN-LAST:event_jButtonAideActionPerformed

    
    
    private void jButtonAnnulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnnulerActionPerformed
        m_donnees.m_iSelectionUtilisateur = SELECTION_UTILISATEUR_ANNULER;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButtonAnnulerActionPerformed

    
    
    private void jButtonSauvegarderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSauvegarderActionPerformed
        if (MemoriserSelectionsUtilisateur()) {
            m_donnees.m_iSelectionUtilisateur = SELECTION_UTILISATEUR_ENREGISTRER;
            setVisible(false);
            dispose();
        }            
        else
            JOptionPane.showMessageDialog(null, "Veuillez choisir au moins une cat�gorie de param�tres � enregistrer !", "Erreur", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButtonSauvegarderActionPerformed
    
    
    
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new DialogSaveProfile(0, new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAide;
    private javax.swing.JButton jButtonAnnuler;
    private javax.swing.JButton jButtonSauvegarder;
    private javax.swing.JCheckBox jCheckBoxApriori;
    private javax.swing.JCheckBox jCheckBoxChargementRegles;
    private javax.swing.JCheckBox jCheckBoxGenetique;
    private javax.swing.JCheckBox jCheckBoxPreChargement;
    private javax.swing.JCheckBox jCheckBoxPreExtraction;
    private javax.swing.JCheckBox jCheckBoxRecuit;
    private javax.swing.JLabel jLabelTechniques;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JPanel jPanelSelection;
    // End of variables declaration//GEN-END:variables
    
}
