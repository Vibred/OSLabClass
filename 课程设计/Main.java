package OSLabClass.课程设计;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Queue;

public class Main extends Application {
    private SpoolingService spoolingService;
    private TextArea outputArea;
    private Queue<OutputRequest> requestQueue;

    @Override
    public void start(Stage primaryStage) {
        requestQueue = new LinkedList<>();

        BorderPane root = new BorderPane();

        VBox userProcessBox = new VBox(10);
        userProcessBox.setPadding(new Insets(10));
        userProcessBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        userProcessBox.getChildren().add(new Label("用户进程"));

        TextField user1Input = new TextField();
        Button user1Button = new Button("用户1请求输出");
        user1Button.setOnAction(e -> handleUserRequest("用户1", user1Input.getText()));

        TextField user2Input = new TextField();
        Button user2Button = new Button("用户2请求输出");
        user2Button.setOnAction(e -> handleUserRequest("用户2", user2Input.getText()));

        userProcessBox.getChildren().addAll(new Label("用户1输入："), user1Input, user1Button, new Label("用户2输入："), user2Input, user2Button);

        VBox spoolingBox = new VBox(10);
        spoolingBox.setPadding(new Insets(10));
        spoolingBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        spoolingBox.getChildren().add(new Label("SPOOLing输出服务"));

        Button spoolingButton = new Button("执行SPOOLing输出");
        spoolingButton.setOnAction(e -> spoolingService.processRequests());

        outputArea = new TextArea();
        outputArea.setEditable(false);

        spoolingBox.getChildren().addAll(spoolingButton, new Label("输出结果："), outputArea);

        root.setLeft(userProcessBox);
        root.setCenter(spoolingBox);

        // 初始化 SpoolingService 实例并传递 TextArea 对象
        spoolingService = new SpoolingService(requestQueue, outputArea);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("SPOOLing假脱机输入输出模拟");
        primaryStage.show();
    }

    private void handleUserRequest(String userName, String content) {
        if (content.isEmpty()) {
            showAlert("错误", "输入内容不能为空");
            return;
        }
        OutputRequest request = new OutputRequest(userName, content);
        requestQueue.add(request);
        showAlert("成功", userName + "的输出请求已加入队列");
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