package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DemoApplication.class, args);
        System.out.println("Запуск синхронизированной системы сбора данных");
        DataCollector safeCollector = new DataCollector();
        int numCollectors = 3;
        int numProcessors = 2;
        int itemsPerCollector = 5;
        int totalItems = numCollectors * itemsPerCollector;
        int itemsPerProcessor = totalItems / numProcessors; // Примерное распределение

        List<Thread> collectorThreads = new ArrayList<>();
        List<Thread> processorThreads = new ArrayList<>();

        // Создание и запуск потоков-сборщиков
        for (int i = 0; i < numCollectors; i++) {
            CollectorThread collector = new CollectorThread("Collector-" + (i + 1), safeCollector, itemsPerCollector);
            collectorThreads.add(collector);
            collector.start();
        }

        System.out.println("Фактическое количество собранных элементов: " + safeCollector.getAllCollectedData().size());

        // Создание и запуск потоков-обработчиков
        for (int i = 0; i < numProcessors; i++) {
            // Некоторые обработчики могут получать немного больше элементов для распределения
            int currentItemsToProcess = (i == numProcessors - 1) ?
                    itemsPerProcessor + (totalItems % numProcessors) :
                    itemsPerProcessor;
            ProcessorThread processor = new ProcessorThread("Processor-" + (i + 1), safeCollector, currentItemsToProcess);
            processorThreads.add(processor);
            processor.start();
        }

        // Ожидание завершения всех потоков-сборщиков
        for (Thread thread : collectorThreads) {
            thread.join();
        }
        System.out.println("\nВсе сборщики завершили работу.\n");

        // Ожидание завершения всех потоков-обработчиков
        // Важно: Добавить небольшую задержку, чтобы уведомить ожидающие потоки,
        // если они еще не пробудились после завершения сборщиков.
        Thread.sleep(1000); // Даем время на финальные уведомления и пробуждения
        for (Thread thread : processorThreads) {
            thread.join();
        }
        System.out.println("\nВсе обработчики завершили работу.\n");

        System.out.println("Итоговая статистика (Синхронизированная система)");
        System.out.println("Ожидаемое количество элементов: " + totalItems);
        System.out.println("Фактическое количество собранных элементов: " + safeCollector.getAllCollectedData().size());
        System.out.println("Фактическое количество обработанных элементов (processedCount): " + safeCollector.getProcessedCount());

        // Проверка, совпадает ли processedCount с общим количеством уникальных ключей
        Set<String> uniqueKeys = new HashSet<>();
        safeCollector.getAllCollectedData().forEach(item -> uniqueKeys.add(item.getKey()));
        System.out.println("Количество уникальных ключей в собранных данных: " + uniqueKeys.size());

        // Если processedCount == totalItems и uniqueKeys.size() == totalItems (или меньше, если были дубликаты, но collectItem их обрабатывает),
        // то система работает корректно.
        if (safeCollector.getProcessedCount() == totalItems && safeCollector.getAllCollectedData().isEmpty()) {
            System.out.println("Результат: Синхронизированная система работает корректно!");
        } else {
            System.err.println("Результат: Обнаружены некорректности в синхронизированной системе!");
        }
    }
}
