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
        Button createFileBtn = new Button("创建文件");
        createFileBtn.setOnAction(e -> {
            String fileName = fileNameInput.getText();
            String currentPath = pathField.getText();
            if (!fileName.isEmpty()) {
                if (fileSystem.createFile(currentPath, fileName)) {
                    showAlert("成功", "文件创建成功。");
                } else {
                    showAlert("错误", "文件名已存在或路径无效。");
                }
                updateFileList(currentPath);
                fileNameInput.clear();
            }
        });
        Button createFolderBtn = new Button("创建文件夹");
        createFolderBtn.setOnAction(e -> {
            String folderName = fileNameInput.getText();
            String currentPath = pathField.getText();
            if (!folderName.isEmpty()) {
                if (fileSystem.createFolder(currentPath, folderName)) {
                    showAlert("成功", "文件夹创建成功。");
                } else {
                    showAlert("错误", "文件夹名已存在或路径无效。");
                }
                updateFileList(currentPath);
                fileNameInput.clear();
            }
        });
        Button renameBtn = new Button("重命名");
        renameBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String newName = newFileNameInput.getText();
            String currentPath = pathField.getText();
            if (selectedItem != null && !newName.isEmpty()) {
                String name = selectedItem.split(" ")[0];
                if (fileSystem.renameFileOrFolder(currentPath, name, newName)) {
                    showAlert("成功", "文件/文件夹重命名成功。");
                } else {
                    showAlert("错误", "新名称已存在或路径无效。");
                }
                updateFileList(currentPath);
                newFileNameInput.clear();
            }
        });
        Button backButton = new Button("返回");
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
        buttonBox.getChildren().addAll(new Label("文件/文件夹名:"), fileNameInput, createFileBtn, createFolderBtn, new Label("新名称:"), newFileNameInput, renameBtn, backButton);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10));
        Button deleteBtn = new Button("删除");
        deleteBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String currentPath = pathField.getText();
            if (selectedItem != null) {
                String name = selectedItem.split(" ")[0];
                fileSystem.deleteFileOrFolder(currentPath, name);
                updateFileList(currentPath);
            }
        });
        Button openBtn = new Button("打开");
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
        Button saveContentBtn = new Button("保存内容");
        saveContentBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            String currentPath = pathField.getText();
            if (selectedItem != null) {
                String name = selectedItem.split(" ")[0];
                FileEntity entity = fileSystem.getFile(currentPath, name);
                if (entity != null && entity.isFile()) {
                    entity.setContent(fileContentArea.getText());
                    fileSystem.writeDisk();
                    showAlert("成功", "内容保存成功。");
                }
            }
        });
        rightBox.getChildren().addAll(new Label("选择一个文件或文件夹:"), fileList, openBtn, deleteBtn, fileContentArea, saveContentBtn);

        root.setTop(pathField);
        root.setCenter(buttonBox);
        root.setRight(rightBox);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("文件系统");
        primaryStage.show();
    }

    private void updateFileList(String path) {
        fileList.getItems().clear();
        List<FileEntity> files = fileSystem.getFiles(path);
        if (files != null) {
            for (FileEntity file : files) {
                fileList.getItems().add(file.getName() + (file.isFile() ? " (文件)" : " (文件夹)"));
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