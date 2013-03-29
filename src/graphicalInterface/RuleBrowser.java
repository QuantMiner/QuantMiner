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

import javax.swing.*;

import src.apriori.*;
import src.solver.*;
import src.tools.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

public class RuleBrowser extends javax.swing.JPanel { //step 5 the third panel
    
  	private static final long serialVersionUID = 1L;
	private ResolutionContext m_contexteResolution = null;
    
	private Font m_fontEnTete = null;
    private Font m_fontItems = null;
    private Font m_fontDetails = null;
    
    private int m_iIndiceRegleAffichee = 0;
    private AssociationRule [] m_tReglesFiltrees = null;
    
    private Dimension m_dimensionConteneur = null;
    
    
    
    public RuleBrowser(ResolutionContext contexteResolution) {
        m_tReglesFiltrees = null;
        m_iIndiceRegleAffichee = 0;
        
        m_contexteResolution = contexteResolution;
        
        m_dimensionConteneur = getSize();
        
        //title font
        m_fontEnTete = UtilDraw.ChargerFonte("font_tahomabd.ttf");
        if (m_fontEnTete != null)
            m_fontEnTete = m_fontEnTete.deriveFont(14.0f);  
        else
            m_fontEnTete = new Font("Dialog", Font.BOLD|Font.ITALIC, 12);
    
        //item font
        m_fontItems = UtilDraw.ChargerFonte("font_timesbi.ttf");
        if (m_fontItems != null)
            m_fontItems = m_fontItems.deriveFont(11.0f);
        else
            m_fontItems = new Font("Dialog", Font.BOLD|Font.ITALIC, 10);
        
        //details font
        m_fontDetails = UtilDraw.ChargerFonte("arial.ttf");
        if (m_fontDetails != null)
            m_fontDetails = m_fontDetails.deriveFont(11.0f);
        else
            m_fontDetails = new Font("Dialog", Font.BOLD|Font.ITALIC, 10);
    }
    
    
    //define a list of rules
    public void DefinirListeRegles(AssociationRule [] tReglesFiltrees) {
        m_tReglesFiltrees = tReglesFiltrees;    
    }
    
    
    //define the index of a rule and repaint the rule panel
    public void DefinirIndiceRegleAffichee(int iIndiceRegleAffichee) {
        m_iIndiceRegleAffichee = iIndiceRegleAffichee;
        repaint();
    }
         
     
    
    public int ObtenirIndiceRegleAffichee() {
        return m_iIndiceRegleAffichee;
    }

    
    public void DefinirDimensionConteneur(int largeur, int hauteur) {
        m_dimensionConteneur = new Dimension(largeur, hauteur);
    }
    
    
    public float PeindreRegle(int iNumeroRegle, AssociationRule regle, int iY, int iLargeurZone, Graphics2D g2D) {
        FontRenderContext frc = null;
        TextLayout layout = null;
        LineMetrics mesuresFont = null;
        Item item = null;
        ItemQuantitative itemQuant = null;
        int iLargeurMaxi = 0;
        int iLargeurMaxiGauche = 0;
        int iLargeurMaxiDroite = 0;
        int iIndiceItem = 0;
        float fHauteurCumulee = 0.0f;
        float fHauteurCumuleeGauche = 0.0f;
        float fHauteurCumuleeDroite = 0.0f;
        float fHauteurCumuleeElement = 0.0f;
        float fHauteurHautAccolade = 0.0f;
        float fMilieuItems = 0.0f;
        String sTexte = null;
        float fMaxHauteur = 0.0f;
        Stroke ancienPinceau = null;
        int iNombreLignesBD = 0;
        int iPositionMilieuZone = 0;
        float fValeurConfiance = 0.0f;
        float fAmplitudeDomaine = 0.0f;
        float fBorneMin = 0.0f, fBorneMax = 0.0f;
        float fProportionMin = 0.0f, fProportionMax = 0.0f;        
        boolean bItemsQualitatifsPresents = false;        
        boolean bPremierItemInscrit = false;
        int iIndiceDisjonction = 0;
        int iNombreDisjonctions = 0;
        int iNombreItemsQuantitatifs = 0;
        int iIndiceCoteRegle = 0;
        int iPositionTexteX = 0;
        int iNombreItems = 0;
        Item tItemsRegle [] = null; 
        int iPositionItemsQuantX = 0;
        int iLargeurMaxItemsQuantX = 0;
        
        if (regle == null)
            return 0.0f;
        
        //set title font
        g2D.setFont(m_fontEnTete);
        g2D.setColor( new Color(255, 30, 50) );
        frc = g2D.getFontRenderContext();
        
        iPositionMilieuZone = iLargeurZone / 2;
        iLargeurMaxiGauche = iLargeurMaxiDroite = (iLargeurZone - 140) / 2;
        
        sTexte = String.valueOf(iNumeroRegle) + ". ";
        sTexte += "SUPPORT = ";
        sTexte += String.valueOf(regle.m_iOccurrences);
        sTexte += " (";
        sTexte += ResolutionContext.EcrirePourcentage(regle.m_fSupport, 2, true);
        sTexte += ") , CONFIDENCE = ";
        sTexte += ResolutionContext.EcrirePourcentage(regle.m_fConfiance, 2, true);
        sTexte += "  :  ";
        
        mesuresFont = m_fontEnTete.getLineMetrics(sTexte, frc);
        g2D.drawString(sTexte, 20, iY+mesuresFont.getAscent() );

        layout = new TextLayout(sTexte, m_fontEnTete, frc);
        fHauteurCumulee = (float) (layout.getBounds()).getHeight() + 20.0f;
         
        g2D.setFont(m_fontItems);
        frc = g2D.getFontRenderContext();
        
        // left side and right side:
        for (iIndiceCoteRegle = 0; iIndiceCoteRegle < 2; iIndiceCoteRegle++) {
            //left side
            if (iIndiceCoteRegle==0) {
                iNombreItems = regle.m_iNombreItemsGauche;
                tItemsRegle = regle.m_tItemsGauche;
                iNombreItemsQuantitatifs = regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF);
                iNombreDisjonctions = regle.m_iNombreDisjonctionsGaucheValides;
                iLargeurMaxi = iLargeurMaxiGauche;
                iPositionTexteX = 20;
            }
            else {
                iNombreItems = regle.m_iNombreItemsDroite;
                tItemsRegle = regle.m_tItemsDroite;
                iNombreItemsQuantitatifs = regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
                iNombreDisjonctions = regle.m_iNombreDisjonctionsDroiteValides;
                iLargeurMaxi = iLargeurMaxiDroite;
                iPositionTexteX = iLargeurMaxiGauche + 120;
            }      
          
            fHauteurCumuleeElement = fHauteurCumulee;
            
            
            g2D.setColor( new Color(85, 34, 0) );
           
            bPremierItemInscrit = false;
            for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                item = tItemsRegle[iIndiceItem];
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
                    if (bPremierItemInscrit)
                        fHauteurCumuleeElement += 15.0f;
                    fHauteurCumuleeElement += UtilDraw.PeindreTexteLimite(item.toString(), iLargeurMaxi, iPositionTexteX, iY+(int)fHauteurCumuleeElement, g2D);
                    bPremierItemInscrit = true;
                }
            }                
            bItemsQualitatifsPresents = bPremierItemInscrit;
            
        
            // Next display quantitative items:
            
            if (iNombreItemsQuantitatifs > 0) {
            
                g2D.setColor( new Color(19, 45, 91) ); 

                if (bItemsQualitatifsPresents)
                    fHauteurCumuleeElement += 15.0f;

                if (iNombreDisjonctions > 1) {
                    iPositionItemsQuantX = iPositionTexteX+20;
                    iLargeurMaxItemsQuantX = iLargeurMaxi-40;
                }
                else {
                    iPositionItemsQuantX = iPositionTexteX;
                    iLargeurMaxItemsQuantX = iLargeurMaxi;                    
                }
                
                for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {

                    if (iIndiceDisjonction>0)
                    	fHauteurCumuleeElement += 30.0f + UtilDraw.PeindreTexteCentreLimite("OR", iLargeurMaxi-40, iPositionTexteX+20, iY+(int)fHauteurCumuleeElement+15, g2D);

                    fHauteurHautAccolade = fHauteurCumuleeElement;
                
                    bPremierItemInscrit = false;
                    for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {
                        item = tItemsRegle[iIndiceItem];
                        if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                            if (bPremierItemInscrit)
                                fHauteurCumuleeElement += 15.0f;                            
                            fHauteurCumuleeElement += UtilDraw.PeindreTexteLimite(((ItemQuantitative)item).toString(iIndiceDisjonction), iLargeurMaxItemsQuantX, iPositionItemsQuantX, iY+(int)fHauteurCumuleeElement, g2D);
                            bPremierItemInscrit = true;
                        }
                    }               

                    // Display accolades about des items quantitatifs : 
                    if (iNombreDisjonctions > 1) {
                        ancienPinceau = g2D.getStroke();
                        g2D.setStroke( new BasicStroke(2.0f) );
                        fMilieuItems = fHauteurHautAccolade + (fHauteurCumuleeElement - fHauteurHautAccolade)/2;
                        UtilDraw.PeindreAccolade(iPositionTexteX+10, (float)iY + fMilieuItems, (fHauteurCumuleeElement-fHauteurHautAccolade)/2 + 2.0f, true, g2D);
                        UtilDraw.PeindreAccolade(iPositionTexteX+10+iLargeurMaxi-20, (float)iY + fMilieuItems, (fHauteurCumuleeElement-fHauteurHautAccolade)/2 + 2.0f, false, g2D);
                        g2D.setStroke(ancienPinceau);
                    }
                }
            }

            
             if (iIndiceCoteRegle == 0)
                 fHauteurCumuleeGauche = fHauteurCumuleeElement;
             else
                 fHauteurCumuleeDroite = fHauteurCumuleeElement;                 
        }

   
        
        fMaxHauteur = java.lang.Math.max(fHauteurCumuleeGauche, fHauteurCumuleeDroite) - fHauteurCumulee;
        fMilieuItems = fHauteurCumulee + fMaxHauteur/2;
       
        g2D.setColor( Color.BLACK );
        g2D.setPaint( Color.BLACK );

        // Display de la fl�che :
        UtilDraw.PeindreFleche(iLargeurMaxiGauche+35, iY + (int)fMilieuItems, g2D);
        
        // Display des accolades :
        ancienPinceau = g2D.getStroke();
        g2D.setStroke( new BasicStroke(2.0f) );

        UtilDraw.PeindreAccolade(10.0f, (float)iY + fMilieuItems, fMaxHauteur/2 + 2.0f, true, g2D);
        UtilDraw.PeindreAccolade(iLargeurMaxiGauche+30.0f, (float)iY + fMilieuItems, fMaxHauteur/2 + 2.0f, false, g2D);
        UtilDraw.PeindreAccolade(iLargeurMaxiGauche+110.0f, (float)iY + fMilieuItems, fMaxHauteur/2 + 2.0f, true, g2D);
        UtilDraw.PeindreAccolade(iLargeurMaxiGauche+iLargeurMaxiDroite+130.0f, (float)iY + fMilieuItems, fMaxHauteur/2 + 2.0f, false, g2D);

        g2D.setStroke(ancienPinceau);
        
        // Display the indicator of the 2 parties of the rule:
        g2D.setFont(m_fontEnTete);
        g2D.drawString("A", 3, iY+(int)fHauteurCumulee+2);
        g2D.drawString("B", iLargeurMaxiGauche+103, iY+(int)fHauteurCumulee+2);
        
        fHauteurCumulee += fMaxHauteur;
       
        //set detail font
        g2D.setFont(m_fontDetails);
        g2D.setColor( Color.BLACK );        
        
        fHauteurCumulee += 15.0f;
         
        //display the percentage bar
        //left and right side
        for (iIndiceCoteRegle = 0; iIndiceCoteRegle < 2; iIndiceCoteRegle++) {
            //left side
            if (iIndiceCoteRegle==0) {
                iNombreItems = regle.m_iNombreItemsGauche;
                tItemsRegle = regle.m_tItemsGauche;
                iNombreItemsQuantitatifs = regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF);
                iNombreDisjonctions = regle.m_iNombreDisjonctionsGaucheValides;
            }
            else {
                iNombreItems = regle.m_iNombreItemsDroite;
                tItemsRegle = regle.m_tItemsDroite;
                iNombreItemsQuantitatifs = regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
                iNombreDisjonctions = regle.m_iNombreDisjonctionsDroiteValides;
            }


            if (iNombreItemsQuantitatifs > 0) {
                fHauteurCumulee += 15.0f;
                if (iIndiceCoteRegle==0)
                    fHauteurCumulee += 15.0f; // + UtilDessin.PeindreTexteLimite("PROPORTIONS DU DOMAINE COUVERT PAR LES INTERVALLES DE LA PARTIE LEFT :", iLargeurZone-20, 10, iY+(int)fHauteurCumulee, g2D);
                else
                    fHauteurCumulee += 15.0f; // + UtilDessin.PeindreTexteLimite("PROPORTIONS DU DOMAINE COUVERT PAR LES INTERVALLES DE LA PARTIE RIGHT :", iLargeurZone-20, 10, iY+(int)fHauteurCumulee, g2D);
            }
            
           
            if (iNombreItemsQuantitatifs > 0)
                for (iIndiceDisjonction = 0; iIndiceDisjonction < iNombreDisjonctions; iIndiceDisjonction++) {
            
                    if (iIndiceDisjonction > 0)
                        fHauteurCumulee += 10.0f;
                
                    fHauteurHautAccolade = fHauteurCumulee;
                    for (iIndiceItem = 0; iIndiceItem < iNombreItems; iIndiceItem++) {

                        item = tItemsRegle[iIndiceItem];
                        if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                            itemQuant = (ItemQuantitative)item;
                            UtilDraw.PeindreTexteLimite(itemQuant.m_attributQuant.ObtenirNom()+" :", 90, 30, iY+(int)fHauteurCumulee, g2D);
                            g2D.drawRect(130, iY+(int)fHauteurCumulee, 300, 10);

                            fBorneMin = itemQuant.m_attributQuant.m_colonneDonnees.ObtenirBorneMin();
                            fBorneMax = itemQuant.m_attributQuant.m_colonneDonnees.ObtenirBorneMax();
                            fAmplitudeDomaine = fBorneMax - fBorneMin;

                            if (fAmplitudeDomaine != 0.0f) {
                                fProportionMin = (itemQuant.ObtenirBorneMinIntervalle(iIndiceDisjonction) - fBorneMin) / fAmplitudeDomaine;
                                fProportionMax = (itemQuant.ObtenirBorneMaxIntervalle(iIndiceDisjonction) - fBorneMin) / fAmplitudeDomaine;
                            }
                            else
                                fProportionMin = fProportionMax = 0.0f;

                            int iTailleProportionAffichee = 0;

                            iTailleProportionAffichee = (int)((fProportionMax-fProportionMin)*300.0f);
                            if (iTailleProportionAffichee < 1)
                                iTailleProportionAffichee = 1;

                            g2D.fillRect(130+(int)(fProportionMin*300.0f), iY+(int)fHauteurCumulee, iTailleProportionAffichee, 10);                    

                            sTexte = ResolutionContext.EcrirePourcentage((fProportionMax-fProportionMin), 2, true) + " of [";
                            sTexte += String.valueOf(fBorneMin) + ", " + String.valueOf(fBorneMax) + "]";
                            
                            UtilDraw.PeindreTexteLimite(sTexte, iLargeurZone-460, 450, iY+(int)fHauteurCumulee, g2D);

                            fHauteurCumulee += 20.0f;
                        }
                    }
                    
                    ancienPinceau = g2D.getStroke();
                    g2D.setStroke( new BasicStroke(2.0f) );
                    UtilDraw.PeindreAccolade(20, (float)iY+fHauteurHautAccolade+(fHauteurCumulee-10.0f-fHauteurHautAccolade)/2, (fHauteurCumulee-10.0f-fHauteurHautAccolade)/2 + 2.0f, true, g2D);
                    g2D.setStroke(ancienPinceau);
                }
        }        
        
        
        // Display the mesures suppl�mentaires :
        iNombreLignesBD = m_contexteResolution.m_gestionnaireBD.ObtenirNombreLignes();

        fHauteurCumulee += 20.0f;
        fHauteurCumuleeGauche = fHauteurCumulee;
        fHauteurCumuleeDroite = fHauteurCumulee;
        
        g2D.setFont(m_fontDetails);

        //Display the supports :
        g2D.setColor( Color.BLUE );
        UtilDraw.PeindreTexteLimite("SUPPORTS :", 100, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 20.0f;        
        
        UtilDraw.PeindreTexteLimite("A and B", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrences) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrences), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f; 
        
        UtilDraw.PeindreTexteLimite("A", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrencesGauche) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrencesGauche), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f;        

        UtilDraw.PeindreTexteLimite("B", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrencesDroite) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrencesDroite), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f;        
        
        UtilDraw.PeindreTexteLimite("A and (~B)", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrences_Gauche_NonDroite) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrences_Gauche_NonDroite), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f; 
        
        UtilDraw.PeindreTexteLimite("(~A) and B", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrences_NonGauche_Droite) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrences_NonGauche_Droite), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f; 
        
        UtilDraw.PeindreTexteLimite("(~A) and (~B)", 80, 10, iY+(int)fHauteurCumuleeGauche, g2D);
        g2D.fill3DRect(100, iY+(int)fHauteurCumuleeGauche, (120*regle.m_iOccurrences_NonGauche_NonDroite) / iNombreLignesBD, 10, true);
        UtilDraw.PeindreTexteLimite(m_contexteResolution.EcrireSupport(regle.m_iOccurrences_NonGauche_NonDroite), 90, 230, iY+(int)fHauteurCumuleeGauche, g2D);
        fHauteurCumuleeGauche += 15.0f; 
        
        
        // Display the confidences :
        g2D.setColor( Color.RED );
        UtilDraw.PeindreTexteLimite("CONFIDENCES:", 100, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        fHauteurCumuleeDroite += 20.0f;   
        
        fValeurConfiance = ((float)regle.m_iOccurrences) / ((float)regle.m_iOccurrencesGauche);
        UtilDraw.PeindreTexteLimite("A -> B", 80, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        g2D.fill3DRect(iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, (int)(120.0f*fValeurConfiance), 10, true);
        UtilDraw.PeindreTexteLimite(ResolutionContext.EcrirePourcentage(fValeurConfiance, 2, true), 90, iPositionMilieuZone+230, iY+(int)fHauteurCumuleeDroite, g2D);
        fHauteurCumuleeDroite += 15.0f;         
        
        UtilDraw.PeindreTexteLimite("(~A) -> B", 80, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        if ( (iNombreLignesBD - regle.m_iOccurrencesGauche) == 0)
            UtilDraw.PeindreTexteLimite("ind�termin� car 'non A' n'apparait jamais dans la BD.", 220, iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, g2D);
        else {
            fValeurConfiance = ((float)regle.m_iOccurrences_NonGauche_Droite) / ((float)(iNombreLignesBD - regle.m_iOccurrencesGauche));
            g2D.fill3DRect(iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, (int)(120.0f*fValeurConfiance), 10, true);
            UtilDraw.PeindreTexteLimite(ResolutionContext.EcrirePourcentage(fValeurConfiance, 2, true), 90, iPositionMilieuZone+230, iY+(int)fHauteurCumuleeDroite, g2D);
        }
        fHauteurCumuleeDroite += 15.0f;  
        
        fValeurConfiance = ((float)regle.m_iOccurrences) / ((float)regle.m_iOccurrencesDroite);
        UtilDraw.PeindreTexteLimite("B -> A", 80, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        g2D.fill3DRect(iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, (int)(120.0f*fValeurConfiance), 10, true);
        UtilDraw.PeindreTexteLimite(ResolutionContext.EcrirePourcentage(fValeurConfiance, 2, true), 90, iPositionMilieuZone+230, iY+(int)fHauteurCumuleeDroite, g2D);
        fHauteurCumuleeDroite += 15.0f; 
        
        UtilDraw.PeindreTexteLimite("(~B) -> A", 80, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        if ( (iNombreLignesBD - regle.m_iOccurrencesDroite) == 0)
            UtilDraw.PeindreTexteLimite("ind�termin� car 'non B' n'apparait jamais dans la BD.", 220, iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, g2D);
        else {
            fValeurConfiance = ((float)regle.m_iOccurrences_Gauche_NonDroite) / ((float)(iNombreLignesBD - regle.m_iOccurrencesDroite));
            g2D.fill3DRect(iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, (int)(120.0f*fValeurConfiance), 10, true);
            UtilDraw.PeindreTexteLimite(ResolutionContext.EcrirePourcentage(fValeurConfiance, 2, true), 90, iPositionMilieuZone+230, iY+(int)fHauteurCumuleeDroite, g2D);
        }
        fHauteurCumuleeDroite += 30.0f;
        
        fValeurConfiance = ((float)(regle.m_iOccurrences + regle.m_iOccurrences_NonGauche_NonDroite)) / ((float)iNombreLignesBD);
        UtilDraw.PeindreTexteLimite("A <-> B", 80, iPositionMilieuZone+10, iY+(int)fHauteurCumuleeDroite, g2D);
        g2D.fill3DRect(iPositionMilieuZone+100, iY+(int)fHauteurCumuleeDroite, (int)(120.0f*fValeurConfiance), 10, true);
        UtilDraw.PeindreTexteLimite(ResolutionContext.EcrirePourcentage(fValeurConfiance, 2, true), 90, iPositionMilieuZone+230, iY+(int)fHauteurCumuleeDroite, g2D);
        fHauteurCumuleeDroite += 15.0f; 
        
        // Trac� du s�parateur entre infos de support et infos de confiance :
        fMaxHauteur = java.lang.Math.max(fHauteurCumuleeGauche, fHauteurCumuleeDroite);
            
        g2D.setColor( Color.BLACK );
        g2D.drawLine(iPositionMilieuZone, iY+((int)fHauteurCumulee)+5, iPositionMilieuZone, iY+((int)fMaxHauteur)-10);
            
        fHauteurCumulee = fMaxHauteur;
        
        return fHauteurCumulee;
    }

    
    
	public static Graphics2D antialias(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		// Enable antialiasing for shapes
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);
    		return g2;
	}
	
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = null;
        AssociationRule regle = null;
        float fHauteurCumulee = 0.0f;
        
        g2D = antialias(g);

        
        super.paintComponent(g); 
        
        if ( (m_tReglesFiltrees == null) || (m_iIndiceRegleAffichee<0) ) { 
            fHauteurCumulee = (float)(m_dimensionConteneur.height/2-12);
            g2D.setFont(m_fontEnTete);
            g2D.setColor( new Color(255, 30, 50) );
            fHauteurCumulee += UtilDraw.PeindreTexteCentreLimite("No rule returned for the selected critera.", getWidth(), 0, (int)fHauteurCumulee, g2D);
            setPreferredSize( new Dimension(getWidth(), (int)fHauteurCumulee) );
            revalidate();
            return;
        }        

        
        try {
            fHauteurCumulee = 10.0f;
            regle  = m_tReglesFiltrees[m_iIndiceRegleAffichee];
            fHauteurCumulee += 10.0f + PeindreRegle(1+m_iIndiceRegleAffichee, regle, (int)fHauteurCumulee, getWidth(), g2D);
            setPreferredSize( new Dimension(getWidth(), (int)fHauteurCumulee) );
            revalidate();
        }
        catch (IndexOutOfBoundsException e) {}
  
    }
    
    
    
    // Enregistre dans un fichier JPEG la repr�sentation graphique de la r�gle :
    public void EnregistrerImageRegle(AssociationRule regle, int iIndiceRegle, String sCheminFichier) {
        BufferedImage imageRegle = null;
        Graphics2D contexteImage = null;
        File fichierImage = null;
        FileOutputStream fluxSortieImage = null;
        JPEGImageEncoder encoderJPEG = null;
        boolean bProcessusInterrompu = false;
        int iLargeur = 0, iHauteur = 0;
        
        if ( (sCheminFichier == null) || (regle == null) )
            return;
        
        iLargeur = 950;
        iHauteur = 1;
        imageRegle = new BufferedImage(iLargeur, iHauteur, BufferedImage.TYPE_INT_RGB);
        
        contexteImage = antialias(imageRegle.createGraphics());
        
        if (contexteImage != null) {

            if (m_tReglesFiltrees != null) { 
                
                bProcessusInterrompu = false;
                
                // On peint l'image une premi�re fois pour calculer sa hauteur :
                contexteImage.setColor(Color.WHITE);
                contexteImage.fillRect(0, 0, iLargeur, iHauteur);
                iHauteur = 10 + (int)PeindreRegle(1+iIndiceRegle, regle, 10, iLargeur, contexteImage);
                contexteImage = null;
                imageRegle = null;
                
                // Puis on la peint une seconde fois dans une image � la bonne dimension :
                imageRegle = new BufferedImage(iLargeur, iHauteur, BufferedImage.TYPE_INT_RGB);
                contexteImage = imageRegle.createGraphics();
                if (contexteImage != null) {
        
                    contexteImage.setColor(Color.WHITE);
                    contexteImage.fillRect(0, 0, iLargeur, iHauteur);
                    PeindreRegle(1+iIndiceRegle, regle, 10, iLargeur, contexteImage);
                
                    try {
                        fichierImage = new File(sCheminFichier);
                        fluxSortieImage = new FileOutputStream(fichierImage);
                    }
                    catch (FileNotFoundException e1) { bProcessusInterrompu = true; }
                    catch (SecurityException e2) { bProcessusInterrompu = true; }

                    try {
                        encoderJPEG = JPEGCodec.createJPEGEncoder(fluxSortieImage);
                        encoderJPEG.encode(imageRegle);
                    }
                    catch (IOException e1) {}
                    catch (ImageFormatException e2) {}
                    
                    try {
                        fluxSortieImage.close();
                    }
                    catch (IOException e) {}
                }
                
            }
        }
    }
    
}
