package OSLabClass.实验二;

import java.util.ArrayList;
import java.util.List;

class FileEntity {
    private String name;
    private boolean isFile;
    private String content;
    private List<FileEntity> children;

    public FileEntity(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
        this.content = isFile ? "" : null; // 默认内容为空
        this.children = isFile ? null : new ArrayList<>(); // 文件夹包含子文件和文件夹
    }

    public String getName() {
        return name;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<FileEntity> getChildren() {
        return children;
    }

    public void setName(String name) {
        this.name = name;
    }
}