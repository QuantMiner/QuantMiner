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


import java.io.*;
import java.util.*;
import javax.swing.*;

import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;
import src.simulatedAnnealing.*;
import src.solver.*;
import src.tools.*;

import java.awt.event.*;


public class PanelGenetic extends DatabasePanelAssistant { //step 4
    
   
    
    private RuleTester m_calculateur = null; //A very important member, it derives from thread
    private int m_iMaxReglesTestees = 0;        //max number of testing rule? 
    private int m_iIndiceRegleAffichee = 0;     //Affichee means display--the index of the rule being displayed
    private boolean m_bResultatAffiche = false; // Indicate que la total des r�gles calcul�es ont �t� affich�es
    
    
    class IndicateurCalculReglesGenerique extends RuleTester.IndicateurCalculRegles {
        PanelGenetic m_panneauParent = null;
        
        public IndicateurCalculReglesGenerique(PanelGenetic panneauParent) {
            super();
            
            m_panneauParent = panneauParent;
        }
        
        //indicate that calculation is finished
        public void IndiquerFinCalcul() {
            DialogEndComputeRules fenetreFinCalcul = null;

            if (!ENV.AVERTIR_FIN_CALCUL) //AVERTIR notice finishing calculation
                return;
            
            fenetreFinCalcul = new DialogEndComputeRules(m_panneauParent.m_contexteResolution.m_fenetreProprietaire, true);
            
            if ( (m_panneauParent.m_contexteResolution.m_fenetreProprietaire.getExtendedState() & java.awt.Frame.ICONIFIED) != 0 ) {
                m_panneauParent.m_contexteResolution.m_fenetreProprietaire.setExtendedState( java.awt.Frame.MAXIMIZED_BOTH );
                m_panneauParent.m_contexteResolution.m_fenetreProprietaire.toFront();
            }
            fenetreFinCalcul.show();
        }
        
        //send information
        public void EnvoyerInfo(String sNouvelleInfo) {
            m_panneauParent.AjouterInfo(sNouvelleInfo);
        }
        
        //indicate the number of the rule being tested
        public void IndiquerNombreReglesATester(int iNombreReglesATester) {
            m_iMaxReglesTestees = iNombreReglesATester;
            jProgressResolution.setMaximum(m_iMaxReglesTestees);
            jProgressResolution.setValue(0);
            m_iIndiceRegleAffichee = 0;
        }
    }//END OF CLASS IndicateurCalculReglesGenerique
    
    /** Creates new form PanneauAlgoGenetique */
    public PanelGenetic(ResolutionContext contexteResolution) {
        super(contexteResolution);

        initComponents();
        
        switch (contexteResolution.m_iTechniqueResolution) { //which algorithm to use
            
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL :
                super.DefinirEtape(4, "Mining rules using Apriori", ENV.REPERTOIRE_AIDE+"computation.htm");
                break;            
           
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :
                super.DefinirEtape(4, "Mining rules using a genetic algorithm", ENV.REPERTOIRE_AIDE+"computation.htm");
                break;
                
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                super.DefinirEtape(4, "Mining rules using a simulated annealing algorithm", ENV.REPERTOIRE_AIDE+"computation.htm");
                break;
        }        
        
        super.DefinirPanneauPrecedent(MainWindow.PANNEAU_CONFIG_TECHNIQUE); //previous step is step 3
        super.DefinirPanneauSuivant(MainWindow.PANNEAU_RESULTATS);          //next step is step 5
        super.initBaseComponents();
        
        m_iMaxReglesTestees = 0;
        m_iIndiceRegleAffichee = 0;
        
        // Initialization des propri�t�s de la barre de progression :
        jProgressResolution.setMinimum(0);
        jProgressResolution.setMaximum(100);
        jProgressResolution.setValue(0);
        jProgressResolution.setIndeterminate(false);
        jProgressResolution.setStringPainted(true);        
        
        if (super.m_contexteResolution != null)
            super.m_contexteResolution.m_listeRegles = null;
        
        // Create a timer for le refresh des contr�les durant l'extraction :
        ActionListener tacheProgrammee = new ActionListener() { //tache means task
            public void actionPerformed(ActionEvent evt) {
                RafraichirControles();  //Rafraichir means refresh
            }
        };
        
        //Starts the Timer, causing it to start sending action events to its listeners. 
        new javax.swing.Timer(1000, tacheProgrammee).start(); //call tacheProgrammee to do refresh every second
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jBoutonDemarrer = new javax.swing.JButton();         //Demarrer means start
        jBoutonArreter = new javax.swing.JButton();          //Arreter means stop
        jScrollPaneRegles = new javax.swing.JScrollPane();   //rule display panel
        jZoneTexteRegles = new javax.swing.JTextArea();      //text area to display rules
        jScrollPaneContexte = new javax.swing.JScrollPane(); //context display panel
        jTextAreaContexte = new javax.swing.JTextArea();     //text area to display context
        jProgressResolution = new javax.swing.JProgressBar(); //progress bar
        jButtonTravaillerFond = new javax.swing.JButton();    //work as a background task

        setLayout(null);
        //start button
        jBoutonDemarrer.setText("Start");
        jBoutonDemarrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBoutonDemarrerActionPerformed(evt);
            }
        });

        add(jBoutonDemarrer);
        jBoutonDemarrer.setBounds(30, 170, 100, 26);

        //stop button
        jBoutonArreter.setText("Stop");
        jBoutonArreter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBoutonArreterActionPerformed(evt);
            }
        });

        add(jBoutonArreter);
        jBoutonArreter.setBounds(150, 170, 100, 26);

        //rule panel
        jScrollPaneRegles.setAutoscrolls(true);
        jZoneTexteRegles.setEditable(false);
        jZoneTexteRegles.setFont(new java.awt.Font("Lucida Bright", 3, 12));
        jScrollPaneRegles.setViewportView(jZoneTexteRegles);

        add(jScrollPaneRegles);
        jScrollPaneRegles.setBounds(30, 250, 770, 250);

        //context panel
        jScrollPaneContexte.setAutoscrolls(true);
        jTextAreaContexte.setEditable(false);
        jTextAreaContexte.setLineWrap(true);
        jScrollPaneContexte.setViewportView(jTextAreaContexte);

        add(jScrollPaneContexte);
        jScrollPaneContexte.setBounds(30, 10, 810, 150);

        //progress bar
        add(jProgressResolution);
        jProgressResolution.setBounds(30, 210, 440, 20);

        //the button of working as a background task
        jButtonTravaillerFond.setText("Work as a background task");
        jButtonTravaillerFond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTravaillerFondActionPerformed(evt);
            }
        });

        add(jButtonTravaillerFond);
        jButtonTravaillerFond.setBounds(440, 170, 300, 26);

    }//GEN-END:initComponents

        
    
    //the button of working as a background task
    private void jButtonTravaillerFondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTravaillerFondActionPerformed
        if (m_calculateur != null)
            m_calculateur.setPriority( Thread.MIN_PRIORITY );
        
        super.m_contexteResolution.m_fenetreProprietaire.setExtendedState( java.awt.Frame.ICONIFIED );
    }//GEN-LAST:event_jButtonTravaillerFondActionPerformed

    
    
   //stop calculation
    private void jBoutonArreterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBoutonArreterActionPerformed
        ArreterCalculateur(true);
    }//GEN-LAST:event_jBoutonArreterActionPerformed

    //start calculation
    private void jBoutonDemarrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBoutonDemarrerActionPerformed
        RuleOptimizer optimiseur = null; //optimize rule
        ArreterCalculateur(false);  //stop calculation
                
        m_calculateur = new RuleTester( super.m_contexteResolution, new IndicateurCalculReglesGenerique(this) );
        
        m_bResultatAffiche = false;

        jZoneTexteRegles.setText("");  //set rule text area empty
        jTextAreaContexte.setText(""); //set context text area empty
        
        //create an optimizer
        switch (super.m_contexteResolution.m_iTechniqueResolution) {
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL :
                optimiseur = new OptimizerAprioriQual();
                break;
                    
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :
                optimiseur = new OptimizerGeneticAlgo();
                break;
                
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                optimiseur = new OptimizerSimulatedAnnealing();
                break;
        }
       
        m_calculateur.DefinirOptimiseurRegle( optimiseur );
        //Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread. 
        m_calculateur.start();
    }//GEN-LAST:event_jBoutonDemarrerActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBoutonArreter;
    private javax.swing.JButton jBoutonDemarrer;
    private javax.swing.JButton jButtonTravaillerFond;
    private javax.swing.JProgressBar jProgressResolution;
    private javax.swing.JScrollPane jScrollPaneContexte;
    private javax.swing.JScrollPane jScrollPaneRegles;
    private javax.swing.JTextArea jTextAreaContexte;
    private javax.swing.JTextArea jZoneTexteRegles;
    // End of variables declaration//GEN-END:variables
 
    
    //stop calculation
    private void ArreterCalculateur(boolean bEnregistrerRegles) {
        
        if (m_calculateur != null) {
                
            m_calculateur.AutoriserIndicationFinCalcul(false);
            
            m_calculateur.ArreterExecution();
            
            if (!m_calculateur.EstResultatDisponible())
                while (m_calculateur.isAlive()) {};

            if (bEnregistrerRegles)
                if (super.m_contexteResolution != null)
                    super.m_contexteResolution.m_listeRegles = m_calculateur.ObtenirListeReglesOptimales();

            RafraichirControles();

            m_calculateur = null;

        }
        
    }

    
    //append more rules to the rule text being displayed
    private void AjouterRegle(String sNouvelleRegle) {
        jZoneTexteRegles.append(sNouvelleRegle);
    }
     
    //append more information to the context text being displayed
    private void AjouterInfo(String sInfo) {
    	//Appends the given text to the end of the document. Does nothing if the model is null or the string is null or empty. 
        jTextAreaContexte.append(sInfo); 
    }
 
    
    //refresh
    private void RafraichirControles() {
        String sIndicateur = null;
        AssociationRule regle = null;
        boolean bResultatDisponible = false;
        
        if (m_calculateur != null)
            if (!m_bResultatAffiche) {
                bResultatDisponible = m_calculateur.EstResultatDisponible();
                if ( (m_calculateur.m_bEnExecution) || (bResultatDisponible) ) {
                
                    m_bResultatAffiche = bResultatDisponible;
                    
                    if (m_iMaxReglesTestees == 0) {
                        jProgressResolution.setValue(0);
                        jProgressResolution.setString("");
                    }
                    else {
                        jProgressResolution.setValue( m_calculateur.m_iNombreReglesTestees );
                        sIndicateur =
                              String.valueOf( m_calculateur.m_iNombreReglesTestees )
                            + " tested rules /  "
                            + String.valueOf( m_iMaxReglesTestees );
                        jProgressResolution.setString(sIndicateur);
                        jProgressResolution.repaint();
                    }
            
                    // Display the progress of the rules being calculated:
                    do {
                        regle = m_calculateur.ObtenirRegleCalculee(m_iIndiceRegleAffichee);
                        if (regle != null) {
                            AjouterRegle( String.valueOf(m_iIndiceRegleAffichee + 1) + ".  " );
                            AjouterRegle( regle.toString() );
                            AjouterRegle("\n");
                            m_iIndiceRegleAffichee++;
                        }
                    }
                    while (regle != null);
                }
            }
    }
    
    
    
    // Outrepassement de la m�thode m�re pour l'ajustement des champs :
    void ArrangerDisposition() {
        int iDeltaPosX = 0; // Diff�rence de positionnement horizontal entre la position id�ale et celle de l'�diteur de formulaires
        int iDeltaPosY = 0; // Diff�rence de positionnement vertical entre la position id�ale et celle de l'�diteur de formulaires
        
        super.ArrangerDisposition();
     
        iDeltaPosX = jScrollPaneContexte.getX() - super.m_zoneControles.x;
        iDeltaPosY = jScrollPaneContexte.getY() - super.m_zoneControles.y;
        
        jScrollPaneContexte.setBounds(
            jScrollPaneContexte.getX() - iDeltaPosX,
            jScrollPaneContexte.getY() - iDeltaPosY,
            super.m_zoneControles.width,
            jScrollPaneContexte.getHeight() );
        
        jBoutonDemarrer.setLocation(jBoutonDemarrer.getX()-iDeltaPosX, jBoutonDemarrer.getY()-iDeltaPosY);
        jBoutonArreter.setLocation(jBoutonArreter.getX()-iDeltaPosX, jBoutonArreter.getY()-iDeltaPosY);
        jButtonTravaillerFond.setLocation( (super.m_zoneControles.width+super.m_zoneControles.x)-jButtonTravaillerFond.getWidth(), jButtonTravaillerFond.getY()-iDeltaPosY);
        
        jProgressResolution.setBounds(
            jProgressResolution.getX()-iDeltaPosX,
            jProgressResolution.getY()-iDeltaPosY,
            super.m_zoneControles.width,
            jProgressResolution.getHeight() );
        
        jScrollPaneRegles.setBounds(
            jScrollPaneRegles.getX() - iDeltaPosX,
            jScrollPaneRegles.getY() - iDeltaPosY,
            super.m_zoneControles.width,
            super.m_zoneControles.height + super.m_zoneControles.y - (jScrollPaneRegles.getY()-iDeltaPosY) );        
    }
    
    
    // Outrepassement de la m�thode m�re pour des traitements sp�cifiques :
    protected boolean TraitementsSpecifiquesAvantSuivant() {
        ArreterCalculateur(true);
        
        return true;
    }
    
    
    // Outrepassement de la m�thode m�re pour l'annulation du processus � partir de ce panneau :
    public boolean AnnulerPanneau() {
        ArreterCalculateur(false);
        
        return true;
    }
}
