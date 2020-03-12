public class Main {
    public static void main(String[] args) throws Exception{
        //zagon GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    logic_GUI.createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
