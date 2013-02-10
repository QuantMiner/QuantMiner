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
package src.baseDeDonnees;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;

public class CsvFileParser {
	InputStream m_InputStream = null;
	int m_iNombreChamps = 0;
	int m_iNombreLignes = 0;
	private LabeledCSVParser csvParser = null;
	public String[][] m_data = null;
	public String [] m_nameChamp = null;
	
	public CsvFileParser(String nomFichier) {
		try {
			m_InputStream = new FileInputStream(new File(nomFichier));
			try {
				csvParser = new LabeledCSVParser(new ExcelCSVParser(m_InputStream));
			} catch (IOException e) {
				e.printStackTrace();
			}   
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			m_InputStream = null;
		}
		
		if (m_InputStream == null)
			return;
		
		try {
			m_data = csvParser.getAllValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			 m_nameChamp = csvParser.getLabels();
			 m_iNombreChamps = m_nameChamp.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_iNombreLignes = m_data.length;
	}

	public int ObtenirNombreLignes() {
		return m_iNombreLignes;
	}

	public int ObtenirNombreChamps() {
		return m_iNombreChamps;
	}

	public String[] ObtenirNomChamps() {
		return m_nameChamp;
	}
	
	public void close() {
		try {
			csvParser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int ObtenirIndiceChamp(String sNomChamp) {
		int iIndiceChamp = 0;
		boolean bChampTrouve = false;
		String sNomChampEnumere = null;

		if (sNomChamp == null)
			return -1;

		iIndiceChamp = 0;
		//System.out.println("===========number of columns ==========" + m_iNombreChamps);
		while ((!bChampTrouve) && (iIndiceChamp < m_iNombreChamps)) {
			sNomChampEnumere = m_nameChamp[iIndiceChamp];
			//System.out.println("sNomChampEnumere is " + sNomChampEnumere);
			//System.out.println("passed in sNomChamp is " + sNomChamp);
			if (sNomChamp.equals(sNomChampEnumere.trim()))
				bChampTrouve = true;
			else
				iIndiceChamp++;
		}

		if (bChampTrouve){
			//System.out.println("---------find-----------");
			return iIndiceChamp;	
		}
		
		else
			return -1;
	}

}
