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

/**Generic algorithm rule parameter
 */
public class StandardParametersQuantitative {
    
    public static final float DEFAUT_MINSUPP = 0.10f;
    public static final float DEFAUT_MINCONF = 0.60f;
    public static final float DEFAUT_MINSUPP_DISJONCTIONS = 0.0f;   //support threshold for additional intervals 
    
    public float m_fMinSupp = 0.0f;
    public float m_fMinConf = 0.0f;
    public int m_iNombreMinAttributsQuant = 0; //min # of quantitative attributes in a rule
    public int m_iNombreMaxAttributsQuant = 0; //max # of quantitative attributes in a rule
    public int m_iNombreDisjonctionsGauche = 0;  //# of allowed OR in the rule on the right side
    public int m_iNombreDisjonctionsDroite = 0;  //# of allowed OR in the rule on the left side
    public float m_fMinSuppDisjonctions = 0.0f;  //support threshold for additional intervals 
    
    
    public StandardParametersQuantitative() {
        m_fMinSupp = DEFAUT_MINSUPP;
        m_fMinConf = DEFAUT_MINCONF;
        m_iNombreMinAttributsQuant = 1;
        m_iNombreMaxAttributsQuant = 3;
        m_iNombreDisjonctionsGauche = 1;
        m_iNombreDisjonctionsDroite = 1;
        m_fMinSuppDisjonctions = DEFAUT_MINSUPP_DISJONCTIONS;
    }
        
    public String toString() {
        String sParametres = null;
        
        sParametres = "Gerneral parameters selected for rule extraction:" + "\n" + "\n";
        sParametres += "Minimal Support: " + ResolutionContext.EcrirePourcentage(m_fMinSupp, 3, true) + "\n";
        sParametres += "Minimal Confidence: " + ResolutionContext.EcrirePourcentage(m_fMinConf, 3, true) + "\n";
        sParametres += "Minimum number of numerical attributes in a rule: " + String.valueOf(m_iNombreMinAttributsQuant) + "\n";
        sParametres += "Maximum number of numerical attributes in a rule: " + String.valueOf(m_iNombreMaxAttributsQuant) + "\n";
        sParametres += "Number of disjunctions allowed in the left-hand side: " + String.valueOf(m_iNombreDisjonctionsGauche) + "\n";
        sParametres += "Number of disjunctions allowed in the right-hand side: " + String.valueOf(m_iNombreDisjonctionsDroite) + "\n";
        sParametres += "Minimal support for the additional interval: " + String.valueOf(m_fMinSuppDisjonctions) + "\n";
       
        return sParametres;
    }
}
