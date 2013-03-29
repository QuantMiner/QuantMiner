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
package src.graphicalInterface.TableEvolvedCells;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.event.*;

import src.tools.*;



public class CelluleCheckButton3StatesEditor extends JButton implements TableCellEditor {
     
    int m_iEtat = 0;    // 0 pour non coch�, 1 pour coch�, et -1 pour "les deux � la fois"
    ImageIcon iconeCoche = null;
    ImageIcon iconeNonCoche = null;
    ImageIcon iconeSemiCoche = null;
    
    
    public CelluleCheckButton3StatesEditor() {
        m_iEtat = 0;
        iconeCoche = new ImageIcon( ENV.REPERTOIRE_RESSOURCES + "case_coche.jpg" );
        iconeNonCoche = new ImageIcon( ENV.REPERTOIRE_RESSOURCES + "case_noncoche.jpg" );
        iconeSemiCoche = new ImageIcon( ENV.REPERTOIRE_RESSOURCES + "case_semicoche.jpg" );
        setText("");
    }
    
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
            
        // Select the current value
        m_iEtat = ((Integer)value).intValue();
        
        if (m_iEtat == 1)
            setIcon(iconeCoche);
        else if (m_iEtat == 0)
            setIcon(iconeNonCoche);
        else 
            setIcon(iconeSemiCoche);  
        
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBouton3EtatsActionPerformed(evt);
            }
        } );
        
        return this;    
    }

    
    
    private void jBouton3EtatsActionPerformed(java.awt.event.ActionEvent evt) {
        if (m_iEtat == 0) {
            m_iEtat = 1;
            setIcon(iconeCoche);
        }
        else {
            m_iEtat = 0;
            setIcon(iconeNonCoche);
        }
        stopCellEditing();
    }
    
    
    public Object getCellEditorValue() {
        return new Integer(m_iEtat);
    }
    
    
    
    public boolean isCellEditable(java.util.EventObject anEvent) { return true; }
    
    public boolean shouldSelectCell(java.util.EventObject anEvent) { return false; }
    
    
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }
    
    
    public void cancelCellEditing() {
        fireEditingCanceled();
    }


    
    public void addCellEditorListener(CellEditorListener l) {
	listenerList.add(CellEditorListener.class, l);
    }

    
    
    public void removeCellEditorListener(CellEditorListener l) {
	listenerList.remove(CellEditorListener.class, l);
    }


    
    protected void fireEditingStopped() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		((CellEditorListener)listeners[i+1]).editingStopped(new ChangeEvent(this));
	    }	       
	}
    }


    
    protected void fireEditingCanceled() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		((CellEditorListener)listeners[i+1]).editingCanceled(new ChangeEvent(this));
	    }	       
	}
    }
    
   
}    



