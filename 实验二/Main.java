package OSLabClass.实验二;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class FileEntity {
    private String name;
    private boolean isFile;

    public FileEntity(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public boolean isFile() {
        return isFile;
    }
}

class FileSystem {
    private File disk;
    private List<FileEntity> files;

    public FileSystem(String diskPath) {
        disk = new File(diskPath);
        if (!disk.exists()) {
            try {
                disk.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        files = new ArrayList<>();
        readDisk();
    }

    private void readDisk() {
        try (BufferedReader reader = new BufferedReader(new FileReader(disk))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                files.add(new FileEntity(parts[0], Boolean.parseBoolean(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDisk() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(disk))) {
            for (FileEntity file : files) {
                writer.write(file.getName() + "," + file.isFile() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String name) {
        files.add(new FileEntity(name, true));
        writeDisk();
    }

    public void createFolder(String name) {
        files.add(new FileEntity(name, false));
        writeDisk();
    }

    public void deleteFile(String name) {
        files.removeIf(file -> file.getName().equals(name) && file.isFile());
        writeDisk();
    }

    public void deleteFolder(String name) {
        files.removeIf(file -> file.getName().equals(name) && !file.isFile());
        writeDisk();
    }

    public List<FileEntity> getFiles() {
        return files;
    }
}

public class Main extends Application {

    private FileSystem fileSystem;
    private ListView<String> fileList;

    @Override
    public void start(Stage primaryStage) {
        fileSystem = new FileSystem("disk.txt");

        BorderPane root = new BorderPane();

        fileList = new ListView<>();
        updateFileList();

        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));
        TextField fileNameInput = new TextField();
        Button createFileBtn = new Button("Create File");
        createFileBtn.setOnAction(e -> {
            String fileName = fileNameInput.getText();
            if (!fileName.isEmpty()) {
                fileSystem.createFile(fileName);
                updateFileList();
                fileNameInput.clear();
            }
        });
        Button createFolderBtn = new Button("Create Folder");
        createFolderBtn.setOnAction(e -> {
            String folderName = fileNameInput.getText();
            if (!folderName.isEmpty()) {
                fileSystem.createFolder(folderName);
                updateFileList();
                fileNameInput.clear();
            }
        });
        buttonBox.getChildren().addAll(fileNameInput, createFileBtn, createFolderBtn);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10));
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            String selectedItem = fileList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                fileSystem.deleteFile(selectedItem);
                updateFileList();
            }
        });
        rightBox.getChildren().addAll(new Label("Select a file to delete:"), fileList, deleteBtn);

        root.setCenter(buttonBox);
        root.setRight(rightBox);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("File System");
        primaryStage.show();
    }

    private void updateFileList() {
        fileList.getItems().clear();
        for (FileEntity file : fileSystem.getFiles()) {
            fileList.getItems().add(file.getName() + (file.isFile() ? " (File)" : " (Folder)"));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
