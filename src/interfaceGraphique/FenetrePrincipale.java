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

import javax.swing.*;
import javax.swing.filechooser.*;

import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;
import src.solver.*;
import src.utilitaires.*;

import java.awt.*;
import java.io.*;
import java.util.*;


/**main window */
public class FenetrePrincipale extends javax.swing.JFrame {
    
    static final int PANNEAU_AUCUN = 0;
    static final int PANNEAU_DEFAUT = 1;
    static final int PANNEAU_PRE_CHARGEMENT_BD = 2;
    static final int PANNEAU_PRE_EXTRACION = 3; 
    static final int PANNEAU_CONFIG_TECHNIQUE = 4;
    static final int PANNEAU_RESULTATS = 6;
    static final int PANNEAU_TECH_GENERIQUE = 7;
    
    
    PanneauBase m_panneauCourant = null;
    int m_iPanneauCourant = 0;
    
    ResolutionContext m_contexteResolution = null;
    
    public FenetrePrincipale() {
        Dimension dimensionEcran = null;
        Image iconeFenetre = null;  
        
        m_contexteResolution = null;
        m_iPanneauCourant = PANNEAU_AUCUN;
        
        initComponents();
        
        
        ImageIcon icone = null;
        icone = new ImageIcon(ENV.REPERTOIRE_RESSOURCES+"incone_quantminer.jpg");
        
        iconeFenetre = icone.getImage();
        if (iconeFenetre != null)
            setIconImage( iconeFenetre );
        
        
        ActiverPanneau(PANNEAU_DEFAUT);
        pack();
        
        dimensionEcran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimensionEcran.width/2 - getWidth()/2,  dimensionEcran.height/2 - getHeight()/2);
       
        setExtendedState(java.awt.Frame.NORMAL);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        panneauPrincipal = new javax.swing.JPanel();            //main panel
        menuPrincipal = new javax.swing.JMenuBar();             //main menu bar
        menuFichier = new javax.swing.JMenu();                  //file menu
        ouvrirMenuItem = new javax.swing.JMenuItem();           //open file
        fermeMenuItem = new javax.swing.JMenuItem();            //close --> return back to main panel
        quitteMenuItem = new javax.swing.JMenuItem();           //exit
        jMenuProfils = new javax.swing.JMenu();                 //profile menu
        chargeProfilMenuItem = new javax.swing.JMenuItem();     //load profile
        sauveProfilMenuItem = new javax.swing.JMenuItem();      //save the current profile
        jMenuOutils = new javax.swing.JMenu();                  //tool menu
        jMenuItemInfosRegles = new javax.swing.JMenuItem();     //get info on a rule file
        jMenuItemParametrage = new javax.swing.JMenuItem();     //Quant Miner parameter
        menuAide = new javax.swing.JMenu();                     //help menu
        ouvrirAideMenuItem = new javax.swing.JMenuItem();       //help content
        aProposMenuItem = new javax.swing.JMenuItem();          //about

        setTitle("QuantMiner");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        panneauPrincipal.setLayout(new java.awt.BorderLayout());

        //main window size, resize
        panneauPrincipal.setMinimumSize(new java.awt.Dimension(840, 550));
        panneauPrincipal.setPreferredSize(new java.awt.Dimension(840, 550));
        panneauPrincipal.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panneauPrincipalComponentResized(evt);
            }
        });

        getContentPane().add(panneauPrincipal, java.awt.BorderLayout.CENTER);

        menuFichier.setText("File");
        menuFichier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierActionPerformed(evt);
            }
        });

        ouvrirMenuItem.setText("Open File");
        ouvrirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ouvrirMenuItemActionPerformed(evt);
            }
        });
        menuFichier.add(ouvrirMenuItem);

        fermeMenuItem.setText("Close");
        fermeMenuItem.setEnabled(false);
        fermeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fermeMenuItemActionPerformed(evt);
            }
        });
        menuFichier.add(fermeMenuItem);

        quitteMenuItem.setText("Exit");
        quitteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitteMenuItemActionPerformed(evt);
            }
        });
        menuFichier.add(quitteMenuItem);
        menuPrincipal.add(menuFichier);
        
        jMenuProfils.setText("Profiles"); 
        jMenuProfils.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuProfilsActionPerformed(evt);
            }
        });

        chargeProfilMenuItem.setLabel("Load a profile");
        chargeProfilMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chargeProfilMenuItemActionPerformed(evt);
            }
        });

        jMenuProfils.add(chargeProfilMenuItem);

        sauveProfilMenuItem.setLabel("Save the current profile");
        sauveProfilMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sauveProfilMenuItemActionPerformed(evt);
            }
        });

        jMenuProfils.add(sauveProfilMenuItem);
        menuPrincipal.add(jMenuProfils);

        
        jMenuOutils.setText("Tools");
        jMenuItemInfosRegles.setLabel("Get information on a rules file");
        jMenuItemInfosRegles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInfosReglesActionPerformed(evt);
            }
        });

        jMenuOutils.add(jMenuItemInfosRegles);

        jMenuItemParametrage.setLabel("QuantMiner parameters");
        jMenuItemParametrage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemParametrageActionPerformed(evt);
            }
        });

        jMenuOutils.add(jMenuItemParametrage);
        menuPrincipal.add(jMenuOutils);

        menuAide.setText("Help");
        menuAide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAideActionPerformed(evt);
            }
        });

        ouvrirAideMenuItem.setLabel("Help contents");
        ouvrirAideMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ouvrirAideMenuItemActionPerformed(evt);
            }
        });

        menuAide.add(ouvrirAideMenuItem);

        aProposMenuItem.setLabel("About QuantMiner");
        aProposMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aProposMenuItemActionPerformed(evt);
            }
        });

        menuAide.add(aProposMenuItem);
        menuPrincipal.add(menuAide);
        setJMenuBar(menuPrincipal);

        pack();  //Causes this Window to be sized to fit the preferred size and layouts of its subcomponents.
    }// </editor-fold>//GEN-END:initComponents

    private void menuAideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAideActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_menuAideActionPerformed

    private void jMenuProfilsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuProfilsActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jMenuProfilsActionPerformed
    
    
    /**Get information of a rule file*/
    private void jMenuItemInfosReglesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInfosReglesActionPerformed
        DialogAnalyseFichierRegles dialogAnalyseFichierRegles = new DialogAnalyseFichierRegles(this, true);
        dialogAnalyseFichierRegles.show();
    }//GEN-LAST:event_jMenuItemInfosReglesActionPerformed
    
    
    /**QuantMiner parameters menu item*/
    private void jMenuItemParametrageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemParametrageActionPerformed
        DialogParametrage dialogParametrage = new DialogParametrage(m_contexteResolution, this, true);
        dialogParametrage.show();
    }//GEN-LAST:event_jMenuItemParametrageActionPerformed
    
    
    //About
    private void aProposMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aProposMenuItemActionPerformed
        DialogAPropos dialogAPropos = new DialogAPropos(this, true);
        dialogAPropos.show();
    }//GEN-LAST:event_aProposMenuItemActionPerformed
    
    
    /**Help menu item */
    private void ouvrirAideMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ouvrirAideMenuItemActionPerformed
        DialogFenetreAide dialogAide = new DialogFenetreAide(ENV.REPERTOIRE_AIDE+"index.htm", this, true);
        dialogAide.show();
    }//GEN-LAST:event_ouvrirAideMenuItemActionPerformed
    
    
    private void chargeProfilMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chargeProfilMenuItemActionPerformed
        String sFichierChoisi = null;
        String sInformationChargement = null;
      
        ArrayList<String> description = new ArrayList<String>();
        description.add("Profile File");
        ArrayList<String> extention = new ArrayList<String>();
        extention.add("prf");  
        
        sFichierChoisi = UtilitairesInterface.DialogOuvertureFichier(this, ENV.REPERTOIRE_PROFILS, description, extention);

        if (sFichierChoisi != null) {
            sInformationChargement = m_contexteResolution.ChargerProfil(sFichierChoisi);
            
            if (sInformationChargement != null)
                JOptionPane.showMessageDialog(this, sInformationChargement, "Avertissement", JOptionPane.INFORMATION_MESSAGE);
            
            if (m_iPanneauCourant != PANNEAU_AUCUN)
                ActiverPanneau(m_iPanneauCourant);
        }
    }//GEN-LAST:event_chargeProfilMenuItemActionPerformed
    
    
    /**Save profile*/
    private void sauveProfilMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sauveProfilMenuItemActionPerformed
        String sFichierChoisi = null;
        DialogEnregistrementProfil fenetreTypeProfil = null;
        DialogEnregistrementProfil.DialogEnregistrementProfil_Donnees donnees = null;
        
        // the save dialog has relevance to the current panel
        fenetreTypeProfil = new DialogEnregistrementProfil(m_iPanneauCourant, this, true);  
        donnees = fenetreTypeProfil.LierStructureDonnees();
        fenetreTypeProfil.show();
        
        //user cancel to save a profile
        if (donnees.m_iSelectionUtilisateur == DialogEnregistrementProfil.SELECTION_UTILISATEUR_ANNULER) 
            return;
        
        //user save a profile-- get the path where user want to save his file
        sFichierChoisi = UtilitairesInterface.DialogSauvegardeFichier(this, ENV.REPERTOIRE_PROFILS, "Profile File", "prf");
        //save the file
        if (sFichierChoisi != null)
            if (m_panneauCourant != null)
                if (m_panneauCourant.SychroniserDonneesInternesSelonAffichage())
                    m_contexteResolution.SauvegarderProfil(sFichierChoisi, donnees.m_iMasqueEnregistrement);
    }//GEN-LAST:event_sauveProfilMenuItemActionPerformed
    
    
    /**Close menu item under File*/
    private void fermeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fermeMenuItemActionPerformed
        if (m_panneauCourant != null)
            if (!m_panneauCourant.AnnulerPanneau())
                return;
        
        m_contexteResolution = null;
        ActiverPanneau(PANNEAU_DEFAUT);
    }//GEN-LAST:event_fermeMenuItemActionPerformed
    
    
    /**main panel resize*/
    private void panneauPrincipalComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panneauPrincipalComponentResized
        if (m_panneauCourant != null) {
            m_panneauCourant.ArrangerDisposition();
            m_panneauCourant.validate();
        }
    }//GEN-LAST:event_panneauPrincipalComponentResized
    
    
    /**Open File menu item clicked */
    private void ouvrirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ouvrirMenuItemActionPerformed
        String sFichierChoisi = null;  //absolute path of the file
        DatabaseAdmin gestionnaireBD = null;
        
        ArrayList<String> description = new ArrayList<String>();
        description.add("File DBase 4");
        description.add("File csv");
        ArrayList<String> extention = new ArrayList<String>();
        extention.add("dbf"); 
        extention.add("csv");
        
        sFichierChoisi = UtilitairesInterface.DialogOuvertureFichier(this, ENV.CHEMIN_DERNIERE_BASE_OUVERTE, description, extention);

        if (sFichierChoisi != null) {
            
       		int index = sFichierChoisi.lastIndexOf('.');
       		if (index < 0)
       			return;
        	String extension = sFichierChoisi.substring(index + 1, sFichierChoisi.length()).toLowerCase();
        	gestionnaireBD = new DatabaseAdmin(sFichierChoisi, extension);
            setTitle("QuantMiner " +  gestionnaireBD.m_sNomBaseDeDonnees);

            if (gestionnaireBD.EstBaseDeDonneesValide()) { // the data file(i.e.without path) is valid
                ENV.CHEMIN_DERNIERE_BASE_OUVERTE = sFichierChoisi;
                
                if (m_panneauCourant != null)
                    if (!m_panneauCourant.AnnulerPanneau())
                        return;
                
                m_contexteResolution = new ResolutionContext(this);
                m_contexteResolution.m_gestionnaireBD = gestionnaireBD;
                //In step 1, at the beginning, all columns are selected, and we also get to know column type due to AnalyserTypesChampsBD();
                m_contexteResolution.m_gestionnaireBD.PrendreEnCompteToutesLesColonnes(); 
                
                // Display du panel de manipulation de la Base de Donn�es :
                ActiverPanneau(PANNEAU_PRE_CHARGEMENT_BD);  //Activate panel
            }
            else
                JOptionPane.showMessageDialog(null, "An error occured while loading the database. QuantMiner supports only table in DBF or CSV format. Use Excel for example to generate such tables.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ouvrirMenuItemActionPerformed
    
    
    private void menuFichierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFichierActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_menuFichierActionPerformed
    
    
    private void quitteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitteMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_quitteMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aProposMenuItem;
    private javax.swing.JMenuItem chargeProfilMenuItem;
    private javax.swing.JMenuItem fermeMenuItem;               //close panel --> to default panel
    private javax.swing.JMenuItem jMenuItemInfosRegles;
    private javax.swing.JMenuItem jMenuItemParametrage;
    private javax.swing.JMenu jMenuOutils;
    private javax.swing.JMenu jMenuProfils;
    private javax.swing.JMenu menuAide;
    private javax.swing.JMenu menuFichier;
    private javax.swing.JMenuBar menuPrincipal;
    private javax.swing.JMenuItem ouvrirAideMenuItem;
    private javax.swing.JMenuItem ouvrirMenuItem;
    private javax.swing.JPanel panneauPrincipal;
    private javax.swing.JMenuItem quitteMenuItem;
    private javax.swing.JMenuItem sauveProfilMenuItem;
    // End of variables declaration//GEN-END:variables
    
    
    
    
    /**
     * Activate a specific panel
     * @param iPanneau panel ID
     */
    public void ActiverPanneau(int iPanneau) {
        
        // D�sactive le panneau courant s'il existe :
        if (m_panneauCourant != null) {
            m_panneauCourant.setVisible(false);
            m_panneauCourant = null;
        }
        
        switch (iPanneau) {
            
            case PANNEAU_DEFAUT :
                m_panneauCourant = (PanneauBase)( new PanneauDefaut() );  //Default panel
                break;
                
            case PANNEAU_PRE_CHARGEMENT_BD ://step1
                m_panneauCourant = (PanneauBase)( new PanneauPreChargementBD(m_contexteResolution) );
                break;
                
            case PANNEAU_PRE_EXTRACION : //step2
                m_panneauCourant = (PanneauBase)( new PanneauPreExtraction(m_contexteResolution) );
                break;
                
            case PANNEAU_CONFIG_TECHNIQUE : //step 3 parameter configuration
                m_panneauCourant = (PanneauBase)( new PanneauConfigTechnique(m_contexteResolution) );
                break;
                
            case PANNEAU_RESULTATS : //step 5
            	if (m_contexteResolution == null)
            		System.out.println("m_contexteResolution is null");
            	else if (m_contexteResolution.m_listeRegles == null)
            		System.out.println("m_contexteResolution.m_listeRegles is null");
                m_panneauCourant = (PanneauBase)( new PanneauResultats(m_contexteResolution) );
                
                break;
                
            case PANNEAU_TECH_GENERIQUE : //step 4
                m_panneauCourant = (PanneauBase)( new PanneauTechniqueGenerique(m_contexteResolution) );
                break;
                
            default :
                iPanneau = PANNEAU_AUCUN;
                m_panneauCourant = null;
        }
        
        
        fermeMenuItem.setEnabled( (iPanneau!=PANNEAU_DEFAUT) && (m_panneauCourant!=null) );
        jMenuProfils.setEnabled(  (m_panneauCourant!=null)
        &&(  (iPanneau==PANNEAU_PRE_CHARGEMENT_BD)
        ||(iPanneau==PANNEAU_PRE_EXTRACION)
        ||(iPanneau==PANNEAU_CONFIG_TECHNIQUE)  )  );
        
        
        m_iPanneauCourant = iPanneau;
        
        if (m_panneauCourant != null) {
            panneauPrincipal.add(m_panneauCourant, java.awt.BorderLayout.CENTER);
            panneauPrincipal.validate();
            m_panneauCourant.ArrangerDisposition();
        }
        
    }
    
}
