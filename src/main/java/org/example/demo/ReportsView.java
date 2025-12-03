package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;

/**
 * Reports view with various SQL queries and reports
 */
public class ReportsView {
    private BorderPane root;
    private TabPane tabPane;

    public ReportsView() {
        createView();
    }

    private void createView() {
        root = new BorderPane();
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create all report tabs
        tabPane.getTabs().addAll(
            createTotalBooksValueTab(),
            createBooksByAuthorTab(),
            createBooksByBorrowerTab(),
            createCurrentLoansTab(),
            createBooksByCountryTab(),
            createBorrowersNeverBorrowedTab(),
            createBooksMultipleAuthorsTab(),
            createBooksSoldTab(),
            createAvailableBooksTab(),
            createLoanHistoryTab(),
            createLoansByDateRangeTab(),
            createBooksByCategoryTab()
        );

        root.setCenter(tabPane);
    }

    // Report 1: Total value of all books
    private Tab createTotalBooksValueTab() {
        Tab tab = new Tab("Total Books Value");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Button refreshBtn = new Button("Calculate Total Value");
        Label resultLabel = new Label("Click button to calculate");

        refreshBtn.setOnAction(e -> {
            String sql = "SELECT SUM(original_price * available) as total_value FROM book";
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        if (rs.next()) {
                            double total = rs.getDouble("total_value");
                            resultLabel.setText(String.format("Total Value of All Books: $%.2f", total));
                        }
                    }
                }
            } catch (SQLException ex) {
                resultLabel.setText("Error: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(refreshBtn, resultLabel);
        tab.setContent(content);
        return tab;
    }

    // Report 2: Books written by a selected author
    private Tab createBooksByAuthorTab() {
        Tab tab = new Tab("Books by Author");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> authorCombo = new ComboBox<>();
        authorCombo.setPromptText("Select Author");
        loadAuthors(authorCombo);

        TableView<Book> table = createBookTable();
        Button refreshBtn = new Button("Show Books");

        refreshBtn.setOnAction(e -> {
            String authorName = authorCombo.getValue();
            if (authorName == null) {
                showAlert("Please select an author");
                return;
            }

            String sql = "SELECT DISTINCT b.* FROM book b " +
                        "INNER JOIN bookauthor ba ON b.book_id = ba.book_id " +
                        "INNER JOIN author a ON ba.author_id = a.author_id " +
                        "WHERE CONCAT(a.first_name, ' ', a.last_name) = ?";
            
            ObservableList<Book> books = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, authorName);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                books.add(new Book(
                                    rs.getInt("book_id"),
                                    rs.getString("title"),
                                    rs.getInt("publisher_id"),
                                    rs.getString("category"),
                                    rs.getString("book_type"),
                                    rs.getDouble("original_price"),
                                    rs.getInt("available")>0?"Yes":"No"
                                ));
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(books);
        });

        content.getChildren().addAll(new Label("Select Author:"), authorCombo, refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 3: Books borrowed or bought by a specific borrower
    private Tab createBooksByBorrowerTab() {
        Tab tab = new Tab("Books by Borrower");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> borrowerCombo = new ComboBox<>();
        borrowerCombo.setPromptText("Select Borrower");
        loadBorrowers(borrowerCombo);

        TableView<Book> table = createBookTable();
        Button refreshBtn = new Button("Show Books");

        refreshBtn.setOnAction(e -> {
            String borrowerName = borrowerCombo.getValue();
            if (borrowerName == null) {
                showAlert("Please select a borrower");
                return;
            }

            String sql = "SELECT DISTINCT b.* FROM book b " +
                        "LEFT JOIN loan l ON b.book_id = l.book_id " +
                        "LEFT JOIN sale s ON b.book_id = s.book_id " +
                        "LEFT JOIN borrower br ON (l.borrower_id = br.borrower_id OR s.borrower_id = br.borrower_id) " +
                        "WHERE CONCAT(br.first_name, ' ', br.last_name) = ?";
            
            ObservableList<Book> books = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, borrowerName);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                books.add(new Book(
                                    rs.getInt("book_id"),
                                    rs.getString("title"),
                                    rs.getInt("publisher_id"),
                                    rs.getString("category"),
                                    rs.getString("book_type"),
                                    rs.getDouble("original_price"),
                                    rs.getInt("available")>0?"Yes":"No"
                                ));
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(books);
        });

        content.getChildren().addAll(new Label("Select Borrower:"), borrowerCombo, refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 4: Current loans and due dates
    private Tab createCurrentLoansTab() {
        Tab tab = new Tab("Current Loans");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<LoanInfo> table = new TableView<>();
        
        TableColumn<LoanInfo, String> bookCol = new TableColumn<>("Book Title");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        
        TableColumn<LoanInfo, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        
        TableColumn<LoanInfo, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        
        TableColumn<LoanInfo, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<LoanInfo, Integer> daysOverdueCol = new TableColumn<>("Days Overdue");
        daysOverdueCol.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));

        table.getColumns().addAll(bookCol, borrowerCol, loanDateCol, dueDateCol, daysOverdueCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            String sql = "SELECT l.*, b.title as book_title, " +
                        "CONCAT(br.first_name, ' ', br.last_name) as borrower_name " +
                        "FROM loan l " +
                        "INNER JOIN book b ON l.book_id = b.book_id " +
                        "INNER JOIN borrower br ON l.borrower_id = br.borrower_id " +
                        "WHERE l.return_date IS NULL";
            
            ObservableList<LoanInfo> loans = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                            LocalDate today = LocalDate.now();
                            int daysOverdue = dueDate.isBefore(today) ? 
                                (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today) : 0;
                            
                            loans.add(new LoanInfo(
                                rs.getString("book_title"),
                                rs.getString("borrower_name"),
                                rs.getDate("loan_date").toLocalDate(),
                                dueDate,
                                daysOverdue
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(loans);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 5: Books published in a selected country
    private Tab createBooksByCountryTab() {
        Tab tab = new Tab("Books by Country");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> countryCombo = new ComboBox<>();
        countryCombo.setPromptText("Select Country");
        loadCountries(countryCombo);

        TableView<Book> table = createBookTable();
        Button refreshBtn = new Button("Show Books");

        refreshBtn.setOnAction(e -> {
            String country = countryCombo.getValue();
            if (country == null) {
                showAlert("Please select a country");
                return;
            }

            String sql = "SELECT b.* FROM book b " +
                        "INNER JOIN publisher p ON b.publisher_id = p.publisher_id " +
                        "WHERE p.country = ?";
            
            ObservableList<Book> books = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, country);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                books.add(new Book(
                                    rs.getInt("book_id"),
                                    rs.getString("title"),
                                    rs.getInt("publisher_id"),
                                    rs.getString("category"),
                                    rs.getString("book_type"),
                                    rs.getDouble("original_price"),
                                    rs.getInt("available")>0?"Yes":"No"
                                ));
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(books);
        });

        content.getChildren().addAll(new Label("Select Country:"), countryCombo, refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 6: Borrowers who never borrowed or bought a book
    private Tab createBorrowersNeverBorrowedTab() {
        Tab tab = new Tab("Borrowers Never Active");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<Borrower> table = new getTable<Borrower>().gettable(Borrower.class, FXCollections.observableArrayList());
        Button refreshBtn = new Button("Refresh");

        refreshBtn.setOnAction(e -> {
            String sql = "SELECT br.* FROM borrower br " +
                        "LEFT JOIN loan l ON br.borrower_id = l.borrower_id " +
                        "LEFT JOIN sale s ON br.borrower_id = s.borrower_id " +
                        "WHERE l.borrower_id IS NULL AND s.borrower_id IS NULL";
            
            ObservableList<Borrower> borrowers = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            borrowers.add(new Borrower(
                                rs.getInt("borrower_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getInt("type_id"),
                                rs.getString("contact_info")
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(borrowers);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 7: Books with more than one author
    private Tab createBooksMultipleAuthorsTab() {
        Tab tab = new Tab("Books (Multiple Authors)");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<Book> table = createBookTable();
        Button refreshBtn = new Button("Refresh");

        refreshBtn.setOnAction(e -> {
            String sql = "SELECT b.*, COUNT(ba.author_id) as author_count " +
                        "FROM book b " +
                        "INNER JOIN bookauthor ba ON b.book_id = ba.book_id " +
                        "GROUP BY b.book_id " +
                        "HAVING COUNT(ba.author_id) > 1";
            
            ObservableList<Book> books = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            books.add(new Book(
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getInt("publisher_id"),
                                rs.getString("category"),
                                rs.getString("book_type"),
                                rs.getDouble("original_price"),
                                rs.getInt("available")>0?"Yes":"No"
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(books);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 8: Books that were sold and their sale prices
    private Tab createBooksSoldTab() {
        Tab tab = new Tab("Books Sold");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<SaleInfo> table = new TableView<>();
        
        TableColumn<SaleInfo, String> bookCol = new TableColumn<>("Book Title");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        
        TableColumn<SaleInfo, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        
        TableColumn<SaleInfo, LocalDate> saleDateCol = new TableColumn<>("Sale Date");
        saleDateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        
        TableColumn<SaleInfo, Double> priceCol = new TableColumn<>("Sale Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

        table.getColumns().addAll(bookCol, borrowerCol, saleDateCol, priceCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            String sql = "SELECT s.*, b.title as book_title, " +
                        "CONCAT(br.first_name, ' ', br.last_name) as borrower_name " +
                        "FROM sale s " +
                        "INNER JOIN book b ON s.book_id = b.book_id " +
                        "INNER JOIN borrower br ON s.borrower_id = br.borrower_id " +
                        "ORDER BY s.sale_date DESC";
            
            ObservableList<SaleInfo> sales = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            sales.add(new SaleInfo(
                                rs.getString("book_title"),
                                rs.getString("borrower_name"),
                                rs.getDate("sale_date").toLocalDate(),
                                rs.getDouble("sale_price")
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(sales);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 9: Books currently available for borrowing
    private Tab createAvailableBooksTab() {
        Tab tab = new Tab("Available Books");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<Book> table = createBookTable();
        Button refreshBtn = new Button("Refresh");

        refreshBtn.setOnAction(e -> {
            String sql = "SELECT * FROM book WHERE available > 0";
            
            ObservableList<Book> books = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            books.add(new Book(
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getInt("publisher_id"),
                                rs.getString("category"),
                                rs.getString("book_type"),
                                rs.getDouble("original_price"),
                                rs.getInt("available")>0?"Yes":"No"
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(books);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 10: Loan history for a selected borrower
    private Tab createLoanHistoryTab() {
        Tab tab = new Tab("Loan History");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> borrowerCombo = new ComboBox<>();
        borrowerCombo.setPromptText("Select Borrower");
        loadBorrowers(borrowerCombo);

        TableView<LoanInfo> table = new TableView<>();
        
        TableColumn<LoanInfo, String> bookCol = new TableColumn<>("Book Title");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        
        TableColumn<LoanInfo, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        
        TableColumn<LoanInfo, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<LoanInfo, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        table.getColumns().addAll(bookCol, loanDateCol, dueDateCol, returnDateCol);

        Button refreshBtn = new Button("Show History");
        refreshBtn.setOnAction(e -> {
            String borrowerName = borrowerCombo.getValue();
            if (borrowerName == null) {
                showAlert("Please select a borrower");
                return;
            }

            String sql = "SELECT l.*, b.title as book_title " +
                        "FROM loan l " +
                        "INNER JOIN book b ON l.book_id = b.book_id " +
                        "INNER JOIN borrower br ON l.borrower_id = br.borrower_id " +
                        "WHERE CONCAT(br.first_name, ' ', br.last_name) = ? " +
                        "ORDER BY l.loan_date DESC";
            
            ObservableList<LoanInfo> loans = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, borrowerName);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                loans.add(new LoanInfo(
                                    rs.getString("book_title"),
                                    borrowerName,
                                    rs.getDate("loan_date").toLocalDate(),
                                    rs.getDate("due_date").toLocalDate(),
                                    rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null
                                ));
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(loans);
        });

        content.getChildren().addAll(new Label("Select Borrower:"), borrowerCombo, refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 11: Books borrowed within a certain date range
    private Tab createLoansByDateRangeTab() {
        Tab tab = new Tab("Loans by Date Range");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        startDate.setPromptText("Start Date");
        endDate.setPromptText("End Date");

        TableView<LoanInfo> table = new TableView<>();
        
        TableColumn<LoanInfo, String> bookCol = new TableColumn<>("Book Title");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        
        TableColumn<LoanInfo, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        
        TableColumn<LoanInfo, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));

        table.getColumns().addAll(bookCol, borrowerCol, loanDateCol);

        Button refreshBtn = new Button("Search");
        refreshBtn.setOnAction(e -> {
            if (startDate.getValue() == null || endDate.getValue() == null) {
                showAlert("Please select both start and end dates");
                return;
            }

            String sql = "SELECT l.*, b.title as book_title, " +
                        "CONCAT(br.first_name, ' ', br.last_name) as borrower_name " +
                        "FROM loan l " +
                        "INNER JOIN book b ON l.book_id = b.book_id " +
                        "INNER JOIN borrower br ON l.borrower_id = br.borrower_id " +
                        "WHERE l.loan_date BETWEEN ? AND ? " +
                        "ORDER BY l.loan_date";
            
            ObservableList<LoanInfo> loans = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setDate(1, Date.valueOf(startDate.getValue()));
                        pstmt.setDate(2, Date.valueOf(endDate.getValue()));
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                loans.add(new LoanInfo(
                                    rs.getString("book_title"),
                                    rs.getString("borrower_name"),
                                    rs.getDate("loan_date").toLocalDate(),
                                    rs.getDate("due_date").toLocalDate(),
                                    null
                                ));
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(loans);
        });

        HBox dateBox = new HBox(10, new Label("Start:"), startDate, new Label("End:"), endDate);
        content.getChildren().addAll(dateBox, refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Report 12: Books per category
    private Tab createBooksByCategoryTab() {
        Tab tab = new Tab("Books by Category");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TableView<CategoryCount> table = new TableView<>();
        
        TableColumn<CategoryCount, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<CategoryCount, Integer> countCol = new TableColumn<>("Number of Books");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        table.getColumns().addAll(categoryCol, countCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            String sql = "SELECT category, COUNT(*) as count FROM book GROUP BY category ORDER BY count DESC";
            
            ObservableList<CategoryCount> categories = FXCollections.observableArrayList();
            try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
                if (conn != null) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            categories.add(new CategoryCount(
                                rs.getString("category"),
                                rs.getInt("count")
                            ));
                        }
                    }
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
            table.setItems(categories);
        });

        content.getChildren().addAll(refreshBtn, table);
        tab.setContent(content);
        return tab;
    }

    // Helper methods
    private TableView<Book> createBookTable() {
        return new getTable<Book>().gettable(Book.class, FXCollections.observableArrayList());
    }

    private void loadAuthors(ComboBox<String> combo) {
        String sql = "SELECT DISTINCT CONCAT(first_name, ' ', last_name) as name FROM author ORDER BY name";
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        combo.getItems().add(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    private void loadBorrowers(ComboBox<String> combo) {
        String sql = "SELECT DISTINCT CONCAT(first_name, ' ', last_name) as name FROM borrower ORDER BY name";
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        combo.getItems().add(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    private void loadCountries(ComboBox<String> combo) {
        String sql = "SELECT DISTINCT country FROM publisher WHERE country IS NOT NULL ORDER BY country";
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        combo.getItems().add(rs.getString("country"));
                    }
                }
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }

    // Helper classes for reports
    public static class LoanInfo {
        private String bookTitle;
        private String borrowerName;
        private LocalDate loanDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private int daysOverdue;

        public LoanInfo(String bookTitle, String borrowerName, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
            this.bookTitle = bookTitle;
            this.borrowerName = borrowerName;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.daysOverdue = 0;
        }

        public LoanInfo(String bookTitle, String borrowerName, LocalDate loanDate, LocalDate dueDate, int daysOverdue) {
            this.bookTitle = bookTitle;
            this.borrowerName = borrowerName;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.daysOverdue = daysOverdue;
        }

        // Getters
        public String getBookTitle() { return bookTitle; }
        public String getBorrowerName() { return borrowerName; }
        public LocalDate getLoanDate() { return loanDate; }
        public LocalDate getDueDate() { return dueDate; }
        public LocalDate getReturnDate() { return returnDate; }
        public int getDaysOverdue() { return daysOverdue; }
    }

    public static class SaleInfo {
        private String bookTitle;
        private String borrowerName;
        private LocalDate saleDate;
        private double salePrice;

        public SaleInfo(String bookTitle, String borrowerName, LocalDate saleDate, double salePrice) {
            this.bookTitle = bookTitle;
            this.borrowerName = borrowerName;
            this.saleDate = saleDate;
            this.salePrice = salePrice;
        }

        // Getters
        public String getBookTitle() { return bookTitle; }
        public String getBorrowerName() { return borrowerName; }
        public LocalDate getSaleDate() { return saleDate; }
        public double getSalePrice() { return salePrice; }
    }

    public static class CategoryCount {
        private String category;
        private int count;

        public CategoryCount(String category, int count) {
            this.category = category;
            this.count = count;
        }

        // Getters
        public String getCategory() { return category; }
        public int getCount() { return count; }
    }
}

