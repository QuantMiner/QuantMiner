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

import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;



public class JTreeTable extends JTable {
   
    TreeTableCellRenderer m_treeRenderer = null;
    TreeTableModel m_treeTableModel = null;
    
    
    
    /*************************/
    /* TreeTableCellRenderer */
    /*************************/

    public class TreeTableCellRenderer extends JTree implements TableCellRenderer { 
        
        protected int visibleRow; 
    
         
        TreeTableCellRenderer(TreeModel treeModel) {
            super(treeModel); 
        }
        
        
        public void updateUI() {
	    
            super.updateUI();
	    TreeCellRenderer tcr = super.getCellRenderer();
	    if (tcr instanceof DefaultTreeCellRenderer) {
		DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr); 
		dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
		dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
	    }
	}

	
        
	public void setRowHeight(int rowHeight) { 
	    if (rowHeight > 0) {
		super.setRowHeight(rowHeight); 
		if ( (JTreeTable.this!=null) && (JTreeTable.this.getRowHeight()!=rowHeight) )
		    JTreeTable.this.setRowHeight(getRowHeight()); 
	    }
	}

	
        
	public void setBounds(int x, int y, int w, int h) {
	    super.setBounds(x, 0, w, JTreeTable.this.getHeight());
	}

	
        
	public void paint(Graphics g) {
	    g.translate(0, -visibleRow * getRowHeight());
	    super.paint(g);
	}

	
        
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected)
		setBackground(table.getSelectionBackground());
	    else
		setBackground(table.getBackground());

	    visibleRow = row;
	    return this;
	}
    }

    
    
        
    /***********************/
    /* TreeTableCellEditor */
    /***********************/

    public class TreeTableCellEditor  extends AbstractCellEditor implements TableCellEditor {
	
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
	    return m_treeRenderer;
	}

	
        
	public boolean isCellEditable(EventObject e) {
	    
            if (e instanceof MouseEvent) {
		for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
		    if (getColumnClass(counter) == TreeTableModel.class) {
			MouseEvent me = (MouseEvent)e;
			MouseEvent newME = new MouseEvent(m_treeRenderer, me.getID(),
                            me.getWhen(), me.getModifiers(),
                            me.getX() - getCellRect(0, counter, true).x,
                            me.getY(), me.getClickCount(),
                            me.isPopupTrigger());
			m_treeRenderer.dispatchEvent(newME);
			break;
		    }
		}
	    }
            
	    return false;
	}
        
    }
    
    
    
    
    /***********************************/
    /* ListToTreeSelectionModelWrapper */
    /***********************************/
    
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel { 

        protected boolean updatingListSelectionModel;

	public ListToTreeSelectionModelWrapper() {
	    super();
	    getListSelectionModel().addListSelectionListener( createListSelectionListener() );
	}

	
        
	ListSelectionModel getListSelectionModel() {
	    return listSelectionModel; 
	}

	
        
	public void resetRowSelection() {
	    if (!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    super.resetRowSelection();
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	}


        
        protected ListSelectionListener createListSelectionListener() {
	    return new ListSelectionHandler();
	}


        
	protected void updateSelectedPathsFromSelectedRows() {
	    if (!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    int min = listSelectionModel.getMinSelectionIndex();
		    int max = listSelectionModel.getMaxSelectionIndex();

		    clearSelection();
		    if (min != -1 && max != -1) {
			for (int counter = min; counter <= max; counter++) {
                            if(listSelectionModel.isSelectedIndex(counter)) {
				TreePath selPath = m_treeRenderer.getPathForRow(counter);

				if (selPath != null)
				    addSelectionPath(selPath);
			    }
			}
		    }
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	}

	
        
	class ListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
		updateSelectedPathsFromSelectedRows();
	    }
	}
    }



    
    
    public JTreeTable(AbstractTreeTableModel treeTableModel) {
        
        super();
        
        m_treeRenderer = new TreeTableCellRenderer( treeTableModel );
        
	m_treeTableModel = treeTableModel;
        super.setModel( new TreeTableModelAdapter(treeTableModel, m_treeRenderer) );
        
        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
	m_treeRenderer.setSelectionModel(selectionWrapper);
	setSelectionModel(selectionWrapper.getListSelectionModel());

        m_treeRenderer.setRootVisible(false);

        setDefaultRenderer(TreeTableModel.class, m_treeRenderer); 
	setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());  
        
        setShowGrid(true);
	setIntercellSpacing(new Dimension(1, 1)); 
        
        setRowHeight(22);
        
    }  
    
    
    
    public void updateUI() {
	super.updateUI();
	if (m_treeRenderer != null)
            m_treeRenderer.updateUI();

        LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
    }

   
        
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;  
    }

    
    
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
	if ( (m_treeRenderer!=null) && (m_treeRenderer.getRowHeight()!=rowHeight) ) {
            m_treeRenderer.setRowHeight(getRowHeight());
	}
    }

    
    
    public JTree getTree() {
	return m_treeRenderer;
    }

    
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        TreePath treePath = null;
        TableCellRenderer renderer = null;
        
        renderer = null;
        if (m_treeTableModel != null) {
            treePath = m_treeRenderer.getPathForRow(row);
            if (treePath != null)
                renderer = m_treeTableModel.getCellRenderer(treePath.getLastPathComponent(), column);
        }
        
        if (renderer != null)
            return renderer;
        else
            return super.getCellRenderer(row, column);
    }
    
}
