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



class TableItems {
    
    static long m_lCompteurItem = 0; // ID sur 64 bits (borne � 9 milliards de milliards)
    
    ItemQualitative m_premierItem;
    ItemQualitative m_dernierItem;
     
     
    TableItems() {
        m_premierItem = null;
        m_dernierItem = null;
    }
    

    static long ObtenirIdentifieurUnique() {
        m_lCompteurItem++;
        return m_lCompteurItem;
    }
    
    
    void DeclarerItemQualitatif(AttributQualitative attribut, short iIndiceValeur) {
        
        ItemQualitative nouvelItem = new ItemQualitative(attribut, iIndiceValeur);
        
        if ( (m_premierItem==null) || (m_dernierItem==null) )
            m_premierItem = m_dernierItem = nouvelItem;
        else {
            m_dernierItem.m_itemSuivant = nouvelItem;
            m_dernierItem = nouvelItem;
        }
    }
    

    ItemQualitative ObtenirPremierItem() {
        return m_premierItem;
    }
    
    
}
