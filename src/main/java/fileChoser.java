import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.*;


public class fileChoser extends JPanel
        implements ActionListener {
    JButton go;
    JFileChooser chooser = new JFileChooser();
    String choosertitle;
    JTextField filename = new JTextField();
    public fileChoser() {
        choosertitle = "Save As...";
        JLabel choose = new JLabel("Excel sheet Location: ");

        filename.setPreferredSize(new Dimension(300, 20));
        filename.setEditable(false);
        go = new JButton();
        go.setIcon(new ImageIcon(mainUI.class.getResource("folder.png")));

        go.setPreferredSize(new Dimension(20, 20));
        go.addActionListener(this);
        add(choose);

        add(filename);
        add(go);
    }

    public void actionPerformed(ActionEvent e) {
        int result;

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);

        //FileFilter filter = new FileNameExtensionFilter("Excel file", new String[] {"xls", "xlt", "xlm", "xlsx"});
        //chooser.setFileFilter(filter);
        //chooser.addChoosableFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);



        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String directory = chooser.getSelectedFile().toString();
            filename.setText(directory);
        }
        else {
            System.out.println("No Selection ");
        }
    }

    public Dimension getPreferredSize(){
        return new Dimension(40, 40);
    }


    public static void main(String s[]) {
    }
}
