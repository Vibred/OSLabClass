package OSLabClass.实验一;

class PCB {
    String name;
    char state;
    int needTime; // 所需要的时间
    int runTime; // 进程已经执行的时间
    int startTime; // 进程从就绪状态第一次开始执行的时间
    int endTime; // 进程执行完成的时间
    int arrivalTime; // 进程到达时间

    public PCB(String name, int needTime, int arrivalTime) {
        this.name = name;
        this.state = 'W'; // W表示等待状态
        this.needTime = needTime;
        this.runTime = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.arrivalTime = arrivalTime;
    }
}
