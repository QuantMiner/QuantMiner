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

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

import src.apriori.*;
import src.solver.*;

public class DialogGraphQuality extends javax.swing.JDialog {
    

    public class PanneauGrapheQualite extends JPanel {

        private BufferedImage m_graphe = null;
        private int m_iLargeurGraphe = 0;
        private int m_iHauteurGraphe = 0;
        public int m_iNombrePoints = 0;
        public float [] m_tQualiteMoyenne = null;
        public float [] m_tQualiteMin = null;
        public float [] m_tQualiteMax = null;
        private float m_fQualiteMax = 0.0f;
        private float m_fQualiteMin = 0.0f;
        private int m_iPositionYAxeAbscisse = 0;
        private Font m_fontEchelle = null;
        
        
        public PanneauGrapheQualite(int iNombrePoints, int iLargeurGraphe, int iHauteurGraphe) {

            m_iLargeurGraphe = iLargeurGraphe;
            m_iHauteurGraphe = iHauteurGraphe;
            
            m_iNombrePoints = iNombrePoints;
            
            m_fontEchelle = UtilDraw.ChargerFonte("arial.ttf");
            if (m_fontEchelle != null)
                m_fontEchelle = m_fontEchelle.deriveFont(11.0f);
            else
                m_fontEchelle = new Font("Dialog", Font.BOLD|Font.ITALIC, 10);
            
            m_graphe = new BufferedImage(m_iLargeurGraphe, m_iHauteurGraphe, BufferedImage.TYPE_INT_RGB);
        }



        private void CalculerPointsCulminants() {
            int iIndicePoint = 0;
            
            m_fQualiteMax = m_tQualiteMax[0];
            for (iIndicePoint=0; iIndicePoint<m_iNombrePoints; iIndicePoint++)
                if (m_fQualiteMax < m_tQualiteMax[iIndicePoint])
                    m_fQualiteMax = m_tQualiteMax[iIndicePoint];
            
            m_fQualiteMin = m_tQualiteMin[0];
            for (iIndicePoint=0; iIndicePoint<m_iNombrePoints; iIndicePoint++)
                if (m_fQualiteMin > m_tQualiteMin[iIndicePoint])
                    m_fQualiteMin = m_tQualiteMin[iIndicePoint];            
        }
        
        
        private void DessinerPoint(Graphics2D gc, int x, int y) {
            gc.drawLine(x, y, x, y);
        }
        
        
        public void DessinerGraphe() {
            Graphics2D gc = null;
            int iIndicePoint = 0;
            float fEchelleQualite = 0.0f;
            
            if ((m_tQualiteMoyenne==null) || (m_tQualiteMax==null) || (m_tQualiteMin==null))
                return;
                
            CalculerPointsCulminants();
            
            // Trac� des composantes communes du dessin :
            gc = m_graphe.createGraphics();

            gc.setStroke( new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER) );

            // Effacement du fond de la fen�tre :
            gc.setColor(Color.WHITE);
            gc.fillRect(0, 0, m_iLargeurGraphe, m_iHauteurGraphe);

            // Trac� des axes :
            gc.setColor(Color.BLACK);

           
            if ((m_fQualiteMax-m_fQualiteMin) == 0.0f)
                return;
            else if ( (m_fQualiteMax >= 0.0f) && (m_fQualiteMin >= 0.0f) ) {
                m_iPositionYAxeAbscisse = m_iHauteurGraphe-20;
                fEchelleQualite = ((float)(m_iHauteurGraphe-40)) / m_fQualiteMax;
            }
            else if ( (m_fQualiteMax <= 0.0f) && (m_fQualiteMin <= 0.0f) ) {
                m_iPositionYAxeAbscisse = 20;
                fEchelleQualite = ((float)(m_iHauteurGraphe-40)) / (-m_fQualiteMin);
            }
            else if ( (m_fQualiteMax * m_fQualiteMin) < 0.0f) { // Signes diff�rents
                m_iPositionYAxeAbscisse = 20 + (int)( ((float)(m_iHauteurGraphe-40)) * (m_fQualiteMax / (m_fQualiteMax-m_fQualiteMin)) );
                fEchelleQualite = ((float)(m_iHauteurGraphe-40)) / (m_fQualiteMax-m_fQualiteMin);
            }
            

            // Axe vertical :
            gc.drawLine(20, m_iHauteurGraphe-20, 20, 20);
            gc.drawLine(20, 20, 20-10, 20+10);
            gc.drawLine(20, 20, 20+10, 20+10);
            gc.drawLine(20, m_iHauteurGraphe-20, 20-10, m_iHauteurGraphe-20-10);
            gc.drawLine(20, m_iHauteurGraphe-20, 20+10, m_iHauteurGraphe-20-10);

            // Axe horizontal :
            gc.drawLine(20, m_iPositionYAxeAbscisse, m_iLargeurGraphe-20, m_iPositionYAxeAbscisse);
            gc.drawLine(m_iLargeurGraphe-20, m_iPositionYAxeAbscisse, m_iLargeurGraphe-20-10, m_iPositionYAxeAbscisse-10);
            gc.drawLine(m_iLargeurGraphe-20, m_iPositionYAxeAbscisse, m_iLargeurGraphe-20-10, m_iPositionYAxeAbscisse+10);


            // Trac� des courbes d'�volution de la qualit� :
            
            // Qualit� moyenne :
            gc.setColor(Color.BLUE);
            for (iIndicePoint=0; iIndicePoint<m_iNombrePoints; iIndicePoint++)
                DessinerPoint(gc, 20+iIndicePoint, m_iPositionYAxeAbscisse-(int)(m_tQualiteMoyenne[iIndicePoint]*fEchelleQualite));
                
            // Qualit� max :
            gc.setColor(Color.RED);
            for (iIndicePoint=0; iIndicePoint<m_iNombrePoints; iIndicePoint++)
                DessinerPoint(gc, 20+iIndicePoint, m_iPositionYAxeAbscisse-(int)(m_tQualiteMax[iIndicePoint]*fEchelleQualite));

            // Qualit� min :
            gc.setColor(Color.GREEN);
            for (iIndicePoint=0; iIndicePoint<m_iNombrePoints; iIndicePoint++)
                DessinerPoint(gc, 20+iIndicePoint, m_iPositionYAxeAbscisse-(int)(m_tQualiteMin[iIndicePoint]*fEchelleQualite));

            // Bornes :
            gc.setColor(Color.BLACK);            
            gc.setFont(m_fontEchelle);
            gc.drawString(String.valueOf(m_fQualiteMax), 10, 18);
            gc.drawString(String.valueOf(m_fQualiteMin), 10, m_iHauteurGraphe-8);            
        }


        protected void paintComponent(Graphics g) {
            Graphics2D g2D = null;

            super.paintComponent(g); 

            g2D = (Graphics2D)g;

            g2D.drawImage(m_graphe, new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR), 0, 0);
        }
       

    }
    
    
    private PanneauGrapheQualite m_panneauGrapheQualite = null;
    private float [] m_tQualiteMoyenne = null;
    private float [] m_tQualiteMin = null;
    private float [] m_tQualiteMax = null;    
    
    
    /** Creates new form DialogGrapheQualite */
    public DialogGraphQuality(java.awt.Frame parent, boolean modal, ResolutionContext contexteResolution) {
        super(parent, modal);
        initComponents();
        
        setSize(600, 600);
        validate();
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jButtonFermer = new javax.swing.JButton();
        jScrollGrapheQualite = new javax.swing.JScrollPane();

        getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jButtonFermer.setText("Close");
        jButtonFermer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFermerActionPerformed(evt);
            }
        });

        getContentPane().add(jButtonFermer);
        jButtonFermer.setBounds(333, 160, 90, 26);

        getContentPane().add(jScrollGrapheQualite);
        jScrollGrapheQualite.setBounds(0, 0, 430, 150);

        pack();
    }//GEN-END:initComponents

    private void jButtonFermerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFermerActionPerformed
        FermerBoiteDialogue();
    }//GEN-LAST:event_jButtonFermerActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        FermerBoiteDialogue();
    }//GEN-LAST:event_closeDialog
    
    
    private void FermerBoiteDialogue() {
        setVisible(false);
        dispose();  
    }
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonFermer;
    private javax.swing.JScrollPane jScrollGrapheQualite;
    // End of variables declaration//GEN-END:variables
    
    
    
    // Repositionne chaque contr�le pour remplir au mieux l'espace de la fen�tre :
    void ArrangerDisposition() {
        Dimension tailleZoneFenetre = null;
        
        tailleZoneFenetre = getContentPane().getSize();
        
        jScrollGrapheQualite.setBounds(0, 0, tailleZoneFenetre.width, tailleZoneFenetre.height-(jButtonFermer.getHeight()+20));
        jButtonFermer.setLocation(tailleZoneFenetre.width-jButtonFermer.getWidth()-20, jScrollGrapheQualite.getHeight()+10);
    }
    
    
    
    public void SpecifierQualitesMoyennes(float [] tQualiteMoyenne) {
        m_tQualiteMoyenne = tQualiteMoyenne;
    }
    
    
    
    public void SpecifierQualitesMax(float [] tQualiteMax) {
        m_tQualiteMax = tQualiteMax;
    }
    
    
    
    public void SpecifierQualitesMin(float [] tQualiteMin) {
        m_tQualiteMin = tQualiteMin;
    }
    
    
    
    public void ConstruireGraphe() {
        int iNombrePoints = 0;
        
        iNombrePoints = m_tQualiteMoyenne.length;
        m_panneauGrapheQualite = new PanneauGrapheQualite(iNombrePoints, 50+iNombrePoints, 500);
        
        m_panneauGrapheQualite.m_tQualiteMoyenne = m_tQualiteMoyenne;
        m_panneauGrapheQualite.m_tQualiteMax = m_tQualiteMax;
        m_panneauGrapheQualite.m_tQualiteMin = m_tQualiteMin;
        
        m_panneauGrapheQualite.DessinerGraphe();
        
        jScrollGrapheQualite.setViewportView( m_panneauGrapheQualite );
        jScrollGrapheQualite.validate();
        
        ArrangerDisposition();
        validate();
    }
    
}
