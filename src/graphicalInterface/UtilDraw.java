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

import src.tools.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.io.*;


public class UtilDraw {
    
    
    public static Font ChargerFonte(String sNomFichier) {
        FileInputStream fontStream = null;
        Font font = null;
                
        try {
            fontStream = new FileInputStream(ENV.REPERTOIRE_RESSOURCES + sNomFichier);
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            fontStream.close();
        }
        catch (IOException e) { font = null; }
        catch (FontFormatException e) {
        	System.out.println(e);
        	 font = null; }
    
        return font;
    }
    
    
    
    public static float PeindreTexteCentreLimite(String sTexte, int iLargeurMax, int iX, int iY, Graphics2D g2D) {
        int iLargeurTexte = 0;
        Font fontCourante = null;
        FontRenderContext frc = null;
        TextLayout layout = null;
        Rectangle2D contourTexte = null;
        
        if (g2D == null)
            return 0.0f;
        
        fontCourante = g2D.getFont();
        if (fontCourante == null)
            return 0.0f;
        
        frc = g2D.getFontRenderContext();
        
        layout = new TextLayout(sTexte, fontCourante, frc);
        contourTexte = layout.getBounds();        
        
        iLargeurTexte = (int)contourTexte.getWidth();
        if (iLargeurTexte > iLargeurMax)
            iLargeurTexte = iLargeurMax;
        
        return PeindreTexteLimite(sTexte, iLargeurMax-(iLargeurMax-iLargeurTexte)/2, iX+(iLargeurMax-iLargeurTexte)/2, iY, g2D);  
    }  
        
        
    //draw text boundary 
    public static float PeindreTexteLimite(String sTexte, int iLargeurMax, int iX, int iY, Graphics2D g2D) {
        Font fontCourante = null;
        FontRenderContext frc = null;
        TextLayout layout = null;
        TextLayout layout3Points = null;
        TextHitInfo hitInfo = null;
        LineMetrics mesuresFont = null;
        Rectangle2D contourTexte = null;
        String sTextePeint = null;
        float fLargeur3Points = 0.0f; // Largeur de la cha�ne "..."
        float fHauteurTexte = 0.0f;
        
        if (g2D == null)
            return 0.0f;
        
        fontCourante = g2D.getFont();
        if (fontCourante == null)
            return 0.0f;
        
        frc = g2D.getFontRenderContext();
        
        layout = new TextLayout(sTexte, fontCourante, frc);
        contourTexte = layout.getBounds();
        fHauteurTexte = (float)contourTexte.getHeight();
        
        if ( contourTexte.getWidth() > (double)iLargeurMax) {
            
            // Calcul de la distance n�cessaire au trac� de la cha�ne "..." indiquant qu'on a tronqu� le texte :
            layout3Points = new TextLayout("...", fontCourante, frc);
            fLargeur3Points = (float) ((layout3Points.getBounds()).getWidth());
 
            // Construction de la nouvelle cha�ne, tronqu�e :
            if (fLargeur3Points >= (float)iLargeurMax)
                sTexte = "...";
            else {
                hitInfo = layout.hitTestChar(((float)iLargeurMax) - fLargeur3Points, 0.0f);
                sTexte = sTexte.substring(0, hitInfo.getCharIndex()) + "...";
            }
        }
        
        mesuresFont = fontCourante.getLineMetrics(sTexte, frc);
        g2D.drawString(sTexte, iX, iY+mesuresFont.getAscent() );
        
        return fHauteurTexte;
    }
    
    
    
    public static void PeindreFleche(int iPosFlecheX, int iPosFlecheY, Graphics2D g2D) {
        Polygon elementFleche = null;
       
        elementFleche = new Polygon();
        elementFleche.addPoint(iPosFlecheX, iPosFlecheY-5);
        elementFleche.addPoint(iPosFlecheX+10, iPosFlecheY);
        elementFleche.addPoint(iPosFlecheX, iPosFlecheY+5);
        elementFleche.addPoint(iPosFlecheX+45, iPosFlecheY+2);
        elementFleche.addPoint(iPosFlecheX+40, iPosFlecheY+10);
        elementFleche.addPoint(iPosFlecheX+70, iPosFlecheY);
        elementFleche.addPoint(iPosFlecheX+40, iPosFlecheY-10);
        elementFleche.addPoint(iPosFlecheX+45, iPosFlecheY-2);
        
        g2D.fill(elementFleche);
    }
        
    
    
    public static void PeindreAccolade(float fPosX, float fPosMilieuY, float fTaille, boolean bOuvrante, Graphics2D g2D) {
        float fCoeffSens = 0.0f;
        
        if (bOuvrante)
            fCoeffSens = 1.0f;
        else
            fCoeffSens = -1.0f;
        
        Point2D.Float [] pointAccolade = null;
       
        pointAccolade = new Point2D.Float [7];
       
        pointAccolade[0] = new Point2D.Float(fPosX + 10.0f*fCoeffSens, fPosMilieuY - fTaille);
        pointAccolade[1] = new Point2D.Float(fPosX + 5.0f*fCoeffSens, fPosMilieuY - fTaille + 4.0f);
        pointAccolade[2] = new Point2D.Float(fPosX + 5.0f*fCoeffSens, fPosMilieuY - 4.0f);
        pointAccolade[3] = new Point2D.Float(fPosX, fPosMilieuY);
        pointAccolade[4] = new Point2D.Float(pointAccolade[2].x, fPosMilieuY + 5.0f);
        pointAccolade[5] = new Point2D.Float(pointAccolade[1].x, fPosMilieuY + fTaille - 5.0f);
        pointAccolade[6] = new Point2D.Float(pointAccolade[0].x, fPosMilieuY + fTaille);

        GeneralPath accolade = new GeneralPath();
        accolade.append( new Line2D.Float(pointAccolade[0], pointAccolade[1]), false );
        accolade.append( new Line2D.Float(pointAccolade[1], pointAccolade[2]), false );
        accolade.append( new Line2D.Float(pointAccolade[2], pointAccolade[3]), false );
        accolade.append( new Line2D.Float(pointAccolade[3], pointAccolade[4]), false );
        accolade.append( new Line2D.Float(pointAccolade[4], pointAccolade[5]), false );
        accolade.append( new Line2D.Float(pointAccolade[5], pointAccolade[6]), false );

        g2D.draw(accolade);
    }
 
    
}
