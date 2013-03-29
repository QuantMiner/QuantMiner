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
package src.simulatedAnnealing;


// Classe r�pertoriant l'ensemble des param�tres d�finis par l'utilisateur pour la prochaine
// ex�cution de l'algorithme de recuit simul�.

public class SimulatedAnnealingParameters {
    
    public static final int DEFAUT_NBITER = 1000;
    public static final int DEFAUT_NBPARALL = 4;
    
    public int m_iNombreIterations = 0;
    public int m_iNombreSolutionsParalleles = 0;
    
    
    public SimulatedAnnealingParameters() {
        m_iNombreIterations = DEFAUT_NBITER;
        m_iNombreSolutionsParalleles = DEFAUT_NBPARALL;
    }
    
    
    public String toString() {
        String sParametres = null;
        
        sParametres = "Selected parameters for the simulated annealing algorithm:" + "\n" + "\n";
        sParametres += "Number of tentatives to improve the rule: " + String.valueOf(m_iNombreIterations) + "\n";
        sParametres += "Number of potential rules to test in parallel: " + String.valueOf(m_iNombreSolutionsParalleles) + "\n";
        
        return sParametres;
    }
        
}
