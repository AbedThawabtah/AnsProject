package org.example.demo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Book {
    private IntegerProperty book_id;
    private StringProperty title;
    private IntegerProperty publisher_id;
    private StringProperty name;
    private StringProperty category;
    private StringProperty book_type;
    private DoubleProperty original_price;
    private IntegerProperty available;

    public Book(int book_id, String title, int publisher_id,
                  String category,String book_type, double original_price, int available) {
        this.book_id = new SimpleIntegerProperty(book_id);
        this.title = new SimpleStringProperty(title);
        this.publisher_id = new SimpleIntegerProperty(publisher_id);
        this.category = new SimpleStringProperty(category);
        this.book_type = new SimpleStringProperty(book_type);
        this.original_price = new SimpleDoubleProperty(original_price);
        this.available = new SimpleIntegerProperty(available);
    }
    public Book(int book_id, String title, String name,
                String category,String book_type, double original_price, int available) {
        this.book_id = new SimpleIntegerProperty(book_id);
        this.title = new SimpleStringProperty(title);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.book_type = new SimpleStringProperty(book_type);
        this.original_price = new SimpleDoubleProperty(original_price);
        this.available = new SimpleIntegerProperty(available);
    }

    // Getters for TableView binding
    public IntegerProperty book_idProperty() { return book_id; }
    public StringProperty titleProperty() { return title; }
    public IntegerProperty publisher_idProperty() { return publisher_id; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty book_typeProperty() { return book_type; }
    public DoubleProperty original_priceProperty() { return original_price; }
    public IntegerProperty availableProperty() { return available; }
    public StringProperty nameProperty() { return name; }

    @Override
    public String toString() {
        return "Book{" +
                "book_id=" + book_id +
                ", title=" + title +
                ", publisher_id=" + publisher_id +
                ", category=" + category +
                ", book_type=" + book_type +
                ", original_price=" + original_price +
                ", available=" + available +
                '}';
    }
}

