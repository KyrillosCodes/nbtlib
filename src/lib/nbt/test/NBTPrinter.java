package lib.nbt.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import lib.nbt.NBTType;
import lib.nbt.Tag;
import lib.nbt.Util;

public class NBTPrinter {
  
  private static String repeat(char c, int times) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < times; i++) {
      result.append(c);
    }
    return result.toString();
  }
  
  @SuppressWarnings("unchecked")
  public static void print(Tag root, int preindent) {
    if (root == null) {
      throw new NullPointerException();
    } else if (!root.getType().equals(NBTType.COMPOUND)) {
      throw new IllegalArgumentException("Tag must be an NBTCompound tag, not " + root.getType().toString() + "!");
    }
    
    Map<String, Object> data = (Map<String, Object>)root.getValue();
    
    System.out.println(repeat(' ', preindent) + root.getName());
    for (String key : data.keySet()) {
      Object value = data.get(key);
      if (value instanceof Map) {
        print(new Tag(key, data.get(key)), preindent + 2); 
      } else {
        String valueOf;
        if (value.getClass().isArray()) {
          valueOf = Util.formatList(Arrays.asList((Object[])value));
        } else {
          valueOf = String.valueOf(value);
        }
        System.out.printf("%s%s = %s\n", repeat(' ', preindent + 2), key, valueOf);
      }
    }
  }
  
  public static void print(Tag root) {
    print(root, 0);
  }
}
