package org.example.demo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class getTable<E>{
    public TableView<E> gettable(Class<E> clazz, ObservableList<E> list) {

        TableView<E> tableView = new TableView<>();
        ObservableList<E> bookList = null;
        Constructor<?> constructor = clazz.getConstructors()[0];


        for (int i = 0; i <clazz.getDeclaredFields().length; i++) {
            Field[] f=clazz.getDeclaredFields();
            TableColumn<E, String> name = new TableColumn<>(f[i].getName());
            name.setCellValueFactory(new PropertyValueFactory<>(f[i].getName()));

            tableView.getColumns().add(name);
        }

        tableView.setItems(FXCollections.observableArrayList(list));

        bookList = (ObservableList<E>) list;

        tableView.setItems(bookList);
        return tableView;
    }

}
