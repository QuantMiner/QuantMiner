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

import java.util.*;

import src.tools.dataStructures.*;


public class DataColumn {
    
        
    public String m_sNomColonne = null; //name of the column
    public int m_iIndiceChamp = 0;      // Index column in that source file
    public int m_iTypeValeurs;          //type of values
    public int m_iNombreLignes = 0;     //number of lines
    
    // Member about unique les column de value categorical :
    public short [] m_tIDQualitatif = null; // table d'entiers using 16 bits identification every item (categorical)
    public WordList m_listeValeurs = null;
    
    // Member about unique les column that value quantitative :
    public float [] m_tValeurReelle = null;
    public float [] m_tValeursReellesTriees = null; // Table regroupant les indices des valeurs, tri�s par ordre de valeurs correspondantes croissantes
    public int m_iNombreValeursReellesCorrectes = 0;  // Number of quantitative value qui ne sont ni manquantes, ni erronn�es

    public int [] m_tValeursUniques = null; // Table effectuant une correspondance entre les indices de toutes les valeurs et un indice unique repr�sentant chaque valeur unique
    public int [] m_tCumulSupportCroissant = null; // Table r�pertoriant les supports cumul� au niveau de chaque indice de valeur (unique). Pour trouver le support d'un intervalle, faire une soustraction des supports cumul�s au niveau des bornes
    public int m_iNombreValeursUniques = 0;
    
    public float m_fValeurMin = 0.0f;
    public float m_fValeurMax = 0.0f;
    private boolean m_bBornesReellesDefinies = false;
   
       
    
    DataColumn(String sNomColonne, int iTypeValeurs, int iNombreLignes, int index) {

        m_sNomColonne = sNomColonne;
        m_iTypeValeurs = iTypeValeurs;
        m_iNombreLignes = iNombreLignes;
        m_bBornesReellesDefinies = false;
        m_iIndiceChamp = index;
        m_fValeurMin = 0.0f;
        m_fValeurMax = 0.0f;
        m_iNombreValeursReellesCorrectes = 0;
        m_tValeursReellesTriees = null;
          
        if (iNombreLignes > 0) {
            
            switch (m_iTypeValeurs) {
                case DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM :
                    m_listeValeurs = new WordList();
                    m_tIDQualitatif = new short [ iNombreLignes ];
                    break;
                    
                case DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL :
                    m_tValeurReelle = new float [ iNombreLignes ];
                    break;
            }
        }
        else {
            m_listeValeurs = null;
            m_tIDQualitatif = null;
            m_tValeurReelle = null;
        }
    }
   
    
    public short RepertorierValeur(String sValeur) {
        return (short)m_listeValeurs.InsererMot(sValeur);
    }
    
    
    /**
     * Function allowing to make the correspondence between the textual representation of a value and its numerical identifier
     * @param sValeur
     * @return short
     */
    public short ObtenirNumeroCorrespondance(String sValeur) {
        WordList.InfosMot infosMot = null;
        
        infosMot = m_listeValeurs.ChercherInfosMot(sValeur);
        if (infosMot!=null)
            return (short)infosMot.m_iIdentifiantUnique;
        else
            return -1;
    }

    
    /**
     *  Unique for cateogorical (qualitative) values, it returns the number of occurrences of a given item in all the tuples
     * @param sValeur The item
     * @return number of occurrences
     */
    public int ObtenirNombreOccurrencesItem(String sValeur) {
        WordList.InfosMot infosMot = null;
        
        infosMot = m_listeValeurs.ChercherInfosMot(sValeur);
        if (infosMot == null)
            return 0;
        else
            return infosMot.m_iOccurrences;
    }    
    
    
    // Uniquement pour les valeurs quantitatives, renvoie la plus petite valeur constat�e dans les donn�es :
    public float ObtenirBorneMin() {
        return m_fValeurMin;
    }
    

    // Uniquement pour les valeurs quantitatives, renvoie la plus grande valeur constat�e dans les donn�es :
    public float ObtenirBorneMax() {
        return m_fValeurMax;
    }
     
    
    /**
     * keeps in memory a real value and updates the min and max if necessary
     * @param iIndiceLigne
     * @param fValeur
     */
    void AssignerValeurReelle(int iIndiceLigne, float fValeur) {
        
        if (fValeur != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT) {
            
            if (m_bBornesReellesDefinies) {
                if (fValeur < m_fValeurMin) m_fValeurMin = fValeur;
                if (fValeur > m_fValeurMax) m_fValeurMax = fValeur;
            }
            else {
                m_fValeurMin = m_fValeurMax = fValeur;
                m_bBornesReellesDefinies = true;
            }
            
        }
        
        m_tValeurReelle[iIndiceLigne] = fValeur;
    }
    
    
    
    public int ObtenirNombreValeursDifferentes() {
        return m_listeValeurs.ObtenirNombreMots();
    }
    
    public String [] ConstituerTableauValeurs() {
        int iNombreValeurs = 0;
        int iIdentifiantValeur = 0;
        String [] tTableauValeurs;
        
        if (m_iTypeValeurs != DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM)
            return null;
                
        iNombreValeurs = m_listeValeurs.ObtenirNombreMots();
        if (iNombreValeurs <= 0) return null;
        
        tTableauValeurs = new String [iNombreValeurs];
        Arrays.fill(tTableauValeurs, null); //Assigns the specified Object reference to each element of the specified array of Objects
        
        m_listeValeurs.InicierParcours();
        while (m_listeValeurs.AvancerParcours()) {
            iIdentifiantValeur = (m_listeValeurs.ObtenirInfosMotParcouru()).m_iIdentifiantUnique;
            if ( (iIdentifiantValeur >= 0) && (iIdentifiantValeur < iNombreValeurs) )
                tTableauValeurs[iIdentifiantValeur] = m_listeValeurs.ObtenirMotParcouru();
        }
        
        return tTableauValeurs;
    }
    
    
    /**
     *  fills an additional table containing all the numerical values of the column, but sorted.
     */
    public void ConstruireTableauValeursQuantitativesTriees() {
        float fValeurCourante = 0.0f;
        float fValeurSuivante = 0.0f;
        int iValeurUniqueCourante = 0;
        int iValeurUniqueSuivante = 0;
        int iIndiceValeur = 0;
        int iIndiceValeurSuivante = 0;
        int iIndiceRemplissage = 0;
        int iSupportCumule = 0;
        
        
        if (m_iTypeValeurs != DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL)
            return;
        

        // On comptabilise le nombre de valeurs num�riques non manquantes :
        m_iNombreValeursReellesCorrectes = 0;
        for (iIndiceValeur = 0; iIndiceValeur < m_iNombreLignes; iIndiceValeur++)
            if (m_tValeurReelle[iIndiceValeur] != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT)
                m_iNombreValeursReellesCorrectes++;
        
        if (m_iNombreValeursReellesCorrectes == 0)
            m_tValeursReellesTriees = null;
        else         
            m_tValeursReellesTriees = new float [m_iNombreValeursReellesCorrectes];  

        // On remplit le tableau des valeurs num�riques correctes :
        for (iIndiceValeur = 0; iIndiceValeur < m_iNombreValeursReellesCorrectes; iIndiceValeur++)
            if (m_tValeurReelle[iIndiceValeur] != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT)
                m_tValeursReellesTriees[iIndiceValeur] = m_tValeurReelle[iIndiceValeur];
            
        
        // Puis on les trie :
        if (m_tValeursReellesTriees != null)
            Arrays.sort(m_tValeursReellesTriees);
       

        
        //----------------------------------------------
        // CONSTRUCTION DU TABLEAU DES VALEURS UNIQUES :        

        if (m_iNombreValeursReellesCorrectes == 0)
            m_tValeursUniques = null;
        else         
            m_tValeursUniques = new int [m_iNombreValeursReellesCorrectes];  
        
        
        m_iNombreValeursUniques = 0;
        iIndiceValeur=0;
        while (iIndiceValeur < m_iNombreValeursReellesCorrectes) {
            fValeurSuivante = fValeurCourante = m_tValeursReellesTriees[iIndiceValeur];

            iIndiceValeurSuivante = iIndiceValeur + 1;
            while ( (fValeurSuivante == fValeurCourante) && (iIndiceValeurSuivante < m_iNombreValeursReellesCorrectes) ) {
                fValeurSuivante = m_tValeursReellesTriees[iIndiceValeurSuivante];
                if (fValeurSuivante == fValeurCourante)
                    iIndiceValeurSuivante++;
            }
            
            // Remplissage du tableau pour une m�me valeur de support cumule :
            for (iIndiceRemplissage = iIndiceValeur; iIndiceRemplissage < iIndiceValeurSuivante; iIndiceRemplissage++)
                m_tValeursUniques[iIndiceRemplissage] = m_iNombreValeursUniques;
                
            iIndiceValeur = iIndiceValeurSuivante;
            m_iNombreValeursUniques++;
        }
            
        
        
        //-----------------------------------------------
        // CONSTRUCTION DU TABLEAU DES SUPPORTS CUMULES :
        
        if (m_iNombreValeursUniques == 0)
            m_tCumulSupportCroissant = null;
        else         
            m_tCumulSupportCroissant = new int [m_iNombreValeursUniques];  

        
        iSupportCumule = 1;
        iIndiceValeur=0;
        while (iIndiceValeur<m_iNombreValeursReellesCorrectes) {
            iValeurUniqueSuivante = iValeurUniqueCourante = m_tValeursUniques[iIndiceValeur];

            iIndiceValeurSuivante = iIndiceValeur + 1;
            while ( (iValeurUniqueSuivante==iValeurUniqueCourante) && (iIndiceValeurSuivante<m_iNombreValeursReellesCorrectes) ) {
                iValeurUniqueSuivante = m_tValeursUniques[iIndiceValeurSuivante];
                if (iValeurUniqueSuivante == iValeurUniqueCourante) {
                    iSupportCumule++;
                    iIndiceValeurSuivante++;
                }
            }
            
            m_tCumulSupportCroissant[iValeurUniqueCourante] = iSupportCumule;
                
            iIndiceValeur = iIndiceValeurSuivante;
            iSupportCumule++;
        }
        
    }

    
    
    public int ObtenirSupportIntervalle(int iBorneMin, int iBorneMax) {
        int iIndiceUniqueMin = 0;
        int iIndiceUniqueMax = 0;
       
        if (m_tCumulSupportCroissant == null)
            return 0;
        
        if (iBorneMin < 0)
            iBorneMin = 0;
        
        if (iBorneMax >= m_iNombreValeursReellesCorrectes)
            iBorneMax = m_iNombreValeursReellesCorrectes - 1;
        
        iIndiceUniqueMin = m_tValeursUniques[iBorneMin];
        iIndiceUniqueMax = m_tValeursUniques[iBorneMax];
        
        if (iIndiceUniqueMin == 0)
            return m_tCumulSupportCroissant[iIndiceUniqueMax];
        else
            return (m_tCumulSupportCroissant[iIndiceUniqueMax] - m_tCumulSupportCroissant[iIndiceUniqueMin-1]);
    }
    
    
}
