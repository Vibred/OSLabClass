package OSLabClass.实验一;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProcessMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请选择功能：");
            System.out.println("1. 执行processes.txt文件中的进程调度并输出结果");
            System.out.println("2. 执行随机生成多个进程并比较调度算法优劣");
            System.out.println("3. 退出");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    executeProcessesFromFile();
                    break;
                case 2:
                    System.out.print("请输入运行次数：");
                    int numberOfRuns = scanner.nextInt();
                    System.out.print("请输入每次生成的进程数：");
                    int numberOfProcesses = scanner.nextInt();
                    compareAlgorithmsWithRandomProcesses(numberOfRuns, numberOfProcesses);
                    break;
                case 3:
                    System.out.println("退出程序。");
                    return;
                default:
                    System.out.println("无效选择，请重新选择。");
            }
        }
    }

    private static void executeProcessesFromFile() {
        RoundRobin rr = new RoundRobin(4); // 时间片为4
        ShortestJobFirst sjf = new ShortestJobFirst();
        FCFS fcfs = new FCFS();

        List<PCB> processes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("E:\\OSLabClass\\OSLabClass\\实验一\\processes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String name = parts[0];
                int needTime = Integer.parseInt(parts[1]);
                PCB pcb = new PCB(name, needTime, 0);
                processes.add(pcb);
                rr.addProcess(new PCB(name, needTime, 0)); // 创建新的PCB对象传递给每个调度器，防止状态共享
                sjf.addProcess(new PCB(name, needTime, 0));
                fcfs.addProcess(new PCB(name, needTime, 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 输出processes的内容
        System.out.println("进程列表：");
        for (PCB pcb : processes) {
            System.out.println("进程名：" + pcb.name + "，需求时间：" + pcb.needTime + "，运行时间：" + pcb.runTime + "，状态：" + pcb.state);
        }

        System.out.println("\n轮转调度：");
        rr.schedule();

        System.out.println("\n最短作业优先调度：");
        sjf.schedule();

        System.out.println("\n先来先服务调度：");
        fcfs.schedule();
    }

    private static void compareAlgorithmsWithRandomProcesses(int numberOfRuns, int numberOfProcesses) {
        Random random = new Random();
        List<List<Integer>> rrResults = new ArrayList<>();
        List<List<Integer>> sjfResults = new ArrayList<>();
        List<List<Integer>> fcfsResults = new ArrayList<>();

        for (int i = 0; i < numberOfRuns; i++) {
            RoundRobin rr = new RoundRobin(4); // 时间片为4
            ShortestJobFirst sjf = new ShortestJobFirst();
            FCFS fcfs = new FCFS();

            for (int j = 0; j < numberOfProcesses; j++) {
                String name = "P" + j;
                int needTime = random.nextInt(10) + 1;
                int arrivalTime = random.nextInt(100);
                PCB pcb = new PCB(name, needTime, arrivalTime);
                rr.addProcess(new PCB(name, needTime, arrivalTime));
                sjf.addProcess(new PCB(name, needTime, arrivalTime));
                fcfs.addProcess(new PCB(name, needTime, arrivalTime));
            }

            rr.schedule();
            sjf.schedule();
            fcfs.schedule();

            rrResults.add(rr.calculateMetrics());
            sjfResults.add(sjf.calculateMetrics());
            fcfsResults.add(fcfs.calculateMetrics());
        }

        printAverageResults(rrResults, "轮转调度");
        printAverageResults(sjfResults, "最短作业优先调度");
        printAverageResults(fcfsResults, "先来先服务调度");
    }

    private static void printAverageResults(List<List<Integer>> results, String algorithmName) {
        int total = 0;
        int totalTurnaround = 0;
        int totalWaiting = 0;

        for (List<Integer> result : results) {
            total += result.get(0);
            totalTurnaround += result.get(1);
            totalWaiting += result.get(2);
        }

        int numberOfRuns = results.size();

        System.out.println("\n" + algorithmName + "平均结果：");
        System.out.println("平均总用时：" + total / numberOfRuns + " 个时间单位。");
        System.out.println("平均周转时间：" + totalTurnaround / numberOfRuns + " 个时间单位。");
        System.out.println("平均等待时间：" + totalWaiting / numberOfRuns + " 个时间单位。");
    }
}