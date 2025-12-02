package org.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.example.demo.GenericDAO;

import java.lang.reflect.Field;
import java.util.*;

public class GenericFormBuilder<T> {

    private final Class<T> clazz;
    private final GenericDAO<T> dao;
    private final TableView<T> table;
    private final javafx.collections.ObservableList<T> observableList; // Backing list for TableView
    private final Runnable reloadCallback; // Callback to reload from database
    private boolean canEdit = true; // For role-based access control

    public GenericFormBuilder(Class<T> clazz, GenericDAO<T> dao, TableView<T> table) {
        this.clazz = clazz;
        this.dao = dao;
        this.table = table;
        this.observableList = null;
        this.reloadCallback = null;
    }

    public GenericFormBuilder(Class<T> clazz, GenericDAO<T> dao, TableView<T> table, boolean canEdit) {
        this.clazz = clazz;
        this.dao = dao;
        this.table = table;
        this.observableList = null;
        this.reloadCallback = null;
        this.canEdit = canEdit;
    }
    
    /**
     * Constructor with ObservableList and reload callback for direct list updates
     */
    public GenericFormBuilder(Class<T> clazz, GenericDAO<T> dao, TableView<T> table, 
                             javafx.collections.ObservableList<T> observableList, 
                             boolean canEdit, Runnable reloadCallback) {
        this.clazz = clazz;
        this.dao = dao;
        this.table = table;
        this.observableList = observableList;
        this.canEdit = canEdit;
        this.reloadCallback = reloadCallback;
    }

    public GridPane buildForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(8);
        form.setVgap(8);

        Map<Field, Control> fieldInputs = new LinkedHashMap<>();

        int row = 0;


        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().contains("id") && f.getName().contains(clazz.getSimpleName().toLowerCase())) continue; // skip ID (auto)
            f.setAccessible(true);

            Label label = new Label(capitalize(f.getName()) + ":");
            TextField input = new TextField();
            input.setPromptText(f.getName());
 if (f.getName().equals("publisher_id")) {
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.setEditable(false);
    comboBox.setPromptText("select publisher");
    for (Publisher p : DataCollector.getAllPublisher()) {
        comboBox.getItems().add(p.nameProperty().get());
    }
    Label label2= new Label("publisher names:");

    form.add(label2, 0, row);
    form.add(comboBox, 1, row);
    fieldInputs.put(f, comboBox);
    row++;
    continue;


            }
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

        // Disable buttons if user doesn't have edit permissions
        if (!canEdit) {
            btnAdd.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        }

        HBox actions = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        actions.setAlignment(Pos.CENTER_RIGHT);
        form.add(actions, 0, row, 2, 1);

        // Add
        btnAdd.setOnAction(e -> {
            try {
                // Validate required fields
                String validationError = validateRequiredFields(fieldInputs);
                if (validationError != null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", validationError);
                    return;
                }

                // Validate field formats
                if (!validateFields(fieldInputs)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid values for all fields.");
                    return;
                }

                // Create object using constructor with default values, then set fields
                T obj = createNewInstance(fieldInputs);
                System.out.println("Created new instance: " + obj);
                
                // Attempt to add
                boolean success = dao.add(obj);
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Record added successfully!");
                    // Clear all input fields
                    fieldInputs.values().forEach(c -> {
                        if (c instanceof TextField tf) {
                            tf.clear();
                        }
                    });
                    
                    // Update TableView: reload from database to get auto-generated ID
                    if (reloadCallback != null && observableList != null) {
                        // Reload from database and update ObservableList
                        reloadCallback.run();
                        // Refresh table to show new record
                        table.refresh();
                    } else {
                        // Fallback: use old refresh method
                        refreshTable();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add record. Please check your input and try again.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid numbers for numeric fields.");
            } catch (IllegalArgumentException ex) {
                // Validation errors from DAO
                showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            } catch (RuntimeException ex) {
                // Database errors wrapped in RuntimeException
                String errorMsg = ex.getMessage();
                if (errorMsg != null && errorMsg.contains("Database error:")) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", errorMsg.replace("Database error: ", ""));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + errorMsg);
                }
                ex.printStackTrace();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Update
        btnUpdate.setOnAction(e -> {
            T selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a record to update.");
                return;
            }
            try {
                // Validate required fields
                String validationError = validateRequiredFields(fieldInputs);
                if (validationError != null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", validationError);
                    return;
                }

                // Validate field formats
                if (!validateFields(fieldInputs)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid values for all fields.");
                    return;
                }

                // Update all fields from form inputs
                for (Field f : fieldInputs.keySet()) {
                    String value = ((TextField) fieldInputs.get(f)).getText().trim();
                    // Skip ID fields (they shouldn't be updated)
                    if (f.getName().toLowerCase().endsWith("_id")) {
                        continue;
                    }
                    // Set field value (handles empty strings as null for nullable fields)
                    setFieldValue(selected, f, value);
                }
                
                // Attempt to update
                boolean success = dao.update(selected);
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Record updated successfully!");
                    
                    // Update TableView: reload from database to get latest data
                    if (reloadCallback != null && observableList != null) {
                        // Reload from database and update ObservableList
                        reloadCallback.run();
                        // Refresh table to show updated data
                        table.refresh();
                    } else {
                        // Fallback: use old refresh method
                        refreshTable();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update record. Please check your input and try again.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid numbers for numeric fields.");
            } catch (IllegalArgumentException ex) {
                // Validation errors from DAO
                showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            } catch (RuntimeException ex) {
                // Database errors wrapped in RuntimeException
                String errorMsg = ex.getMessage();
                if (errorMsg != null && errorMsg.contains("Database error:")) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", errorMsg.replace("Database error: ", ""));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + errorMsg);
                }
                ex.printStackTrace();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Delete
        btnDelete.setOnAction(e -> {
            T selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a record to delete.");
                return;
            }
            // Confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Record");
            confirmAlert.setContentText("Are you sure you want to delete this record? This action cannot be undone.");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int id = getIdFromObject(selected);
                    if (id <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid record ID. Cannot delete.");
                        return;
                    }
                    
                    boolean success = dao.delete(id);
                    
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Record deleted successfully!");
                        // Clear input fields

                                fieldInputs.values().forEach(c -> {
                                    if (c instanceof TextField tf) {
                                        tf.clear();
                                    }
                                });


                        // Update TableView: remove from ObservableList directly
                        if (observableList != null) {
                            // Remove the selected item directly from the list
                            observableList.remove(selected);
                            // Refresh table to update display
                            table.refresh();
                        } else {
                            // Fallback: reload from database
                            if (reloadCallback != null) {
                                reloadCallback.run();
                                table.refresh();
                            } else {
                                refreshTable();
                            }
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete record. It may be referenced by other records.");
                    }
                } catch (Exception ex) {
                    String errorMsg = ex.getMessage();
                    if (errorMsg != null && errorMsg.contains("foreign key") || errorMsg.contains("constraint")) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error", "Cannot delete this record. It is referenced by other records in the database.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting: " + errorMsg);
                    }
                    ex.printStackTrace();
                }
            }
        });

        // Clear
        btnClear.setOnAction(e ->
                fieldInputs.values().forEach(c -> {
                    if (c instanceof TextField tf) {
                        tf.clear();
                    }
                })
        );


        // Table selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) return;
            try {
                for (Field f : fieldInputs.keySet()) {
                    f.setAccessible(true);
                    Object v = f.get(val);
                    Object control = fieldInputs.get(f);
                    if (v != null) {
                        // Handle property objects
                        if (control instanceof ComboBox) {
                            ComboBox<String> comboBox = (ComboBox<String>) control;
                            if (v != null) {
                                if (v instanceof javafx.beans.property.Property) {
                                    v = ((javafx.beans.property.Property<?>) v).getValue();
                                }
                                comboBox.setValue(v.toString());
                            } else {
                                comboBox.setValue(null);
                            }
                        } else if (control instanceof TextField) {
                            TextField textField = (TextField) control;
                            if (v != null) {
                                if (v instanceof javafx.beans.property.Property) {
                                    v = ((javafx.beans.property.Property<?>) v).getValue();
                                }
                                textField.setText(v.toString());
                            } else {
                                textField.setText("");
                            }
                        }
                    }}}
                     catch (Exception ex) {
                        ex.printStackTrace();
                    }

        return form;
    }

    /**
     * Safe refresh method that never shows alerts
     * Silently handles any errors and ensures table is always in a valid state
     */
    private void refreshTable() {
        try {
            // Ensure table is not null
            if (table == null) {
                System.err.println("Warning: Table is null, cannot refresh");
                return;
            }
            
            // Ensure dao is not null
            if (dao == null) {
                System.err.println("Warning: DAO is null, cannot refresh");
                return;
            }
            
            // Get all items from DAO - ensure it never returns null
            java.util.List<T> allItems = dao.getAll();
            if (allItems == null) {
                // If DAO returns null, use empty list
                allItems = new java.util.ArrayList<>();
                System.err.println("Warning: DAO.getAll() returned null, using empty list");
            }
            
            // Safely update table items
            if (table.getItems() != null) {
                table.getItems().clear();
                table.getItems().setAll(allItems);
            } else {
                // If items list is null, initialize it
                javafx.collections.ObservableList<T> items = javafx.collections.FXCollections.observableArrayList(allItems);
                table.setItems(items);
            }
            
            // Force table refresh
            table.refresh();
            
            // Clear selection safely
            if (table.getSelectionModel() != null) {
                table.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            // Silently log error - NEVER show alert during refresh
            System.err.println("Error refreshing table (silent): " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            e.printStackTrace();
            
            // Ensure table is in a valid state even if refresh failed
            try {
                if (table != null && table.getItems() != null) {
                    // Try to set empty list as fallback
                    table.getItems().clear();
                }
            } catch (Exception ex) {
                // Ignore fallback errors
                System.err.println("Error in refresh fallback: " + ex.getMessage());
            }
        }
    }

    private void setFieldValue(Object obj, Field f, String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return; // Skip empty values
        }
        
        f.setAccessible(true);
        Class<?> fieldType = f.getType();
        
        // Handle JavaFX properties
        Object fieldValue = f.get(obj);
        if (fieldValue instanceof javafx.beans.property.Property) {
            Class<?> propertyType = getPropertyType(fieldValue);
            if (propertyType == Integer.class || propertyType == int.class) {
                ((javafx.beans.property.IntegerProperty) fieldValue).set(Integer.parseInt(value));
            } else if (propertyType == Double.class || propertyType == double.class) {
                ((javafx.beans.property.DoubleProperty) fieldValue).set(Double.parseDouble(value));
            } else if (propertyType == String.class) {
                ((javafx.beans.property.StringProperty) fieldValue).set(value);
            }
        } else {
            // Direct field setting (fallback)
            if (fieldType == int.class || fieldType == Integer.class) {
                f.set(obj, Integer.parseInt(value));
            } else if (fieldType == double.class || fieldType == Double.class) {
                f.set(obj, Double.parseDouble(value));
            } else {
                f.set(obj, value);
            }
        }
    }

    private Class<?> getPropertyType(Object property) {
        if (property instanceof javafx.beans.property.IntegerProperty) return Integer.class;
        if (property instanceof javafx.beans.property.DoubleProperty) return Double.class;
        if (property instanceof javafx.beans.property.StringProperty) return String.class;
        return String.class; // default
    }

    private int getIdFromObject(T obj) throws Exception {
        // Try to find ID field (author_id, book_id, borrower_id, etc.)
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().toLowerCase().endsWith("_id") || f.getName().equalsIgnoreCase("id")) {
                f.setAccessible(true);
                Object value = f.get(obj);
                if (value instanceof javafx.beans.property.IntegerProperty) {
                    return ((javafx.beans.property.IntegerProperty) value).get();
                } else if (value instanceof Integer) {
                    return (Integer) value;
                }
            }
        }
        return -1;
    }

    /**
     * Validate required fields based on NOT NULL constraints
     */
    private String validateRequiredFields(Map<Field, Control> fieldInputs) {
        String className = clazz.getSimpleName();
        
        // Author: first_name, last_name are NOT NULL
        if (className.equals("Author")) {
            String firstName = getFieldValueByName("first_name", fieldInputs);
            String lastName = getFieldValueByName("last_name", fieldInputs);
            if (firstName == null || firstName.trim().isEmpty()) {
                return "First name is required.";
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                return "Last name is required.";
            }
        }
        // Book: title is NOT NULL
        else if (className.equals("Book")) {
            String title = getFieldValueByName("title", fieldInputs);
            if (title == null || title.trim().isEmpty()) {
                return "Title is required.";
            }
        }
        // Borrower: first_name, last_name, type_id are NOT NULL
        else if (className.equals("Borrower")) {
            String firstName = getFieldValueByName("first_name", fieldInputs);
            String lastName = getFieldValueByName("last_name", fieldInputs);
            String typeId = getFieldValueByName("type_id", fieldInputs);
            if (firstName == null || firstName.trim().isEmpty()) {
                return "First name is required.";
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                return "Last name is required.";
            }
            if (typeId == null || typeId.trim().isEmpty()) {
                return "Type ID is required.";
            }
            try {
                if (Integer.parseInt(typeId) <= 0) {
                    return "Type ID must be greater than 0.";
                }
            } catch (NumberFormatException e) {
                return "Type ID must be a valid number.";
            }
        }
        return null; // No validation errors
    }

    private String getFieldValueByName(String fieldName, Map<Field, Control> fieldInputs) {
        for (Map.Entry<Field, Control> entry : fieldInputs.entrySet()) {
            if (entry.getKey().getName().equals(fieldName)) {
                return ((TextField) entry.getValue()).getText().trim();
            }
        }
        return null;
    }

    /**
     * Validate field formats (numbers, etc.)
     */
    private boolean validateFields(Map<Field, Control> fieldInputs) {
        for (Map.Entry<Field, Control> entry : fieldInputs.entrySet()) {
            Field f = entry.getKey();
            String value="";
            if (entry.getValue() instanceof TextField) {
            TextField tf = (TextField) entry.getValue();
                value = tf.getText().trim();}
            else {
                ComboBox<String> tf=(ComboBox<String>) entry.getValue();
                value= String.valueOf(new BookDAO().getIDbyString(tf.getValue()));
            }

            
            // Skip ID fields (auto-generated)
            if (f.getName().toLowerCase().endsWith("_id") && f.getName().toLowerCase().contains("id")) {
                continue;
            }
            
            // Check if numeric fields have valid numbers when not empty
            Class<?> fieldType = f.getType();
            if (fieldType == int.class || fieldType == Integer.class || 
                fieldType == double.class || fieldType == Double.class) {
                if (!value.isEmpty()) {
                    try {
                        if (fieldType == double.class || fieldType == Double.class) {
                            double num = Double.parseDouble(value);
                            if (num < 0 && f.getName().toLowerCase().contains("price")) {
                                return false; // Prices should be non-negative
                            }
                        } else {
                            int num = Integer.parseInt(value);
                            if (num < 0) {
                                return false; // IDs and counts should be non-negative
                            }
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        // Handle snake_case
        if (s.contains("_")) {
            String[] parts = s.split("_");
            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    result.append(part.substring(0, 1).toUpperCase())
                          .append(part.substring(1).toLowerCase())
                          .append(" ");
                }
            }
            return result.toString().trim();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @SuppressWarnings("unchecked")
    private T createNewInstance(Map<Field, Control> fieldInputs) throws Exception {
        // Get all declared fields in order
        Field[] fields = clazz.getDeclaredFields();
        java.util.List<Object> constructorArgs = new java.util.ArrayList<>();
        java.util.List<Class<?>> paramTypes = new java.util.ArrayList<>();
        
        for (Field f : fields) {
            f.setAccessible(true);
            Class<?> fieldType = f.getType();
            String fieldName = f.getName();
            String value = getFieldValue(f, fieldInputs);
            
            // Handle JavaFX properties - get the actual type
            if (javafx.beans.property.Property.class.isAssignableFrom(fieldType)) {
                // Determine the property type
                if (javafx.beans.property.IntegerProperty.class.isAssignableFrom(fieldType)) {
                    paramTypes.add(int.class);

                        constructorArgs.add(value != null && !value.isEmpty() ? Integer.parseInt(value) : 0);

                } else if (javafx.beans.property.DoubleProperty.class.isAssignableFrom(fieldType)) {
                    paramTypes.add(double.class);
                    constructorArgs.add(value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0);
                } else if (javafx.beans.property.StringProperty.class.isAssignableFrom(fieldType)) {
                    paramTypes.add(String.class);
                    // For nullable fields, use empty string (will be converted to NULL in DAO)
                    constructorArgs.add(value != null && !value.isEmpty() ? value : "");
                } else if (javafx.beans.property.ObjectProperty.class.isAssignableFrom(fieldType)) {
                    // For LocalDate, etc. - try to parse or use null
                    paramTypes.add(java.time.LocalDate.class);
                    if (value != null && !value.isEmpty()) {
                        try {
                            constructorArgs.add(java.time.LocalDate.parse(value));
                        } catch (Exception e) {
                            constructorArgs.add(null);
                        }
                    } else {
                        constructorArgs.add(null);
                    }
                } else {
                    paramTypes.add(String.class);
                    constructorArgs.add(value != null ? value : "");
                }
            } else {
                // Direct field types
                paramTypes.add(fieldType);
                if (fieldType == int.class || fieldType == Integer.class) {
                    if (fieldName.toLowerCase().endsWith("_id")) {
                        constructorArgs.add(0);
                    } else {
                        constructorArgs.add(value != null && !value.isEmpty() ? Integer.parseInt(value) : 0);
                    }
                } else if (fieldType == double.class || fieldType == Double.class) {
                    constructorArgs.add(value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0);
                } else if (fieldType == String.class) {
                    constructorArgs.add(value != null && !value.isEmpty() ? value : "");
                } else {
                    constructorArgs.add(null);
                }
            }
        }
        
        // Find and call constructor
        java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(paramTypes.toArray(new Class[0]));
        return (T) constructor.newInstance(constructorArgs.toArray());
    }

    private String getFieldValue(Field f, Map<Field, Control> fieldInputs) {
        for (Map.Entry<Field, Control> entry : fieldInputs.entrySet()) {
            if (entry.getKey().getName().equals(f.getName())) {
                return ((TextField) entry.getValue()).getText().trim();
        }}
        return "";
    }
}
