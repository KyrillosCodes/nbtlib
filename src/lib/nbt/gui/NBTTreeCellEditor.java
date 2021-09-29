package lib.nbt.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import lib.nbt.SyncTag;
import lib.nbt.Tag;

/**
 * A TreeCellEditor used for editing NBT data in {@link lib.nbt.gui.NBTTree}
 * 
 * @author Kyrillos Tawadros
 */
public class NBTTreeCellEditor extends JPanel implements TreeCellEditor, Cloneable {
  
  private JLabel fieldName;
  private JTextField fieldValue;
  
  private Tag editing;
  private TreePath editingPath;
  
  public NBTTreeCellEditor(final JTree treeFor) {
    super();
    
    fieldName = new JLabel();
    fieldValue = new JTextField();
    
    add(fieldName);
    add(fieldValue);
    
    fieldValue.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
          treeFor.stopEditing();
          evt.consume();
        }
      }
    });
  }
  
  public TreePath getEditingPath() {
    return editingPath;
  }

  @Override
  public Object getCellEditorValue() {
    return editing;
  }
  
  private boolean canEdit = false;
  
  public void enable() {
    canEdit = true;
  }
  
  public void disable() {
    canEdit = false;
  }

  @Override
  public boolean isCellEditable(EventObject anEvent) {
    return canEdit;
  }

  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    return true;
  }

  @Override
  public boolean stopCellEditing() {
    if (editing == null) return true;
    
    String text = fieldValue.getText();
    
    boolean success = true;
    try {
      if (editing.getType().isMultiple()) {
        editing.setName(text);
      } else {
        Object newValue = editing.getType().fromString(text);
        editing.setValue(newValue);
      }
    } catch (NumberFormatException nfe) {
      success = false;
    }
    
    if (success) {
      ChangeEvent evt = new ChangeEvent(this);
      fireStopEvent(evt);
    }
    
    return success;
  }

  @Override
  public void cancelCellEditing() {
    ChangeEvent evt = new ChangeEvent(this);
    fireCancelEvent(evt);
  }
  
  private void fireStopEvent(ChangeEvent evt) {
    for (CellEditorListener listener : listeners) {
      listener.editingStopped(evt);
    }
  }
  
  private void fireCancelEvent(ChangeEvent evt) {
    for (CellEditorListener listener : listeners) {
      listener.editingCanceled(evt);
    }
  }

  private ArrayList<CellEditorListener> listeners = new ArrayList<>();
  @Override
  public void addCellEditorListener(CellEditorListener l) {
    listeners.add(l);
  }

  @Override
  public void removeCellEditorListener(CellEditorListener l) {
    listeners.remove(l);
  }

  @Override
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
      boolean leaf, int row) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
    Tag tag = (Tag)node.getUserObject();
    editing = tag;
    editingPath = new TreePath(node.getPath());
    if (tag.getName() != null && !tag.getType().isMultiple()) {
      fieldName.setText(tag.getName() + ":");
    } else {
      fieldName.setText("");
    }
    
    fieldName.setIcon(NBTTreeCellRenderer.getIconFor(tag.getType()));
    if (!tag.getType().isMultiple()) {
      fieldValue.setText(String.valueOf(tag.getValue()));
    } else {
      fieldValue.setText(tag.getName());
    }
    
    if (fieldValue.getPreferredSize().width < 50) {
      fieldValue.setPreferredSize(new Dimension(50, fieldValue.getPreferredSize().height));
    } else {
      fieldValue.setPreferredSize(null);
    }
    
    UIDefaults defaults = UIManager.getDefaults();
    setBackground(defaults.getColor("Tree.selectionBackground"));
    setForeground(defaults.getColor("Tree.selectionForeground"));
    
    setOpaque(true);
    return this;
  }

}
