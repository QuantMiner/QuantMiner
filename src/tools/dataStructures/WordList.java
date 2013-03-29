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
package src.tools.dataStructures;

import java.util.*;



public class WordList {

    
    public class InfosMot {
        
        public int m_iIdentifiantUnique;   // Num�ro identifiant le mot parmi all ceux de la liste
        public int m_iOccurrences;         // Nombre de fois o� on a ins�r� le mot dans la liste

        public InfosMot(int iIdentifiantUnique) {
            m_iIdentifiantUnique = iIdentifiantUnique;
            m_iOccurrences = 1;
        }        
        
    }
    
    
    
    Hashtable m_tableMots = null;
    int m_iNombreMots = 0;    
    
    String m_sChaineParcours = null;
    boolean m_bParcoursInicie = false;
    Enumeration m_enumerationMotsParcourus = null;
    

    
    
    public WordList() {
        m_tableMots = new Hashtable();
        m_iNombreMots = 0;
        
        m_sChaineParcours = null;
        m_bParcoursInicie = false;
        m_enumerationMotsParcourus = null;
    }
        
    
        
    public int ObtenirNombreMots() {  //obtain number of words
        return m_iNombreMots;
    }
    
    
    /**Return the position of the word in String
     * @param sChaine String
     * @return position
     */
    public int InsererMot(String sChaine) {
        int iIdentificateurChaine = 0;
        InfosMot infosMot = null;
        
        if (sChaine==null)
            return -1;
        
        infosMot = (InfosMot)m_tableMots.get(sChaine);

        // Cas o� la cha�ne est d�j� r�pertori�e :
        if (infosMot != null) {
            infosMot.m_iOccurrences++;
            iIdentificateurChaine = infosMot.m_iIdentifiantUnique;
        }
        
        // Cas o� elle n'est pas r�pertoiri�e, dans ce cas on l'ajoute :
        else {
            infosMot = new InfosMot(m_iNombreMots);
            m_tableMots.put(sChaine, infosMot);
            iIdentificateurChaine = m_iNombreMots;   //Position in Chain 
            m_iNombreMots++;                         //number of words increase by one
        }           
            
        return iIdentificateurChaine;
    }

        

    public boolean EstDansListe(String sChaine) {
        if (sChaine == null)
            return false;
        else
            return m_tableMots.containsKey(sChaine);
    }
    
       
        
    // Retrouve les informations stock�es pour un mot donn� de la liste :
    public InfosMot ChercherInfosMot(String sChaine) {  //Chercher means search/look for
        if (sChaine==null)
            return null;
        else
            return (InfosMot)m_tableMots.get(sChaine); //Returns the value to which the specified key is mapped in this hashtable.         
    }
    
        
 
    public void InicierParcours() {
        m_bParcoursInicie = true;
        m_sChaineParcours = null;
        m_enumerationMotsParcourus = m_tableMots.keys(); //Returns an enumeration of the keys in this hashtable. 
    }
        

    
    public boolean AvancerParcours() { //Get the next
            
        if ( (!m_bParcoursInicie) || (m_enumerationMotsParcourus == null) )
            return false;
        
        if (m_enumerationMotsParcourus.hasMoreElements()) //Tests if this enumeration contains more elements. 
            m_sChaineParcours = (String)m_enumerationMotsParcourus.nextElement();
        else {
            m_enumerationMotsParcourus = null;
            m_sChaineParcours = null;
            m_bParcoursInicie = false;
        }
        
        return m_bParcoursInicie;
    }
        
        
    public String ObtenirMotParcouru() {
        if (m_bParcoursInicie)
            return m_sChaineParcours;
        else
            return null;
    }
        

    public InfosMot ObtenirInfosMotParcouru() {
        if ( (m_bParcoursInicie) && (m_sChaineParcours != null) )
            return ChercherInfosMot(m_sChaineParcours);
        else
            return null;
    }
 
}
