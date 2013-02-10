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



public class Item {
        
    public static final int ITEM_TYPE_INDEFINI = 0;
    public static final int ITEM_TYPE_QUALITATIF = 1;
    public static final int ITEM_TYPE_QUANTITATIF = 2;
   
    public Item m_itemSuivant = null;
    public int m_iTypeItem = ITEM_TYPE_INDEFINI;
        
    
    public Item() {
        m_iTypeItem = ITEM_TYPE_INDEFINI;
        m_itemSuivant = null;
    }
 
    
    public String toString() {
        String sItem = null;
        
        switch (m_iTypeItem) {
            
            case ITEM_TYPE_QUALITATIF :
                sItem = "Item Qualitatif";
                break;
            
            case ITEM_TYPE_QUANTITATIF :
                sItem = "Item Quantitatif";
                break;
            
            default :
                sItem = "Item undefined"; //undefined
        }
        
        return sItem;        
    }
    
}
