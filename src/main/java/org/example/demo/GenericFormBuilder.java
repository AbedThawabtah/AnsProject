package org.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.example.demo.GenericDAO;

import java.lang.reflect.Field;
import java.util.*;

public class GenericFormBuilder<T> {

    private final Class<T> clazz;
    private final GenericDAO<T> dao;
    private final TableView<T> table;

    public GenericFormBuilder(Class<T> clazz, GenericDAO<T> dao, TableView<T> table) {
        this.clazz = clazz;
        this.dao = dao;
        this.table = table;
    }

    public GridPane buildForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(8);
        form.setVgap(8);

        Map<Field, TextField> fieldInputs = new LinkedHashMap<>();

        int row = 0;


        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equalsIgnoreCase("id")) continue; // skip ID (auto)
            f.setAccessible(true);

            Label label = new Label(capitalize(f.getName()) + ":");
            TextField input = new TextField();
            input.setPromptText(f.getName());

            form.add(label, 0, row);
            form.add(input, 1, row);
            fieldInputs.put(f, input);

            row++;
        }

        // Buttons
        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnClear = new Button("Clear");

        HBox actions = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        actions.setAlignment(Pos.CENTER_RIGHT);
        form.add(actions, 0, row, 2, 1);

        // Add
        btnAdd.setOnAction(e -> {
            try {
                T obj = clazz.newInstance();
                for (Field f : fieldInputs.keySet()) {
                    String value = fieldInputs.get(f).getText();
                    setFieldValue(obj, f, value);
                }
                if (dao.add(obj)) refreshTable();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Update
        btnUpdate.setOnAction(e -> {
            T selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                for (Field f : fieldInputs.keySet()) {
                    String value = fieldInputs.get(f).getText();
                    setFieldValue(selected, f, value);
                }
                if (dao.update(selected)) refreshTable();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Delete
        btnDelete.setOnAction(e -> {
            T selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Field idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                int id = (int) idField.get(selected);
                if (dao.delete(id)) refreshTable();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Clear
        btnClear.setOnAction(e -> fieldInputs.values().forEach(TextField::clear));

        // Table selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) return;
            try {
                for (Field f : fieldInputs.keySet()) {
                    f.setAccessible(true);
                    Object v = f.get(val);
                    fieldInputs.get(f).setText(v == null ? "" : v.toString());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        return form;
    }

    private void refreshTable() {
        table.getItems().setAll(dao.getAll());
    }

    private void setFieldValue(Object obj, Field f, String value) throws Exception {
        if (f.getType() == int.class || f.getType() == Integer.class)
            f.set(obj, Integer.parseInt(value));
        else if (f.getType() == double.class || f.getType() == Double.class)
            f.set(obj, Double.parseDouble(value));
        else
            f.set(obj, value);
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
