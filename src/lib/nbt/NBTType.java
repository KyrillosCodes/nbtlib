package lib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration representing all types supported
 * by the NBT format. Each constant contains information
 * necessary to read tags of its type from a file, write
 * them to a file, and provide a default "empty" value for
 * its type.
 * 
 * @author Kyrillos Tawadros
 */
public enum NBTType {
  END((byte)0, Void.class), BYTE((byte)1, Byte.class), SHORT((byte)2, Short.class),
  INT((byte)3, Integer.class), LONG((byte)4, Long.class), FLOAT((byte)5, Float.class),
  DOUBLE((byte)6, Double.class), BYTE_ARRAY((byte)7, Byte[].class), STRING((byte)8, String.class),
  LIST((byte)9, NBTList.class), COMPOUND((byte)10, Map.class), INT_ARRAY((byte)11, Integer[].class),
  LONG_ARRAY((byte)12, Long[].class);
  
  /**
   * The byte value that represents this tag in the NBT file format.
   */
  public final byte id;
  
  /**
   * The Java type that this NBT type represents.
   */
  public final Class<?> javaType;
  
  private NBTType(byte id, Class<?> javaType) {
    this.id = id;
    this.javaType = javaType;
  }
  
  /**
   * @param value The value to validate.
   * @return <code>true</code> if the value is a valid
   *   object of this type, <code>false</code> otherwise
   *   
   * Checks if <code>value</code> is a valid value of this
   * NBT type.
   */
  public boolean isValidValue(Object value) {
    if (javaType == Void.class) {
      // single exceptional case
      return value == null;
    } else {
      return javaType.isInstance(value);
    }
  }
  
  /**
   * @return <code>true</code> if this NBT type is a multiple
   * type, <code>false</code> otherwise
   * 
   * A multiple type is a type which can contain multiple values
   * in it - compounds, lists, and arrays are multiple. Individual
   * values (all other types) are not multiple.
   */
  public boolean isMultiple() {
    switch(this) {
    case COMPOUND:
    case LIST:
    case BYTE_ARRAY:
    case INT_ARRAY:
    case LONG_ARRAY:
      return true;
    default:
      return false;
    }
  }
  
  /**
   * @return The type of element contained by this array type,
   * <code>null</code> if this type is not an array type
   */
  public NBTType getElementType() {
    switch(this) {
    case BYTE_ARRAY:
      return BYTE;
    case INT_ARRAY:
      return INT;
    case LONG_ARRAY:
      return LONG;
    default:
      return null;
    }
  }
  
  /**
   * @param str The value to parse.
   * @return An object of the appropriate
   *   Java type represented by <code>str</code>.
   *   
   * Parses a String into a value of this type.
   * For example, <code>NBTType.FLOAT.fromString("1.23")</code>
   * is <code>1.23f</code>
   */
  public Object fromString(String str) {
    if (isMultiple()) {
      return null;
    } else {
      switch(this) {
      case BYTE:
        return Byte.parseByte(str);
      case SHORT:
        return Short.parseShort(str);
      case INT:
        return Integer.parseInt(str);
      case LONG:
        return Long.parseLong(str);
      case FLOAT:
        return Float.parseFloat(str);
      case DOUBLE:
        return Double.parseDouble(str);
      case STRING:
        return str;
      default:
        return null;
      }
    }
  }
  
  /**
   * @param source The {@link java.io.DataInputStream} to read from
   * @return An object of this type read from <code>source</code>
   * @throws IOException If <code>source</code> throws an
   *   {@link java.io.IOException} on the read operation
   *   
   * Reads an object of this type from <code>source</code>.
   */
  public Object readFrom(DataInputStream source) throws IOException {
    int length = -1;
    switch(this) {
    case END:
      return null;
    case BYTE:
      return source.readByte();
    case SHORT:
      return source.readShort();
    case INT:
      return source.readInt();
    case LONG:
      return source.readLong();
    case FLOAT:
      return source.readFloat();
    case DOUBLE:
      return source.readDouble();
    case BYTE_ARRAY:
      length = source.readInt();
      Byte[] arr = new Byte[length];
      for(int i = 0; i < length; i++) {
        arr[i] = source.readByte();
      }
      return arr;
    case STRING:
      return source.readUTF();
    case LIST:
      byte tag_id = source.readByte();
      NBTType type = NBTType.getById(tag_id);
      length = source.readInt();
      NBTList list = new NBTList(type);
      for (int i = 0; i < length; i++) {
        Object result = type.readFrom(source);
        list.add(result);
      }
      return list;
    case COMPOUND:
      Map<String, Object> compound = new HashMap<String, Object>();
      byte next_tag_id;
      while((next_tag_id = source.readByte()) != NBTType.END.id) {
        String name = source.readUTF();
        NBTType next_type = NBTType.getById(next_tag_id);
        Object content = next_type.readFrom(source);
        compound.put(name, content);
      }
      return compound;
    case INT_ARRAY:
      length = source.readInt();
      Integer[] int_out = new Integer[length];
      for (int i = 0; i < length; i++) {
        int_out[i] = source.readInt();
      }
      return int_out;
    case LONG_ARRAY:
      length = source.readInt();
      Long[] long_out = new Long[length];
      for (int i = 0; i < length; i++) {
        long_out[i] = source.readLong();
      }
      return long_out;
    default:
      return null;
    }
  }
  
  /**
   * @param name The name of the tag to write.
   * @param obj The value of the tag. Expected to be of
   *   the appropriate type for this <code>NBTType</code>.
   * @param dest The {@link java.io.DataOutputStream} to
   *   write to.
   * @throws IOException if <code>dest</code> throws an
   *   {@link java.io.IOException} on the write operation.
   *   
   * @see #writePayload(Object, DataOutputStream)
   * 
   * Writes a full tag of this type to <code>dest</code>,
   * including the tag ID, the name, and the payload.
   */
  public void writeTo(String name, Object obj, DataOutputStream dest) throws IOException {
    dest.writeByte(this.id);
    dest.writeUTF(name);
    writePayload(obj, dest);
  }
  
  /**
   * @param obj The value to write.
   * @param dest The {@link java.io.DataOutputStream} to write to.
   * @throws IOException if <code>dest</code> throws an
   * {@link java.io.IOException} on the write operation.
   * 
   * Writes a payload of this type to <code>dest</code>.
   * Does not write a full tag, only a value.
   */
  public void writePayload(Object obj, DataOutputStream dest) throws IOException {
    switch(this) {
    case COMPOUND:
      @SuppressWarnings("unchecked")
      Map<String, ?> items = (Map<String, ?>)obj;
      for (String key : items.keySet()) {
        Object item = items.get(key);
        NBTType type = NBTType.typeOf(item);
        if (type == null) {
          throw new IllegalArgumentException("Invalid object " + String.valueOf(item));
        }
        type.writeTo(key, item, dest);
      }
      dest.writeByte(END.id);
      break;
    case END:
      break;
    case BYTE:
      dest.writeByte((Byte)obj);
      break;
    case SHORT:
      dest.writeShort((short)obj);
      break;
    case INT:
      dest.writeInt((int)obj);
      break;
    case LONG:
      dest.writeLong((long)obj);
      break;
    case FLOAT:
      dest.writeFloat((float)obj);
      break;
    case DOUBLE:
      dest.writeDouble((double)obj);
      break;
    case BYTE_ARRAY:
      Byte[] bytes = (Byte[])obj;
      dest.writeInt(bytes.length);
      for (Byte b : bytes) {
        dest.writeByte(b);
      }
      break;
    case STRING:
      dest.writeUTF((String)obj);
      break;
    case LIST:
      if (!(obj instanceof NBTList)) {
        throw new IllegalArgumentException("List payloads must be instances of NBTList.");
      }
      NBTList lst = (NBTList) obj;
      if (lst.size() == 0) {
        // assume empty lists are of type byte
        dest.writeByte(BYTE.id);
        dest.writeInt(0);
      } else {
        NBTType type = lst.type;
        dest.writeByte(type.id);
        dest.writeInt(lst.size());
        for (Object item : lst) {
          type.writePayload(item, dest);
        }
      }
      break;
    case INT_ARRAY:
      Integer[] numbers = (Integer[])obj;
      dest.writeInt(numbers.length);
      for (int i : numbers) {
        dest.writeInt(i);
      }
      break;
    case LONG_ARRAY:
      Long[] longs = (Long[])obj;
      dest.writeInt(longs.length);
      for(long l : longs) {
        dest.writeLong(l);
      }
      break;
    }
  }
  
  // Static methods
  
  /**
   * 
   * @param value the value to determine the type of
   * @return The NBTType that appropriate defines <code>value</code>
   * 
   * Gets the <code>NBTType</code> that appropriately defines <code>value</code>.
   * 
   * For example, <code>NBTType.typeOf(1.0f)</code> is <code>NBTType.FLOAT</code>
   */
  public static NBTType typeOf(Object value) {
    if (value == null) {
      return END;
    }
    
    for (NBTType t : NBTType.values()) {
      if (t.javaType.isInstance(value)) {
        return t;
      }
    }
    
    return null;
  }
  
  /**
   * @param id The tag ID to get the type of.
   * @return The type represented by <code>id</code>
   * 
   * Returns the type represented by <code>id</code>
   * according to the NBT file format.
   */
  public static NBTType getById(byte id) {
    for (NBTType t : NBTType.values()) {
      if (t.id == id) {
        return t;
      }
    }
    
    return null;
  }
  
  /**
   * @return The enumeration's constant name.
   */
  public String enumName() {
    return super.toString();
  }
  
  /**
   * @return An empty or initial value for this type.
   */
  public Object defaultValue() {
    switch(this) {
    case BYTE:
      return (byte)0;
    case SHORT:
      return (short)0;
    case INT:
      return 0;
    case LONG:
      return 0L;
    case FLOAT:
      return 0.0f;
    case DOUBLE:
      return 0.0;
    case BYTE_ARRAY:
      return new Byte[0];
    case STRING:
      return "";
    case LIST:
      return new NBTList(BYTE);
    case COMPOUND:
      return new HashMap<String, Object>();
    case INT_ARRAY:
      return new Integer[0];
    case LONG_ARRAY:
      return new Long[0];
    default:
      return null;
    }
  }
  
  /**
   * @return an easily readable identifier representing this type.
   */
  @Override
  public String toString() {
    String orig_name = super.toString();
    String mod_name = orig_name.charAt(0) + orig_name.substring(1).toLowerCase().replaceAll("_", "");
    return String.format("NBT%s", mod_name);
  }
  
  /**
   * @return A String appropriate for presenting to users
   *   selecting a type.
   */
  public String toDisplayString() {
    String orig_name = super.toString();
    String[] parts = orig_name.split("_");
    String[] newparts = new String[parts.length];
    int idx = 0;
    for (String part : parts) {
      newparts[idx++] = part.charAt(0) + part.substring(1).toLowerCase();
    }
    return String.join(" ", newparts);
  }
  
  /**
   * @return an array populated with the display strings of
   *   every NBTType, except <code>NBTType.END</code>
   */
  public static String[] choices() {
    String[] ret = new String[NBTType.values().length - 1];
    int next_idx = 0;
    for (int i = 0; i < NBTType.values().length; i++) {
      if (NBTType.values()[i] == END) continue;
      ret[next_idx++] = NBTType.values()[i].toDisplayString();
    }
    return ret;
  }
  
  /**
   * @param choice The display string to parse.
   * @return The NBTType whose display string is <code>choice</code>
   */
  public static NBTType fromDisplayString(String choice) {
    return NBTType.valueOf(choice.replaceAll(" ", "_").toUpperCase());
  }
}
