package lib.nbt;

/**
 * An exception fired indicating invalid NBT data.
 * 
 * @author Kyrillos Tawadros
 */
public class NBTException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * @param msg The exception message.
   */
  public NBTException(String msg) {
    super(msg);
  }
}
