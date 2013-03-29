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
package src.database;

import java.io.*;
import java.util.*;

/**DBF file reader
 */

public class DBFReader {

	DataInputStream m_dataInputStream = null;
	int m_iNombreChamps = 0;
	DBFChamp[] m_champs = null;
	int m_iTailleEnregistrement = 0;
	int m_iNombreLignes = 0;

	final static int DBF_TYPE_CHAMP_ERRONE = 0;
	final static int DBF_TYPE_CHAMP_CARAC = 1;
	final static int DBF_TYPE_CHAMP_DATE = 2;
	final static int DBF_TYPE_CHAMP_REEL = 3;
	final static int DBF_TYPE_CHAMP_DECIMAL = 4;
	final static int DBF_TYPE_CHAMP_LOGIQUE = 5;
	final static int DBF_TYPE_CHAMP_MEMO = 6;

	public class DBFChamp {

		String m_sNom = null;                     //field name
		int m_iTypeChamp = DBF_TYPE_CHAMP_ERRONE; //field type
		int m_iTailleChamp = 0;                   //field length

		DBFChamp(String sNom, int iTypeChamp, int iTailleChamp) {
			m_sNom = sNom;
			m_iTypeChamp = iTypeChamp;
			m_iTailleChamp = iTailleChamp;
		}

		public String ObtenirNom() {
			return m_sNom;
		}

		public int ObtenirType() {
			return m_iTypeChamp;
		}

		public int ObtenirTaille() {
			return m_iTailleChamp;
		}

		/**isplay column information
		 */
		public void AfficherChamp() {
			String sType = null;

			System.out.print(m_sNom + " : ");

			sType = "type ";
			switch (m_iTypeChamp) {
			case DBF_TYPE_CHAMP_CARAC:
				sType += "caract�res";
				break;
			case DBF_TYPE_CHAMP_DATE:
				sType += "date";
				break;
			case DBF_TYPE_CHAMP_REEL:
				sType += "flottant";
				break;
			case DBF_TYPE_CHAMP_DECIMAL:
				sType += "d�cimal";
				break;
			case DBF_TYPE_CHAMP_LOGIQUE:
				sType += "bool�en";
				break;
			case DBF_TYPE_CHAMP_MEMO:
				sType += "m�mo";
				break;
			default:
				sType += "ind�termin�";
			}
			System.out.print(sType);
			System.out.print(", taille ");
			System.out.println(m_iTailleChamp);
		}

	}
    
	/**read 32bit value
	 * @param fluxEntree DataInputStream
	 * @return int
	 * @throws IOException
	 */
	public int LireValeur32Bits(DataInputStream fluxEntree) throws IOException {

		int iValeurLue = 0;
		int iDecalage = 0;

		for (iDecalage = 0; iDecalage < 32; iDecalage += 8)
			iValeurLue |= (fluxEntree.readByte() & 0xff) << iDecalage;

		return iValeurLue;
	}

	/**read 16bit value
	 * @param fluxEntree DataInputStream
	 * @return short
	 * @throws IOException
	 */
	public short LireValeur16Bits(DataInputStream fluxEntree)
			throws IOException {

		short iValeurLue = 0;
		int iDecalage = 0;

		for (iDecalage = 0; iDecalage < 16; iDecalage += 8)
			iValeurLue |= (fluxEntree.readByte() & 0xff) << iDecalage;

		return iValeurLue;
	}

	public void IgnorerOctets(DataInputStream fluxEntree, int iNombreOctets)
			throws IOException {

		int iValeurLue = 0;
		int iDecalage = 0;

		for (iDecalage = 0; iDecalage < iNombreOctets; iDecalage++)
			fluxEntree.readByte();
	}

	/**
	 * This function gets all field information, i.e. number of fields, type, name. It also gets number of records/rows
	 * This function does not read each row's value
	 * @param sNomFichier the full name of a file, including path 
	 */
	public DBFReader(String sNomFichier) { 

		boolean bChampsTousLus = false;
		boolean bLectureEnTeteCorrecte = false;
		byte octetLu = 0;
		byte[] tOctetsLus = null;
		int positionZeroTerminal = 0;
		int iIndexLettre = 0; // Lettre means letter
		int iTypeChamp = DBF_TYPE_CHAMP_ERRONE; // ERRONE means error
		String sNomChamp = null;
		int iTailleChamp = 0;
		Vector<DBFChamp> champs = null;
		DBFChamp champ = null;

		// Structure Informations about DBF file :
		byte signature; // 1st byte "database start signal（if database includes DBT file->80H，else->03H）"
		byte annee; // 2nd byte  annee means year "file create or modify date（YYMMDD with YY=Date-1900）"
		byte mois; // 3rd byte   mois means month
		byte jour; // 4th byte   jour means day
		short longueurEnTete; // 9-10 Length of File structure information

		try {
			m_dataInputStream = new DataInputStream(new FileInputStream(
					sNomFichier));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			m_dataInputStream = null;
		}

		if (m_dataInputStream == null)
			return;

		try {
			signature = m_dataInputStream.readByte(); //Reads one signed input byte. range -128 through 127
			annee = m_dataInputStream.readByte();
			mois = m_dataInputStream.readByte();
			jour = m_dataInputStream.readByte();
			m_iNombreLignes = LireValeur32Bits(m_dataInputStream); //lire means read. number of lines 5-8bytes
			//5-8bytes number of database records，with lower bytes in front and higher bytes after
			
			longueurEnTete = LireValeur16Bits(m_dataInputStream);
			System.out.println("longueurEnTete " + longueurEnTete);
			m_iTailleEnregistrement = (int) LireValeur16Bits(m_dataInputStream); //11-12bytes　the total length of each record
			System.out.println("m_iTailleEnregistrement " + m_iTailleEnregistrement);
			// On se place au niveau de la description des champs :
			IgnorerOctets(m_dataInputStream, 20);  //13-32bytes　 reserved
		} catch (IOException e) {

		}
		// End of Structure Informations about DBF file
		
		champs = new Vector<DBFChamp>();

		bChampsTousLus = false;
		while (!bChampsTousLus) {

			try {
				octetLu = m_dataInputStream.readByte(); 
				if (octetLu == (byte) 0x0d) { // Indicator of termination
					// l'en-t�te
					bChampsTousLus = true;
					bLectureEnTeteCorrecte = true;
				}
			} catch (IOException e) {
				bChampsTousLus = true;
				bLectureEnTeteCorrecte = false;
			}

			if (!bChampsTousLus) {

				try {
					// Get field's name  1-11 bytes
					tOctetsLus = new byte[11];
					m_dataInputStream.read(tOctetsLus, 1, 10); //read 10 bytes to tOctetsLus, start at tOctetsLus[1]
					tOctetsLus[0] = octetLu;

					for (iIndexLettre = 10; iIndexLettre >= 0; iIndexLettre--)
						if (tOctetsLus[iIndexLettre] == 0)
							positionZeroTerminal = iIndexLettre;

					if (positionZeroTerminal > 0)
						sNomChamp = new String(tOctetsLus, 0,
								positionZeroTerminal);
					else
						sNomChamp = new String("");

					// Get field's type the 12th byte
					octetLu = m_dataInputStream.readByte();
					switch (octetLu) {
					case (byte) 0x43:
						iTypeChamp = DBF_TYPE_CHAMP_CARAC;  //type is character
						break; // 'C'
					case (byte) 0x44:
						iTypeChamp = DBF_TYPE_CHAMP_DATE;   //type is date
						break; // 'D'
					case (byte) 0x46:
						iTypeChamp = DBF_TYPE_CHAMP_REEL;   //type is float number
						break; // 'F'
					case (byte) 0x4E:
						iTypeChamp = DBF_TYPE_CHAMP_DECIMAL; //type is decimal
						break; // 'N'
					case (byte) 0x4C:
						iTypeChamp = DBF_TYPE_CHAMP_LOGIQUE; //type is boolean
						break; // 'L'
					case (byte) 0x4D:
						iTypeChamp = DBF_TYPE_CHAMP_MEMO;   //type is memo??
						break; // 'M'
					default:
						iTypeChamp = DBF_TYPE_CHAMP_ERRONE; //type is error
					}
					
					IgnorerOctets(m_dataInputStream, 4);

					// Get the length of the field:
					iTailleChamp = (int) m_dataInputStream.readUnsignedByte();

					IgnorerOctets(m_dataInputStream, 15);

					// create a field and put into field list
					champ = new DBFChamp(sNomChamp, iTypeChamp, iTailleChamp); //field name, field type and field length
					champs.add(champ);  //add to field list
				} catch (IOException e) {
				}
			}

		}

		// End of Structure of Records
		
		m_iNombreChamps = champs.size();
		//System.out.println("m_iNombreChamps before " + m_iNombreChamps);
		if (m_iNombreChamps > 0) {
			m_champs = (DBFChamp[]) champs.toArray(new DBFChamp[1]); //return an array with all elements in that vector, the array type is specified
			m_iNombreChamps = m_champs.length; // pour �tre s�r...
			System.out.println("m_iNombreChamps after " + m_iNombreChamps);

		} else
			m_champs = null;
	}

	public void close() {

		if (m_dataInputStream == null)
			return;

		try {
			m_dataInputStream.close();
		} catch (IOException e) {
		}
	}

	public int ObtenirNombreChamps() {
		return m_iNombreChamps;
	}

	public DBFChamp ObtenirChamp(int iIndexChamp) {
		if (iIndexChamp < m_iNombreChamps)
			return m_champs[iIndexChamp];
		else
			return null;
	}

	
	/**Get the index of a column by name
	 * @param sNomChamp Column name
	 * @return int
	 */
	public int ObtenirIndiceChamp(String sNomChamp) {
		int iIndiceChamp = 0;
		boolean bChampTrouve = false;
		String sNomChampEnumere = null;

		if (sNomChamp == null)
			return -1;

		iIndiceChamp = 0;
		while ((!bChampTrouve) && (iIndiceChamp < m_iNombreChamps)) {
			sNomChampEnumere = m_champs[iIndiceChamp].ObtenirNom();
			if (sNomChamp.equals(sNomChampEnumere))
				bChampTrouve = true;
			else
				iIndiceChamp++;
		}

		if (bChampTrouve)
			return iIndiceChamp;
		else
			return -1;
	}

	public int ObtenirNombreLignes() {
		return m_iNombreLignes;
	}

	public String[] LireEnregistrementSuivant() { //read subsequent data  (DBFLecteur Constructor read all data before real row values) 

		byte octetLu = 0;
		byte[] tOctetsLus = null;
		int iIndiceChamp = 0;
		int iTailleChamp = 0;
		int iTypeChamp = 0;
		String tValeursChamps[] = null;
		String sChaineLue = null;

		if (m_dataInputStream == null)
			return null;

		tValeursChamps = new String[m_iNombreChamps];

		try {

			// On ignore les enregistrements marqu�s comme effac�s :
			octetLu = 0x2A;
			while (octetLu == 0x2A) {//skip all deleted rows

				octetLu = m_dataInputStream.readByte(); 
				if (octetLu == 0x2A) { //this record has been deleted
					IgnorerOctets(m_dataInputStream, //ignore the rest record size - 1 bytes
							m_iTailleEnregistrement - 1); 
				}

			}

			// mark the end of that file:
			if (octetLu == 0x1A)
				return null;

			for (iIndiceChamp = 0; iIndiceChamp < m_iNombreChamps; iIndiceChamp++) {

				iTailleChamp = m_champs[iIndiceChamp].ObtenirTaille();
				iTypeChamp = m_champs[iIndiceChamp].ObtenirType();

				// Test de correction en cas de fichier mal con�u :
				if (iTailleChamp <= 0) {
					if (iTypeChamp == DBF_TYPE_CHAMP_DATE)
						iTailleChamp = 8;
					else if (iTypeChamp == DBF_TYPE_CHAMP_LOGIQUE)
						iTailleChamp = 1;
					else
						iTailleChamp = 0;
				}

				if (iTailleChamp > 0) {

					tOctetsLus = new byte[iTailleChamp];
					m_dataInputStream.read(tOctetsLus);

					switch (iTypeChamp) {

					case DBF_TYPE_CHAMP_CARAC:
						tValeursChamps[iIndiceChamp] = new String(tOctetsLus);
						break;

					case DBF_TYPE_CHAMP_DATE:
						tValeursChamps[iIndiceChamp] = new String(tOctetsLus);
						break;

					case DBF_TYPE_CHAMP_REEL:
						tValeursChamps[iIndiceChamp] = (new String(tOctetsLus))
								.trim();
			
						break;

					case DBF_TYPE_CHAMP_DECIMAL:
						tValeursChamps[iIndiceChamp] = (new String(tOctetsLus))
								.trim();
				 
						break;

					case DBF_TYPE_CHAMP_LOGIQUE:
						if (tOctetsLus[0] == 0x59 || tOctetsLus[0] == 0x79
								|| tOctetsLus[0] == 0x54
								|| tOctetsLus[0] == 0x74) // 'Y', 'y', 'T', 't'
							tValeursChamps[iIndiceChamp] = new String("Vrai");  //True
						else
							tValeursChamps[iIndiceChamp] = new String("Faux");  //False
						break;

					default:
						tValeursChamps[iIndiceChamp] = new String(tOctetsLus);
					}
				} else
					tValeursChamps[iIndiceChamp] = "";
			}

		} catch (EOFException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return tValeursChamps;
	}

}
