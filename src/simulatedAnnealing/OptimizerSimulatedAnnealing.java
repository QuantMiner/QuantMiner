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

import src.apriori.*;
import src.database.*;
import src.graphicalInterface.*;
import src.solver.*;



public class OptimizerSimulatedAnnealing extends RuleOptimizer {

    SimulatedAnnealingAlgo m_algoRecuitSimule = null;
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;
    SimulatedAnnealingParameters m_parametresAlgo = null;
    
    
    // Tableaux r�pertoriant l'�volution de la qualit� d'une r�gle au fur et � mesure de son optimisation :
    public float [] m_tQualiteMoyenne = null;
    public float [] m_tQualiteMin = null;
    public float [] m_tQualiteMax = null;
    private int m_iNombreEtapesCalculRegle = 0;
    
    // METTRE CETTE VARIABLE A VRAI POUR AFFICHER UN GRAPHE D'EVOLUTION DE LA QUALITE APRES L'OPTIMISATION D'UNE REGLE :
    static boolean m_bAfficherGrapheQualite = false;   
    
    
    
    public OptimizerSimulatedAnnealing() {
        m_algoRecuitSimule = null;
    }
    
    
    

    public void DefinirContexteResolution(ResolutionContext contexteResolution) {
        super.DefinirContexteResolution(contexteResolution);
        
        if (super.m_contexteResolution == null) {
            m_algoRecuitSimule = null;
            return;
        }
        
        m_parametresReglesQuantitatives = super.m_contexteResolution.m_parametresReglesQuantitatives;
        m_parametresAlgo = super.m_contexteResolution.m_parametresTechRecuitSimule;
        m_algoRecuitSimule = new SimulatedAnnealingAlgo(super.m_contexteResolution.m_gestionnaireBD, m_parametresAlgo.m_iNombreIterations, m_parametresAlgo.m_iNombreSolutionsParalleles);
        m_algoRecuitSimule.SpecifierParametresStatistiques(m_parametresReglesQuantitatives.m_fMinSupp, m_parametresReglesQuantitatives.m_fMinConf, m_parametresReglesQuantitatives.m_fMinSuppDisjonctions);

        if (m_bAfficherGrapheQualite) {
            m_iNombreEtapesCalculRegle = m_parametresAlgo.m_iNombreIterations;
            m_tQualiteMoyenne = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMin = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMax = new float [m_iNombreEtapesCalculRegle];
        }

    }
    
    
    
    public boolean OptimiseRegle(AssociationRule regle) {
        int iNombreItemsQuantitatifs = 0;
        int iIndiceEtape = 0;
        boolean bRegleEstSolide = false;
        AssociationRule meilleureRegle = null;
        
        if ( (m_algoRecuitSimule == null) || (regle == null) )
            return false;

        iNombreItemsQuantitatifs =    regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF)
                                    + regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
        
        // Si la r�gle est uniquement qualitative, on ne cherche pas � l'optimiser :
        if (iNombreItemsQuantitatifs <= 0) {
            
            regle.EvaluerSiQualitative(super.m_contexteResolution);
            
            return (  (regle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
                    &&(regle.m_fConfiance >= m_parametresReglesQuantitatives.m_fMinConf)  );
            
        }
        
   
        // Calcul de la r�gle optimis�e, sur le sch�ma courant :

        // On indique � l'algorithme la forme de la r�gle qu'il doit optimiser :
        m_algoRecuitSimule.SpecifierSchemaRegle(regle);

        m_algoRecuitSimule.GenererReglesPotentiellesInitiales();

        do {
            m_algoRecuitSimule.InitialiserRecuitSimulePourNouvellePasse();
            
            for (iIndiceEtape=0; iIndiceEtape<m_parametresAlgo.m_iNombreIterations; iIndiceEtape++) {
                m_algoRecuitSimule.NouvelleEtape();
            
                if (m_bAfficherGrapheQualite) {
                    m_tQualiteMoyenne[iIndiceEtape] = m_algoRecuitSimule.CalculerQualiteMoyenne();
                    m_tQualiteMin[iIndiceEtape] = m_algoRecuitSimule.ObtenirPireQualiteCourante();
                    m_tQualiteMax[iIndiceEtape] = m_algoRecuitSimule.ObtenirMeilleureQualiteCourante();
                }
            }
            
        }
        while ( m_algoRecuitSimule.InitierNouvellePasse() );

        
        meilleureRegle = m_algoRecuitSimule.ObtenirMeilleureRegle();

        if (meilleureRegle != null) {
            bRegleEstSolide = (  (meilleureRegle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
                               &&(meilleureRegle.m_fConfiance >= m_parametresReglesQuantitatives.m_fMinConf)  );
            if (bRegleEstSolide)
                regle.CopierRegleAssociation(meilleureRegle);            
        }
        else 
            bRegleEstSolide = false;
        
        
        if (m_bAfficherGrapheQualite) {
            DialogGraphQuality fenetreDetailsRegle = null;
            fenetreDetailsRegle = new DialogGraphQuality(super.m_contexteResolution.m_fenetreProprietaire, true, super.m_contexteResolution);
            fenetreDetailsRegle.SpecifierQualitesMoyennes(m_tQualiteMoyenne);
            fenetreDetailsRegle.SpecifierQualitesMax(m_tQualiteMax);
            fenetreDetailsRegle.SpecifierQualitesMin(m_tQualiteMin);
            fenetreDetailsRegle.ConstruireGraphe();
            fenetreDetailsRegle.show();
        }
        
        
        return bRegleEstSolide;
    }
    
}
