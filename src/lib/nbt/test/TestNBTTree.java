package lib.nbt.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import lib.nbt.NBTException;
import lib.nbt.Tag;
import lib.nbt.gui.NBTTree;
import lib.nbt.io.NBTReader;
import lib.nbt.io.NBTWriter;

public class TestNBTTree extends JFrame {
  
  private NBTTree tree;
  
  public TestNBTTree() {
    super("NBT Tree Test");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 500);
    
    JMenuBar mb = new JMenuBar();
    JMenu tools = new JMenu("Tools");
    JMenuItem printer = new JMenuItem("Print NBT Data");
    JMenuItem saver = new JMenuItem("Save to Test File");
    
    printer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        NBTPrinter.print(tree.getRoot());
      }
    });
    
    saver.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          NBTWriter writer = new NBTWriter(new FileOutputStream("resource/test.dat"), false);
          writer.writeNBT(tree.getRoot());
          JOptionPane.showMessageDialog(TestNBTTree.this, "Saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ioe) {
          JOptionPane.showMessageDialog(TestNBTTree.this, "Could not write to test.dat", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    tools.add(printer);
    tools.add(saver);
    
    mb.add(tools);
    setJMenuBar(mb);
    
    setupTree();
    
    setLayout(new BorderLayout());
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }
  
  private void setupTree() {
    try {
      NBTReader reader = new NBTReader(new FileInputStream("resource/level.dat"));
      Tag root = reader.readNBT();
      tree = new NBTTree(root);
      tree.setEditable(true);
    } catch (NBTException | IOException e) {
      System.err.println("WARNING: Could not load source NBT data. Stack trace below.");
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  public static void main(String[] args) {
    TestNBTTree test = new TestNBTTree();
    test.setVisible(true);
  }
}
