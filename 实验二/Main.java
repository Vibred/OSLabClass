package OSLabClass.实验二;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private FileSystem fileSystem;
    private ListView<String> fileList;
    private TextArea fileContentArea;
    private TextField pathField;

    @Override
    public void start(Stage primaryStage) {
        fileSystem = new FileSystem("disk.txt");

        BorderPane root = new BorderPane();

        pathField = new TextField("/");
        pathField.setEditable(false);

        fileList = new ListView<>();
        updateFileList("/");

        fileContentArea = new TextArea();
        fileContentArea.setEditable(true);

        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));
        TextField fileNameInput = new TextField();
        TextField newFileNameInput = new TextField();
        Button createFileBtn = new Button("Create File");
        createFileBtn.setOnAction(e -> {
            String fileName = fileNameInput.getText();
            String currentPath = pathField.getText();
            if (!fileName.isEmpty()) {
                if (fileSystem.createFile(currentPath, fileName)) {
                    showAlert("Success", "File created successfully.");
                } else {
                    showAlert("Error", "File name already exists or invalid path.");
                }
                updateFileList(currentPath);
                fileNameInput.clear();
            }
        });
        Button createFolderBtn = new Button("Create Folder");
        createFolderBtn.setOnAction(e -> {
            String folderName = fileNameInput.getText();
            String currentPath = pathField.getText();
            if (!folderName.isEmpty()) {
                if (fileSystem.createFolder(currentPath, folderName)) {
                    showAlert("Success", "Folder created successfully.");
                } else {
                    showAlert("Error", "Folder name already exists or invalid path.");
                }
                updateFileList(currentPath);
                fileNameInput.clear();
            }
        });
        Button renameBtn = new Button("Rename");
        renameBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String newName = newFileNameInput.getText();
            String currentPath = pathField.getText();
            if (selectedItem != null && !newName.isEmpty()) {
                String name = selectedItem.split(" ")[0];
                if (fileSystem.renameFileOrFolder(currentPath, name, newName)) {
                    showAlert("Success", "File/Folder renamed successfully.");
                } else {
                    showAlert("Error", "New name already exists or invalid path.");
                }
                updateFileList(currentPath);
                newFileNameInput.clear();
            }
        });
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            String currentPath = pathField.getText();
            if (!"/".equals(currentPath)) {
                String parentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                if (parentPath.isEmpty()) {
                    parentPath = "/";
                }
                pathField.setText(parentPath);
                updateFileList(parentPath);
            }
        });
        buttonBox.getChildren().addAll(new Label("File/Folder Name:"), fileNameInput, createFileBtn, createFolderBtn, new Label("New Name:"), newFileNameInput, renameBtn, backButton);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10));
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String currentPath = pathField.getText();
            if (selectedItem != null) {
                String name = selectedItem.split(" ")[0];
                fileSystem.deleteFileOrFolder(currentPath, name);
                updateFileList(currentPath);
            }
        });
        Button openBtn = new Button("Open");
        openBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String currentPath = pathField.getText();
            if (selectedItem != null) {
                String name = selectedItem.split(" ")[0];
                FileEntity entity = fileSystem.getFile(currentPath, name);
                if (entity != null) {
                    if (entity.isFile()) {
                        fileContentArea.setText(entity.getContent());
                    } else {
                        pathField.setText(currentPath + "/" + name);
                        updateFileList(currentPath + "/" + name);
                    }
                }
            }
        });
        Button saveContentBtn = new Button("Save Content");
        saveContentBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String currentPath = pathField.getText();
            if (selectedItem != null) {
                String name = selectedItem.split(" ")[0];
                FileEntity entity = fileSystem.getFile(currentPath, name);
                if (entity != null && entity.isFile()) {
                    entity.setContent(fileContentArea.getText());
                    fileSystem.writeDisk();
                    showAlert("Success", "Content saved successfully.");
                }
            }
        });
        rightBox.getChildren().addAll(new Label("Select a file or folder:"), fileList, openBtn, deleteBtn, fileContentArea, saveContentBtn);

        root.setTop(pathField);
        root.setCenter(buttonBox);
        root.setRight(rightBox);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("File System");
        primaryStage.show();
    }

    private void updateFileList(String path) {
        fileList.getItems().clear();
        List<FileEntity> files = fileSystem.getFiles(path);
        if (files != null) {
            for (FileEntity file : files) {
                fileList.getItems().add(file.getName() + (file.isFile() ? " (File)" : " (Folder)"));
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}