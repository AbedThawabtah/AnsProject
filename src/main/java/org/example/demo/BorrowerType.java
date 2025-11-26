package org.example.demo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class BorrowerType {
    private IntegerProperty type_id;
    private StringProperty type_name;
    

    public BorrowerType(int type_id, String type_name) {
        this.type_id = new SimpleIntegerProperty(type_id);
        this.type_name = new SimpleStringProperty(type_name);
    }

    // Getters for TableView binding
    public IntegerProperty type_idProperty() { return type_id; }
    public StringProperty type_nameProperty() { return type_name; }
}

