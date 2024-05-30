package OSLabClass.实验三;

import java.util.Scanner;
import java.util.Random;

public class BankersAlgorithm {
    private int numProcesses; // 进程数
    private int numResources; // 资源种类数
    private int[] available; // 可用资源向量
    private int[][] max; // 最大需求矩阵
    private int[][] allocation; // 已分配矩阵
    private int[][] need; // 需求矩阵

    public BankersAlgorithm(int numProcesses, int numResources) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;
        available = new int[numResources];
        max = new int[numProcesses][numResources];
        allocation = new int[numProcesses][numResources];
        need = new int[numProcesses][numResources];
    }

    // 生成示例输入
    private void generateExampleInput() {
        Random rand = new Random();

        System.out.println("示例输入:");

        System.out.println("已分配矩阵 (Allocation Matrix):");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                allocation[i][j] = rand.nextInt(5); // 随机生成0-4之间的数
                System.out.print(allocation[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("最大需求矩阵 (Max Matrix):");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                max[i][j] = allocation[i][j] + rand.nextInt(5); // 确保最大需求大于等于已分配
                System.out.print(max[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("可用资源向量 (Available Resources):");
        for (int j = 0; j < numResources; j++) {
            available[j] = rand.nextInt(5); // 随机生成0-4之间的数
            System.out.print(available[j] + " ");
        }
        System.out.println();
    }

    // 输入矩阵
    public void inputMatrices() {
        Scanner sc = new Scanner(System.in);

        System.out.println("请输入已分配矩阵 (Allocation Matrix):");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                allocation[i][j] = sc.nextInt();
            }
        }

        System.out.println("请输入最大需求矩阵 (Max Matrix):");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                max[i][j] = sc.nextInt();
            }
        }

        System.out.println("请输入可用资源向量 (Available Resources):");
        for (int j = 0; j < numResources; j++) {
            available[j] = sc.nextInt();
        }

        // 计算需求矩阵
        calculateNeed();
    }

    // 计算需求矩阵
    private void calculateNeed() {
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }

    // 判断系统是否处于安全状态
    public boolean isSafe() {
        boolean[] finish = new boolean[numProcesses]; // 标记进程是否完成
        int[] work = new int[numResources]; // 工作向量
        System.arraycopy(available, 0, work, 0, numResources);

        while (true) {
            boolean found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canAllocate = true;
                    for (int j = 0; j < numResources; j++) {
                        if (need[i][j] > work[j]) {
                            canAllocate = false;
                            break;
                        }
                    }
                    if (canAllocate) {
                        for (int j = 0; j < numResources; j++) {
                            work[j] += allocation[i][j];
                        }
                        finish[i] = true;
                        found = true;
                    }
                }
            }
            if (!found) {
                break;
            }
        }

        for (boolean f : finish) {
            if (!f) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入进程数 (最多8个):");
        int numProcesses = sc.nextInt();
        System.out.println("请输入资源种类数 (最多3种):");
        int numResources = sc.nextInt();

        BankersAlgorithm ba = new BankersAlgorithm(numProcesses, numResources);
        ba.generateExampleInput(); // 生成示例输入
        ba.inputMatrices();

        System.out.println("\n输入的矩阵和向量:");
        System.out.println("进程数: " + numProcesses);
        System.out.println("资源种类数: " + numResources);
        System.out.println("已分配矩阵:");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                System.out.print(ba.allocation[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("最大需求矩阵:");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                System.out.print(ba.max[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("可用资源向量:");
        for (int j = 0; j < numResources; j++) {
            System.out.print(ba.available[j] + " ");
        }
        System.out.println();

        if (ba.isSafe()) {
            System.out.println("系统处于安全状态。");
        } else {
            System.out.println("系统处于不安全状态。");
        }
    }
}