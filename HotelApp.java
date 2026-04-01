import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

// ENUM
enum RoomType {
    SINGLE(1500), DOUBLE(2500), TRIPLE(4000), DELUXE(5000), SUITE(8000);

    int price;

    RoomType(int p) {
        price = p;
    }

    int getPrice() {
        return price;
    }
}

// ROOM CLASS
class Room {
    int id;
    RoomType type;
    boolean available = true;
    String customer = "";

    Room(int id, RoomType type) {
        this.id = id;
        this.type = type;
    }

    String show() {
        return "\n------------------------\n" +
                "Room ID : " + id + "\n" +
                "Type    : " + type + "\n" +
                "Price   : ₹" + type.getPrice() + "\n" +
                "Status  : " + (available ? "Available" : "Booked by " + customer) +
                "\n------------------------\n";
    }
}

// MANAGER
class Manager<T extends Room> {
    private ArrayList<T> list = new ArrayList<>();

    public synchronized void add(T r) {
        list.add(r);
    }

    public synchronized boolean book(int id, String name) {
        for (T r : list) {
            if (r.id == id && r.available) {
                r.available = false;
                r.customer = name;
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkout(int id) {
        for (T r : list) {
            if (r.id == id && !r.available) {
                r.available = true;
                r.customer = "";
                return true;
            }
        }
        return false;
    }

    public ArrayList<T> getAll() {
        return list;
    }

    public T find(int id) {
        for (T r : list)
            if (r.id == id) return r;
        return null;
    }
}

// MAIN
public class HotelApp extends Application {

    Manager<Room> manager = new Manager<>();

    TextArea viewArea = new TextArea();
    TextArea bookArea = new TextArea();

    void msg(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(m);
        a.showAndWait();
    }

    void runThread(Runnable r) {
        new Thread(r).start();
    }

    @Override
    public void start(Stage stage) {

        TabPane tabs = new TabPane();

        viewArea.setEditable(false);
        bookArea.setEditable(false);

        // Common Title
        Label title = new Label("Hotel Management System");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ===== ADD TAB =====
        VBox addBox = new VBox(10);
        addBox.setPadding(new Insets(10));

        Label addTitle = new Label("Hotel Management System");

        TextField idField = new TextField();
        idField.setPromptText("Room ID");

        ComboBox<RoomType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(RoomType.values());
        typeBox.setPromptText("Select Type");

        Button addBtn = new Button("Add Room");

        addBtn.setOnAction(e -> {
            try {
                if (idField.getText().isEmpty() || typeBox.getValue() == null) {
                    msg("Fill all fields");
                    return;
                }

                int id = Integer.parseInt(idField.getText());

                if (manager.find(id) != null) {
                    msg("ID already exists");
                    return;
                }

                runThread(() -> manager.add(new Room(id, typeBox.getValue())));

                msg("Room added");

                idField.clear();
                typeBox.setValue(null);

            } catch (Exception ex) {
                msg("Invalid input");
            }
        });

        addBox.getChildren().addAll(title, new Label("Add Room"), idField, typeBox, addBtn);

        // ===== VIEW TAB =====
        VBox viewBox = new VBox(10);
        viewBox.setPadding(new Insets(10));

        Button all = new Button("All");
        Button booked = new Button("Booked");
        Button available = new Button("Available");

        all.setOnAction(e -> {
            viewArea.clear();
            for (Room r : manager.getAll())
                viewArea.appendText(r.show());
        });

        booked.setOnAction(e -> {
            viewArea.clear();
            for (Room r : manager.getAll())
                if (!r.available)
                    viewArea.appendText(r.show());
        });

        available.setOnAction(e -> {
            viewArea.clear();
            for (Room r : manager.getAll())
                if (r.available)
                    viewArea.appendText(r.show());
        });

        viewBox.getChildren().addAll(title, all, booked, available, viewArea);

        // ===== SEARCH TAB =====
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(10));

        TextField search = new TextField();
        search.setPromptText("Enter Room ID");

        Button searchBtn = new Button("Search");

        searchBtn.setOnAction(e -> {
            try {
                if (search.getText().isEmpty()) {
                    msg("Enter ID");
                    return;
                }

                int id = Integer.parseInt(search.getText());
                Room r = manager.find(id);

                if (r != null)
                    msg(r.show());
                else
                    msg("Room not found");

                search.clear();

            } catch (Exception ex) {
                msg("Invalid input");
            }
        });

        searchBox.getChildren().addAll(title, new Label("Search Room"), search, searchBtn);

        // ===== BOOK TAB =====
        VBox bookBox = new VBox(10);
        bookBox.setPadding(new Insets(10));

        ComboBox<RoomType> filter = new ComboBox<>();
        filter.getItems().addAll(RoomType.values());
        filter.setPromptText("Check Type");

        Button showBtn = new Button("Show Available");

        showBtn.setOnAction(e -> {
            if (filter.getValue() == null) {
                msg("Select type");
                return;
            }

            bookArea.clear();
            boolean found = false;

            for (Room r : manager.getAll()) {
                if (r.available && r.type == filter.getValue()) {
                    bookArea.appendText(r.show());
                    found = true;
                }
            }

            if (!found)
                bookArea.setText("No rooms available");
        });

        TextField bookId = new TextField();
        bookId.setPromptText("Room ID");

        TextField name = new TextField();
        name.setPromptText("Customer Name");

        Button bookBtn = new Button("Book");

        bookBtn.setOnAction(e -> {
            try {
                if (bookId.getText().isEmpty() || name.getText().trim().isEmpty()) {
                    msg("Fill all fields");
                    return;
                }

                int id = Integer.parseInt(bookId.getText());

                runThread(() -> {
                    boolean ok = manager.book(id, name.getText());
                    if (ok)
                        msg("Booked for " + name.getText());
                    else
                        msg("Room not available");
                });

                bookId.clear();
                name.clear();

            } catch (Exception ex) {
                msg("Invalid input");
            }
        });

        bookBox.getChildren().addAll(
                title,
                new Label("Book Room"),
                filter, showBtn,
                bookArea,
                bookId, name,
                bookBtn
        );

        // ===== CHECKOUT TAB =====
        VBox outBox = new VBox(10);
        outBox.setPadding(new Insets(10));

        TextField outId = new TextField();
        outId.setPromptText("Room ID");

        Button outBtn = new Button("Checkout");

        outBtn.setOnAction(e -> {
            try {
                if (outId.getText().isEmpty()) {
                    msg("Enter ID");
                    return;
                }

                int id = Integer.parseInt(outId.getText());

                runThread(() -> {
                    boolean ok = manager.checkout(id);
                    if (ok)
                        msg("Checkout done");
                    else
                        msg("Room not booked");
                });

                outId.clear();

            } catch (Exception ex) {
                msg("Invalid input");
            }
        });

        outBox.getChildren().addAll(title, new Label("Checkout"), outId, outBtn);

        // Tabs
        tabs.getTabs().addAll(
                new Tab("Add", addBox),
                new Tab("View", viewBox),
                new Tab("Search", searchBox),
                new Tab("Book", bookBox),
                new Tab("Checkout", outBox)
        );

        stage.setScene(new Scene(tabs, 550, 450));
        stage.setTitle("Hotel Management System");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}