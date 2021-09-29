package lib.nbt.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import lib.nbt.NBTType;
import lib.nbt.Tag;

/**
 * A class for writing data from a
 * {@link lib.nbt.Tag} to an output stream
 * 
 * @author Kyrillos Tawadros
 */
public class NBTWriter {
  private DataOutputStream dest;
  
  /**
   * @param dest The output stream to write to.
   * @param compressed <code>true</code> if the output
   *   should be GZIPped (it usually should be, especially
   *   if you are writing to files used by Minecraft)
   * @throws IOException If the creation of a {@link java.util.zip.GZIPOutputStream}
   *   throws an {@link java.io.IOException}
   */
  public NBTWriter(OutputStream dest, boolean compressed) throws IOException {
    if (compressed) {
      this.dest = new DataOutputStream(new GZIPOutputStream(dest));
    } else {
      this.dest = new DataOutputStream(dest);
    }
  }
  
  /**
   * @param dest The output stream to write to.
   * @throws IOException If the creation of a {@link java.util.zip.GZIPOutputStream}
   *   throws an {@link java.io.IOException}
   *   
   * This constructor assumes <code>compressed=true</code>,
   * as this should almost always be the case.
   */
  public NBTWriter(OutputStream dest) throws IOException {
    this(dest, true);
  }
  
  /**
   * @param root The root compound Tag to write.
   * @throws IOException If {@link #dest} throws an
   *   {@link java.io.IOException} on a write operation
   */
  public void writeNBT(Tag root) throws IOException {
    if (!root.getType().equals(NBTType.COMPOUND)) {
      throw new IllegalArgumentException("Root must be a compound tag!");
    }
    
    NBTType.COMPOUND.writeTo(root.getName(), root.getValue(), dest);
    dest.close();
  }
}
