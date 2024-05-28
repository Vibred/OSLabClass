package OSLabClass.实验一;

import java.util.*;

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
            if (current.arrivalTime > currentTime) {
                currentTime = current.arrivalTime;
            }
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
        System.out.println("先来先服务调度总用时：" + totalTime + " 个时间单位。");
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
