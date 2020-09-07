package main.java.edu.jhu.en605681.network;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    // define server
    private static final String SEVER_IP = "web7.jhuep.com";
    // define port
    private static final int SEVER_PORT = 20025;
    // static variable of type Client used to initialize singleton
    private static Client instance = null;
    // private constructor
    private Client() {}
    // static method to create instance of Client class
    public static Client getInstance() {
        if (instance == null)
            instance = new Client();
        return instance;
    }

    public Pair<String, Double> getPrice(int hikeId,
                         int startYear,
                         int startMonth,
                         int startDay,
                         int tripDuration) {
        // validate parameters

        // error handling
        // no internet
        // null string
        // invalid parameters
        // malformed string

        //format input data
        String inputData = inputData(hikeId, startYear, startMonth, startDay, tripDuration);
        // default error message
        String rawOutput = "-0.01:Error connecting to the server. Check your internet connection, server name, and port.";
        // try with resources
        try (
                // initialize socket and pass host and port parameters
                Socket socket = new Socket(SEVER_IP, SEVER_PORT);
                // initialize print writer to send data to the server
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                // initialize reader to get response from server
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // send data
            printWriter.println(inputData);
            // receive data (raw)
            rawOutput = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return data as a Pair object
        return outputData(rawOutput);
    }

    private String inputData(int hikeId,
                             int startYear,
                             int startMonth,
                             int startDay,
                             int tripDuration){
        // format input to match format expected by server
        return String.format("%d:%d:%d:%d:%d", hikeId, startYear,startMonth, startDay, tripDuration);
    }

    private Pair<String, Double> outputData(String rawOutput){
        // davide raw data into parts
        String[] parts = rawOutput.split(":");
        // store price in string
        String stringPrice = parts[0];
        // store message in string
        String message = parts[1];
        // convert string to Double
        Double price = Double.valueOf(stringPrice);
        // return data as pair
        return new Pair<>(message,price);
    }
}
