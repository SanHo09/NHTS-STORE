package com.nhom4.nhtsstore.ui.shared.components;

import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import java.util.Date;
import java.util.Calendar;
/**
 *
 * @author NamDang
 */
public class DatePicker extends JPanel {
    private final JDatePickerImpl datePicker;

    public DatePicker() {
        setLayout(new BorderLayout());

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        add(datePicker, BorderLayout.CENTER);
    }

    public Date getDate() {
        return (Date) datePicker.getModel().getValue();
    }

    public void setDate(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            datePicker.getModel().setDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.getModel().setSelected(true);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        datePicker.setEnabled(enabled);
    }
}
