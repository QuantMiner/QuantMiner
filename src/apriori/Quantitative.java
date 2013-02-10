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
package src.apriori;

public class Quantitative {
   protected String m_name;
   protected float m_lower;
   protected float m_upper;
public String getM_name() {
	return m_name;
}
public void setM_name(String m_name) {
	this.m_name = m_name;
}
public float getM_lower() {
	return m_lower;
}
public void setM_lower(float m_lower) {
	this.m_lower = m_lower;
}
public float getM_upper() {
	return m_upper;
}
public void setM_upper(float m_upper) {
	this.m_upper = m_upper;
}
   
}
