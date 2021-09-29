package lib.nbt.gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import lib.nbt.NBTType;
import lib.nbt.Tag;

/**
 * A TreeCellRenderer for rendering NBT tree nodes
 * 
 * @author Kyrillos Tawadros
 */
public class NBTTreeCellRenderer extends JLabel implements TreeCellRenderer, Cloneable {
  private static final long serialVersionUID = 1L;
  
  public static final BufferedImage ICON_PALETTE;
  static {
    BufferedImage _palette = null;
    try {
      _palette = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nbticons.png"));
    } catch (IOException ioe) {
      System.err.println("Warning: Could not load icon palette.");
      ioe.printStackTrace();
    }
    finally {
      ICON_PALETTE = _palette;
    }
  }
  
  private static final HashMap<NBTType, ImageIcon> ICONS = new HashMap<>();
  
  private static final Image getIconAt(int icoX, int icoY) {
    BufferedImage selection = ICON_PALETTE.getSubimage(icoX * 32, icoY * 32, 32, 32);
    return selection.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  }
  
  public static final ImageIcon getIconFor(NBTType t) {
    if (ICONS.containsKey(t)) {
      return ICONS.get(t);
    } else {
      Image base;
      switch(t) {
      case BYTE:
        base = getIconAt(0, 0);
        break;
      case SHORT:
        base = getIconAt(1, 1);
        break;
      case INT:
        base = getIconAt(3, 0);
        break;
      case LONG:
        base = getIconAt(0, 1);
        break;
      case FLOAT:
        base = getIconAt(2, 0);
        break;
      case DOUBLE:
        base = getIconAt(1, 0);
        break;
      case BYTE_ARRAY:
        base = getIconAt(0, 2);
        break;
      case STRING:
        base = getIconAt(2, 1);
        break;
      case LIST:
        base = getIconAt(2, 2);
        break;
      case COMPOUND:
        base = getIconAt(3, 1);
        break;
      case INT_ARRAY:
        base = getIconAt(1, 2);
        break;
      case LONG_ARRAY:
        base = getIconAt(0, 2);
        break;
      default:
        base = getIconAt(1, 3);
      }
      ImageIcon ret = new ImageIcon(base);
      ICONS.put(t, ret);
      return ret;
    }
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {
    DefaultMutableTreeNode treenode = (DefaultMutableTreeNode)value;
    
    Object obj = treenode.getUserObject();
    String text;
    ImageIcon ico;
    
    if (obj != null) {
      Tag tag = (Tag)obj;
      NBTType type = tag.getType();
      if (type != null) {
        if (type.isMultiple()) {
          text = tag.getName();
        } else {
          if (tag.getName() == null) {
            text = String.valueOf(tag.getValue());
          } else {
            text = String.format("%s: %s", tag.getName(), String.valueOf(tag.getValue()));
          }
        }
      } else {
        text = String.format("<invalid value> %s", String.valueOf(tag.getValue()));
      }
      
      ico = NBTTreeCellRenderer.getIconFor(tag.getType());
    } else {
      text = "";
      ico = null;
    }
    
    UIDefaults defaults = UIManager.getDefaults();
    if (selected) {
      setBackground(defaults.getColor("Tree.selectionBackground"));
      setForeground(defaults.getColor("Tree.selectionForeground"));
      setBorder(BorderFactory.createLineBorder(defaults.getColor("Tree.selectionBorderColor"), 1));
    } else {
      setBackground(defaults.getColor("Tree.textBackground"));
      setForeground(defaults.getColor("Tree.textForeground"));
      setBorder(null);
    }
    
    setText(text);
    setOpaque(true);
    setIcon(ico);
    setComponentOrientation(tree.getComponentOrientation());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    return this;
  }

}
