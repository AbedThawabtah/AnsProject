package org.example.demo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Borrower {
    private IntegerProperty borrower_id;
    private StringProperty first_name;
    private StringProperty last_name;
    private IntegerProperty type_id;
    private StringProperty contact_info;
    

    public Borrower(int borrower_id, String first_name, 
                  String last_name,int type_id, String contact_info) {
        this.borrower_id = new SimpleIntegerProperty(borrower_id);
        this.first_name = new SimpleStringProperty(first_name);
        this.last_name = new SimpleStringProperty(last_name);
        this.type_id = new SimpleIntegerProperty(type_id);
        this.contact_info = new SimpleStringProperty(contact_info);
    }

    // Getters for TableView binding
    public IntegerProperty borrower_idProperty() { return borrower_id; }
    public StringProperty first_nameProperty() { return first_name; }
    public StringProperty last_nameProperty() { return last_name; }
    public IntegerProperty type_idProperty() { return type_id; }
    public StringProperty contact_infoProperty() { return contact_info; }
}

