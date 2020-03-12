import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultHighlighter;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class logic_GUI extends JPanel{
    static Double version = 0.1;
    static JFrame f = new JFrame("Nmap XML - CSV (v" + version+")");
    JFrame frame = new JFrame();
    JButton zazeni = new JButton("Poženi program");
    JTextArea log;
    String rootPath = System.getProperty("user.dir") + '\\';
    File nastavitve = new File(rootPath + "Set.properties");
    Properties prop = new Properties();
    public logic_GUI() throws Exception {
        super(new BorderLayout());
        log = new JTextArea(10, 55);
        try (InputStream input = new FileInputStream(nastavitve)) {
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            String str = "Kliknite na gumb poženi LE ENKRAT s isto xml datoteko, *če ne bodo večkrat vpisani enaki podatki!*\n";
            log.append(str);
            log.append("Podatki se bodo shranili v: " + prop.getProperty("CSV") + "\n");
            log.append("Vsi porti s podrobnimi podatki so shranjeni v datoteki izpis.csv, kjer lahko tudi spremenite poti datotek.\n");
            log.append("Če datoteka izpis.csv že obstaja ji bodo podatki le pripisani.\n");
            log.getHighlighter().addHighlight(0, str.length(), DefaultHighlighter.DefaultPainter);
            log.setBorder(new EmptyBorder(5, 5, 0, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
        //System.out.println("Nastavitve: "+ nastavitve);
        String rootPath = System.getProperty("user.dir");
        File nastavitve = new File(rootPath + "\\" + "Set.properties");
        //System.out.println("Delovna pot: "+ rootPath);
        if (!(nastavitve.exists())) {
            JOptionPane.showMessageDialog(frame, "Set.properties ne obstaja, program ga bo ustvaril s privzetimi nastavitvami!", "Opozorilo", JOptionPane.INFORMATION_MESSAGE);
            ustvariNastavitve(frame, nastavitve, rootPath);
        }
        Properties prop = new Properties();
        prop.load(new FileInputStream(nastavitve));
        String XMLdatoteka = prop.getProperty("nmap");
        //System.out.println("Nmap: " + XMLdatoteka);
        File CSV = new File(prop.getProperty("CSV"));
        if(!(CSV.isFile())) {
            ustvariNastavitve(frame, nastavitve, rootPath);
            CSV.createNewFile();
            /*
            String msg = "Izbrana lokacija za csv datoteko zapisana v Set.properties,\n" + CSV + " ne obstaja več.\nIzbrišem Set.properties ter jih nastavim na privzete nastavitve?";
            int chk = JOptionPane.showConfirmDialog(frame, msg, "Lokacija ne obstaja več", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (chk == JOptionPane.YES_OPTION) {
                ustvariNastavitve(frame, nastavitve, rootPath);
                CSV.createNewFile();
            } else {
                frame.dispose();
                f.dispose();
                System.exit(-1);
            }
            */
        }
        //System.out.println("pot izpisa: " + CSV);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try{
            doc = factory.newDocumentBuilder().parse(XMLdatoteka);
            doc.getDocumentElement().normalize();
        }catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Datoteka nmap.xml ni najdena na predvideni lokaciji! Lokacijo xml datoteke lahko spremenite\nv nastavitvah programa Set.properties(v lokacijski poti mora biti uporabljen dvojni backslash \\\\).\nProgram se bo zaprl!", "Napaka!", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            f.dispose();
            System.exit(-1);
        }
        NodeList nListAddr = doc.getElementsByTagName("address");
        NodeList nListPort = doc.getElementsByTagName("port");
        NodeList nListState = doc.getElementsByTagName("state");
        NodeList nListServ = doc.getElementsByTagName("service");

        int counter = nListPort.getLength();
        String[][] output = new String[6][counter];

        //System.out.println("Št. portov: " + counter);

        Node nNodeAddr = nListAddr.item(0);//odstani mac naslove

        for (int i = 0; i < counter; i++) {
            Node nNodePort = nListPort.item(i);
            Node nNodeState = nListState.item(i);
            Node nNodeServ = nListServ.item(i);
            Element eElementAddr = (Element) nNodeAddr;
            Element eElementPort = (Element) nNodePort;
            Element eElementState = (Element) nNodeState;
            Element eElementServ = (Element) nNodeServ;
            //if (nNodePort.getNodeType() == Node.ELEMENT_NODE && nNodeState.getNodeType() == Node.ELEMENT_NODE && nNodeAddr.getNodeType() == Node.ELEMENT_NODE && nNodeServ.getNodeType() == Node.ELEMENT_NODE) {
            output[0][i] = eElementPort.getAttribute("portid");
            output[1][i] = eElementPort.getAttribute("protocol");
            output[2][i] = eElementState.getAttribute("state");
            output[3][i] = eElementAddr.getAttribute("addr");
            output[4][i] = eElementAddr.getAttribute("addrtype");
            if (eElementServ.getAttribute("product").isEmpty()) {
                output[5][i] = "?";
            } else {
                output[5][i] = eElementServ.getAttribute("product");
                //  }
            }
        }
        frame.add(zazeni);
        String[][] data = vnos(CSV);
        String[] column = {"Port", "Najdenih"};
        final JTable table = new JTable(data, column);
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(10);
        table.setPreferredScrollableViewportSize(new Dimension(350, 193));
        table.setFillsViewportHeight(true);
        table.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPaneTab = new JScrollPane(table);
        add(scrollPaneTab, BorderLayout.WEST);
        table.validate();
        frame.dispose();
        add(zazeni, BorderLayout.SOUTH);
        add(logScrollPane, BorderLayout.CENTER);
        File finalCSV = CSV;
        zazeni.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Sestavi_CSV(counter, finalCSV, output);
                    frame.dispose();
                    f.dispose();
                    createAndShowGUI();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void createAndShowGUI() throws Exception {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new logic_GUI());
        logic_GUI newContentPane = new logic_GUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        f.setContentPane(newContentPane);
        f.pack();
        f.setVisible(true);
    }

    public static String[][] vnos(File CSV) throws Exception {
        /*
        FileReader r = new FileReader(CSV);
        BufferedReader br = new BufferedReader(r);
        int counter = 0;
        while(!(br.readLine() == null){
            counter++;
        }
        br.close();
        r.close();
        */
        int neznani = 0;
        int st_portov = 2;

        String[][] data = new String[st_portov][2];
        data[0][0] = "x";
        data[1][0] = "Neznani porti";
        for(int i = 0; i<st_portov;i++){
            data[i][1] = "0";
        }
        Scanner s = new Scanner(CSV);
        //System.out.println(counter);
        while(s.hasNext()){
            String[] temp = s.nextLine().split(";", 0);
            if(temp[5].equals(" ?"))neznani++;
            //System.out.println(temp[0]);
            switch(temp[0]){
                case "x":
                    data[0][1] = String.valueOf(Integer.parseInt(data[0][1])+1);
                    break;
                case "ip":
                    break;
            }
            data[8][1] = String.valueOf(neznani);
        }
        s.close();
        return data;
    }
    public static void Sestavi_CSV(int counter, File CSV, String[][] tab) throws Exception {
        FileWriter w = new FileWriter(CSV, true);
        String query = new String();
        try{
            if (CSV.length() == 0) {
                query = "Port id; Protokol; Status; IP; IP tip; Storitev";
                w.write(query);
                w.flush();
                query = "";
            }
            for (int i = 0; i < counter; i++) {
                for(int j = 0; j<6;j++)
                    if(!(j == 6)) {
                        query += tab[j][i] + "; ";
                    }else{
                        query += tab[j][i];
                    }
                //System.out.println(query);
                w.write(System.lineSeparator());
                w.append(query);
                w.flush();
                query = "";
            }
            w.flush();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void ustvariNastavitve(JFrame frame, File nastavitve, String rootPath) throws Exception {
        FileWriter fw = new FileWriter(nastavitve);
        nastavitve.createNewFile();
        rootPath = rootPath.replace("\\", "\\\\");
        fw.write("----\n"+
                "#To so nastavitve programa NmapXMLvCSV, ki jih lahko spreminjamo tudi ročno\n"+
                "nmap = "+ rootPath +"\\\\nmap.xml\n"+
                "CSV = "+ rootPath +"\\\\izpis.csv\n"+
                "#Avtor = Tomažin Peter\n" +
                "----");
        fw.flush();
        fw.close();
    }
}
