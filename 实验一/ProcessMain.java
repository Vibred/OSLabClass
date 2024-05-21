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

// 调度器基类
abstract class Scheduler {
    protected int totalTime;
    protected List<PCB> allProcesses;

    public Scheduler() {
        this.totalTime = 0;
        this.allProcesses = new ArrayList<>();
    }

    public void addProcess(PCB pcb) {
        allProcesses.add(pcb);
    }

    protected void printCurrentState(PCB current, int time) {
        System.out.println("时间：" + time);
        System.out.println("当前执行程序：" + current.name + "\t下一状态：" + getNextState(current));
        System.out.print("当前就绪队列：");
        for (PCB pcb : getProcessQueue()) {
            System.out.print(pcb.name + " ");
        }
        System.out.println();
    }

    protected void printFinalStatus() {
        System.out.println("\n最终结果：");
        for (PCB pcb : allProcesses) {
            System.out.print("进程名：" + pcb.name + "\t");
            System.out.println("在"+ (pcb.startTime) + "时刻首次开始运行，从就绪到结束用时" + (pcb.endTime) + " 个时间单位");
        }
    }

    protected abstract Queue<PCB> getProcessQueue();

    protected abstract char getNextState(PCB current);

    public abstract void schedule();
}

// 先来先服务调度类
class FCFS extends Scheduler {
    private Queue<PCB> processQueue;

    public FCFS() {
        this.processQueue = new LinkedList<>();
    }

    @Override
    public void addProcess(PCB pcb) {
        super.addProcess(pcb);
        processQueue.offer(pcb);
    }

    @Override
    public void schedule() {
        int currentTime = 0;
        while (!processQueue.isEmpty()) {
            PCB current = processQueue.poll();
            current.state = 'R'; // R表示运行状态
            current.startTime = currentTime;
            for (int t = 0; t < current.needTime; t++) {
                current.runTime++;
                currentTime++;
                printCurrentState(current, currentTime);
            }
            current.endTime = currentTime;
            totalTime += current.needTime;
            current.state = 'F'; // F表示完成状态
        }
        printFinalStatus();
        System.out.println("先来先服务调度总用时：" + totalTime + " 个时间单位");
    }

    @Override
    protected Queue<PCB> getProcessQueue() {
        return processQueue;
    }

    @Override
    protected char getNextState(PCB current) {
        if (current.runTime == current.needTime) {
            return 'F'; // F表示完成状态
        }
        return 'R'; // R表示运行状态
    }
}

// 最短作业优先调度类
class ShortestJobFirst extends Scheduler {
    private List<PCB> processList;

    public ShortestJobFirst() {
        this.processList = new ArrayList<>();
    }

    @Override
    public void addProcess(PCB pcb) {
        super.addProcess(pcb);
        processList.add(pcb);
    }

    @Override
    public void schedule() {
        int currentTime = 0;
        processList.sort(Comparator.comparingInt(pcb -> pcb.needTime)); // 按需求时间排序
        while (!processList.isEmpty()) {
            PCB current = processList.get(0);
            current.state = 'R'; // R表示运行状态
            current.startTime = currentTime;
            for (int t = 0; t < current.needTime; t++) {
                current.runTime++;
                currentTime++;
                printCurrentState(current, currentTime);
            }
            current.endTime = currentTime;
            totalTime += current.needTime;
            current.state = 'F'; // F表示完成状态
            processList.remove(0);
        }
        printFinalStatus();
        System.out.println("最短作业优先调度总用时：" + totalTime + " 个时间单位");
    }

    @Override
    protected Queue<PCB> getProcessQueue() {
        return new LinkedList<>(processList); // 将list转换为queue返回
    }

    @Override
    protected char getNextState(PCB current) {
        if (current.runTime == current.needTime) {
            return 'F'; // F表示完成状态
        }
        return 'R'; // R表示运行状态
    }
}

// 轮转调度类
class RoundRobin extends Scheduler {
    private int timeQuantum;
    private Queue<PCB> processQueue;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
        this.processQueue = new LinkedList<>();
    }

    @Override
    public void addProcess(PCB pcb) {
        super.addProcess(pcb);
        processQueue.offer(pcb);
    }

    @Override
    public void schedule() {
        int currentTime = 0;
        while (!processQueue.isEmpty()) {
            PCB current = processQueue.poll();
            current.state = 'R'; // R表示运行状态
            if (current.startTime == 0) {
                current.startTime = currentTime;
            }
            int timeToRun = Math.min(timeQuantum, current.needTime - current.runTime);
            for (int t = 0; t < timeToRun; t++) {
                current.runTime++;
                currentTime++;
                printCurrentState(current, currentTime);
            }
            totalTime += timeToRun;

            if (current.runTime < current.needTime) {
                current.state = 'W'; // 继续等待
                processQueue.offer(current);
            } else {
                current.state = 'F'; // F表示完成状态
                current.endTime = currentTime;
            }
        }
        printFinalStatus();
        System.out.println("轮转调度总用时：" + totalTime + " 个时间单位");
    }

    @Override
    protected Queue<PCB> getProcessQueue() {
        return processQueue;
    }

    @Override
    protected char getNextState(PCB current) {
        if (current.runTime == current.needTime) {
            return 'F'; // F表示完成状态
        }
        return 'R'; // R表示运行状态
    }
}

// 进程控制块类
class PCB {
    String name;
    char state;
    int needTime; //所需要的时间
    int runTime; //进程已经执行的时间
    int startTime; //进程从就绪状态第一次开始执行的时间
    int endTime;  //进程执行完成的时间

    public PCB(String name, int needTime) {
        this.name = name;
        this.state = 'W'; // W表示等待状态
        this.needTime = needTime;
        this.runTime = 0;
        this.startTime = 0;
        this.endTime = 0;
    }
}
