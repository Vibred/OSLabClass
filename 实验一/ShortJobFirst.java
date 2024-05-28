package OSLabClass.实验一;

import java.util.*;

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
            processList.remove(0);
        }
        printFinalStatus();
        System.out.println("最短作业优先调度总用时：" + totalTime + " 个时间单位。");
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
