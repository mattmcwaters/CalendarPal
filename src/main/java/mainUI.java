import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

public class mainUI extends JFrame {
    protected JButton b4, b3, comfirm;
    int closed = 0;
    JPanel layout = new JPanel();
    JPanel mode = new JPanel();
    JPanel buttons = new JPanel();
    JPanel info = new JPanel();
    JPanel infoTwo = new JPanel();
    JPanel infoThree = new JPanel();
    JPanel keywordPanel = new JPanel();
    JPanel checkboxPanel = new JPanel();
    JPanel browse = new JPanel();

    JLabel startAdd = new JLabel("Home Address");
    JTextField addField = new JTextField("119 Rockport Crescent");

    JLabel startLabel = new JLabel("Start Date");
    JLabel endLabel = new JLabel("End Date");

    JLabel keywordLabel = new JLabel("Keyword");

    JTextField startDate = new JTextField("June 1 2015");
    JTextField endDate = new JTextField("June 21 2015");
    JTextField keywordField = new JTextField("EG: 'Appointment'");
    JTextArea textArea = new JTextArea(50, 10);
    JScrollPane sp = new JScrollPane(textArea);

    JCheckBox ignoreEmpty = new JCheckBox("Ignore empty location");
    JCheckBox exportToExcel = new JCheckBox("Enable Excel Export");
    JRadioButton keyword = new JRadioButton("Keyword mode");
    JRadioButton standard = new JRadioButton("Standard mode");

    Border outline = BorderFactory.createLineBorder(Color.black);

    public fileChoser fileInput = new fileChoser();

    int windows= 0;
    int modeInput = 0;
    static calendarConnector cC;

    public mainUI() {

        super("Your Calendar Pal");
        keywordField.setPreferredSize(new Dimension(150, 20));
        addField.setPreferredSize(new Dimension(220, 20));
        fileInput.setPreferredSize(new Dimension(10, 10));
        endDate.setPreferredSize(new Dimension(100, 20));
        startDate.setPreferredSize(new Dimension(100, 20));
        startLabel.setBounds(10, 40, 80, 25);
        endLabel.setBounds(10, 40, 80, 25);
        startLabel.setBounds(10, 40, 80, 25);

        setSize(600, 800);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));
        b3 = new JButton("Calculate Distance");
        b4 = new JButton("Clear Status info");
        comfirm = new JButton("Comfirm appointments");

        standard.setSelected(true);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(keyword);
        buttonGroup.add(standard);


        mode.add(keyword);
        mode.add(standard);

        checkboxPanel.add(ignoreEmpty);
        checkboxPanel.add(exportToExcel);

        infoThree.add(startAdd);
        infoThree.add(addField);

        info.add(startLabel);
        info.add(startDate);

        infoTwo.add(endLabel);
        infoTwo.add(endDate);
        keywordPanel.add(keywordLabel);
        keywordPanel.add(keywordField);

        buttons.add(comfirm);
        buttons.add(b3);
        buttons.add(b4);

        b3.setVisible(false);


        layout.add(mode);
        layout.add(checkboxPanel);
        layout.add(fileInput);
        layout.add(keywordPanel);
        layout.add(infoThree);
        layout.add(info);
        layout.add(infoTwo);
        layout.add(buttons);

        layout.add(sp);
        add(layout);
        textArea.setEditable(false);
        keywordPanel.setVisible(false);
        fileInput.setVisible(false);




        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if((closed == windows)) {
                    b3.setVisible(false);
                    comfirm.setVisible(true);
                    cC.getDistance(addField.getText());
                }
                else if(windows == 0){
                    b3.setVisible(false);
                    comfirm.setVisible(true);
                }
                int errorCheck = cC.hasError("Google maps problem");
                if(errorCheck!= 0){
                    b3.setVisible(false);
                    comfirm.setVisible(true);
                    System.out.println("Encountered an error locating address for "
                            + cC.printOne(errorCheck-1));
                }
                if (exportToExcel.isSelected()) {
                    String directory = fileInput.chooser.getSelectedFile().toString();
                    directory += "\\" + startDate.getText();
                    directory += "-" + endDate.getText() + "[Mileage].xls";
                    excelConnector excelSheet = new excelConnector(directory, cC);
                    fileInput.filename.setText(directory);
                    System.out.println(cC.printOut());
                }
                else{
                    System.out.println(cC.printOut());
                }


            }
        });

        exportToExcel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(exportToExcel.isSelected()){
                    fileInput.setVisible(true);
                }
                else{
                    fileInput.setVisible(false);
                }
            }
        });

        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                System.out.print("Status Information: \n");
            }
        });

        comfirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cC = new calendarConnector(modeInput);
                cC.getAppts(keywordField.getText(), startDate.getText(), endDate.getText());
                cC.checkLocations();

                if(!ignoreEmpty.isSelected()){
                    for (int i = 0; i < cC.numberOfMatchingItems; i++) {
                        if (!cC.eventIsGood(i)) {
                                windows++;
                                apptComfirmer checker = new apptComfirmer(cC, i);
                                checker.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent arg0) {
                                        closed++;
                                    }
                                });

                        }
                    }
                    cC.emptyErrors();
                }


                b3.setVisible(true);
                comfirm.setVisible(false);


            }
        });

        standard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keywordPanel.setVisible(false);
                modeInput = 0;
            }
        });

        keyword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keywordPanel.setVisible(true);
                modeInput = 1;
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
    public static String cleanUp(String directory){
        String[] types = new String[] {".xls", ".xlt", ".xlm"};
        for (int i = 0; i < types.length; i++) {
            if(directory.endsWith(types[i])){
                return directory;
            }
        }
        return directory + ".xls";
    }
}
