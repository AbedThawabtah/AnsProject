package org.example.demo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Loan {
    private IntegerProperty loan_id;
    private IntegerProperty borrower_id;
    private IntegerProperty book_id;
    private IntegerProperty period_id;
    private ObjectProperty<LocalDate> loan_date;
    private ObjectProperty<LocalDate> due_date;
    private ObjectProperty<LocalDate> return_date;
   
    
    public Loan(int loan_id, int borrower_id,int book_id, int period_id,
    		LocalDate loan_date, LocalDate due_date, LocalDate return_date) {
        this.loan_id = new SimpleIntegerProperty(loan_id);
        this.borrower_id = new SimpleIntegerProperty(borrower_id);
        this.book_id = new SimpleIntegerProperty(book_id);
        this.period_id = new SimpleIntegerProperty(period_id);
        this.loan_date = new SimpleObjectProperty<>(loan_date);
        this.due_date = new SimpleObjectProperty<>(due_date);
        this.return_date = new SimpleObjectProperty<>(return_date);
    }

    // Getters for TableView binding
    public IntegerProperty loan_idProperty() { return loan_id; }
    public IntegerProperty borrower_idProperty() { return borrower_id; }
    public IntegerProperty book_idProperty() { return book_id; }
    public IntegerProperty period_idProperty() { return period_id; }
    public ObjectProperty<LocalDate> loan_dateProperty() { return loan_date; }
    public ObjectProperty<LocalDate> due_dateProperty() { return due_date; }
    public ObjectProperty<LocalDate> return_dateProperty() { return return_date; }
}

