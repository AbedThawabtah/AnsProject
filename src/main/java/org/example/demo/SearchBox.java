package org.example.demo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.lang.reflect.Field;

public class SearchBox<E> {

	public <T> HBox createSearchBox(Class<T> clazz,
									ObservableList<T> list,
									TableView<T> table) {

		if (list == null)
			list = FXCollections.observableArrayList();

		FilteredList<T> filtered = new FilteredList<>(list, p -> true);

		TextField tf = new TextField();
		tf.setPromptText("Search...");
		tf.setMinWidth(220);

		// Listener
		tf.textProperty().addListener((obs, ov, nv) -> {
			String txt = (nv == null ? "" : nv.toLowerCase().trim());

			filtered.setPredicate(item -> {
				if (txt.isEmpty()) return true;

				// Loop on fields using reflection
				for (Field f : clazz.getDeclaredFields()) {
					try {
						f.setAccessible(true);
						Object value = f.get(item);   // get field value

						if (value == null) continue;
						String v = value.toString().toLowerCase();

						if (v.contains(txt))
							return true;

					} catch (Exception ignored) {}
				}
				return false;
			});
		});

		table.setItems(filtered);

		Button btnSearch = new Button("Search");
		btnSearch.setOnAction(e -> table.refresh());

		HBox box = new HBox(8, new Label("Search:"), tf, btnSearch);
		box.setPadding(new Insets(10));
		box.setAlignment(Pos.TOP_RIGHT);

		return box;
	}


}
