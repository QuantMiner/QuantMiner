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

import src.solver.*;


// Classe r�pertoriant l'ensemble des param�tres d�finis par l'utilisateur pour la prochaine
// ex�cution de l'algorithme extracteur de r�gles.

/**Apiori rule parameter
 */
public class StandardParameters {
    
    public static final float DEFAUT_MINSUPP = 0.10f;
    public static final float DEFAUT_MINCONF = 0.60f;
    
    public float m_fMinSupp = 0.0f;
    public float m_fMinConf = 0.0f;

    
    public StandardParameters() {
        m_fMinSupp = DEFAUT_MINSUPP;
        m_fMinConf = DEFAUT_MINCONF;
    }
        
    
    public String toString() {
        String sParametres = null;
        
        sParametres = "General parameters selected for rule extraction:" + "\n" + "\n";
        sParametres += "Minimal Support: " + ResolutionContext.EcrirePourcentage(m_fMinSupp, 3, true) + "\n";
        sParametres += "Minimal Confidence: " + ResolutionContext.EcrirePourcentage(m_fMinConf, 3, true) + "\n";
       
        return sParametres;
    }
}
