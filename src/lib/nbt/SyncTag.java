package lib.nbt;

/**
 * An extension of {@link lib.nbt.Tag} used internally by {@link lib.nbt.gui.NBTTree}
 * to provide a link between the tree model and the NBT data model, particularly
 * elements within NBTLists and NBTArrayLists.
 * 
 * Not intended for direct use.
 * 
 * @see lib.nbt.gui.NBTTree
 * @author Kyrillos Tawadros
 */
public class SyncTag extends Tag {
  
  private NBTList ref;
  public int index;

  /**
   * @param name The name of the tag.
   * @param value The value of this tag.
   * @param ref A reference to the {@link lib.nbt.NBTList}
   *   containing the value this instance represents.
   * @param index The index of this element within <code>ref</code>.
   */
  public SyncTag(String name, Object value, NBTList ref, int index) {
    super(name, value);
    this.ref = ref;
    this.index = index;
  }
  
  /**
   * @param value The value to set this tag to.
   * 
   * Sets the value of this tag, updating the value
   * in the list <code>ref</code> accordingly
   */
  @Override
  public void setValue(Object value) {
    super.setValue(value);
    ref.set(index, value);
  }
  
  /**
   * @return A shallow copy of this instance.
   */
  @Override
  public SyncTag copy() {
    return new SyncTag(name, value, ref, index);
  }

}
