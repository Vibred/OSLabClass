package OSLabClass.课程设计;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// 打印任务类
class PrintTask {
    static int idCounter = 0; // 静态变量，用于生成唯一的任务编号
    private int id; // 任务编号
    private String content; // 打印内容
    private int priority; // 任务优先级
    private long timestamp; // 任务提交时间戳

    // 构造方法，初始化任务内容和优先级，并生成任务编号和时间戳
    public PrintTask(String content, int priority) {
        this.id = ++idCounter;
        this.content = content;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
    }

    // 获取任务编号
    public int getId() {
        return id;
    }

    // 获取任务内容
    public String getContent() {
        return content;
    }

    // 获取任务优先级
    public int getPriority() {
        return priority;
    }

    // 获取任务提交时间戳
    public long getTimestamp() {
        return timestamp;
    }
}

// 打印假脱机模拟类，继承JFrame
public class PrintSpoolerSim extends JFrame {
    // 优先级阻塞队列，用于存储打印任务，按优先级和时间排序
    private PriorityBlockingQueue<PrintTask> outputQueue = new PriorityBlockingQueue<>(10, Comparator.comparingInt(PrintTask::getPriority).thenComparingLong(PrintTask::getTimestamp));
    private ExecutorService printerService = Executors.newSingleThreadExecutor(); // 单线程执行服务，用于处理打印任务
    private Future<?> currentTask; // 当前打印任务的Future对象
    private JTextArea queueArea = new JTextArea(); // 队列区文本区域
    private JTextArea printerArea = new JTextArea(); // 打印区文本区域
    private JTextArea waitingQueueArea = new JTextArea(); // 等待队列区文本区域

    // 构造方法，初始化GUI组件
    public PrintSpoolerSim() {
        setTitle("打印假脱机模拟");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel taskLabel = new JLabel("任务内容:");
        inputPanel.add(taskLabel, gbc);

        gbc.gridx = 1;
        JTextField taskField = new JTextField(20);
        inputPanel.add(taskField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel priorityLabel = new JLabel("优先级:");
        inputPanel.add(priorityLabel, gbc);

        gbc.gridx = 1;
        JTextField priorityField = new JTextField(20);
        inputPanel.add(priorityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("提交任务");
        inputPanel.add(submitButton, gbc);

        gbc.gridy = 3;
        JButton resetButton = new JButton("重置打印队列");
        inputPanel.add(resetButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        queueArea.setEditable(false);
        printerArea.setEditable(false);
        waitingQueueArea.setEditable(false);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout(1, 3));

        JPanel queuePanel = new JPanel(new BorderLayout());
        JLabel queueLabel = new JLabel("队列区");
        queuePanel.add(queueLabel, BorderLayout.NORTH);
        queuePanel.add(new JScrollPane(queueArea), BorderLayout.CENTER);

        JPanel printerPanel = new JPanel(new BorderLayout());
        JLabel printerLabel = new JLabel("打印区");
        printerPanel.add(printerLabel, BorderLayout.NORTH);
        printerPanel.add(new JScrollPane(printerArea), BorderLayout.CENTER);

        JPanel waitingQueuePanel = new JPanel(new BorderLayout());
        JLabel waitingQueueLabel = new JLabel("等待队列区（输出井）");
        waitingQueuePanel.add(waitingQueueLabel, BorderLayout.NORTH);
        waitingQueuePanel.add(new JScrollPane(waitingQueueArea), BorderLayout.CENTER);

        outputPanel.add(queuePanel);
        outputPanel.add(printerPanel);
        outputPanel.add(waitingQueuePanel);

        add(outputPanel, BorderLayout.CENTER);

        // 提交任务按钮的事件监听器
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = taskField.getText();
                int priority = Integer.parseInt(priorityField.getText());
                submitTask(content, priority);
            }
        });

        // 重置打印队列按钮的事件监听器
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetQueue();
            }
        });
    }

    // 提交任务的方法
    private void submitTask(String content, int priority) {
        PrintTask task = new PrintTask(content, priority); // 创建新的打印任务
        outputQueue.add(task); // 将任务添加到输出队列
        queueArea.append("任务提交: " + task.getContent() + "，优先级 " + task.getPriority() + "，任务编号 " + task.getId() + "\n");
        updateWaitingQueueArea(); // 更新等待队列区
        processTasks(); // 处理打印任务
    }

    // 处理打印任务的方法
    private void processTasks() {
        if (currentTask == null || currentTask.isDone()) { // 如果当前没有正在执行的任务
            currentTask = printerService.submit(() -> { // 提交新的打印任务
                while (!outputQueue.isEmpty()) {
                    PrintTask task = outputQueue.poll(); // 从队列中取出任务
                    updateWaitingQueueArea(); // 更新等待队列区
                    printerArea.append("任务编号 " + task.getId() + " 开始打印；\n任务优先级 " + task.getPriority() + "；\n");
                    printerArea.append("打印内容：\n");
                    String content = task.getContent();
                    for (char c : content.toCharArray()) {
                        printerArea.append(String.valueOf(c));
                        try {
                            Thread.sleep(2000); // 每输出一个字符花费两秒
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    printerArea.append("\n任务编号 " + task.getId() + " 打印完毕；\n\n");
                }
            });
        }
    }

    // 重置打印队列的方法
    private void resetQueue() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true); // 取消当前正在执行的任务
        }
        outputQueue.clear(); // 清空输出队列
        PrintTask.idCounter = 0; // 重置任务编号计数器
        queueArea.append("打印队列已重置\n");
        printerArea.append("打印任务已重置\n");
        updateWaitingQueueArea(); // 更新等待队列区
    }

    // 更新等待队列区的方法
    private void updateWaitingQueueArea() {
        SwingUtilities.invokeLater(() -> {
            waitingQueueArea.setText("");
            for (PrintTask task : outputQueue) {
                waitingQueueArea.append("任务编号 " + task.getId() + "，优先级 " + task.getPriority() + "，内容 " + task.getContent() + "\n");
            }
        });
    }

    // 主方法，启动程序
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrintSpoolerSim sim = new PrintSpoolerSim();
            sim.setVisible(true);
        });
    }
}
