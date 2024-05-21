// RoundRobin.java

package OSLabClass.实验一;

import java.util.*;

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
