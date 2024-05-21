// Scheduler.java

package OSLabClass.实验一;

import java.util.*;

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
            System.out.print("进程名：" + pcb.name +";"+"\t");
            System.out.println("首次运行时刻："+ (pcb.startTime) +";" + "\t从就绪到结束用时 " + (pcb.endTime) + " 个时间单位" +";");
        }
    }

    protected abstract Queue<PCB> getProcessQueue();

    protected abstract char getNextState(PCB current);

    public abstract void schedule();
}
