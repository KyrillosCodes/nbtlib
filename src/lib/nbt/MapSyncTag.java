package lib.nbt;

import java.util.Map;

/**
 * 
 * An extension of {@link lib.nbt.Tag} used internally by {@link lib.nbt.gui.NBTTree}
 * to provide a link between the tree model and the NBT data model.
 * 
 * Not intended for direct use.
 * 
 * @see lib.nbt.gui.NBTTree
 * @author Kyrillos Tawadros
 *
 */
public class MapSyncTag extends Tag {
  private Map<String, Object> ref;
  
  /**
   * @param name The name of the tag.
   * @param value The value stored in the tag.
   * @param ref A reference to the Map which contains
   *   the NBT tag denoted by this instance.
   *   
   * Creates a new MapSyncTag with the value initialized to <code>value</code>.
   * The type is inferred from the type of <code>value</code>.
   */
  public MapSyncTag(String name, Object value, Map<String, Object> ref) {
    super(name, value);
    this.ref = ref;
    if (!ref.containsKey(name)) {
      setValue(value);
    }
  }
  
  /**
   * 
   * @param name The name of the tag.
   * @param type The type of value stored in this tag.
   * @param ref A reference to the Map which contains
   *   the NBT tag denoted by this instance.
   *   
   * Creates a new MapSyncTag with the value initialized to the default
   * value associated with <code>type</code>.
   */
  public MapSyncTag(String name, NBTType type, Map<String, Object> ref) {
    this(name, type.defaultValue(), ref);
  }
  
  /**
   * @param value The value to set the tag to.
   * 
   * Sets the value of the tag, updating the
   * value in the NBT data model as well.
   */
  @Override
  public void setValue(Object value) {
    super.setValue(value);
    ref.put(name, value);
  }
  
  /**
   * @return A shallow copy of this instance.
   */
  @Override
  public MapSyncTag copy() {
    return new MapSyncTag(name, value, ref);
  }
}
