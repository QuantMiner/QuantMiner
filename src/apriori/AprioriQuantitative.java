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

import java.sql.*;
import java.util.*;
import java.util.*;

import src.database.*;
import src.solver.*;


public class AprioriQuantitative {

    ArrayList m_listeAttributsQual = null;
    ArrayList m_listeAttributsQuant = null;

    TableItems m_tableItems = null;
    ArrayList m_listeListeItemSets = null;

    public DatabaseAdmin m_gestionnaireBD = null;
    private ResolutionContext m_contexteResolution = null;
    
    
    float m_fMinSupp = 0.0f;
    int m_iNombreTransactions = 0;
    
    
    // Classe de base permettant l'ex�cution d'un code particulier pendant la g�n�ration des itemsets :
    static public class TraitementExternePendantCalcul {
        // Renvoie faux si le traitement externe demande l'arr�t des calculs :
        public boolean ExecuterTraitementExterne() { return true; }
    }
    TraitementExternePendantCalcul m_traitementExterne = null;
    
    
    
    public AprioriQuantitative(ResolutionContext contexteResolution) {
        m_listeAttributsQual = new ArrayList();
        m_listeAttributsQuant = new ArrayList();
        m_tableItems = new TableItems();
        m_listeListeItemSets = new ArrayList();
        m_fMinSupp = 0.0f;
        m_iNombreTransactions = 0;
        m_traitementExterne = null;
        
        m_contexteResolution = contexteResolution;
        if (m_contexteResolution != null)
            m_gestionnaireBD = m_contexteResolution.m_gestionnaireBD;
    }
    
    
    /** specify external treatment
     * @param traitementExterne External Treatment For Calculating
     */
    public void SpecifierTraitementExterne(TraitementExternePendantCalcul traitementExterne) {
        m_traitementExterne = traitementExterne;
    }
    
    
    /**
     * Run Pretreatment
     * @param bGenererItemSetsSingletons
     */
    public void ExecuterPretraitement(boolean bGenererItemSetsSingletons) {
        int iNombreAttributsQual = 0;
        int iIndiceAttributQual = 0;
        int iNombreColonnes = 0;
        int iIndiceColonne = 0;
        int iTypePriseEnCompte = 0;
        AttributQualitative attributQual = null;
        AttributQuantitative attributQuant = null;
        DataColumn colonneDonnees = null;
        m_iNombreTransactions = m_gestionnaireBD.ObtenirNombreLignes();  //obtain the number of lines
        
        iNombreColonnes = m_gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte(); //obtain the number of selected columns??
        
        for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnes; iIndiceColonne++) { 
            
            colonneDonnees = m_gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
            iTypePriseEnCompte = m_contexteResolution.ObtenirTypePrisEnCompteAttribut(colonneDonnees.m_sNomColonne);
            
            switch ( colonneDonnees.m_iTypeValeurs) {
                case DatabaseAdmin.TYPE_VALEURS_COLONNE_ITEM : //add categorical attribute to categorical list
                    if (iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART) {
                        attributQual = new AttributQualitative( colonneDonnees.m_sNomColonne, colonneDonnees );
                        m_listeAttributsQual.add(attributQual);
                    }
                    break;
                    
                case DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL : //add quantitative attribute to categorical list
                    if (iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART) {
                        attributQuant = new AttributQuantitative( colonneDonnees.m_sNomColonne, colonneDonnees );
                        m_listeAttributsQuant.add(attributQuant);
                    }
                    break;
            }
             
        }
        

        iNombreAttributsQual = m_listeAttributsQual.size();
        iIndiceAttributQual = 0;
        for (iIndiceAttributQual = 0; iIndiceAttributQual < iNombreAttributsQual; iIndiceAttributQual++) {
            
            attributQual = (AttributQualitative)m_listeAttributsQual.get(iIndiceAttributQual);
            attributQual.GenererItems(m_tableItems);
        
        }

        
        if (bGenererItemSetsSingletons)
            GenererNouvelleListeItemSets();  //generate new itemset list
    }
  
    
   /**
    * Calculate Distribution Items
    * @param iIndiceRepartition Index Distribution
    * @param iNombreItems Number of Items
    * @return
    */
    public static boolean [] CalculerRepartitionItems(int iIndiceRepartition, int iNombreItems) {
        boolean [] tRepartitionItems = null;
        int iIndiceItem = 0;
        int iIndiceMaxPourItem = 0;
        int iNombreItemsRestants = 0;
        boolean bTestPremierItem = true; // Vrai tant qu'on peut contruire l'ensemble contenant tous les attributs, qu'il faut prendre garde d'�liminer
        
        if (iNombreItems==0) return null;
        
        tRepartitionItems = new boolean [iNombreItems];
        Arrays.fill(tRepartitionItems, false);
        
        iNombreItemsRestants = iNombreItems;
        iIndiceItem = 0;
        bTestPremierItem = true;
        while (iIndiceItem<iNombreItems) {
            
            iIndiceMaxPourItem = 1 << (iNombreItems - iIndiceItem - 1);
            
            if (bTestPremierItem) iIndiceMaxPourItem--;
            
            if (iIndiceRepartition==0) {
                tRepartitionItems[iIndiceItem] = true;
                iNombreItemsRestants--;
                iIndiceItem = iNombreItems;    // Pour provoquer la fin de l'algo
            }
            
            else if (iIndiceRepartition<iIndiceMaxPourItem) {
                tRepartitionItems[iIndiceItem] = true;
                iNombreItemsRestants--;
                iIndiceRepartition--;
            }
            
            else {
                iIndiceRepartition -= iIndiceMaxPourItem;
                bTestPremierItem = false;
            }
 
            iIndiceItem++;
        }
        
        if (iNombreItemsRestants==iNombreItems)
            return null;
        else
            return tRepartitionItems;
    }
   
    
    
    // Calcule tous les ensembles d'items possibles, chaque ensemble �tant constitu� de 'iNombrePlaces' �l�ments
    // (renvoie un tableau indiquant la pr�sence ou non du i-�me item dans l'ensemble) :
    /**
     * Calculate Sets Items
     * @param iIndiceRepartition Index Distribution
     * @param iNombreItems Number of items
     * @param iNombrePlaces Number of places
     */
    public static boolean [] CalculerEnsemblesItems(int iIndiceRepartition, int iNombreItems, int iNombrePlaces) {
        boolean [] tRepartitionItems = null;
        boolean bItemAffecte = false;        
        int iIndiceItem = 0;
        int iNombreItemsRestants = 0;
        int iNombrePlacesRestantes = 0;
        int iCompteur = 0;
        int factN = 0, factP = 0, factN_P = 0, iCNP = 0; // Variables d�di�es au calcul des combinaisons de p valeurs parmi n
        
        if (iNombreItems==0) return null;
 
        tRepartitionItems = new boolean [iNombreItems];
        Arrays.fill(tRepartitionItems, false);
        
        iIndiceItem = 0;
        iNombreItemsRestants = iNombreItems;
        iNombrePlacesRestantes = iNombrePlaces;
        while ( (iNombrePlacesRestantes>0) && (iNombreItemsRestants>=iNombrePlacesRestantes) && (iIndiceItem<iNombreItems) ) {
            
            if (iNombreItemsRestants>iNombrePlacesRestantes) {

                factN = factP = factN_P = 1;

                for (iCompteur=2;iCompteur<iNombreItemsRestants;iCompteur++)
                    factN *= iCompteur;

                for (iCompteur=2;iCompteur<iNombrePlacesRestantes;iCompteur++)
                    factP *= iCompteur;

                for (iCompteur=2;iCompteur<=(iNombreItemsRestants-iNombrePlacesRestantes);iCompteur++)
                    factN_P *= iCompteur;

                iCNP = factN / (factP * factN_P);

            }
            // Sinon cas iNombreItemsRestants == iNombrePlacesRestantes :
            else
                iCNP = 1;
                
            if (iIndiceRepartition < iCNP) {
                tRepartitionItems[ iIndiceItem ] = true;
                iNombrePlacesRestantes--;
            }
            else
                iIndiceRepartition -= iCNP;
                
            iIndiceItem++;
            iNombreItemsRestants--;
        }
        
            
        if (iNombrePlacesRestantes==iNombrePlaces)
            return null;
        else
            return tRepartitionItems;
    }
    
    
    /** set minimum support
     * @param fMinSupp Minimum Support value
     */
    public void SpecifierSupportMinimal(float fMinSupp) {
        m_fMinSupp = fMinSupp;
    }
    
    
    
    public float ObtenirSupportMinimal() {
        return m_fMinSupp;
    }
        
    
    /**Write Frequents List*/
    public String EcrireListeFrequents() {
        String sTexteListeFrequents = null;
        int iTailleMaxItemSets = 0;
        int iIndiceListesItemSets = 0;
        int iNombreItemSets = 0;
        int iIndiceItemSet = 0;
        ArrayList listeItemSets = null;
        ItemSet itemset = null;
        
        sTexteListeFrequents = "";
        
        iTailleMaxItemSets = m_listeListeItemSets.size();
        
        for (iIndiceListesItemSets=0; iIndiceListesItemSets<iTailleMaxItemSets; iIndiceListesItemSets++) {
            listeItemSets = (ArrayList)m_listeListeItemSets.get(iIndiceListesItemSets);
            
            iNombreItemSets = listeItemSets.size();
            for (iIndiceItemSet=0; iIndiceItemSet<iNombreItemSets; iIndiceItemSet++) {
                
                itemset = (ItemSet)listeItemSets.get(iIndiceItemSet);
                
                if ( itemset.m_iSupport >= (int)(m_fMinSupp*(float)m_iNombreTransactions) ) {
                    sTexteListeFrequents += itemset.EcrireItemSet(m_iNombreTransactions, true);
                    sTexteListeFrequents += "\n";
                }
                
            }
            
            if (iNombreItemSets > 0)
                sTexteListeFrequents += "\n";            
        }
        
        return sTexteListeFrequents;
    }
    
    
    /**
     * Remove Not Frequent Sets
     * @param iIndiceListe index of the ListItemSet
     */
    void RetirerNonFrequentsDeListeItemSets(int iIndiceListe) {
        
        ArrayList listeItemSets = null;
        ArrayList listeItemSetsTemp = null;
        ItemSet itemSet = null;
        int iIndiceItemSet = 0;
        int iNombreItemSets = 0;
        
        listeItemSets = (ArrayList)m_listeListeItemSets.get(iIndiceListe);
        listeItemSetsTemp = new ArrayList();
        iNombreItemSets = listeItemSets.size();
        
        for (iIndiceItemSet=0; iIndiceItemSet<iNombreItemSets; iIndiceItemSet++) {
            
            itemSet = (ItemSet)listeItemSets.get(iIndiceItemSet);
            
            // On ne conserve que les itemSets fr�quents :
            if ( itemSet.m_iSupport >= (int)(m_fMinSupp*(float)m_iNombreTransactions) )
                listeItemSetsTemp.add(itemSet);
            
        }
        
        // On remplace l'ancienne liste par la nouvelle :
        m_listeListeItemSets.set(iIndiceListe, listeItemSetsTemp);
    }
    
    
    
    // Retire tous les itemsets qui ne satisfont pas les conditions donn�es par l'utilsateur
    // dans le tableau de filtrage :
    public void ElaguerItemsetsSelonFiltre() {
        ArrayList listeItemSets = null;
        ArrayList listeItemSetsTemp = null;
        ItemSet itemSet = null;
        int iIndiceListeItemSets = 0;
        int iNombreListesItemSets = 0;
        int iIndiceItemSet = 0;
        int iNombreItemSets = 0;
        
        iNombreListesItemSets = m_listeListeItemSets.size();
        
        for (iIndiceListeItemSets=0; iIndiceListeItemSets<iNombreListesItemSets; iIndiceListeItemSets++) {
            
            listeItemSets = (ArrayList)m_listeListeItemSets.get(iIndiceListeItemSets);
            listeItemSetsTemp = new ArrayList();
            iNombreItemSets = listeItemSets.size();
        
            for (iIndiceItemSet=0; iIndiceItemSet<iNombreItemSets; iIndiceItemSet++) {

                itemSet = (ItemSet)listeItemSets.get(iIndiceItemSet);
                if (itemSet != null)
                    if (m_contexteResolution.EstItemSetValide(itemSet))
                        listeItemSetsTemp.add(itemSet);

            }
        
            // On remplace l'ancienne liste par la nouvelle :
            m_listeListeItemSets.set(iIndiceListeItemSets, listeItemSetsTemp);        
        }
    }
    
    
    /**Generate new item sets*/
    public boolean GenererNouvelleListeItemSets() {
        
        ArrayList nouvelleListe = null;
        ArrayList precedenteListe = null;
        int iSupportItem = 0;
        int iIndiceListe = 0;
        int iNombreAttributsQual = 0;
        int iIndiceAttributQual = 0;
        ItemSet itemSet = null;
        ItemQualitative item = null;
        AttributQualitative attributQual = null;
      
        iIndiceListe = m_listeListeItemSets.size();
        iNombreAttributsQual = m_listeAttributsQual.size();
         
        
        // On r�initialise les pointeurs de comptage des itemsets, qui mettaient en relation
        // toute valeur d'un attribut avec l'ensemble des itemsets qui le contenaient :
        for (iIndiceAttributQual = 0; iIndiceAttributQual < iNombreAttributsQual; iIndiceAttributQual++) {
            attributQual = (AttributQualitative)m_listeAttributsQual.get(iIndiceAttributQual);
            attributQual.ReinitialiserListeLiensItemSets();
        }
        
        
        nouvelleListe = new ArrayList();
        
        // Itemsets de taille 1 :
        if (iIndiceListe == 0) {
            
            item = m_tableItems.ObtenirPremierItem();
            while (item != null) {
                    
                if (  m_contexteResolution.ObtenirTypePrisEnCompteItem(item.m_attributQual.m_sNomAttribut, item.ObtenirIdentifiantTexteItem())
                     != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART ) {
                         
                    itemSet = new ItemSet(1);
                    itemSet.SpecifierItem(item);

                    iSupportItem = item.m_attributQual.ObtenirSupportItem(item.m_iIndiceValeur);
                
                    itemSet.SpecifierSupport( iSupportItem );
                    item.m_attributQual.AjouterLienVersItemSet(item.m_iIndiceValeur, itemSet);

                    nouvelleListe.add(itemSet);
                }

                item = (ItemQualitative)(item.m_itemSuivant);
            }        
                
        }
            
        
        // Combinaisons d'items :
        else {
            
            int iIndiceParcours1 = 0, iIndiceParcours2 = 0;
            int iNombreItemSets = 0;
            int iTailleNouveauxItemSets = 0;
            int iNombreItemsIdentiques = 0;
            int iIndiceItem = 0;
            boolean bContinuerParcours = false;
            ItemSet itemSet1 = null, itemSet2 = null;
            
            bContinuerParcours = true;
            
            
            // On �pure la liste des (K-1)-itemSets de ceux qui ne sont pas fr�quents :
            
            RetirerNonFrequentsDeListeItemSets(iIndiceListe-1);

            
            // On g�n�re la liste des K-itemSets � partir de la pr�c�dente contenant les (K-1)-itemSets fr�quents :

            precedenteListe = (ArrayList)m_listeListeItemSets.get(iIndiceListe-1);
            iNombreItemSets = precedenteListe.size();
            iTailleNouveauxItemSets = iIndiceListe + 1;
            iNombreItemsIdentiques = iIndiceListe - 1;
            
            iIndiceParcours1=0;
            while ( bContinuerParcours && (iIndiceParcours1<iNombreItemSets) ) {
                
                itemSet1 = (ItemSet)precedenteListe.get(iIndiceParcours1);
                
                for (iIndiceParcours2=iIndiceParcours1+1;iIndiceParcours2<iNombreItemSets;iIndiceParcours2++) {
                    
                    itemSet2 = (ItemSet)precedenteListe.get(iIndiceParcours2);
                    
                    if (itemSet1.EstGenerateurCandidatAvec(itemSet2)) {
                    
                        // On doit aussi v�rifier que les 2 dernier items (ceux qui sont diff�rents
                        // l'un de l'autre) ne correspondent pas � des valeurs issues d'un 
                        // m�me attribut de la BD (les valeurs d'un m�me attribut sont exclusives).
                        // Il n'est pas utile de les comparer aux items communs
                        // puisque par "r�cursion" les (K-1)-itemSet sont
                        // d�j� constitu�es d'items r�pondant � cette imp�rative.
                        if ( !itemSet1.m_listeItems[iNombreItemsIdentiques].m_attributQual.equals(
                              itemSet2.m_listeItems[iNombreItemsIdentiques].m_attributQual) )
                        {
                             itemSet = new ItemSet(iTailleNouveauxItemSets);
                        
                            // Le nouvel itemSet est constitu� des k-2 premiers items communs aux deux
                            // (K-1)-itemSets g�n�rateurs... :
                            for (iIndiceItem=0;iIndiceItem<iNombreItemsIdentiques;iIndiceItem++) {
                                item = itemSet1.m_listeItems[iIndiceItem];
                                itemSet.SpecifierItem(item);
                                item.m_attributQual.AjouterLienVersItemSet(item.m_iIndiceValeur, itemSet);
                          }
                        
                            // ... auxquels on ajoute les deux items qui ne sont pas en commun :

                            item = itemSet1.m_listeItems[iNombreItemsIdentiques];
                            itemSet.SpecifierItem(item);
                            item.m_attributQual.AjouterLienVersItemSet(item.m_iIndiceValeur, itemSet);
                            
                            item = itemSet2.m_listeItems[iNombreItemsIdentiques];
                            itemSet.SpecifierItem(item);
                            item.m_attributQual.AjouterLienVersItemSet(item.m_iIndiceValeur, itemSet);

                            itemSet.SpecifierSupport(0);

                            nouvelleListe.add(itemSet);
       
                        }     
                    }   
                }
                
                iIndiceParcours1++;
                if (m_traitementExterne != null)
                    bContinuerParcours = m_traitementExterne.ExecuterTraitementExterne();
                    
            }
           
      
           
            // On effectue une nouvelle passe de lecture de la BD afin de calculer les nouveaux supports :
            
            int iIndiceTransaction = 0;            
            int iNombreTransactions = 0;
            
            iNombreAttributsQual = m_listeAttributsQual.size();
            iIndiceAttributQual = 0;
            attributQual = null;
            
            // Lecture ligne par ligne...
            iNombreTransactions = m_gestionnaireBD.ObtenirNombreLignes();
            iIndiceTransaction = 0;
            
            if (bContinuerParcours) 
                for (iIndiceTransaction=0; iIndiceTransaction<iNombreTransactions; iIndiceTransaction++) {
                
                    // ... puis attribut par attribut :
                    for (iIndiceAttributQual=0;iIndiceAttributQual<iNombreAttributsQual;iIndiceAttributQual++) {

                        attributQual = (AttributQualitative)m_listeAttributsQual.get(iIndiceAttributQual);
                        if (attributQual!=null)
                            attributQual.ComptabiliserOcurrenceValeur(iIndiceTransaction, attributQual.m_colonneDonnees.m_tIDQualitatif[iIndiceTransaction]);

                    }

                }

        }
        

        if (!nouvelleListe.isEmpty())
        {
            m_listeListeItemSets.add(nouvelleListe);
            return true;
        }
        else
            return false;
    }
    
   /**
    * Frequent recover 
    * @param iTailleFrequent Frequent size
    * @param iIndiceFrequent Frequent index
    * @return an Item Set
    */
   public ItemSet RecupererFrequent(int iTailleFrequent, int iIndiceFrequent) {
        int iDimensionMax = 0;
        int iNombreFrequents = 0;
        ArrayList listeItemSets = null;
        
        iDimensionMax = m_listeListeItemSets.size();
        if ( (iTailleFrequent<=0) || (iTailleFrequent>iDimensionMax) )
            return null;

        listeItemSets = (ArrayList)m_listeListeItemSets.get( (iTailleFrequent-1) );
        
        iNombreFrequents = listeItemSets.size();
        if ( (iIndiceFrequent<0) || (iIndiceFrequent>=iNombreFrequents) )
            return null;
        
        return (ItemSet)listeItemSets.get(iIndiceFrequent);
    }
    
    
   
    // Recherche parmi la liste des fr�quents l'itemset contenant les items pass�s en param�tre :
   /**
    * Search through the list of the frquents itemset containing items in passs paramtre
    * @param items An array of Qualitative item
    */
    public ItemSet RechercherFrequent(ItemQualitative [] items) {
        int iTailleFrequent = 0;
        int iDimensionMax = 0;
        ArrayList listeItemSets = null;
        ItemSet itemSet = null;
        boolean bItemSetTrouve = false;
        int iNombreItemSets = 0;
        int iIndiceItemSet = 0;
        ItemQualitative item = null;
        int iIndiceItem = 0;
        boolean bItemTrouveDansItemSet = false;
        int iIndiceItemDansItemSet = 0;        
        
        iTailleFrequent = items.length;
        iDimensionMax = m_listeListeItemSets.size();
        if ( (iTailleFrequent<=0) || (iTailleFrequent>iDimensionMax) )
            return null;
        
        listeItemSets = (ArrayList)m_listeListeItemSets.get( (iTailleFrequent-1) );

        iNombreItemSets = listeItemSets.size();
        iIndiceItemSet = 0;
        itemSet = null;
        bItemSetTrouve = false;
        while ( (!bItemSetTrouve) && (iIndiceItemSet<iNombreItemSets) ) {
            itemSet = (ItemSet)listeItemSets.get(iIndiceItemSet);
            
            // On v�rifie si chaque item est pr�sent dans l'itemset :
            iIndiceItem = 0;
            bItemSetTrouve = true;
            while ( (bItemSetTrouve) && (iIndiceItem<iTailleFrequent) ) {
                item = items[iIndiceItem];
                
                // On recherche la pr�sence de l'item dans l'itemset :
                bItemTrouveDansItemSet = false;
                iIndiceItemDansItemSet = 0;
                while ( (!bItemTrouveDansItemSet) && (iIndiceItemDansItemSet<iTailleFrequent) ) {
                    bItemTrouveDansItemSet = item.equals( itemSet.m_listeItems[iIndiceItemDansItemSet] );
                    iIndiceItemDansItemSet++;                
                }
                
                bItemSetTrouve = bItemTrouveDansItemSet;
                iIndiceItem++;
            }
            
            iIndiceItemSet++;
        }
        
        if (bItemSetTrouve)
            return itemSet;
        else
            return null;
    }
    
        
    /**
     * Obtain quantitative attributes
     * @param iIndiceQuant Index of the quantitative attribute
     * @return Quantitative attribute
     */
    public AttributQuantitative ObtenirAttributQuantitatif(int iIndiceQuant) {
        if ( (iIndiceQuant>=0) && (iIndiceQuant<m_listeAttributsQuant.size()) )
            return (AttributQuantitative)m_listeAttributsQuant.get(iIndiceQuant);
        else
            return null;
    }
    
    
    
    public int ObtenirNombreAttributsQuantitatifs() {
        if (m_listeAttributsQuant==null)
            return 0;
        else
            return m_listeAttributsQuant.size();
    }
    
    
    /**
     * Obtaining quantitative attributes by Name
     * @param sNomAttributQuant
     * @return quantitative attribute
     */
    public AttributQuantitative ObtenirAttributQuantitatifDepuisNom(String sNomAttributQuant) {
        AttributQuantitative attribut = null;
        int iNombreAttributsQuant = 0;
        int iIndiceAttributQuant = 0;
        
        if (sNomAttributQuant == null)
            return null;
        
        iNombreAttributsQuant = m_listeAttributsQuant.size();
        
        attribut = null;
        iIndiceAttributQuant = 0;
        while ( (attribut==null) && (iIndiceAttributQuant<iNombreAttributsQuant) ) {
            attribut = (AttributQuantitative)m_listeAttributsQuant.get(iIndiceAttributQuant);
            if (attribut != null)
                if ( !sNomAttributQuant.equals(attribut.ObtenirNom()) )
                    attribut = null;
            iIndiceAttributQuant++;
        }
        
        return attribut;
    }
    
    
    /**
     * Get Attribute From Name Qualitative
     * @param sNomAttributQual Name of Quanlitative Attribute
     * @return Qualitative attribute
     */
    public AttributQualitative ObtenirAttributQualitatifDepuisNom(String sNomAttributQual) {
        AttributQualitative attribut = null;
        int iNombreAttributsQual = 0;
        int iIndiceAttributQual = 0;
        
        if (sNomAttributQual == null)
            return null;
        
        iNombreAttributsQual = m_listeAttributsQual.size();
        
        attribut = null;
        iIndiceAttributQual = 0;
        while ( (attribut==null) && (iIndiceAttributQual<iNombreAttributsQual) ) {
            attribut = (AttributQualitative)m_listeAttributsQual.get(iIndiceAttributQual);
            if (attribut != null)
                if ( !sNomAttributQual.equals(attribut.ObtenirNom()) )
                    attribut = null;
            iIndiceAttributQual++;
        }
        
        return attribut; 
    }
    
    
    /**
     * Return Qualitative Item from AttributQualitatif
     * @param attribut Quanlitative Attribute
     * @param iIndiceValeur Index
     * @return Qualitative Item
     */
    public ItemQualitative ObtenirItem(AttributQualitative attribut, short iIndiceValeur) {
        ItemQualitative item = null;
        boolean bItemTrouve = false;
        
        bItemTrouve = false;
        item = m_tableItems.ObtenirPremierItem();
        while ( (!bItemTrouve) && (item != null) ) {
            
            // On teste l'�galit� de l'attribut puis celle de l'item lui-meme :
            if ( (Object)item.m_attributQual == (Object)attribut )
                bItemTrouve = ( item.m_iIndiceValeur == iIndiceValeur );
            
            if (!bItemTrouve)
                item = (ItemQualitative)(item.m_itemSuivant);
        } 
        
        return item;
    }
    
}
