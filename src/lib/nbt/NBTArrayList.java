package lib.nbt;

import java.util.Collection;

/**
 * An extension of NBTList used to allow manipulation of the
 * arrays contained by Array-typed tags (i.e. Byte, Int, and Long arrays).
 * 
 * @see lib.nbt.NBTList
 * @author Kyrillos Tawadros
 */
public class NBTArrayList extends NBTList {
  
  private Tag ref;

  /**
   * @param ref The tag containing this list.
   */
  public NBTArrayList(Tag ref) {
    super(ref.getType().getElementType());
    this.ref = ref;
    for (Object el : (Object[])ref.getValue()) {
      internal.add(el);
    }
  }
  
  /**
   * 
   * @param <T> the element type of the array this list represents.
   * 
   * An internal method used to update the value of the tag whenever
   * the contents of the list are updated.
   * 
   */
  private <T> void onchange() {
    @SuppressWarnings("unchecked")
    T[] newarray = (T[]) java.lang.reflect.Array.newInstance(type.javaType, size());
    this.toArray(newarray);
    this.ref.setValue(newarray);
  }
  
  @Override
  public boolean add(Object item) {
    boolean result = super.add(item);
    onchange();
    return result;
  }
  
  @Override
  public boolean remove(Object o) {
    boolean result = super.remove(o);
    onchange();
    return result;
  }
  
  @Override
  public boolean addAll(Collection<? extends Object> c) {
    boolean result = super.addAll(c);
    onchange();
    return result;
  }
  
  @Override
  public boolean addAll(int index, Collection<? extends Object> c) {
    boolean result = super.addAll(index, c);
    onchange();
    return result;
  }
  
  @Override
  public boolean removeAll(Collection<? extends Object> c) {
    boolean result = super.removeAll(c);
    onchange();
    return result;
  }
  
  @Override
  public boolean retainAll(Collection<? extends Object> c) {
    boolean result = super.retainAll(c);
    onchange();
    return result;
  }
  
  @Override
  public void clear() {
    super.clear();
    onchange();
  }
  
  @Override
  public Object set(int index, Object value) {
    Object result = super.set(index, value);
    onchange();
    return result;
  }
  
  @Override
  public void add(int index, Object value) {
    super.add(index, value);
    onchange();
  }
  
  @Override
  public Object remove(int index) {
    Object result = super.remove(index);
    onchange();
    return result;
  }
}
