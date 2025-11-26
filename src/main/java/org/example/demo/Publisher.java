package org.example.demo;

import javafx.beans.property.*;


public class Publisher {
    private IntegerProperty publisher_id;
    private StringProperty name;
    private StringProperty city;
    private StringProperty country;
    private StringProperty contact_info;

    public Publisher(int publisher_id, String name,
                  String city,String country, String contact_info) {
        this.publisher_id = new SimpleIntegerProperty(publisher_id);
        this.name = new SimpleStringProperty(name);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.contact_info = new SimpleStringProperty(contact_info);
    }

    // Getters for TableView binding
    public IntegerProperty publisher_idProperty() { return publisher_id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty cityProperty() { return city; }
    public StringProperty countryProperty() { return country; }
    public StringProperty contact_infoProperty() { return contact_info; }
}

