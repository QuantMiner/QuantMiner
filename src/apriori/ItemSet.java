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

public class ItemSet {
    
    int m_iTaille = 0;
    ItemQualitative m_listeItems [] = null;
    int m_iNombreItemsSpecifies = 0;
    public int m_iSupport = 0;
    int m_iNombreItemsComptabilises = 0;
    int m_iDerniereTransactionComptabilisee = -1;
    
    public ItemSet(int iTaille) {
        
        int iIndiceItem = 0;
        
        m_iTaille = iTaille;
        
        if (m_iTaille>0) { 
            m_listeItems = new ItemQualitative [iTaille];
            Arrays.fill(m_listeItems, null);
        }
        
        m_iNombreItemsSpecifies = 0;
        m_iSupport = 0;
        m_iNombreItemsComptabilises = 0;
        m_iDerniereTransactionComptabilisee = -1;
    }
      
 
    void SpecifierItem(ItemQualitative item) {
        
        if (m_iNombreItemsSpecifies < m_iTaille) {
        
            m_listeItems[m_iNombreItemsSpecifies] = item;
            m_iNombreItemsSpecifies++;
        
            if (m_iNombreItemsSpecifies == m_iTaille)
                Arrays.sort(m_listeItems);               
        }
    }
    
    
    
    public ItemQualitative ObtenirItem(int iIndiceItem) {
        if ( (iIndiceItem >= 0) && (iIndiceItem < m_iNombreItemsSpecifies) )
            return m_listeItems[iIndiceItem];
        else 
            return null;
    }
    
    
    
    void SpecifierSupport(int iSupport) {
        m_iSupport = iSupport;
    }
    
    
    /**
     * This function is called each time one of the items belonging to the itemset is found 
	 * while reading a tuple of the dataset.  The counter must be re-initialized for each new tuple 
     * with the function 'ReinitialiserComptabilisationItems'.
     * @param iNumeroTransaction
     */
    void ComptabiliserDecouverteItem(int iNumeroTransaction) {
        
        if (iNumeroTransaction == m_iDerniereTransactionComptabilisee)
            m_iNombreItemsComptabilises++;
        else
            m_iNombreItemsComptabilises = 1;
        
        // On incr�mente le support si tous les items ont �t� trouv�s :
        if (m_iNombreItemsComptabilises==m_iTaille)
            m_iSupport++;

        m_iDerniereTransactionComptabilisee = iNumeroTransaction;
    }
    
    
    void ReinitialiserComptabilisationItems() {
        m_iNombreItemsComptabilises = 0;
        m_iDerniereTransactionComptabilisee = -1;
    }
    
    /**
     * Get string format of Item sets
     * @param iNombreTransactions number of transactions
     * @param bAfficherSuppport	display Support or not
     * @return string
     */
    public String EcrireItemSet(int iNombreTransactions, boolean bAfficherSuppport) {
        
        String sTexteItemSet = null;
        String sTexteItem = null;
        int iIndiceItem = 0;
        ItemQualitative item = null;
        
        sTexteItemSet = "{ ";
        
        for (iIndiceItem=0;iIndiceItem<m_iTaille;iIndiceItem++) {
            item = m_listeItems[iIndiceItem];
            
            if (item!=null) {
                if (iIndiceItem>0) sTexteItemSet += ", ";
                
                sTexteItemSet += item.toString();
            }
        }

        sTexteItemSet += " }";
        
        if (bAfficherSuppport) {
            sTexteItemSet += "  ,  support = ";
            sTexteItemSet += String.valueOf( m_iSupport );
            sTexteItemSet += " (";
            sTexteItemSet += String.valueOf( (float)(100.0f*((float)m_iSupport / (float)iNombreTransactions)) );
            sTexteItemSet += "%)";
        }
        
        return sTexteItemSet;
    }
    
       
    
    /**
     * Generate a K-itemset starting from two (k-1) itemsets given they share the same (k-2) items in the beginning: 
     * (the two itemsets used by the function must have the same size)
     * @param itemSetAutre
     * @return
     */
    boolean EstGenerateurCandidatAvec(ItemSet itemSetAutre) {
        
        int iIndiceItem = 0;
        boolean bItemTousIdentiques = true;
        
        iIndiceItem=0;
        while ( (bItemTousIdentiques) && (iIndiceItem<m_iTaille-1) ) {
            
            bItemTousIdentiques = ( this.m_listeItems[iIndiceItem].compareTo( itemSetAutre.m_listeItems[iIndiceItem] ) == 0 );
            
            iIndiceItem++;
        }
        
        return bItemTousIdentiques;
    }
    

}
