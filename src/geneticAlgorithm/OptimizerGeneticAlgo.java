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

import java.io.*;

import src.apriori.*;
import src.database.*;
import src.graphicalInterface.*;
import src.solver.*;


public class OptimizerGeneticAlgo extends RuleOptimizer {
    
    GeneticAlgo m_algoGenetique = null;
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;
    ParametersGeneticAlgo m_parametresAlgo = null;
    
    // Tableaux r�pertoriant l'�volution de la qualit� d'une r�gle au fur et � mesure de son optimisation :
    public float [] m_tQualiteMoyenne = null;  //QualiteMean
    public float [] m_tQualiteMin = null;
    public float [] m_tQualiteMax = null;
    private int m_iNombreEtapesCalculRegle = 0;
    
    // METTRE CETTE VARIABLE A VRAI POUR AFFICHER UN GRAPHE D'EVOLUTION DE LA QUALITE APRES L'OPTIMISATION D'UNE REGLE :
    static boolean m_bAfficherGrapheQualite = false;
    
    static boolean m_bSortirQualite = true;
    
    static int m_iRules=0;
    
    public OptimizerGeneticAlgo() {
        m_algoGenetique = null;
    }
    
    
    
    // Outrepassement de la fonction de sp�cification du contexte :
    public void DefinirContexteResolution(ResolutionContext contexteResolution) {
        super.DefinirContexteResolution(contexteResolution);
        
        if (super.m_contexteResolution == null) {
            m_algoGenetique = null;
            return;
        }
        
        m_parametresReglesQuantitatives = super.m_contexteResolution.m_parametresReglesQuantitatives;
        m_parametresAlgo = super.m_contexteResolution.m_parametresTechAlgoGenetique;
        m_algoGenetique = new GeneticAlgo(m_parametresAlgo.m_iTaillePopulation, super.m_contexteResolution.m_gestionnaireBD);
        m_algoGenetique.SpecifierParametresStatistiques(m_parametresReglesQuantitatives.m_fMinSupp, m_parametresReglesQuantitatives.m_fMinConf, m_parametresReglesQuantitatives.m_fMinSuppDisjonctions);
        m_algoGenetique.SpecifierParametresGenetiques(m_parametresAlgo.m_fPourcentageCroisement, m_parametresAlgo.m_fPourcentageMutation);
        
        
        m_iRules=0;
        
        if (m_bAfficherGrapheQualite || m_bSortirQualite) {
            m_iNombreEtapesCalculRegle = m_parametresAlgo.m_iNombreGenerations;
            m_tQualiteMoyenne = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMin = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMax = new float [m_iNombreEtapesCalculRegle];
        }
        
        
    }
    
    
    
    /**Optimize Rule Association
     * @param regle the Association rule
     */
    public boolean OptimiseRegle(AssociationRule regle) {
        long currentTime=System.currentTimeMillis();
        int iNombreItemsQuantitatifs = 0;
        int iIndiceEvolution = 0;
        boolean bRegleEstSolide = false;
        AssociationRule meilleureRegle = null;
        
        if ( (m_algoGenetique == null) || (regle == null) )
            return false;
        
        iNombreItemsQuantitatifs =    regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF)
        + regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
        
        // if the rule has uniquely qualitative, no need to optimize:
        if (iNombreItemsQuantitatifs <= 0) {
            
            regle.EvaluerSiQualitative(super.m_contexteResolution);
            
            return (  (regle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
            &&(regle.m_fConfiance >= m_parametresReglesQuantitatives.m_fMinConf)  );
            
        }
        
        
        // Calcul de la r�gle optimis�e, sur le sch�ma courant :
        
        // Indicate algorithm genetic the template of the rule to optimize :
        m_algoGenetique.SpecifierSchemaRegle(regle);
        m_algoGenetique.GenererReglesPotentiellesInitiales();
        
        do {
            for (iIndiceEvolution = 0; iIndiceEvolution < m_parametresAlgo.m_iNombreGenerations; iIndiceEvolution++) {
                m_algoGenetique.Evoluer();
                
                if (m_bAfficherGrapheQualite  || m_bSortirQualite ) {
                    m_tQualiteMoyenne[iIndiceEvolution] = m_algoGenetique.CalculerQualiteMoyenne();
                    m_tQualiteMin[iIndiceEvolution] = m_algoGenetique.ObtenirPireQualiteCourante();
                    m_tQualiteMax[iIndiceEvolution] = m_algoGenetique.ObtenirMeilleureQualiteCourante();
                }
            }
        }
        while ( m_algoGenetique.InitierNouvellePasse() );
        
        //obtain the best rule
        meilleureRegle = m_algoGenetique.ObtenirMeilleureRegle();

        //if the rule is not null and have enough support and confidence, copy it to rule
        if (meilleureRegle != null) {
            bRegleEstSolide = ((meilleureRegle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
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
        
       /* if ( m_bSortirQualite){
            File outputFile = new File("E:\\temp\\rules\\rule"+Integer.toString(m_iRules++)+".txt");
            outputFile.delete();
            System.out.println(outputFile);
            try{
                FileWriter out = new FileWriter(outputFile);
                System.out.println(out);
                out.write("Algo genetiques\n");
                out.write("\n - Nb quantitatifs :"+ Integer.toString(iNombreItemsQuantitatifs)+"\n");
                out.write("\n - Generations :"+ Integer.toString( (int)(m_parametresAlgo.m_iNombreGenerations))+"\n");
                out.write("\n - Population :"+ Integer.toString( (int)(m_parametresAlgo.m_iTaillePopulation))+"\n");
                out.write("\n - Taux Croisement :"+ Float.toString((float)(m_parametresAlgo.m_fPourcentageCroisement))+"\n");
                out.write("\n - Taux Mutation :"+ Float.toString((m_parametresAlgo.m_fPourcentageMutation))+"\n");
                out.write("\n - MinSupp :"+ Float.toString((m_parametresReglesQuantitatives.m_fMinSupp))+"\n");
                out.write("\n - MinConf :"+ Float.toString((m_parametresReglesQuantitatives.m_fMinConf))+"\n");
                out.write("\n - Temps (s) :"+ Integer.toString( (int)(System.currentTimeMillis()-currentTime))+"\n");
                out.write("\n - Temps (ms):"+ Integer.toString( (int)(System.currentTimeMillis()-currentTime)/1000)+"\n");
                out.write("\n");
                out.write("\n - Regle :\n\n");
                out.write("---------------------------------------------------------------\n");
                out.write(regle.toString());
                out.write("\n---------------------------------------------------------------\n");
                out.write("\n");
                out.write("generation,moy,max,min\n");
                for (int i=0; i<m_tQualiteMoyenne.length; i++){
                    out.write(Integer.toString(i));
                    out.write(',');
                    out.write(Float.toString(m_tQualiteMoyenne[i]));
                    out.write(',');
                    out.write(Float.toString(m_tQualiteMax[i]));
                    out.write(',');
                    out.write(Float.toString(m_tQualiteMin[i]));
                    out.write("\n");
                }
                out.close();
            } catch(java.io.IOException e){
                e.printStackTrace();
            }
        }
        */
        
        
        return bRegleEstSolide;
    }
    
}
