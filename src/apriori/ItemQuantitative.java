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


public class ItemQuantitative extends Item {
    
    public AttributQuantitative m_attributQuant = null;   // Attribut de la BD dont est issu l'item
    public int m_iNombreDisjonctions = 0;
    public float [] m_tBornes = null; // Les bornes min et max, stock�es successivement 2 � 2 (chaque paire correspond � un intervalle afin de pouvoir autoriser les unions)
     
    
    /**
     * Creating Multi Item Quantitative Intervals
     * @param attributQuant Quantitative attributes
     * @param tBornes Interval bounds
     */
    private void CreerItemQuantitatifMultiIntervalles(AttributQuantitative attributQuant, float [] tBornes) {
        int iIndiceDisjonction = 0;
        
        super.m_iTypeItem = ITEM_TYPE_QUANTITATIF;
        
        m_attributQuant = attributQuant;
        if (tBornes == null)
            m_iNombreDisjonctions = 0;
        else
            m_iNombreDisjonctions = tBornes.length / 2;
        
        if (m_iNombreDisjonctions == 0) {
            m_tBornes = null;
            return;
        }
                  
        m_tBornes = new float [m_iNombreDisjonctions * 2];
        for (iIndiceDisjonction=0; iIndiceDisjonction<(m_iNombreDisjonctions*2); iIndiceDisjonction++)
            m_tBornes[iIndiceDisjonction] = tBornes[iIndiceDisjonction];
    }
        
    
    
    public ItemQuantitative(AttributQuantitative attributQuant, float [] tBornes) {
        super();
        
        CreerItemQuantitatifMultiIntervalles(attributQuant, tBornes);
    } 
    
    
    
    /**
     * Simple constructor that puts all the intervals, the number required [0,0]:
     * @param attributQuant Quantitative attribute
     * @param iNombreDisjonctions number of disjunctions
     */
    public ItemQuantitative(AttributQuantitative attributQuant, int iNombreDisjonctions) {
        super();
        
        float tBornes [] = null;
        int iIndiceBorne = 0;
      
        if (iNombreDisjonctions <= 0)
            CreerItemQuantitatifMultiIntervalles(attributQuant, null);
        else {
            tBornes = new float [iNombreDisjonctions*2];
            for (iIndiceBorne=0; iIndiceBorne<iNombreDisjonctions*2; iIndiceBorne++)
                tBornes[iIndiceBorne] = 0.0f;
            CreerItemQuantitatifMultiIntervalles(attributQuant, tBornes);
        }
    } 

    
    
    /**
     * Constructor plus simple pour attribuer un intervalle unique :
     * @param attributQuant
     * @param fBorneMin
     * @param fBorneMax
     */
    public ItemQuantitative(AttributQuantitative attributQuant, float fBorneMin, float fBorneMax) {
        super();

        super.m_iTypeItem = ITEM_TYPE_QUANTITATIF;
       
        m_attributQuant = attributQuant;
        m_iNombreDisjonctions = 1;
        
        m_tBornes = new float [2];
        m_tBornes[0] = fBorneMin;
        m_tBornes[1] = fBorneMax;
    } 
    
    
    
    // Constructeur de copie :
    public ItemQuantitative(ItemQuantitative item) {
        super();
        
        CopierItemQuantitatif(item);
    }
    
    

    public void CopierItemQuantitatif(ItemQuantitative item) {
        int iIndiceDisjonction = 0;

        this.m_iTypeItem = ITEM_TYPE_QUANTITATIF;
        this.m_attributQuant = item.m_attributQuant;
        this.m_iNombreDisjonctions = item.m_iNombreDisjonctions;
        
        if (m_iNombreDisjonctions == 0) {
            this.m_tBornes = null;
            return;
        }
                  
        this.m_tBornes = new float [this.m_iNombreDisjonctions * 2];
        for (iIndiceDisjonction=0; iIndiceDisjonction<(this.m_iNombreDisjonctions*2); iIndiceDisjonction++)
            this.m_tBornes[iIndiceDisjonction] = item.m_tBornes[iIndiceDisjonction];
    }
    
    
    //obtain the min value of the iIndiceDisjonction disjunct
    public float ObtenirBorneMinIntervalle(int iIndiceDisjonction) {
        if (iIndiceDisjonction < m_iNombreDisjonctions)
            return m_tBornes[iIndiceDisjonction * 2];
        else
            return 0;
    }
    
    //obtain the max value of the iIndiceDisjonction disjunct    
    public float ObtenirBorneMaxIntervalle(int iIndiceDisjonction) {
        if (iIndiceDisjonction < m_iNombreDisjonctions)
            return m_tBornes[iIndiceDisjonction*2 + 1];
        else
            return 0;
    }
    
    //obtain the name, min, max value of the attribute
    public Quantitative getAttributeNameBoundary(int iIndiceDisjonction){
    	Quantitative element = new Quantitative();
     	String sNomItem = null;
        
     	if (m_attributQuant==null)
           return null;
     	
         sNomItem = m_attributQuant.ObtenirNom();
         if (sNomItem != null)
             sNomItem = sNomItem.trim();
         
         element.setM_name(sNomItem);
         element.setM_lower(m_tBornes[iIndiceDisjonction*2]);
         element.setM_upper(m_tBornes[iIndiceDisjonction*2 + 1]);
         
         return element;         
    }
    
    
    //return something like ALTITUDE in [1814.0; 4926.0]
    public String toString(int iIndiceDisjonction) {
        String sItem = null;
        String sNomItem = null;
        
        if (m_attributQuant==null)
            return "Item nul";
 
        sNomItem = m_attributQuant.ObtenirNom();
        if (sNomItem != null)
            sNomItem = sNomItem.trim();
        
        sItem = sNomItem;
        sItem += " in ";
        
        sItem += "[";
        sItem += String.valueOf( m_tBornes[iIndiceDisjonction*2] );   //min
        sItem += "; ";
        sItem += String.valueOf( m_tBornes[iIndiceDisjonction*2 + 1] ); //max
        sItem += "]";
        
        return sItem;       
    }
 
}
