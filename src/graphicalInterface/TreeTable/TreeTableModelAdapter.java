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
package src.graphicalInterface.TreeTable;


import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;



public class TreeTableModelAdapter extends AbstractTableModel
{
    
    JTree m_tree;
    TreeTableModel m_treeTableModel;

    
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        m_tree = tree;
        m_treeTableModel = treeTableModel;

        m_tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {  
                fireTableDataChanged(); 
	    }
            public void treeCollapsed(TreeExpansionEvent event) {  
                fireTableDataChanged(); 
	    }
	});
    }

    

    public int getColumnCount() {
	return m_treeTableModel.getColumnCount();
    }
    
    

    public String getColumnName(int column) {
	return m_treeTableModel.getColumnName(column);
    }

    
    
    public Class getColumnClass(int column) {
	return m_treeTableModel.getColumnClass(column);
    }

    
    
    public int getRowCount() {
	return m_tree.getRowCount();
    }

    
    
    protected Object nodeForRow(int row) {
	TreePath treePath = m_tree.getPathForRow(row);
	return treePath.getLastPathComponent();         
    }

    
    
    public Object getValueAt(int row, int column) {
	return m_treeTableModel.getValueAt(nodeForRow(row), column);
    }

    
    
    public boolean isCellEditable(int row, int column) {
         return m_treeTableModel.isCellEditable(nodeForRow(row), column); 
    }

    
    
    public void setValueAt(Object value, int row, int column) {
	m_treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
    
}

