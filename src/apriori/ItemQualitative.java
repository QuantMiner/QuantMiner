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

import src.tools.dataStructures.*;



public class ItemQualitative extends Item implements Comparable {
        
    long m_lIdentifieur = 0;
    public AttributQualitative m_attributQual = null;   // Attribut de la BD dont est issu l'item
    public short m_iIndiceValeur = 0;                  // Indice de la valeur repr�sent�e par l'item parmi toutes celles de l'attribut 
       
        
    public ItemQualitative(AttributQualitative attributQual, short iIndiceValeur) {
        super();
        
        super.m_iTypeItem = ITEM_TYPE_QUALITATIF;
        
        // Obtention d'un num�ro d'identification unique pour l'item nouvellement cr�� :
        m_lIdentifieur = TableItems.ObtenirIdentifieurUnique();
        
        m_attributQual = attributQual;
        m_iIndiceValeur = iIndiceValeur;
        
        m_itemSuivant = null;
    }
         
   
    
    /**Get full Name of the item
     * @return name of the item
     */
    public String ObtenirNomCompletItem() {
        String sChaineItem = null;
        
        if (m_attributQual==null)
            return null;
        
        sChaineItem = m_attributQual.ObtenirNom();
        sChaineItem += ".";
        sChaineItem += m_attributQual.ObtenirValeurCorrespondantIndice(m_iIndiceValeur);
        
        return sChaineItem;
    }
    
    
    
    /**Get value of an item ID
     * @return value of an item
     */
    public String ObtenirIdentifiantTexteItem() {
        
        if (m_attributQual==null)
            return null;
        
        return m_attributQual.ObtenirValeurCorrespondantIndice(m_iIndiceValeur);
    }
    
    /**Obtain the name and value of this item/attribute
     * @return A Qualitative item
     */
    public Qualitative getAttributeNameValue(){
    	Qualitative element = new Qualitative();
    	String sNomAttribut = null;       //name of the attribute
    	String sValeurItem = null;        //value of the item

    	if (m_attributQual==null)
            return null;
    	
    	sNomAttribut = m_attributQual.ObtenirNom();   //get the name of the attribute
        if (sNomAttribut != null)
            sNomAttribut = sNomAttribut.trim();
        
        sValeurItem = m_attributQual.ObtenirValeurCorrespondantIndice(m_iIndiceValeur); //get the value of the attribute
        if (sValeurItem != null)
            sValeurItem = sValeurItem.trim();
        
        element.setM_name(sNomAttribut);
        element.setM_value(sValeurItem);
        
        return element;
    }
    
    
    //return something like COD_GEOL = Tv
    public String toString() {
        String sItem = null;              //
        String sNomAttribut = null;       //name of the attribute
        String sValeurItem = null;        //value of the item
        
        if (m_attributQual==null)
            return "Item nul";
 
        sNomAttribut = m_attributQual.ObtenirNom();   //get the name of the attribute
        if (sNomAttribut != null)
            sNomAttribut = sNomAttribut.trim();
        
        sValeurItem = m_attributQual.ObtenirValeurCorrespondantIndice(m_iIndiceValeur); //get the value of the attribute
        if (sValeurItem != null)
            sValeurItem = sValeurItem.trim();
        
        sItem = sNomAttribut;
        sItem += " = ";
        sItem += sValeurItem;
        
        return sItem;       
    }
    
    
    
    public int compareTo(Object o) {
        if (this.m_lIdentifieur > ((ItemQualitative)o).m_lIdentifieur)
            return 1;
        else if (this.m_lIdentifieur < ((ItemQualitative)o).m_lIdentifieur)
            return -1;
        else
            return 0;       
    }
    
    
    
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else
            return ( this.m_lIdentifieur == ((ItemQualitative)obj).m_lIdentifieur );       
    }

}
