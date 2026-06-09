package group.lab6.lab6.controller;
import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.Photo;
import group.lab6.lab6.model.DefectPhoto;
import group.lab6.lab6.service.PhotoService;
import group.lab6.lab6.service.VinylService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PhotoManagerController {

    @FXML private Label instanceInfoLabel;
    @FXML private Label coverConditionLabel;
    @FXML private Label vinylConditionLabel;
    @FXML private TilePane photoTilePane;
    @FXML private TilePane defectPhotoTilePane;
    @FXML private Label statusLabel;

    private PhotoService photoService;
    private VinylService vinylService;
    private Instance currentInstance;
    private Integer currentUsedDetailsId;

    // Для хранения выбранных фото (для операций редактирования/архивации)
    private Photo selectedPhoto;
    private DefectPhoto selectedDefectPhoto;

    public void setPhotoService(PhotoService photoService) {
        this.photoService = photoService;
    }

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void setInstance(Instance instance) {
        this.currentInstance = instance;
        if (instance != null) {
            instanceInfoLabel.setText(String.format("Экземпляр: %s - %s",
                    instance.getRelease().getArtist(),
                    instance.getRelease().getAlbumTitle()));

            if (instance.getUsedDetails() != null && instance.getUsedDetails().getUsedDetailsId() != null) {
                currentUsedDetailsId = instance.getUsedDetails().getUsedDetailsId().intValue();
                coverConditionLabel.setText(instance.getUsedDetails().getCoverCondition().getRusName());
                vinylConditionLabel.setText(instance.getUsedDetails().getVinylCondition().getRusName());
            } else {
                currentUsedDetailsId = null;
                coverConditionLabel.setText("Новый экземпляр");
                vinylConditionLabel.setText("Новый экземпляр");
            }
            loadPhotos();
        }
    }

    @FXML
    public void initialize() {
        // Настройка TilePane
        photoTilePane.setPrefColumns(3);
        photoTilePane.setHgap(10);
        photoTilePane.setVgap(10);

        defectPhotoTilePane.setPrefColumns(3);
        defectPhotoTilePane.setHgap(10);
        defectPhotoTilePane.setVgap(10);
    }

    @FXML
    private void handleRefresh() {
        loadPhotos();
    }

    @FXML
    private void handleAddPhoto() {
        if (currentInstance == null) {
            statusLabel.setText("Нет выбранного экземпляра");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фото");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File file = fileChooser.showOpenDialog(photoTilePane.getScene().getWindow());
        if (file != null) {
            String description = showDescriptionDialog();
            if (description != null) {
                try {
                    String savedPath = savePhotoToDisk(file, "instance_" + currentInstance.getInstanceId());
                    Integer photoId = photoService.addPhoto(currentInstance.getInstanceId().intValue(), savedPath, description);
                    if (photoId != null) {
                        statusLabel.setText("Фото добавлено");
                        loadPhotos();
                    } else {
                        statusLabel.setText("Ошибка при добавлении фото");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Ошибка: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleAddDefectPhoto() {
        if (currentInstance == null) {
            statusLabel.setText("Нет выбранного экземпляра");
            return;
        }

        if (currentUsedDetailsId == null) {
            statusLabel.setText("Этот экземпляр новый, фото дефектов не требуются");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фото дефекта");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File file = fileChooser.showOpenDialog(defectPhotoTilePane.getScene().getWindow());
        if (file != null) {
            String description = showDescriptionDialog();
            if (description != null) {
                try {
                    String savedPath = savePhotoToDisk(file, "defect_" + currentUsedDetailsId);
                    Integer photoId = photoService.addDefectPhoto(currentUsedDetailsId, savedPath, description);
                    if (photoId != null) {
                        statusLabel.setText("Фото дефекта добавлено");
                        loadPhotos();
                    } else {
                        statusLabel.setText("Ошибка при добавлении фото дефекта");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Ошибка: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleEditDescription() {
        if (selectedPhoto != null) {
            String newDescription = showDescriptionDialog(selectedPhoto.getPhotoDescription());
            if (newDescription != null) {
                photoService.updatePhotoDescription(selectedPhoto.getPhotoId().intValue(), newDescription);
                loadPhotos();
                statusLabel.setText("Описание обновлено");
                selectedPhoto = null;
            }
        } else if (selectedDefectPhoto != null) {
            String newDescription = showDescriptionDialog(selectedDefectPhoto.getPhotoDescription());
            if (newDescription != null) {
                photoService.updateDefectPhotoDescription(selectedDefectPhoto.getDefectPhotoId().intValue(), newDescription);
                loadPhotos();
                statusLabel.setText("Описание обновлено");
                selectedDefectPhoto = null;
            }
        } else {
            statusLabel.setText("Выберите фото для редактирования");
        }
    }

    @FXML
    private void handleArchive() {
        if (selectedPhoto != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение");
            confirm.setContentText("Заархивировать фото?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    photoService.archivePhoto(selectedPhoto.getPhotoId().intValue());
                    loadPhotos();
                    statusLabel.setText("Фото заархивировано");
                    selectedPhoto = null;
                }
            });
        } else if (selectedDefectPhoto != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение");
            confirm.setContentText("Заархивировать фото дефекта?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    photoService.archiveDefectPhoto(selectedDefectPhoto.getDefectPhotoId().intValue());
                    loadPhotos();
                    statusLabel.setText("Фото дефекта заархивировано");
                    selectedDefectPhoto = null;
                }
            });
        } else {
            statusLabel.setText("Выберите фото для архивации");
        }
    }

    private void loadPhotos() {
        if (currentInstance == null) return;

        // Очищаем панели
        photoTilePane.getChildren().clear();
        defectPhotoTilePane.getChildren().clear();

        // Сбрасываем выбранные фото
        selectedPhoto = null;
        selectedDefectPhoto = null;

        try {
            // Загружаем обычные фото
            List<Photo> photos = photoService.getPhotosByInstanceId(currentInstance.getInstanceId().intValue());
            for (Photo photo : photos) {
                VBox card = createPhotoCard(photo);
                photoTilePane.getChildren().add(card);
            }
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки фото: " + e.getMessage());
        }

        // Загружаем фото дефектов
        if (currentUsedDetailsId != null) {
            try {
                List<DefectPhoto> defectPhotos = photoService.getDefectPhotosByUsedDetailsId(currentUsedDetailsId);
                for (DefectPhoto photo : defectPhotos) {
                    VBox card = createDefectPhotoCard(photo);
                    defectPhotoTilePane.getChildren().add(card);
                }
            } catch (Exception e) {
                statusLabel.setText("Ошибка загрузки фото дефектов: " + e.getMessage());
            }
        }
    }

    private VBox createPhotoCard(Photo photo) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(5));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Добавляем эффект при наведении
        card.setOnMouseEntered(e -> card.setStyle("-fx-border-color: #0078d7; -fx-border-radius: 5; -fx-background-color: #f0f0f0;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;"));

        // Клик для выбора фото
        card.setOnMouseClicked(e -> {
            selectedPhoto = photo;
            selectedDefectPhoto = null;
            statusLabel.setText("Выбрано фото: " + (photo.getPhotoDescription() != null ? photo.getPhotoDescription() : "без описания"));
            // Визуальное выделение
            resetSelection();
            card.setStyle("-fx-border-color: #0078d7; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-color: #e0f0ff;");
        });

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        String fullPath = System.getProperty("user.home") + File.separator + "VinylManager" + File.separator + photo.getPhotoUrl();
        File imageFile = new File(fullPath);
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
            imageView.setImage(image);
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/icons/no-image.png")));
        }

        Label descLabel = new Label(photo.getPhotoDescription() != null && !photo.getPhotoDescription().isEmpty()
                ? photo.getPhotoDescription() : "Нет описания");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(100);
        descLabel.setStyle("-fx-font-size: 11px;");

        card.getChildren().addAll(imageView, descLabel);
        return card;
    }

    private VBox createDefectPhotoCard(DefectPhoto photo) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(5));
        card.setStyle("-fx-border-color: #ff9999; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Добавляем эффект при наведении
        card.setOnMouseEntered(e -> card.setStyle("-fx-border-color: #ff0000; -fx-border-radius: 5; -fx-background-color: #fff0f0;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-border-color: #ff9999; -fx-border-radius: 5;"));

        // Клик для выбора фото
        card.setOnMouseClicked(e -> {
            selectedDefectPhoto = photo;
            selectedPhoto = null;
            statusLabel.setText("Выбрано фото дефекта: " + (photo.getPhotoDescription() != null ? photo.getPhotoDescription() : "без описания"));
            // Визуальное выделение
            resetSelection();
            card.setStyle("-fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-color: #ffe0e0;");
        });

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        String fullPath = System.getProperty("user.home") + File.separator + "VinylManager" + File.separator + photo.getPhotoUrl();
        File imageFile = new File(fullPath);
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
            imageView.setImage(image);
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/icons/no-image.png")));
        }

        Label descLabel = new Label(photo.getPhotoDescription() != null && !photo.getPhotoDescription().isEmpty()
                ? photo.getPhotoDescription() : "Нет описания");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(100);
        descLabel.setStyle("-fx-font-size: 11px;");

        card.getChildren().addAll(imageView, descLabel);
        return card;
    }

    private void resetSelection() {
        for (var node : photoTilePane.getChildren()) {
            node.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
        }
        for (var node : defectPhotoTilePane.getChildren()) {
            node.setStyle("-fx-border-color: #ff9999; -fx-border-radius: 5;");
        }
    }

    private String savePhotoToDisk(File sourceFile, String prefix) throws IOException {
        String photosDir = System.getProperty("user.home") + File.separator + "VinylManager" + File.separator + "photos";
        File dir = new File(photosDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = "";
        String name = sourceFile.getName();
        int lastDot = name.lastIndexOf(".");
        if (lastDot > 0) {
            extension = name.substring(lastDot);
        }

        String newFileName = prefix + "_" + UUID.randomUUID() + extension;
        Path targetPath = Paths.get(photosDir, newFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "photos/" + newFileName;
    }

    private String showDescriptionDialog() {
        return showDescriptionDialog(null);
    }

    private String showDescriptionDialog(String initialDescription) {
        TextInputDialog dialog = new TextInputDialog(initialDescription != null ? initialDescription : "");
        dialog.setTitle("Описание фото");
        dialog.setHeaderText("Введите описание фото");
        dialog.setContentText("Описание:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}