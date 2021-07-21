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

import java.util.*;

import src.database.*;
import src.solver.*;




public class AssociationRule {
    
    public Item [] m_tItemsGauche = null;  //left item
    public Item [] m_tItemsDroite = null;  //right item
    public int m_iNombreItemsGauche = 0;   //number of left item
    public int m_iNombreItemsDroite = 0;   //number of right items
    
    public int m_iOccurrences = 0; // number de fois o� la r�gle est v�rifi�e (support non relative) 
    public float m_fSupport = 0.0f;
    public float m_fConfiance = 0.0f;
    
    public int m_iOccurrencesGauche = 0;
    public int m_iOccurrencesDroite = 0;
    public int m_iOccurrences_Gauche_NonDroite = 0;
    public int m_iOccurrences_NonGauche_Droite = 0;
    public int m_iOccurrences_NonGauche_NonDroite = 0;    
 
    public int m_iNombreDisjonctionsGauche = 0;
    public int m_iNombreDisjonctionsDroite = 0;
    public int m_iNombreDisjonctionsGaucheValides = 0;
    public int m_iNombreDisjonctionsDroiteValides = 0;    
    
    
    
    public AssociationRule(int iNombreItemsGauche, int iNombreItemsDroite, int iNombreDisjonctionsGauche, int iNombreDisjonctionsDroite) {
        
        if ( (iNombreItemsGauche <= 0) || (iNombreItemsDroite <= 0) ) {
            m_iNombreItemsGauche = 0;
            m_iNombreItemsDroite = 0;
            m_tItemsGauche = null;
            m_tItemsDroite = null;
            m_iNombreDisjonctionsGauche = 0;
            m_iNombreDisjonctionsDroite = 0;
        }
        else {
            m_iNombreItemsGauche = iNombreItemsGauche;
            m_iNombreItemsDroite = iNombreItemsDroite;
            m_tItemsGauche = new Item [m_iNombreItemsGauche];
            m_tItemsDroite = new Item [m_iNombreItemsDroite];
            Arrays.fill(m_tItemsGauche, null);
            Arrays.fill(m_tItemsDroite, null);
            m_iNombreDisjonctionsGauche = iNombreDisjonctionsGauche;
            m_iNombreDisjonctionsDroite = iNombreDisjonctionsDroite;
        }
        
        m_iNombreDisjonctionsGaucheValides = m_iNombreDisjonctionsGauche;
        m_iNombreDisjonctionsDroiteValides = m_iNombreDisjonctionsDroite;
            
        m_iOccurrences = 0;
        m_fSupport = 0.0f;
        m_fConfiance = 0.0f;
        m_iOccurrencesGauche = 0;
        m_iOccurrencesDroite = 0;
        m_iOccurrences_Gauche_NonDroite = 0;
        m_iOccurrences_NonGauche_Droite = 0;
        m_iOccurrences_NonGauche_NonDroite = 0;
    }
    

    
    // Construct de copie :
    public AssociationRule(AssociationRule regle) {
        this.CopierRegleAssociation(regle);
    }
    
    
    
    // Construct de copie :
    public void CopierRegleAssociation(AssociationRule regle) {
        int iIndiceItem = 0;
        Item item = null;
                
        if (regle != null) {
            
            this.m_iNombreItemsGauche = regle.m_iNombreItemsGauche;
            this.m_iNombreItemsDroite = regle.m_iNombreItemsDroite;
            
            this.m_tItemsGauche = new Item [m_iNombreItemsGauche];
            this.m_tItemsDroite = new Item [m_iNombreItemsDroite];
            
            // Copie des items de la partie gauche :
            for (iIndiceItem = 0; iIndiceItem < m_iNombreItemsGauche; iIndiceItem++) {
                item = regle.m_tItemsGauche[iIndiceItem];
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF)
                    this.m_tItemsGauche[iIndiceItem] = new ItemQuantitative((ItemQuantitative)item);
                else
                    this.m_tItemsGauche[iIndiceItem] = item;
            } 
                  
            for (iIndiceItem=0; iIndiceItem<m_iNombreItemsDroite; iIndiceItem++) {
                item = regle.m_tItemsDroite[iIndiceItem];
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF)
                    this.m_tItemsDroite[iIndiceItem] = new ItemQuantitative((ItemQuantitative)item);
                else
                    this.m_tItemsDroite[iIndiceItem] = item;
            }
           
            this.m_iOccurrences = regle.m_iOccurrences;
            this.m_fSupport = regle.m_fSupport;
            this.m_fConfiance = regle.m_fConfiance;
            this.m_iOccurrencesGauche = regle.m_iOccurrencesGauche;
            this.m_iOccurrencesDroite = regle.m_iOccurrencesDroite;
            this.m_iOccurrences_Gauche_NonDroite = regle.m_iOccurrences_Gauche_NonDroite;
            this.m_iOccurrences_NonGauche_Droite = regle.m_iOccurrences_NonGauche_Droite;
            this.m_iOccurrences_NonGauche_NonDroite = regle.m_iOccurrences_NonGauche_NonDroite; 
            this.m_iNombreDisjonctionsGauche = regle.m_iNombreDisjonctionsGauche;
            this.m_iNombreDisjonctionsDroite = regle.m_iNombreDisjonctionsDroite;
            this.m_iNombreDisjonctionsGaucheValides = regle.m_iNombreDisjonctionsGaucheValides;
            this.m_iNombreDisjonctionsDroiteValides = regle.m_iNombreDisjonctionsDroiteValides;
        }
        else {
            
            this.m_iNombreItemsGauche = 0;
            this.m_iNombreItemsDroite = 0;
            this.m_tItemsGauche = null;
            this.m_tItemsDroite = null;
            this.m_iOccurrences = 0;
            this.m_fSupport = 0.0f;
            this.m_fConfiance = 0.0f;
            this.m_iOccurrencesGauche = 0;
            this.m_iOccurrencesDroite = 0;
            this.m_iOccurrences_Gauche_NonDroite = 0;
            this.m_iOccurrences_NonGauche_Droite = 0;
            this.m_iOccurrences_NonGauche_NonDroite = 0; 
            this.m_iNombreDisjonctionsGauche = 0;
            this.m_iNombreDisjonctionsDroite = 0;    
            this.m_iNombreDisjonctionsGaucheValides = 0;
            this.m_iNombreDisjonctionsDroiteValides = 0;
        }
    }
 
    
    
    // Classe permettant la comparaison des r�gles selon la valeur de confiance :
    public static abstract class ComparateurRegles implements Comparator {
        
        boolean m_bTriDecroissant = false;
        
        
        public ComparateurRegles(boolean bTriDecroissant) {
            m_bTriDecroissant = bTriDecroissant;
        }
        
        
        public int compare(Object o1, Object o2) {
            int iResultatComparaison = 0;
            
            iResultatComparaison = CompareRegles((AssociationRule)o1, (AssociationRule)o2);
                        
            if (m_bTriDecroissant)
                iResultatComparaison = -iResultatComparaison;
            
            return iResultatComparaison;
        }        
        
        
        public abstract int CompareRegles(AssociationRule regle1, AssociationRule regle2);
        
        
        public boolean equals(Object obj) {
            return (compare(this, obj) == 0);
        }
    }  
    
    
    
    /**comparing RULES on the value of Confidence
     */
    public static class ComparateurConfiance extends ComparateurRegles {
        
        public ComparateurConfiance(boolean bTriDecroissant) {
            super(bTriDecroissant);
        }
        
        public int CompareRegles(AssociationRule regle1, AssociationRule regle2) {
            if (regle1.m_fConfiance > regle2.m_fConfiance)
                return 1;
            else if (regle1.m_fConfiance < regle2.m_fConfiance)
                return -1;
            else {
                if (regle1.m_iOccurrences > regle2.m_iOccurrences)
                    return 1;
                else if (regle1.m_iOccurrences < regle2.m_iOccurrences)
                    return -1;
                else
                    return 0;
            }
        }
    }    
    
    
    
    /**comparing RULES on the value of support
     */
    public static class ComparateurSupport extends ComparateurRegles {

        public ComparateurSupport(boolean bTriDecroissant) {
            super(bTriDecroissant);
        }
        
        public int CompareRegles(AssociationRule regle1, AssociationRule regle2) {
            if (regle1.m_iOccurrences > regle2.m_iOccurrences)
                return 1;
            else if (regle1.m_iOccurrences < regle2.m_iOccurrences)
                return -1;
            else {
                if (regle1.m_fConfiance > regle2.m_fConfiance)
                    return 1;
                else if (regle1.m_fConfiance < regle2.m_fConfiance)
                    return -1;                
                else
                    return 0;
            }
        }        
    }  
    
    
    
    /**Class for comparing RULES depending on the number of attributes it contains
     */
    public static class ComparateurNombreAttributs extends ComparateurRegles {

        public ComparateurNombreAttributs(boolean bTriDecroissant) {
            super(bTriDecroissant);
        }
        
        public int CompareRegles(AssociationRule regle1, AssociationRule regle2) {
            int iNombreItemsRegle1 = 0;
            int iNombreItemsRegle2 = 0;
            
            iNombreItemsRegle1 = regle1.m_iNombreItemsGauche + regle1.m_iNombreItemsDroite;
            iNombreItemsRegle2 = regle2.m_iNombreItemsGauche + regle2.m_iNombreItemsDroite;
            
            if (iNombreItemsRegle1 > iNombreItemsRegle2)
                return 1;
            else if (iNombreItemsRegle1 < iNombreItemsRegle2)
                return -1;
            else {
                if (regle1.m_iNombreItemsGauche > regle2.m_iNombreItemsGauche)
                    return 1;
                else if (regle1.m_iNombreItemsGauche < regle2.m_iNombreItemsGauche)
                    return -1;                
                else {
                    if (regle1.m_fConfiance > regle2.m_fConfiance)
                        return 1;
                    else if (regle1.m_fConfiance < regle2.m_fConfiance)
                        return -1;
                    else {
                        if (regle1.m_iOccurrences > regle2.m_iOccurrences)
                            return 1;
                        else if (regle1.m_iOccurrences < regle2.m_iOccurrences)
                            return -1;
                        else
                            return 0;
                    }
                }
            }
        }        
    }  
    
    
    
    // G�n�rateurs de comparateurs :
    public static Comparator ObtenirComparateurConfiance(boolean bTriDecroissant) { return new ComparateurConfiance(bTriDecroissant); }
    public static Comparator ObtenirComparateurSupport(boolean bTriDecroissant) { return new ComparateurSupport(bTriDecroissant); }
    public static Comparator ObtenirComparateurNombreAttributs(boolean bTriDecroissant) { return new ComparateurNombreAttributs(bTriDecroissant); }
        
    
    /**Assign Left Item 
     * @param item
     * @param iPosition
     */
    public void AssignerItemGauche(Item item, int iPosition) {
        if ( (iPosition < m_iNombreItemsGauche) && (item != null) )
            m_tItemsGauche[iPosition] = item;
    }
    
    
    /**
     * Assign Right Item
     * @param item
     * @param iPosition
     */
    public void AssignerItemDroite(Item item, int iPosition) {
        if ( (iPosition < m_iNombreItemsDroite) && (item != null) )
            m_tItemsDroite[iPosition] = item;
    }

    
    
    public void AssignerNombreOccurrences(int iOccurrences) {
        m_iOccurrences = iOccurrences;
    }

    
    
    public void AssignerSupport(float fSupport) {
        m_fSupport = fSupport;
    }
    
    
    
    public void AssignerConfiance(float fConfiance) {
        m_fConfiance = fConfiance;
    }
    
    
    /**
     * Get left item
     * @param iPosition
     * @return Item
     */
    public Item ObtenirItemGauche(int iPosition) {
        if (iPosition < m_iNombreItemsGauche)
            return m_tItemsGauche[iPosition];
        else
            return null;
    }
    
    
    /**
     * Get right item
     * @param iPosition
     * @return item
     */
    public Item ObtenirItemDroite(int iPosition) {
        if (iPosition < m_iNombreItemsDroite)
            return m_tItemsDroite[iPosition];
        else
            return null;
    }
 
    
    /**Compute the number of items of a specific type on the left
     * @param iTypeItem
     * @return int
     */
    public int CompterItemsGaucheSelonType(int iTypeItem) {
        int iPosition = 0;
        int iCompteur = 0;
        
        iCompteur = 0;
        for (iPosition = 0; iPosition < m_iNombreItemsGauche; iPosition++)
            if (m_tItemsGauche[iPosition].m_iTypeItem == iTypeItem)
                iCompteur++;
        
        return iCompteur;
    }
    
    
    /**Compute the number of items of a specific type on the right
     * 
     * @param iTypeItem
     * @return int
     */
    public int CompterItemsDroiteSelonType(int iTypeItem) {
        int iPosition = 0;
        int iCompteur = 0;
        
        iCompteur = 0;
        for (iPosition=0; iPosition<m_iNombreItemsDroite; iPosition++)
            if (m_tItemsDroite[iPosition].m_iTypeItem == iTypeItem)
                iCompteur++;
        
        return iCompteur;
    }
    
    
    
    public void EvaluerSiQualitative(ResolutionContext contexteResolution) {
        ItemQualitative [] itemsGauche = null;
        ItemQualitative [] itemsTotaux = null;
        int iIndiceItem = 0;
        Item item = null;
        int iIndiceAjoutItemGauche = 0;
        int iIndiceAjoutItemTotaux = 0;
        ItemSet itemSetGauche = null;
        ItemSet itemSetTotal = null;
        AprioriQuantitative apriori = null;
        
        if (contexteResolution == null)
            return;
        
        apriori = contexteResolution.m_aprioriCourant;
        if (apriori == null)
            return;
        
        itemsGauche = new ItemQualitative[ m_iNombreItemsGauche ];
        itemsTotaux = new ItemQualitative[ m_iNombreItemsGauche+m_iNombreItemsDroite ];
        iIndiceAjoutItemGauche = 0;
        iIndiceAjoutItemTotaux = 0;
        
        // On r�pertorie tous les items qualitatifs de gauche :
        for (iIndiceItem=0; iIndiceItem<m_iNombreItemsGauche; iIndiceItem++) {
            item = m_tItemsGauche[iIndiceItem];
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                itemsGauche[iIndiceAjoutItemGauche] = (ItemQualitative)item;
                itemsTotaux[iIndiceAjoutItemTotaux] = (ItemQualitative)item;
                iIndiceAjoutItemGauche++;
                iIndiceAjoutItemTotaux++;
            }
            else
                return;
        }
                
        // On r�pertorie tous les items qualitatifs de droite :
        for (iIndiceItem=0; iIndiceItem<m_iNombreItemsDroite; iIndiceItem++) {
            item = m_tItemsDroite[iIndiceItem];
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                itemsTotaux[iIndiceAjoutItemTotaux] = (ItemQualitative)item;
                iIndiceAjoutItemTotaux++;
            }
            else
                return;
        }        
        

        if (  (iIndiceAjoutItemGauche != m_iNombreItemsGauche)
            ||(iIndiceAjoutItemTotaux != (m_iNombreItemsGauche+m_iNombreItemsDroite))  )
            return;
        
        // Evaluation des valeurs statistiques de la r�gle :
        itemSetTotal = apriori.RechercherFrequent(itemsTotaux);
        if (itemSetTotal != null) {
            
            // Evaluation du support de la r�gle :
            this.m_iOccurrences = itemSetTotal.m_iSupport;
            this.m_fSupport = ((float)this.m_iOccurrences) / ((float)contexteResolution.m_gestionnaireBD.ObtenirNombreLignes());
    
            // Evaluation de la confiance de la r�gle :
            itemSetGauche = apriori.RechercherFrequent(itemsGauche);
            if ( (itemSetGauche != null) && (itemSetGauche.m_iSupport > 0) )
                this.m_fConfiance = this.m_iOccurrences / ((float)itemSetGauche.m_iSupport);
        }
    }
    
	public String leftToString() {
		 String sRegle = null;
		 int iIndiceItem = 0;
	     Item item = null;
	     boolean bItemsQualitatifsPresents = false;        
	     boolean bPremierItemInscrit = false;
	     int iIndiceDisjonction = 0;
	     int iNombreDisjonctions = 0;
	     int iNombreItemsQuantitatifs = 0;
	     int iNombreItems = 0;
	     Item tItemsRegle [] = null; 
	     sRegle = new String("");
		 
	     iNombreItems = m_iNombreItemsGauche;  //number of items on left
         tItemsRegle = m_tItemsGauche;         //the left items
         iNombreItemsQuantitatifs = CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF); 
         iNombreDisjonctions = m_iNombreDisjonctionsGaucheValides;
         
        //Firstly, write the qualitative items. if more than one exist, concatenate with AND
         bPremierItemInscrit = false;
         for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
             item = tItemsRegle[iIndiceItem];
             if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                 if (bPremierItemInscrit)
                     sRegle += "  AND  "; // "  ET  "
                 sRegle += ((ItemQualitative)item).toString();
                 bPremierItemInscrit = true;
             }
         }    
         bItemsQualitatifsPresents = bPremierItemInscrit;   //if has qualitative item
     
         //Next, display quantitative items:
         if (iNombreItemsQuantitatifs > 0) {
             if (bItemsQualitatifsPresents) {
                 sRegle += "  AND  ";
                 if (iNombreDisjonctions > 1)
                     sRegle += "( ";   
             }

             for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {

                 if (iIndiceDisjonction > 0)
                     sRegle += "  OR  "; //OU

                 if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                     sRegle += "( ";

                 bPremierItemInscrit = false;
                 for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                     item = tItemsRegle[iIndiceItem];
                     if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                         if (bPremierItemInscrit)
                             sRegle += "  AND  "; //ET
                         sRegle += ((ItemQuantitative)item).toString(iIndiceDisjonction);
                         bPremierItemInscrit = true;
                     }
                 }               

                 if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                     sRegle += " )";            
             }

             if ( (bItemsQualitatifsPresents) && (iNombreDisjonctions > 1) )
                     sRegle += " ) ";   
         }
         
         return sRegle;
	}
    
	public Vector<Qualitative> leftQualiToArray() {
		// TODO Auto-generated method stub
		 int iIndiceItem = 0;
	     Item item = null;
	     int iNombreItems = 0;                         //number of items on the left
	     Item tItemsRegle [] = null;                   //the left items
	     Vector<Qualitative> array = new Vector<Qualitative>();
		 
	    iNombreItems = m_iNombreItemsGauche;  //number of items on left
        tItemsRegle = m_tItemsGauche;         //the left items
        
       //Firstly, write the qualitative items. if more than one exist, concatenate with AND
        for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
            item = tItemsRegle[iIndiceItem];
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
            	array.add(((ItemQualitative)item).getAttributeNameValue());
            }
        }    
        return array;
	}
	
	public Vector<Vector<Quantitative>> leftQuantiToArray() {
		// TODO Auto-generated method stub
		 int iIndiceItem = 0;
	     Item item = null;
	     int iIndiceDisjonction = 0;                   //which or
	     int iNombreDisjonctions = 0;                  //number of ORs
	     int iNombreItemsQuantitatifs = 0;             //number of Quantitatives
	     int iNombreItems = 0;                         //number of items on the left
	     Item tItemsRegle [] = null;                   //the left items
		 
	    iNombreItems = m_iNombreItemsGauche;  //number of items on left
        tItemsRegle = m_tItemsGauche;         //the left items
        iNombreItemsQuantitatifs = CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF); 
        iNombreDisjonctions = m_iNombreDisjonctionsGaucheValides;

        if (iNombreItemsQuantitatifs == 0)
        	return null;
        	
        Vector<Vector<Quantitative>> array = new Vector<Vector<Quantitative>>();
        
        for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {
                
        	    Vector<Quantitative> temp = new Vector<Quantitative>();
                for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                    item = tItemsRegle[iIndiceItem];
                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                    	temp.add(((ItemQuantitative)item).getAttributeNameBoundary(iIndiceDisjonction));
                    }
                }    
                
                array.add(temp);

            }

        return array;
	}
	
	public String rightToString() {
		// TODO Auto-generated method stub
		 String sRegle = null;
		 int iIndiceItem = 0;
	     Item item = null;
	     boolean bItemsQualitatifsPresents = false;        
	     boolean bPremierItemInscrit = false;
	     int iIndiceDisjonction = 0;
	     int iNombreDisjonctions = 0;
	     int iNombreItemsQuantitatifs = 0;
	     int iNombreItems = 0;
	     Item tItemsRegle [] = null; 
	     sRegle = new String("");
		 
	     iNombreItems = m_iNombreItemsDroite;
         tItemsRegle = m_tItemsDroite;
         iNombreItemsQuantitatifs = CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
         iNombreDisjonctions = m_iNombreDisjonctionsDroiteValides;
        
       //Firstly, write the qualitative items. if more than one exist, concatenate with AND
        bPremierItemInscrit = false;
        for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
            item = tItemsRegle[iIndiceItem];
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                if (bPremierItemInscrit)
                    sRegle += "  AND  "; // "  ET  "
                sRegle += ((ItemQualitative)item).toString();
                bPremierItemInscrit = true;
            }
        }    
        bItemsQualitatifsPresents = bPremierItemInscrit;   //if has qualitative item
    
        //Next, display quantitative items:
        if (iNombreItemsQuantitatifs > 0) {
            if (bItemsQualitatifsPresents) {
                sRegle += "  AND  ";
                if (iNombreDisjonctions > 1)
                    sRegle += "( ";   
            }

            for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {

                if (iIndiceDisjonction > 0)
                    sRegle += "  OR  "; //OU

                if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                    sRegle += "( ";

                bPremierItemInscrit = false;
                for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                    item = tItemsRegle[iIndiceItem];
                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                        if (bPremierItemInscrit)
                            sRegle += "  AND  "; //ET
                        sRegle += ((ItemQuantitative)item).toString(iIndiceDisjonction);
                        bPremierItemInscrit = true;
                    }
                }               

                if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                    sRegle += " )";            
            }

            if ( (bItemsQualitatifsPresents) && (iNombreDisjonctions > 1) )
                    sRegle += " ) ";   
        }
        
        return sRegle;
	}

	
	public Vector<Qualitative> rightQualiToArray() {
		// TODO Auto-generated method stub
		 int iIndiceItem = 0;
	     Item item = null;
	     int iNombreItems = 0;                         //number of items on the left
	     Item tItemsRegle [] = null;                   //the left items
	     Vector<Qualitative> array = new Vector<Qualitative>();
		 
	     iNombreItems = m_iNombreItemsDroite;
	     tItemsRegle = m_tItemsDroite;
        
       //Firstly, write the qualitative items. if more than one exist, concatenate with AND
        for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
            item = tItemsRegle[iIndiceItem];
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
            	array.add(((ItemQualitative)item).getAttributeNameValue());
            }
        }    
        return array;
	}
	
	public Vector<Vector<Quantitative>> rightQuantiToArray() {
		// TODO Auto-generated method stub
		 int iIndiceItem = 0;
	     Item item = null;
	     int iIndiceDisjonction = 0;                   //which or
	     int iNombreDisjonctions = 0;                  //number of ORs
	     int iNombreItemsQuantitatifs = 0;             //number of Quantitatives
	     int iNombreItems = 0;                         //number of items on the left
	     Item tItemsRegle [] = null;                   //the left items
		 
	     iNombreItems = m_iNombreItemsDroite;
	     tItemsRegle = m_tItemsDroite;
	     iNombreItemsQuantitatifs = CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
         iNombreDisjonctions = m_iNombreDisjonctionsDroiteValides;

        if (iNombreItemsQuantitatifs == 0)
        	return null;
        	
        Vector<Vector<Quantitative>> array = new Vector<Vector<Quantitative>>();
        
        for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {
                
        	    Vector<Quantitative> temp = new Vector<Quantitative>();
                for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                    item = tItemsRegle[iIndiceItem];
                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                    	temp.add(((ItemQuantitative)item).getAttributeNameBoundary(iIndiceDisjonction));
                    }
                }    
                
                array.add(temp);

            }

        return array;
	}
    public String toString() {
        int iIndiceItem = 0;
        String sRegle = null;
        Item item = null;
        boolean bItemsQualitatifsPresents = false;        
        boolean bPremierItemInscrit = false;
        int iIndiceDisjonction = 0;
        int iNombreDisjonctions = 0;
        int iNombreItemsQuantitatifs = 0;
        int iIndiceCoteRegle = 0;
        int iNombreItems = 0;
        Item tItemsRegle [] = null; 
        
        sRegle = new String("");
        
        sRegle += "support = ";
        sRegle += String.valueOf(m_iOccurrences);
        sRegle += " (";
        sRegle += String.valueOf( (int)(100.0f*m_fSupport) );
        sRegle += "%) , confidence = ";
        sRegle += String.valueOf( (int)(100.0f*m_fConfiance) );
        sRegle += " %";
        
        sRegle += "  :  ";
            
        
        // left(qualitative, quantitative) --> right(qualitative, quantitative):
        for (iIndiceCoteRegle = 0; iIndiceCoteRegle < 2; iIndiceCoteRegle++) {
        
            if (iIndiceCoteRegle==0) {                //left side
                iNombreItems = m_iNombreItemsGauche;  //number of items on left
                tItemsRegle = m_tItemsGauche;         //the left items
                iNombreItemsQuantitatifs = CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF); 
                iNombreDisjonctions = m_iNombreDisjonctionsGaucheValides;
            }
            else {                                    //right side
                iNombreItems = m_iNombreItemsDroite;
                tItemsRegle = m_tItemsDroite;
                iNombreItemsQuantitatifs = CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
                iNombreDisjonctions = m_iNombreDisjonctionsDroiteValides;
                sRegle += "   -->   ";
            }                    
            
            
            //Firstly, write the qualitative items. if more than one exist, concatenate with AND
            bPremierItemInscrit = false;
            for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                item = tItemsRegle[iIndiceItem];
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                    if (bPremierItemInscrit)
                        sRegle += "  AND  "; // "  ET  "
                    sRegle += ((ItemQualitative)item).toString();
                    bPremierItemInscrit = true;
                }
            }    
            bItemsQualitatifsPresents = bPremierItemInscrit;   //if has qualitative item
        
            //Next, display quantitative items:
            if (iNombreItemsQuantitatifs > 0) {
                if (bItemsQualitatifsPresents) {
                    sRegle += "  AND  ";
                    if (iNombreDisjonctions > 1)
                        sRegle += "( ";   
                }

                for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {

                    if (iIndiceDisjonction > 0)
                        sRegle += "  OR  "; //OU

                    if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                        sRegle += "( ";

                    bPremierItemInscrit = false;
                    for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                        item = tItemsRegle[iIndiceItem];
                        if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                            if (bPremierItemInscrit)
                                sRegle += "  AND  "; //ET
                            sRegle += ((ItemQuantitative)item).toString(iIndiceDisjonction);
                            bPremierItemInscrit = true;
                        }
                    }               

                    if ( (iNombreItemsQuantitatifs > 1) && (iNombreDisjonctions > 1) )
                        sRegle += " )";            
                }

                if ( (bItemsQualitatifsPresents) && (iNombreDisjonctions > 1) )
                        sRegle += " ) ";   
            }
        }

        
        return sRegle;
    }
    
    
    
    
    public static void CalculerMesuresDiverses(AssociationRule [] tRegles, ResolutionContext contexte) {
        AssociationRule regle = null;
        int iNombreRegles = 0;
        int iIndiceRegle = 0;
        int iNombreLignes = 0;
        int iIndiceLigne = 0;
        int iIndiceItem = 0;
        int iIndiceDisjonction=0;
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        boolean bGaucheCouvert = true;
        boolean bDroiteCouvert = false;
        float fValeurReelle = 0.0f;
        
        if ( (tRegles==null) || (contexte==null) )
            return;
        
        iNombreRegles = tRegles.length;
        
        if (iNombreRegles == 0)
            return;
        
       
        // On r�initialise les diverses mesures avant de faire les comptes :
        for (iIndiceRegle=0;iIndiceRegle<iNombreRegles;iIndiceRegle++) {
            regle = tRegles[iIndiceRegle];
            regle.m_iOccurrencesGauche = 0;
            regle.m_iOccurrencesDroite = 0;
            regle.m_iOccurrences_Gauche_NonDroite = 0;
            regle.m_iOccurrences_NonGauche_Droite = 0;
            regle.m_iOccurrences_NonGauche_NonDroite = 0;

            
            regle.m_iOccurrences = 0;
        }
        
        
        iNombreLignes = contexte.m_gestionnaireBD.ObtenirNombreLignes();

        for (iIndiceLigne=0;iIndiceLigne<iNombreLignes;iIndiceLigne++) {

            for (iIndiceRegle=0;iIndiceRegle<iNombreRegles;iIndiceRegle++) {
            
                regle = tRegles[iIndiceRegle];

                // V�rification de la couverture de tous les items qualitatifs de gauche ou si la ligne contient une valeur manquante :
                bGaucheCouvert = true;
                iIndiceItem=0;
                while ( (bGaucheCouvert) && (iIndiceItem<regle.m_iNombreItemsGauche) ) {
                    item = regle.m_tItemsGauche[iIndiceItem];
                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                        itemQual = (ItemQualitative)item;
                        bGaucheCouvert = ( itemQual.m_iIndiceValeur == itemQual.m_attributQual.m_colonneDonnees.m_tIDQualitatif[iIndiceLigne] );
                    }
                    else {
                        itemQuant = (ItemQuantitative)item;
                        fValeurReelle = itemQuant.m_attributQuant.m_colonneDonnees.m_tValeurReelle[iIndiceLigne];
                        bGaucheCouvert = (fValeurReelle != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT);
                    }     
                    iIndiceItem++;
                }                
                
                
                // V�rification de la couverture de tous les items quantitatifs de gauche :
                if (bGaucheCouvert) {
                    bGaucheCouvert = false;
                    iIndiceDisjonction=0;                     
                    while ( (!bGaucheCouvert) && (iIndiceDisjonction<regle.m_iNombreDisjonctionsGaucheValides) ) {
                        bGaucheCouvert = true;
                        iIndiceItem=0;
                        while ( (bGaucheCouvert) && (iIndiceItem<regle.m_iNombreItemsGauche) ) {
                            item = regle.m_tItemsGauche[iIndiceItem];
                            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                                itemQuant = (ItemQuantitative)item;
                                fValeurReelle = itemQuant.m_attributQuant.m_colonneDonnees.m_tValeurReelle[iIndiceLigne];
                                bGaucheCouvert =    ( fValeurReelle >= itemQuant.m_tBornes[iIndiceDisjonction*2] )
                                                 && ( fValeurReelle <= itemQuant.m_tBornes[iIndiceDisjonction*2+1] );
                            }     
                            iIndiceItem++;
                        }
                        iIndiceDisjonction++;
                    }
                }

                
                // V�rification de la couverture de tous les items qualitatifs de droite ou si la ligne contient une valeur manquante :
                bDroiteCouvert = true;
                iIndiceItem=0;
                while ( (bDroiteCouvert) && (iIndiceItem<regle.m_iNombreItemsDroite) ) {
                    item = regle.m_tItemsDroite[iIndiceItem];
                    if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                        itemQual = (ItemQualitative)item;
                        bDroiteCouvert = ( itemQual.m_iIndiceValeur == itemQual.m_attributQual.m_colonneDonnees.m_tIDQualitatif[iIndiceLigne] );
                    }
                    else {
                        itemQuant = (ItemQuantitative)item;
                        fValeurReelle = itemQuant.m_attributQuant.m_colonneDonnees.m_tValeurReelle[iIndiceLigne];
                        bDroiteCouvert = (fValeurReelle != DatabaseAdmin.VALEUR_MANQUANTE_FLOAT );
                    }     
                    iIndiceItem++;
                }                
                
                
                // V�rification de la couverture de tous les items quantitatifs de droite :
                if (bDroiteCouvert) {
                    bDroiteCouvert = false;
                    iIndiceDisjonction=0;                     
                    while ( (!bDroiteCouvert) && (iIndiceDisjonction<regle.m_iNombreDisjonctionsDroiteValides) ) {
                        bDroiteCouvert = true;
                        iIndiceItem=0;
                        while ( (bDroiteCouvert) && (iIndiceItem<regle.m_iNombreItemsDroite) ) {
                            item = regle.m_tItemsDroite[iIndiceItem];
                            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                                itemQuant = (ItemQuantitative)item;
                                fValeurReelle = itemQuant.m_attributQuant.m_colonneDonnees.m_tValeurReelle[iIndiceLigne];
                                bDroiteCouvert =    ( fValeurReelle >= itemQuant.m_tBornes[iIndiceDisjonction*2] )
                                                 && ( fValeurReelle <= itemQuant.m_tBornes[iIndiceDisjonction*2+1] );
                             }     
                            iIndiceItem++;
                        }
                        iIndiceDisjonction++;
                    }
                }
                
                
                
                if ( (bGaucheCouvert) && (bDroiteCouvert) )
                    regle.m_iOccurrences++;
                
                if (bGaucheCouvert) 
                    regle.m_iOccurrencesGauche++;

                if (bDroiteCouvert) 
                    regle.m_iOccurrencesDroite++;
                
                if ( (bGaucheCouvert) && (!bDroiteCouvert) )
                    regle.m_iOccurrences_Gauche_NonDroite++;
                
                if ( (!bGaucheCouvert) && (bDroiteCouvert) )
                    regle.m_iOccurrences_NonGauche_Droite++;
                
                if ( (!bGaucheCouvert) && (!bDroiteCouvert) )
                    regle.m_iOccurrences_NonGauche_NonDroite++;
         
            }
        }
    }
    
}
