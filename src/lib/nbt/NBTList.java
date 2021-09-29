package lib.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A class that implements {@link java.util.List}
 * representing an NBT list. The list is typed,
 * but not using Java generics - typing is handled
 * internally and manually.
 * 
 * This class wraps an instance of {@link java.util.ArrayList},
 * the main functional difference being that all values that
 * are added are validated before being added. If there is an
 * invalid value, an exception is thrown and the value(s) are
 * not added.
 * 
 * @see lib.nbt.NBTType
 * @see java.util.List
 * 
 * @author Kyrillos Tawadros
 */
public class NBTList implements List<Object> {
  protected List<Object> internal;
  public final NBTType type;
  
  /**
   * @param type The type of value stored in the list.
   * 
   * Initializes an empty list with the specified <code>type</code>.
   */
  public NBTList(NBTType type) {
    this.type = type;
    this.internal = new ArrayList<>();
  }
  
  /**
   * @param value The value to validate.
   * @throws IllegalArgumentException If the value is not valid.
   * 
   * Checks if <code>value</code> is valid. Throws an exception if
   * it is not, does nothing if it is.
   */
  private void validate(Object value) {
    if (!type.isValidValue(value)) {
      throw new IllegalArgumentException(String.format("Invalid value '%s' for NBT type %s", String.valueOf(value),
          type.toString()));
    }
  }

  @Override
  public int size() {
    return internal.size();
  }

  @Override
  public boolean isEmpty() {
    return internal.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return internal.contains(o);
  }

  @Override
  public Iterator<Object> iterator() {
    return internal.iterator();
  }

  @Override
  public Object[] toArray() {
    return internal.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return internal.toArray(a);
  }

  @Override
  public boolean add(Object e) {
    validate(e);
    return internal.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return internal.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return internal.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Object> c) {
    boolean changed = false;
    for (Object obj : c) {
      changed |= add(obj);
    }
    return changed;
  }

  @Override
  public boolean addAll(int index, Collection<? extends Object> c) {
    for (Object obj : c) {
      add(index, obj);
    }
    return c.size() > 0;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return internal.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return internal.retainAll(c);
  }

  @Override
  public void clear() {
    internal.clear();
  }

  @Override
  public Object get(int index) {
    return internal.get(index);
  }

  @Override
  public Object set(int index, Object element) {
    validate(element);
    return internal.set(index, element);
  }

  @Override
  public void add(int index, Object element) {
    validate(element);
    internal.add(index, element);
  }

  @Override
  public Object remove(int index) {
    return internal.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return internal.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return internal.lastIndexOf(o);
  }

  @Override
  public ListIterator<Object> listIterator() {
    return internal.listIterator();
  }

  @Override
  public ListIterator<Object> listIterator(int index) {
    return internal.listIterator(index);
  }

  @Override
  public List<Object> subList(int fromIndex, int toIndex) {
    return internal.subList(fromIndex, toIndex);
  }

  @Override
  public String toString() {
    return Util.formatList(this);
  }
}
