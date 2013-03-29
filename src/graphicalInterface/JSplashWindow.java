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
import java.awt.*;
import java.io.File;
import java.net.*;

public class JSplashWindow extends JWindow{
	
	 private int m_duration;
	 private String m_path;
	    
	    public JSplashWindow(int duration, String path) {
	        m_duration = duration;
	        m_path = path;
	    }
	    
	    public void showSplash() {
	        JPanel content = (JPanel)getContentPane();
	        content.setBackground(Color.white);
	        
	        int width = 785;
	        int height =514;
	        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	        int x = (screen.width-width)/2;
	        int y = (screen.height-height)/2;
	        setBounds(x,y,width,height);
	        
	        // Build the splash screen
	        JLabel label = new JLabel(new ImageIcon( m_path + File.separator + "QuantMiner.png"));
	        content.add(label, BorderLayout.CENTER);
	        setVisible(true);
	        
	        try {
	        	Thread.sleep(m_duration); 
	        	} 
	        catch (Exception e) {}
	        	setVisible(false);
	    }
	
}
