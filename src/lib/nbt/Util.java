package lib.nbt;

import java.util.List;

/**
 * A class miscellaneous utility methods.
 * 
 * @author Kyrillos Tawadros
 */
public class Util {
  
  /**
   * @param l The list to format
   * @return A neatly formatted list bound by brackets
   *   with comma-delimited values
   */
  public static final String formatList(List<?> l) {
    StringBuilder result = new StringBuilder();
    result.append("[");
    boolean remove = false;
    for (Object obj : l) {
      result.append(obj);
      result.append(',');
      remove = true;
    }
    if (remove) result.setLength(result.length() - 1); // hack to remove last comma
    result.append("]");
    return result.toString();
  }
}
