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

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;



public class PanelRuleGraphs extends JPanel {
    
    private ResolutionContext m_contexteResolution = null;
    private BufferedImage [] m_graphes = null;
    private AssociationRule m_regle = null;
    private int m_iLargeurGraphe = 0;
    private int m_iHauteurGraphe = 0;
    private Font m_fontItem = null;
    
    

    public PanelRuleGraphs(ResolutionContext contexteResolution, int iIndiceRegle) {
        int iNombreTotalItems = 0;
        int iIndiceItem = 0;
        
        m_contexteResolution = contexteResolution;
        m_regle = null;
        
        if (m_contexteResolution.m_listeRegles == null)
            return;
        
        try {
            m_regle = (AssociationRule)m_contexteResolution.m_listeRegles.get(iIndiceRegle);
        }
        catch (IndexOutOfBoundsException e) {
            return;
        }
        
        if (m_regle == null)
            return;
        
        
        // Chargement des ressources utilis�es pour l'affichage des r�gles :
        m_fontItem = UtilDraw.ChargerFonte("font_tahomabd.ttf");
        if (m_fontItem != null)
            m_fontItem = m_fontItem.deriveFont(12.0f);
        else
            m_fontItem = new Font("Dialog", Font.BOLD|Font.ITALIC, 12);
                
        
        iNombreTotalItems = m_regle.m_iNombreItemsGauche + m_regle.m_iNombreItemsDroite;
        m_iLargeurGraphe = 256;
        m_iHauteurGraphe = 300;
        
        m_graphes = new BufferedImage[iNombreTotalItems];
        for (iIndiceItem=0; iIndiceItem<iNombreTotalItems; iIndiceItem++) {
            m_graphes[iIndiceItem] = new BufferedImage(m_iLargeurGraphe, m_iHauteurGraphe, BufferedImage.TYPE_INT_RGB);
            DessinerGraphe(iIndiceItem);
        }
    }
    
    
    
    public void DessinerGraphe(int iIndiceItem) {
        BufferedImage graphe = null;
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        Graphics2D gc = null;
        String sNomItem = null;
        
        if (m_regle == null)
            return;
        
        graphe = m_graphes[iIndiceItem];
        
        if (iIndiceItem < m_regle.m_iNombreItemsGauche)
            item = m_regle.ObtenirItemGauche(iIndiceItem);
        else
            item = m_regle.ObtenirItemDroite(iIndiceItem - m_regle.m_iNombreItemsGauche);
        
        if (item == null)
            return;
        
        if (item.m_iTypeItem == Item.ITEM_TYPE_QUALITATIF) {
            itemQual = (ItemQualitative)item;
        }
        else if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
            itemQuant = (ItemQuantitative)item;
        }
        else
            return;

        
        // Trac� des composantes communes du dessin :
        gc = graphe.createGraphics();
        
        gc.setStroke( new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER) );
        
        // Effacement du fond de la fen�tre :
        gc.setColor(Color.WHITE);
        gc.fillRect(0, 0, m_iLargeurGraphe, m_iHauteurGraphe);
        
        // Trac� des axes :
        gc.setColor(Color.BLACK);
        
        gc.drawLine(20, m_iLargeurGraphe-20, 20, 20);
        gc.drawLine(20, 20, 20-(10*m_iLargeurGraphe)/256, 20+(10*m_iHauteurGraphe)/300);
        gc.drawLine(20, 20, 20+(10*m_iLargeurGraphe)/256, 20+(10*m_iHauteurGraphe)/300);
        
        gc.drawLine(20, m_iLargeurGraphe-20, m_iHauteurGraphe-20, m_iHauteurGraphe-20);
        gc.drawLine(m_iLargeurGraphe-20, m_iLargeurGraphe-20, m_iHauteurGraphe-20-(10*m_iHauteurGraphe)/300, m_iHauteurGraphe-20-(10*m_iHauteurGraphe)/300);
        gc.drawLine(m_iLargeurGraphe-20, m_iLargeurGraphe-20, m_iHauteurGraphe-20-(10*m_iHauteurGraphe)/300, m_iHauteurGraphe-20+(10*m_iHauteurGraphe)/300);
        
        gc.setFont(m_fontItem);
        
        // Trac� des composantes du dessin relatives � un item qualitatif :
        // Trac� des composantes du dessin relatives � un item quantitatif :
    }
    
    
    
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = null;
        int iNombreTotalItems = 0;
        int iIndiceItem = 0;
        
        super.paintComponent(g); 
        
        if (m_regle == null)
            return;
        
        iNombreTotalItems = m_regle.m_iNombreItemsGauche + m_regle.m_iNombreItemsDroite;

        g2D = (Graphics2D)g;
        
        for (iIndiceItem=0; iIndiceItem<iNombreTotalItems; iIndiceItem++) {
            g2D.drawImage(m_graphes[iIndiceItem], new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR), iIndiceItem * (20+m_iLargeurGraphe), 0);
        }
    }
    
    
}
