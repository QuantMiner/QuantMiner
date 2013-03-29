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

import src.apriori.*;
import src.database.CsvFileParser;
import src.solver.*;
import src.tools.*;

import com.Ostermiller.util.ExcelCSVPrinter;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;


public class PanelResults extends DatabasePanelAssistant { //step 5
    
	//called when saving file in html with graph
    private class ResultatsEnregistreurGraphiqueRegle implements ResolutionContext.EnregistreurGraphiqueRegle { 
    	//Enregistrer means record/write down
        private String m_sNomBaseFichier = null;
        
        ResultatsEnregistreurGraphiqueRegle(String sNomBaseFichier) {
            super();
            m_sNomBaseFichier = sNomBaseFichier;
        }        
        
        public String EnregistrerRegle(AssociationRule regle, int iIndiceRegle) {  //record rules
            File fichier = null;
            String sCheminFichier = null;
            sCheminFichier = m_sNomBaseFichier+String.valueOf(iIndiceRegle)+".jpg";
            m_afficheurRegles.EnregistrerImageRegle(regle, iIndiceRegle, sCheminFichier);
            fichier = new File(sCheminFichier);
            return fichier.getName();
        }
    }
    
    /** Creates new form PanneauResultats */
    public PanelResults(ResolutionContext contexteResolution) {
        super(contexteResolution);

        int iNombreRegles = 0;
        AssociationRule [] tRegles = null;
        
        m_tReglesFiltrees = null;
        
        // If les rules sont issues d'un file, on commence by les load :
        if (super.m_contexteResolution.m_iTechniqueResolution == ResolutionContext.TECHNIQUE_CHARGEMENT) {  //if it load file
            super.m_contexteResolution.m_sDescriptionRegles = m_contexteResolution.m_parametresTechChargement.m_sDescriptionRegles;
            super.m_contexteResolution.ChargerReglesBinaire(super.m_contexteResolution.m_parametresTechChargement.m_sNomFichier);
        }
        
        // Mise � jour des informations de filtrage :
        super.m_contexteResolution.GenererStructuresDonneesSelonBDPriseEnCompte_Filtrage();
        super.m_contexteResolution.MettreAJourDonneesInternesFiltre_Filtrage();
        
        initComponents();

        jButtonCopy.setIcon( new ImageIcon( ENV.REPERTOIRE_RESSOURCES + "copier.jpg" ) ); //set icon for the copy button
        
        jTextNumeroRegle.setText("no rule selected");  //initialized with no rule selected
        
        //the middle part
        m_panneauTri = new PanelSort(this, super.m_contexteResolution); 
        m_panneauTri.setBorder(new javax.swing.border.EtchedBorder());
        add(m_panneauTri);
        
        //the third part
        m_afficheurRegles = new RuleBrowser(super.m_contexteResolution);
        m_afficheurRegles.setBackground(new java.awt.Color(255, 255, 255));
        jScrollRegles.setViewportView(m_afficheurRegles);
        jScrollRegles.validate();
        
        //the scroll bar
        jScrollBarRegles.setMinimum(0);
        jScrollBarRegles.setMaximum(0);
        jScrollBarRegles.setUnitIncrement(1);
        jScrollBarRegles.setVisibleAmount(1);
        
        iNombreRegles = 0;
        
        if (super.m_contexteResolution.m_listeRegles != null){
            iNombreRegles = super.m_contexteResolution.m_listeRegles.size();
        }
        
        if (iNombreRegles > 0) {
            // Calcul des mesures suppl�mentaires permettant d'�valuer plus finement les r�gles :
            // (si les r�gles ont �t� charg�es depuis un fichier, ces calculs sont d�j� faits)
            if (super.m_contexteResolution.m_iTechniqueResolution != ResolutionContext.TECHNIQUE_CHARGEMENT) {
                tRegles = new AssociationRule [1];
                tRegles = (AssociationRule [])(super.m_contexteResolution.m_listeRegles.toArray(tRegles));
                AssociationRule.CalculerMesuresDiverses(tRegles, super.m_contexteResolution);
            }
            
            jScrollBarRegles.setMaximum( iNombreRegles-1 );
        }
        else  
            jScrollBarRegles.setMaximum( 0 );
        
        
        super.DefinirEtape(5, "Results", ENV.REPERTOIRE_AIDE+"consult_results.htm");
        
        switch (m_contexteResolution.m_iTechniqueResolution) {
            
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL :
                super.DefinirPanneauPrecedent(MainWindow.PANNEAU_TECH_GENERIQUE);
                break;
            
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :
                super.DefinirPanneauPrecedent(MainWindow.PANNEAU_TECH_GENERIQUE);
                break;
                
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                super.DefinirPanneauPrecedent(MainWindow.PANNEAU_TECH_GENERIQUE);
                break;
                
            case ResolutionContext.TECHNIQUE_CHARGEMENT :
                super.DefinirPanneauPrecedent(MainWindow.PANNEAU_CONFIG_TECHNIQUE);
                break;
            
            default:
                super.DefinirPanneauPrecedent(MainWindow.PANNEAU_AUCUN);
        }        
        
        super.DefinirPanneauSuivant(MainWindow.PANNEAU_AUCUN);
        super.initBaseComponents();
        
        MettreAJourListeRegles();
     }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollBarRegles = new javax.swing.JScrollBar();  //rule scroll bar
        jButtonSauver = new javax.swing.JButton();        //save in a file
        jTextNumeroRegle = new javax.swing.JTextField();  //rules
        jScrollRegles = new javax.swing.JScrollPane();   //rules
        jButtonCopy = new javax.swing.JButton();         //copy button
        jButtonVoirContexte = new javax.swing.JButton(); //visualize the extraction context
        jButtonExtractRows = new javax.swing.JButton();  //Extract the rows of a specific rule

        setLayout(null);

      //text field --number of rules 
        jTextNumeroRegle.setEditable(false);
        jTextNumeroRegle.setFont(new java.awt.Font("Dialog", 1, 12));
        jTextNumeroRegle.setText("Rule n\u00b0.. / .. (total : ...)");
        add(jTextNumeroRegle);
        jTextNumeroRegle.setBounds(10, 110, 210, 20);
        
        //scroll bar
        jScrollBarRegles.setMaximum(0);
        jScrollBarRegles.setOrientation(javax.swing.JScrollBar.HORIZONTAL);  
        jScrollBarRegles.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarReglesAdjustmentValueChanged(evt);
            }
        });

        add(jScrollBarRegles);
        jScrollBarRegles.setBounds(220, 110, 100, 20);
        //jScrollBarRegles.setBounds(220, 110, 130, 20);
        
        //copy button
        jButtonCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopyActionPerformed(evt);
            }
        });

        add(jButtonCopy);
        jButtonCopy.setBounds(320, 110, 20, 20);
        // jButtonCopy.setBounds(350, 110, 20, 20);
        
        //button --Extract rows for a specific rule
        jButtonExtractRows.setText("Extract rows");
        jButtonExtractRows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExtractActionPerformed(evt);
            }
        });
        add(jButtonExtractRows);
        jButtonExtractRows.setBounds(340, 110, 120, 20);
        
       
        //button --save in a file 
        jButtonSauver.setText("Save in a file");
        jButtonSauver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSauverActionPerformed(evt);
            }
        });

        add(jButtonSauver);
        jButtonSauver.setBounds(10, 10, 210, 26);

        //scroll panel --about resulted rules
        jScrollRegles.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollRegles.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(jScrollRegles);
        jScrollRegles.setBounds(10, 130, 360, 40);

        //button --Visualize the extraction context
        jButtonVoirContexte.setText("Visualize the extraction context");
        jButtonVoirContexte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVoirContexteActionPerformed(evt);
            }
        });

        add(jButtonVoirContexte);
        jButtonVoirContexte.setBounds(240, 10, 250, 26);
        
    }//GEN-END:initComponents

    private void jButtonVoirContexteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVoirContexteActionPerformed
        DialogWindowInfoHTML dialogContexte = null;
        String sInfosContexte = null;
        
        sInfosContexte = this.m_contexteResolution.ObtenirInfosContexte(true);
        dialogContexte = new DialogWindowInfoHTML("Information on the context of extraction of rules", sInfosContexte, this.m_contexteResolution.m_fenetreProprietaire, true);
        dialogContexte.show();
    }//GEN-LAST:event_jButtonVoirContexteActionPerformed

    
    
    private void jButtonCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopyActionPerformed
        StringSelection selection = null;
        AssociationRule regleCourante = null;
        Clipboard clipboard = null;
        
        // On place la r�gle courante dans le presse papier :
        try {
            clipboard = getToolkit().getSystemClipboard();
            regleCourante = ObtenirRegleCourante();
            if (regleCourante != null)
                selection = new StringSelection( regleCourante.toString() );
            else
                selection = new StringSelection( "No rule copied!");
            clipboard.setContents(selection, selection);
        }
        catch (HeadlessException e) {}       
    }//GEN-LAST:event_jButtonCopyActionPerformed

    
    //save result in a file
    private void jButtonSauverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSauverActionPerformed
        String sFichierChoisi = null;
        DialogChoiceFileRecords fenetreTypeEnregistrement = null;
        DialogChoiceFileRecords.DialogChoixEnregistrementFichier_Donnees donnees = null;
        ResultatsEnregistreurGraphiqueRegle enregistreurGraphique = null;
        
        //save a list of rules, dialog
        fenetreTypeEnregistrement = new DialogChoiceFileRecords(m_contexteResolution, m_contexteResolution.m_fenetreProprietaire, true);
        donnees = fenetreTypeEnregistrement.LierStructureDonnees();
        fenetreTypeEnregistrement.show();
        
        //cancel
        if (donnees.m_iTypeEnregistrement == DialogChoiceFileRecords.TYPE_ENREGISTREMENT_ANNULER)
            return;
        
        m_contexteResolution.m_sNomUtilisateur = donnees.m_sNomUtilisateur;         //user name
        m_contexteResolution.m_sDescriptionRegles = donnees.m_sDescriptionRegles;   //rule description
       
       //save as html file (text or graphic)
       if (  (donnees.m_iTypeEnregistrement == DialogChoiceFileRecords.TYPE_ENREGISTREMENT_HTML_TEXTE)
            ||(donnees.m_iTypeEnregistrement == DialogChoiceFileRecords.TYPE_ENREGISTREMENT_HTML_GRAPHIQUE)  )
            sFichierChoisi = ToolsInterface.DialogSauvegardeFichier(this, ENV.REPERTOIRE_RESULTATS, "HTML File", "htm");//"Fichiers HTML", "htm");
       //save as qmr file
       else if (donnees.m_iTypeEnregistrement == DialogChoiceFileRecords.TYPE_ENREGISTREMENT_BINAIRE)
            sFichierChoisi = ToolsInterface.DialogSauvegardeFichier(this, ENV.REPERTOIRE_REGLES_QMR, "QuantMiner File", "qmr");//"Fichiers QuantMiner", "qmr");

       //save as csv file
       else 
    	   sFichierChoisi = ToolsInterface.DialogSauvegardeFichier(this, ENV.REPERTOIRE_RESULTATS, "CSV File", "csv");
       
        if (sFichierChoisi != null) {

            switch (donnees.m_iTypeEnregistrement) {
                case DialogChoiceFileRecords.TYPE_ENREGISTREMENT_HTML_TEXTE :
                    super.m_contexteResolution.SauvegarderReglesHTML(sFichierChoisi, m_tReglesFiltrees, false, null);
                    break;
                case DialogChoiceFileRecords.TYPE_ENREGISTREMENT_HTML_GRAPHIQUE :
                    enregistreurGraphique = new ResultatsEnregistreurGraphiqueRegle( FileTools.ObtenirCheminSansExtension(sFichierChoisi) );
                    super.m_contexteResolution.SauvegarderReglesHTML(sFichierChoisi, m_tReglesFiltrees, true, enregistreurGraphique);
                    break;
                case DialogChoiceFileRecords.TYPE_ENREGISTREMENT_BINAIRE :
                    super.m_contexteResolution.SauvegarderReglesBinaire(sFichierChoisi, m_tReglesFiltrees);
                    break;
                case DialogChoiceFileRecords.TYPE_ENREGISTREMENT_CSV :
                	super.m_contexteResolution.SauvegarderReglesCsv(sFichierChoisi, m_tReglesFiltrees);
                	break;
            }
        }
    }//GEN-LAST:event_jButtonSauverActionPerformed

    //Extract the rows for a specific rule
    private void jButtonExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExtractActionPerformed
    	System.out.println("m_iIndexCurrentRule" + m_iIndexCurrentRule);
    	String sFichierChoisi = null;
    	sFichierChoisi = ToolsInterface.DialogSauvegardeFichier(this, ENV.REPERTOIRE_RESULTATS, "CSV File", "csv");
    	System.out.println(sFichierChoisi);
    	ExcelCSVPrinter csvPrinter = null;
    	try {
        	csvPrinter = new ExcelCSVPrinter(new FileOutputStream(sFichierChoisi));
        } catch(IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
    	 
    	if (m_tReglesFiltrees == null)
    		return;
    	if (m_tReglesFiltrees[m_iIndexCurrentRule] == null)
    		return;
    	
    	String left = m_tReglesFiltrees[m_iIndexCurrentRule].leftToString();
    	System.out.println(left);
    	String right = m_tReglesFiltrees[m_iIndexCurrentRule].rightToString();
    	System.out.println(right);
    	
    	CsvFileParser csvParser = super.m_contexteResolution.m_gestionnaireBD.csvParser;
    	
    	Vector<Qualitative> leftQualitative = m_tReglesFiltrees[m_iIndexCurrentRule].leftQualiToArray();
    	Vector<Vector<Quantitative>> leftQuantitative = m_tReglesFiltrees[m_iIndexCurrentRule].leftQuantiToArray();
    	Vector<Qualitative> rightQualitative = m_tReglesFiltrees[m_iIndexCurrentRule].rightQualiToArray();
    	Vector<Vector<Quantitative>> rightQuantitative = m_tReglesFiltrees[m_iIndexCurrentRule].rightQuantiToArray();
    	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	Vector<String> label = new Vector<String>();
    	if (leftQualitative != null)
    	for (int i = 0; i < leftQualitative.size(); i++){
    	   label.add(leftQualitative.get(i).getM_name());	
    	}
    	if (rightQualitative != null)
    	for (int i = 0; i < rightQualitative.size(); i++){
    		label.add(rightQualitative.get(i).getM_name());
    	}
    	if (leftQuantitative != null)
    	for (int i = 0; i < leftQuantitative.get(0).size(); i++){
    		label.add(leftQuantitative.get(0).get(i).getM_name());
    	}
    	if (rightQuantitative != null)
    	for (int i = 0; i < rightQuantitative.get(0).size(); i++){
    		label.add(rightQuantitative.get(0).get(i).getM_name());
    	}
    
    	String[] labelString = new String[1];
    	labelString = (String[])label.toArray(labelString);
    	try {
			//csvPrinter.writeln(labelString);
    		csvPrinter.writeln(csvParser.m_nameChamp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
    	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	int countline = 0;
    	for (int line = 0; line < csvParser.ObtenirNombreLignes(); line++){
    		boolean running = false;
    		
    	if (leftQualitative != null){
    	Iterator<Qualitative> iterLeftQuali = leftQualitative.iterator();
    	while(iterLeftQuali.hasNext()){
    	   Qualitative ruleElement = (Qualitative) iterLeftQuali.next();
    	   int index = csvParser.ObtenirIndiceChamp(ruleElement.getM_name());
    	   if (csvParser.m_data[line][index].equals(ruleElement.getM_value()))
    	       running = true;
    	   else
    		   {
    		    running = false;
    		    break;
    		   }
    	 }
    	}
    	
    	if (!running) continue;
    	
    	if (rightQualitative != null){
    	Iterator<Qualitative> iterRightQuali = rightQualitative.iterator();
    	while(iterRightQuali.hasNext()){
    	   Qualitative ruleElement = (Qualitative) iterRightQuali.next();
    	   int index = csvParser.ObtenirIndiceChamp(ruleElement.getM_name());
    	   if (csvParser.m_data[line][index].equals(ruleElement.getM_value()))
    	       running = true;
    	   else
    		   {
    		    running = false;
    		    break;
    		   }
    	}
    	}
    	
    	if (!running) continue;
    	if (leftQuantitative != null){
    	Iterator<Vector<Quantitative>> iterLeftQuanti = leftQuantitative.iterator();
    	while(iterLeftQuanti.hasNext()){ //OR
    		Vector<Quantitative> disjunctElement = iterLeftQuanti.next();
    		Iterator<Quantitative> iter = disjunctElement.iterator();
    		while(iter.hasNext()){       //AND
    			Quantitative ruleElement = (Quantitative) iter.next();
    			int index = csvParser.ObtenirIndiceChamp(ruleElement.getM_name());
    	    	if (Float.parseFloat(csvParser.m_data[line][index]) >= ruleElement.getM_lower() && Float.parseFloat(csvParser.m_data[line][index]) <= ruleElement.getM_upper())
    	    	   running = true;
    	    	else
    	    	{
    	    	    running = false;
    	    	    break;
    	    	}
    		}
    		if (running == true)
    			 break; //jump out of OR
    	}
    	}
    	
    	if (!running) continue;
    	if (rightQuantitative != null){
    	Iterator<Vector<Quantitative>> iterRightQuanti = rightQuantitative.iterator();
    	while(iterRightQuanti.hasNext()){ //OR
    		Vector<Quantitative> disjunctElement = iterRightQuanti.next();
    		Iterator<Quantitative> iter = disjunctElement.iterator();
    		while(iter.hasNext()){  //AND
    			Quantitative ruleElement = (Quantitative) iter.next();
    			int index = csvParser.ObtenirIndiceChamp(ruleElement.getM_name());
    			if (Float.parseFloat(csvParser.m_data[line][index]) >= ruleElement.getM_lower() && Float.parseFloat(csvParser.m_data[line][index]) <= ruleElement.getM_upper())
     	    	    running = true;
     	    	else
     	    	{
     	    	    running = false;
     	    	    break;
     	    	}   
    		}
    		if (running == true)
   			   break; //jump out of OR
        }
    	}
    	if (running){
    		countline++;
    		//for (int i = 0; i < (label.size()-1); i++){
    		//	System.out.println("*****************"+csvParser.m_data[line][csvParser.ObtenirIndiceChamp(label.get(i))]+ "************");
    			try {
    				for (int j  = 0; j < csvParser.ObtenirNombreChamps()-1; j++){
    					//csvPrinter.write(csvParser.m_data[line][csvParser.ObtenirIndiceChamp(label.get(i))]);
    					csvPrinter.write(csvParser.m_data[line][j]);
    				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		//}
    		
    		try {
    			//System.out.println("###########"+csvParser.m_data[line][csvParser.ObtenirIndiceChamp(label.get(label.size()-1))]+ "##########");
				//csvPrinter.writeln(csvParser.m_data[line][csvParser.ObtenirIndiceChamp(label.get(label.size()-1))]);
    			csvPrinter.writeln(csvParser.m_data[line][csvParser.ObtenirNombreChamps()-1]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		System.out.println();
    	}
    }//END OF FOR LOOP
    	 try {
				csvPrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    //once switch to another rule, update the rule index and repaint
    private void jScrollBarReglesAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarReglesAdjustmentValueChanged
        int iIndiceRegle = 0;
        
        iIndiceRegle = evt.getValue();
        IndiquerRegleCourante( iIndiceRegle );
    }//GEN-LAST:event_jScrollBarReglesAdjustmentValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCopy;
    private javax.swing.JButton jButtonSauver;
    private javax.swing.JButton jButtonVoirContexte;
    private javax.swing.JScrollBar jScrollBarRegles;
    private javax.swing.JScrollPane jScrollRegles;    //the third part of the result panel
    private javax.swing.JTextField jTextNumeroRegle;  // allows the editing of a single line of text
    private javax.swing.JButton jButtonExtractRows;   //extract rows for a specific rule
    // End of variables declaration//GEN-END:variables

    //Display rules, the third panel
    private RuleBrowser m_afficheurRegles = null;
    
    //The middle panel
    private PanelSort m_panneauTri = null;
    private int m_iNombreReglesTotales = 0;
    private int m_iIndexCurrentRule = 0;
    private int m_iNombreReglesRetenues = 0;
    private AssociationRule [] m_tReglesFiltrees = null;    
    
    // Indique qu'on a appuy� sur le bouton d'affichage du filtre :
    public void IndiquerModificationAffichageFiltre() {
        ArrangerDisposition();
    }
    

    // Met � jour la liste des r�gles en appliquant les param�tres de tri et de filtrage :
    public void MettreAJourListeRegles() {
        ArrayList listeTempRegles = null;
        int iIndiceRegle = 0;
        AssociationRule regle = null;
        Comparator comparateur = null;
        boolean bTriDecroissant = false;
        float fSeuilMaxSupportDroite = 0.0f;
        int iNombreMaxOccurrencesDroite = 0;
        
        if (super.m_contexteResolution.m_listeRegles == null)
            return;
       
        // Prise en compte des informations entr�es pour le tri et le filtrage :
        super.m_contexteResolution.MettreAJourDonneesInternesFiltre_Filtrage();
        
        fSeuilMaxSupportDroite = m_panneauTri.ObtenirSueilMaxSupportConsequent();
        iNombreMaxOccurrencesDroite =  super.m_contexteResolution.m_gestionnaireBD.ObtenirNombreLignes();
        if (fSeuilMaxSupportDroite >= 0.0f)
            iNombreMaxOccurrencesDroite = (int)(((double)iNombreMaxOccurrencesDroite)*((double)fSeuilMaxSupportDroite));
        
            
        listeTempRegles = new ArrayList();
        m_iNombreReglesTotales = super.m_contexteResolution.m_listeRegles.size();
        
        m_iNombreReglesRetenues = 0;
        for (iIndiceRegle=0; iIndiceRegle<m_iNombreReglesTotales; iIndiceRegle++) {
            regle = (AssociationRule)super.m_contexteResolution.m_listeRegles.get(iIndiceRegle);
            if (super.m_contexteResolution.EstRegleValide_Filtrage(regle))
                if (regle.m_iOccurrencesDroite <= iNombreMaxOccurrencesDroite) {
                    listeTempRegles.add(regle);
                    m_iNombreReglesRetenues++;
                }
        }
        
        // Copie d�finitive dans le tableau des r�gles filtr�es :
        m_tReglesFiltrees = null;
        if (m_iNombreReglesRetenues > 0) {
            m_tReglesFiltrees = new AssociationRule[1];
            m_tReglesFiltrees = (AssociationRule [])(listeTempRegles.toArray(m_tReglesFiltrees));
 
        
            // Tri des r�gles :
            bTriDecroissant = m_panneauTri.EstTriDecroissant();
            switch ( m_panneauTri.ObtenirMethodeTri() ) {
                case PanelSort.METHODE_TRI_SUPPORT:
                    comparateur = AssociationRule.ObtenirComparateurSupport(bTriDecroissant);
                    break;
                case PanelSort.METHODE_TRI_NOMBRE_ATTRIBUTS:
                    comparateur = AssociationRule.ObtenirComparateurNombreAttributs(bTriDecroissant);
                    break;
                default :
                    comparateur = AssociationRule.ObtenirComparateurConfiance(bTriDecroissant);
            }
            Arrays.sort(m_tReglesFiltrees, comparateur);
        }
        
        
        // On d�clare le nouveau tableau des r�gles filtr�es et tri�es :
        m_afficheurRegles.DefinirListeRegles(m_tReglesFiltrees);
        if (m_iNombreReglesRetenues > 0)
            jScrollBarRegles.setMaximum(m_iNombreReglesRetenues-1);
        else
            jScrollBarRegles.setMaximum(0);
                   
        if (m_iNombreReglesRetenues > 0)
            IndiquerRegleCourante(0);
        else
            IndiquerRegleCourante(-1);
    }
    
    
    //the index of the rule --middle part
    public void IndiquerRegleCourante(int iIndiceRegleCourante) {
        String sTexteNumeroRegleCourante = null;
        
        m_iIndexCurrentRule = iIndiceRegleCourante;
        
        if ( (iIndiceRegleCourante >= 0) && (m_iNombreReglesRetenues>0) ) {
            sTexteNumeroRegleCourante = "Rule ";
            sTexteNumeroRegleCourante += String.valueOf(iIndiceRegleCourante+1);
            sTexteNumeroRegleCourante += "/";
            sTexteNumeroRegleCourante += String.valueOf(m_iNombreReglesRetenues);
            sTexteNumeroRegleCourante += " (total : ";
            sTexteNumeroRegleCourante += String.valueOf(m_iNombreReglesTotales);
            sTexteNumeroRegleCourante += ")";
        }
        else
            sTexteNumeroRegleCourante = "No rule selected";
       
        jTextNumeroRegle.setText(sTexteNumeroRegleCourante);
        
        //at the same time, repaint the third part to match with the current index of rule!!!
        m_afficheurRegles.DefinirIndiceRegleAffichee(iIndiceRegleCourante);
    }    
    
    
    
    public AssociationRule ObtenirRegleCourante() {
        int iIndiceRegleCourante = 0;
        
        iIndiceRegleCourante = m_afficheurRegles.ObtenirIndiceRegleAffichee();
        
        if ( (m_tReglesFiltrees != null) && (iIndiceRegleCourante >= 0) && (m_iNombreReglesRetenues>0) )
            return m_tReglesFiltrees[iIndiceRegleCourante];
        else
            return null;
    }
    
    
    
    void ArrangerDisposition() {
        int iDeltaPosX = 0; // Diff�rence de positionnement horizontal entre la position id�ale et celle de l'�diteur de formulaires
        int iDeltaPosY = 0; // Diff�rence de positionnement vertical entre la position id�ale et celle de l'�diteur de formulaires
        
        super.ArrangerDisposition();
        
        iDeltaPosX = jButtonSauver.getX() - super.m_zoneControles.x;
        iDeltaPosY = jButtonSauver.getY() - super.m_zoneControles.y;

        jButtonSauver.setLocation(jButtonSauver.getX()-iDeltaPosX, jButtonSauver.getY()-iDeltaPosY);
        jButtonVoirContexte.setLocation(jButtonVoirContexte.getX()-iDeltaPosX, jButtonVoirContexte.getY()-iDeltaPosY);
        
        if (m_panneauTri.EstFiltreAffiche())
            m_panneauTri.setBounds(
                super.m_zoneControles.x,
                jButtonSauver.getY()+jButtonSauver.getHeight()+10,
                super.m_zoneControles.width,
                super.m_zoneControles.height/2); 
        else
            m_panneauTri.setBounds(
                super.m_zoneControles.x,
                jButtonSauver.getY()+jButtonSauver.getHeight()+10,
                super.m_zoneControles.width,
                m_panneauTri.ObtenirTailleReduite()); 

        m_panneauTri.ArrangerDisposition();
        
        jTextNumeroRegle.setBounds(
            jTextNumeroRegle.getX()-iDeltaPosX,
            m_panneauTri.getY()+m_panneauTri.getHeight()+10,
            jTextNumeroRegle.getWidth(),
            jTextNumeroRegle.getHeight()); 
        
        jButtonExtractRows.setBounds(
                super.m_zoneControles.width+super.m_zoneControles.x - (jButtonExtractRows.getWidth()),
                jTextNumeroRegle.getY(),
                jButtonExtractRows.getWidth(),
                jButtonExtractRows.getHeight());  
        
        
        jButtonCopy.setBounds(
        	jButtonExtractRows.getX()-jButtonCopy.getWidth(),
            jTextNumeroRegle.getY(),
            jButtonCopy.getWidth(),
            jButtonCopy.getHeight());     
        
        jScrollBarRegles.setBounds(
            jScrollBarRegles.getX()-iDeltaPosX,
            jTextNumeroRegle.getY(),
            jButtonCopy.getX() - 2 - (jScrollBarRegles.getX()-iDeltaPosX),
            jScrollBarRegles.getHeight());
        
        jScrollRegles.setBounds(
            super.m_zoneControles.x,
            jTextNumeroRegle.getY()+jTextNumeroRegle.getHeight()+2,
            super.m_zoneControles.width,
            super.m_zoneControles.height+super.m_zoneControles.y-(jTextNumeroRegle.getY()+jTextNumeroRegle.getHeight()+2));
        jScrollRegles.validate();

        m_afficheurRegles.setPreferredSize( jScrollRegles.getViewport().getExtentSize());
        m_afficheurRegles.revalidate();
        
        m_afficheurRegles.DefinirDimensionConteneur(jScrollRegles.getWidth(), jScrollRegles.getHeight());
        m_afficheurRegles.repaint();
    }

      
}
