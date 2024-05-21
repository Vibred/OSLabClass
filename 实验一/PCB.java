package OSLabClass.实验一;

public class PCB {
    String name;
    char state;
    int priority;
    int needTime;
    int runTime;

    public PCB(String name, int priority, int needTime) {
        this.name = name;
        this.state = 'W';
        this.priority = priority;
        this.needTime = needTime;
        this.runTime = 0;
    }
}
