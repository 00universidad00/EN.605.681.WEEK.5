package main.java.edu.jhu.en605681;

import com.formdev.flatlaf.FlatIntelliJLaf;
import main.java.edu.jhu.en605681.controller.Home;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Sett up new theme
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        // initialize frame and mount root panel
        JFrame frame = new JFrame("Hello");
        frame.setContentPane(new Home().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}