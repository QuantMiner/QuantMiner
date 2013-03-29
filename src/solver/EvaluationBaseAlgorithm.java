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
package src.solver;


import java.util.*;

import src.apriori.*;
import src.database.*;



public abstract class EvaluationBaseAlgorithm {
    
    
    protected class RefItemQualitatif {
        public DataColumn m_colonneDonnees;  // Colonne de la BD o� sont stock�es les valeurs de l'attribut en m�moire 
        public short m_iIndiceValeurAttribut;    // Identifiant de la sous-valeur de l'attribut qui �quivaut � l'item
        public String m_sChaineIdentifiantItem;
        
        public RefItemQualitatif(DataColumn colonneDonnees, short iIndiceValeurAttribut, String sChaineIdentifiant) {
            m_colonneDonnees = colonneDonnees;
            m_iIndiceValeurAttribut = iIndiceValeurAttribut;
            m_sChaineIdentifiantItem = sChaineIdentifiant;
        }
    }
    
    
    protected class RefItemQuantitatif {
        public DataColumn m_colonneDonnees = null;
        
        public RefItemQuantitatif(DataColumn colonneDonnees) {
            m_colonneDonnees = colonneDonnees;
        }
    }
    
    
    
    /**potential rules
     */
    protected class ReglePotentielle implements Comparable {

        public int m_iDimension = 0;
        public float m_tIntervalleMin [] = null;   // Bornes minimales des 'm_iDimension' intervalles
        public float m_tIntervalleMax [] = null;   // Bornes maximales des 'm_iDimension' intervalles
        public int m_tIndiceMin [] = null;       // Indices des bornes min parmi l'ensemble des valeurs de l'attribut dans la BD
        public int m_tIndiceMax [] = null;       // Indices des bornes max parmi l'ensemble des valeurs de l'attribut dans la BD
        public float m_fQualite = 0.0f;
        public int m_iSupportRegle = 0;
        public int m_iSupportCond = 0;
        public int m_iNombreTotalIntervalles = 0;
        

        
        public ReglePotentielle(int iDimension, int iNombreTotalIntervalles) {
            int iIndiceDimension = 0;
           
            m_iDimension = iDimension;
            m_iNombreTotalIntervalles = iNombreTotalIntervalles;
                   
            m_tIntervalleMin = new float [m_iNombreTotalIntervalles];
            m_tIntervalleMax = new float [m_iNombreTotalIntervalles];
            m_tIndiceMin = new int [m_iNombreTotalIntervalles];
            m_tIndiceMax = new int [m_iNombreTotalIntervalles];

            m_fQualite = 0.0f;
            m_iSupportRegle = 0;
            m_iSupportCond = 0;
        }


        
        public int compareTo(Object o) {
            if ( this.m_fQualite < ((ReglePotentielle)o).m_fQualite )
                return -1;
            else if ( this.m_fQualite == ((ReglePotentielle)o).m_fQualite )
                return 0;
            else
                return 1;
        }



        public void Copier(ReglePotentielle reglePotentielle) {
            int iIndiceIntervalle = 0;
            
            if (reglePotentielle == null)
                return;

            if (this.m_iDimension != reglePotentielle.m_iDimension)
                return;

            
            for (iIndiceIntervalle=0; iIndiceIntervalle<m_iNombreTotalIntervalles; iIndiceIntervalle++) {
                this.m_tIntervalleMin[iIndiceIntervalle] = reglePotentielle.m_tIntervalleMin[iIndiceIntervalle];
                this.m_tIntervalleMax[iIndiceIntervalle] = reglePotentielle.m_tIntervalleMax[iIndiceIntervalle];
                this.m_tIndiceMin[iIndiceIntervalle] = reglePotentielle.m_tIndiceMin[iIndiceIntervalle];
                this.m_tIndiceMax[iIndiceIntervalle] = reglePotentielle.m_tIndiceMax[iIndiceIntervalle];            
            }

            this.m_fQualite = reglePotentielle.m_fQualite;
            this.m_iSupportRegle = reglePotentielle.m_iSupportRegle;
            this.m_iSupportCond = reglePotentielle.m_iSupportCond;        
        }
        
    }
        
        

    

    // Repr�sentation du sch�ma de la r�gle � optimiser :
    
    // Constituants de la r�gle :
    protected RefItemQualitatif [] m_tItemsQualCond = null;    // Items qualificatifs � gauche de la r�gle
    protected RefItemQuantitatif [] m_tItemsQuantCond = null;    // Items quantitatifs � gauche de la r�gle
    protected RefItemQualitatif [] m_tItemsQualObj = null;     // Items qualificatifs � droite de la r�gle
    protected RefItemQuantitatif [] m_tItemsQuantObj = null;     // Items quantitatifs � droite de la r�gle

    // Nombre d'items de chaque types :
    protected int m_iNombreItemsQualCond, m_iNombreItemsQuantCond, m_iNombreItemsQualObj, m_iNombreItemsQuantObj;
    protected int m_iDimension = 0;
    protected int m_iNombreTotalItems;    
    
    protected int m_iNombreReglesPotentielles = 0;
    protected ReglePotentielle [] m_tReglesPotentielles = null;        //potential rules
    protected ReglePotentielle m_meilleureReglePotentielle = null;     //best potential rule (only one, maybe change it to an array)
    protected ReglePotentielle m_derniereReglePotentielleValide = null; //last valid potential rule
    protected DatabaseAdmin m_gestionBD = null;

    protected float m_fMinSupp = 0.0f;
    protected float m_fMinConf = 0.0f;
    protected float m_fMinSuppRegle = 0.0f; // Support minimal pour autoriser la r�gle (compl�te et sous sa premi�re forme sans disjonctions)
    protected float m_fMinSuppDisjonction = 0.0f; // Support minimal d'un sous-r�gle pour etre incorpor�e dans la r�gle finale
    
    protected int m_iNombreTransactions = 0;

    protected int m_iDebutIntervallesDroite = 0; // Indice du premier intervalle de la partie droite d'une r�gle potentielle
    protected int m_iNombreTotalIntervalles = 0; // Nombre total d'intervalles � optimiser (en tenant compte des disjonctions)
        
    protected int m_iDisjonctionGaucheCourante = 0;
    protected int m_iDisjonctionDroiteCourante = 0;
    protected int m_iNombreDisjonctionsGaucheValides = 0;
    protected int m_iNombreDisjonctionsDroiteValides = 0;
    protected boolean m_bPrendreEnCompteQuantitatifsGauche = false;
    protected boolean m_bPrendreEnCompteQuantitatifsDroite = false;
    protected int m_iSupportCumuleCond = 0;
    protected int m_iSupportCumuleRegle = 0;
    
    
    
    protected AssociationRule m_schemaRegleOptimale = null;    // R�gle que l'algorithme doit optimiser en trouvant les meilleures bornes pour chaque intervalle    
    
    
    // Structures de donn�es pour l'optimisation des calculs :
    
    static protected float [] m_tRandomFloat = null;  // Table de r�els compris entre 0.0f et 1.0f, tir�s al�atoirement
    static protected short m_compteurRandomFloat = 0;

    protected int [] m_tLignesAPrendreEnCompte = null; // Liste des lignes dans la BD � prendre en compte lors de l'�valuation, c'est-�dire celles qui sont couvertes par les items qualitatifs gauche du sch�ma de r�gle, et qui ne contiennent pas de valeur manquante pour les items quantitatifs pr�sents dans la r�gle
    protected boolean [] m_tPrendreEnCompteLigneDroite = null; // Tableau indiquant pour chaque ligne de la BD si ses items qualitatifs � droite sont couverts par le sch�ma de r�gle
    protected boolean [] m_tLignesCouvertesGauche = null; // Tableau indiquant pour chaque ligne de la BD si ses items (qualitatifs et quantitatifs fix�s) � gauche sont couverts par le sch�ma de r�gle
    protected boolean [] m_tLignesCouvertesDroite = null; // Tableau indiquant pour chaque ligne de la BD si ses items (qualitatifs et quantitatifs fix�s) � gauche sont couverts par le sch�ma de r�gle
    protected int m_iNombreLignesAPrendreEnCompte = 0;   // Nombre d'�l�ments � prendre en compte dans la liste 'm_tLignesCouvertesGauche' (la taille de ce tableau est initialis�e au nombre de lignes totales dans la BD) 
    
    protected ReglePotentielle [] m_tReglesPotentiellesAEvaluer = null; // All  des propositions de r�gles modifi�es et donc qui n�cessitent une r�-�valuation
    protected int m_iNombreReglesPotentiellesAEvaluer = 0;    //the number of potential rules to evaluate
    
    
    static void InitialiserValeursAleatoires() {
        int iIndiceValeur = 0;
        
        if (m_tRandomFloat != null)
            return;
        
        m_tRandomFloat = new float [65536];
        for (iIndiceValeur = 0; iIndiceValeur < 65536; iIndiceValeur++)
            m_tRandomFloat[iIndiceValeur] = (float)java.lang.Math.random();
        
        m_compteurRandomFloat = 0;
    }
    
    
    
    public EvaluationBaseAlgorithm(int iNombreReglesPotentielles, DatabaseAdmin gestionBD) {
        
        m_iNombreReglesPotentielles = iNombreReglesPotentielles;
        m_gestionBD = gestionBD;
        
        m_fMinSupp = 0.0f;
        m_fMinConf = 0.0f;
        m_iNombreTransactions = m_gestionBD.ObtenirNombreLignes();
        
        m_iNombreItemsQualCond = 0;
        m_iNombreItemsQuantCond = 0;
        m_iNombreItemsQualObj = 0;
        m_iNombreItemsQuantObj = 0;

        m_iDimension = 0;
        m_iNombreTotalItems = 0;
        m_schemaRegleOptimale = null;
        
        m_tReglesPotentielles = new ReglePotentielle [m_iNombreReglesPotentielles];
        m_meilleureReglePotentielle = null;
        m_derniereReglePotentielleValide = null;
        
        m_iNombreTotalIntervalles = 0;
        m_iDebutIntervallesDroite = 0;
        m_iNombreDisjonctionsGaucheValides = 0;
        m_iNombreDisjonctionsDroiteValides = 0;
        m_bPrendreEnCompteQuantitatifsGauche = false;
        m_bPrendreEnCompteQuantitatifsDroite = false;
        m_iSupportCumuleCond = 0;
        m_iSupportCumuleRegle = 0;
        
        InitialiserValeursAleatoires();
        
        m_tLignesAPrendreEnCompte = new int [m_iNombreTransactions];
        m_tPrendreEnCompteLigneDroite = new boolean [m_iNombreTransactions];
        m_tLignesCouvertesGauche = new boolean [m_iNombreTransactions];
        m_tLignesCouvertesDroite = new boolean [m_iNombreTransactions];
        m_iNombreLignesAPrendreEnCompte = 0;
        
        m_tReglesPotentiellesAEvaluer = new ReglePotentielle [m_iNombreReglesPotentielles];
        m_iNombreReglesPotentiellesAEvaluer = 0;
    }
       
      
    /**
     * Set statistic parameters
     * @param fMinSuppRegle Minimun support of a rule
     * @param fMinConf Minimun confidence of a rule
     * @param fMinSuppDisjonction minimun support disjunction
     */
    public void SpecifierParametresStatistiques(float fMinSuppRegle, float fMinConf, float fMinSuppDisjonction) {
        m_fMinSuppRegle = fMinSuppRegle;
        m_fMinSuppDisjonction = fMinSuppDisjonction;
        m_fMinSupp = m_fMinSuppRegle;
        m_fMinConf = fMinConf;
    }
    

        
    protected void VerifierEtAffecterBornesReglePotentielle(ReglePotentielle reglePotentielle, int iIndiceDimension, int iIndiceDisjonction, int iIndice1, int iIndice2) {
        DataColumn colonneDonnees = null;
        int iIndiceTemp = 0;
        int iIndiceMax = 0;
        int iIndiceIntervalle = 0;
        
        if (iIndiceDimension < m_iNombreItemsQuantCond) {
            colonneDonnees = m_tItemsQuantCond[ iIndiceDimension ].m_colonneDonnees;
            iIndiceIntervalle = (m_schemaRegleOptimale.m_iNombreDisjonctionsGauche*iIndiceDimension) + iIndiceDisjonction;
        }
        else {
            colonneDonnees = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees;
            iIndiceIntervalle = m_iDebutIntervallesDroite + ((iIndiceDimension-m_iNombreItemsQuantCond)*m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) + iIndiceDisjonction;
        }
        
        iIndiceMax = colonneDonnees.m_iNombreValeursReellesCorrectes - 1;
        
        // On s'assure du bon ordre des bornes :
        if (iIndice1 > iIndice2) {
            iIndiceTemp = iIndice1;
            iIndice1 = iIndice2;
            iIndice2 = iIndiceTemp;
        }

        // On v�rifie que les indices ne sortent pas des bornes autoris�es, sinon on s'y contraint :
        if (iIndice1 < 0) iIndice1 = 0;
        if (iIndice2 < 0) iIndice2 = 0;
        if (iIndice1 > iIndiceMax) iIndice1 = iIndiceMax;
        if (iIndice2 > iIndiceMax) iIndice2 = iIndiceMax;

        // Affectation des bornes dans la r�gle :
        reglePotentielle.m_tIndiceMin[iIndiceIntervalle] = iIndice1;
        reglePotentielle.m_tIndiceMax[iIndiceIntervalle] = iIndice2;
        reglePotentielle.m_tIntervalleMin[iIndiceIntervalle] = colonneDonnees.m_tValeursReellesTriees[iIndice1];
        reglePotentielle.m_tIntervalleMax[iIndiceIntervalle] = colonneDonnees.m_tValeursReellesTriees[iIndice2];
    }

        
        
        
    /**
     * Give an initial value to a potential rule
     * @param reglePotentielle
     * @param iIndiceReglePotentielle
     */
    protected void InitialiserReglePotentielle(ReglePotentielle reglePotentielle, int iIndiceReglePotentielle) {
        int iAmplitudeIntervalle = 0;
        int iIndiceDimension = 0;
        int iIndiceDisjonction = 0;
        int iNombreValeursDomaine = 0;
        int iNombreDisjonctions = 0;
        int iIndiceValeurDomaineMin, iIndiceValeurDomaineMax = 0;
        DataColumn colonneDonnees = null;
       
        // Lors des passes suppl�mentaires, on conserve les intervalles d�j� trait�s de la meilleure r�gle :
        if ( (!m_bPrendreEnCompteQuantitatifsGauche) || (!m_bPrendreEnCompteQuantitatifsDroite) )
            reglePotentielle.Copier(m_meilleureReglePotentielle);
        
        
        for (iIndiceDimension=0;iIndiceDimension<m_iDimension;iIndiceDimension++) {
            
            if (  ( m_bPrendreEnCompteQuantitatifsGauche && (iIndiceDimension<m_iNombreItemsQuantCond) )
                ||( m_bPrendreEnCompteQuantitatifsDroite && (iIndiceDimension>=m_iNombreItemsQuantCond) )  ) {

                    
                if (iIndiceDimension<m_iNombreItemsQuantCond) {
                    colonneDonnees = m_tItemsQuantCond[ iIndiceDimension ].m_colonneDonnees;
                    iIndiceDisjonction = m_iDisjonctionGaucheCourante;
                }
                else {
                    colonneDonnees = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees;
                    iIndiceDisjonction = m_iDisjonctionDroiteCourante;
                }


                iNombreValeursDomaine = colonneDonnees.m_iNombreValeursReellesCorrectes;
            
                // Ancienne m�thode de g�n�ration totalement al�atoire d'intervalles :
                //iIndiceValeurDomaineMin = (int)( ((float)java.lang.Math.random()) * ((float)iNombreValeursDomaine) );
                //iIndiceValeurDomaineMax = (int)( ((float)java.lang.Math.random()) * ((float)iNombreValeursDomaine) );

                // Autre ancienne m�thode qui ne g�n�rait que des intervalles maximaux, ce qui n'est pas bon pour la diversit� :
                //iIndiceValeurDomaineMin = 0;
                //iIndiceValeurDomaineMax = iNombreValeursDomaine-1;

                // Nouvelle m�thode :
                // L'amplitude de l'intervalle de d�part est d'autant plus large que l'indice de la 
                // r�gle potentielle dans la "population de r�gles" est �lev�, afin d'augmenter la
                // vari�t�. Cependant, un intervalle de d�part ne peut pas contenir moins de valeurs
                // que sp�cifi� avec le support minimal.
                iAmplitudeIntervalle = iNombreValeursDomaine;
                if (iIndiceReglePotentielle>0)
                    iAmplitudeIntervalle -= (iIndiceReglePotentielle * (iNombreValeursDomaine - ((int)(m_fMinSupp*(float)iNombreValeursDomaine)))) / (m_iNombreReglesPotentielles-1);

                iIndiceValeurDomaineMin = (int)( ((float)java.lang.Math.random()) * ((float)(iNombreValeursDomaine-iAmplitudeIntervalle)) );
                iIndiceValeurDomaineMax = iIndiceValeurDomaineMin + iAmplitudeIntervalle - 1;

                VerifierEtAffecterBornesReglePotentielle(reglePotentielle, iIndiceDimension, iIndiceDisjonction, iIndiceValeurDomaineMin, iIndiceValeurDomaineMax);
            }
        
        }

    }    
    
    
    
    
    public void GenererReglesPotentiellesInitiales() {
        int iIndiceReglePotentielle = 0;
        ReglePotentielle reglePotentielle = null;
       
       
        for (iIndiceReglePotentielle=0;iIndiceReglePotentielle<m_iNombreReglesPotentielles;iIndiceReglePotentielle++) {
            
            // G�n�ration d'une r�gle sans initialisation : c'est la classe fille qui se chargera
            // de lui attribuer des valeurs initiales
            reglePotentielle = new ReglePotentielle(m_iDimension, m_iNombreTotalIntervalles);
               
            InitialiserReglePotentielle(reglePotentielle, iIndiceReglePotentielle);
            
            m_tReglesPotentielles[iIndiceReglePotentielle] = reglePotentielle;
            m_tReglesPotentiellesAEvaluer[iIndiceReglePotentielle] = reglePotentielle;
        }
    
        // Cr�ation de la r�gle potentielle repr�sentant celle qui a eu la meilleure qualit� trouv�e jusqu'alors :
        // creation of the potential rule with the best quality so far
        m_meilleureReglePotentielle = new ReglePotentielle(m_iDimension, m_iNombreTotalIntervalles);
        
        // Cr�ation d'un r�gle potentielle destin�e � m�moriser la meilleure r�gle trouv�e jusqu'alors
        // lors des multiples passes pour obtenir des r�gles disjonctives :
        m_derniereReglePotentielleValide = new ReglePotentielle(m_iDimension, m_iNombreTotalIntervalles);
        
        m_iNombreReglesPotentiellesAEvaluer = m_iNombreReglesPotentielles;    
        EvaluerReglesPotentielles();
        
        // On force la m�morisation de la meilleure r�gle initiale :
        //we keep the best initial rule
        m_meilleureReglePotentielle.Copier(m_tReglesPotentielles[m_iNombreReglesPotentielles-1]);
    }

    

    public void SpecifierSchemaRegle(AssociationRule regle) {
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        int iIndiceItem = 0;
        int iNombreItemsGauche = 0;
        int iNombreItemsDroite = 0;
        int iIndiceAjoutQual = 0;
        int iIndiceAjoutQuant = 0;
        int iIndiceLigneDonnees = 0;
        boolean bItemsCouverts = true;
        int iValeurQualCourante = 0;
        int iIndiceDimension = 0;
        
        
        // On commence par stocker de mani�re plus directe (tableaux statiques) les divers
        // constituants de la r�gle :
        m_iNombreItemsQualCond = regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUALITATIF);
        m_iNombreItemsQuantCond = regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF);
        m_iNombreItemsQualObj = regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUALITATIF);
        m_iNombreItemsQuantObj = regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);

        iNombreItemsGauche = m_iNombreItemsQualCond + m_iNombreItemsQuantCond;
        iNombreItemsDroite = m_iNombreItemsQualObj + m_iNombreItemsQuantObj;
        
        if (m_iNombreItemsQualCond > 0)
            m_tItemsQualCond = new RefItemQualitatif [m_iNombreItemsQualCond];
        else
            m_tItemsQualCond = null;
        
        if (m_iNombreItemsQuantCond > 0)
            m_tItemsQuantCond = new RefItemQuantitatif [m_iNombreItemsQuantCond];
        else
            m_tItemsQuantCond = null;

        if (m_iNombreItemsQualObj > 0)
            m_tItemsQualObj = new RefItemQualitatif [m_iNombreItemsQualObj];
        else
            m_tItemsQualObj = null;

        if (m_iNombreItemsQuantObj > 0)
            m_tItemsQuantObj = new RefItemQuantitatif [m_iNombreItemsQuantObj];
        else
            m_tItemsQuantObj = null;

        
        // M�morisation des items qualitatifs :
        
        iIndiceAjoutQual = 0;
        iIndiceAjoutQuant = 0;
        for (iIndiceItem=0; iIndiceItem<iNombreItemsGauche; iIndiceItem++) {
            item = regle.ObtenirItemGauche(iIndiceItem);
            if (item != null) {
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                    itemQual = (ItemQualitative)item;
                    m_tItemsQualCond[iIndiceAjoutQual] = new RefItemQualitatif(itemQual.m_attributQual.m_colonneDonnees, itemQual.m_iIndiceValeur, itemQual.ObtenirNomCompletItem());
                    iIndiceAjoutQual++;
                }
                else if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                    itemQuant = (ItemQuantitative)item;
                    m_tItemsQuantCond[iIndiceAjoutQuant] = new RefItemQuantitatif(itemQuant.m_attributQuant.m_colonneDonnees);
                    iIndiceAjoutQuant++;
                }
            }
        }
        
            
        // M�morisation des items quantitatifs :

        iIndiceAjoutQual = 0;
        iIndiceAjoutQuant = 0;
        for (iIndiceItem=0; iIndiceItem<iNombreItemsDroite; iIndiceItem++) {
            item = regle.ObtenirItemDroite(iIndiceItem);
            if (item != null) {
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                    itemQual = (ItemQualitative)item;
                    m_tItemsQualObj[iIndiceAjoutQual] = new RefItemQualitatif(itemQual.m_attributQual.m_colonneDonnees, itemQual.m_iIndiceValeur, itemQual.ObtenirNomCompletItem());
                    iIndiceAjoutQual++;
                }
                else if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                    itemQuant = (ItemQuantitative)item;
                    m_tItemsQuantObj[iIndiceAjoutQuant] = new RefItemQuantitatif(itemQuant.m_attributQuant.m_colonneDonnees);
                    iIndiceAjoutQuant++;
                }
            }
        }
        
        
        // M�morisation du sch�ma de la r�gle :
        m_schemaRegleOptimale = new AssociationRule(regle);
        
        
        // M�morisation d'informations sur le sch�ma de la r�gle :
        m_iDimension = m_iNombreItemsQuantCond + m_iNombreItemsQuantObj;
        m_iNombreTotalItems = m_iDimension + m_iNombreItemsQualCond + m_iNombreItemsQualObj;
        m_iDebutIntervallesDroite = m_schemaRegleOptimale.m_iNombreDisjonctionsGauche * m_iNombreItemsQuantCond;
        m_iNombreTotalIntervalles = m_schemaRegleOptimale.m_iNombreDisjonctionsGauche*m_iNombreItemsQuantCond + m_schemaRegleOptimale.m_iNombreDisjonctionsDroite*m_iNombreItemsQuantObj;
        m_iDisjonctionGaucheCourante = 0;
        m_iDisjonctionDroiteCourante = 0;
        m_iNombreDisjonctionsGaucheValides = 1;
        m_iNombreDisjonctionsDroiteValides = 1;        
        m_bPrendreEnCompteQuantitatifsGauche = true;
        m_bPrendreEnCompteQuantitatifsDroite = true;
        m_iSupportCumuleCond = 0;
        m_iSupportCumuleRegle = 0;
        
        
        // Pour optimiser les calculs de l'�valuation d'une r�gle, on constitue un tableau qui 
        // r�pertorie uniquement les lignes qui couvrent les items qualitatifs de la r�gle
        // (les autres lignes ne seront forc�ment pas couvertes ni par la r�gle, ni m�me par sa partie gauche) :
        
        m_iNombreLignesAPrendreEnCompte = 0;
        for (iIndiceLigneDonnees=0;iIndiceLigneDonnees<m_iNombreTransactions;iIndiceLigneDonnees++) {

            // D�tection des lignes couvertes par les valeurs qualitatives dans la partie GAUCHE de la r�gle :
            
            bItemsCouverts = true;
            iIndiceItem=0;
            while ( bItemsCouverts && (iIndiceItem<m_iNombreItemsQualCond) ) {
                iValeurQualCourante = m_tItemsQualCond[iIndiceItem].m_colonneDonnees.m_tIDQualitatif[iIndiceLigneDonnees];
                bItemsCouverts = bItemsCouverts && (  iValeurQualCourante == m_tItemsQualCond[iIndiceItem].m_iIndiceValeurAttribut);
                iIndiceItem++;
            }
            
            // Et v�rification qu'elles ne contiennent pas de valeurs manquantes pour les items quantitatifs de gauche :            
            
            if (bItemsCouverts) {
                iIndiceItem=0;
                while ( bItemsCouverts && (iIndiceItem<m_iNombreItemsQuantCond) ) {
                    bItemsCouverts = (m_tItemsQuantCond[iIndiceItem].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees] != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT );
                    iIndiceItem++;
                }
            }
                
            
            if (bItemsCouverts) {
                m_tLignesAPrendreEnCompte[m_iNombreLignesAPrendreEnCompte] = iIndiceLigneDonnees;
                m_iNombreLignesAPrendreEnCompte++;
            }
           
            
            // Test des valeurs qualitatives dans la partie DROITE de la r�gle :     
            
            bItemsCouverts = true;
            iIndiceItem=0;
            while ( bItemsCouverts && (iIndiceItem<m_iNombreItemsQualObj) ) {
                iValeurQualCourante = m_tItemsQualObj[iIndiceItem].m_colonneDonnees.m_tIDQualitatif[iIndiceLigneDonnees];
                bItemsCouverts = bItemsCouverts && (  iValeurQualCourante == m_tItemsQualObj[iIndiceItem].m_iIndiceValeurAttribut);
                iIndiceItem++;
            }
            
            // Et v�rification que la ligne ne contient pas de valeurs manquantes pour les items quantitatifs de droite :            
            
            if (bItemsCouverts) {
                iIndiceItem=0;
                while ( bItemsCouverts && (iIndiceItem<m_iNombreItemsQuantObj) ) {
                    bItemsCouverts = (m_tItemsQuantObj[iIndiceItem].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees] != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT );
                    iIndiceItem++;
                }
            }
            
                 
            m_tPrendreEnCompteLigneDroite[iIndiceLigneDonnees] = bItemsCouverts;
            
            m_tLignesCouvertesGauche[iIndiceLigneDonnees] = false;
            m_tLignesCouvertesDroite[iIndiceLigneDonnees] = false;
        }

/*
        
        m_tItemsQuantCond[iIndiceItem].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees]
        
        m_tIndicesLignesTrieesAttributQuant
*/        
    }
    
    
    
    
    // Prend en compte une nouvelle disjonction et relance l'algorithme :
    public boolean InitierNouvellePasse() {
        boolean bProchainePassePrivilegieDroite = false;
        int [] tNouvellesLignesAPrendreEnCompte = null;
        int iNombreNouvellesLignesAPrendreEnCompte = 0;
        int iIndiceLigneDonnees = 0;
        int iIndiceLigne = 0;
        boolean bItemCondCouvertReglePotentielle = false;
        boolean bItemObjCouvertReglePotentielle = false;
        boolean bItemRegleCouvertReglePotentielle = false;
        boolean bPremierePasse = false;
        int iIndiceDimension = 0;
        int iIndiceIntervalle = 0;
        float fValeurReelle = 0.0f;
        int iIndiceReglePotentielle = 0;
        boolean bNouvelleRegleEstSolide = false;
        int iAncienSupportCumuleRegle = 0;
        int iAncienSupportCumuleCond = 0;
        
        
        
        // Si aucune disjonction ne doit appara�tre dans la r�gle, on peut arr�ter l'algorithme :
        if (  (m_schemaRegleOptimale.m_iNombreDisjonctionsGauche == 1)
            &&(m_schemaRegleOptimale.m_iNombreDisjonctionsDroite == 1)  ) {
                
            m_iSupportCumuleCond = m_meilleureReglePotentielle.m_iSupportCond;
            m_iSupportCumuleRegle = m_meilleureReglePotentielle.m_iSupportRegle;
            
            return false;
        }
        
        
        bPremierePasse = m_bPrendreEnCompteQuantitatifsGauche && m_bPrendreEnCompteQuantitatifsDroite;
        

        
        // On v�rifie que la nouvelle passe a produit une r�gle de bonne qualit�, sinon on ne la prend pas en compte :
        bNouvelleRegleEstSolide = (  ( ((float)m_meilleureReglePotentielle.m_iSupportRegle) >= m_fMinSupp*((float)m_iNombreTransactions) )
                                   &&( ((float)m_meilleureReglePotentielle.m_iSupportRegle) >= m_fMinConf*((float)m_meilleureReglePotentielle.m_iSupportCond) )  );

        if (bNouvelleRegleEstSolide) {
           
        
            // On m�morise les caract�ristisques de la derni�re r�gle (cumul�e) valide, au cas o� il faille la restaurer :
            iAncienSupportCumuleRegle = m_iSupportCumuleRegle;
            iAncienSupportCumuleCond = m_iSupportCumuleCond;

            
            // On supprime de la table tous les exemples couverts par la r�gle :

            tNouvellesLignesAPrendreEnCompte = new int [m_iNombreTransactions];
            iNombreNouvellesLignesAPrendreEnCompte = 0;
        
        
            // 'm_tLignesAPrendreEnCompte' contient toutes les lignes couvertes par les items qualitatifs 
            // gauche de la r�gle, ne contient pas d'attribut quantitatif dont la ligne correspondante
            // pr�sente une valeur manquante, et enfin on lui retire successivement toutes les lignes
            // couvertes par la r�gle dont les disjonctions sont construites incr�mentalement.

            // 'm_tLignesCouvertesGauche' indique pour chaque ligne si elle est couverte
            // par l'une des disjonctions gauches jusqu'alors construites pour la r�gle

            // 'm_tPrendreEnCompteLigneDroite' indique pour chaque ligne si elle est couverte
            // par l'ensemble des items qualitatitifs de la partie droite de la r�gle, et �galement si
            // elle ne contient pas de valeur manquante pour l'un des attributs quantitatifs de la partie droite.
        
            for (iIndiceLigne=0; iIndiceLigne<m_iNombreLignesAPrendreEnCompte; iIndiceLigne++) {

                iIndiceLigneDonnees = m_tLignesAPrendreEnCompte[iIndiceLigne]; // Indice r�el de la lignes dans la BD

                bItemCondCouvertReglePotentielle = m_tLignesCouvertesGauche[iIndiceLigneDonnees];
                bItemObjCouvertReglePotentielle = m_tLignesCouvertesDroite[iIndiceLigneDonnees];                

                // Test des valeurs quantitatives dans la partie gauche de la r�gle :
                if (m_bPrendreEnCompteQuantitatifsGauche) {

                    bItemCondCouvertReglePotentielle = true;
                    iIndiceDimension=0;
                    iIndiceIntervalle = m_iDisjonctionGaucheCourante;
                    while ( bItemCondCouvertReglePotentielle && (iIndiceDimension<m_iNombreItemsQuantCond) ) {
                        fValeurReelle = m_tItemsQuantCond[iIndiceDimension].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees];
                        bItemCondCouvertReglePotentielle =    ( fValeurReelle >= m_meilleureReglePotentielle.m_tIntervalleMin[iIndiceIntervalle] )
                                                           && ( fValeurReelle <= m_meilleureReglePotentielle.m_tIntervalleMax[iIndiceIntervalle] );
                        iIndiceDimension++;
                        iIndiceIntervalle += m_schemaRegleOptimale.m_iNombreDisjonctionsGauche;
                    } 

                    // Mise � jour du support en tenant compte de l'adjonction du nouvel intervalle, 
                    // et en faisant attention � ne pas comptabiliser d'�ventuelles zones qui se recouvrent :
                    if ( bItemCondCouvertReglePotentielle && (!m_tLignesCouvertesGauche[iIndiceLigneDonnees]) )
                        m_iSupportCumuleCond++;

                    m_tLignesCouvertesGauche[iIndiceLigneDonnees] = m_tLignesCouvertesGauche[iIndiceLigneDonnees] || bItemCondCouvertReglePotentielle;            
                }


                // Test des valeurs quantitatives dans la partie droite de la r�gle
                if (m_bPrendreEnCompteQuantitatifsDroite) {

                    // Tout d'abord on v�rifie la couverture, m�me si la condition n'est pas v�rifi�e
                    // (afin de mettre � jour correctement le tableau 'm_tLignesCouvertesDroite') :
                    bItemObjCouvertReglePotentielle = m_tPrendreEnCompteLigneDroite[iIndiceLigneDonnees];
                    iIndiceDimension = m_iNombreItemsQuantCond;
                    iIndiceIntervalle = m_iDebutIntervallesDroite + m_iDisjonctionDroiteCourante;
                    while ( bItemObjCouvertReglePotentielle && (iIndiceDimension<m_iDimension) ) {
                        fValeurReelle = m_tItemsQuantObj[iIndiceDimension-m_iNombreItemsQuantCond].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees];
                        bItemObjCouvertReglePotentielle =    ( fValeurReelle >= m_meilleureReglePotentielle.m_tIntervalleMin[iIndiceIntervalle] )
                                                            && ( fValeurReelle <= m_meilleureReglePotentielle.m_tIntervalleMax[iIndiceIntervalle] );
                        iIndiceDimension++;
                        iIndiceIntervalle += m_schemaRegleOptimale.m_iNombreDisjonctionsDroite;
                    }

                    m_tLignesCouvertesDroite[iIndiceLigneDonnees] = m_tLignesCouvertesDroite[iIndiceLigneDonnees] || bItemObjCouvertReglePotentielle;

                }

                // La r�gle n'est couverte que si les parties gauche et droite le sont :
                bItemRegleCouvertReglePotentielle = bItemCondCouvertReglePotentielle && bItemObjCouvertReglePotentielle;

                if (bItemRegleCouvertReglePotentielle)
                    m_iSupportCumuleRegle++;


                // On ne conserve que les lignes non couvertes par la r�gle :
                if ( (!bItemCondCouvertReglePotentielle) || (!bItemRegleCouvertReglePotentielle) ) {
                    tNouvellesLignesAPrendreEnCompte[iNombreNouvellesLignesAPrendreEnCompte] = iIndiceLigneDonnees;
                    iNombreNouvellesLignesAPrendreEnCompte ++;
                }

            }
            
        
        
            // On v�rifie maintenant que la r�gle cumulant les disjonctions est elle aussi valide :
            
            bNouvelleRegleEstSolide = (  ( ((float)m_iSupportCumuleRegle) >= m_fMinSuppRegle*((float)m_iNombreTransactions) )
                                       &&( ((float)m_iSupportCumuleRegle) >= m_fMinConf*((float)m_iSupportCumuleCond) )  );


            if (bNouvelleRegleEstSolide) {

                // On remplace l'ancien index de lignes par le nouveau, �pur� :
                m_tLignesAPrendreEnCompte = tNouvellesLignesAPrendreEnCompte;           
                m_iNombreLignesAPrendreEnCompte = iNombreNouvellesLignesAPrendreEnCompte;

                // On prend en compte la nouvelle disjonction :
                if ( (m_bPrendreEnCompteQuantitatifsGauche) && (!m_bPrendreEnCompteQuantitatifsDroite) )
                    m_iNombreDisjonctionsGaucheValides++;

                if ( (m_bPrendreEnCompteQuantitatifsDroite) && (!m_bPrendreEnCompteQuantitatifsGauche) )
                    m_iNombreDisjonctionsDroiteValides++;
                
                // On m�morise la meilleure r�gle trouv�e jusqu'ici :
                m_derniereReglePotentielleValide.Copier(m_meilleureReglePotentielle);
                
            }
            // Sinon on ne tient pas compte de la nouvelle r�gle et on restaure l'�tat pr�c�dent :
            else {
                m_iSupportCumuleRegle = iAncienSupportCumuleRegle;
                m_iSupportCumuleCond = iAncienSupportCumuleCond;
            }
            
        }
       
        
        
        if (!bNouvelleRegleEstSolide) {
            
            // Si la derni�re r�gle g�n�r�e n'a pas permis d'am�liorer la r�gle initiale, on arr�te la recherche 
            // de nouvelles disjonctions du c�t� o� on vient d'effectuer la pr�c�dente passe :
            
            if (m_bPrendreEnCompteQuantitatifsGauche)
                m_iDisjonctionGaucheCourante = m_schemaRegleOptimale.m_iNombreDisjonctionsGauche;

            if (m_bPrendreEnCompteQuantitatifsDroite)
                m_iDisjonctionDroiteCourante = m_schemaRegleOptimale.m_iNombreDisjonctionsDroite;
        
            
            // On restaure la derni�re r�gle valide (si celle-ci existe) :
            
            if (!bPremierePasse)
                m_meilleureReglePotentielle.Copier(m_derniereReglePotentielleValide);
            else {
                // Mise � jour n�cessaires si la premi�re r�gle trouv�e n'�tait pas valide :
                m_iSupportCumuleCond = m_meilleureReglePotentielle.m_iSupportCond;
                m_iSupportCumuleRegle = m_meilleureReglePotentielle.m_iSupportRegle;
            }
            
        }

        
        
        bProchainePassePrivilegieDroite = m_bPrendreEnCompteQuantitatifsGauche;
        
            
        if (bProchainePassePrivilegieDroite) {
            
            if (m_iDisjonctionDroiteCourante+1 < m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) {
                m_iDisjonctionDroiteCourante++;
                m_bPrendreEnCompteQuantitatifsGauche = false;
                m_bPrendreEnCompteQuantitatifsDroite = true;
            }
            else if (m_iDisjonctionGaucheCourante+1 < m_schemaRegleOptimale.m_iNombreDisjonctionsGauche) {
                m_iDisjonctionGaucheCourante++;
                m_bPrendreEnCompteQuantitatifsGauche = true;
                m_bPrendreEnCompteQuantitatifsDroite = false;
            }
            else
                m_bPrendreEnCompteQuantitatifsGauche = m_bPrendreEnCompteQuantitatifsDroite = false;
                
        }
        else {
            
            if (m_iDisjonctionGaucheCourante+1 < m_schemaRegleOptimale.m_iNombreDisjonctionsGauche) {
                m_iDisjonctionGaucheCourante++;
                m_bPrendreEnCompteQuantitatifsGauche = true;
                m_bPrendreEnCompteQuantitatifsDroite = false;
            }
            else if (m_iDisjonctionDroiteCourante+1 < m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) {
                m_iDisjonctionDroiteCourante++;
                m_bPrendreEnCompteQuantitatifsGauche = false;
                m_bPrendreEnCompteQuantitatifsDroite = true;
            }
            else
                m_bPrendreEnCompteQuantitatifsGauche = m_bPrendreEnCompteQuantitatifsDroite = false;
        
        }
        
        
        // On g�n�re un nouvel ensemble de r�gles potentielles pour la passe suivante :
        if ( m_bPrendreEnCompteQuantitatifsGauche || m_bPrendreEnCompteQuantitatifsDroite ) {
            
            // Pour les passes suivantes, on utilise un support moins �lev� pour autoris� les r�gles suppl�mentaires � �tre incorpor�es � la r�gle principale :
            m_fMinSupp = m_fMinSuppDisjonction;
            
            for (iIndiceReglePotentielle=0; iIndiceReglePotentielle<m_iNombreReglesPotentielles; iIndiceReglePotentielle++) {
                InitialiserReglePotentielle(m_tReglesPotentielles[iIndiceReglePotentielle], iIndiceReglePotentielle);
                m_tReglesPotentiellesAEvaluer[iIndiceReglePotentielle] = m_tReglesPotentielles[iIndiceReglePotentielle];
            }
            
            m_iNombreReglesPotentiellesAEvaluer = m_iNombreReglesPotentielles;    
            EvaluerReglesPotentielles();

            m_meilleureReglePotentielle.Copier(m_tReglesPotentielles[m_iNombreReglesPotentielles-1]);

            return true;
        }
 
        return false;
    }
        
    
   
    
    /**
     * Evaluation of the set of potential rules in one scan of the dataset
     */
    public void EvaluerReglesPotentielles() {
        ReglePotentielle reglePotentielle = null;
        DataColumn colonneDonnees = null;
        int iIndiceLigneDonnees = 0;
        int iIndiceLigne = 0;
        int iIndiceDimension = 0;
        int iIndiceDisjonction = 0;
        int iIndiceItem = 0;
        int iIndiceReglePotentielle = 0;
        short iValeurQualCourante = 0;    // Valeur qualitaive lue pour un attribut sur une ligne de la BD
        float fValeurQuantCourante = 0;   // Valeur num�rique lue pour un attribut sur une ligne de la BD
        boolean bItemCondCouvertReglePotentielle = false;
        boolean bItemRegleCouvertReglePotentielle = false;
        float [] tValeursReelles = null;   // Tableau contenant les valeurs des attributs quantitatifs pour une ligne
        int iNombreDisjonctions = 0;
        int iIndiceIntervalle = 0;
        boolean bPremierePasse = false;
        boolean bTestPreliminaire = false;
                

        bPremierePasse = (m_bPrendreEnCompteQuantitatifsGauche && m_bPrendreEnCompteQuantitatifsDroite);
        
        // Le test pr�liminaire permet d'�viter de passer dans la plus grosse partie du calcul
        // dans le cas o� le 1er item quantitatif n'est pas v�rifi� :
        bTestPreliminaire = m_bPrendreEnCompteQuantitatifsGauche && (m_iNombreItemsQuantCond > 0);
        
        
        // On r�initialise les mesures de support pour chacunes des r�gles potentielles :
        for (iIndiceReglePotentielle=0;iIndiceReglePotentielle<m_iNombreReglesPotentiellesAEvaluer;iIndiceReglePotentielle++) {
            reglePotentielle = m_tReglesPotentiellesAEvaluer[iIndiceReglePotentielle];
            reglePotentielle.m_iSupportCond = 0;
            reglePotentielle.m_iSupportRegle = 0;
        }
        
        tValeursReelles = new float [m_iDimension];
        

        // On commence par �valuer la couverture des items qualitatifs, ceux-ci �tant
        // communs � toutes les r�gles potentielles. Pour cela, d'une part on ne parcourt que les lignes
        // couvertes par les items qualitatifs gauche de la r�gle. Par ailleurs, on a d�j�
        // pr�-calcul� la couverture droite lors du passage du sch�ma de r�gle � l'algorithme.
        for (iIndiceLigne = 0; iIndiceLigne < m_iNombreLignesAPrendreEnCompte; iIndiceLigne++) {

            // Le tableau 'm_tLignesAPrendreEnCompte' contient toutes les lignes couvertes
            // par la partie gauche QUALITATIVE de la r�gle,
            // mais a �t� d�pourvu des lignes couvertes par la r�gle en entier (items qualitatifs et quantitatifs).
            
            iIndiceLigneDonnees = m_tLignesAPrendreEnCompte[iIndiceLigne]; // Indice r�el de la lignes dans la BD


            // On r�pertorie les valeurs num�riques pour chaque colonne de la ligne :
            for (iIndiceDimension=0; iIndiceDimension<m_iNombreItemsQuantCond; iIndiceDimension++)
                tValeursReelles[iIndiceDimension] = m_tItemsQuantCond[iIndiceDimension].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees];
            
            if (m_tPrendreEnCompteLigneDroite[iIndiceLigneDonnees])
                for (iIndiceDimension=m_iNombreItemsQuantCond; iIndiceDimension<m_iDimension; iIndiceDimension++)
                    tValeursReelles[iIndiceDimension] = m_tItemsQuantObj[iIndiceDimension-m_iNombreItemsQuantCond].m_colonneDonnees.m_tValeurReelle[iIndiceLigneDonnees];
            

            // On �value ensuite la couverture des intervalles quantitatifs pour chacunes des regles potentielles :
            for (iIndiceReglePotentielle=0;iIndiceReglePotentielle<m_iNombreReglesPotentiellesAEvaluer;iIndiceReglePotentielle++) {

                reglePotentielle = m_tReglesPotentiellesAEvaluer[iIndiceReglePotentielle];

                bItemCondCouvertReglePotentielle = true;
                if (bTestPreliminaire)
                    bItemCondCouvertReglePotentielle =    (tValeursReelles[0] >= reglePotentielle.m_tIntervalleMin[m_iDisjonctionGaucheCourante])
                                                       && (tValeursReelles[0] <= reglePotentielle.m_tIntervalleMax[m_iDisjonctionGaucheCourante]);
                
                if (bItemCondCouvertReglePotentielle) {


                    // Test des valeurs quantitatives dans la partie gauche de la r�gle :
                    if (!m_bPrendreEnCompteQuantitatifsGauche)
                        bItemCondCouvertReglePotentielle = m_tLignesCouvertesGauche[iIndiceLigneDonnees];
                    else {

                        iIndiceDimension=1;
                        iIndiceIntervalle = m_iDisjonctionGaucheCourante + m_schemaRegleOptimale.m_iNombreDisjonctionsGauche;
                        while ( bItemCondCouvertReglePotentielle && (iIndiceDimension<m_iNombreItemsQuantCond) ) {
                            bItemCondCouvertReglePotentielle =    ( tValeursReelles[iIndiceDimension] >= reglePotentielle.m_tIntervalleMin[iIndiceIntervalle] )
                                                               && ( tValeursReelles[iIndiceDimension] <= reglePotentielle.m_tIntervalleMax[iIndiceIntervalle] );
                            iIndiceDimension++;
                            iIndiceIntervalle += m_schemaRegleOptimale.m_iNombreDisjonctionsGauche;
                        } 

                    }

                    
                    if (bItemCondCouvertReglePotentielle) {
                        
                        reglePotentielle.m_iSupportCond++;


                        // Test des valeurs quantitatives dans la partie droite de la r�gle
                        // (utile uniquement si la partie gauche est couverte, afin d'augmenter le support de la r�gle,
                        // et � condition que les items qualitatifs droits soient eux aussi couverts)

                        if (!m_bPrendreEnCompteQuantitatifsDroite)
                            bItemRegleCouvertReglePotentielle = (bItemCondCouvertReglePotentielle) && (m_tLignesCouvertesDroite[iIndiceLigneDonnees]);
                        else {

                            bItemRegleCouvertReglePotentielle = bItemCondCouvertReglePotentielle && (m_tPrendreEnCompteLigneDroite[iIndiceLigneDonnees]);
                            iIndiceDimension = m_iNombreItemsQuantCond;
                            iIndiceIntervalle = m_iDebutIntervallesDroite + m_iDisjonctionDroiteCourante;
                            while ( bItemRegleCouvertReglePotentielle && (iIndiceDimension<m_iDimension) ) {
                                bItemRegleCouvertReglePotentielle =    ( tValeursReelles[iIndiceDimension] >= reglePotentielle.m_tIntervalleMin[iIndiceIntervalle] )
                                                                    && ( tValeursReelles[iIndiceDimension] <= reglePotentielle.m_tIntervalleMax[iIndiceIntervalle] );
                                iIndiceDimension++;
                                iIndiceIntervalle += m_schemaRegleOptimale.m_iNombreDisjonctionsDroite;
                            }
                        }

                        if (bItemRegleCouvertReglePotentielle)
                            reglePotentielle.m_iSupportRegle++;   
                    }
                    
                }
            }
        }
      
        
        // La mesure de qualit� reste � la charge de la classe fille :    
        for (iIndiceReglePotentielle = 0;iIndiceReglePotentielle<m_iNombreReglesPotentiellesAEvaluer;iIndiceReglePotentielle++)
            EvaluerQualiteReglePotentielle( m_tReglesPotentiellesAEvaluer[iIndiceReglePotentielle] );

        
        // Tous les indices de qualit� sont maintenant � jour :
        m_iNombreReglesPotentiellesAEvaluer = 0;
        
        
        // Tri croissant par quality :        
        Arrays.sort(m_tReglesPotentielles);
        
        
        // M�morisation de la meilleure r�gle potentielle :
        if (m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_fQualite >= m_meilleureReglePotentielle.m_fQualite)
            m_meilleureReglePotentielle.Copier(m_tReglesPotentielles[m_iNombreReglesPotentielles-1]);
    }

    
    public AssociationRule ObtenirMeilleureRegle() {
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        int iIndiceItem = 0;
        int iIndiceItemQuant = 0;
        float fConfianceRegle = 0.0f;
        int iIndiceIntervalleReglePotentielle = 0;
        int iIndiceDisjonction = 0;
        
        if (m_schemaRegleOptimale == null)
            return null;

        // Determination des bornes optimales dans la partie gauche de la r�gle :
        
        iIndiceIntervalleReglePotentielle = 0;
        iIndiceItemQuant = 0;
        for (iIndiceItem = 0; iIndiceItem < m_schemaRegleOptimale.m_iNombreItemsGauche; iIndiceItem++) {
            
            item = m_schemaRegleOptimale.ObtenirItemGauche(iIndiceItem);
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                itemQuant = (ItemQuantitative)item;  
                for (iIndiceDisjonction = 0; iIndiceDisjonction < m_iNombreDisjonctionsGaucheValides; iIndiceDisjonction++) {
                    itemQuant.m_tBornes[iIndiceDisjonction*2] = m_meilleureReglePotentielle.m_tIntervalleMin[iIndiceIntervalleReglePotentielle];
                    itemQuant.m_tBornes[iIndiceDisjonction*2+1] = m_meilleureReglePotentielle.m_tIntervalleMax[iIndiceIntervalleReglePotentielle];
                    iIndiceIntervalleReglePotentielle++;
                }
                iIndiceItemQuant++;
                iIndiceIntervalleReglePotentielle += m_schemaRegleOptimale.m_iNombreDisjonctionsGauche - iIndiceDisjonction;
            }    
            
        }
            
        
        // Determination des bornes optimales dans la partie droite de la regle :
        
        iIndiceIntervalleReglePotentielle = this.m_iDebutIntervallesDroite;
        iIndiceItemQuant = m_iNombreItemsQuantCond;
        for (iIndiceItem=0;iIndiceItem<m_schemaRegleOptimale.m_iNombreItemsDroite;iIndiceItem++) {
            
            item = m_schemaRegleOptimale.ObtenirItemDroite(iIndiceItem);
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                itemQuant = (ItemQuantitative)item;  
                for (iIndiceDisjonction=0; iIndiceDisjonction<m_iNombreDisjonctionsDroiteValides; iIndiceDisjonction++) {
                    itemQuant.m_tBornes[iIndiceDisjonction*2] = m_meilleureReglePotentielle.m_tIntervalleMin[iIndiceIntervalleReglePotentielle];
                    itemQuant.m_tBornes[iIndiceDisjonction*2+1] = m_meilleureReglePotentielle.m_tIntervalleMax[iIndiceIntervalleReglePotentielle];
                    iIndiceIntervalleReglePotentielle++;
                }
                iIndiceItemQuant++;
                iIndiceIntervalleReglePotentielle += m_schemaRegleOptimale.m_iNombreDisjonctionsDroite - iIndiceDisjonction;
            }    
            
        }        
 
        m_schemaRegleOptimale.m_iNombreDisjonctionsGaucheValides = this.m_iNombreDisjonctionsGaucheValides;
        m_schemaRegleOptimale.m_iNombreDisjonctionsDroiteValides = this.m_iNombreDisjonctionsDroiteValides;
        
        m_meilleureReglePotentielle.m_iSupportCond = m_iSupportCumuleCond;
        m_meilleureReglePotentielle.m_iSupportRegle = m_iSupportCumuleRegle;
        
        m_schemaRegleOptimale.AssignerNombreOccurrences( m_meilleureReglePotentielle.m_iSupportRegle );
        m_schemaRegleOptimale.AssignerSupport( ((float)m_meilleureReglePotentielle.m_iSupportRegle) / ((float)m_iNombreTransactions) );
        
        if (m_meilleureReglePotentielle.m_iSupportCond > 0)
            fConfianceRegle = ((float)m_meilleureReglePotentielle.m_iSupportRegle) / ((float)m_meilleureReglePotentielle.m_iSupportCond);
        else
            fConfianceRegle = 0.0f;
        m_schemaRegleOptimale.AssignerConfiance(fConfianceRegle);
       
        return m_schemaRegleOptimale;
    }
    
     
    
    public int ObtenirMeilleurSupportCourant() {
        return m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_iSupportRegle;
    }
     
    
    
    public float ObtenirMeilleurSupportRelatifCourant() {
        return ((float)m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_iSupportRegle  / (float)m_iNombreTransactions);
    }
 
    
    
    public float ObtenirMeilleureConfianceCourant() {
        int iSupportCond = 0;
        int iSupportRegle = 0;
        
        iSupportCond = m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_iSupportCond;
        iSupportRegle = m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_iSupportRegle;
        
        if (iSupportCond>0)
            return ((float)iSupportRegle / (float)iSupportCond);
        else
            return 0.0f;
    }
    
    
    
    public float CalculerQualiteMoyenne() {
        int iIndiceReglePotentielle = 0;
        float fCumulQualite = 0.0f;
        
        fCumulQualite = 0.0f;
        for (iIndiceReglePotentielle = 0; iIndiceReglePotentielle < m_iNombreReglesPotentielles; iIndiceReglePotentielle++)
            fCumulQualite += m_tReglesPotentielles[iIndiceReglePotentielle].m_fQualite;
        
        return (fCumulQualite / (float)m_iNombreReglesPotentielles);
    }

    
    
    public float ObtenirMeilleureQualiteCourante() {
        return m_tReglesPotentielles[m_iNombreReglesPotentielles-1].m_fQualite;
    }
    
    
    
    public float ObtenirPireQualiteCourante() {
        return m_tReglesPotentielles[0].m_fQualite;
    }

    
    public void EvaluerQualiteReglePotentielle(ReglePotentielle reglePotentielle) {
        int iIndiceDimension = 0;
        int iIndiceDisjonction = 0;       
        int iNombreDisjonctions = 0;
        int iIndiceIntervalle = 0;
        int iSupportIntervalle = 0;
        int iSupportMax = 0;
        float fTauxCouvertureDomaine1 = 0.0f;
        float fTauxCouvertureDomaine2 = 0.0f;
        DataColumn colonneDonnees = null;
        
        // 1 �re mesure de qualit� :
        /*            individu.m_fQualite = (float)individu.m_iSupportRegle - m_fMinConf * (float)individu.m_iSupportCond;
                    if ((float)individu.m_iSupportRegle <  m_fMinSupp*(float)m_iNombreTransactions)
                        individu.m_fQualite = -(float)m_iNombreTransactions;
        */
        
            
        // 2nde mesure de qualit� : on pond�re par le taux de couverture du domaine de chaque valeur quantitative
        //calculate the gain
        reglePotentielle.m_fQualite = (float)reglePotentielle.m_iSupportRegle - m_fMinConf * (float)reglePotentielle.m_iSupportCond;
            
        //reglePotentielle.m_fQualite /= (float)m_iNombreTransactions;
        
        
        if (reglePotentielle.m_fQualite > 0.0f) {

            for (iIndiceDimension=0; iIndiceDimension<m_iDimension; iIndiceDimension++) {

                if (  ( m_bPrendreEnCompteQuantitatifsGauche && (iIndiceDimension<m_iNombreItemsQuantCond) )
                    ||( m_bPrendreEnCompteQuantitatifsDroite && (iIndiceDimension>=m_iNombreItemsQuantCond) )  ) {

                    
                    if (iIndiceDimension<m_iNombreItemsQuantCond) {
                        iIndiceIntervalle = (iIndiceDimension*m_schemaRegleOptimale.m_iNombreDisjonctionsGauche) + m_iDisjonctionGaucheCourante;
                        colonneDonnees = m_tItemsQuantCond[ iIndiceDimension ].m_colonneDonnees;
                    }
                    else {
                        iIndiceIntervalle = m_iDebutIntervallesDroite + ((iIndiceDimension-m_iNombreItemsQuantCond)*m_schemaRegleOptimale.m_iNombreDisjonctionsDroite) + m_iDisjonctionDroiteCourante;
                        colonneDonnees = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees;
                    }


                    // Technique 2 :
                    //    - probl�me des petits �lots dont le support est < minsupp/nb intervalles et donc non trouv�s,
                    //    - tend � ne pas d�passer du support minimal, alors qu'il pourrait sans perdre de qualit�

                    fTauxCouvertureDomaine1 = reglePotentielle.m_tIntervalleMax[iIndiceIntervalle] - reglePotentielle.m_tIntervalleMin[iIndiceIntervalle];
                    fTauxCouvertureDomaine1 /= (colonneDonnees.m_fValeurMax - colonneDonnees.m_fValeurMin);

                    
                    iSupportMax = colonneDonnees.m_iNombreValeursReellesCorrectes;
                    if (iSupportMax > 0) {
                        iSupportIntervalle = colonneDonnees.ObtenirSupportIntervalle(reglePotentielle.m_tIndiceMin[iIndiceIntervalle], reglePotentielle.m_tIndiceMax[iIndiceIntervalle]);
                        fTauxCouvertureDomaine2 = ((float)iSupportIntervalle) / ((float)iSupportMax);
                    }
                    
/*                        
                    if (iIndiceDimension<m_iNombreItemsQuantCond)
                        fTauxCouvertureDomaine2 = 1.0f;
                    else {
                        iSupportIntervalle = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees.ObtenirSupportIntervalle(reglePotentielle.m_tIndiceMin[iIndiceIntervalle], reglePotentielle.m_tIndiceMax[iIndiceIntervalle]);
                        iSupportMax = m_tItemsQuantObj[ iIndiceDimension-m_iNombreItemsQuantCond ].m_colonneDonnees.m_iNombreValeursReellesCorrectes;
                        fTauxCouvertureDomaine2 = ((float)iSupportIntervalle) / ((float)iSupportMax);
                    }
*/

                    reglePotentielle.m_fQualite *= (1.0f-fTauxCouvertureDomaine1) * (1.0f-fTauxCouvertureDomaine2);

                }
            }
        }


        if ((float)reglePotentielle.m_iSupportRegle < m_fMinSupp*(float)m_iNombreTransactions)
            reglePotentielle.m_fQualite -= (float)m_iNombreTransactions + (float)reglePotentielle.m_iSupportRegle - m_fMinSupp*(float)m_iNombreTransactions ;//) / ((float)m_iNombreTransactions);

    }
    
}
