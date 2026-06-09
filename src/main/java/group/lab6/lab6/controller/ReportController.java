package group.lab6.lab6.controller;
import group.lab6.lab6.service.ReportService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Map<String, Object>> reportTable;
    @FXML private TableColumn<Map<String, Object>, String> saleDateColumn;
    @FXML private TableColumn<Map<String, Object>, String> checkNumberColumn;
    @FXML private TableColumn<Map<String, Object>, String> artistColumn;
    @FXML private TableColumn<Map<String, Object>, String> albumColumn;
    @FXML private TableColumn<Map<String, Object>, String> priceColumn;
    @FXML private TableColumn<Map<String, Object>, String> paymentMethodColumn;
    @FXML private Label collectionValueLabel;
    @FXML private Label statusLabel;

    private ReportService reportService;
    private ObservableList<Map<String, Object>> reportData = FXCollections.observableArrayList();

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        // Устанавливаем даты по умолчанию (последний месяц)
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Настройка колонок для таблицы
        saleDateColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("sale_date");
            return new SimpleStringProperty(value != null ? value.toString() : "");
        });
        checkNumberColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("check_number");
            return new SimpleStringProperty(value != null ? value.toString() : "");
        });
        artistColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("artist");
            return new SimpleStringProperty(value != null ? value.toString() : "");
        });
        albumColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("album_title");
            return new SimpleStringProperty(value != null ? value.toString() : "");
        });
        priceColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("final_price");
            return new SimpleStringProperty(value != null ? String.format("%.2f ₽", ((Number) value).doubleValue()) : "");
        });
        paymentMethodColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("payment_method");
            String method = value != null ? value.toString() : "";
            return new SimpleStringProperty(translatePaymentMethod(method));
        });

        reportTable.setItems(reportData);

        // Загружаем текущую стоимость коллекции
        handleRefreshCollectionValue();
        // Загружаем отчёт за период по умолчанию
        handleGenerateReport();
    }

    @FXML
    private void handleRefreshCollectionValue() {
        if (reportService == null) {
            statusLabel.setText("Сервис не инициализирован");
            return;
        }

        try {
            BigDecimal total = reportService.getCollectionValue();
            collectionValueLabel.setText(String.format("%.2f ₽", total));
            statusLabel.setText("Стоимость коллекции обновлена");
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReport() {
        if (reportService == null) {
            statusLabel.setText("Сервис не инициализирован");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null) {
            statusLabel.setText("Выберите дату начала");
            return;
        }
        if (endDate == null) {
            statusLabel.setText("Выберите дату окончания");
            return;
        }
        if (startDate.isAfter(endDate)) {
            statusLabel.setText("Дата начала не может быть позже даты окончания");
            return;
        }

        try {
            List<Map<String, Object>> results = reportService.getSoldReport(startDate, endDate);
            reportData.clear();
            reportData.addAll(results);
            statusLabel.setText("Найдено продаж: " + results.size());
        } catch (ValidationException e) {
            statusLabel.setText("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveReport() {
        if (reportData.isEmpty()) {
            statusLabel.setText("Нет данных для сохранения.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчёт");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );
        fileChooser.setInitialFileName("report_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt");

        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Заголовок отчёта
                writer.println("        ОТЧЁТ ПО ПРОДАЖАМ");
                writer.println("Период: " + startDatePicker.getValue() + " - " + endDatePicker.getValue());
                writer.println("Дата формирования: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                writer.printf("%-12s %-12s %-20s %-25s %-10s %-12s%n",
                        "Дата", "Чек №", "Исполнитель", "Альбом", "Цена", "Оплата");

                // Данные
                for (Map<String, Object> row : reportData) {
                    String date = row.get("sale_date") != null ? row.get("sale_date").toString() : "";
                    String checkNumber = row.get("check_number") != null ? row.get("check_number").toString() : "";
                    String artist = row.get("artist") != null ? row.get("artist").toString() : "";
                    String album = row.get("album_title") != null ? row.get("album_title").toString() : "";
                    String price = row.get("final_price") != null ? String.format("%.2f", ((Number) row.get("final_price")).doubleValue()) : "0.00";
                    String paymentMethod = row.get("payment_method") != null ? translatePaymentMethod(row.get("payment_method").toString()) : "";

                    writer.printf("%-12s %-12s %-20s %-25s %-10s %-12s%n",
                            date, checkNumber,
                            truncate(artist, 20),
                            truncate(album, 25),
                            price + " ₽",
                            paymentMethod);
                }

                writer.println("ИТОГО ПРОДАНО: " + reportData.size() + " позиций");

                statusLabel.setText("Отчёт сохранён: " + file.getName());

            } catch (IOException e) {
                statusLabel.setText("Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveCollectionValue() {
        if (collectionValueLabel.getText().isEmpty()) {
            statusLabel.setText("Нет данных для сохранения");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить стоимость коллекции");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );
        fileChooser.setInitialFileName("collection_value_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt");

        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("   СТОИМОСТЬ КОЛЛЕКЦИИ ВИНИЛОВЫХ ПЛАСТИНОК");
                writer.println("Дата: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                writer.println("Общая стоимость: " + collectionValueLabel.getText());
                statusLabel.setText("Стоимость сохранена: " + file.getName());
            } catch (IOException e) {
                statusLabel.setText("Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private String translatePaymentMethod(String method) {
        switch (method) {
            case "cash": return "Наличные";
            case "card": return "Карта";
            case "online": return "Онлайн";
            default: return method;
        }
    }
}