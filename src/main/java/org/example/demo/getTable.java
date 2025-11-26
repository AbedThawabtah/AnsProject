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
        
        // Ensure list is never null
        if (list == null) {
            list = FXCollections.observableArrayList();
        }

        // Create columns for all fields
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            TableColumn<E, String> column = new TableColumn<>(f.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(f.getName()));
            tableView.getColumns().add(column);
        }

        // Set items once with safe list
        ObservableList<E> safeList = FXCollections.observableArrayList(list);
        tableView.setItems(safeList);
        
        return tableView;
    }

}
