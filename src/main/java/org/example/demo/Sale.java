package org.example.demo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Sale {
    private IntegerProperty sale_id;
    private IntegerProperty book_id;
    private IntegerProperty borrower_id;
    private ObjectProperty<LocalDate> sale_date;
    private DoubleProperty sale_price;
    

    public Sale(int sale_id,int book_id,int borrower_id , LocalDate sale_date,
    		double sale_price ) {
        this.sale_id = new SimpleIntegerProperty(sale_id);
        this.book_id = new SimpleIntegerProperty(book_id);
        this.borrower_id = new SimpleIntegerProperty(borrower_id);
        this.sale_date = new SimpleObjectProperty<>(sale_date);
        this.sale_price = new SimpleDoubleProperty(sale_price);
        
    }

    // Getters for TableView binding
    public IntegerProperty sale_idProperty() { return sale_id; }
    public IntegerProperty book_idProperty() { return book_id; }
    public IntegerProperty borrower_idProperty() { return borrower_id; }
    public ObjectProperty<LocalDate> sale_dateProperty() { return sale_date; }
    public DoubleProperty sale_priceProperty() { return sale_price; }
}

