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

import java.util.*;

import src.apriori.*;
import src.database.*;


public class PositionRuleParameters {
    

    // Definit l'usage pour lequel sont destines les parametres (peut modifier le comportement de certaines methodes) :
	// 
    public static final int PARAMETRES_POSITION_REGLES = 0;
    public static final int PARAMETRES_FILTRAGE_REGLES = 1;
 
    private int m_iTypeParametrage = 0;
    
    private ResolutionContext m_contexteResolution = null;
    
    private Hashtable m_tableParametresItemsQualitatifs = null; // Table r�pertoriant les param�tres attribu�s aux items qualitatifs
    private Hashtable m_tableParametresAttributsQuantitatifs = null; // Table r�pertoriant les param�tres attribu�s aux attributs quantitatifs
    
    private Hashtable m_tablePresenceObligatoire = null; // Table dont les cl�s sont les noms des attributs qualitatifs et quantitatifs dont la pr�sence est obligatoire
                                                         // (pour un attribut qualitatif, il suffit qu'un de ses items soit obligatoire pour qu'il soit consid�r� obligatoire)
    

    
    
    // Classe de stockage des param�tres sur les attributs qualitatifs :
    /**
     * Class of parameters for qualitative (categorical) attributes
     */
    public class ParametresItemsQualitatifs {
        
        public int m_iTypePriseEnCompte = 0; // Indicate the position of the item in that rule      
        public boolean m_bPresenceObligatoire = false;  // Indicate if the item must appear que l'item doit  faire partie of that rule
        
        public ParametresItemsQualitatifs() {
            m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
            m_bPresenceObligatoire = false;
        }
        
    }
    
    
    // Classe de stockage des param�tres sur les attributs quantitatifs :
    /**
     * Class of parameters for quantitative (numerical) attributes
     */
    public class ParametresAttributsQuantitatifs {
        
        public int m_iTypePriseEnCompte = 0; // Indique la position o� placer l'attribut dans la r�gle       
        public boolean m_bPresenceObligatoire = false;  // Indique que l'attribut doit obligatoirement faire partie de la r�gle
        
        public ParametresAttributsQuantitatifs() {
            m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
            m_bPresenceObligatoire = false;
        }
        
    }
    
    
    
    
    
    public PositionRuleParameters(ResolutionContext contexteResolution, int iTypeParametrage) {
        m_iTypeParametrage = iTypeParametrage;
        
        m_contexteResolution = contexteResolution;
        
        m_tableParametresItemsQualitatifs = new Hashtable();
        m_tableParametresAttributsQuantitatifs = new Hashtable();
        m_tablePresenceObligatoire = new Hashtable();
    }
    
    
    
        
    // Construit, ou adapte suite � une modification des attributs pris en compte dans la BD, 
    // une structure de donn�es sur 2 niveaux de tables de hachages, le premier sur le nom des
    // attributs et le second sur celui des items. Uniquement valable pour les attributs qualitatifs.
    /**
     * Build or adapt, following a modification of the attributes considered in the DB, 
	 * a data structure at 2 levels of hashing tables, the first on the name of
	 * the attributes, the second on the items. Only for the qualitative (categorical) attibutes.
     */
    public void GenererStructuresDonneesSelonBDPriseEnCompte() {
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonne = 0;
        int iIndiceItem = 0;
        DataColumn colonne = null;
        Hashtable sousTable = null;
        String [] tListeItems = null;
        boolean bEnregistrerParametres = false;
        
        
        if (m_contexteResolution.m_gestionnaireBD == null)
            return;
        
        // Dans le cas des informations de filtrage, on r�initialise les param�tres :
        if (m_iTypeParametrage == PARAMETRES_FILTRAGE_REGLES) {
            m_tableParametresItemsQualitatifs.clear();
            m_tableParametresAttributsQuantitatifs.clear();
            m_tablePresenceObligatoire.clear();
        }        
        
        //Number of items checked in Step 1
        iNombreColonnesPrisesEnCompte = m_contexteResolution.m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte();
        for (iIndiceColonne=0; iIndiceColonne < iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
            
        	//Get the column information about the selected column in step 1, i.e the data load step
            colonne = m_contexteResolution.m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            if (colonne.m_sNomColonne != null)
            {
                
                // ATTRIBUTS QUALITATIFS :
                
                if (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM) {
                
                    // Table des items qualitatifs :
                    
                    // Si l'attribut est d�j� r�pertori�, on ne modifie pas les informations le concernant,
                    // sinon on cr�e une nouvelle entr�e :
                    if (!m_tableParametresItemsQualitatifs.containsKey(colonne.m_sNomColonne)) {
                        
                        sousTable = new Hashtable();
                        m_tableParametresItemsQualitatifs.put(colonne.m_sNomColonne, sousTable);
                        
                        tListeItems = colonne.ConstituerTableauValeurs();
                        for (iIndiceItem=0; iIndiceItem<tListeItems.length; iIndiceItem++)
                            if (tListeItems[iIndiceItem] != null) {
                                
                                // Si on est en train de construire les param�tres de filtrage, on ne consid�re
                                // que les items retenus pour constituer les r�gles :
                                bEnregistrerParametres = true;
                                if (m_iTypeParametrage == PARAMETRES_FILTRAGE_REGLES)
                                    bEnregistrerParametres = ( m_contexteResolution.ObtenirTypePrisEnCompteItem(colonne.m_sNomColonne, tListeItems[iIndiceItem]) != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART );
                                   
                                if (bEnregistrerParametres) {
                                    parametreItemDefaut = new ParametresItemsQualitatifs();
                                    
                                    if (m_iTypeParametrage == PARAMETRES_FILTRAGE_REGLES) {
                                        parametreItemDefaut.m_iTypePriseEnCompte = m_contexteResolution.ObtenirTypePrisEnCompteItem(colonne.m_sNomColonne, tListeItems[iIndiceItem]);
                                        parametreItemDefaut.m_bPresenceObligatoire = m_contexteResolution.ObtenirPresenceObligatoireItem(colonne.m_sNomColonne, tListeItems[iIndiceItem]);
                                    }
                                    else {
                                        if ((tListeItems[iIndiceItem].trim()).equals(""))
                                            parametreItemDefaut.m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
                                        else
                                            parametreItemDefaut.m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;//PRISE_EN_COMPTE_ITEM_2_COTES;
                                        parametreItemDefaut.m_bPresenceObligatoire = false;
                                    }

                                    sousTable.put(tListeItems[iIndiceItem], parametreItemDefaut);
                                }                                
                            }
                        
                        
                        // Table des attributs quantitatifs :
                        
                        m_tableParametresAttributsQuantitatifs.remove(colonne.m_sNomColonne);
                    }
                }
                
                
                // ATTRIBUTS QUANTITATIFS :
                
                else if (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL) {
                    
                    // Table des items qualitatifs :
                    
                    m_tableParametresItemsQualitatifs.remove(colonne.m_sNomColonne);
                    
                    
                    // Table des attributs quantitatifs :
                        
                    if (!m_tableParametresAttributsQuantitatifs.containsKey(colonne.m_sNomColonne)) {
                        
                        // Si on est en train de construire les param�tres de filtrage, on ne consid�re
                        // que les attributs retenus pour constituer les r�gles :
                        bEnregistrerParametres = true;
                        if (m_iTypeParametrage == PARAMETRES_FILTRAGE_REGLES)
                            bEnregistrerParametres = ( m_contexteResolution.ObtenirTypePrisEnCompteAttribut(colonne.m_sNomColonne) != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART );
                        
                        if (bEnregistrerParametres) {
                            parametreQuantDefaut = new ParametresAttributsQuantitatifs();
                            
                            if (m_iTypeParametrage == PARAMETRES_FILTRAGE_REGLES) {
                                parametreQuantDefaut.m_iTypePriseEnCompte = m_contexteResolution.ObtenirTypePrisEnCompteAttribut(colonne.m_sNomColonne);
                                parametreQuantDefaut.m_bPresenceObligatoire = ( m_contexteResolution.ObtenirPresenceObligatoireAttribut(colonne.m_sNomColonne) == 1);
                            }
                            else {
                                parametreQuantDefaut.m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
                                parametreQuantDefaut.m_bPresenceObligatoire = false;
                                
                                // Si l'attribut ne contient que des valeurs manquantes, on ne le prend pas en compte :
                                if (colonne.m_iNombreValeursReellesCorrectes <= 0) {
                                    parametreQuantDefaut.m_iTypePriseEnCompte = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
                                    parametreQuantDefaut.m_bPresenceObligatoire = false;
                                }
                            }                            
                            
                            m_tableParametresAttributsQuantitatifs.put(colonne.m_sNomColonne, parametreQuantDefaut);
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
    }

    
    
    
    public void DefinirTypePrisEnCompteItem(String sAttribut, String sItem, int iTypePosition) {
        Hashtable sousTable = null;
        ParametresItemsQualitatifs parametres = null;
        
        if ( (sAttribut==null) || (sItem==null) )
            return;
        
        sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
        if (sousTable == null)
            return;
       
        parametres = (ParametresItemsQualitatifs)(sousTable.get(sItem));
        if (parametres == null)
            return;
                    
        // Modification de la valeur de prise en compte :
        parametres.m_iTypePriseEnCompte = iTypePosition;
    }
    
    
    /**
     * return categorical item's position in the rule
     * @param sAttribut
     * @param sItem
     * @return int
     */
    public int ObtenirTypePrisEnCompteItem(String sAttribut, String sItem) {
        int iTypePosition = 0;
        Hashtable sousTable = null;
        ParametresItemsQualitatifs parametres = null;
        
        if ( (sAttribut==null) || (sItem==null) )
            return 0;
        
        sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
        if (sousTable == null)
            return 0;
        
        parametres = (ParametresItemsQualitatifs)(sousTable.get(sItem));
        if (parametres == null)
            return 0;
        else
            return parametres.m_iTypePriseEnCompte;
    }
    

    
    public void DefinirTypePrisEnCompteAttribut(String sAttribut, int iTypePosition) {
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        Hashtable sousTable = null;
        Enumeration enumItems = null;
        
        // Attribut quantitatif :
        parametreQuantDefaut = (ParametresAttributsQuantitatifs)(m_tableParametresAttributsQuantitatifs.get(sAttribut));
        if (parametreQuantDefaut != null)
            parametreQuantDefaut.m_iTypePriseEnCompte = iTypePosition;
        
        // Attribut qualitatif :
        else {
            sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
            if (sousTable != null) {
                
                enumItems = sousTable.elements();
                if (enumItems.hasMoreElements()) {
                    
                    while ( enumItems.hasMoreElements() ) {
                        parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                        parametreItemDefaut.m_iTypePriseEnCompte = iTypePosition;
                    }
                 
                }
            }            
        }
    }
    
    
    /**
     * return numercial's position in the rule
     * @param sAttribut
     * @return int
     */
    public int ObtenirTypePrisEnCompteAttribut(String sAttribut) {
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        boolean bTypesTousPareils = false;
        int iTypePosition = 0;
        Hashtable sousTable = null;
        Enumeration enumItems = null;
        
        iTypePosition = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
        
        // Attribut quantitatif :
        parametreQuantDefaut = (ParametresAttributsQuantitatifs)(m_tableParametresAttributsQuantitatifs.get(sAttribut));
        if (parametreQuantDefaut != null)
            iTypePosition = parametreQuantDefaut.m_iTypePriseEnCompte;
        
        // Attribut qualitatif :
        else {
            sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
            if (sousTable != null) {
                
                enumItems = sousTable.elements();
                if (enumItems.hasMoreElements()) {
                    
                    bTypesTousPareils = true;
                    parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                    iTypePosition = parametreItemDefaut.m_iTypePriseEnCompte;
                    while ( (bTypesTousPareils) && enumItems.hasMoreElements() ) {
                        parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                        bTypesTousPareils = (iTypePosition == parametreItemDefaut.m_iTypePriseEnCompte);
                    }
                    
                    if (!bTypesTousPareils)
                        iTypePosition = ResolutionContext.PRISE_EN_COMPTE_INDEFINI;
                        
                }

            }            
        }

        
        return iTypePosition;
    }
    
    
    
    
    public void DefinirPresenceObligatoireItem(String sAttribut, String sItem, boolean bPresenceObligatoire) {
        Hashtable sousTable = null;
        ParametresItemsQualitatifs parametres = null;
        
        if ( (sAttribut==null) || (sItem==null) )
            return;
        
        sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
        if (sousTable == null)
            return;
       
        parametres = (ParametresItemsQualitatifs)(sousTable.get(sItem));
        if (parametres == null)
            return;
                    
        parametres.m_bPresenceObligatoire = bPresenceObligatoire;    
    }
    
    
    
    public void DefinirPresenceObligatoireAttribut(String sAttribut, boolean bPresenceObligatoire) {
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        Hashtable sousTable = null;
        Enumeration enumItems = null;
        
        // Attribut quantitatif :
        parametreQuantDefaut = (ParametresAttributsQuantitatifs)(m_tableParametresAttributsQuantitatifs.get(sAttribut));
        if (parametreQuantDefaut != null)
            parametreQuantDefaut.m_bPresenceObligatoire = bPresenceObligatoire;
        
        // Attribut qualitatif :
        else {
            sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
            if (sousTable != null) {
                
                enumItems = sousTable.elements();
                if (enumItems.hasMoreElements()) {
                    
                    while ( enumItems.hasMoreElements() ) {
                        parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                        if ( (!bPresenceObligatoire) || (parametreItemDefaut.m_iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART) )
                            parametreItemDefaut.m_bPresenceObligatoire = bPresenceObligatoire;
                    }
                 
                }
            }            
        }
    }
    
    
    
    public boolean ObtenirPresenceObligatoireItem(String sAttribut, String sItem) {
        boolean bPresenceObligatoire = false;
        Hashtable sousTable = null;
        ParametresItemsQualitatifs parametres = null;
        
        if ( (sAttribut==null) || (sItem==null) )
            return false;
        
        sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
        if (sousTable == null)
            return false;
        
        parametres = (ParametresItemsQualitatifs)(sousTable.get(sItem));
        if (parametres == null)
            return false;
        else
            return parametres.m_bPresenceObligatoire;
    }
    
    
    /**Whether present or not
     * @param sAttribut
     * @return 0 for False, 1 for True, -1 to indicate that all the values are not the same
     */
    public int ObtenirPresenceObligatoireAttribut(String sAttribut) {
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        boolean bTypesTousPareils = false;
        boolean bPresenceObligatoire = false;
        Hashtable sousTable = null;
        Enumeration enumItems = null;
        
        bTypesTousPareils = true;
        bPresenceObligatoire = false;
        
        // Attribut quantitatif :
        parametreQuantDefaut = (ParametresAttributsQuantitatifs)(m_tableParametresAttributsQuantitatifs.get(sAttribut));
        if (parametreQuantDefaut != null)
            bPresenceObligatoire = parametreQuantDefaut.m_bPresenceObligatoire;

        
        // Attribut qualitatif :
        else {
            sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sAttribut));
            if (sousTable != null) {
                
                enumItems = sousTable.elements();
                if (enumItems.hasMoreElements()) {
                    
                    parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                    bPresenceObligatoire = parametreItemDefaut.m_bPresenceObligatoire;
                    while ( (bTypesTousPareils) && enumItems.hasMoreElements() ) {
                        parametreItemDefaut = (ParametresItemsQualitatifs)enumItems.nextElement();
                        bTypesTousPareils = (bPresenceObligatoire == parametreItemDefaut.m_bPresenceObligatoire);
                    }
                       
                }

            }            
        }

        if (!bTypesTousPareils)
            return -1;
        else if (bPresenceObligatoire)
            return 1;
        else 
            return 0;
    }
    
  
    
    /**
     * Build the data structures optimizing the filtering process
     */
    public void MettreAJourDonneesInternesFiltre() {
        Enumeration enumAttributs = null;
        Enumeration enumItems = null;
        ParametresAttributsQuantitatifs parametreQuant = null;
        ParametresItemsQualitatifs parametreQual = null;
        Hashtable sousTable = null;        
        String sNomAttribut = null;
        boolean bFinEnumItems = false;
        
        m_tablePresenceObligatoire.clear();
        
        
        //Enumeration of mandatory quantitative attributes
        
        enumAttributs = m_tableParametresAttributsQuantitatifs.keys();
        while (enumAttributs.hasMoreElements()) {
             sNomAttribut = (String)enumAttributs.nextElement();
             
             parametreQuant = (ParametresAttributsQuantitatifs)m_tableParametresAttributsQuantitatifs.get(sNomAttribut);
             if (parametreQuant != null) {
                 if (   parametreQuant.m_bPresenceObligatoire
                    &&(parametreQuant.m_iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)  ) {
                    m_tablePresenceObligatoire.put(sNomAttribut, Boolean.FALSE);
                 }
             }
        }
        
        
        // Enumeration of mandatory qualitative attributes
        
        enumAttributs = m_tableParametresItemsQualitatifs.keys();
        while (enumAttributs.hasMoreElements()) {
            sNomAttribut = (String)enumAttributs.nextElement();
                    
            sousTable = (Hashtable)m_tableParametresItemsQualitatifs.get(sNomAttribut);
            if (sousTable != null) {
                
                enumItems = sousTable.elements();
                bFinEnumItems = false;
                while ( (!bFinEnumItems) && (enumItems.hasMoreElements()) ) {
                    parametreQual = (ParametresItemsQualitatifs)enumItems.nextElement();

                    if (   parametreQual.m_bPresenceObligatoire
                         &&(parametreQual.m_iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)  ) {
                        m_tablePresenceObligatoire.put(sNomAttribut, Boolean.FALSE);
                        bFinEnumItems = true;
                    }
                }
                
            }
        }

    }
    
    
    
    /**
     * Reset the table of mandatory presence for a new presence test
     */
    private void ReinitialiserTablePresenceObligatoire() {
        Enumeration enumAttributs = null;
        String sNomAttribut = null;
        
        enumAttributs = m_tablePresenceObligatoire.keys();
        while (enumAttributs.hasMoreElements()) {
             sNomAttribut = (String)enumAttributs.nextElement();        
             m_tablePresenceObligatoire.put(sNomAttribut, Boolean.FALSE);
        }
    }
        
        

    /**
     * Validate all the quantitative attributes in case they are not taken into consideration
     */
    private void ValiderAttributsQuantitatifs() {
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonne = 0;
        DataColumn colonne = null;
        String sNomColonne = null;
        
        if (m_contexteResolution.m_gestionnaireBD == null)
            return;

        iNombreColonnesPrisesEnCompte = m_contexteResolution.m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte();
        for (iIndiceColonne=0; iIndiceColonne<iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
            
            colonne = m_contexteResolution.m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            sNomColonne = colonne.m_sNomColonne;
            if (sNomColonne != null)
                if (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL)
                    if (m_tablePresenceObligatoire.containsKey(sNomColonne))
                        m_tablePresenceObligatoire.put(sNomColonne, Boolean.TRUE);  
            
        }
    }
    
    
    
    /**
     * Test wehther all the entries in the mandatory table are valid
     * @return boolean
     */
    private boolean TesterValidationTablePresenceObligatoire() {
        Enumeration enumAttributs = null;
        String sNomAttribut = null;
        boolean bTableValidee = false;
        
        bTableValidee = true;
        
        enumAttributs = m_tablePresenceObligatoire.elements();
        while ( bTableValidee && (enumAttributs.hasMoreElements()) )
             bTableValidee = ((Boolean)enumAttributs.nextElement()) == Boolean.TRUE;        
        
        return bTableValidee;
    }
    
    
    
    /**
     * Indicate the presence of a mandatory attribute
     * @param sNomAttribut
     */
    private void ValiderPresenceObligatoireAttribut(String sNomAttribut) {
        if ( m_tablePresenceObligatoire.containsKey(sNomAttribut) )
            m_tablePresenceObligatoire.put(sNomAttribut, Boolean.TRUE);
    }
        
    
    
    
    public void DefinirPositionnementPourTous(int iTypePosition, boolean bPresenceObligatoire) {
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonne = 0;
        DataColumn colonne = null;
        String sNomColonne = null;
     
        
        if (m_contexteResolution.m_gestionnaireBD == null)
            return;

        iNombreColonnesPrisesEnCompte = m_contexteResolution.m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte();
        for (iIndiceColonne=0; iIndiceColonne<iNombreColonnesPrisesEnCompte; iIndiceColonne++) {
            
            colonne = m_contexteResolution.m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            sNomColonne = colonne.m_sNomColonne;
            
            if (sNomColonne != null)
            {
                DefinirTypePrisEnCompteAttribut(sNomColonne, iTypePosition);
                DefinirPresenceObligatoireAttribut(sNomColonne, bPresenceObligatoire);
            } 
        }
 
    }
    
    
    
    /**
     * Check whether the filter allowing to position the attribite on the left or the right for the rule to generate
     */
    public boolean EstFiltreCoherent() {
        Enumeration enumItems = null;
        ParametresItemsQualitatifs parametreItem = null;
        Hashtable sousTable = null;        
        int iNombreColonnesPrisesEnCompte = 0;
        int iIndiceColonne = 0;
        DataColumn colonne = null;
        String sNomColonne = null;
        int iTypePosition = 0;
        int iNombreAttributsGauche = 0;
        int iNombreAttributsDroite = 0;
        int iNombreAttributsTotal = 0;
        boolean bItemAGauche = false;
        boolean bItemADroite = false;
                        
        if (m_contexteResolution.m_gestionnaireBD == null)
            return false;

        iNombreColonnesPrisesEnCompte = m_contexteResolution.m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte();
        
        iNombreAttributsGauche = 0;
        iNombreAttributsDroite = 0; 
        iNombreAttributsTotal = 0;
        iIndiceColonne=0;
        while ( ( (iNombreAttributsTotal<2) || (iNombreAttributsGauche<1) || (iNombreAttributsDroite<1) ) && (iIndiceColonne<iNombreColonnesPrisesEnCompte) ) {
            
            colonne = m_contexteResolution.m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            sNomColonne = colonne.m_sNomColonne;
            
            if (sNomColonne != null)
            {
                // Attributs quantitatifs :
                if (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL) {
                    
                    iTypePosition = ObtenirTypePrisEnCompteAttribut(sNomColonne);
                    if ( (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES) ) 
                        iNombreAttributsGauche++;
                    if ( (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES) ) 
                        iNombreAttributsDroite++;
                    if ( (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES) ) 
                        iNombreAttributsTotal++;
                    
                }                    
                
                // Attributs qualitatifs :
                else if (colonne.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM) {
        
                    sousTable = (Hashtable)(m_tableParametresItemsQualitatifs.get(sNomColonne));
                    if (sousTable != null) {
                        enumItems = sousTable.elements();
                        
                        bItemAGauche = false;
                        bItemADroite = false;
                        while ( (!(bItemAGauche && bItemADroite)) && enumItems.hasMoreElements() ) {
                            parametreItem = (ParametresItemsQualitatifs)enumItems.nextElement();
                            iTypePosition = parametreItem.m_iTypePriseEnCompte;
                            if ( (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES) ) 
                                bItemAGauche = true;
                            if ( (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE) || (iTypePosition==ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES) ) 
                                bItemADroite = true;
                        }
                    }
                    
                    if (bItemAGauche) 
                        iNombreAttributsGauche++;
                    if (bItemADroite) 
                        iNombreAttributsDroite++;
                    if (bItemAGauche || bItemADroite)
                        iNombreAttributsTotal++;
                }
            } 
            
            iIndiceColonne++;
        }
             
        return ( (iNombreAttributsTotal>=2) && (iNombreAttributsGauche>0) && (iNombreAttributsDroite>0) );
    }
    
    
    
    /**
     * Test whether an itemset fulfills the user filtering criteria
     * @param itemSet
     * @return boolean
     */
    public boolean EstItemSetValide(ItemSet itemSet) {
        int iIndiceItem = 0;
        ItemQualitative item = null;
        boolean bItemSetValide = false;
        String sNomAttribut = null;
        String sNomItem = null;
        
        bItemSetValide = true;
        
        // We re-initialise the counting table for mandatory attributes for this itemset,
		// then we validatr all the quantitative attributes since an itemset is purely qualitative:
        ReinitialiserTablePresenceObligatoire();
        ValiderAttributsQuantitatifs();
       
        iIndiceItem = 0;
        item = itemSet.ObtenirItem(0);
        while (item != null) {
            
            sNomAttribut = item.m_attributQual.ObtenirNom();
            sNomItem = item.ObtenirIdentifiantTexteItem();
            
            bItemSetValide = ( ObtenirTypePrisEnCompteItem(sNomAttribut, sNomItem) != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART );
            
            if (bItemSetValide) {
                     
                if (ObtenirPresenceObligatoireItem(sNomAttribut, sNomItem))
                    ValiderPresenceObligatoireAttribut(sNomAttribut);
                
                iIndiceItem++;
                item = itemSet.ObtenirItem(iIndiceItem);
            }
        }
        
        if (bItemSetValide)
            bItemSetValide = TesterValidationTablePresenceObligatoire();
        
        return bItemSetValide;        
    }

        
        
        
        
    /**
     * Test whether a rule fulfills the user filtering criteria
     * @param regle
     * @return boolean
     */
    public boolean EstRegleValide(AssociationRule regle) {
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        ParametresItemsQualitatifs parametreItemDefaut = null;
        ParametresAttributsQuantitatifs parametreQuantDefaut = null;
        String sNomAttribut = null;
        int iIndiceItem = 0;
        boolean bRegleValide = false;
        int iTypePosition = 0;
        int iEtapeTestRegle = 0;
        int iNombreItems = 0;
        int iTypeTestEtape = 0;

        
        if (regle == null)
            return false;

        bRegleValide = true;

        // On r�initialise la table de comptabilisation des attributs obligatoires pour cette r�gle :
        ReinitialiserTablePresenceObligatoire();
        
        
        // 2-step test: first on the left and then on the right
        iEtapeTestRegle=0;
        while ( bRegleValide && (iEtapeTestRegle<2) ) {
            
            if (iEtapeTestRegle==0) {
                iNombreItems = regle.m_iNombreItemsGauche;
                iTypeTestEtape = ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE;
            }
            else {
                iNombreItems = regle.m_iNombreItemsDroite;
                iTypeTestEtape = ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE;
            }
             
            iIndiceItem=0;
            while ( (bRegleValide) && (iIndiceItem<iNombreItems) ) {
            
                if (iEtapeTestRegle==0)
                    item = regle.ObtenirItemGauche(iIndiceItem);
                else
                    item = regle.ObtenirItemDroite(iIndiceItem);

                if (item == null)
                    bRegleValide = false;
                else {

                    iTypePosition = ResolutionContext.PRISE_EN_COMPTE_INDEFINI;

                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                        itemQuant = (ItemQuantitative)item;  
                        sNomAttribut = itemQuant.m_attributQuant.ObtenirNom();
                        iTypePosition = ObtenirTypePrisEnCompteAttribut(sNomAttribut);
                        if (ObtenirPresenceObligatoireAttribut(sNomAttribut) == 1)
                            ValiderPresenceObligatoireAttribut(sNomAttribut);
                    }

                    else if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                        itemQual = (ItemQualitative)item;  
                        sNomAttribut = itemQual.m_attributQual.ObtenirNom();
                        iTypePosition = ObtenirTypePrisEnCompteItem(sNomAttribut, itemQual.ObtenirIdentifiantTexteItem() );
                        if (ObtenirPresenceObligatoireItem(sNomAttribut, itemQual.ObtenirIdentifiantTexteItem() ) )
                            ValiderPresenceObligatoireAttribut(sNomAttribut);
                    }

                    bRegleValide =    (iTypePosition == iTypeTestEtape)
                                   || (iTypePosition == ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES);

                    iIndiceItem++;
                }
            }
            
            iEtapeTestRegle++;
        }
        
        if (bRegleValide)
            bRegleValide = TesterValidationTablePresenceObligatoire();
        
        return bRegleValide;
    }
    
}
