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

import java.util.*;

import src.apriori.*;
import src.database.*;
import src.solver.*;



public class GeneticAlgo extends EvaluationBaseAlgorithm {
    
    private float m_fTauxCroisement = 0.0f;  //crossover rate
    private float m_fTauxMutation = 0.0f;    //mutation rate
   
    // Structures de donn�es pour l'optimisation des calculs :

    private short [] m_tIndicesAleatoiresCroisements = null;    // Indices d'individus � croiser, d'apr�s un tir al�atoire dans un espace o� chaque individu de rang n a une chance de plus d'etre tir� qu'un �l�ment de taille n-1
    private short m_compteurIndicesAleatoiresCroisements = 0;

    
    
    private void CalculerTableIndicesTirages() {
        int iTable = 0;
        int iTirage = 0;
        int iPlageRoulette;
        int iPlageRestante = 0;
        int iIndiceReglePotentielle = 0;
            
        m_tIndicesAleatoiresCroisements = new short [16384];
        
        iPlageRoulette = (m_iNombreReglesPotentielles * (m_iNombreReglesPotentielles+1)) / 2;
        for (iTable=0; iTable<16384; iTable++) {
            iTirage = (int)(java.lang.Math.random() * (double)iPlageRoulette);
            
            // On d�termine l'indice de l'individu correspondant au r�sultat du tir al�atoire sur la plage 
            iPlageRestante = iPlageRoulette - m_iNombreReglesPotentielles;
            iIndiceReglePotentielle = m_iNombreReglesPotentielles - 1;
            while (iTirage<iPlageRestante) {
                iPlageRestante -= iIndiceReglePotentielle;
                iIndiceReglePotentielle--;
            }
            
            m_tIndicesAleatoiresCroisements[iTable] = (short)iIndiceReglePotentielle;
        }
        
        m_compteurIndicesAleatoiresCroisements = 0;
    }    
    
    
    /**Genetics Algorithm
     * @param iNombreIndividus number of individuals
     * @param gestionBD GestionnaireBaseDeDonnees obj
     */
    public GeneticAlgo(int iNombreIndividus, DatabaseAdmin gestionBD) {
        super(iNombreIndividus, gestionBD);
        
        m_fTauxCroisement = 0.0f;
        m_fTauxMutation = 0.0f;
        
        CalculerTableIndicesTirages();
    }

    
    
    /**Set Genetic parameters
     * @param fTauxCroisement Crossover rate
     * @param fTauxMutation Mutation rate
     */
    public void SpecifierParametresGenetiques(float fTauxCroisement, float fTauxMutation) {
        m_fTauxCroisement = fTauxCroisement;
        m_fTauxMutation = fTauxMutation;
    }
    
    
    /**Do evolution*/
    public void Evoluer() {
        int iIndiceReglePotentielle = 0;
        int iIndiceReglePotentiellePere = 0;
        int iIndiceReglePotentielleMere = 0;
        int iNombreReglesPotentiellesRemplacees = 0;
        int iPlageRoulette = 0;
        ReglePotentielle reglePotentielleEvolue = null;
        
        //The number of potential rules to be replaced
        iNombreReglesPotentiellesRemplacees = (int)((float)m_iNombreReglesPotentielles * m_fTauxCroisement);
        m_iNombreReglesPotentiellesAEvaluer = 0;    
            
        for (iIndiceReglePotentielle = 0; iIndiceReglePotentielle < iNombreReglesPotentiellesRemplacees; iIndiceReglePotentielle++) {
            
            reglePotentielleEvolue = m_tReglesPotentielles[iIndiceReglePotentielle];
            
            //Get father and mother rules
            iIndiceReglePotentiellePere = m_tIndicesAleatoiresCroisements[ (int)((m_compteurIndicesAleatoiresCroisements++)&0x3FFF) ];
            iIndiceReglePotentielleMere = m_tIndicesAleatoiresCroisements[ (int)((m_compteurIndicesAleatoiresCroisements++)&0x3FFF) ];
            // Ancienne technique de choix des parents :
            //iIndiceIndividuPere = iNombreIndividusRemplaces + (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)]) * ((float)(m_iNombreIndividus-iNombreIndividusRemplaces)) );
            //iIndiceIndividuMere = iNombreIndividusRemplaces + (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)]) * ((float)(m_iNombreIndividus-iNombreIndividusRemplaces)) );
            
            //Do crossover
            EffectuerCroisement(reglePotentielleEvolue, m_tReglesPotentielles[iIndiceReglePotentiellePere], m_tReglesPotentielles[iIndiceReglePotentielleMere]);
            
            //Do mutation
            if ( m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] <= m_fTauxMutation)
                EffectuerMutation(reglePotentielleEvolue);            

            m_tReglesPotentiellesAEvaluer[m_iNombreReglesPotentiellesAEvaluer] = reglePotentielleEvolue;
            m_iNombreReglesPotentiellesAEvaluer++;
        }

        //The rest potential rules that haven't been replaced by crossover
        for (iIndiceReglePotentielle = iNombreReglesPotentiellesRemplacees; iIndiceReglePotentielle < m_iNombreReglesPotentielles; iIndiceReglePotentielle++)
            
        	//Do mutation
            if ( m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] <= m_fTauxMutation) {
                reglePotentielleEvolue = m_tReglesPotentielles[iIndiceReglePotentielle];
                EffectuerMutation(reglePotentielleEvolue);
                m_tReglesPotentiellesAEvaluer[m_iNombreReglesPotentiellesAEvaluer] = reglePotentielleEvolue;
                m_iNombreReglesPotentiellesAEvaluer++;
            }

        super.EvaluerReglesPotentielles();
    }
    
    
    /** Perform Mutation
     * @param reglePotentielle Potential rules
     */
    public void EffectuerMutation(ReglePotentielle reglePotentielle) {
        int iIndiceDimension = 0;
        int iIndiceDisjonction = 0;
        int iNombreValeursDomaine = 0;
        int iIndiceValeurDomaineMin, iIndiceValeurDomaineMax = 0;
        int iIndiceIntervalle = 0;
        DataColumn colonneDonnees = null;
       
        for (iIndiceDimension=0;iIndiceDimension<m_iDimension;iIndiceDimension++) {
            
            if (  ( m_bPrendreEnCompteQuantitatifsGauche && (iIndiceDimension<m_iNombreItemsQuantCond) )
                ||( m_bPrendreEnCompteQuantitatifsDroite && (iIndiceDimension>=m_iNombreItemsQuantCond) )  ) {
                
                if (iIndiceDimension<m_iNombreItemsQuantCond) {
                    colonneDonnees = m_tItemsQuantCond[ iIndiceDimension ].m_colonneDonnees;
                    iIndiceIntervalle = iIndiceDimension * m_schemaRegleOptimale.m_iNombreDisjonctionsGauche + m_iDisjonctionGaucheCourante;
                    iIndiceDisjonction = m_iDisjonctionGaucheCourante; 
                }
                else {
                    colonneDonnees = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees;
                    iIndiceIntervalle = m_iDebutIntervallesDroite + ((iIndiceDimension-m_iNombreItemsQuantCond)*m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) + m_iDisjonctionDroiteCourante;
                    iIndiceDisjonction = m_iDisjonctionDroiteCourante; 
                }

                iNombreValeursDomaine = colonneDonnees.m_iNombreValeursReellesCorrectes;

                if ( m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] > 0.5f) {

                    iIndiceValeurDomaineMin = reglePotentielle.m_tIndiceMin[iIndiceIntervalle];
                    iIndiceValeurDomaineMax = reglePotentielle.m_tIndiceMax[iIndiceIntervalle];
                    iIndiceValeurDomaineMin += (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] - 0.4f) * ((float)iNombreValeursDomaine) * 0.05f);
                    iIndiceValeurDomaineMax += (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] - 0.6f) * ((float)iNombreValeursDomaine) * 0.05f);

                    VerifierEtAffecterBornesReglePotentielle(reglePotentielle, iIndiceDimension, iIndiceDisjonction, iIndiceValeurDomaineMin, iIndiceValeurDomaineMax);
                }
            
            }

        }
    }
    
    
    /**Create a new rule potentialRuleChild, child of the crossover of a father rule and a mother rule.
     * @param reglePotentielleFille potentialRuleChild
     * @param reglePotentiellePere potentialRuleFather
     * @param reglePotentielleMere potentialRuleMother
     */
    public void EffectuerCroisement(ReglePotentielle reglePotentielleFille, ReglePotentielle reglePotentiellePere, ReglePotentielle reglePotentielleMere) {
        int iIndiceDimension = 0;
        int iIndiceIntervalle = 0;
        int iIndiceDisjonction = 0;
        int iResultatRoulette = 0;
        
        for (iIndiceDimension=0;iIndiceDimension<m_iDimension;iIndiceDimension++) {
            
            if (  ( m_bPrendreEnCompteQuantitatifsGauche && (iIndiceDimension<m_iNombreItemsQuantCond) )
                ||( m_bPrendreEnCompteQuantitatifsDroite && (iIndiceDimension>=m_iNombreItemsQuantCond) )  ) {

                if (iIndiceDimension<m_iNombreItemsQuantCond) {
                    iIndiceIntervalle = (iIndiceDimension*m_schemaRegleOptimale.m_iNombreDisjonctionsGauche) + m_iDisjonctionGaucheCourante;
                    iIndiceDisjonction = m_iDisjonctionGaucheCourante;
                }
                else {
                    iIndiceIntervalle = m_iDebutIntervallesDroite + ((iIndiceDimension-m_iNombreItemsQuantCond)*m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) + m_iDisjonctionDroiteCourante;
                    iIndiceDisjonction = m_iDisjonctionDroiteCourante;
                }
                
                iResultatRoulette = (int)(8.0f * m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)]);

                if (iResultatRoulette == 0)
                    VerifierEtAffecterBornesReglePotentielle(reglePotentielleFille, iIndiceDimension, iIndiceDisjonction,
                                                             reglePotentiellePere.m_tIndiceMin[iIndiceIntervalle], 
                                                             reglePotentiellePere.m_tIndiceMax[iIndiceIntervalle]);
                else if (iResultatRoulette == 1)
                    VerifierEtAffecterBornesReglePotentielle(reglePotentielleFille, iIndiceDimension, iIndiceDisjonction,
                                                             reglePotentielleMere.m_tIndiceMin[iIndiceIntervalle], 
                                                             reglePotentielleMere.m_tIndiceMax[iIndiceIntervalle]);
                else if (iResultatRoulette <= 4)
                    VerifierEtAffecterBornesReglePotentielle(reglePotentielleFille, iIndiceDimension, iIndiceDisjonction,
                                                             reglePotentiellePere.m_tIndiceMin[iIndiceIntervalle], 
                                                             reglePotentielleMere.m_tIndiceMax[iIndiceIntervalle]);
                else
                    VerifierEtAffecterBornesReglePotentielle(reglePotentielleFille, iIndiceDimension, iIndiceDisjonction,
                                                             reglePotentielleMere.m_tIndiceMin[iIndiceIntervalle], 
                                                             reglePotentiellePere.m_tIndiceMax[iIndiceIntervalle]);
            }
        }
    }

}
