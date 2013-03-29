/*                                             
 *Copyright 2007, 2011 CCLS Columbia University (USA), LIFO University of Orleans (France), BRGM (France)
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
package src.solver;

import java.util.*;
import java.io.*;

import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;
import src.graphicalInterface.*;
import src.simulatedAnnealing.*;
import src.tools.*;


import com.Ostermiller.util.CSVPrinter;
import com.Ostermiller.util.ExcelCSVPrinter;


// Classe permettant de memoriser un ensemble de parametres a conserver tout au long du processus de resolution :
// Class allowing to remember a set of parameters to use all over the process:
public class ResolutionContext {
    
    // Classe a outrepasser pour implanter l'enregistrement graphique d'une regle dans un fichier :
	// Class to implement the graphical recording of a rule in a file: 
    public interface EnregistreurGraphiqueRegle {
        
        // Fonction qui cree l'image JPEG de la regle et renvoie le nom du fichier sur le disque :
    	// Function that create a JPEG picture of the rule and send back the name of the file on disc:
        public String EnregistrerRegle(AssociationRule regle, int iIndiceRegle);
    }
    
    
    // Identification of techniques possibles d'extraction rules quantitatives :
    public static final int TECHNIQUE_INDEFINIE = 0;         //undefined technique
    public static final int TECHNIQUE_APRIORI_QUAL = 1;      //Apriori algorithm
    public static final int TECHNIQUE_ALGO_GENETIQUE = 2;    //Generic algorithm
    public static final int TECHNIQUE_RECUIT_SIMULE = 3;     //simulated annealing algorithm
    public static final int TECHNIQUE_CHARGEMENT = 4;        //load rule file
    
    
    // Indicate the position of each item in the association rule:
    public static final int PRISE_EN_COMPTE_INDEFINI = 0;         //undefined
    public static final int PRISE_EN_COMPTE_ITEM_NULLE_PART = 1;  //no where
    public static final int PRISE_EN_COMPTE_ITEM_GAUCHE = 2;      //left side
    public static final int PRISE_EN_COMPTE_ITEM_DROITE = 3;      //right side
    public static final int PRISE_EN_COMPTE_ITEM_2_COTES = 4;     //two sides
    
    // Selecteurs d'informations a sauvegarder dans un profil :        These parameter has something to do with profile
    public static final int PROFIL_INFO_PRECHARGEMENT = 1;
    public static final int PROFIL_INFO_PREEXTRACTION = 2;
    public static final int PROFIL_INFO_ALGO_APRIORI = 4;
    public static final int PROFIL_INFO_ALGO_GENETIQUE = 8;
    public static final int PROFIL_INFO_ALGO_RECUIT = 16;
    public static final int PROFIL_INFO_ALGO_CHARGEMENT = 32;
    
    // Constantes utilisees pour l'ecriture des fichiers de profils :  constant values used to write the profile files
    private static final int FICHIER_PROFIL_DONNEES_AUCUNES = 0;
    private static final int FICHIER_PROFIL_DONNEES_PRECHARGE = 1;
    private static final int FICHIER_PROFIL_DONNEES_PREEXTRACTION = 2;
    private static final int FICHIER_PROFIL_DONNEES_ALGO_APRIORI = 3;
    private static final int FICHIER_PROFIL_DONNEES_ALGO_GENETIQUE = 4;
    private static final int FICHIER_PROFIL_DONNEES_ALGO_RECUIT = 5;
    private static final int FICHIER_PROFIL_DONNEES_ALGO_CHARGEMENT = 6;
    
    private static final int FICHIER_PROFIL_CHAMP_TYPE_QUAL = 1;
    private static final int FICHIER_PROFIL_CHAMP_TYPE_QUANT = 2;
    
    private PositionRuleParameters m_positionnementRegles = null;
    private PositionRuleParameters m_filtrageRegles = null;
    
    //Following two will be in profile file
    public String m_sNomUtilisateur = null;             //user name
    public String m_sDescriptionRegles = null;          //description
    
    public MainWindow m_fenetreProprietaire = null;  // Fenetre conteneur de l'application -- windows containing the application
    public DatabaseAdmin m_gestionnaireBD = null;  // Acces a la base de donnees en cours -- access the database in use
    public int m_iTechniqueResolution = 0;  // Identifiant de la technique d'extraction des regles quantitatives -- identifier of the extraction technique
    public ArrayList<AssociationRule> m_listeRegles = null;  // Liste des dernieres regles optimales calculees -- list of the last optimal rules calculated
    
    
    // Objects definissant les parametres utilisateur pour chaque technique d'extraction : object defininf user parameter for each extraction technique
    public StandardParameters m_parametresRegles = null;    // Criteria  generaux sur les regles a extraire -- general criteria for the rules to extract
    public StandardParametersQuantitative m_parametresReglesQuantitatives = null;    // ici avec des indications particulieres relatives aux attributs quantitatifs -- here with indications for quantitative attributes
    public ParametersGeneticAlgo m_parametresTechAlgoGenetique = null;    // Parametres utilisateur relatifs a la technique de l'algorithme genetique  -- user parameter for the genetic algorithm
    public SimulatedAnnealingParameters m_parametresTechRecuitSimule = null;  // Parametres utilisateur relatifs a la technique du recuit simule -- user parameter for the simulated annealing
    public LoadingParameters m_parametresTechChargement = null;  // Parametres utilisateur concernant le chargement d'un fichier de regles -- user parameter for loading the rule file
    // ... Ajouter ici d'autres objets pour chaque nouvelle technique
    
    public AprioriQuantitative m_aprioriCourant = null;  // Derniere instance executee de l'algorithme Apriori -- last excecution instance of apriori
    
    
    public ResolutionContext(MainWindow fenetreProprietaire) {
        
        m_sNomUtilisateur = ENV.NOM_UTILISATEUR;        //user name
        m_sDescriptionRegles = "No description.";       //description
        
        m_fenetreProprietaire = fenetreProprietaire;
        m_gestionnaireBD = null;
        m_iTechniqueResolution = TECHNIQUE_APRIORI_QUAL;
        m_listeRegles = null;
        
        m_parametresRegles = new StandardParameters();
        m_parametresReglesQuantitatives = new StandardParametersQuantitative();
        m_parametresTechAlgoGenetique = new ParametersGeneticAlgo();
        m_parametresTechRecuitSimule = new SimulatedAnnealingParameters();
        m_parametresTechChargement = new LoadingParameters();
        
        m_positionnementRegles = new PositionRuleParameters(this, PositionRuleParameters.PARAMETRES_POSITION_REGLES);
        m_filtrageRegles = new PositionRuleParameters(this, PositionRuleParameters.PARAMETRES_FILTRAGE_REGLES);
        
        m_aprioriCourant = null;
    }
    
    // Methodes d'acces direct aux parametres de positionnement des attributs dans les regles :
    // Methods to directly access to the positionning parameters of the attributes in the rules
    public void GenererStructuresDonneesSelonBDPriseEnCompte() { m_positionnementRegles.GenererStructuresDonneesSelonBDPriseEnCompte(); }
    public void DefinirTypePrisEnCompteItem(String sAttribut, String sItem, int iTypePosition) { m_positionnementRegles.DefinirTypePrisEnCompteItem(sAttribut, sItem, iTypePosition); }
    public int ObtenirTypePrisEnCompteItem(String sAttribut, String sItem) { return m_positionnementRegles.ObtenirTypePrisEnCompteItem(sAttribut, sItem); }
    public void DefinirTypePrisEnCompteAttribut(String sAttribut, int iTypePosition) { m_positionnementRegles.DefinirTypePrisEnCompteAttribut(sAttribut, iTypePosition); }
    public int ObtenirTypePrisEnCompteAttribut(String sAttribut) { return m_positionnementRegles.ObtenirTypePrisEnCompteAttribut(sAttribut); }
    public void DefinirPresenceObligatoireItem(String sAttribut, String sItem, boolean bPresenceObligatoire) { m_positionnementRegles.DefinirPresenceObligatoireItem(sAttribut, sItem, bPresenceObligatoire); }
    public void DefinirPresenceObligatoireAttribut(String sAttribut, boolean bPresenceObligatoire) { m_positionnementRegles.DefinirPresenceObligatoireAttribut(sAttribut, bPresenceObligatoire); }
    public boolean ObtenirPresenceObligatoireItem(String sAttribut, String sItem) { return m_positionnementRegles.ObtenirPresenceObligatoireItem(sAttribut, sItem); }
    public int ObtenirPresenceObligatoireAttribut(String sAttribut) { return m_positionnementRegles.ObtenirPresenceObligatoireAttribut(sAttribut); }
    public void MettreAJourDonneesInternesFiltre() { m_positionnementRegles.MettreAJourDonneesInternesFiltre(); }
    public void DefinirPositionnementPourTous(int iTypePosition, boolean bPresenceObligatoire) { m_positionnementRegles.DefinirPositionnementPourTous(iTypePosition, bPresenceObligatoire); }
    public boolean EstFiltreCoherent() { return m_positionnementRegles.EstFiltreCoherent(); }
    public boolean EstItemSetValide(ItemSet itemSet) { return m_positionnementRegles.EstItemSetValide(itemSet); }
    public boolean EstRegleValide(AssociationRule regle) { return m_positionnementRegles.EstRegleValide(regle); }
    
    
    // Methodes d'acces direct aux parametres de filtrage des attributs dans les regles :
    // Methods to directly access to the filtering of the attributes in the rules:
    public void GenererStructuresDonneesSelonBDPriseEnCompte_Filtrage() { m_filtrageRegles.GenererStructuresDonneesSelonBDPriseEnCompte(); }
    public void DefinirTypePrisEnCompteItem_Filtrage(String sAttribut, String sItem, int iTypePosition) { m_filtrageRegles.DefinirTypePrisEnCompteItem(sAttribut, sItem, iTypePosition); }
    public int ObtenirTypePrisEnCompteItem_Filtrage(String sAttribut, String sItem) { return m_filtrageRegles.ObtenirTypePrisEnCompteItem(sAttribut, sItem); }
    public void DefinirTypePrisEnCompteAttribut_Filtrage(String sAttribut, int iTypePosition) { m_filtrageRegles.DefinirTypePrisEnCompteAttribut(sAttribut, iTypePosition); }
    public int ObtenirTypePrisEnCompteAttribut_Filtrage(String sAttribut) { return m_filtrageRegles.ObtenirTypePrisEnCompteAttribut(sAttribut); }
    public void DefinirPresenceObligatoireItem_Filtrage(String sAttribut, String sItem, boolean bPresenceObligatoire) { m_filtrageRegles.DefinirPresenceObligatoireItem(sAttribut, sItem, bPresenceObligatoire); }
    public void DefinirPresenceObligatoireAttribut_Filtrage(String sAttribut, boolean bPresenceObligatoire) { m_filtrageRegles.DefinirPresenceObligatoireAttribut(sAttribut, bPresenceObligatoire); }
    public boolean ObtenirPresenceObligatoireItem_Filtrage(String sAttribut, String sItem) { return m_filtrageRegles.ObtenirPresenceObligatoireItem(sAttribut, sItem); }
    public int ObtenirPresenceObligatoireAttribut_Filtrage(String sAttribut) { return m_filtrageRegles.ObtenirPresenceObligatoireAttribut(sAttribut); }
    public void MettreAJourDonneesInternesFiltre_Filtrage() { m_filtrageRegles.MettreAJourDonneesInternesFiltre(); }
    public void DefinirPositionnementPourTous_Filtrage(int iTypePosition, boolean bPresenceObligatoire) { m_filtrageRegles.DefinirPositionnementPourTous(iTypePosition, bPresenceObligatoire); }
    public boolean EstFiltreCoherent_Filtrage() { return m_filtrageRegles.EstFiltreCoherent(); }
    public boolean EstItemSetValide_Filtrage(ItemSet itemSet) { return m_filtrageRegles.EstItemSetValide(itemSet); }
    public boolean EstRegleValide_Filtrage(AssociationRule regle) { return m_filtrageRegles.EstRegleValide(regle); }
    
    // Accesseurs pour les informations de positionnement :
    // Assessors for the positionning information
    public PositionRuleParameters ObtenirInfosPostionnementRegles() { return m_positionnementRegles; }
    public PositionRuleParameters ObtenirInfosPostionnementFiltrage() { return m_filtrageRegles; }
    
    public String ObtenirInfosContexte(boolean bAjouterTagsHTML) {
        String sInfoContexte = null;
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonne = 0;
        DataColumn colonne = null;
        AttributQualitative attributQual = null;
        AttributQuantitative attributQuant = null;
        
        sInfoContexte = "";
        
        if ( (m_aprioriCourant == null) || (m_gestionnaireBD == null) )
            return sInfoContexte;
        
        // Si les regles proviennent d'un fichier, on renvoie le contexte original :
        // If the rules come from a file, we send the general context:
        if (m_iTechniqueResolution == TECHNIQUE_CHARGEMENT)
            return m_parametresTechChargement.toString();
        
        
        if (bAjouterTagsHTML) sInfoContexte += "<a name=\"conftech\"><big><u><b>";
        sInfoContexte += "Parameters :";
        if (bAjouterTagsHTML) sInfoContexte += "</b></u></big></a>";
        sInfoContexte += "\n\n\n";
        
        // Recapitulation des parametres :
        // summarization of the parameters:
        if (bAjouterTagsHTML) sInfoContexte += "<i>";
        sInfoContexte += "Method : ";
        if (bAjouterTagsHTML) sInfoContexte += "</i><b>";
        
        switch (m_iTechniqueResolution) {
            case TECHNIQUE_APRIORI_QUAL :
                sInfoContexte += "Apriori algorithm (categorical)\n\n\n";
                if (bAjouterTagsHTML) sInfoContexte += "</b>";
                sInfoContexte += m_parametresRegles.toString() + "\n\n";
                break;
                
            case TECHNIQUE_ALGO_GENETIQUE :
                sInfoContexte += "genetic algorithm\n\n\n";
                if (bAjouterTagsHTML) sInfoContexte += "</b>";
                sInfoContexte += m_parametresReglesQuantitatives.toString() + "\n\n";
                sInfoContexte += m_parametresTechAlgoGenetique.toString() + "\n\n";
                break;
                
            case TECHNIQUE_RECUIT_SIMULE :
                sInfoContexte += "simulated annealing\n\n\n";
                if (bAjouterTagsHTML) sInfoContexte += "</b>";
                sInfoContexte += m_parametresReglesQuantitatives.toString() + "\n\n";
                sInfoContexte += m_parametresTechRecuitSimule.toString() + "\n\n";
                break;
                
            default:
                if (bAjouterTagsHTML) sInfoContexte += "</b>";
        }
        sInfoContexte += "\n\n\n";
        
        if (bAjouterTagsHTML) sInfoContexte += "<a name=\"recapattr\"><big><u><b>";
        sInfoContexte += "Summary of the attributes used in mining rules:";
        if (bAjouterTagsHTML) sInfoContexte += "</b></u></big></a>";
        sInfoContexte += "\n\n\n";
        
        iNombreColonnesPrisesEnCompte = m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte();
        
        if (bAjouterTagsHTML) sInfoContexte += "<b>";
        sInfoContexte += "Qualitative attributes :";
        if (bAjouterTagsHTML) sInfoContexte += "</b>";
        sInfoContexte += "\n\n";
        
        for (iIndiceColonne=0; iIndiceColonne<iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
            colonne = m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            if (colonne != null)
                if ( (colonne.m_sNomColonne != null) && (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM) ) {
                if (m_positionnementRegles.ObtenirTypePrisEnCompteAttribut(colonne.m_sNomColonne) != PRISE_EN_COMPTE_ITEM_NULLE_PART) {
                    attributQual = m_aprioriCourant.ObtenirAttributQualitatifDepuisNom(colonne.m_sNomColonne);
                    if (attributQual != null)
                        sInfoContexte += attributQual.ObtenirNom() + ", ";
                    sInfoContexte += String.valueOf(attributQual.m_colonneDonnees.ObtenirNombreValeursDifferentes()) + " values.\n";
                }
                }
        }
        sInfoContexte += "\n\n";
        
        
        if (m_iTechniqueResolution != TECHNIQUE_APRIORI_QUAL) {
            if (bAjouterTagsHTML) sInfoContexte += "<b>";
            sInfoContexte += "Quantitative attributes:";
            if (bAjouterTagsHTML) sInfoContexte += "</b>";
            sInfoContexte += "\n\n";
            
            for (iIndiceColonne=0; iIndiceColonne<iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
                colonne = m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
                if (colonne != null)
                    if ( (colonne.m_sNomColonne != null) && (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL) ) {
                    if (m_positionnementRegles.ObtenirTypePrisEnCompteAttribut(colonne.m_sNomColonne) != PRISE_EN_COMPTE_ITEM_NULLE_PART) {
                        attributQuant = m_aprioriCourant.ObtenirAttributQuantitatifDepuisNom(colonne.m_sNomColonne);
                        if (attributQuant != null)
                            sInfoContexte += attributQuant.ObtenirNom() + ", domain ";
                        sInfoContexte += "[ " + String.valueOf( colonne.ObtenirBorneMin() );
                        sInfoContexte +=  ", " + String.valueOf( colonne.ObtenirBorneMax() ) + "].\n";
                    }
                    }
            }
            sInfoContexte += "\n\n";
            
        }
        sInfoContexte += "\n\n\n";
        
        
        if (bAjouterTagsHTML) sInfoContexte += "<a name=\"lstfreq\"><big><u><b>";
        sInfoContexte += "Frequent itemsets:";
        if (bAjouterTagsHTML) sInfoContexte += "</b></u></big></a>";
        sInfoContexte += "\n\n\n";
        
        sInfoContexte += m_aprioriCourant.EcrireListeFrequents();
        sInfoContexte += "\n\n\n\n\n";
        
        if (bAjouterTagsHTML)
            sInfoContexte = FormateHTML(sInfoContexte);
        
        return sInfoContexte;
    }
    
    
    // (remplace notamment les sauts de lignes "\n" par le tag <BR>) :
    // (replace the carriages "\n" with the tag <BR>):
    public static String FormateHTML(String sChaineInitiale) {
        if (sChaineInitiale == null)
            return null;
        
        return sChaineInitiale.replaceAll("\n", "<BR>");
    }
    
    public void SauvegarderReglesCsv(String sCheminFichier, AssociationRule[] tRegles) {
		    int iNombreRegles = 0;
	        int iIndiceRegle = 0;
	        int iNombreLignesBD = 0;
	        float fValeurConfiance = 0.0f;
	        AssociationRule regle = null;
	        ExcelCSVPrinter csvPrinter = null;
		    
	        System.out.println(sCheminFichier);
	        try {
	        	csvPrinter = new ExcelCSVPrinter(new FileOutputStream(sCheminFichier));
	        } catch(IOException e) {
	            System.out.println( e.getMessage() );
	            return;
	        }
	        
		    if (tRegles != null) {
	            iNombreRegles = tRegles.length;
	            System.out.println(iNombreRegles);
	        } else {
	            iNombreRegles = 0;
	        }
		    
		    //output the attributes
		    String [] attribute = {"Left","Right","Frequency[L and R]","Support[L and R]","Confidence[L->R]","Confidence[(~L)->R]","Confidence[R->L]",
		    		"Confidence[(~R)->L]", "Confidence[L<->R]"};
		    try {
				csvPrinter.writeln(attribute);
			
		    iNombreLignesBD = m_gestionnaireBD.ObtenirNombreLignes();
		    for (iIndiceRegle = 0; iIndiceRegle < iNombreRegles; iIndiceRegle++) {
	            regle = tRegles[iIndiceRegle];
	            csvPrinter.write(regle.leftToString());
	            csvPrinter.write(regle.rightToString());
	            csvPrinter.write(Integer.toString(regle.m_iOccurrences));
	            csvPrinter.write(EcrirePourcentage(regle.m_fSupport, 2, true));
	            csvPrinter.write(EcrirePourcentage(regle.m_fConfiance, 2, true));
	            
	            if ((iNombreLignesBD - regle.m_iOccurrencesGauche) > 0)
	            {
	                fValeurConfiance = ((float)regle.m_iOccurrences_NonGauche_Droite) / ((float)(iNombreLignesBD - regle.m_iOccurrencesGauche));
	                csvPrinter.write(EcrirePourcentage(fValeurConfiance, 2, true));
		        }
	            else{
	            	csvPrinter.write("no non-left");
	            }
	            
	            fValeurConfiance = ((float)regle.m_iOccurrences) / ((float)regle.m_iOccurrencesDroite);
	            csvPrinter.write(EcrirePourcentage(fValeurConfiance, 2, true));
	            
	            if ( (iNombreLignesBD - regle.m_iOccurrencesDroite) > 0)
	            {
	                fValeurConfiance = ((float)regle.m_iOccurrences_Gauche_NonDroite) / ((float)(iNombreLignesBD - regle.m_iOccurrencesDroite));
	                csvPrinter.write(EcrirePourcentage(fValeurConfiance, 2, true));
	            }
	            else{
	            	csvPrinter.write("no non-left");
	            }
	            
	            fValeurConfiance = ((float)(regle.m_iOccurrences + regle.m_iOccurrences_NonGauche_NonDroite)) / ((float)iNombreLignesBD);
	            csvPrinter.writeln(EcrirePourcentage(fValeurConfiance, 2, true));
		    }
		    } catch (IOException e) {
				e.printStackTrace();
			}
		    try {
				csvPrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
    
    //save rules in HTML file (text and graphic)
    public void SauvegarderReglesHTML(String sCheminFichier, AssociationRule [] tRegles, boolean bGraphique, EnregistreurGraphiqueRegle enregistreurGraphique) {
        DataOutputStream fluxFichier = null;
        int iNombreRegles = 0;
        int iIndiceRegle = 0;
        AssociationRule regle = null;
        String [] tNomsFichiersGraphiques = null;
        
        try {
            fluxFichier = new DataOutputStream( new FileOutputStream(sCheminFichier) );
        } catch(IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        try {
            fluxFichier.writeChars( "<HTML>" );
            fluxFichier.writeChars( "<HEAD>" );
            fluxFichier.writeChars( "<TITLE>" + "Rules extraction- Results" + "</TITLE>" );
            fluxFichier.writeChars( "</HEAD>" );
            fluxFichier.writeChars( "<BODY>" );
        } catch (IOException e) {}
        
        // Ecriture du titre, quelques infos et du sommaire :
        try {
            fluxFichier.writeChars( "<BR><p align=\"center\"><big><big><big><b>ASSOCIATION RULES ANALYSIS</b></big></big></big></p>" );
            fluxFichier.writeChars( "<BR><BR><BR>" );
            
            fluxFichier.writeChars( "<i><BR>Software: </i><b>QuantMiner version "+ENV.VERSION_QUANTMINER+"</b><BR>" );
            fluxFichier.writeChars( "<i>Date: </i><b>"+ ENV.ObtenirDateCourante() +"</b><BR>" );
            fluxFichier.writeChars( "<i>Database: </i><b>"+ m_gestionnaireBD.ObtenirNomBaseDeDonnees() +"</b><BR>" );
            fluxFichier.writeChars( "<i>Author: </i><b>"+ this.m_sNomUtilisateur +"</b><BR>" );
            
            fluxFichier.writeChars( "<BR><BR><BR><BR><p align=\"center\">" );
            fluxFichier.writeChars( "<a href=\"#conftech\">Parameters</a><BR><BR>" );
            if (m_iTechniqueResolution != TECHNIQUE_CHARGEMENT) {
                fluxFichier.writeChars( "<a href=\"#recapattr\">Attributes used</a><BR><BR>" );
                fluxFichier.writeChars( "<a href=\"#lstfreq\">List of frequent itemsets</a><BR><BR>" );
            }
            fluxFichier.writeChars( "<a href=\"#lstregles\">Extracted Rules List</a><BR><BR>" );
            fluxFichier.writeChars( "</p><BR><BR><BR><BR><BR><BR>" );
        } catch (IOException e) {}
        
        try {
            fluxFichier.writeChars( ObtenirInfosContexte(true) );
        } catch (IOException e) {}
        
        // write the rule list
        try {
            fluxFichier.writeChars( "<a name=\"lstregles\"><big><u><b>List of Rules:</b></u></big></a><BR><BR><BR>");
        } catch (IOException e) {}
        
        if (tRegles != null) {
            iNombreRegles = tRegles.length;
            tNomsFichiersGraphiques = new String [iNombreRegles];
        } else {
            iNombreRegles = 0;
            tNomsFichiersGraphiques = null;
        }
        
        for (iIndiceRegle = 0; iIndiceRegle < iNombreRegles; iIndiceRegle++) {
            regle = tRegles[iIndiceRegle];
            tNomsFichiersGraphiques[iIndiceRegle] = null;
            
            try {
                if ( bGraphique && (enregistreurGraphique != null) )
                    tNomsFichiersGraphiques[iIndiceRegle] = enregistreurGraphique.EnregistrerRegle(regle, iIndiceRegle);
                
                if (tNomsFichiersGraphiques[iIndiceRegle] != null)
                    fluxFichier.writeChars("<a href=\"#" + String.valueOf(iIndiceRegle) + "\">");
                
                fluxFichier.writeChars(regle.toString());
                
                if (tNomsFichiersGraphiques[iIndiceRegle] != null)
                    fluxFichier.writeChars("</a>");
                
                fluxFichier.writeChars("<BR><BR>");
            } catch (IOException e) {}
        }
        
        
        // Affichage de la liste de regles en images :
        // Browsing the lists or rules images: 
        
        if ( bGraphique && (enregistreurGraphique != null) ) {
            
            try {
                fluxFichier.writeChars("<BR><BR>");
            } catch (IOException e) {}
            
            for (iIndiceRegle=0; iIndiceRegle<iNombreRegles; iIndiceRegle++) {
                regle = tRegles[iIndiceRegle];
                if (tNomsFichiersGraphiques[iIndiceRegle] != null)
                    try {
                        fluxFichier.writeChars("<BR><BR><hr width=\"40%\" size=\"4\" align=\"center\"><BR>");
                        fluxFichier.writeChars("<a name=\"" + String.valueOf(iIndiceRegle) + "\"><BR></a><BR>");
                        fluxFichier.writeChars("<p align=\"center\"><img src=\"" + tNomsFichiersGraphiques[iIndiceRegle] + "\"><BR><p><BR>");
                        
                    } catch (IOException e) {}
            }
            
        }
        
        
        try {
            fluxFichier.writeChars("<BR><BR><BR><BR><BR><BR><BR><BR><BR>");
        } catch (IOException e) {}
        
        
        // Cloture du fichier HTML :
        // Closing the HTML file:
        try {
            fluxFichier.writeChars( "</BODY>" );
            fluxFichier.writeChars( "</HTML>" );
        } catch (IOException e) {}
        
        
        try {
            fluxFichier.close();
        } catch (IOException e) {
        }
    }
    
    
    /**Saving a profile
     * @param sCheminFichier File name
     * @param iInfosSelectionnees
     */
    
    public void SauvegarderProfil(String sCheminFichier, int iInfosSelectionnees) {
        DataOutputStream fluxFichier = null;
        int iNombreChamps = 0;
        int iIndiceChamp = 0;
        String sNomChamp = null;
        int iNombreAttributsPrisEnCompte = 0;
        int iIndiceAttribut = 0;
        DataColumn colonneDonnees = null;
        String sNomAttribut = null;
        String [] tItems = null;
        int iIndiceItem = 0;
        
        if (m_gestionnaireBD == null)
            return;
        
        try {
            fluxFichier = new DataOutputStream( new FileOutputStream(sCheminFichier) );
        } catch(IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        
        try {
            fluxFichier.writeUTF( "PROFIL01.00" );    // Identification
            fluxFichier.writeUTF( m_gestionnaireBD.ObtenirNomBaseDeDonnees() );    // name of the data file for that profile!!!
            System.out.println( m_gestionnaireBD.ObtenirNomBaseDeDonnees() );
        } catch (IOException e) {}
        

        // Informations about pre-load the information about that BD :
        if ( (iInfosSelectionnees & PROFIL_INFO_PRECHARGEMENT) != 0 ) {
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_PRECHARGE);    // Identification !!!  1
            } catch (IOException e) {}
            
            
            // For every column de la BD, indicate son type et s'il est pris en compte :
            iNombreChamps = m_gestionnaireBD.ObtenirNombreColonnesBDInitiale();    //obtain the number of columns in the data file
            
            // Ecriture du nombre de champs :
            // Writing the number of fields:
            try {
                fluxFichier.writeInt(iNombreChamps);    //Number of columns in that data file!!!
            } catch (IOException e) {}
            
            if (iNombreChamps > 0) {
                
                for (iIndiceChamp = 0; iIndiceChamp < iNombreChamps; iIndiceChamp++) {
                    sNomChamp = m_gestionnaireBD.ObtenirNomColonneBDInitiale(iIndiceChamp); //name of the columns in that data file
                    
                    try {
                        // Write the name of that column:
                        fluxFichier.writeUTF(sNomChamp);                                   //name of the columns in that data file!!!
                        
                        // Write the type of that column:
                        if (m_gestionnaireBD.ObtenirTypeColonne(sNomChamp) == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL)
                            fluxFichier.writeByte(FICHIER_PROFIL_CHAMP_TYPE_QUANT);       //column type!!!
                        else
                            fluxFichier.writeByte(FICHIER_PROFIL_CHAMP_TYPE_QUAL);        //column type!!!
                        
                        // Write value (0 or 1) indicating if that column has been selected during computation:
                        if ( m_gestionnaireBD.EstPriseEnCompteColonne(sNomChamp) )       //Not checked in Step 1
                            fluxFichier.writeByte(1);                                    //selected during computation
                        else  
                            fluxFichier.writeByte(0);                                    //no selected during computation
                    } catch (IOException e) {}
                    
                }
            }
        }
        
        // Information about pre-extraction from information of that BD:
        if ( (iInfosSelectionnees & PROFIL_INFO_PREEXTRACTION) != 0 ) {
            
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_PREEXTRACTION);    // Identification!!! 2
            } catch (IOException e) {}
            
            iNombreAttributsPrisEnCompte = m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte(); //Checked in Step 1
            
            // Ecriture du nombre d'attributs pris en compte pour l'extraction des r�gles :
            // Writing the number of attributes taking into consideration for rule extraction: 
            try {
            	//Writes an int to the underlying output stream as four bytes, high byte first
                fluxFichier.writeInt( iNombreAttributsPrisEnCompte );   //Number of item checked in Step 1
            } catch (IOException e) {};
            
            for (iIndiceAttribut = 0; iIndiceAttribut < iNombreAttributsPrisEnCompte; iIndiceAttribut++) {
                
                try {
                    
                    colonneDonnees = m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceAttribut);
                    if (colonneDonnees != null) {
                        
                        sNomAttribut = new String( colonneDonnees.m_sNomColonne );
                        
                        fluxFichier.writeUTF(sNomAttribut); 
                        
                        // Write in the profile if the column type is categorical
                        if (m_gestionnaireBD.ObtenirTypeColonne(sNomAttribut) == DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM) {
                            fluxFichier.writeByte(1); // Indicate que l'attribut est pas qualitatif, et so qu'il faut prendre en compte le positionnement de ses items -- indicate that the attribute id qualitative
                            
                            tItems = colonneDonnees.ConstituerTableauValeurs();
                            fluxFichier.writeInt(tItems.length); //Write the number of d'items pour l'attribut!!!
                            
                            if (tItems != null)
                                for (iIndiceItem = 0; iIndiceItem < tItems.length; iIndiceItem++) {
                                fluxFichier.writeUTF(tItems[iIndiceItem]);
                                System.out.println("xixi " + tItems[iIndiceItem]);
                                fluxFichier.writeByte( ObtenirTypePrisEnCompteItem(sNomAttribut, tItems[iIndiceItem]) );
                                if ( ObtenirPresenceObligatoireItem(sNomAttribut, tItems[iIndiceItem]) )
                                    fluxFichier.writeByte(1);  
                                else
                                    fluxFichier.writeByte(0);
                                }
                        }
                        // Sinon on place un identificateur pour indiquer qu'il n'y a pas d'items qualitatifs,
                        // mais simplement le positionnement general de l'attribut quantitatif :
                        // otherwise we place an indicator that there is no qualitative items, but simply the general postionning of the quantitative attribute
                        else { //Not Item, i.e. numerical
                            fluxFichier.writeByte(0); //Indique que l'attribut n'est pas qualitatif -- indicate that the attribute is not qualitative
                            fluxFichier.writeByte( ObtenirTypePrisEnCompteAttribut(sNomAttribut) );
                            if ( ObtenirPresenceObligatoireAttribut(sNomAttribut) == 1 )
                                fluxFichier.writeByte(1);
                            else
                                fluxFichier.writeByte(0);
                        }
                    }
                    
                } catch (IOException e) {};
                
            }
            
        }
        
        // Parameter of algorithme APriori :
        if ( (iInfosSelectionnees & PROFIL_INFO_ALGO_APRIORI) != 0 ) {
            
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_ALGO_APRIORI);    
            } catch (IOException e) {}
            
            try {
                // Memorisation de la techique de resolution courante : memorize the current technique
            	fluxFichier.writeInt(m_iTechniqueResolution);                  //TECHNIQUE_APRIORI_QUAL
                // Parameters of that rule :
                fluxFichier.writeFloat(m_parametresRegles.m_fMinSupp);         //min_support
                fluxFichier.writeFloat(m_parametresRegles.m_fMinConf);         //min_confidence!
            } catch (IOException e) {}
        }
        
        
        // Parameter of generic algorithm :
        if ( (iInfosSelectionnees & PROFIL_INFO_ALGO_GENETIQUE) != 0 ) {
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_ALGO_GENETIQUE);   
            } catch (IOException e) {}
            
            try {
                // Memorisation de la techique de resolution courante : memorize the current technique
                fluxFichier.writeInt(m_iTechniqueResolution);           
                
                // Parameters of that rule :
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinSupp); 
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinConf);
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreDisjonctionsGauche);  //# of OR on left side
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreDisjonctionsDroite);  //# of OR on right side
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreMinAttributsQuant);   //min # of numerical attri 
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreMaxAttributsQuant);   //max # of numerical attri in the rule
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinSuppDisjonctions);     //support threshold for additional interval
                
                // Technique Parameter about generic algorithm:
                fluxFichier.writeInt(m_parametresTechAlgoGenetique.m_iTaillePopulation);
                fluxFichier.writeInt(m_parametresTechAlgoGenetique.m_iNombreGenerations);
                fluxFichier.writeFloat(m_parametresTechAlgoGenetique.m_fPourcentageCroisement);
                fluxFichier.writeFloat(m_parametresTechAlgoGenetique.m_fPourcentageMutation);
            } catch (IOException e) {}
        }
        
        // Parameters about simulated annealing algorithm:
        if ( (iInfosSelectionnees & PROFIL_INFO_ALGO_RECUIT) != 0 ) {
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_ALGO_RECUIT); 
            } catch (IOException e) {}
            
            try {
                // Memorisation de la techique de resolution courante : memorize the current technique
                fluxFichier.writeInt(m_iTechniqueResolution);
                
                // Parametres concernant les regles : Parameters of the rules:
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinSupp);
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinConf);
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreDisjonctionsGauche);
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreDisjonctionsDroite);
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreMinAttributsQuant);
                fluxFichier.writeInt(m_parametresReglesQuantitatives.m_iNombreMaxAttributsQuant);
                fluxFichier.writeFloat(m_parametresReglesQuantitatives.m_fMinSuppDisjonctions);
                
                // Parameters about that technique :
                fluxFichier.writeInt(m_parametresTechRecuitSimule.m_iNombreIterations);
                fluxFichier.writeInt(m_parametresTechRecuitSimule.m_iNombreSolutionsParalleles);
            } catch (IOException e) {}
        }
        
        
        // Parametres du processus de chargement de regles pre-calculees : parameters of loading pre-calculated rules
        // 
        if ( (iInfosSelectionnees & PROFIL_INFO_ALGO_CHARGEMENT) != 0 ) {
            
            try {
                fluxFichier.writeByte(FICHIER_PROFIL_DONNEES_ALGO_CHARGEMENT); 
            } catch (IOException e) {}
            
            try {
                // Memorisation de la techique de resolution courante : memorize the current technique
                fluxFichier.writeInt(m_iTechniqueResolution);
                
                // Parameters about technique :
                fluxFichier.writeUTF(m_parametresTechChargement.m_sNomFichier);  //name of the loaded rule file
            } catch (IOException e) {}
        }
        
        
        try {
            fluxFichier.close();
        } catch (IOException e) {
        }
    }
    
    
    // Load profile
    // La fonction renvoie "null" ou un message indiquant un avertissement ou une erreur :
    // The function returns "nullor a message indicating an error message:
    public String ChargerProfil(String sCheminFichier) {  //main menu Load profile will call "Load profile" 
        DataInputStream fluxFichier = null;
        String sChaineUTF = null;
        String sNomBaseDeDonneesOuverte = null;
        int iIdentificateur = 0;
        boolean bFichierValide = false;
        int iNombreChamps = 0;
        int iIndiceChamp = 0;
        String sNomChamp = null;
        int iTypeChamp = 0;
        boolean bPrendreEnCompteChamp = false;
        int iNombreAttributs = 0;
        int iIndiceAttribut = 0;
       // ColonneDonnees colonneDonnees = null;
        boolean bBaseDeDonneesChargee = false;
        String sNomAttribut = null;
        String sNomItem = null;
        int iNombreItems = 0;
        int iIndiceItem = 0;
        String sMessageInformation = null;
        
        
        bBaseDeDonneesChargee = false;
        sMessageInformation = null;
        
        if (m_gestionnaireBD == null)
            return null;
        
        sNomBaseDeDonneesOuverte = m_gestionnaireBD.ObtenirNomBaseDeDonnees();
        if (sNomBaseDeDonneesOuverte == null)
            return null;
        
        try {
            fluxFichier = new DataInputStream( new FileInputStream(sCheminFichier) );
        } catch (IOException e) {
            System.out.println( e.getMessage() );
            return null;
        }
        
        // Lecture de l'identificateur d'un fichier de profil :
        // reading of the identifier of the profile file
        bFichierValide = false;
        try {
            sChaineUTF = fluxFichier.readUTF();    // Identificateur
            if (sChaineUTF != null)
                bFichierValide = sChaineUTF.equals("PROFIL01.00");
        } catch (IOException e) { }
        
        if (!bFichierValide) {
            try {
                fluxFichier.close();
            } catch (IOException e2) { };
            return null;
        }
        
        try {
            sChaineUTF = fluxFichier.readUTF();
            if (!sNomBaseDeDonneesOuverte.equals(sChaineUTF))
                sMessageInformation = "Warning : The profile was generated for a database named \""+sChaineUTF+"\", which does not correspond to the current database (\""+sNomBaseDeDonneesOuverte+"\").\nParameters were loaded to the best but could be inadequate.";
        } catch (IOException e) {
            try { fluxFichier.close(); } catch (IOException e2) { };
            return null;
        }
        
        // Lecture de chaque categories d'informations contenues dans le profil : reading the information in the profile
        do {
            
            try {
                iIdentificateur = (int)fluxFichier.readByte();    // Identificateur
            } catch (IOException e) { iIdentificateur = FICHIER_PROFIL_DONNEES_AUCUNES; }
            
            // Lecture des informations de pre-chargement des infos de la BD : reading the information about pre-loading the DB
            if (iIdentificateur == FICHIER_PROFIL_DONNEES_PRECHARGE) {
                
                // Lecture du nombre de champs memorises : reading the number of field saved
                try {
                    iNombreChamps = fluxFichier.readInt();
                } catch (IOException e) { iNombreChamps = 0; }
                
                
                // Lecture de la configuration, champ par champ : reading the configuration field by field
                for (iIndiceChamp=0;iIndiceChamp<iNombreChamps;iIndiceChamp++) {
                    
                    try {
                        // Lecture du nom du champ : reading the name of the field
                        sNomChamp = fluxFichier.readUTF();
                        
                        // Lecture du type du champ : reading the type of the field
                        if ( fluxFichier.readByte() == FICHIER_PROFIL_CHAMP_TYPE_QUANT )
                            iTypeChamp = DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL;
                        else
                            iTypeChamp = DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM;
                        
                        // Lecture de la valeur (0 ou 1) indiquant si on prend ou non en compte le champ : reading the value of 0 or 1 indicating whether to take into consideration the field
                        bPrendreEnCompteChamp = fluxFichier.readByte() == 1;
                        
                        m_gestionnaireBD.DefinirPriseEnCompteColonne(sNomChamp, iTypeChamp, bPrendreEnCompteChamp);
                    } catch (IOException e) {}
                    
                }
                
                // Chargement du contenu de la base de donnees : loading the content of the DB
                m_gestionnaireBD.ChargerDonneesPrisesEnCompte();
                GenererStructuresDonneesSelonBDPriseEnCompte();
                MettreAJourDonneesInternesFiltre();
                bBaseDeDonneesChargee = true;
            }
            
            
            // Lecture des informations sur la facon de construire une regle a extraire : reading the information on the way to build the rule to extract
            else if (iIdentificateur == FICHIER_PROFIL_DONNEES_PREEXTRACTION) {
                
                // Creation des structures de donnees necessaires a la prise en compte des parametres de positionnement : Building data structure of the positionning parameters
                // si cette operation n'a pas encore ete effectuee prealablement : if not done before
                if (!bBaseDeDonneesChargee) {
                    m_gestionnaireBD.ChargerDonneesPrisesEnCompte();
                    GenererStructuresDonneesSelonBDPriseEnCompte();
                    bBaseDeDonneesChargee = true;
                }
                
                // Lecture du nombre d'attributs dont le positionnement est memorisee dans le fichier profil : read the number of attributes of positionning in the profile file
                try {
                    iNombreAttributs = fluxFichier.readInt();
                } catch (IOException e) {};
                
                for (iIndiceAttribut=0; iIndiceAttribut<iNombreAttributs; iIndiceAttribut++) {
                    
                    try {
                        
                        sNomAttribut = fluxFichier.readUTF();
                        
                        // Lecture d'un attribut qualitatif : reading qualitative attribute
                        if (fluxFichier.readByte() == 1) {
                            iNombreItems = fluxFichier.readInt();
                            for (iIndiceItem=0; iIndiceItem<iNombreItems; iIndiceItem++) {
                                sNomItem = fluxFichier.readUTF();
                                DefinirTypePrisEnCompteItem(sNomAttribut, sNomItem, (int)fluxFichier.readByte());
                                DefinirPresenceObligatoireItem(sNomAttribut, sNomItem, ((int)fluxFichier.readByte() == 1));
                            }
                        }
                        
                        // Lecture d'un attribut quantitatif :  reading quantitative attribute
                        else {
                            DefinirTypePrisEnCompteAttribut(sNomAttribut, (int)fluxFichier.readByte());
                            DefinirPresenceObligatoireAttribut(sNomAttribut, ((int)fluxFichier.readByte() == 1));
                        }
                        
                    } catch (IOException e) {};
                    
                }
                
                MettreAJourDonneesInternesFiltre();
            }
            
            
            // Lecture des parametres concerant la technique d'extraction par Apriori : reading apriori parameters
            else if (iIdentificateur == FICHIER_PROFIL_DONNEES_ALGO_APRIORI) {
                try {
                    // Technique courante lors de l'enregistrement du profil : technique used at the profile recording
                    m_iTechniqueResolution = fluxFichier.readInt();
                    
                    // Parametres concernant les regles : parameters related to rules
                    m_parametresRegles.m_fMinSupp = fluxFichier.readFloat();
                    m_parametresRegles.m_fMinConf = fluxFichier.readFloat();
                } catch (IOException e) {};
            }
            
            
            // Lecture des parametres concerant la technique d'extraction par algorithme genetique : reading parameters w.r.t. the genetic algorithm
            else if (iIdentificateur == FICHIER_PROFIL_DONNEES_ALGO_GENETIQUE) {
                try {
                    // Technique courante lors de l'enregistrement du profil :  technique used at the profile recording
                    m_iTechniqueResolution = fluxFichier.readInt();
                    
                    // Param�tres concernant les r�gles :  parameters related to rules
                    m_parametresReglesQuantitatives.m_fMinSupp = fluxFichier.readFloat();
                    m_parametresReglesQuantitatives.m_fMinConf = fluxFichier.readFloat();
                    m_parametresReglesQuantitatives.m_iNombreDisjonctionsGauche = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreDisjonctionsDroite = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreMinAttributsQuant = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreMaxAttributsQuant = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_fMinSuppDisjonctions = fluxFichier.readFloat();
                    
                    // Param�tres concernant la technique :  parameters related to the technique
                    m_parametresTechAlgoGenetique.m_iTaillePopulation = fluxFichier.readInt();
                    m_parametresTechAlgoGenetique.m_iNombreGenerations = fluxFichier.readInt();
                    m_parametresTechAlgoGenetique.m_fPourcentageCroisement = fluxFichier.readFloat();
                    m_parametresTechAlgoGenetique.m_fPourcentageMutation = fluxFichier.readFloat();
                } catch (IOException e) {};
            }
            
            // Lecture des param�tres concerant la technique d'extraction par recuit simul� :  reading parameters w.r.t. simulated annealing technique
            else if (iIdentificateur == FICHIER_PROFIL_DONNEES_ALGO_RECUIT) {
                try {
                    // Technique courante lors de l'enregistrement du profil : technique used at the profile recording
                    m_iTechniqueResolution = fluxFichier.readInt();
                    
                    // Param�tres concernant les r�gles : parameters related to rules
                    m_parametresReglesQuantitatives.m_fMinSupp = fluxFichier.readFloat();
                    m_parametresReglesQuantitatives.m_fMinConf = fluxFichier.readFloat();
                    m_parametresReglesQuantitatives.m_iNombreDisjonctionsGauche = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreDisjonctionsDroite = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreMinAttributsQuant = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_iNombreMaxAttributsQuant = fluxFichier.readInt();
                    m_parametresReglesQuantitatives.m_fMinSuppDisjonctions = fluxFichier.readFloat();
                    
                    // Param�tres concernant la technique : parameters related to the technique
                    m_parametresTechRecuitSimule.m_iNombreIterations = fluxFichier.readInt();
                    m_parametresTechRecuitSimule.m_iNombreSolutionsParalleles = fluxFichier.readInt();
                } catch (IOException e) {};
            }
            
            // Lecture des param�tres concerant len chargement de r�gles pr�-calcul�es : reading parameters w.r.t. precalculated rules
            else if (iIdentificateur == FICHIER_PROFIL_DONNEES_ALGO_CHARGEMENT) {
                try {
                    // Technique courante lors de l'enregistrement du profil : technique used at the profile recording
                    m_iTechniqueResolution = fluxFichier.readInt();
                    
                    // Param�tres concernant la technique : parameters related to the technique
                    m_parametresTechChargement.m_sNomFichier = fluxFichier.readUTF();
                    PreChargerFichierReglesBinaires(m_parametresTechChargement.m_sNomFichier);
                } catch (IOException e) {};
            }
            
            else
                iIdentificateur = FICHIER_PROFIL_DONNEES_AUCUNES;
        }
        while (iIdentificateur != FICHIER_PROFIL_DONNEES_AUCUNES);
        
        try {
            fluxFichier.close();
        } catch (IOException e) {
        }
        
        return sMessageInformation;
    }
    
    
    //return percentage in string format
    public static String EcrirePourcentage(float fProportion, int iNombreChiffresApresVirgule, boolean bAfficherPourcent) {
        String sChaine = null;
        int iPartieEntiere = 0;
        int iPartieDecimale = 0;
        
        fProportion *= 100.0f;
        iPartieEntiere = (int)fProportion;
        
        fProportion -= (float)iPartieEntiere;
        fProportion *= (float) Math.pow(10.0, (double)iNombreChiffresApresVirgule);
        iPartieDecimale = Math.round(fProportion);
        
        sChaine = String.valueOf(iPartieEntiere) + "." + String.valueOf(iPartieDecimale);
        
        if (bAfficherPourcent)
            sChaine += " %";
        
        return sChaine;
    }
    
    
    public String EcrireSupport(int iNombreOccurrences) {
        String sChaine = null;
        
        sChaine = String.valueOf(iNombreOccurrences);
        sChaine += " (";
        if (m_gestionnaireBD != null)
            sChaine += String.valueOf( EcrirePourcentage(((float)iNombreOccurrences) / ((float)m_gestionnaireBD.ObtenirNombreLignes()), 2, true) );
        else
            sChaine += " ? ";
        sChaine += ")";
        
        return sChaine;
    }
    
    
    //save in rules in qmr file
    public void SauvegarderReglesBinaire(String sCheminFichier, AssociationRule [] tRegles) {
        DataOutputStream fluxFichier = null;
        int iNombreRegles = 0;
        int iIndiceRegle = 0;
        int iIndiceItem = 0;
        int iNombreItems = 0;
        int iEtapeTestRegle = 0;
        int iIndiceDisjonction = 0;
        int iNombreDisjonctions = 0;
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        AssociationRule regle = null;
        
        if ( (m_aprioriCourant == null) || (sCheminFichier == null) )
            return;
        
        try {
            fluxFichier = new DataOutputStream( new FileOutputStream(sCheminFichier) );
        } 
        catch(IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        try {
            fluxFichier.writeUTF( "QUANTMINER_REGLES.00" );    // Identificateur
        } 
        catch (IOException e) {}
        
        
        // Enregistrement des informations sur le contexte d'enregistrement : Saving informnation about the recording context
        try {
            fluxFichier.writeUTF( m_sNomUtilisateur );
            fluxFichier.writeUTF( m_gestionnaireBD.ObtenirNomBaseDeDonnees() );
            fluxFichier.writeUTF( ENV.ObtenirDateCourante() );
            fluxFichier.writeUTF( m_sDescriptionRegles );
            fluxFichier.writeUTF( ObtenirInfosContexte(true) );
        } 
        catch (IOException e) { }
        
        // Enregistrement du nombre de r�gles : recording the number of rules
        if (tRegles != null)
            iNombreRegles = tRegles.length;
        else
            iNombreRegles = 0;
        
        try {
            fluxFichier.writeInt(iNombreRegles);
        } catch (IOException e) {}
        
        
        // Enregistrement du support utilis� pour l'extraction des r�gles : recording the support used for rule extraction
        try {
            fluxFichier.writeFloat(m_aprioriCourant.ObtenirSupportMinimal());
        } catch (IOException e) {}
        
        
        for (iIndiceRegle=0; iIndiceRegle<iNombreRegles; iIndiceRegle++) {
            
            regle = tRegles[iIndiceRegle];
            
            try {
                
                // Informations sur la structure de la r�gle : information about the rule structure
                fluxFichier.writeInt( regle.m_iNombreItemsGauche );
                fluxFichier.writeInt( regle.m_iNombreItemsDroite );
                fluxFichier.writeInt( regle.m_iNombreDisjonctionsGaucheValides );
                fluxFichier.writeInt( regle.m_iNombreDisjonctionsDroiteValides );
                
                // Informations statistiques sur la r�gle : information about rule stats
                fluxFichier.writeFloat( regle.m_fSupport );
                fluxFichier.writeFloat( regle.m_fConfiance );
                fluxFichier.writeInt( regle.m_iOccurrences );
                fluxFichier.writeInt( regle.m_iOccurrencesGauche );
                fluxFichier.writeInt( regle.m_iOccurrencesDroite );
                fluxFichier.writeInt( regle.m_iOccurrences_Gauche_NonDroite );
                fluxFichier.writeInt( regle.m_iOccurrences_NonGauche_Droite );
                fluxFichier.writeInt( regle.m_iOccurrences_NonGauche_NonDroite );
                
                // Contenu de la r�gle : rule content
                for (iEtapeTestRegle=0; iEtapeTestRegle<2; iEtapeTestRegle++) {
                    
                    if (iEtapeTestRegle==0) {
                        iNombreItems = regle.m_iNombreItemsGauche;
                        iNombreDisjonctions = regle.m_iNombreDisjonctionsGaucheValides;
                    } else {
                        iNombreItems = regle.m_iNombreItemsDroite;
                        iNombreDisjonctions = regle.m_iNombreDisjonctionsDroiteValides;
                    }
                    
                    for (iIndiceItem=0; iIndiceItem<iNombreItems; iIndiceItem++) {
                        
                        if (iEtapeTestRegle==0)
                            item = regle.ObtenirItemGauche(iIndiceItem);
                        else
                            item = regle.ObtenirItemDroite(iIndiceItem);
                        
                        // Enregistrement du type de l'item : recording the type of item
                        fluxFichier.writeInt(item.m_iTypeItem);
                        
                        // Enregistrement des donnees de l'item suivant son type : recording the data of the item depending on its type
                        
                        if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                            itemQual = (ItemQualitative)item;
                            fluxFichier.writeUTF( itemQual.m_attributQual.ObtenirNom() );
                            fluxFichier.writeUTF( itemQual.ObtenirIdentifiantTexteItem() );
                        } else if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                            itemQuant = (ItemQuantitative)item;
                            fluxFichier.writeUTF( itemQuant.m_attributQuant.ObtenirNom() );
                            
                            // Ecriture des bornes pour chaque disjonction : writing the bounds of each disjunction
                            for (iIndiceDisjonction=0; iIndiceDisjonction<iNombreDisjonctions; iIndiceDisjonction++) {
                                fluxFichier.writeFloat( itemQuant.m_tBornes[iIndiceDisjonction*2] );
                                fluxFichier.writeFloat( itemQuant.m_tBornes[iIndiceDisjonction*2+1] );
                            }
                        }
                    }
                }
            } catch (IOException e) {}
        }
        
        
        try {
            fluxFichier.close();
        } catch (IOException e) {}
    }
    
    
    //pre-load qmr file
    public void PreChargerFichierReglesBinaires(String sCheminFichier) {
        DataInputStream fluxFichier = null;
        String sChaineUTF = null;
        boolean bFichierValide = false;
        
        // Retour aux valeurs par defaut en cas d'echec : recover default values in case of failure
        m_parametresTechChargement.m_sNomFichier = null;
        m_parametresTechChargement.m_sNomUtilisateurOrigine = "User unknown";
        m_parametresTechChargement.m_sNomBaseOrigine = "Database unkown";
        m_parametresTechChargement.m_sDateOrigine = "Date unknown";
        m_parametresTechChargement.m_sDescriptionRegles = "Missing Description";
        m_parametresTechChargement.m_sDescriptionCompleteContexte = "No information.";
        
        if (sCheminFichier == null)
            return;
        
        try {
            fluxFichier = new DataInputStream( new FileInputStream(sCheminFichier) );
        } catch (IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        // Lecture de l'identificateur d'un fichier binaire de regles :
        bFichierValide = false;
        try {
            sChaineUTF = fluxFichier.readUTF();
            if (sChaineUTF != null)
                bFichierValide = sChaineUTF.equals("QUANTMINER_REGLES.00");
        } catch (IOException e) { }
        
        if (!bFichierValide) {
            try {
                fluxFichier.close();
            } catch (IOException e) { };
            return;
        }
        
        
        // Le fichier est valide : on le m�morise pour le param�trage :
        m_parametresTechChargement.m_sNomFichier = sCheminFichier;
        
        // Lecture des informations d'en-tete contenues dans le fichier de r�gles :
        try {
            m_parametresTechChargement.m_sNomUtilisateurOrigine = fluxFichier.readUTF();
            m_parametresTechChargement.m_sNomBaseOrigine = fluxFichier.readUTF();
            m_parametresTechChargement.m_sDateOrigine = fluxFichier.readUTF();
            m_parametresTechChargement.m_sDescriptionRegles = fluxFichier.readUTF();
            m_parametresTechChargement.m_sDescriptionCompleteContexte = fluxFichier.readUTF();
        } catch (IOException e) { }
        
        
        try {
            fluxFichier.close();
        } catch (IOException e) {}
    }
    
    
    public static String EcrireDescriptionFichierReglesBinairesHTML(String sCheminFichier) {
        DataInputStream fluxFichier = null;
        String sChaineUTF = null;
        boolean bFichierValide = false;
        int iNombreRegles = 0;
        String sNomUtilisateurOrigine = null;
        String sNomBaseOrigine = null;
        String sDateOrigine = null;
        String sDescriptionRegles = null;
        String sDescriptionCompleteContexte = null;
        String sDescriptifTotal = null;
        
        
        if (sCheminFichier == null)
            return "<BR><BR><b>Impossible to open the file!</b>";
        
        // Valeurs par d�faut :
        sNomUtilisateurOrigine = "user unknown";
        sNomBaseOrigine = "database unkown";
        sDateOrigine = "date unknown";
        sDescriptionRegles = "Missing description";
        sDescriptionCompleteContexte = "No information.";
        iNombreRegles = 0;
        sDescriptifTotal = "";
        
        try {
            fluxFichier = new DataInputStream( new FileInputStream(sCheminFichier) );
        } catch (IOException e) {
            return "<BR><BR><b>Impossible to open the file!</b>";
        }
        
        // Lecture de l'identificateur d'un fichier binaire de r�gles :
        bFichierValide = false;
        try {
            sChaineUTF = fluxFichier.readUTF();
            if (sChaineUTF != null)
                bFichierValide = sChaineUTF.equals("QUANTMINER_REGLES.00");
        } catch (IOException e) { }
        
        if (!bFichierValide) {
            try {
                fluxFichier.close();
            } catch (IOException e) { };
            return "<BR><BR><b>This file is not a valid QuantMiner file of rules!</b>";
        }
        
        
        // Lecture des informations d'en-tete contenues dans le fichier de r�gles :
        try {
            sNomUtilisateurOrigine = fluxFichier.readUTF();
            sNomBaseOrigine = fluxFichier.readUTF();
            sDateOrigine = fluxFichier.readUTF();
            sDescriptionRegles = fluxFichier.readUTF();
            sDescriptionCompleteContexte = fluxFichier.readUTF();
            iNombreRegles = fluxFichier.readInt();
        } catch (IOException e) { }
        
        
        // Ecriture du compte-rendu HTML :
        sDescriptifTotal += "<BR><i>Database: </i><b>" + sNomBaseOrigine + "</b>";
        sDescriptifTotal += "<BR><i>Date:</i><b>" + sDateOrigine + "</b>";
        sDescriptifTotal += "<BR><i>Author: </i><b>" + sNomUtilisateurOrigine + "</b>";
        sDescriptifTotal += "<BR><i>Number of rules: </i><b>" + String.valueOf(iNombreRegles) + "</b>";
        
        sDescriptifTotal += "<BR><BR><BR><BR><BR><b><u><i><big>Extracted rules:</big></i></u></b><BR><BR><BR><BR>";
        sDescriptifTotal += FormateHTML( sDescriptionRegles );
        
        sDescriptifTotal += "<BR><BR><BR><BR><BR><b><u><i><big>Extraction context:</big></i></u></b><BR><BR><BR><BR>";
        sDescriptifTotal += sDescriptionCompleteContexte;
        
        try {
            fluxFichier.close();
        } catch (IOException e) {}
        
        return sDescriptifTotal;
    }
    
    //load rules
    public void ChargerReglesBinaire(String sCheminFichier) {
        DataInputStream fluxFichier = null;
        String sChaineUTF = null;
        boolean bFichierValide = false;
        boolean bRegleValide = false;
        int iNombreRegles = 0;
        int iIndiceRegle = 0;
        int iIndiceItem = 0;
        int iNombreItems = 0;
        int iEtapeTestRegle = 0;
        int iIndiceDisjonction = 0;
        int iNombreDisjonctions = 0;
        int iNombreItemsGauche = 0;
        int iNombreItemsDroite = 0;
        int iNombreDisjonctionsGauche = 0;
        int iNombreDisjonctionsDroite = 0;
        int iTypeItem = 0;
        short iIndiceValeurItemQual = 0;
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        AttributQualitative attributQual = null;
        AttributQuantitative attributQuant = null;
        AssociationRule regle = null;
        int iIndiceAjoutItem = 0;
        String sNomAttribut = null;
        String sNomItem = null;
        float fSupportMin = 0.0f;
        float fBorneMin = 0.0f;
        float fBorneMax = 0.0f;
        
        if (sCheminFichier == null)
            return;
        
        if (m_listeRegles == null)
            m_listeRegles = new ArrayList<AssociationRule>();
        else
            m_listeRegles.clear();
        
        try {
            fluxFichier = new DataInputStream( new FileInputStream(sCheminFichier) );
        } catch (IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        // Lexture de l'identificateur d'un fichier binaire de r�gles :
        bFichierValide = false;
        try {
            sChaineUTF = fluxFichier.readUTF();
            if (sChaineUTF != null)
                bFichierValide = sChaineUTF.equals("QUANTMINER_REGLES.00");
        } catch (IOException e) { }
        
        if (!bFichierValide) {
            try {
                fluxFichier.close();
            } catch (IOException e2) { };
            return;
        }
        
        
        // Lecture des informations contenues dans l'en-tete du fichier de r�gles :
        try {
            // Simples informations d�j� charg�es via un pr�-chargement du fichier :
            fluxFichier.readUTF();
            fluxFichier.readUTF();
            fluxFichier.readUTF();
            fluxFichier.readUTF();
            fluxFichier.readUTF();
            
            // Nombre de r�gles � lire dans le fichier :
            iNombreRegles = fluxFichier.readInt();
        } catch (IOException e) { }
        
        
        // Lecture du support utilis� pour l'extraction :
        try {
            fSupportMin = fluxFichier.readFloat();
        } catch (IOException e) { }
        
        
        // Cr�ation d'un Apriori r�pertoriant attributs et items :
        m_aprioriCourant = new AprioriQuantitative(this);
        m_aprioriCourant.SpecifierSupportMinimal(fSupportMin);
        m_aprioriCourant.ExecuterPretraitement(false);
        
        
        // Lecture de la liste de r�gles :
        for (iIndiceRegle=0; iIndiceRegle<iNombreRegles; iIndiceRegle++) {
            
            try {
                
                // Informations sur la structure de la r�gle :
                iNombreItemsGauche = fluxFichier.readInt();
                iNombreItemsDroite = fluxFichier.readInt();
                iNombreDisjonctionsGauche = fluxFichier.readInt();
                iNombreDisjonctionsDroite = fluxFichier.readInt();
                
                regle = new AssociationRule(iNombreItemsGauche, iNombreItemsDroite, iNombreDisjonctionsGauche, iNombreDisjonctionsDroite);
                
                // Informations statistiques sur la r�gle :
                regle.m_fSupport = fluxFichier.readFloat();
                regle.m_fConfiance = fluxFichier.readFloat();
                regle.m_iOccurrences = fluxFichier.readInt();
                regle.m_iOccurrencesGauche = fluxFichier.readInt();
                regle.m_iOccurrencesDroite = fluxFichier.readInt();
                regle.m_iOccurrences_Gauche_NonDroite = fluxFichier.readInt();
                regle.m_iOccurrences_NonGauche_Droite = fluxFichier.readInt();
                regle.m_iOccurrences_NonGauche_NonDroite = fluxFichier.readInt();
                
                // Contenu de la r�gle :
                bRegleValide = true;
                iEtapeTestRegle=0;
                while ( iEtapeTestRegle<2 ) {
                    
                    if (iEtapeTestRegle==0) {
                        iNombreItems = iNombreItemsGauche;
                        iNombreDisjonctions = iNombreDisjonctionsGauche;
                    } else {
                        iNombreItems = iNombreItemsDroite;
                        iNombreDisjonctions = iNombreDisjonctionsDroite;
                    }
                    
                    iIndiceAjoutItem = 0;
                    
                    iIndiceItem=0;
                    while ( iIndiceItem<iNombreItems ) {
                        
                        // Lecture du type de l'item :
                        iTypeItem = fluxFichier.readInt();
                        
                        // Lecture des donn�es de l'item suivant son type :
                        
                        if (iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                            
                            sNomAttribut = fluxFichier.readUTF();
                            sNomItem = fluxFichier.readUTF();
                            
                            itemQual = null;
                            attributQual = m_aprioriCourant.ObtenirAttributQualitatifDepuisNom(sNomAttribut);
                            if (attributQual != null) {
                                iIndiceValeurItemQual = attributQual.ObtenirIndiceCorrespondantValeur(sNomItem);
                                if (iIndiceValeurItemQual >= 0)
                                    itemQual = m_aprioriCourant.ObtenirItem(attributQual, iIndiceValeurItemQual);
                            }
                            
                            item = (Item)itemQual;
                            
                        } else if (iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                            
                            sNomAttribut = fluxFichier.readUTF();
                            
                            itemQuant = null;
                            attributQuant = m_aprioriCourant.ObtenirAttributQuantitatifDepuisNom(sNomAttribut);
                            if (attributQuant != null)
                                itemQuant = new ItemQuantitative(attributQuant, iNombreDisjonctions);
                            
                            for (iIndiceDisjonction=0; iIndiceDisjonction<iNombreDisjonctions; iIndiceDisjonction++) {
                                fBorneMin = fluxFichier.readFloat();
                                fBorneMax = fluxFichier.readFloat();
                                if (itemQuant != null) {
                                    itemQuant.m_tBornes[iIndiceDisjonction*2] = fBorneMin;
                                    itemQuant.m_tBornes[iIndiceDisjonction*2+1] = fBorneMax;
                                }
                            }
                            
                            item = (Item)itemQuant;
                        }
                        
                        if (item == null)
                            bRegleValide = false;
                        else {
                            if (iEtapeTestRegle==0)
                                regle.AssignerItemGauche(item, iIndiceAjoutItem);
                            else
                                regle.AssignerItemDroite(item, iIndiceAjoutItem);
                            iIndiceAjoutItem++;
                        }
                        
                        iIndiceItem++;
                    }
                    iEtapeTestRegle++;
                }
            } catch (IOException e) {
                bRegleValide = false;
            }

            // Ajout de la r�gle si celle-ci est valide :
            if (bRegleValide)
                if (EstRegleValide(regle))
                    m_listeRegles.add(regle);
        }
        
        try {
            fluxFichier.close();
        } catch (IOException e) {}
    }


}
