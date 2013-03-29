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
import javax.swing.filechooser.*;
import java.io.*;
import java.util.ArrayList;
import java.awt.*;

import javax.sound.sampled.*;

import src.tools.*;

public class ToolsInterface {
    
    public static String DialogSauvegardeFichier(Component parent, String sCheminDossier, String sDescriptionExtension, String sExtension) {
        JFileChooser fenetreChoixFichier = null;
        FiltreChoiceFiles filtreChoix = null;
        int iResultatChoixFichier = 0;
        File fichierChoisi = null;
        String sFichierChoisi = null;
        boolean bFichierCibleOK = false;
        
        fenetreChoixFichier = new JFileChooser(sCheminDossier);
        fenetreChoixFichier.setMultiSelectionEnabled(false);
        fenetreChoixFichier.setAcceptAllFileFilterUsed(false);
        
        filtreChoix = new FiltreChoiceFiles(sDescriptionExtension);
        filtreChoix.AjouterExtension(sExtension);
        fenetreChoixFichier.setFileFilter(filtreChoix);
        
        bFichierCibleOK = false;
        
        iResultatChoixFichier = fenetreChoixFichier.showSaveDialog(parent);
        if (iResultatChoixFichier == JFileChooser.APPROVE_OPTION) {

            fichierChoisi = fenetreChoixFichier.getSelectedFile();
            
            if (fichierChoisi != null) {
                sFichierChoisi = fichierChoisi.getAbsolutePath();
                
                // Correction de l'extension si besoin :
                sFichierChoisi = FileTools.AssurerBonneExtension(sFichierChoisi, sExtension);
                fichierChoisi = new File(sFichierChoisi);
                
                if ( fichierChoisi.exists() )
                    bFichierCibleOK = ( JOptionPane.showConfirmDialog(
                        parent,
                         "This file exists, do you want to replace it?",
                        "Confirmation replace file.",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION );
                else
                    bFichierCibleOK = true;
            }
        }
        
        if (bFichierCibleOK)
            return sFichierChoisi;
        else
            return null;
    }
    
    public static String DialogOuvertureFichier(Component parent, String sCheminDossier, ArrayList<String> sDescription, ArrayList<String> sExtention){//, String sDescriptionExtension, String sExtension) {
        JFileChooser fenetreChoixFichier = null;
        FiltreChoiceFiles filtreChoix = null;
        int iResultatChoixFichier = 0;
        File fichierChoisi = null;
        String sFichierChoisi = null;
        boolean bFichierCibleOK = false;
       
        fenetreChoixFichier = new JFileChooser(sCheminDossier);
        fenetreChoixFichier.setMultiSelectionEnabled(false);
        fenetreChoixFichier.setAcceptAllFileFilterUsed(false);
        
        assert(sDescription.size() == sDescription.size());
        
        for (int i =0; i < sDescription.size(); i++)
        {
        	filtreChoix = new FiltreChoiceFiles(sDescription.get(i));
        	filtreChoix.AjouterExtension(sExtention.get(i));
        	fenetreChoixFichier.addChoosableFileFilter(filtreChoix);
        }
        
        
        bFichierCibleOK = false;
        
        iResultatChoixFichier = fenetreChoixFichier.showOpenDialog(parent);
        if (iResultatChoixFichier == JFileChooser.APPROVE_OPTION) {

            fichierChoisi = fenetreChoixFichier.getSelectedFile();
            if (fichierChoisi != null) {
                sFichierChoisi = fichierChoisi.getAbsolutePath();
                bFichierCibleOK = true;
            }
        }
        
        if (bFichierCibleOK)
            return sFichierChoisi;
        else
            return null;
    }
    
    
    
    // Classe permettant la correction de la saisie dans un JTextField d'un nombre contraint � un intervalle :
    public static class VerifieurTextFieldIntervalleFloat extends InputVerifier {
        float m_fValeurMin = 0.0f;
        float m_fValeurMax = 0.0f;

        public VerifieurTextFieldIntervalleFloat(float fValeurMin, float fValeurMax) {
            super();
            
            m_fValeurMin = fValeurMin;
            m_fValeurMax = fValeurMax;
        }       
        
        
        public boolean verify(JComponent input) {
            float fValeur = 0.0f;

            
            // V�rification de la valeur de support minimal saisie :
            if (input instanceof JTextField) {
                       
                try {
                    fValeur = Float.parseFloat( ((JTextField)input).getText() );
                }
                catch (NumberFormatException e) {
                    return false;
                }

                if (fValeur > m_fValeurMax)
                    ((JTextField)input).setText( String.valueOf(m_fValeurMax) );

                if (fValeur < m_fValeurMin)
                    ((JTextField)input).setText( String.valueOf(m_fValeurMin) );

                return true;
            }
            
            return true;
        }
        
    }
    
    
    
    public static void JouerSon(String sCheminFichierSon) { //play sound 
        File fichierSon = null;
        AudioInputStream audioInputStream = null;
        AudioFormat audioFormat = null;
        SourceDataLine ligne = null;
        DataLine.Info info = null;
        int iNombreOctetsLus = 0;
        byte[] tBuffer = new byte[128000];
        
        fichierSon = new File(sCheminFichierSon);
        try {
            audioInputStream = AudioSystem.getAudioInputStream(fichierSon);
        }
        catch (Exception e) { e.printStackTrace(); return; }
        
        audioFormat = audioInputStream.getFormat();
        
        
        info = new DataLine.Info(SourceDataLine.class, audioFormat);
        
        try {
            ligne = (SourceDataLine)AudioSystem.getLine(info);
            ligne.open(audioFormat);
        }
        catch (LineUnavailableException e) { return; }
        catch (Exception e) { return; }
        
        ligne.start();
        

        while (iNombreOctetsLus != -1) {
            
            try {
                iNombreOctetsLus = audioInputStream.read(tBuffer, 0, tBuffer.length);
            }
            catch (IOException e) {
                iNombreOctetsLus = -1;
            }
            
            if (iNombreOctetsLus > 0) {
                int nBytesWritten = ligne.write(tBuffer, 0, iNombreOctetsLus);
            }
            
        }
        
        ligne.drain();
        
        ligne.close();
    }
    
    
}
