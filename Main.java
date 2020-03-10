package com.company.xml;
public class Main {
    public static void main(String[] args) throws Exception{
        //zagon GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    FileChooser.createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
