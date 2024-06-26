package OSLabClass.课程设计;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.Queue;

public class SpoolingService {
    private Queue<OutputRequest> requestQueue;
    private TextArea outputArea;

    public SpoolingService(Queue<OutputRequest> requestQueue, TextArea outputArea) {
        this.requestQueue = requestQueue;
        this.outputArea = outputArea;
    }

    public void processRequests() {
        if (requestQueue.isEmpty()) {
            showAlert("提示", "当前没有需要输出的请求");
            return;
        }

        new Thread(() -> {
            while (!requestQueue.isEmpty()) {
                OutputRequest request = requestQueue.poll();
                Platform.runLater(() -> outputArea.appendText(request.getUserName() + ": " + request.getContent() + "\n"));
                try {
                    Thread.sleep(1000); // 模拟输出时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            showAlert("提示", "所有输出请求已处理完毕");
        }).start();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}