package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataCollector {
    public static ObservableList<Author> getAllAuthor() {
        ObservableList<Author> Authors = FXCollections.observableArrayList();
        String query = "SELECT * FROM Author";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Author auhtor = new Author(
                        rs.getInt("author_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("country"),
                        rs.getString("bio"));
                Authors.add(auhtor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Authors;
    }


    public static ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT * FROM Book";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getInt("publisher_id"),
                        rs.getString("category"),
                        rs.getString("book_type"),
                        rs.getDouble("original_price"),
                        rs.getInt("available"));

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }


    public static ObservableList<Borrower> getAllBorrower() {
        ObservableList<Borrower> borrowers = FXCollections.observableArrayList();
        String query = "SELECT * FROM Borrower";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Borrower borrower = new Borrower(
                        rs.getInt("borrower_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("type_id"),
                        rs.getString("contact_info"));
                borrowers.add(borrower);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowers;
    }

    public static ObservableList<BorrowerType> getAllBorrowerType() {
        ObservableList<BorrowerType> bts = FXCollections.observableArrayList();
        String query = "SELECT * FROM BorrowerType";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BorrowerType bt = new BorrowerType(
                        rs.getInt("type_id"),
                        rs.getString("type_name"));
                bts.add(bt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bts;
    }
    public static ObservableList<Loan> getAllLoan() {
        ObservableList<Loan> bas = FXCollections.observableArrayList();
        String query = "SELECT * FROM Loan";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Loan ba = new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("borrower_id"),
                        rs.getInt("book_id"),
                        rs.getInt("period_id"),
                        rs.getDate("loan_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);

                bas.add(ba);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return bas;
    }

    public static ObservableList<LoanPeriod> getAllLoanPeriod() {
        ObservableList<LoanPeriod> lps = FXCollections.observableArrayList();
        String query = "SELECT * FROM LoanPeriod";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LoanPeriod lp = new LoanPeriod(
                        rs.getInt("period_id"),
                        rs.getString("period_name"),
                        rs.getInt("days"));

                lps.add(lp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lps;
    }

    public static ObservableList<Publisher> getAllPublisher() {
        ObservableList<Publisher> publishers = FXCollections.observableArrayList();
        String query = "SELECT * FROM Publisher";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Publisher publisher = new Publisher(
                        rs.getInt("publisher_id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("contact_info"));
                publishers.add(publisher);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return publishers;
    }

    public static ObservableList<Sale> getAllSale() {
        ObservableList<Sale> sales = FXCollections.observableArrayList();
        String query = "SELECT * FROM Sale";

        try (Connection conn = HelloController.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Sale sale = new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_id"),
                        rs.getDate("sale_date").toLocalDate(),
                        rs.getDouble("sale_price"));
                sales.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }



}




