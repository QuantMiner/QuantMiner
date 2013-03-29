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
package src.graphicalInterface;

import src.solver.*;



public abstract class PanelBaseParam extends javax.swing.JPanel {
    
    protected ResolutionContext m_contexteResolution = null;

    
    /** Creates new form PanneauBase */
    public PanelBaseParam(ResolutionContext contexteResolution) {
        m_contexteResolution = contexteResolution;
    }
    
    
    // Request l'enregistrement des param�tres dans l'objet de context 
    public abstract boolean EnregistrerParametres();
    
}
