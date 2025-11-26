package org.example.demo;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class LibraryApp extends Application {


	// Search boxes
	private HBox authorSearchBox;
	private HBox bookSearchBox;
	private HBox borrowerSearchBox;
	private HBox borrowerTypeSearchBox;
	private HBox loanSearchBox;
	private HBox loanPeriodSearchBox;
	private HBox publisherSearchBox;
	private HBox saleSearchBox;

	@Override
	public void start(Stage primaryStage) {
		// left buttons
		Button btnAuthor = new Button("Author");
		Button btnBook = new Button("Book");
		Button btnBorrower = new Button("Borrower");
		Button btnBorrowerType = new Button("BorrowerType");
		Button btnLoan = new Button("Loan");
		Button btnLoanPeriod = new Button("LoanPeriod");
		Button btnPublisher = new Button("Publisher");
		Button btnSale = new Button("Sale");

		double btnWidth = 200;
		for (Button b : new Button[]{btnAuthor, btnBook, btnBorrower, btnBorrowerType, btnLoan, btnLoanPeriod, btnPublisher, btnSale}) {
			b.setPrefWidth(btnWidth);
			b.setPrefHeight(40);
			b.setDisable(false); // explicitly enabled
		}

		VBox vbox = new VBox(12);
		vbox.setPadding(new Insets(20));
		vbox.getChildren().addAll(
				btnAuthor, btnBook, btnBorrower, btnBorrowerType, btnLoan, btnLoanPeriod, btnPublisher, btnSale
		);

// GenericFormBuilder(Class<T> clazz, GenericDAO<T> dao, TableView<T> table)
		// search boxes
		authorSearchBox = new SearchBox<Author>().createSearchBox(Author.class,DataCollector.getAllAuthor(),new getTable<Author>().gettable(Author.class,DataCollector.getAllAuthor()));
		bookSearchBox = new SearchBox<Book>().createSearchBox(Book.class,DataCollector.getAllBooks(),new getTable<Book>().gettable(Book.class,DataCollector.getAllBooks()));
		borrowerSearchBox = new SearchBox<Borrower>().createSearchBox(Borrower.class,DataCollector.getAllBorrower(),new getTable<Borrower>().gettable(Borrower.class,DataCollector.getAllBorrower()));
		borrowerTypeSearchBox = new SearchBox<BorrowerType>().createSearchBox(BorrowerType.class,DataCollector.getAllBorrowerType(),new getTable<BorrowerType>().gettable(BorrowerType.class,DataCollector.getAllBorrowerType()));
		loanSearchBox = new SearchBox<Loan>().createSearchBox(Loan.class,DataCollector.getAllLoan(),new getTable<Loan>().gettable(Loan.class,DataCollector.getAllLoan()));
		loanPeriodSearchBox =new SearchBox<LoanPeriod>().createSearchBox(LoanPeriod.class,DataCollector.getAllLoanPeriod(),new getTable<LoanPeriod>().gettable(LoanPeriod.class,DataCollector.getAllLoanPeriod()));
		publisherSearchBox =new SearchBox<Publisher>().createSearchBox(Publisher.class,DataCollector.getAllPublisher(),new getTable<Publisher>().gettable(Publisher.class,DataCollector.getAllPublisher()));
		saleSearchBox =new SearchBox<Sale>().createSearchBox(Sale.class,DataCollector.getAllSale(),new getTable<Sale>().gettable(Sale.class,DataCollector.getAllSale()));


		BorderPane borderpane = new BorderPane();
		borderpane.setLeft(vbox);

		// default view
		borderpane.setCenter( new getTable<Author>().gettable(Author.class
				,DataCollector.getAllAuthor()));
		borderpane.setBottom(new GenericFormBuilder(Author.class,new Operation<Author>(), new getTable().gettable(Author.class,DataCollector.getAllAuthor())).buildForm());
		borderpane.setRight(authorSearchBox);

		// button handlers to show table + form + search
		btnAuthor.setOnAction(e -> {
			borderpane.setCenter( new getTable<Author>().gettable(Author.class
					,DataCollector.getAllAuthor()));
			borderpane.setBottom(new GenericFormBuilder(Author.class,new Operation<Author>(), new getTable().gettable(Author.class,DataCollector.getAllAuthor())).buildForm());
			borderpane.setRight(authorSearchBox);
		});

		btnBook.setOnAction(e -> {
			borderpane.setCenter( new getTable<Book>().gettable(Book.class
					,DataCollector.getAllBooks()));
			borderpane.setBottom(new GenericFormBuilder(Book.class,new Operation<Book>(), new getTable().gettable(Book.class,DataCollector.getAllBooks())).buildForm());
			borderpane.setRight(bookSearchBox);
		});

		btnBorrower.setOnAction(e -> {
			borderpane.setCenter( new getTable<Borrower>().gettable(Borrower.class
					,DataCollector.getAllBorrower()));
			borderpane.setBottom(new GenericFormBuilder(Borrower.class,new Operation<Borrower>(), new getTable().gettable(Borrower.class,DataCollector.getAllSale())).buildForm());;
			borderpane.setRight(borrowerSearchBox);
		});

		btnBorrowerType.setOnAction(e -> {
			borderpane.setCenter(new getTable<BorrowerType>().gettable(BorrowerType.class
					,DataCollector.getAllBorrowerType()));
			borderpane.setBottom(new GenericFormBuilder(BorrowerType.class,new Operation<BorrowerType>(), new getTable().gettable(BorrowerType.class,DataCollector.getAllBorrowerType())).buildForm());
			borderpane.setRight(borrowerTypeSearchBox);
		});

		btnLoan.setOnAction(e -> {
			borderpane.setCenter(new getTable<Loan>().gettable(Loan.class
					,DataCollector.getAllLoan()));
			borderpane.setBottom(new GenericFormBuilder(Loan.class,new Operation<Loan>(), new getTable().gettable(Loan.class,DataCollector.getAllLoan())).buildForm());
			borderpane.setRight(loanSearchBox);
		});

		btnLoanPeriod.setOnAction(e -> {
			borderpane.setCenter(new getTable<Loan>().gettable(Loan.class
					,DataCollector.getAllLoan()));
			borderpane.setBottom(new GenericFormBuilder(LoanPeriod.class,new Operation<LoanPeriod>(), new getTable().gettable(LoanPeriod.class,DataCollector.getAllLoanPeriod())).buildForm());
			borderpane.setRight(loanPeriodSearchBox);
		});

		btnPublisher.setOnAction(e -> {
			borderpane.setCenter(new getTable<Publisher>().gettable(Publisher.class
					,DataCollector.getAllPublisher()));
			borderpane.setBottom(new GenericFormBuilder(Publisher.class,new Operation<Publisher>(), new getTable().gettable(Publisher.class,DataCollector.getAllPublisher())).buildForm());
			borderpane.setRight(publisherSearchBox);
		});

		btnSale.setOnAction(e -> {
			borderpane.setCenter(new getTable<Sale>().gettable(Sale.class
					,DataCollector.getAllSale()));
			borderpane.setBottom(new GenericFormBuilder(Sale.class,new Operation<Sale>(), new getTable().gettable(Sale.class,DataCollector.getAllSale())).buildForm());
			borderpane.setRight(saleSearchBox);
		});

		Scene scene = new Scene(borderpane, 1200, 800);
		primaryStage.setTitle("Library App");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
