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
package src.tools;

import java.util.*;



public class SortingTools {
    
  
    // Classe renvoyant un tableau de cha�nes tri�es relativement au contenu d'un tableau d'entiers
    // (� valeur enti�re �gales, 2 �l�ments sont distingu�s par ordre alphab�tique) :
    public static String [] CompateurBiTableaux_Chaines_Entiers(final String [] tChaines, final int [] tEntiers, final boolean bTriEntiersCroissants) {
        
        // Classe interne permettant la comparaison :
        class Indice implements Comparable {
            public int m_iIndice = 0;
           
            public Indice(int iIndice) {
                m_iIndice = iIndice;
            }
            
            public int compareTo(Object o) {
                Indice indice = null;
                
                indice = (Indice)o;
                if ( tEntiers[this.m_iIndice] < tEntiers[indice.m_iIndice] ) {
                    if (bTriEntiersCroissants)
                        return -1;
                    else
                        return 1;
                }
                else if ( tEntiers[this.m_iIndice] > tEntiers[indice.m_iIndice] ) {
                    if (bTriEntiersCroissants)
                        return 1;
                    else
                        return -1;
                }
                else {
                    if (tChaines[this.m_iIndice] != null)
                        return tChaines[this.m_iIndice].compareTo(tChaines[indice.m_iIndice]);
                    else
                        return -1;
                }
            }
            
        }
            
            
        Indice [] tIndices = null; // Tableau des indices des cha�nes � trier dans 'tChaines'
        String [] tChainesTriees = null;
        int iNombreChaines = 0;
        int iIndiceChaine = 0;

        if ( (tChaines == null) || (tEntiers == null) )
            return tChaines;

        iNombreChaines = tChaines.length;

        if (iNombreChaines == 0)
            return tChaines;

        if (iNombreChaines != tEntiers.length)
            return tChaines;
        
        tIndices = new Indice[iNombreChaines];
        for (iIndiceChaine=0; iIndiceChaine<iNombreChaines; iIndiceChaine++)
            tIndices[iIndiceChaine] = new Indice(iIndiceChaine);
        
        // Tri des indices en prenant en compte d'abord l'ordre du tableau d'entiers puis l'ordre alphab�tique des cha�nes :
        Arrays.sort(tIndices);
        
        // On r�ordonne le tableau de cha�nes suivant le nouvel ordre des indices calcul� :
        tChainesTriees = new String[iNombreChaines];
        for (iIndiceChaine=0; iIndiceChaine<iNombreChaines; iIndiceChaine++)
            tChainesTriees[iIndiceChaine] = tChaines[ tIndices[iIndiceChaine].m_iIndice ]; 
        
        return tChainesTriees;
    }
 
}
