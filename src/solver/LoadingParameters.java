/*                                             
 *Copyright 2007, 2011 CCLS Columbia University (USA), LIFO University of Orleans (France), BRGM (France)
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
package src.solver;


// Classe repertoriant l'ensemble des parametres definis par l'utilisateur pour le prochain
// chargement d'un fichier de regles.
// Class enumerating all the parameters defined by the user to be used for the next loading of a rule file.

public class LoadingParameters {
    
    public String m_sNomFichier = null;
    public String m_sNomUtilisateurOrigine = null;
    public String m_sNomBaseOrigine = null;
    public String m_sDateOrigine = null;
    public String m_sDescriptionRegles = null;
    public String m_sDescriptionCompleteContexte = null;  // Description complete des parametres et autres infos liees au contexte d'extraction des regles
 
    
    
    public LoadingParameters() {
        m_sNomFichier = null;
        m_sNomUtilisateurOrigine = "User unknown";
        m_sNomBaseOrigine = "Database unknown";
        m_sDateOrigine = "Date unknown";
        m_sDescriptionRegles = "Missing Description";
        m_sDescriptionCompleteContexte = "No information.";
    }
    
    
    public String toString() {
        return m_sDescriptionCompleteContexte;
    }
        
}
