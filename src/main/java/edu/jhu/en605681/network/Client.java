package main.java.edu.jhu.en605681.network;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class that follow the singleton structure used to communicate with the server
 */
public class Client {
    // define server
    private static final String SEVER_IP = "web7.jhuep.com";
    // define port
    private static final int SEVER_PORT = 20025;
    // static variable of type Client used to initialize singleton
    private static Client instance = null;

    /**
     * Private constructor to avoid instantiation
     */
    private Client() {
    }

    /**
     * Method used to get single instance of Client class
     *
     * @return class instance
     */
    public static Client getInstance() {
        if (instance == null)
            instance = new Client();
        return instance;
    }

    /**
     * Method used to get the price from the server
     *
     * @param hikeId       the ID of the hike as integer
     * @param startYear    the year of the hike as integer
     * @param startMonth   the month of the hike as an integer
     * @param startDay     the day of the hike as an integer
     * @param tripDuration the duration of the trip as an integer
     * @return a pair object consisting of the price of the hike and a notification message
     */
    public Pair<String, Double> getPrice(int hikeId,
                                         int startYear,
                                         int startMonth,
                                         int startDay,
                                         int tripDuration) {

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

    /**
     * Method used to format data to match format expected by server
     *
     * @param hikeId       the ID of the hike as integer
     * @param startYear    the year of the hike as integer
     * @param startMonth   the month of the hike as an integer
     * @param startDay     the day of the hike as an integer
     * @param tripDuration the duration of the trip as an integer
     * @return formatted string hike_id:begin_year:begin_month:begin_day:duration (e.g: 1:2008:7:1:3)
     */
    private String inputData(int hikeId,
                             int startYear,
                             int startMonth,
                             int startDay,
                             int tripDuration) {
        // format input to match format expected by server
        return String.format("%d:%d:%d:%d:%d", hikeId, startYear, startMonth, startDay, tripDuration);
    }

    /**
     * method used to format the data received from the server
     *
     * @param rawOutput raw output from server
     * @return Key Pair: Value is the price of the rip, key is the message
     */
    private Pair<String, Double> outputData(String rawOutput) {
        // davide raw data into parts
        String[] parts = rawOutput.split(":");
        // store price in string
        String stringPrice = parts[0];
        // store message in string
        String message = "[Server] " + parts[1];
        // convert string to Double
        Double price = Double.valueOf(stringPrice);
        // print values
        System.out.println(message + ", Price: " + price);
        // return data as pair
        return new Pair<>(message, price);
    }
}
