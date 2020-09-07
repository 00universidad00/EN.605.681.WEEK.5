package main.java.edu.jhu.en605681.controller;

import javafx.util.Pair;
import main.java.edu.jhu.en605681.model.Hike;
import main.java.edu.jhu.en605681.network.Client;
import main.java.edu.jhu.en605681.utils.BookingDay;
import main.java.edu.jhu.en605681.utils.Rates;
import org.jdatepicker.DateLabelFormatter;
import org.jdatepicker.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Eleazar Miranda
 */
public class Home {
    // class members
    public JScrollPane topPanel;
    private JPanel rootPanel;
    private JComboBox<Hike> destination;
    private JComboBox<Integer> days;
    private JButton calculatePrice;
    private JLabel weekDaysLabel;
    private JLabel weekRateLabel;
    private JLabel weekPriceLabel;
    private JLabel weekendDaysLabel;
    private JLabel weekendRateLabel;
    private JLabel weekendFeeLabel;
    private JLabel weekendPriceLabel;
    private JLabel totalPriceLabel;
    private JPanel datePickerContainer;
    private JLabel icon;
    private JDatePickerImpl datePicker;

    /**
     * Public constructor
     */
    public Home() {
        // initialize ui components
        initUiComponents();
        // register listeners
        registerListeners();
    }

    /**
     * Method used to set up the initial models and data for the UI
     */
    private void initUiComponents() {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/edu/jhu/en605681/assets/logo.png"));
        int width = 60;
        int height = 60;
        Image scaled = scaleImage(originalIcon.getImage(), width, height);
        ImageIcon scaledIcon = new ImageIcon(scaled);
        icon.setIcon(scaledIcon);

        // create date picker and bind it to the GUI JPanel
        UtilDateModel model = new UtilDateModel();
        model.setSelected(true);
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePickerContainer.setLayout(new BoxLayout(datePickerContainer, BoxLayout.PAGE_AXIS));
        datePickerContainer.add(datePicker);

        // Populate destination combo box
        this.destination.setModel(new DefaultComboBoxModel<>(Hike.values()));
        this.destination.setRenderer(new MyObjectListCellRenderer());
        // get selected hike
        Hike selectedHike = (Hike) destination.getSelectedItem();
        // set initial model for days combo box
        days.setModel(new DefaultComboBoxModel<>(Objects.requireNonNull(selectedHike).length()));
    }

    /**
     * Method used to register elements that accept actions form users
     */
    private void registerListeners() {
        destination.addActionListener(e -> {
            // update days combo box each time destination changes
            Hike selectedHike1 = (Hike) destination.getSelectedItem();
            days.setModel(new DefaultComboBoxModel<>(Objects.requireNonNull(selectedHike1).length()));
        });
        calculatePrice.addActionListener(e -> calculatePrice());
    }

    /**
     * Method used to calculate the price of the tours
     */
    private void calculatePrice() {
        // Get selected date
        Date selectedDate = (Date) datePicker.getModel().getValue();
        // Check if selected date is valid
        if (selectedDate.before(Date.from(new Date().toInstant().truncatedTo(ChronoUnit.DAYS)))) {
            // clear values in ui
            clearLabels();
            // notify the user about incorrect date
            displayErrorPopup("You selected a date in the past. \nPlease select a valid date and try again.");
        } else {
            // get selected hike
            Hike selectedHike = (Hike) destination.getSelectedItem();
            // get hike id
            int hikeId = Objects.requireNonNull(selectedHike).id();
            // get date information
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(selectedDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // get selected tour length
            int selectedLength = (Integer) (Objects.requireNonNull(days.getSelectedItem()));
            // call client instance and pass parameters, save results in Pair object
            Pair<String, Double> serverResponse = Client.getInstance().getPrice(hikeId, year, month, day, selectedLength);
            // pass values to Rates class (Only used to calculate details about the selected dates)
            Rates rates = new Rates(Rates.HIKE.valueOf(Objects.requireNonNull(selectedHike).toString()));
            // pass date to rates class
            rates.setBeginDate(new BookingDay(year, month, day));
            // pass tour duration
            rates.setDuration(selectedLength);
            // validate server response
            if(serverResponse.getValue() > 0){
                // update price labels
                updateLabels(Objects.requireNonNull(selectedHike).price(),
                        rates.getNormalDays(),
                        rates.getPremiumDays(),
                        // get price form server
                        serverResponse.getValue());
            } else {
                // clear values in ui
                clearLabels();
                // notify the user about incorrect date using response form server
                displayErrorPopup(serverResponse.getKey());
            }
        }
    }

    /**
     * Method used to present a popup to the user (Alerts the date is incorrect)
     *
     * @param details error message
     */
    private void displayErrorPopup(String details) {
        JOptionPane.showMessageDialog(rootPanel,
                details,
                "Date Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Method used to reset the information displayed in the UI
     */
    private void clearLabels() {
        weekDaysLabel.setText("0");
        weekRateLabel.setText("$0");
        weekPriceLabel.setText("$0");
        weekendDaysLabel.setText("0");
        weekendRateLabel.setText("$0");
        weekendFeeLabel.setText("$0");
        weekendPriceLabel.setText("$0");
        totalPriceLabel.setText("$0");
    }

    /**
     * Method used to update information in the UI
     *
     * @param hikeRate    the price of a particular hike
     * @param weekDays    tour days that fall in a week day
     * @param weekendDays tour days that fall in a weekend
     * @param grandTotal  total of the tour
     */
    private void updateLabels(double hikeRate,
                              int weekDays,
                              int weekendDays,
                              double grandTotal) {
        // clear old values
        clearLabels();
        // update with new values
        weekDaysLabel.setText(String.valueOf(weekDays));
        weekRateLabel.setText("$" + hikeRate);

        if (weekendDays > 0) {
            weekendDaysLabel.setText(String.valueOf(weekendDays));
            weekendRateLabel.setText("$" + hikeRate);
            // calculate weekend fee
            double weekendFee = ((hikeRate / 2.0));
            weekendFeeLabel.setText("$" + weekendFee);

            // calculate weekend total
            double weekendPrice = (weekendDays) * (weekendFee + hikeRate);
            weekendPriceLabel.setText("$" + weekendPrice);
        }

        double weekPrice = (weekDays * hikeRate);
        weekPriceLabel.setText("$" + weekPrice);
        totalPriceLabel.setText("$" + grandTotal);
    }

    /**
     * Method used to resize images
     *
     * @param image image to be resized
     * @param w     width
     * @param h     height
     * @return resized image
     */
    private Image scaleImage(Image image, int w, int h) {
        return image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    /**
     * Helper method used to display the name of a hike in a combo box
     */
    public static class MyObjectListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            if (value instanceof Hike) {
                value = ((Hike) value).location();
            }
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return this;
        }
    }
}
