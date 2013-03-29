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

import java.io.File;
import java.util.*;
import javax.swing.filechooser.*;


public class FiltreChoiceFiles extends FileFilter {//filefilter means what kind of file to open

    private ArrayList m_listeExtensions = null;
    private String m_sDescription = null;


    
    public FiltreChoiceFiles(String sDescription) {//e.g. sDescription = "File DBase 4"
 	m_listeExtensions = new ArrayList();
        if(sDescription!=null)
            m_sDescription = new String(sDescription);
    }

    

    public boolean accept(File fichier) {
	String sExtension = null;
        
        if(fichier != null) {
	    
            if(fichier.isDirectory())
		return true;
            
	    sExtension = ObtenirExtension(fichier);
	    if (sExtension!=null)
                if (m_listeExtensions.contains(sExtension))
                    return true;
	}
        
	return false;
    }


    
    public String ObtenirExtension(File fichier) {
	String sNomFichier = null;
        int iIndiceLettre = 0;
        
        if (fichier != null) {
            
	    sNomFichier = fichier.getName();
	    iIndiceLettre = sNomFichier.lastIndexOf('.');
	    
            if( (iIndiceLettre>0) && (iIndiceLettre<sNomFichier.length()-1) ) // On �limine le cas tr�s particulier d'un fichier commen�ant par un '.' (improbable sous Windows)
		return sNomFichier.substring(iIndiceLettre+1).toLowerCase();
            
	}
	return null;
    }


    
    public void AjouterExtension(String sExtension) {
	int iPositionPoint = 0;
        
        if (sExtension != null) {
            
            // On retire un '.' �ventuel devant l'extension proprement dite :
            iPositionPoint = sExtension.lastIndexOf('.');
	    if( iPositionPoint>=0 )
		iPositionPoint++;
            else
                iPositionPoint = 0;

            m_listeExtensions.add( sExtension.substring(iPositionPoint).toLowerCase() );
        }
    }



    // Fontion abstraite surcharg�e :
    public String getDescription() {
	String sDescriptionComplete = null;
        int iNombreExtensions = 0;
        int iIndiceExtension = 0;
        
        iNombreExtensions = m_listeExtensions.size();
        
        if (iNombreExtensions==0) return "";
        
        if (m_sDescription != null)
            sDescriptionComplete = m_sDescription + " (";
        else
            sDescriptionComplete = new String("(");
        
        sDescriptionComplete += "." + (String)m_listeExtensions.get(0);
        
        for (iIndiceExtension=1;iIndiceExtension<iNombreExtensions;iIndiceExtension++)
            sDescriptionComplete += ", ." + (String)m_listeExtensions.get(iIndiceExtension);

	return sDescriptionComplete + ")";
    }

}
