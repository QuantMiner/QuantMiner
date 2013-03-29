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


import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;

import src.graphicalInterface.TableEvolvedCells.*;
import src.solver.*;



public class AttributsBDModel extends AbstractTreeTableModel {

    // Noms par d�faut des colonnes :
    static protected String [] tNomsDefaut = {"Attribute / Value", "Informations", "Position in the rule", "Present necessarily"};
    // Noms courants des colonnes :
    protected String [] tNoms = null;

    // Types des colonnes :
    static protected Class [] tTypes = { TreeTableModel.class, String.class, String.class, Integer.class };
    
    // Possibilit�s d'�dition :
    static protected boolean [] tEditable = { true, false, true, true };

    // Options de la colonne indiquant la position de l'item choisi :
    static protected String [] tComboBoxPositionsItem = { "2 sides", "left-hand side (condition)", "right-hand side (conclusion)", "nowhere" };
    static protected String [] tComboBoxPositionsAttribut = { "variable", "2 sides", "left-hand side (condition)", "right-hand side (conclusion)", "nowhere" };

    public static final int ELEMENT_MODEL_ATTRIBUT_QUAL = 0;
    public static final int ELEMENT_MODEL_ATTRIBUT_QUANT = 1;
    public static final int ELEMENT_MODEL_ITEM = 2;
    

    TableCellRenderer m_rendererComboAttribut = null;
    
    JTreeTable m_treeTable = null;
    
    

    static public class AttributBDDescription {
        
        private String m_sNomAttribut = null;
        private String m_sNomItem = null;
        private int m_iType = 0;
        private String m_sDescription = null;
        private PositionRuleParameters m_parametresPosition = null;
        private boolean m_bParametresConstants = false;  // Si vrai, alors les param�tres de l'attribut/item ne peuvent �tre modifi�s
        
        
        // Construit un descripteur d'attribut :
        public AttributBDDescription(String sNomAttribut, int iType, String sDescription, PositionRuleParameters parametresPosition, boolean bParametresConstants) {
            m_sNomAttribut = sNomAttribut;
            m_sNomItem = null;
            m_iType = m_iType;
            m_sDescription = sDescription;
            m_parametresPosition = parametresPosition;
            m_bParametresConstants = bParametresConstants;
        }
        
        
        // Construit un descriteur d'item (valeur d'un attribut) :
        public AttributBDDescription(String sNomAttribut, String sNomItem, String sDescription, PositionRuleParameters parametresPosition, boolean bParametresConstants) {
            m_sNomAttribut = sNomAttribut;
            m_sNomItem = sNomItem;
            m_iType = ELEMENT_MODEL_ITEM;
            m_sDescription = sDescription;
            m_parametresPosition = parametresPosition;
            m_bParametresConstants = bParametresConstants;
        }
       
        
        public String ObtenirNomAttribut() {
            if (m_iType == ELEMENT_MODEL_ITEM)
                return m_sNomItem;
            else
                return m_sNomAttribut;
        }
        
        
        public String ObtenirInformation() {
            return m_sDescription;
        }
        
        
        public boolean EstItem() {
            return (m_iType == ELEMENT_MODEL_ITEM);
        }
        
        
        public String ObtenirPositionItem() {
            int iPositionItem = 0;
           
            if (EstItem())
                iPositionItem = m_parametresPosition.ObtenirTypePrisEnCompteItem(m_sNomAttribut, m_sNomItem);
            else
                iPositionItem = m_parametresPosition.ObtenirTypePrisEnCompteAttribut(m_sNomAttribut);
            
            switch (iPositionItem) {
                    
                case ResolutionContext.PRISE_EN_COMPTE_INDEFINI :
                    return "variable";
 
                case ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE :
                    return "left-hand side (condition)";
                    
                case ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE :
                    return "right-hand side (conclusion)";
                    
                case ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES :
                    return "2 sides";
                    
                default :
                    return "nowhere";
            }
            
        }

        
        public void DefinirPositionItem(String sPositionItem) {
            int iPositionItem = 0;
            
            if (m_bParametresConstants)
                return;
            
            if (sPositionItem.equals("2 sides"))
                iPositionItem = ResolutionContext.PRISE_EN_COMPTE_ITEM_2_COTES;
            
            else if (sPositionItem.equals("left-hand side (condition)"))
                iPositionItem = ResolutionContext.PRISE_EN_COMPTE_ITEM_GAUCHE;
                    
            else if (sPositionItem.equals("right-hand side (conclusion)"))
                iPositionItem = ResolutionContext.PRISE_EN_COMPTE_ITEM_DROITE;
                    
            else if (sPositionItem.equals("nowhere"))
                iPositionItem = ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART;
            
            if (EstItem()) {
                if ( !(  (iPositionItem==ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)
                       &&(m_parametresPosition.ObtenirPresenceObligatoireItem(m_sNomAttribut, m_sNomItem))  )  )
                    m_parametresPosition.DefinirTypePrisEnCompteItem(m_sNomAttribut, m_sNomItem, iPositionItem);
            }
            else {
                if ( !(  (iPositionItem==ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)
                       &&(m_parametresPosition.ObtenirPresenceObligatoireAttribut(m_sNomAttribut)!=0)  )  )
                    m_parametresPosition.DefinirTypePrisEnCompteAttribut(m_sNomAttribut, iPositionItem);
            }
        }

        
        
        // Renvoie 0 pour faux, 1 pour vrai, et -1 pour indiquer que toutes les valeurs ne sont pas les m�mes
        public int ObtenirPresenceObligatoireItem() {
            
            if (EstItem()) {
                if (m_parametresPosition.ObtenirPresenceObligatoireItem(m_sNomAttribut, m_sNomItem))
                    return 1;
                else
                    return 0;
            }
            else
                return m_parametresPosition.ObtenirPresenceObligatoireAttribut(m_sNomAttribut);
        }

        
        public void DefinirPresenceObligatoireItem(boolean bPresenceObligatoire) {

            if (m_bParametresConstants)
                return;
                        
            if (EstItem()) {
                if (m_parametresPosition.ObtenirTypePrisEnCompteItem(m_sNomAttribut, m_sNomItem) != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)
                    m_parametresPosition.DefinirPresenceObligatoireItem(m_sNomAttribut, m_sNomItem, bPresenceObligatoire);
            }
            else {
                if (m_parametresPosition.ObtenirTypePrisEnCompteAttribut(m_sNomAttribut) != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART)
                    m_parametresPosition.DefinirPresenceObligatoireAttribut(m_sNomAttribut, bPresenceObligatoire);
            }
        }

        
        
        // Cha�ne qui s'affiche au niveau d'un noeud de l'arbre :
        public String toString() { 
	    return ObtenirNomAttribut();
	}

    }
    
    
    

    public AttributsBDModel() { 
        super( new DefaultMutableTreeNode( null ) );
        
        int iIndiceNom = 0;
        int iNombreNoms = 0;
        
        iNombreNoms = tNomsDefaut.length;
        
        tNoms = new String [ iNombreNoms ];
        for (iIndiceNom=0; iIndiceNom<iNombreNoms; iIndiceNom++)
            tNoms[iIndiceNom] = tNomsDefaut[iIndiceNom];

        m_treeTable = null;
    }

  
    
    public void ModifierNomColonne(int iIndiceColonne, String sNouveauNom) {
        if (iIndiceColonne < tNomsDefaut.length)
            tNoms[iIndiceColonne] = new String(sNouveauNom);
    }
    


    // Ajoute certaines sp�cificit�s � l'arbre-table associ� au mod�le de donn�es :
    public void AdapterTreeTableAModele(JTreeTable treeTable) {
        TableColumn colonneTableau = null;
        
        // La colonne indiquant la position de l'item prend la forme d'une combo box :
        colonneTableau = treeTable.getColumnModel().getColumn(2);
        
        colonneTableau.setCellEditor( new CelluleComboBoxEditor( tComboBoxPositionsItem ) );
        colonneTableau.setCellRenderer( new CelluleComboBoxRenderer( tComboBoxPositionsItem ) );
        
        m_rendererComboAttribut = new CelluleComboBoxRenderer( tComboBoxPositionsAttribut );

        
        // La colonne indiquant la position de l'item prend la forme d'une combo box :
        colonneTableau = treeTable.getColumnModel().getColumn(3);
        colonneTableau.setCellEditor( new CelluleCheckButton3StatesEditor() );
        colonneTableau.setCellRenderer( new CelluleCheckButton3StatesRenderer() );
        
        colonneTableau.setMaxWidth(140);
        colonneTableau.setMinWidth(140);
        colonneTableau.setPreferredWidth(140);
        
        m_treeTable = treeTable;
    }
    
    
    
    public DefaultMutableTreeNode AjouterNoeud(DefaultMutableTreeNode noeudParent, AttributBDDescription attribut) {
        
        DefaultMutableTreeNode nouveauNoeud = null;
        
        if ( (noeudParent==null) || (attribut==null) )
            return null;
        
        nouveauNoeud = new DefaultMutableTreeNode(attribut);
        
        noeudParent.add(nouveauNoeud); //add new node
        
        return nouveauNoeud;
    }
    
    
    
    
    //
    // M�thodes issues de l'interface 'TreeModel' :
    //
  
    public int getChildCount(Object node) { 
	return ((DefaultMutableTreeNode)node).getChildCount();
    }

    
    
    public Object getChild(Object node, int i) { 
	return ((DefaultMutableTreeNode)node).getChildAt(i); 
    }

   
    
    public boolean isLeaf(Object node) {
	return ((DefaultMutableTreeNode)node).isLeaf();
    }

    
    
    //
    // M�thodes issues de l'interface 'TreeTableModel' :
    //

    public int getColumnCount() {
	return tNoms.length;
    }

    
    
    public String getColumnName(int column) {
	return tNoms[column];
    }

    
    
    public Class getColumnClass(int column) {
	return tTypes[column];
    }

   
    
    public Object getValueAt(Object node, int column) {
	DefaultMutableTreeNode defaultNode = null;
        AttributBDDescription attributDescription = null;
        int iIndicateurPresenceObligatoire = 0;
        
        defaultNode = (DefaultMutableTreeNode)node;
        attributDescription = (AttributBDDescription)(defaultNode.getUserObject());

        if (attributDescription == null)
            return null;
        
        switch(column) {
            case 0:
                return attributDescription.ObtenirNomAttribut();
            case 1:
                return attributDescription.ObtenirInformation();
            case 2:
                return attributDescription.ObtenirPositionItem();
            case 3:
                return new Integer( attributDescription.ObtenirPresenceObligatoireItem() );
	}
   
	return null;
    }
    
    
    
    public void setValueAt(Object aValue, Object node, int column) {
	DefaultMutableTreeNode defaultNode = null;
        AttributBDDescription attributDescription = null;
                
        defaultNode = (DefaultMutableTreeNode)node;
        attributDescription = (AttributBDDescription)(defaultNode.getUserObject());
        
        if (attributDescription == null)
            return;

        switch(column) {
            case 0:
                // Rien � modifier : le nom de l'attribut n'est pas �ditable
                break;
            case 1:
                // Rien � modifier : l'information sur l'attribut est en lecture seule
                break;
            case 2:
                if (aValue!=null) {
                    attributDescription.DefinirPositionItem( (String)aValue );
                    IndiquerColonneChangee(2);
                }
                break;
            case 3:
                if (aValue!=null) {
                    attributDescription.DefinirPresenceObligatoireItem( ((Integer)aValue).intValue() == 1 );
                    IndiquerColonneChangee(3);
                }
                break;
            }
    }

    
    
    public boolean isCellEditable(Object node, int column) { 
        return tEditable[column]; 
    }


    
    public TableCellRenderer getCellRenderer(Object node, int column) {
	DefaultMutableTreeNode defaultNode = null;
        AttributBDDescription attributDescription = null;
                
        defaultNode = (DefaultMutableTreeNode)node;
        attributDescription = (AttributBDDescription)(defaultNode.getUserObject());
        
        if (attributDescription == null)
            return null;
        
        switch(column) {
            case 0:
                return null;
            case 1:
                return null;
	    case 2:
                if (!attributDescription.EstItem())
                    return m_rendererComboAttribut;
                else 
                    return null;
            case 3:
                return null;
	}
        
        return null;
    }

    
    
    public void IndiquerColonneChangee(int iColonne) {
	TableModel tableModel = null;
        int iNombreLignes = 0;

	if (m_treeTable == null)
            return;
	
	tableModel = m_treeTable.getModel();
        if (tableModel == null)
            return;
        
        iNombreLignes = tableModel.getRowCount();
	if (iNombreLignes==0)
            return;

        m_treeTable.tableChanged( new TableModelEvent(tableModel, 0, iNombreLignes-1, iColonne) );
    }

}
