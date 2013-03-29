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
import javax.swing.border.*;

import src.tools.*;

import java.awt.*;
import java.io.*;


public class JIndicateSteps extends JPanel { //indicator from step 1 to step 5 the rectangular on top of each step

    JLabel m_labelEtape = null;
    JLabel m_labelEtapeTotal = null;
    JLabel m_labelIntitule = null;
    JButton m_boutonAide = null;

    String m_sFichierAide = null;
    
    
        
    public JIndicateSteps(int iNumero, String sIntitule) {
        ImageIcon iconeEtape = null;
        ImageIcon iconeEtapeTotal = null;
        String sNomIconeEtape = null;
        Rectangle interieurPanneau = null;
        
        if ( (iNumero<1) && (iNumero>5) )
            return;
        
        m_sFichierAide = null;
        
        setLayout(null);
        setBackground( new Color(160,160,192) );
        setBorder( javax.swing.BorderFactory.createBevelBorder(BevelBorder.RAISED) );

        interieurPanneau = calculerZoneInterieurPanneau();
        
        switch (iNumero) { //indicator, from step 1 to step 5
            case 1 : sNomIconeEtape = "etape_1.jpg"; break;
            case 2 : sNomIconeEtape = "etape_2.jpg"; break;
            case 3 : sNomIconeEtape = "etape_3.jpg"; break;
            case 4 : sNomIconeEtape = "etape_4.jpg"; break;
            case 5 : sNomIconeEtape = "etape_5.jpg"; break;
        }
               
        iconeEtape = new ImageIcon( ENV.REPERTOIRE_RESSOURCES + sNomIconeEtape );
        iconeEtapeTotal = new ImageIcon( ENV.REPERTOIRE_RESSOURCES + "drapeau_5.jpg" );
        
        m_labelEtape = new JLabel(iconeEtape);
        m_labelEtape.setSize(iconeEtape.getIconWidth(), iconeEtape.getIconHeight());
        
        m_labelEtapeTotal = new JLabel(iconeEtapeTotal);
        m_labelEtapeTotal.setSize(iconeEtapeTotal.getIconWidth(), iconeEtapeTotal.getIconHeight());
       
        m_labelIntitule = new JLabel(sIntitule);
        
        m_boutonAide = new javax.swing.JButton();
        m_boutonAide.setText("?");
        
        try {
            InputStream fontStream = new FileInputStream(ENV.REPERTOIRE_RESSOURCES + "font_comic.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            font = font.deriveFont(Font.BOLD|Font.ITALIC, 18.0f);
            fontStream.close();
            m_labelIntitule.setFont( font );
            m_boutonAide.setFont( font );
        }
        catch (IOException e) {}
        catch (FontFormatException e) {}
        
        add(m_labelEtape);
        add(m_labelEtapeTotal);
        add(m_labelIntitule);
        add(m_boutonAide);
        
        validate();

        RedimensionnerSelonLargeur( m_labelEtape.getWidth() + m_labelEtapeTotal.getWidth() + 100 );

        m_boutonAide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAideActionPerformed(evt);
            }
        });

    }
    

        
    public void SpecifierFichierAide(String sFichierAide) {
        m_sFichierAide = sFichierAide;
    }



    private void jButtonAideActionPerformed(java.awt.event.ActionEvent evt) {
        DialogHelp dialogAide = null;
        
        if (m_sFichierAide != null) {
            dialogAide = new DialogHelp(m_sFichierAide, null, true);
            dialogAide.show();
        }
    }

    
    
    private Rectangle calculerZoneInterieurPanneau() {
        Border bordure = null;
        Insets insets = null;
        Rectangle rectInterieur = null;
        
        bordure = getBorder();
        
        if (bordure==null)
            return new Rectangle(0, 0, getWidth(), getHeight());
        
        rectInterieur = getBounds();
        insets = bordure.getBorderInsets(this);
        rectInterieur.x = insets.left;
        rectInterieur.y = insets.top;
        rectInterieur.width -= (insets.left + insets.right);
        rectInterieur.height -= (insets.bottom + insets.top);
        
        return rectInterieur;
    }   
    
    
    
    // Calcule la hauteur totale prise par les bordures en haut et en bas du panneau :
    int CalculerCumulHauteurBordures() {
        Border bordure = null;
        Insets insets = null;
        
        bordure = getBorder();
        
        if (bordure==null)
            return 0;
        
        insets = bordure.getBorderInsets(this);
        
        return insets.top + insets.bottom;        
    }
    
    
    
    void RedimensionnerSelonLargeur(int largeur) {
        int iPositionXIntitule = 0;
        Point positionElement = null;
        Rectangle interieurPanneau = null;
        int iMaxIconHeight = 0;
        int iTempHeight = 0;
        int iCumulHauteurBordures = 0;
        
        iMaxIconHeight = m_labelEtape.getHeight();
        iTempHeight = m_labelEtapeTotal.getHeight();
        if (iTempHeight>iMaxIconHeight)
            iMaxIconHeight = iTempHeight;
        
        iCumulHauteurBordures = CalculerCumulHauteurBordures();
        
        setPreferredSize(new java.awt.Dimension(largeur-20, iMaxIconHeight+iCumulHauteurBordures));
        reshape(10, 10, largeur-20, iMaxIconHeight+iCumulHauteurBordures);
        
        interieurPanneau = calculerZoneInterieurPanneau();

        m_labelEtape.setLocation(interieurPanneau.x, interieurPanneau.y);
        m_labelEtapeTotal.setLocation(interieurPanneau.x+m_labelEtape.getWidth(), interieurPanneau.y);

        m_boutonAide.setBounds(interieurPanneau.width+interieurPanneau.x-50, interieurPanneau.y, 50, iMaxIconHeight);

        positionElement = m_labelEtapeTotal.getLocation();
        iPositionXIntitule = positionElement.x + m_labelEtapeTotal.getWidth() + 10;
        m_labelIntitule.setBounds(iPositionXIntitule, interieurPanneau.y, interieurPanneau.width+interieurPanneau.x-iPositionXIntitule-10-m_boutonAide.getWidth()-10, iMaxIconHeight);
    }
    
}
