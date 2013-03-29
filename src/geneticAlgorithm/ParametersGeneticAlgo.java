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
package src.geneticAlgorithm;


// Classe r�pertoriant l'ensemble des param�tres d�finis par l'utilisateur pour la prochaine
// ex�cution de l'algorithme g�n�tique.

/**Generic algorithm's technique parameters
 */
public class ParametersGeneticAlgo {
    
    public static final int DEFAUT_TAILLEPOP = 250;     //population size
    public static final int DEFAUT_NBGEN = 100;         //# of generations
    public static final float DEFAUT_COEFFCROIS = 0.50f;  //cross-over rate
    public static final float DEFAUT_COEFMUT = 0.40f;     //mutation rate
    
    public int m_iTaillePopulation = 0;
    public int m_iNombreGenerations = 0;
    public float m_fPourcentageCroisement = 0.0f;
    public float m_fPourcentageMutation = 0.0f;
    
    
    public ParametersGeneticAlgo() {
        m_iTaillePopulation = DEFAUT_TAILLEPOP;
        m_iNombreGenerations = DEFAUT_NBGEN;
        m_fPourcentageCroisement = DEFAUT_COEFFCROIS;
        m_fPourcentageMutation = DEFAUT_COEFMUT;
    }
    
    
    public String toString() {
        String sParametres = null;
        
        sParametres = "Selected parameters for the genetic algorithm :" + "\n" + "\n";
        sParametres += "Size of the population: " + String.valueOf(m_iTaillePopulation) + "\n";
        sParametres += "Number of generations: " + String.valueOf(m_iNombreGenerations) + "\n";
        sParametres += "Percent cross-over: " + String.valueOf(m_fPourcentageCroisement) + "\n";
        sParametres += "Percent of mutation: " + String.valueOf(m_fPourcentageMutation) + "\n";
        
        return sParametres;
    }
        
}
