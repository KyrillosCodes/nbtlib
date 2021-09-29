package lib.nbt.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lib.nbt.NBTException;
import lib.nbt.Tag;
import lib.nbt.io.NBTReader;
import lib.nbt.io.NBTWriter;

public class TestNBTWriter {
  public static void main(String[] args) {
    try {
      System.out.println("Reading source data...");
      NBTReader reader = new NBTReader(new FileInputStream("resource/level.dat"));
      Tag root = reader.readNBT();
      System.out.println("Writing source data to new file...");
      FileOutputStream fos = new FileOutputStream("resource/level_copy.dat");
      NBTWriter writer = new NBTWriter(fos);
      writer.writeNBT(root);
      System.out.println("Reading data from new written file...");
      NBTReader reader2 = new NBTReader(new FileInputStream("resource/level_copy.dat"));
      NBTPrinter.print(reader2.readNBT());
    } catch (NBTException exc) {
      exc.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
