package org.example.demo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Author {
    private IntegerProperty author_id;
    private StringProperty first_name;
    private StringProperty last_name;
    private StringProperty country;
    private StringProperty bio;
    

    public Author(int author_id, String first_name,String last_name,
    		String country, String bio) {
        this.author_id = new SimpleIntegerProperty(author_id);
        this.first_name = new SimpleStringProperty(first_name);
        this.last_name = new SimpleStringProperty(last_name);
        this.country = new SimpleStringProperty(country);
        this.bio = new SimpleStringProperty(bio);
    }

    // Getters for TableView binding
    public IntegerProperty author_idProperty() { return author_id; }
    public StringProperty first_nameProperty() { return first_name; }
    public StringProperty last_nameProperty() { return last_name; }
    public StringProperty countryProperty() { return country; }
    public StringProperty bioProperty() { return bio; }
}

