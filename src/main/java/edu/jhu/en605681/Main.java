package main.java.edu.jhu.en605681;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javafx.util.Pair;
import main.java.edu.jhu.en605681.controller.Home;
import main.java.edu.jhu.en605681.network.Client;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
//        // test socket
//            Pair <String, Double>test = Client.getInstance().getPrice(2,2020,7,100,1);
//            System.out.println("Message: " + test.getKey() +"\nPrice: " + test.getValue());
        // Sett up new theme
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        // initialize frame and mount root panel
        JFrame frame = new JFrame("Hello");
        frame.setContentPane(new Home().topPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}