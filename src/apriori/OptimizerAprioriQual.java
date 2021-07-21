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


import src.apriori.*;
import src.solver.*;



public class OptimizerAprioriQual extends RuleOptimizer {

    StandardParameters m_parametresRegles = null;
    
    
    public OptimizerAprioriQual() {
        m_parametresRegles = null;
    }
    
    
    
    // Outrepassement de la fonction de sp�cification du contexte :
    public void DefinirContexteResolution(ResolutionContext contexteResolution) {
        super.DefinirContexteResolution(contexteResolution);
        
        if (super.m_contexteResolution == null) {
            m_parametresRegles = null;
            return;
        }
        
        m_parametresRegles = super.m_contexteResolution.m_parametresRegles;
    }
    
    
    
    // Outrepassement de la fonction d'optimisation :
    public boolean OptimiseRegle(AssociationRule regle) {
        int iNombreItemsQuantitatifs = 0;
       
        if (regle == null)
            return false;

        iNombreItemsQuantitatifs =    regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF)
                                    + regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);

        if (iNombreItemsQuantitatifs > 0)
            return false;
        
        // Calcul des support et confiance de la r�gle qualitative :
        regle.EvaluerSiQualitative(super.m_contexteResolution);
        
        return (  (regle.m_fSupport >= m_parametresRegles.m_fMinSupp)
                &&(regle.m_fConfiance >= m_parametresRegles.m_fMinConf)  );
    }
    
}
