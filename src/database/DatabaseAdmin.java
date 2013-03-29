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
package src.database;

import java.sql.*;
import java.util.*;
import java.io.*;


public class DatabaseAdmin {

    // Constante choisie dans le domaine couvert par le type float pour indiquer une valeur manquante
    public static final float VALEUR_MANQUANTE_FLOAT = -(Float.MAX_VALUE / 2.0f);
    
    
    
    // Classe d�finissant une colonne de donn�es qui devra �tre charg�e en m�moire :
    class DescripteurColonnePriseEnCompte {
        
        public boolean m_bPrendreEnCompte = false;
        public int m_iTypeColonne = 0;      
        
        DescripteurColonnePriseEnCompte(int iTypeColonne, boolean bPrendreEnCompte) 
        {
            m_iTypeColonne = iTypeColonne;
            m_bPrendreEnCompte = bPrendreEnCompte;
        }
    }
    
   
    public CsvFileParser csvParser = null;
    String [] m_tNomsColonnes = null;
    DescripteurColonnePriseEnCompte [] m_colonnesPrisesEnCompte = null;  // Description de la fa�on de prendre en compte every attribute
    DataColumn [] m_tDonneesColonnes = null;
    public String m_sNomBaseDeDonnees = null; //basic name of the file, without path
    String m_sNomFichier = null;    // Nom du fichier DBF contenant les donn�es � analyser (si on utilise une telle source)
    String m_sNomFlux = null;   // Nom du flux repr�sentant la source ODBC si c'est par ce biais qu'on r�cup�re les donn�es
    int m_iTypeSource = 0;
    
    private int m_iNombreColonnesTotales = 0;
    public int m_iNombreLignes = 0;
    
    public static final int TYPE_VALEURS_COLONNE_ERREUR = 0;
    public static final int TYPE_VALEURS_COLONNE_ITEM = 1;
    public static final int TYPE_VALEURS_COLONNE_REEL = 2;
   
    public static final int SOURCE_FICHIER_DBF = 1;
    public static final int SOURCE_FLUX_ODBC = 2;
    public static final int SOURCE_FICHIER_CSV = 3;
    
    
    
    private void InitialiserGestionnaireBaseDeDonnees() {
        
        m_tNomsColonnes = null;    //name of columns
        m_tDonneesColonnes = null; //selected columns
        m_sNomBaseDeDonnees = null;//name of database, i.e. the name of the data file, without path
        m_sNomFichier = null;
        m_sNomFlux = null;
        m_iTypeSource = 0;
        m_iNombreColonnesTotales = 0;
        m_iNombreLignes = 0;
        m_colonnesPrisesEnCompte = null; //descriptor of column, e.g if selected, column type...
        
    }
    
    
    
    public DatabaseAdmin(String sCheminFichier, String extension) {
       if (extension.equals("dbf"))
    	   GestionnaireBaseDeDonneesDBF(sCheminFichier);
       if (extension.equals("csv"))
	       GestionnaireBaseDeDonneesCSV(sCheminFichier);
    }
    
    public void GestionnaireBaseDeDonneesDBF(String sCheminFichier){
        DBFReader lecteurDBF = null;
        DBFReader.DBFChamp champDBF = null;
        int iIndiceColonne = 0;
        int iDernierePositionSeparateur = 0;
        
        InitialiserGestionnaireBaseDeDonnees();
        
        if (sCheminFichier==null)
            return;
        
        
        iDernierePositionSeparateur = sCheminFichier.lastIndexOf( File.separator );
        
        //Get the base name of the database file without path, e.g. BASE_TEST.dbf
        if (iDernierePositionSeparateur == -1)
            m_sNomBaseDeDonnees = sCheminFichier;
        else
            try {
                m_sNomBaseDeDonnees = sCheminFichier.substring(iDernierePositionSeparateur+1, sCheminFichier.length() );
            }
            catch (IndexOutOfBoundsException e) { m_sNomBaseDeDonnees = null; }
        
        m_sNomFichier = sCheminFichier; //m_sNomFichier is the full name of the database file, including path
        m_sNomFlux = null;
        m_iTypeSource = SOURCE_FICHIER_DBF;
       
    
        try {
    		//This function gets all field information, i.e. number of fields, type, name. It also gets number of records/rows
    		//This function does not read each row's value
            lecteurDBF = new DBFReader( m_sNomFichier ); 
        }
        catch (Exception e) {
            lecteurDBF = null;
            System.out.println(e);
        }
        
        if (lecteurDBF == null) {
            m_sNomBaseDeDonnees = null;
            return;
        }
        
       
        // Obtain number of rows in that file:
        m_iNombreLignes = lecteurDBF.ObtenirNombreLignes();
        
        
        // Obtain number of columns in that file:
        m_iNombreColonnesTotales = lecteurDBF.ObtenirNombreChamps();
        
        if (m_iNombreColonnesTotales > 0)
        {
            m_tNomsColonnes = new String [ m_iNombreColonnesTotales ];
            m_colonnesPrisesEnCompte = new DescripteurColonnePriseEnCompte [ m_iNombreColonnesTotales ]; 
            
            for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
                
                champDBF = lecteurDBF.ObtenirChamp(iIndiceColonne);
                              
                if (champDBF != null)
                    m_tNomsColonnes[iIndiceColonne] = champDBF.ObtenirNom();
                else
                    m_tNomsColonnes[iIndiceColonne] = null;
                //At present, all columns are not selected,and by default, we suppose they are item type
                m_colonnesPrisesEnCompte[iIndiceColonne] = new DescripteurColonnePriseEnCompte(TYPE_VALEURS_COLONNE_ITEM, false);
            }

            Arrays.sort(m_tNomsColonnes);
        }

        
        lecteurDBF.close();
    }

    public void GestionnaireBaseDeDonneesCSV(String sCheminFichier){
    	csvParser = null;
        int iIndiceColonne = 0;
        int iDernierePositionSeparateur = 0;
        
        InitialiserGestionnaireBaseDeDonnees();
        
        if (sCheminFichier==null)
            return;
        
        iDernierePositionSeparateur = sCheminFichier.lastIndexOf( File.separator );
        
        //Get the base name of the database file without path, e.g. BASE_TEST.csv
        if (iDernierePositionSeparateur == -1)
            m_sNomBaseDeDonnees = sCheminFichier;
        else
            try {
                m_sNomBaseDeDonnees = sCheminFichier.substring(iDernierePositionSeparateur+1, sCheminFichier.length() );
            }
            catch (IndexOutOfBoundsException e) { m_sNomBaseDeDonnees = null; }
        
        m_sNomFichier = sCheminFichier; //m_sNomFichier is the full name of the database file, including path
        m_sNomFlux = null;
        m_iTypeSource = SOURCE_FICHIER_CSV;
       
    
        try {
    		//This function gets information, i.e. number of fields, name. It also gets number of records/rows
    		//This function read each row's value to data
        	csvParser = new CsvFileParser( m_sNomFichier ); 
        }
        catch (Exception e) {
        	
        	csvParser = null;
            System.out.println(e);
            
        }
        
        if (csvParser == null) {
            m_sNomBaseDeDonnees = null;
            return;
        }
        
        // Obtain number of rows in that file:
        m_iNombreLignes = csvParser.ObtenirNombreLignes();
        
        
        // Obtain number of columns in that file:
        m_iNombreColonnesTotales = csvParser.ObtenirNombreChamps();
        
        if (m_iNombreColonnesTotales > 0)
        {
            m_tNomsColonnes = new String [ m_iNombreColonnesTotales ];
            m_colonnesPrisesEnCompte = new DescripteurColonnePriseEnCompte [ m_iNombreColonnesTotales ]; 
            
            for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
            m_tNomsColonnes[iIndiceColonne] = csvParser.ObtenirNomChamps()[iIndiceColonne];
           
                //At present, all columns are not selected,and by default, we suppose they are item type
            m_colonnesPrisesEnCompte[iIndiceColonne] = new DescripteurColonnePriseEnCompte(TYPE_VALEURS_COLONNE_ITEM, false);
            }
            Arrays.sort(m_tNomsColonnes);
        }

        
        csvParser.close();
      
}
    
    public boolean EstBaseDeDonneesValide() {
        return ( m_sNomBaseDeDonnees != null );
    }
    
    //Take into consideration all columns
    //In step 1, at the beginning, all columns are selected, and we also get to know column type due to AnalyserTypesChampsBD();
    public void PrendreEnCompteToutesLesColonnes() { 
        int [] tTypes = null;
        int iIndiceColonne = 0;
        
        //Get the type of each column: numerical or categorical
        if (m_iTypeSource == SOURCE_FICHIER_DBF)
        	tTypes = AnalyserTypesChampsDBF();
        else if (m_iTypeSource == SOURCE_FICHIER_CSV)
        	tTypes = AnalyserTypesChampsCSV();
        else return;
        
        if (tTypes == null)
            return;
        
        
        for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
            m_colonnesPrisesEnCompte[iIndiceColonne].m_iTypeColonne = tTypes[iIndiceColonne];
            m_colonnesPrisesEnCompte[iIndiceColonne].m_bPrendreEnCompte = true;
        }
    }
    
    public void ConsiderAllColumns() { 
    	 int iIndiceColonne = 0;
    	 for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
            m_colonnesPrisesEnCompte[iIndiceColonne].m_bPrendreEnCompte = true;
        }
    }
    
    public void NotConsiderAnyColumn() { 
   	 int iIndiceColonne = 0;
   	 for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
           m_colonnesPrisesEnCompte[iIndiceColonne].m_bPrendreEnCompte = false;
       }
   }


    private int[] AnalyserTypesChampsCSV() {
		// TODO Auto-generated method stub
    	int [] tTypesChamps = null;
    	int [] tCorrespondanceIndicesChamps = null;
    	int iIndiceColonne;
    	int iIndiceLigne;
    	int iIndiceChamp;
    	String sValeurItem;
    	String[] tValeursChamps;
    	
    	if (m_iNombreColonnesTotales <= 0)
             return null;
    	
    	if (csvParser == null)
    	     return null;
    	
        // By default consider all fields type is quantitative :
        tTypesChamps = new int [m_iNombreColonnesTotales];
        Arrays.fill(tTypesChamps, TYPE_VALEURS_COLONNE_REEL);
          
        tCorrespondanceIndicesChamps = new int [ m_iNombreColonnesTotales ];
        Arrays.fill(tCorrespondanceIndicesChamps, -1);
        for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
            tCorrespondanceIndicesChamps[iIndiceColonne] = csvParser.ObtenirIndiceChamp(m_tNomsColonnes[iIndiceColonne]);
        }
        
        // Read line by line (200 maximum, number suffisemment significatif) :
        tValeursChamps = csvParser.m_data[0];
        iIndiceLigne = 0;    
        while ( (tValeursChamps != null) && (iIndiceLigne < m_iNombreLignes) && (iIndiceLigne < 200) ) {
            
            for (iIndiceColonne=0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
                
                iIndiceChamp = tCorrespondanceIndicesChamps[iIndiceColonne]; //order in data file
                if (iIndiceChamp >= 0) {

                    sValeurItem = tValeursChamps[ iIndiceChamp ];
                    
                    // Si jusqu'alors le champ �tait consid�r� comme num�rique, on v�rifie que la nouvelle
                    // value ne le contradict pas; sinon il devient quantitatif :
                    if (tTypesChamps[iIndiceColonne] == TYPE_VALEURS_COLONNE_REEL) {
                        if (sValeurItem != null)
                            if (sValeurItem.trim() != "") { //Returns a copy of the string, with leading and trailing whitespace omitted.
                                try {
                                    Float.parseFloat(sValeurItem); //Throws: if the string does not contain a parsable float
                                }
                                catch (NumberFormatException e) { //If float parse fail, it means the type is categorical
                                    tTypesChamps[iIndiceColonne] = TYPE_VALEURS_COLONNE_ITEM;
                                }
                            }
                    }
                    
                }
              
            }

            iIndiceLigne++;
            if (iIndiceLigne == m_iNombreLignes - 1) break;
            tValeursChamps = csvParser.m_data[iIndiceLigne];
        }

        // Fermeture du fichier :
       
        return tTypesChamps;
        
	}



	public String ObtenirNomBaseDeDonnees() {
        return m_sNomBaseDeDonnees;
    }
    
    
    public int ObtenirNombreLignes() {
        return m_iNombreLignes;
    }

    
    
    private int ObtenirIndiceColonneDepuisNom(String sNomColonne) { //obtain the index of a column by column name
        boolean bTrouveColonne = false;
        int iIndiceColonne = 0;
        
        if (sNomColonne == null)
            return -1;
        
        iIndiceColonne = 0;
        while ( (!bTrouveColonne) && (iIndiceColonne<m_tNomsColonnes.length) )  {
            
            if (sNomColonne.equals( m_tNomsColonnes[iIndiceColonne] ) )
                bTrouveColonne = true;
            else
                iIndiceColonne++;
            
        }       
        
        if (bTrouveColonne)
            return iIndiceColonne;
        else
            return -1;
    }
    
    
    
    // Appeler cette fonction pour d�clarer chaque colonne de la BD initiale qu'on souhaite prendre
    // en compte lors du chargement des donn�es en m�moire :
    public void DefinirPriseEnCompteColonne(String sNomColonne, int iTypeValeurs, boolean bPrendreEnCompte) {
        int iIndiceColonnePriseEnCompte = 0;
        
        iIndiceColonnePriseEnCompte = ObtenirIndiceColonneDepuisNom(sNomColonne);
        if (iIndiceColonnePriseEnCompte<0)
            return;
        
        m_colonnesPrisesEnCompte[iIndiceColonnePriseEnCompte].m_iTypeColonne = iTypeValeurs;
        m_colonnesPrisesEnCompte[iIndiceColonnePriseEnCompte].m_bPrendreEnCompte = bPrendreEnCompte;

    }

    
    public int ObtenirTypeColonne(String sNomColonne) {
        int iIndiceColonnePriseEnCompte = 0;
        
        iIndiceColonnePriseEnCompte = ObtenirIndiceColonneDepuisNom(sNomColonne);
        if (iIndiceColonnePriseEnCompte<0)
            return TYPE_VALEURS_COLONNE_ERREUR;
        else
            return m_colonnesPrisesEnCompte[iIndiceColonnePriseEnCompte].m_iTypeColonne;
    }
    
    
    public boolean EstPriseEnCompteColonne(String sNomColonne) { //consider this column or not  Prise means catch/pick
        int iIndiceColonnePriseEnCompte = 0;
        
        iIndiceColonnePriseEnCompte = ObtenirIndiceColonneDepuisNom(sNomColonne);
        if (iIndiceColonnePriseEnCompte < 0)
            return false;
        else
            return m_colonnesPrisesEnCompte[iIndiceColonnePriseEnCompte].m_bPrendreEnCompte;
    }
    
    
    //Get the initial number of columns, i.e. the number of columns in data file
    public int ObtenirNombreColonnesBDInitiale() {
        return m_iNombreColonnesTotales;
    }
    
    public int ObtenirNombreColonnesPrisesEnCompte() { 
        int iIndiceColonne = 0;
        int iNombreColonnesPrisesEnCompte = 0;
        
        iNombreColonnesPrisesEnCompte = 0;
        for (iIndiceColonne = 0; iIndiceColonne < m_tNomsColonnes.length; iIndiceColonne++)
            if (m_colonnesPrisesEnCompte[iIndiceColonne].m_bPrendreEnCompte)
                iNombreColonnesPrisesEnCompte++;
        
         return iNombreColonnesPrisesEnCompte;
    }
   
    
    //Get the column information about the selected column in step 1, i.e the data load step
    //m_tDonneesColonnes contains all the selected columns
    public DataColumn ObtenirColonneBDPriseEnCompte(int iIndiceColonne) {
        if (m_tDonneesColonnes==null)
            return null;
       
        if (iIndiceColonne < m_tDonneesColonnes.length)
            return m_tDonneesColonnes[iIndiceColonne];
        else
            return null;
    }

    
    
    // Renvoie le nom de la 'iIndiceColonne'-i�me colonne dans la BD originale :
    public String ObtenirNomColonneBDInitiale(int iIndiceColonne) {
        if (iIndiceColonne < m_iNombreColonnesTotales)
            return m_tNomsColonnes[iIndiceColonne];
        else
            return null;
    }
       
    
        
    // Renvoie le nom de la 'iIndiceColonne'-i�me colonne prise en compte :
    public String ObtenirNomColonneBDPriseEnCompte(int iIndiceColonne) {
        DataColumn colonnePriseEnCompte = null;
        
        colonnePriseEnCompte = ObtenirColonneBDPriseEnCompte(iIndiceColonne);
        
        if (colonnePriseEnCompte != null)
            return colonnePriseEnCompte.m_sNomColonne;
        else
            return null;
    }
    
    
    
    //Anaylize the type of each column, numerical or categorical
    // Analyse et extrait les types pr�sum�s des champs contenus dans la BD (renvoie un tableau de types) :
    public int [] AnalyserTypesChampsDBF() { //return type either TYPE_VALEURS_COLONNE_REEL or TYPE_VALEURS_COLONNE_ITEM i.e. quantative or categorical
        DBFReader lecteurDBF = null;
        DBFReader.DBFChamp champDBF = null;
        int iIndiceLigne = 0;
        int iIndiceColonne = 0;
        String sValeurItem = null;
        String [] tValeursChamps = null;
        int [] tCorrespondanceIndicesChamps = null; // Table de correspondance entre le num�ro d'une colonne et l'indice du champ qu'elle repr�sente dans le fichier
        int iIndiceChamp = 0;
        int [] tTypesChamps = null;
        
        
        if (m_iNombreColonnesTotales <= 0)
            return null;
        
        // By default consider all fields type is quantitative :
        tTypesChamps = new int [m_iNombreColonnesTotales];
        Arrays.fill(tTypesChamps, TYPE_VALEURS_COLONNE_REEL); //assigned the specified int value to each element in the array
        
        
        // Open du fichier DBF et lecture de l'en-t�te :   
        try {
            lecteurDBF = new DBFReader( m_sNomFichier );
        }
        catch (Exception e) {
            lecteurDBF = null;
            System.out.println(e);
        }
        
        if (lecteurDBF == null)
            return null;

        // make a table of correspondance entre noms et indices de champs :
        //[iIndiceColonne] --> order in data file
        tCorrespondanceIndicesChamps = new int [ m_iNombreColonnesTotales ];
        Arrays.fill(tCorrespondanceIndicesChamps, -1);
        for (iIndiceColonne=0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
            tCorrespondanceIndicesChamps[iIndiceColonne] = lecteurDBF.ObtenirIndiceChamp(m_tNomsColonnes[iIndiceColonne]);
        }
        
        
        // Read line by line (200 maximum, number suffisemment significatif) :
        tValeursChamps = lecteurDBF.LireEnregistrementSuivant();
        iIndiceLigne = 0;    
        while ( (tValeursChamps != null) && (iIndiceLigne < m_iNombreLignes) && (iIndiceLigne < 200) ) {
            
            for (iIndiceColonne=0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
                
                iIndiceChamp = tCorrespondanceIndicesChamps[iIndiceColonne]; //order in data file
                if (iIndiceChamp >= 0) {

                    sValeurItem = tValeursChamps[ iIndiceChamp ];
                    
                    // Si jusqu'alors le champ �tait consid�r� comme num�rique, on v�rifie que la nouvelle
                    // value ne le contradict pas; sinon il devient quantitatif :
                    if (tTypesChamps[iIndiceColonne] == TYPE_VALEURS_COLONNE_REEL) {
                        if (sValeurItem != null)
                            if (sValeurItem.trim() != "") { //Returns a copy of the string, with leading and trailing whitespace omitted.
                                try {
                                    Float.parseFloat(sValeurItem); //Throws: if the string does not contain a parsable float
                                }
                                catch (NumberFormatException e) { //If float parse fail, it means the type is categorical
                                    tTypesChamps[iIndiceColonne] = TYPE_VALEURS_COLONNE_ITEM;
                                }
                            }
                    }
                    
                }
              
            }

            iIndiceLigne++;
            if (iIndiceLigne == m_iNombreLignes - 1) break;
            tValeursChamps = lecteurDBF.LireEnregistrementSuivant();
        }

        // Fermeture du fichier :
        lecteurDBF.close();
        
        return tTypesChamps;
    }
        
    
    // Place en m�moire le contenu de toutes les colonnes d�clar�es avec des appels successifs
    // de la fonction 'PrendreEnCompteColonne' :
    public void ChargerDonneesPrisesEnCompte() {
        int iNombreColonnesPrisesEnCompte = 0;
        
        iNombreColonnesPrisesEnCompte = ObtenirNombreColonnesPrisesEnCompte();
        
        if (iNombreColonnesPrisesEnCompte > 0) {
            m_tDonneesColonnes = new DataColumn [ iNombreColonnesPrisesEnCompte ];
            Arrays.fill(m_tDonneesColonnes, null);
        }
        else
            m_tDonneesColonnes = null;
        
        if ( (iNombreColonnesPrisesEnCompte <= 0) || (m_iNombreLignes <= 0) )
            return;

        // La fonction d�l�gue le travail suivant le type de donn�es sources :
        if (m_iTypeSource == SOURCE_FICHIER_DBF)
        	ChargerDonneesPrisesEnCompteDBF();
        else if (m_iTypeSource == SOURCE_FICHIER_CSV)
        	ChargerDonneesPrisesEnCompteCSV();
        else return;
        
    }
    
    
    private void ChargerDonneesPrisesEnCompteCSV() {
		// TODO Auto-generated method stub
    	if (csvParser == null)
   	     return;
    	
          int iIndiceLigne = 0;
          int iIndiceColonne = 0;
          DescripteurColonnePriseEnCompte colonnePriseEnCompte = null;
          DataColumn colonneCourante = null;
          String sValeurItem = null;
          String [] tValeursChamps = null;
          int [] tCorrespondanceIndicesChamps = null; // Table de correspondance entre le num�ro d'une colonne et l'indice du champ qu'elle repr�sente dans le fichier
          int iIndiceChamp = 0;
          int iNombreColonnesPrisesEnCompte = 0;
          int iIndiceColonnePriseEnCompte = 0;
          
          if (m_tDonneesColonnes == null)
              return;
          
          iNombreColonnesPrisesEnCompte = m_tDonneesColonnes.length;
      
          
          tCorrespondanceIndicesChamps = new int [ iNombreColonnesPrisesEnCompte ];
          Arrays.fill(tCorrespondanceIndicesChamps, -1);
          
          iIndiceColonnePriseEnCompte = 0;
          for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
                      
              colonnePriseEnCompte = m_colonnesPrisesEnCompte[ iIndiceColonne ];
              
              if (colonnePriseEnCompte.m_bPrendreEnCompte) {
            	  tCorrespondanceIndicesChamps[iIndiceColonnePriseEnCompte] = csvParser.ObtenirIndiceChamp(m_tNomsColonnes[iIndiceColonne]);
                  
            	  m_tDonneesColonnes[iIndiceColonnePriseEnCompte] = new DataColumn(
                      m_tNomsColonnes[iIndiceColonne],
                      colonnePriseEnCompte.m_iTypeColonne,
                      m_iNombreLignes,
                      tCorrespondanceIndicesChamps[iIndiceColonnePriseEnCompte]);
                  
                  iIndiceColonnePriseEnCompte++;
              }
          }
         
          
          
          // Lecture ligne par ligne :
          iIndiceLigne = 0;
          tValeursChamps = csvParser.m_data[iIndiceLigne];
              
          while ( (tValeursChamps != null) && (iIndiceLigne < m_iNombreLignes) ) {
              
              for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
                  
                  iIndiceChamp = tCorrespondanceIndicesChamps[iIndiceColonne];
                  if (iIndiceChamp >= 0) {
                      
                      colonneCourante = m_tDonneesColonnes[iIndiceColonne];
                      sValeurItem = tValeursChamps[ iIndiceChamp ];
                      
                      switch ( colonneCourante.m_iTypeValeurs ) {
                          case TYPE_VALEURS_COLONNE_ITEM :
                              colonneCourante.m_tIDQualitatif[iIndiceLigne] = colonneCourante.RepertorierValeur(sValeurItem);
                          break;
                          case TYPE_VALEURS_COLONNE_REEL :
                              float fValeurReelle = 0.0f;
                              try {
                                  fValeurReelle = Float.parseFloat(sValeurItem);
                              }
                              catch( NumberFormatException e) {
                                  fValeurReelle = VALEUR_MANQUANTE_FLOAT; // valeur erronn�e ou manquante
                              }
                              colonneCourante.AssignerValeurReelle(iIndiceLigne, fValeurReelle);
                          break;
                      }
                  
                  }
                  
              }

              iIndiceLigne++;
              if (iIndiceLigne == m_iNombreLignes - 1) break;
              tValeursChamps = csvParser.m_data[iIndiceLigne];
          }

          // Fermeture du fichier :
          
          
          // Post traitement sur les colonnes de type num�rique (table tri�e des indices des valeurs) :
          for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
              colonneCourante = m_tDonneesColonnes[iIndiceColonne];
              if (colonneCourante.m_iTypeValeurs == TYPE_VALEURS_COLONNE_REEL)
                  colonneCourante.ConstruireTableauValeursQuantitativesTriees();
          }
	}



    // Version de 'ChargerDonneesPrisesEnCompte' op�rant sur les fichiers DBF :
    private void ChargerDonneesPrisesEnCompteDBF() {
        
        DBFReader lecteurDBF = null;
        DBFReader.DBFChamp champDBF = null;
        int iIndiceLigne = 0;
        int iIndiceColonne = 0;
        DescripteurColonnePriseEnCompte colonnePriseEnCompte = null;
        DataColumn colonneCourante = null;
        String sValeurItem = null;
        String [] tValeursChamps = null;
        int [] tCorrespondanceIndicesChamps = null; // Table de correspondance entre le num�ro d'une colonne et l'indice du champ qu'elle repr�sente dans le fichier
        int iIndiceChamp = 0;
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonnePriseEnCompte = 0;
        
        if (m_tDonneesColonnes == null)
            return;
        
        iNombreColonnesPrisesEnCompte = m_tDonneesColonnes.length;
    
        
        try {
            lecteurDBF = new DBFReader( m_sNomFichier );
        }
        catch (Exception e) {
            lecteurDBF = null;
            System.out.println(e);
        }
        
        if (lecteurDBF == null)
            return;

        
        tCorrespondanceIndicesChamps = new int [ iNombreColonnesPrisesEnCompte ];
        Arrays.fill(tCorrespondanceIndicesChamps, -1);
        
        iIndiceColonnePriseEnCompte = 0;
        for (iIndiceColonne = 0; iIndiceColonne < m_iNombreColonnesTotales; iIndiceColonne++) {
                    
            colonnePriseEnCompte = m_colonnesPrisesEnCompte[ iIndiceColonne ];
            
            if (colonnePriseEnCompte.m_bPrendreEnCompte) {
            	
            	 tCorrespondanceIndicesChamps[iIndiceColonnePriseEnCompte] = lecteurDBF.ObtenirIndiceChamp(m_tNomsColonnes[iIndiceColonne]);
                 m_tDonneesColonnes[iIndiceColonnePriseEnCompte] = new DataColumn(
                    m_tNomsColonnes[iIndiceColonne],
                    colonnePriseEnCompte.m_iTypeColonne,
                    m_iNombreLignes,
                    tCorrespondanceIndicesChamps[iIndiceColonnePriseEnCompte]);
                
                iIndiceColonnePriseEnCompte++;
            }
        }
       
        
        
        // Lecture ligne par ligne :
        tValeursChamps = lecteurDBF.LireEnregistrementSuivant();
        iIndiceLigne = 0;    
        while ( (tValeursChamps != null) && (iIndiceLigne < m_iNombreLignes) ) {
            
            for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
                
                iIndiceChamp = tCorrespondanceIndicesChamps[iIndiceColonne];
                if (iIndiceChamp >= 0) {
                    
                    colonneCourante = m_tDonneesColonnes[iIndiceColonne];
                    sValeurItem = tValeursChamps[ iIndiceChamp ];
                    
                    switch ( colonneCourante.m_iTypeValeurs ) {
                        case TYPE_VALEURS_COLONNE_ITEM :
                            colonneCourante.m_tIDQualitatif[iIndiceLigne] = colonneCourante.RepertorierValeur(sValeurItem);
                        break;
                        case TYPE_VALEURS_COLONNE_REEL :
                            float fValeurReelle = 0.0f;
                            try {
                                fValeurReelle = Float.parseFloat(sValeurItem);
                            }
                            catch( NumberFormatException e) {
                                fValeurReelle = VALEUR_MANQUANTE_FLOAT; // valeur erronn�e ou manquante
                            }
                            colonneCourante.AssignerValeurReelle(iIndiceLigne, fValeurReelle);
                        break;
                    }
                
                }
                
            }

            iIndiceLigne++;
            tValeursChamps = lecteurDBF.LireEnregistrementSuivant();
        }

        // Fermeture du fichier :
        lecteurDBF.close();
        
        
        // Post traitement sur les colonnes de type num�rique (table tri�e des indices des valeurs) :
        for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
            colonneCourante = m_tDonneesColonnes[iIndiceColonne];
            if (colonneCourante.m_iTypeValeurs == TYPE_VALEURS_COLONNE_REEL)
                colonneCourante.ConstruireTableauValeursQuantitativesTriees();
        }
    }
    
    
    
    public String EcrireDescriptifColonnesQuantitatives() {
        int iIndiceColonne = 0;
        DescripteurColonnePriseEnCompte colonnePriseEnCompte = null;
        String sTexteDescriptif = null;

        if (m_tDonneesColonnes == null)
            return "No column selected.";
        
        sTexteDescriptif = new String("");
        
        sTexteDescriptif += "ATTRIBUTS QUANTITATIFS :\n\n";
        for (iIndiceColonne=0;iIndiceColonne<m_tDonneesColonnes.length;iIndiceColonne++) {
            if (m_tDonneesColonnes[iIndiceColonne].m_iTypeValeurs == TYPE_VALEURS_COLONNE_REEL) {
                sTexteDescriptif += m_tDonneesColonnes[iIndiceColonne].m_sNomColonne;
                sTexteDescriptif += ", domaine [ ";
                sTexteDescriptif += String.valueOf( m_tDonneesColonnes[iIndiceColonne].ObtenirBorneMin() );
                sTexteDescriptif += ", ";
                sTexteDescriptif += String.valueOf( m_tDonneesColonnes[iIndiceColonne].ObtenirBorneMax() );
                sTexteDescriptif += " ]\n";
            }
        }            
    
        return sTexteDescriptif;
    }

    
    
    // Lib�re de la m�moire tous les champs qui ont �t� charg�s : 
    public void LibererDonneesEnMemoire() { //release data column
        m_tDonneesColonnes = null;
    }
    
}
