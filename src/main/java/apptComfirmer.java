import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public class apptComfirmer extends JFrame {
    JButton change = new JButton("Edit");
    JButton cancel = new JButton("Discard event");
    JPanel layout = new JPanel();
    JPanel info = new JPanel();
    JPanel buttons = new JPanel();

    JLabel locaLabel = new JLabel();
    JTextField newLoca = new JTextField();
    JTextArea textArea = new JTextArea(50, 10);


    public apptComfirmer(final calendarConnector calendar, final int index) {
        super("No Address");

        textArea.setEditable(false);

        setSize(375, 300);
        setResizable(false);
        setVisible(true);


        newLoca.setPreferredSize(new Dimension(220, 20));
        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));

        info.add(locaLabel);
        info.add(newLoca);
        info.add(change);


        buttons.add(cancel);


        layout.add(textArea);
        layout.add(info);
        layout.add(buttons);

        change.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "DoClick");
        add(layout);
        textArea.setText(calendar.printOne(index));


        change.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!newLoca.getText().equals("")) {
                    calendar.newLocation(index, newLoca.getText());
                    apptComfirmer.this.dispatchEvent(new WindowEvent(apptComfirmer.this, WindowEvent.WINDOW_CLOSING));

                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calendar.removeEvent(index);
                apptComfirmer.this.dispatchEvent(new WindowEvent(apptComfirmer.this, WindowEvent.WINDOW_CLOSING));
            }
        });

    }


}
