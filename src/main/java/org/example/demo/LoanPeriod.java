package org.example.demo;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoanPeriod {
    private IntegerProperty period_id;
    private StringProperty period_name;
    private IntegerProperty days;
    

    public LoanPeriod(int period_id, String period_name, int days) {
        this.period_id = new SimpleIntegerProperty(period_id);
        this.period_name = new SimpleStringProperty(period_name);
        this.days = new SimpleIntegerProperty(days);
        
    }

    // Getters for TableView binding
    public IntegerProperty period_idProperty() { return period_id; }
    public StringProperty period_nameProperty() { return period_name; }
    public IntegerProperty daysProperty() { return days; }


    }


