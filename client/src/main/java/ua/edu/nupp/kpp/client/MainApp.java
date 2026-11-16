package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    private final HttpEnergyAuditClient api = new HttpEnergyAuditClient();

    private final TableView<ObjectItem> objectTable = new TableView<>();
    private final TextField objCode = new TextField();
    private final TextField objName = new TextField();
    private final TextField objAddress = new TextField();

    private final TableView<EnergyAudit> auditTable = new TableView<>();
    private final TextField audObjectId = new TextField();
    private final TextField audDate = new TextField();
    private final TextField audAuditor = new TextField();
    private final TextField audNotes = new TextField();

    private final TableView<Measure> measureTable = new TableView<>();
    private final TextField mAuditId = new TextField();
    private final TextField mTitle = new TextField();
    private final TextField mF1 = new TextField();
    private final TextField mF2 = new TextField();
    private final TextField mF3 = new TextField();
    private final TextField mIndex = new TextField();
    private final ComboBox<MeasureState> mState = new ComboBox<>();

    private final TableView<ConsumptionRecord> recordTable = new TableView<>();
    private final TextField recObjectId = new TextField();
    private final TextField recResource = new TextField();
    private final TextField recValue = new TextField();
    private final TextField recPeriod = new TextField();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(objectsTab(), auditsTab(), measuresTab(), recordsTab());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        stage.setTitle("Планування заходів енергоаудиту (Клієнт)");
        stage.setScene(new Scene(tabs, 1000, 620));
        stage.show();

        Platform.runLater(() -> safe(this::refreshAll));
    }

    private Tab objectsTab() {
        addColumn(objectTable, "ID", "id");
        addColumn(objectTable, "Код", "code");
        addColumn(objectTable, "Назва", "name");
        addColumn(objectTable, "Адреса", "address");

        objectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, o) -> {
            if (o != null) {
                objCode.setText(o.getCode());
                objName.setText(o.getName());
                objAddress.setText(o.getAddress());
            }
        });

        GridPane form = newForm();
        addRow(form, 0, "Код", objCode);
        addRow(form, 1, "Назва", objName);
        addRow(form, 2, "Адреса", objAddress);

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addObject(new ObjectItem(null, objCode.getText(), objName.getText(), objAddress.getText()));
            refreshObjects();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            ObjectItem sel = selected(objectTable);
            api.updateObject(new ObjectItem(sel.getId(), objCode.getText(), objName.getText(), objAddress.getText()));
            refreshObjects();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            ObjectItem sel = selected(objectTable);
            api.deleteObject(sel.getId());
            clear(objCode, objName, objAddress);
            refreshAll(); // залежні аудити, заходи й записи видаляються разом з об'єктом
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        return tab("Об'єкти", objectTable, form, new HBox(10, add, update, delete, refresh));
    }

    private Tab auditsTab() {
        addColumn(auditTable, "ID", "id");
        addColumn(auditTable, "Об'єкт (ID)", "objectId");
        addColumn(auditTable, "Дата", "auditDate");
        addColumn(auditTable, "Аудитор", "auditor");
        addColumn(auditTable, "Примітки", "notes");

        audDate.setPromptText("РРРР-ММ-ДД");

        auditTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, a) -> {
            if (a != null) {
                audObjectId.setText(String.valueOf(a.getObjectId()));
                audDate.setText(a.getAuditDate());
                audAuditor.setText(a.getAuditor());
                audNotes.setText(a.getNotes());
            }
        });

        GridPane form = newForm();
        addRow(form, 0, "Об'єкт (ID)", audObjectId);
        addRow(form, 1, "Дата", audDate);
        addRow(form, 2, "Аудитор", audAuditor);
        addRow(form, 3, "Примітки", audNotes);

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addAudit(readAudit(null));
            refreshAudits();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            EnergyAudit sel = selected(auditTable);
            api.updateAudit(readAudit(sel.getId()));
            refreshAudits();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            EnergyAudit sel = selected(auditTable);
            api.deleteAudit(sel.getId());
            clear(audObjectId, audDate, audAuditor, audNotes);
            refreshAll();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        return tab("Енергоаудити", auditTable, form, new HBox(10, add, update, delete, refresh));
    }

    private EnergyAudit readAudit(Long id) {
        EnergyAudit a = new EnergyAudit();
        a.setId(id);
        a.setObjectId(parseLong(audObjectId, "Об'єкт (ID)"));
        a.setAuditDate(audDate.getText());
        a.setAuditor(audAuditor.getText());
        a.setNotes(audNotes.getText());
        return a;
    }

    private Tab measuresTab() {
        addColumn(measureTable, "ID", "id");
        addColumn(measureTable, "Аудит (ID)", "auditId");
        addColumn(measureTable, "Назва", "title");
        addColumn(measureTable, "F1", "f1");
        addColumn(measureTable, "F2", "f2");
        addColumn(measureTable, "F3", "f3");
        addColumn(measureTable, "Інтегральний показник", "integralIndex");
        addColumn(measureTable, "Стан", "state");

        mState.setItems(FXCollections.observableArrayList(MeasureState.values()));
        mIndex.setEditable(false); // обчислюється сервером
        mIndex.setPromptText("обчислює сервер");

        measureTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, m) -> {
            if (m != null) {
                mAuditId.setText(String.valueOf(m.getAuditId()));
                mTitle.setText(m.getTitle());
                mF1.setText(String.valueOf(m.getF1()));
                mF2.setText(String.valueOf(m.getF2()));
                mF3.setText(String.valueOf(m.getF3()));
                mIndex.setText(String.valueOf(m.getIntegralIndex()));
                mState.setValue(m.getState());
            }
        });

        GridPane form = newForm();
        addRow(form, 0, "Аудит (ID)", mAuditId);
        addRow(form, 1, "Назва", mTitle);
        addRow(form, 2, "F1 (потенціал економії, 0–100)", mF1);
        addRow(form, 3, "F2 (привабливість строку окупності, 0–100)", mF2);
        addRow(form, 4, "F3 (потенціал скорочення викидів, 0–100)", mF3);
        addRow(form, 5, "Інтегральний показник", mIndex);
        addRow(form, 6, "Стан", mState);

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addMeasure(readMeasure(null));
            refreshMeasures();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            Measure sel = selected(measureTable);
            api.updateMeasure(readMeasure(sel.getId()));
            refreshMeasures();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            Measure sel = selected(measureTable);
            api.deleteMeasure(sel.getId());
            clear(mAuditId, mTitle, mF1, mF2, mF3, mIndex);
            refreshMeasures();
        }));
        Button changeState = new Button("Змінити стан");
        changeState.setOnAction(e -> safe(() -> {
            Measure sel = selected(measureTable);
            MeasureState next = mState.getValue();
            if (next == null) {
                throw new IllegalArgumentException("Виберіть стан");
            }
            api.changeMeasureState(sel.getId(), next);
            refreshMeasures();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        return tab("Заходи", measureTable, form, new HBox(10, add, update, delete, changeState, refresh));
    }

    private Measure readMeasure(Long id) {
        Measure m = new Measure();
        m.setId(id);
        m.setAuditId(parseLong(mAuditId, "Аудит (ID)"));
        m.setTitle(mTitle.getText());
        m.setF1(parseDouble(mF1, "F1"));
        m.setF2(parseDouble(mF2, "F2"));
        m.setF3(parseDouble(mF3, "F3"));
        return m;
    }

    private Tab recordsTab() {
        addColumn(recordTable, "ID", "id");
        addColumn(recordTable, "Об'єкт (ID)", "objectId");
        addColumn(recordTable, "Ресурс", "resourceType");
        addColumn(recordTable, "Споживання", "consumptionValue");
        addColumn(recordTable, "Період", "period");

        recPeriod.setPromptText("РРРР-ММ");

        recordTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, r) -> {
            if (r != null) {
                recObjectId.setText(String.valueOf(r.getObjectId()));
                recResource.setText(r.getResourceType());
                recValue.setText(String.valueOf(r.getConsumptionValue()));
                recPeriod.setText(r.getPeriod());
            }
        });

        GridPane form = newForm();
        addRow(form, 0, "Об'єкт (ID)", recObjectId);
        addRow(form, 1, "Ресурс", recResource);
        addRow(form, 2, "Споживання", recValue);
        addRow(form, 3, "Період", recPeriod);

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addRecord(readRecord(null));
            refreshRecords();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            ConsumptionRecord sel = selected(recordTable);
            api.updateRecord(readRecord(sel.getId()));
            refreshRecords();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            ConsumptionRecord sel = selected(recordTable);
            api.deleteRecord(sel.getId());
            clear(recObjectId, recResource, recValue, recPeriod);
            refreshRecords();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));
        Button importOne = new Button("Імпорт 1 рядка CSV");
        importOne.setOnAction(e -> safe(() -> {
            api.addRecord(readTestRecord());
            refreshRecords();
        }));

        return tab("Споживання", recordTable, form, new HBox(10, add, update, delete, refresh, importOne));
    }

    private ConsumptionRecord readRecord(Long id) {
        ConsumptionRecord r = new ConsumptionRecord();
        r.setId(id);
        r.setObjectId(parseLong(recObjectId, "Об'єкт (ID)"));
        r.setResourceType(recResource.getText());
        r.setConsumptionValue(parseDouble(recValue, "Споживання"));
        r.setPeriod(recPeriod.getText());
        return r;
    }

    private ConsumptionRecord readTestRecord() {
        Path file = Path.of("sample-data", "consumption_records.csv");
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося прочитати файл " + file + ": " + e.getMessage(), e);
        }
        if (lines.size() < 2) {
            throw new IllegalArgumentException("У CSV-файлі немає рядків даних");
        }
        String[] parts = lines.get(1).split(";", -1); // перший рядок — заголовок, береться наступний
        if (parts.length != 4) {
            throw new IllegalArgumentException("Некоректна кількість стовпців: " + parts.length);
        }
        try {
            long objectId = Long.parseLong(parts[0].trim());
            String resourceType = parts[1].trim();
            double value = Double.parseDouble(parts[2].trim().replace(',', '.'));
            String period = parts[3].trim();
            return new ConsumptionRecord(null, objectId, resourceType, value, period);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некоректне число в CSV-рядку: " + lines.get(1));
        }
    }

    private void refreshObjects() {
        objectTable.setItems(FXCollections.observableArrayList(api.getObjects()));
    }

    private void refreshAudits() {
        auditTable.setItems(FXCollections.observableArrayList(api.getAudits()));
    }

    private void refreshMeasures() {
        measureTable.setItems(FXCollections.observableArrayList(api.getMeasures()));
    }

    private void refreshRecords() {
        recordTable.setItems(FXCollections.observableArrayList(api.getRecords()));
    }

    private void refreshAll() {
        refreshObjects();
        refreshAudits();
        refreshMeasures();
        refreshRecords();
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Операція не виконана", e);
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message == null || message.isBlank() ? "Невідома помилка" : message);
        alert.showAndWait();
    }

    private <T> T selected(TableView<T> table) {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Виберіть запис");
        }
        return item;
    }

    private long parseLong(TextField field, String name) {
        try {
            return Long.parseLong(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» повинно бути цілим числом");
        }
    }

    private double parseDouble(TextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» повинно бути числом");
        }
    }

    private void clear(TextField... fields) {
        for (TextField f : fields) {
            f.clear();
        }
    }

    private <S> void addColumn(TableView<S> table, String title, String property) {
        TableColumn<S, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        table.getColumns().add(column);
    }

    private GridPane newForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, Node control) {
        grid.add(new javafx.scene.control.Label(label), 0, row);
        grid.add(control, 1, row);
        GridPane.setHgrow(control, Priority.ALWAYS);
        if (control instanceof TextField tf) {
            tf.setMaxWidth(Double.MAX_VALUE);
        }
    }

    private Tab tab(String title, TableView<?> table, GridPane form, HBox buttons) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox box = new VBox(10, table, form, buttons);
        box.setPadding(new Insets(10));
        return new Tab(title, box);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
