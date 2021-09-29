package lib.nbt.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import lib.nbt.NBTException;
import lib.nbt.Tag;
import lib.nbt.io.NBTReader;

public class TestNBTReader {
  public static void main(String[] args) {
    try {
      NBTReader reader = new NBTReader(new FileInputStream("resource/test.dat"), false);
      Tag root = reader.readNBT();
      NBTPrinter.print(root);
    } catch (NBTException exc) {
      exc.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
