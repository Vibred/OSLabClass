import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

class Buffer {
    private final Queue<Integer> queue;
    private final int capacity;
    private JTextArea textArea;

    public Buffer(int capacity, JTextArea textArea) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.textArea = textArea;
    }

    public synchronized void put(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(value);
        textArea.append("Produced: " + value + "\n");
        notifyAll();
    }

    public synchronized int get() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        int value = queue.remove();
        textArea.append("Consumed: " + value + "\n");
        notifyAll();
        return value;
    }
}

class Producer implements Runnable {
    private final Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                buffer.put(i);
                i++;
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Consumer implements Runnable {
    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                buffer.get();
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class ProducerConsumerApp {
    public static void main(String[] args) {
        JTextArea textArea = new JTextArea(20, 30);
        textArea.setEditable(false);

        JFrame frame = new JFrame("Producer Consumer Visualization");
        frame.add(new JScrollPane(textArea));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Buffer buffer = new Buffer(5, textArea);
        Thread producerThread = new Thread(new Producer(buffer));
        Thread consumerThread = new Thread(new Consumer(buffer));

        producerThread.start();
        consumerThread.start();
    }
}