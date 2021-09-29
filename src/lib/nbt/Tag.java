package lib.nbt;

/**
 * A class representing a single NBT Tag.
 * 
 * @author Kyrillos Tawadros
 */
public class Tag {
  
  public static final Tag END = new Tag("", NBTType.END) {
    @Override
    public void setValue(Object value) {}
  };
  
  /**
   * The name of this tag.
   */
  protected String name;
  
  /**
   * The value of this tag.
   */
  protected Object value;
  
  /**
   * The type of this tag.
   */
  protected NBTType type;
  
  /**
   * 
   * @param name The name of this tag.
   * @param value The value of this tag.
   * @throws IllegalArgumentException if <code>value</code>
   *   does not match any known NBT type.
   *   
   * This constructor infers <code>type</code> via {@link lib.nbt.NBTType#typeOf(Object)}
   * 
   * @see lib.nbt.NBTType#typeOf(Object)
   */
  public Tag(String name, Object value) throws IllegalArgumentException {
    this.name = name;
    this.type = NBTType.typeOf(value);
    if (this.type == null) {
      throw new IllegalArgumentException(String.format("Could not find NBT type for value '%s'.", String.valueOf(value)));
    }
    
    this.value = value;
  }
  
  /**
   * @param name The name of this tag.
   * @param type The type of this tag.
   * 
   * This constructor initializes <code>value</code> to the default
   * value of <code>type</code> via {@link lib.nbt.NBTType#defaultValue()}
   * 
   * @see lib.nbt.NBTType#defaultValue()
   */
  public Tag(String name, NBTType type) {
    this.name = name;
    this.type = type;
    this.value = type.defaultValue();
  }
  
  /**
   * @return The name of this tag.
   */
  public String getName() {
    return name;
  }
  
  /**
   * @param name The name to set this tag to.
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * @return The value of this tag.
   */
  public Object getValue() {
    return value;
  }
  
  /**
   * @param newValue The new value to set this tag to.
   * @throws IllegalArgumentException If <code>newValue</code>
   *   not a valid instance of {@link lib.nbt.Tag#type}
   */
  public void setValue(Object newValue) {
    if (type.isValidValue(newValue)) {
      this.value = newValue;
    } else {
      throw new IllegalArgumentException(String.format("Invalid value '%s' for NBT type %s", String.valueOf(newValue), type.toString()));
    }
  }
  
  /**
   * @return The type of this tag.
   */
  public NBTType getType() {
    return type;
  }
  
  /**
   * @return A readable String representation of this tag.
   * @see lib.nbt.NBTType#toString()
   */
  @Override
  public String toString() {
    return String.format("Tag<%s> = %s", type.toString(), String.valueOf(value));
  }
  
  /**
   * @return A shallow copy of this tag.
   */
  public Tag copy() {
    return new Tag(this.name, this.value);
  }
}
