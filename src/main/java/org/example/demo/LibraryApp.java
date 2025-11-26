package org.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application class for Library Management System
 */
public class LibraryApp extends Application {

    private Stage primaryStage;
    private BorderPane mainLayout;
    private MenuBar menuBar;
    private VBox sidebar;
    private BorderPane contentArea;
    
    // ObservableLists for TableViews (maintained in controller)
    private ObservableList<Author> authorList;
    private ObservableList<Book> bookList;
    private ObservableList<Borrower> borrowerList;
    
    // TableViews (maintained in controller)
    private TableView<Author> authorTable;
    private TableView<Book> bookTable;
    private TableView<Borrower> borrowerTable;
    
    // DAOs
    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private BorrowerDAO borrowerDAO;
    
    // Views
    private LoginView loginView;
    private SignupView signupView;
    private ReportsView reportsView;
    private AboutView aboutView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize database connection and create Users table
        UserDAO.createUsersTable();
        
        // Show login view first
        showLoginView();
        
        primaryStage.setTitle("Library Management System");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private void showLoginView() {
        loginView = new LoginView();
        loginView.setOnLoginSuccess(() -> showMainWindow());
        loginView.setOnSignupClick(() -> showSignupView());
        
        Scene scene = new Scene(loginView.getRoot(), 500, 400);
        scene.getStylesheets().add(getClass().getResource("/org/example/demo/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showSignupView() {
        signupView = new SignupView();
        signupView.setOnSignupSuccess(() -> showLoginView());
        signupView.setOnBackClick(() -> showLoginView());
        
        Scene scene = new Scene(signupView.getRoot(), 500, 500);
        scene.getStylesheets().add(getClass().getResource("/org/example/demo/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showMainWindow() {
        // Create main layout
        mainLayout = new BorderPane();
        
        // Initialize DAOs
        authorDAO = new AuthorDAO();
        bookDAO = new BookDAO();
        borrowerDAO = new BorrowerDAO();
        
        // Initialize ObservableLists
        authorList = FXCollections.observableArrayList();
        bookList = FXCollections.observableArrayList();
        borrowerList = FXCollections.observableArrayList();
        
        // Load initial data
        reloadAuthors();
        reloadBooks();
        reloadBorrowers();
        
        // Create menu bar
        createMenuBar();
        mainLayout.setTop(menuBar);
        
        // Create sidebar
        createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Create content area
        contentArea = new BorderPane();
        mainLayout.setCenter(contentArea);
        
        // Show default view (Authors)
        showAuthorView();
        
        // Create scene with CSS
        Scene scene = new Scene(mainLayout, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/org/example/demo/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
    
    /**
     * Reload authors from database and update ObservableList
     */
    private void reloadAuthors() {
        java.util.List<Author> list = authorDAO.getAll();
        if (list == null) list = new java.util.ArrayList<>();
        authorList.setAll(list);
    }
    
    /**
     * Reload books from database and update ObservableList
     */
    private void reloadBooks() {
        java.util.List<Book> list = bookDAO.getAll();
        if (list == null) list = new java.util.ArrayList<>();
        bookList.setAll(list);
    }
    
    /**
     * Reload borrowers from database and update ObservableList
     */
    private void reloadBorrowers() {
        java.util.List<Borrower> list = borrowerDAO.getAll();
        if (list == null) list = new java.util.ArrayList<>();
        borrowerList.setAll(list);
    }

    private void createMenuBar() {
        menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            SessionManager.logout();
            showLoginView();
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().addAll(logoutItem, new SeparatorMenuItem(), exitItem);
        
        // View menu
        Menu viewMenu = new Menu("View");
        MenuItem reportsItem = new MenuItem("Reports");
        reportsItem.setOnAction(e -> showReportsView());
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutView());
        viewMenu.getItems().addAll(reportsItem, new SeparatorMenuItem(), aboutItem);
        
        // User info
        Menu userMenu = new Menu("User");
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            MenuItem userInfoItem = new MenuItem("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            userInfoItem.setDisable(true);
            userMenu.getItems().add(userInfoItem);
        }
        
        menuBar.getMenus().addAll(fileMenu, viewMenu, userMenu);
    }

    private void createSidebar() {
        sidebar = new VBox(8);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.getStyleClass().add("sidebar");
        
        // Create buttons for each entity
        Button btnAuthor = createSidebarButton("Authors", () -> showAuthorView());
        Button btnBook = createSidebarButton("Books", () -> showBookView());
        Button btnBorrower = createSidebarButton("Borrowers", () -> showBorrowerView());
        Button btnPublisher = createSidebarButton("Publishers", () -> showPublisherView());
        Button btnLoan = createSidebarButton("Loans", () -> showLoanView());
        Button btnSale = createSidebarButton("Sales", () -> showSaleView());
        Button btnBorrowerType = createSidebarButton("Borrower Types", () -> showBorrowerTypeView());
        Button btnLoanPeriod = createSidebarButton("Loan Periods", () -> showLoanPeriodView());
        
        sidebar.getChildren().addAll(
            btnAuthor, btnBook, btnBorrower, btnPublisher,
            btnLoan, btnSale, btnBorrowerType, btnLoanPeriod
        );
    }

    private Button createSidebarButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(40);
        btn.getStyleClass().add("sidebar-button");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void showAuthorView() {
        // Create or reuse table
        if (authorTable == null) {
            authorTable = new getTable<Author>().gettable(Author.class, authorList);
            authorTable.setItems(authorList);
        }
        
        HBox searchBox = new SearchBox<Author>().createSearchBox(Author.class, authorList, authorTable);
        
        boolean canEdit = SessionManager.canEdit();
        GenericFormBuilder<Author> formBuilder = new GenericFormBuilder<>(
            Author.class, 
            authorDAO, 
            authorTable,
            authorList,
            canEdit,
            () -> reloadAuthors() // Reload callback
        );
        
        contentArea.setCenter(authorTable);
        contentArea.setBottom(formBuilder.buildForm());
        contentArea.setRight(searchBox);
    }

    private void showBookView() {
        // Create or reuse table
        if (bookTable == null) {
            bookTable = new getTable<Book>().gettable(Book.class, bookList);
            bookTable.setItems(bookList);
        }
        
        HBox searchBox = new SearchBox<Book>().createSearchBox(Book.class, bookList, bookTable);
        
        boolean canEdit = SessionManager.canEdit();
        GenericFormBuilder<Book> formBuilder = new GenericFormBuilder<>(
            Book.class, 
            bookDAO, 
            bookTable,
            bookList,
            canEdit,
            () -> reloadBooks() // Reload callback
        );
        
        contentArea.setCenter(bookTable);
        contentArea.setBottom(formBuilder.buildForm());
        contentArea.setRight(searchBox);
    }

    private void showBorrowerView() {
        // Create or reuse table
        if (borrowerTable == null) {
            borrowerTable = new getTable<Borrower>().gettable(Borrower.class, borrowerList);
            borrowerTable.setItems(borrowerList);
        }
        
        HBox searchBox = new SearchBox<Borrower>().createSearchBox(Borrower.class, borrowerList, borrowerTable);
        
        boolean canEdit = SessionManager.canEdit();
        GenericFormBuilder<Borrower> formBuilder = new GenericFormBuilder<>(
            Borrower.class, 
            borrowerDAO, 
            borrowerTable,
            borrowerList,
            canEdit,
            () -> reloadBorrowers() // Reload callback
        );
        
        contentArea.setCenter(borrowerTable);
        contentArea.setBottom(formBuilder.buildForm());
        contentArea.setRight(searchBox);
    }

    private void showPublisherView() {
        TableView<Publisher> table = new getTable<Publisher>().gettable(Publisher.class, DataCollector.getAllPublisher());
        HBox searchBox = new SearchBox<Publisher>().createSearchBox(Publisher.class, DataCollector.getAllPublisher(), table);
        
        // Publishers are view-only for now (no DAO implemented)
        contentArea.setCenter(table);
        contentArea.setBottom(new VBox()); // Empty form for view-only
        contentArea.setRight(searchBox);
    }

    private void showLoanView() {
        TableView<Loan> table = new getTable<Loan>().gettable(Loan.class, DataCollector.getAllLoan());
        HBox searchBox = new SearchBox<Loan>().createSearchBox(Loan.class, DataCollector.getAllLoan(), table);
        
        // Loans are view-only for now
        contentArea.setCenter(table);
        contentArea.setBottom(new VBox());
        contentArea.setRight(searchBox);
    }

    private void showSaleView() {
        TableView<Sale> table = new getTable<Sale>().gettable(Sale.class, DataCollector.getAllSale());
        HBox searchBox = new SearchBox<Sale>().createSearchBox(Sale.class, DataCollector.getAllSale(), table);
        
        // Sales are view-only for now
        contentArea.setCenter(table);
        contentArea.setBottom(new VBox());
        contentArea.setRight(searchBox);
    }

    private void showBorrowerTypeView() {
        TableView<BorrowerType> table = new getTable<BorrowerType>().gettable(BorrowerType.class, DataCollector.getAllBorrowerType());
        HBox searchBox = new SearchBox<BorrowerType>().createSearchBox(BorrowerType.class, DataCollector.getAllBorrowerType(), table);
        
        contentArea.setCenter(table);
        contentArea.setBottom(new VBox());
        contentArea.setRight(searchBox);
    }

    private void showLoanPeriodView() {
        TableView<LoanPeriod> table = new getTable<LoanPeriod>().gettable(LoanPeriod.class, DataCollector.getAllLoanPeriod());
        HBox searchBox = new SearchBox<LoanPeriod>().createSearchBox(LoanPeriod.class, DataCollector.getAllLoanPeriod(), table);
        
        contentArea.setCenter(table);
        contentArea.setBottom(new VBox());
        contentArea.setRight(searchBox);
    }

    private void showReportsView() {
        if (reportsView == null) {
            reportsView = new ReportsView();
        }
        contentArea.setCenter(reportsView.getRoot());
        contentArea.setBottom(null);
        contentArea.setRight(null);
    }

    private void showAboutView() {
        if (aboutView == null) {
            aboutView = new AboutView();
        }
        contentArea.setCenter(aboutView.getRoot());
        contentArea.setBottom(null);
        contentArea.setRight(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
