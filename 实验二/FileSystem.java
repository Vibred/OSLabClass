package OSLabClass.实验二;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class FileSystem {
    private File disk;
    private List<FileEntity> rootFiles;

    public FileSystem(String diskPath) {
        disk = new File(diskPath);
        rootFiles = new ArrayList<>();
        if (!disk.exists()) {
            try {
                disk.createNewFile();
                rootFiles.add(new FileEntity("/", false)); // 初始化根目录
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            readDisk();
        }
    }

    private void readDisk() {
        try (BufferedReader reader = new BufferedReader(new FileReader(disk))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rootFiles.add(parseFileEntity(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileEntity parseFileEntity(String line) {
        String[] parts = line.split(",");
        FileEntity entity = new FileEntity(parts[0], Boolean.parseBoolean(parts[1]));
        if (entity.isFile()) {
            entity.setContent(parts.length > 2 ? parts[2].replace("\\,", ",") : "");
        } else {
            for (int i = 2; i < parts.length; i++) {
                entity.getChildren().add(parseFileEntity(parts[i]));
            }
        }
        return entity;
    }

    protected void writeDisk() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(disk))) {
            for (FileEntity file : rootFiles) {
                writer.write(serializeFileEntity(file) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serializeFileEntity(FileEntity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getName()).append(",").append(entity.isFile());
        if (entity.isFile()) {
            sb.append(",").append(entity.getContent().replace(",", "\\,"));
        } else {
            for (FileEntity child : entity.getChildren()) {
                sb.append(",").append(serializeFileEntity(child));
            }
        }
        return sb.toString();
    }

    public boolean createFile(String path, String name) {
        if (!isValidName(name)) return false;
        FileEntity parent = findEntity(path);
        if (parent == null || parent.isFile() || fileExists(parent, name)) {
            return false;
        }
        parent.getChildren().add(new FileEntity(name, true));
        writeDisk();
        return true;
    }

    public boolean createFolder(String path, String name) {
        if (!isValidName(name)) return false;
        FileEntity parent = findEntity(path);
        if (parent == null || parent.isFile() || fileExists(parent, name)) {
            return false;
        }
        parent.getChildren().add(new FileEntity(name, false));
        writeDisk();
        return true;
    }

    public void deleteFileOrFolder(String path, String name) {
        FileEntity parent = findEntity(path);
        if (parent != null && !parent.isFile()) {
            parent.getChildren().removeIf(file -> file.getName().equals(name));
            writeDisk();
        }
    }

    public boolean renameFileOrFolder(String path, String oldName, String newName) {
        if (!isValidName(newName)) return false;
        FileEntity parent = findEntity(path);
        if (parent == null || fileExists(parent, newName)) {
            return false;
        }
        for (FileEntity file : parent.getChildren()) {
            if (file.getName().equals(oldName)) {
                file.setName(newName);
                writeDisk();
                return true;
            }
        }
        return false;
    }

    private boolean fileExists(FileEntity parent, String name) {
        return parent.getChildren().stream().anyMatch(file -> file.getName().equals(name));
    }

    private FileEntity findEntity(String path) {
        if ("/".equals(path)) {
            return new FileEntity("", false) {
                @Override
                public List<FileEntity> getChildren() {
                    return rootFiles;
                }
            };
        }
        String[] parts = path.split("/");
        List<FileEntity> currentChildren = rootFiles;
        FileEntity currentEntity = null;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            for (FileEntity child : currentChildren) {
                if (child.getName().equals(part)) {
                    currentEntity = child;
                    currentChildren = child.getChildren();
                    break;
                }
            }
            if (currentEntity == null || currentEntity.isFile()) {
                return null; // No matching directory found or reached a file before the end of the path
            }
        }
        return currentEntity;
    }

    public List<FileEntity> getFiles(String path) {
        FileEntity entity = findEntity(path);
        return entity != null ? entity.getChildren() : null;
    }

    public FileEntity getFile(String path, String name) {
        FileEntity parent = findEntity(path);
        if (parent != null && !parent.isFile()) {
            for (FileEntity file : parent.getChildren()) {
                if (file.getName().equals(name)) {
                    return file;
                }
            }
        }
        return null;
    }

    private boolean isValidName(String name) {
        return name != null && !name.contains("/") && !name.contains("\\") && !name.contains(",");
    }
}