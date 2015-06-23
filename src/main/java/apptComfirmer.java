import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

public class apptComfirmer extends JFrame {
    protected JButton b4, b3;
    JPanel layout = new JPanel();
    JPanel buttons = new JPanel();
    JPanel info = new JPanel();
    JPanel infoTwo = new JPanel();
    JPanel infoThree = new JPanel();

    JLabel startAdd = new JLabel("Home Address");
    JTextField addField = new JTextField("119 Rockport Crescent");

    JLabel startLabel = new JLabel("Start Date");
    JLabel endLabel = new JLabel("End Date");

    JTextField startDate = new JTextField("June 1 2015");
    JTextField endDate = new JTextField("June 21 2015");

    JTextArea textArea = new JTextArea(50, 10);


    public apptComfirmer() {
        super("Appointment Comfirmer");
        setSize(600, 800);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));

        b3 = new JButton("Calculate Distance");
        b4 = new JButton("Clear Status info");

        infoThree.add(startAdd);
        infoThree.add(addField);
        info.add(startLabel);
        info.add(startDate);
        infoTwo.add(endLabel);
        infoTwo.add(endDate);

        buttons.add(b3);
        buttons.add(b4);

        layout.add(infoThree);
        layout.add(info);
        layout.add(infoTwo);
        layout.add(buttons);
        layout.add(textArea);
        add(layout);

    }
}
