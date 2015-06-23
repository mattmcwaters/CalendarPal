import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public class apptComfirmer extends JFrame {
    JButton change = new JButton("Edit");
    JPanel layout = new JPanel();
    JPanel info = new JPanel();

    JLabel locaLabel = new JLabel("Enter new Location");
    JTextField newLoca = new JTextField();
    JTextArea textArea = new JTextArea(50, 10);


    public apptComfirmer(final calendarConnector calendar, final int index) {
        super("No Address");

        textArea.setEditable(false);
        //JPanel[] panelArray = new JPanel[cC.numberOfMatchingItems];

        setSize(300, 300);
        setResizable(true);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        newLoca.setPreferredSize(new Dimension(80, 20));
        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));

        info.add(locaLabel);
        info.add(newLoca);



        layout.add(textArea);
        layout.add(info);
        layout.add(change);
        add(layout);
        textArea.setText(calendar.printOne(index));
        getContentPane().setBackground(Color.RED);


        change.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calendar.newLocation(index, newLoca.getText());
                apptComfirmer.this.dispatchEvent(new WindowEvent(apptComfirmer.this, WindowEvent.WINDOW_CLOSING));
            }
        });

    }
}
