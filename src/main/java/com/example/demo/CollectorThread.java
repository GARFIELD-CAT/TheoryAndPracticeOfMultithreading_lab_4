package com.example.demo;

import com.example.demo.model.Item;


public class CollectorThread extends Thread {
    private final DataCollector dataCollector;
    private final int itemsToCollect;
    private final String threadName;

    public CollectorThread(String name, DataCollector dataCollector, int itemsToCollect) {
        super(name);
        this.dataCollector = dataCollector;
        this.itemsToCollect = itemsToCollect;
        this.threadName = name;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemsToCollect; i++) {
                // Имитация получения данных
                Thread.sleep((long) (Math.random() * 100));
                String key = threadName + "-item-" + i + "-" + System.currentTimeMillis() % 1000;
                String value = "Value for " + key;
                Item item = new Item(key, value);

                dataCollector.collectItem(item);
            }
            System.out.println(Thread.currentThread().getName() + " завершил сбор.");
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " прерван.");
            Thread.currentThread().interrupt();
        }
    }
}
