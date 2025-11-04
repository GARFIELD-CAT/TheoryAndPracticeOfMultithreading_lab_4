package com.example.demo;

import com.example.demo.model.Item;

public class ProcessorThread extends Thread{
    private final DataCollector dataCollector;
    private final int itemsToProcess;
    private final String threadName;

    public ProcessorThread(String name, DataCollector dataCollector, int itemsToProcess) {
        super(name);
        this.dataCollector = dataCollector;
        this.itemsToProcess = itemsToProcess;
        this.threadName = name;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemsToProcess; i++) {
                // Ожидаем, пока появятся данные
                dataCollector.waitForData();

                // Пытаемся получить элемент
                Item item = dataCollector.getItem();
                dataCollector.incrementProcessed();

                if (item != null) {
                    // Имитация обработки данных
                    Thread.sleep((long) (Math.random() * 150));
                    // Проверяем, не обработан ли элемент уже другим потоком (на всякий случай, если логика сложнее)
                    if (!dataCollector.isAlreadyProcessed(item.getKey())) {
                        // Обработка элемента (здесь просто вывод)
                        System.out.println(this.threadName + " обработал: " + item.getKey() + " -> " + item.getValue());
//                        dataCollector.incrementProcessed();
                    } else {
                        System.out.println(this.threadName + " обнаружил, что " + item.getKey() + " уже обработан.");
                    }
                } else {
                    // Это условие не должно выполняться, если waitForData() работает корректно,
                    // но добавлено для полноты.
                    System.out.println(this.threadName + " не смог получить данные после ожидания.");
                    Thread.sleep(50); // Небольшая пауза, чтобы избежать спама
                }
            }
            System.out.println(this.threadName + " завершил обработку.");
        } catch (InterruptedException e) {
            System.err.println(this.threadName + " прерван.");
            Thread.currentThread().interrupt();
        }
    }
}
