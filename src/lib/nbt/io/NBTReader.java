package lib.nbt.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import lib.nbt.NBTException;
import lib.nbt.NBTType;
import lib.nbt.Tag;

/**
 * A class used for reading NBT data into
 * a {@link lib.nbt.Tag} instance.
 * 
 * @author Kyrillos Tawadros
 */
public class NBTReader {
  private DataInputStream source;
  
  /**
   * @param source The source of the input NBT data.
   * @param compressed <code>true</code> if the data is GZIPped
   *   (most or all of Minecraft's NBT data files are), <code>false</code>
   *   otherwise
   * @throws IOException If creating a {@link java.util.zip.GZIPInputStream}
   *   throws an {@link java.io.IOException}
   */
  public NBTReader(InputStream source, boolean compressed) throws IOException {
    if (source == null) {
      throw new IllegalArgumentException("Input stream must not be null!");
    }
    if (compressed) {
      source = new GZIPInputStream(source);
    }
    this.source = new DataInputStream(source);
  }
  
  /**
   * @param source The source of the input NBT data.
   * @throws IOException If creating a {@link java.util.zip.GZIPInputStream}
   *   throws an {@link java.io.IOException}
   *   
   * This constructor assumes <code>compressed=true</code>, so the internal
   * {@link java.io.DataInputStream} will be wrapped in a {@link java.util.zip.GZIPInputStream}
   */
  public NBTReader(InputStream source) throws IOException {
    this(source, true);
  }
  
  /**
   * @return The tag represented by the input NBT data.
   *   In virtually all cases, this should represent
   *   
   * @throws NBTException If the NBT data is invalid.
   * @throws IOException If {@link #source} throws an
   *   {@link java.io.IOException} on a read operation
   */
  public Tag readNBT() throws NBTException, IOException {
    try {
      int _tag_id = source.read();
      if (_tag_id == -1) {
        source.close();
        return null;
      } else if (_tag_id == NBTType.END.id) {
        return Tag.END;
      }
      byte tag_id = (byte)_tag_id;
      NBTType type = NBTType.getById(tag_id);
      
      String name = source.readUTF();
      return new Tag(name, type.readFrom(source));
    } catch (EOFException eofe) {
      throw new NBTException("Stream ended before tag completed");
    }
  }
}
