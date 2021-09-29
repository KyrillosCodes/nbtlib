package lib.nbt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lib.nbt.MapSyncTag;
import lib.nbt.NBTArrayList;
import lib.nbt.NBTList;
import lib.nbt.NBTType;
import lib.nbt.SyncTag;
import lib.nbt.Tag;

/**
 * An extension of {@link javax.swing.JTree} used to
 * display and manipulate NBT data
 * 
 * @author Kyrillos Tawadros
 */
public class NBTTree extends JTree {
  private DefaultMutableTreeNode root;
  private DefaultTreeModel model;

  private NBTTreeCellRenderer renderer;
  private NBTTreeCellEditor editor;

  // Context menus
  private JPopupMenu compound_context;
  private JPopupMenu list_context;
  private JPopupMenu item_context;
  
  private DefaultMutableTreeNode selected;

  private JMenuItem add_element;

  /**
   * @param root A compound tag containing the data to populate this tree with.
   * 
   * <b>NOTICE:</b> All changes to this tree will be reflected in <code>root</code>.
   * You must make a deep copy of <code>root</code> if you want to avoid this.
   */
  public NBTTree(Tag root) {
    super();

    model = new DefaultTreeModel(new DefaultMutableTreeNode());
    setModel(model);

    setRoot(root);
    setEditable(false);

    renderer = new NBTTreeCellRenderer();
    setCellRenderer(renderer);

    editor = new NBTTreeCellEditor(this);
    setCellEditor(editor);

    this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    configureContext();

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent evt) {
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1 && isEditable()) {
          TreePath selected = NBTTree.this.getPathForLocation(evt.getX(), evt.getY());
          if (selected == null)
            return;
          DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selected.getLastPathComponent();
          Tag tag = (Tag) dmtn.getUserObject();
          if (!tag.getType().isMultiple()) {
            editor.enable();
            NBTTree.this.startEditingAtPath(selected);
            editor.disable();
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger() && isEditable()) {
          TreePath selected = NBTTree.this.getPathForLocation(evt.getX(), evt.getY());
          if (selected != null) {
            NBTTree.this.setSelectionPath(selected);
            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selected.getLastPathComponent();
            Tag tag = (Tag) dmtn.getUserObject();

            NBTTree.this.selected = dmtn;
            if (tag.getType() == NBTType.COMPOUND) {
              compound_context.show(NBTTree.this, evt.getX(), evt.getY());
            } else if (tag.getType() == NBTType.LIST) {
              add_element.setIcon(NBTTreeCellRenderer.getIconFor(((NBTList) tag.getValue()).type));
              list_context.show(NBTTree.this, evt.getX(), evt.getY());
            } else if (tag.getType().isMultiple()) {
              // Array types
              add_element.setIcon(NBTTreeCellRenderer.getIconFor(tag.getType().getElementType()));
              list_context.show(NBTTree.this, evt.getX(), evt.getY());
            } else {
              item_context.show(NBTTree.this, evt.getX(), evt.getY());
            }
          }
        }
      }
    });
  }

  /**
   * Initializes the tree with no default data.
   */
  public NBTTree() {
    this(null);
  }
  
  private NBTType promptForType() {
    String[] choices = NBTType.choices();
    String type_name = (String) JOptionPane.showInputDialog(null, "Select a list element type", "List Type",
        JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
    return NBTType.fromDisplayString(type_name);
  }
  
  private HashMap<DefaultMutableTreeNode, NBTArrayList> nbt_arrays = new HashMap<>();
  private void configureContext() {
    compound_context = new JPopupMenu();

    ActionListener item_listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        JMenuItem source = (JMenuItem) evt.getSource();
        NBTType to_add = NBTType.valueOf(source.getName());
        String tagName = JOptionPane.showInputDialog(null, "Enter the new tag name:", source.getText(),
            JOptionPane.QUESTION_MESSAGE);

        Tag compound_tag = (Tag) selected.getUserObject();

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) compound_tag.getValue();
        for (String key : data.keySet()) {
          if (key.equals(tagName)) {
            JOptionPane.showMessageDialog(null, "A tag with that name already exists", "Invalid Tag Name",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
        }

        Tag new_tag;
        if (to_add == NBTType.LIST) {
          // Select List Type
          NBTType type = promptForType();
          new_tag = new MapSyncTag(tagName, new NBTList(type), data);
        } else {
          new_tag = new MapSyncTag(tagName, to_add, data);
        }
        
        DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(new_tag);

        int insIndex = -1;
        for (int i = 0; i < selected.getChildCount(); i++) {
          DefaultMutableTreeNode current = (DefaultMutableTreeNode) selected.getChildAt(i);
          String name = ((Tag) current.getUserObject()).getName();

          int compResult = tagName.compareToIgnoreCase(name);
          if (compResult < 0) {
            insIndex = i;
            break;
          }
        }

        if (insIndex == -1) {
          insIndex = selected.getChildCount();
        }

        selected.insert(new_node, insIndex);
        ((DefaultTreeModel) NBTTree.this.model).reload(selected);

        TreePath target = new TreePath(new_node.getPath());
        NBTTree.this.getSelectionModel().setSelectionPath(target);
        NBTTree.this.scrollPathToVisible(target);

        selected = null;
      }
    };

    for (NBTType type : NBTType.values()) {
      if (type == NBTType.END)
        continue;
      JMenuItem add_type = new JMenuItem("Add " + type.toDisplayString());
      add_type.setName(type.enumName());
      add_type.setIcon(NBTTreeCellRenderer.getIconFor(type));
      add_type.addActionListener(item_listener);
      compound_context.add(add_type);
    }

    // List context
    list_context = new JPopupMenu();

    add_element = new JMenuItem("Add Element");
    add_element.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        Tag list_tag = (Tag) selected.getUserObject();
        Object obj = list_tag.getValue();
        NBTType type = NBTType.typeOf(obj);
        
        NBTList list;
        if (type != NBTType.LIST) {
          if (nbt_arrays.containsKey(selected)) {
            list = nbt_arrays.get(selected);
          } else {
            list = new NBTArrayList(list_tag);
            nbt_arrays.put(selected, (NBTArrayList)list);
          }
        } else {
          list = (NBTList) list_tag.getValue();
        }
        
        NBTType list_type = list.type;

        // Whenever this tag's value is set, it will update the associate list item as
        // well
        Object defaultValue;
        if (list_type == NBTType.LIST) {
          defaultValue = new NBTList(promptForType());
        } else {
          defaultValue = list_type.defaultValue();
        }
        SyncTag new_tag = new SyncTag(null, defaultValue, list, list.size());
        list.add(new_tag.getValue());

        // Need to sync changes to list with NBT model
        DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(new_tag);
        selected.add(dmtn);
        ((DefaultTreeModel) NBTTree.this.model).reload(selected);
        
        selected = null;
      }
    });

    list_context.add(add_element);
    
    // Item context
    item_context = new JPopupMenu();
    
    ActionListener delete_tag = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selected.getParent();
        Tag parent_tag = (Tag)parent.getUserObject();
        NBTType parent_type = parent_tag.getType();
        
        if (parent_type != NBTType.COMPOUND) {
          NBTList list;
          if (parent_type == NBTType.LIST) {
            list = (NBTList) parent_tag.getValue();
          } else {
            list = nbt_arrays.get(parent);
          }
          
          int item_index = parent.getIndex(selected);
          
          list.remove(item_index);
          
          for (int i = item_index + 1; i < parent.getChildCount(); i++) {
            SyncTag st = (SyncTag) ((DefaultMutableTreeNode)parent.getChildAt(i)).getUserObject();
            st.index -= 1;
          }
        } else {
          @SuppressWarnings("unchecked")
          Map<String, Object> parent_compound = (Map<String, Object>)parent_tag.getValue();
          MapSyncTag mst = (MapSyncTag) selected.getUserObject();
          parent_compound.remove(mst.getName());
        }
        
        selected.removeFromParent();
        ((DefaultTreeModel)NBTTree.this.model).reload(parent);
        
        selected = null;
      }
    };
    
    JMenuItem delete_item = new JMenuItem("Delete Tag");
    JMenuItem delete_list = new JMenuItem("Delete Tag");
    JMenuItem delete_comp = new JMenuItem("Delete Tag");
    
    delete_item.addActionListener(delete_tag);
    delete_list.addActionListener(delete_tag);
    delete_comp.addActionListener(delete_tag);
    
    item_context.add(delete_item);
    list_context.add(delete_list);
    compound_context.add(delete_comp);
  }

  /**
   * @param root A compound tag containing the NBT data to display in this tree.
   * 
   * Sets the NBT compound tag that should be displayed by this tree
   */
  public void setRoot(Tag root) {
    if (root == null) {
      root = new Tag("Root", NBTType.COMPOUND);
      root.setValue(new HashMap<String, Object>());
      return;
    } else if (!root.getType().equals(NBTType.COMPOUND)) {
      throw new IllegalArgumentException("Root must be a compound tag (or null)!");
    }

    if (this.root != null) {
      this.root.removeAllChildren();
    } else {
      this.root = new DefaultMutableTreeNode(root);
      model.setRoot(this.root);
    }
    createNodes(root, null);

    if (this.root.children().hasMoreElements()) {
      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) this.root.children().nextElement();
      this.setExpandedState(new TreePath(new TreeNode[] { this.root, tn }), true);
    }
  }

  private void createNodes(Tag data, DefaultMutableTreeNode parent) {
    DefaultMutableTreeNode node;
    if (parent == null) {
      node = root;
    } else {
      node = new DefaultMutableTreeNode(data);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> compound = (Map<String, Object>) data.getValue();

    ArrayList<String> keys = new ArrayList<>();
    keys.addAll(compound.keySet());
    Collections.sort(keys, (s1, s2) -> {
      return s1.compareToIgnoreCase(s2);
    });

    for (String key : keys) {
      Object value = compound.get(key);
      NBTType type = NBTType.typeOf(value);
      if (type == NBTType.COMPOUND) {
        createNodes(new MapSyncTag(key, value, compound), node);
      } else if (type == NBTType.LIST) {
        createListNodes(new MapSyncTag(key, value, compound), node);
      } else if (type.isMultiple()) {
        createArrayNodes(new MapSyncTag(key, value, compound), node);
      } else {
        node.add(new DefaultMutableTreeNode(new MapSyncTag(key, value, compound)));
      }
    }

    if (parent != null) {
      parent.add(node);
    }
  }

  @SuppressWarnings("unchecked")
  private <E extends Number> void createArrayNodes(Tag numbers, DefaultMutableTreeNode parent) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(numbers);
    NBTArrayList nbt_array = new NBTArrayList(numbers);
    nbt_arrays.put(node, nbt_array);
    int idx = 0;
    for (E num : (E[]) numbers.getValue()) {
      node.add(new DefaultMutableTreeNode(new SyncTag(null, num, nbt_array, idx++)));
    }
    parent.add(node);
  }

  private void createListNodes(Tag data, DefaultMutableTreeNode parent) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
    NBTList lst = (NBTList) data.getValue();
    int idx = 0;
    for (Object item : lst) {
      NBTType itemtype = NBTType.typeOf(item);
      if (itemtype == NBTType.COMPOUND) {
        createNodes(new SyncTag(null, item, lst, idx), node);
      } else if (itemtype == NBTType.LIST) {
        createListNodes(new SyncTag(null, item, lst, idx), node);
      } else {
        node.add(new DefaultMutableTreeNode(new SyncTag(null, item, lst, idx)));
      }
      idx++;
    }

    parent.add(node);
  }

  /**
   * @return The current data represented by this tree.
   */
  public Tag getRoot() {
    return (Tag) root.getUserObject();
  }
}
