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

import java.util.*;

import src.apriori.*;
import src.database.*;
import src.graphicalInterface.*;
import src.solver.*;



public class SimulatedAnnealingAlgo extends EvaluationBaseAlgorithm {
    
    private ReglePotentielle [] m_tReglesPotentiellesPrecedentes = null;
    private float m_fTemperature = 0.0f;
    private int m_iNombreEtapes = 0;
    
    
    
    public SimulatedAnnealingAlgo(DatabaseAdmin gestionBD, int iNombreEtapes, int iNombreReglesParalleles) {
        super(iNombreReglesParalleles, gestionBD);
        
        m_iNombreEtapes = iNombreEtapes;
    }

    
    
    public void GenererReglesPotentiellesInitiales() {
        super.GenererReglesPotentiellesInitiales();

        m_tReglesPotentiellesPrecedentes = new ReglePotentielle [m_iNombreReglesPotentielles];
        
        InitialiserRecuitSimulePourNouvellePasse();
    }
    
    
    
    public void InitialiserRecuitSimulePourNouvellePasse() {
        int iIndiceReglePotentielle = 0;  
        
        // M�morisation de la solution courante :
        for (iIndiceReglePotentielle=0; iIndiceReglePotentielle<m_iNombreReglesPotentielles; iIndiceReglePotentielle++) {
            m_tReglesPotentiellesPrecedentes[iIndiceReglePotentielle] = new ReglePotentielle(m_iDimension, m_iNombreTotalIntervalles);
            m_tReglesPotentiellesPrecedentes[iIndiceReglePotentielle].Copier( m_tReglesPotentielles[iIndiceReglePotentielle] ); 
        }
        
        m_fTemperature = 1.0f;
    }
        
    
    
    public void NouvelleEtape() {
        int iIndiceDimension = 0;
        int iIndiceDisjonction = 0; 
        int iNombreValeursDomaine = 0;
        int iIndiceValeurDomaineMin, iIndiceValeurDomaineMax = 0;
        int iIndiceReglePotentielle = 0;
        int iNombreDisjonctions = 0;
        int iIndiceIntervalle = 0;
        DataColumn colonneDonnees = null;
        ReglePotentielle reglePotentielle = null;
        float fAmplitude = 0.0f;
        
        for (iIndiceReglePotentielle=0; iIndiceReglePotentielle<m_iNombreReglesPotentielles; iIndiceReglePotentielle++) {

            m_tReglesPotentielles[iIndiceReglePotentielle].Copier( m_tReglesPotentiellesPrecedentes[iIndiceReglePotentielle] );
            reglePotentielle = m_tReglesPotentielles[iIndiceReglePotentielle];
        

            float fQualiteMax = m_iNombreTransactions - m_fMinConf * (float)reglePotentielle.m_iSupportCond;

            int i = m_iNombreTransactions - (int)(m_fMinConf*(float)m_iNombreTransactions);

    /*        if (reglePotentielle.m_iSupportRegle > (int)(m_fMinConf*(float)reglePotentielle.m_iSupportCond))
                fAmplitude = ((float)iNombreValeursDomaine) * 0.2f * (1.0f - ( ((float)Math.abs(reglePotentielle.m_iSupportRegle - (int)(m_fMinConf*(float)reglePotentielle.m_iSupportCond))) / ((float)i) ));
            else
                fAmplitude = ((float)iNombreValeursDomaine) * 0.2f + 0.8f * (1.0f - ((float)reglePotentielle.m_iSupportRegle) / ((float)m_iNombreTransactions) );
    */


            // Si la confiance est �lev�e, on tente d'augmenter le support en augmentant la taille
            // des intervalles 
        



            if (reglePotentielle.m_iSupportRegle > (int)(m_fMinSupp*(float)iNombreValeursDomaine)) {
                if (reglePotentielle.m_iSupportRegle > (int)(m_fMinConf*(float)reglePotentielle.m_iSupportCond))
                    fAmplitude = 0.2f * (1.0f - ( ((float)Math.abs(reglePotentielle.m_iSupportRegle - (int)(m_fMinConf*(float)reglePotentielle.m_iSupportCond))) / ((float)i) ));
                else
                    fAmplitude = 0.2f + 0.8f * (1.0f - ((float)reglePotentielle.m_iSupportRegle) / ((float)m_iNombreTransactions) );
            }
            else
                fAmplitude = 0.2f + 0.8f * (1.0f - ((float)reglePotentielle.m_iSupportRegle) / ((float)m_iNombreTransactions) );

        
        
            fAmplitude = m_fTemperature;

            
            int iNombreDimensionsEffectives = 0;
     /*       
            if (m_bPrendreEnCompteQuantitatifsGauche && m_bPrendreEnCompteQuantitatifsDroite)
                iNombreDimensionsEffectives = m_iDimension;
            else if (m_bPrendreEnCompteQuantitatifsGauche)
                iNombreDimensionsEffectives = m_iNombreItemsQuantCond;                
            else
                iNombreDimensionsEffectives = m_iNombreItemsQuantObj;
            
            iIndiceDimension = (int)( m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] * ((float)iNombreDimensionsEffectives) );
            
            if (m_bPrendreEnCompteQuantitatifsDroite && (!m_bPrendreEnCompteQuantitatifsGauche) )
                iIndiceDimension += m_iNombreItemsQuantCond;
       */         
            
            for (iIndiceDimension=0;iIndiceDimension<m_iDimension;iIndiceDimension++) {
                
                if (  ( m_bPrendreEnCompteQuantitatifsGauche && (iIndiceDimension<m_iNombreItemsQuantCond) )
                    ||( m_bPrendreEnCompteQuantitatifsDroite && (iIndiceDimension>=m_iNombreItemsQuantCond) )  ) {
                    
                    if (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] > 0.5f) {

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

                        iIndiceValeurDomaineMin = reglePotentielle.m_tIndiceMin[iIndiceIntervalle];
                        iIndiceValeurDomaineMax = reglePotentielle.m_tIndiceMax[iIndiceIntervalle];
                        iIndiceValeurDomaineMin += (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] - 0.5f) * fAmplitude*((float)iNombreValeursDomaine));
                        iIndiceValeurDomaineMax += (int)( (m_tRandomFloat[(int)((m_compteurRandomFloat++)&0xFFFF)] - 0.5f) * fAmplitude*((float)iNombreValeursDomaine));

                        VerifierEtAffecterBornesReglePotentielle(reglePotentielle, iIndiceDimension, iIndiceDisjonction, iIndiceValeurDomaineMin, iIndiceValeurDomaineMax);

                    }
                }

            }
            
            m_tReglesPotentiellesAEvaluer[m_iNombreReglesPotentiellesAEvaluer] = reglePotentielle;
            m_iNombreReglesPotentiellesAEvaluer++;

        }
        
        
        super.EvaluerReglesPotentielles();
        

        for (iIndiceReglePotentielle=0; iIndiceReglePotentielle<m_iNombreReglesPotentielles; iIndiceReglePotentielle++) {
            
            reglePotentielle = m_tReglesPotentielles[iIndiceReglePotentielle];
            if (reglePotentielle.m_fQualite >= m_tReglesPotentiellesPrecedentes[iIndiceReglePotentielle].m_fQualite)
                m_tReglesPotentiellesPrecedentes[iIndiceReglePotentielle].Copier(reglePotentielle);

            if (reglePotentielle.m_fQualite >= m_meilleureReglePotentielle.m_fQualite)
                m_meilleureReglePotentielle.Copier(reglePotentielle);
            
        }
        
        m_fTemperature -= 1.0f / (float)m_iNombreEtapes;
    }
     
}
