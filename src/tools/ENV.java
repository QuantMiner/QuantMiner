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
//---------------------------------------------------------------------------------------------------
//
// Cette classe un peu particuli�re a pour but de stocker diverse informations sur
// l'environnement d'ex�cution, et ainsi de les mettre � disposition des autres classes du programme.
// Tous les membres doivent �tre d�clar�s 'static'.
//
//---------------------------------------------------------------------------------------------------

package src.tools;

import java.io.*;
import java.util.*;



public class ENV {

    public static final int LOOK_INTERFACE_JAVA = 0;
    public static final int LOOK_INTERFACE_OS = 1;
    
    public static String REPERTOIRE_TRAVAIL = "./";
    public static String REPERTOIRE_AIDE = "./HELP/";
    public static String REPERTOIRE_RESSOURCES = "./RESOURCES/";
    public static String REPERTOIRE_PROFILS = "./PROFILES/";
    public static String REPERTOIRE_RESULTATS = "./RESULTS/";
    public static String REPERTOIRE_REGLES_QMR = "./RULES_QMR/";
    public static String REPERTOIRE_TABLES_DBF = "./TABLES/";
    
    public static String VERSION_QUANTMINER = "1.0";  
    public static String ANNEE_COPYRIGHT = "2003";
    public static String NOM_UTILISATEUR = "User Unknown";
    public static int LOOK_INTERFACE = LOOK_INTERFACE_JAVA;
    public static boolean AVERTIR_FIN_CALCUL = true;
    public static String CHEMIN_FICHIER_SON_FIN_CALCUL = REPERTOIRE_RESSOURCES + "fin_calcul.wav";
    public static String CHEMIN_DERNIERE_BASE_OUVERTE = REPERTOIRE_TABLES_DBF;
    
    
    public static void Initialiser() {
        
        File repertoireCourant = null;
        
        repertoireCourant = new File("");
        try {
            REPERTOIRE_TRAVAIL = new String( repertoireCourant.getAbsolutePath() );
            while (REPERTOIRE_TRAVAIL.endsWith(".")) {
                REPERTOIRE_TRAVAIL = REPERTOIRE_TRAVAIL.substring(0, (REPERTOIRE_TRAVAIL.length()-1));
            }            
        }
        catch (Exception e) {
            REPERTOIRE_TRAVAIL = "./";
        }

		System.out.println(REPERTOIRE_TRAVAIL);
        REPERTOIRE_RESSOURCES = REPERTOIRE_TRAVAIL + File.separator + "RESOURCES" + File.separator;
        REPERTOIRE_PROFILS =    REPERTOIRE_TRAVAIL  + File.separator + "PROFILES" + File.separator;
        REPERTOIRE_RESULTATS =  REPERTOIRE_TRAVAIL + File.separator +  "RESULTS" + File.separator;
        REPERTOIRE_REGLES_QMR = REPERTOIRE_TRAVAIL  + File.separator +  "RULES_QMR" + File.separator;
        REPERTOIRE_TABLES_DBF = REPERTOIRE_TRAVAIL  + File.separator +  "TABLES" + File.separator;
        REPERTOIRE_AIDE =       REPERTOIRE_TRAVAIL  + File.separator +  "HELP" + File.separator;

        CHEMIN_FICHIER_SON_FIN_CALCUL = REPERTOIRE_RESSOURCES + "fin_calcul.wav";
        CHEMIN_DERNIERE_BASE_OUVERTE = REPERTOIRE_TABLES_DBF;
        ChargerFichierParametrage();
    }

    
    
    public static void EnregistrerFichierParametrage() { //register file parameters
        DataOutputStream fluxFichier = null;    
        try {
            fluxFichier = new DataOutputStream( new FileOutputStream(REPERTOIRE_TRAVAIL + File.separator + "quantminer.ini"));
        }
	    catch (IOException e) {
            System.out.println( e.getMessage() );
            return;
        }        
        
        try {
            fluxFichier.writeUTF(NOM_UTILISATEUR);
            fluxFichier.writeInt(LOOK_INTERFACE);
            fluxFichier.writeBoolean(AVERTIR_FIN_CALCUL);
            fluxFichier.writeUTF(CHEMIN_FICHIER_SON_FIN_CALCUL);
            fluxFichier.writeUTF(CHEMIN_DERNIERE_BASE_OUVERTE);
        }
	    catch (IOException e) {
	    	 System.out.println( e.getMessage() );
	    }        
        
        try {
            fluxFichier.close();
        }
	    catch (IOException e) {
	    	 System.out.println( e.getMessage() );
	    }
    }
    
    
    
    public static void ChargerFichierParametrage() {
        DataInputStream fluxFichier = null;
        File fichierParametrage = null;
        String sCheminFichier = null;
        
        sCheminFichier = REPERTOIRE_TRAVAIL + File.separator + "quantminer.ini";
             
        //If the parameter file doens't exist, create a parameter file with value by default:
        fichierParametrage = new File(sCheminFichier);
        if (!fichierParametrage.exists())
            EnregistrerFichierParametrage();
        
        try {
            fluxFichier = new DataInputStream( new FileInputStream(sCheminFichier) );
        }
	    catch (IOException e) {
            System.out.println( e.getMessage() );
            return;
        }
        
        try {
            NOM_UTILISATEUR = fluxFichier.readUTF();
            LOOK_INTERFACE = fluxFichier.readInt();
            AVERTIR_FIN_CALCUL = fluxFichier.readBoolean();
            CHEMIN_FICHIER_SON_FIN_CALCUL = fluxFichier.readUTF();
            CHEMIN_DERNIERE_BASE_OUVERTE = fluxFichier.readUTF();
        }
	    catch (IOException e) {}           
        
        try {
            fluxFichier.close();
        }
	    catch (IOException e) {}  
    }
    
    
    //obtain date -- used in save a file 
    public static String ObtenirDateCourante() {
        GregorianCalendar dateCourante = null;   
        String sInfoDate = "";
        
        dateCourante = new GregorianCalendar();
        
        switch( dateCourante.get( Calendar.MONTH ) ) {
            case Calendar.JANUARY :     sInfoDate += "January"; break;
            case Calendar.FEBRUARY :    sInfoDate += "February"; break;
            case Calendar.MARCH :       sInfoDate += "March"; break;
            case Calendar.APRIL :       sInfoDate += "April"; break;
            case Calendar.MAY :         sInfoDate += "May"; break;
            case Calendar.JUNE :        sInfoDate += "June"; break;
            case Calendar.JULY :        sInfoDate += "July"; break;
            case Calendar.AUGUST :      sInfoDate += "August"; break;
            case Calendar.SEPTEMBER :   sInfoDate += "September"; break;
            case Calendar.OCTOBER :     sInfoDate += "October"; break;
            case Calendar.NOVEMBER :    sInfoDate += "November"; break;
            case Calendar.DECEMBER :    sInfoDate += "December"; break;
        }
        
        sInfoDate += " " + String.valueOf( dateCourante.get( Calendar.DAY_OF_MONTH ) );
        sInfoDate += ", " + String.valueOf( dateCourante.get( Calendar.YEAR ) ) + " ";
        sInfoDate += " " + String.valueOf( dateCourante.get( Calendar.HOUR_OF_DAY ) ) + ":";
        sInfoDate += String.valueOf( dateCourante.get( Calendar.MINUTE ) );

        return sInfoDate;
    }
    
}
