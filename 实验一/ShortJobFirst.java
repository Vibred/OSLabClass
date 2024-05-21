package OSLabClass.实验一;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShortJobFirst {
    public static void shortestJobFirst(ArrayList<PCB> processes) {
        ArrayList<PCB> readyQueue = new ArrayList<>();
        int currentTime = 0;

        while (true) {
            // 将到达的进程加入就绪队列
            for (PCB process : processes) {
                if (process.state == 'W') {
                    readyQueue.add(process);
                    process.state = 'R';
                }
            }

            // 对就绪队列按需要运行时间排序
            Collections.sort(readyQueue, Comparator.comparingInt(p -> p.needTime));

            // 运行就绪队列中优先级最高的进程
            if (!readyQueue.isEmpty()) {
                PCB runningProcess = readyQueue.get(0);
                readyQueue.remove(0);
                System.out.printf("时间：%-5d  运行进程：%-5s  就绪队列：", currentTime, runningProcess.name);

                // 打印就绪队列
                for (PCB p : readyQueue) {
                    System.out.print(p.name + " ");
                }
                System.out.print("  各进程状态：");

                // 打印各进程状态
                for (PCB p : processes) {
                    System.out.print(p.name + "(" + p.state + ") ");
                }
                System.out.println();

                // 运行进程
                runningProcess.runTime++;
                if (runningProcess.runTime == runningProcess.needTime) {
                    runningProcess.state = 'F';
                    System.out.println("进程 " + runningProcess.name + " 完成");
                } else {
                    readyQueue.add(runningProcess);
                    Collections.sort(readyQueue, Comparator.comparingInt(p -> p.needTime));
                }
                currentTime++;
            } else {
                // 没有就绪进程时,等待下一个进程到达
                currentTime++;
            }

            // 检查是否所有进程都完成
            boolean allFinished = true;
            for (PCB process : processes) {
                if (process.state != 'F') {
                    allFinished = false;
                    break;
                }
            }
            if (allFinished) {
                break;
            }
        }
    }
}
