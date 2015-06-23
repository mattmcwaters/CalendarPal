import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

public class mainUI extends JFrame {
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



    public mainUI(){
        super("Your Calendar Pal");
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

        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                calendarConnector cC = new calendarConnector();
                cC.getAppts("appointment", startDate.getText(), endDate.getText());
                System.out.println("Finished get appts");
                //cC.getDistance(addField.getText());
                //System.out.println(cC);

            }
        });

        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                System.out.print("Status Information: \n ");
            }
        });
    }


    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.\


        mainUI start = new mainUI();

        PrintStream printStream = new PrintStream(new customConsole(start.textArea));
        PrintStream standardOut = System.out;
        PrintStream standardErr = System.err;
        System.setOut(printStream);
        System.setErr(printStream);
        System.out.print("Status Information: \n ");

    }
}
