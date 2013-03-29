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

package src.apriori;

import java.util.*;

import src.database.*;
import src.tools.dataStructures.*;



public class AttributQualitative {
    
    public String m_sNomAttribut;
    
    public int m_iNombreValeurs = 0;
    public String [] m_tTableauValeurs = null;          // Table contains every value associated with that attribute
                                                        // index�es par leur identificateur unique.
    public boolean [] m_tTableauPriseEnCompte = null;   // Tableau mettant en relation every value avec un bool�en dont
                                                        // la valeur indique s'il doit �tre pris en compte ou non lors du proc�d� d'extraction de r�gles
    public DataColumn m_colonneDonnees = null;      // Colonne correspondant � l'attribut dans la base de donn�es
    
    public ArrayList [] m_LiensItemSets = null;         // Itemsets li�s � une valeur d'attribut, devant �tre incr�ment�s lors de la comptabilisation du nombre d'occurrences de l'item
    
    
    
    public AttributQualitative(String sNomAttribut, DataColumn colonneDonnees) {
        
        int iIndiceValeur = 0;
        
        m_sNomAttribut = sNomAttribut;
        m_colonneDonnees = colonneDonnees;
        
        if (m_colonneDonnees!=null) {
            m_iNombreValeurs = m_colonneDonnees.ObtenirNombreValeursDifferentes();
            m_LiensItemSets = new ArrayList [m_iNombreValeurs];
            
            for (iIndiceValeur = 0; iIndiceValeur < m_iNombreValeurs; iIndiceValeur++)
                m_LiensItemSets[iIndiceValeur] = new ArrayList();
        }
        else {
            m_iNombreValeurs = 0;
            m_LiensItemSets = null;
        }
        
        if (m_iNombreValeurs > 0) {
            m_tTableauValeurs = colonneDonnees.ConstituerTableauValeurs();
            m_tTableauPriseEnCompte = new boolean [m_iNombreValeurs];
            
            for (iIndiceValeur=0;iIndiceValeur<m_iNombreValeurs;iIndiceValeur++)
                m_tTableauPriseEnCompte[iIndiceValeur] = true;
        }
        else {
            m_tTableauValeurs = null;
            m_tTableauPriseEnCompte = null;
        }
    }
    

 
    /**Get Correspondent Index Value 
     * @param sNomItem Item Name
     */
    public short ObtenirIndiceCorrespondantValeur(String sNomItem) {
        return (short)m_colonneDonnees.ObtenirNumeroCorrespondance(sNomItem);
    }
    
    
    
    /** return the value of an attribute by index.
     * @param iIndice Index of an attribute
     * @return value of the attribute
     */
    String ObtenirValeurCorrespondantIndice(short iIndice) {
        if ( (iIndice >= 0) && ((int)iIndice < m_iNombreValeurs) )
            return m_tTableauValeurs[iIndice];
        else
            return null;
    }
    
  
    /**Compute number of occurrence
     * @param iNumeroTransaction Transaction Number
     * @param iIndiceValeur Index Value
     */
    void ComptabiliserOcurrenceValeur(int iNumeroTransaction, short iIndiceValeur) {
        
        ItemSet itemSet = null;
        int iIndiceItemSet = 0;
        int iNombreItemSets = 0;
        ArrayList listeItemSets = null;
        
        if ( (iIndiceValeur<0) || ((int)iIndiceValeur >= m_iNombreValeurs) )
            return;
            
        listeItemSets = m_LiensItemSets[ (int)iIndiceValeur ];
        
        iNombreItemSets = listeItemSets.size();
               
        for (iIndiceItemSet=0; iIndiceItemSet<iNombreItemSets; iIndiceItemSet++) {
            itemSet = (ItemSet)listeItemSets.get(iIndiceItemSet);
            itemSet.ComptabiliserDecouverteItem(iNumeroTransaction);
        }        
    }
    
    
    /**Get the name of the attribute
     * @return name of the attribute
     */
    public String ObtenirNom() {
        return m_sNomAttribut;
    }
    

    void GenererItems(TableItems tableItems) {
        
        int iIndiceValeur = 0;
        
        for (iIndiceValeur = 0; iIndiceValeur < m_iNombreValeurs; iIndiceValeur++)
            tableItems.DeclarerItemQualitatif(this, (short)iIndiceValeur);
    }

    
    // Deux attributs sont �gaux si et seulement si ils ont le m�me nom :
    public boolean equals(Object obj) {
        
        if (obj==null) return false;
        
        if ( (m_sNomAttribut==null) || ( ((AttributQualitative)obj).m_sNomAttribut==null) )
            return false;
        
        return ( m_sNomAttribut.equals( ((AttributQualitative)obj).m_sNomAttribut ) );
    }
    
    
    /**Add item set
     * @param iIndiceValeur
     * @param itemSet
     */
    void AjouterLienVersItemSet(int iIndiceValeur, ItemSet itemSet) {
        if ( (iIndiceValeur>=0) && ((int)iIndiceValeur<m_iNombreValeurs) )
            m_LiensItemSets[iIndiceValeur].add(itemSet);
    }
    
    
    /**Reset ItemSets
     */
    void ReinitialiserListeLiensItemSets() {
        int iIndiceValeur = 0;
        
        for (iIndiceValeur=0;iIndiceValeur<m_iNombreValeurs;iIndiceValeur++)
            m_LiensItemSets[iIndiceValeur].clear();
    }
 
    /**Get the support of an item
     * @param iIndiceValeur index
     * @return
     */
    int ObtenirSupportItem(int iIndiceValeur) {
        
        WordList.InfosMot infosMot = null;
        
        if ( (iIndiceValeur>=0) && ((int)iIndiceValeur < m_iNombreValeurs) ) {
            infosMot = m_colonneDonnees.m_listeValeurs.ChercherInfosMot( m_tTableauValeurs[iIndiceValeur] );
            if (infosMot != null)
                return infosMot.m_iOccurrences;
            else
                return 0;
        }
        else
            return 0;
    }
    
    
    
    void DefinirPriseEnCompteValeur(String sNomItem, boolean bPrendreEnCompte) {
        short iIndiceItem = -1;
        
        // On r�cup�re l'identificateur de l'item :
        iIndiceItem = ObtenirIndiceCorrespondantValeur(sNomItem);
        if (iIndiceItem < 0)
            return;
        
        m_tTableauPriseEnCompte[iIndiceItem] = bPrendreEnCompte;
    }


}
    
