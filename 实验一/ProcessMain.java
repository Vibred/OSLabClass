package OSLabClass.实验一;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProcessMain {

    public static void main(String[] args) {
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
                PCB pcb = new PCB(name, needTime);
                processes.add(pcb);
                rr.addProcess(new PCB(name, needTime)); // 创建新的PCB对象传递给每个调度器，防止状态共享
                sjf.addProcess(new PCB(name, needTime));
                fcfs.addProcess(new PCB(name, needTime));
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
}
