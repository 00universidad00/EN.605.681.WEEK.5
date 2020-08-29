package main.java.edu.jhu.en605681.controller;

import main.java.edu.jhu.en605681.model.Hike;
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

public class Home {
    public JPanel rootPanel;
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

    public Home() {
        // initialize ui components
        initUiComponents();
        // register listeners
        registerListeners();
    }

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

    private void registerListeners() {
        destination.addActionListener(e -> {
            // update days combo box each time destination changes
            Hike selectedHike1 = (Hike) destination.getSelectedItem();
            days.setModel(new DefaultComboBoxModel<>(Objects.requireNonNull(selectedHike1).length()));
        });
        calculatePrice.addActionListener(e -> calculatePrice());
    }

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
            // pass hike and date to Booking & Rates class
            Rates rates = new Rates(Rates.HIKE.valueOf(Objects.requireNonNull(selectedHike).toString()));
            // format date
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(selectedDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // pass date to rates class
            rates.setBeginDate(new BookingDay(year, month, day));
            // get selected tour length
            Integer selectedLength = (Integer) days.getSelectedItem();
            rates.setDuration(Objects.requireNonNull(selectedLength));
            // update price labels
            updateLabels(Objects.requireNonNull(selectedHike).price(),
                    rates.getNormalDays(),
                    rates.getPremiumDays(),
                    rates.getCost());
        }
    }

    private void displayErrorPopup(String message) {
        JOptionPane.showMessageDialog(rootPanel,
                message,
                "Date Error",
                JOptionPane.ERROR_MESSAGE);
    }

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

    private Image scaleImage(Image image, int w, int h) {
        Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return scaled;
    }
}
