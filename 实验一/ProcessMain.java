package OSLabClass.实验一;

import java.util.ArrayList;

public class ProcessMain {
    public static void main(String[] args) {
        // 测试数据
        ArrayList<PCB> processes = new ArrayList<>();
        processes.add(new PCB("P1", 1, 8));
        processes.add(new PCB("P2", 2, 4));
        processes.add(new PCB("P3", 3, 9));
        processes.add(new PCB("P4", 4, 5));
        processes.add(new PCB("P5", 5, 2));

        // 调度
        ShortJobFirst.shortestJobFirst(processes);
    }
}
