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
package src.tools;


public class FileTools {
    
    
    public static String ObtenirCheminSansExtension(String sCheminFichier) {
        int iPositionExtension = 0;
        
        if (sCheminFichier == null)
            return null;
        
        iPositionExtension = sCheminFichier.lastIndexOf('.');
        
        if (iPositionExtension<0) 
            return sCheminFichier;
        
        return sCheminFichier.substring(0, iPositionExtension);
    }
    
    
    
    public static String AssurerBonneExtension(String sCheminFichier, String sExtension) {
        String sNomFichierEnMinuscules = null;
        String sExtensionEnMinuscules = null;
        
        if ( (sCheminFichier == null) || (sExtension == null) )
            return null;
        
        sNomFichierEnMinuscules = (sCheminFichier.trim()).toLowerCase();
        sExtensionEnMinuscules = "." + sExtension.toLowerCase();
        
        if (sNomFichierEnMinuscules.endsWith(sExtensionEnMinuscules))
            return sCheminFichier;
        else
            return (ObtenirCheminSansExtension(sCheminFichier) + "." + sExtension);
    }
  
    
}
