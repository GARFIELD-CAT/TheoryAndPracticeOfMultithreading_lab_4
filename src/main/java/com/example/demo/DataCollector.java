package com.example.demo;

import com.example.demo.model.Item;

import java.util.*;

public class DataCollector {
    private final List<Item> dataList = new ArrayList<>();
    private final Set<String> processedItems = new HashSet<>();

    private volatile int processedCount = 0;
    private final Object monitor = new Object(); // Объект для wait/notify

    // Метод для добавления элемента. Защищен синхронизацией.
    public void collectItem(Item item) {
        synchronized (monitor) { // Блокировка на самом объекте DataCollector
            if (!processedItems.contains(item.getKey())) {
                dataList.add(item);
                processedItems.add(item.getKey());
                System.out.println(Thread.currentThread().getName() + " собрал: " + item.getKey());
                // Оповещаем все ожидающие потоки о том, что данные доступны
                monitor.notifyAll();
            } else {
                System.out.println(Thread.currentThread().getName() + " пропустил дубликат: " + item.getKey());
            }
        }
    }

    public void incrementProcessed() {
        synchronized (monitor) {
            processedCount++;
             System.out.println(Thread.currentThread().getName() + " увеличил processedCount до: " + processedCount);
        }
    }

    public boolean isAlreadyProcessed(String key) {
        synchronized (monitor) {
            return processedItems.contains(key);
        }
    }

    public List<Item> getAllCollectedData() {
        synchronized (monitor) {
            return new ArrayList<>(dataList);
        }
    }

    public int getProcessedCount() {
        synchronized (monitor) {
            return processedCount;
        }
    }

    public void waitForData() throws InterruptedException {
        synchronized (monitor) { // Поток должен владеть блокировкой на 'this' для вызова wait()
            while (dataList.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " ожидает данных...");
                monitor.wait(); // Поток освобождает блокировку и переходит в состояние ожидания
            }
        }
    }

    public Item getItem() {
        synchronized (monitor) {
            if (!dataList.isEmpty()) {
                // Удаляем и возвращаем первый элемент
                return dataList.remove(0);
            }
            return null;
        }
    }
}