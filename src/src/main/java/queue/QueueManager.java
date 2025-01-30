package queue;

import visitor.Visitor;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueManager {
    private final LinkedList<Visitor> queue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private int totalVisitors = 0; // Счетчик клиентов

    private static QueueManager instance;

    private QueueManager() {}

    public static synchronized QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    public void addToQueue(Visitor visitor) {
        lock.lock();
        try {
            queue.addLast(visitor);
            totalVisitors++; // Увеличиваем количество клиентов
            System.out.println(visitor.getVisitorName() + " встал в очередь.");
        } finally {
            lock.unlock();
        }
    }

    public Visitor getNextVisitor() {
        lock.lock();
        try {
            return queue.isEmpty() ? null : queue.removeFirst();
        } finally {
            lock.unlock();
        }
    }

    public boolean isQueueEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void executeVisitor(Visitor visitor) {
        executor.submit(() -> {
            try {
                visitor.call();
            } finally {
                checkShutdown(); // Проверяем, можно ли завершить программу
            }
        });
    }

    // ✅ Проверяем, все ли клиенты обработаны
    private void checkShutdown() {
        lock.lock();
        try {
            totalVisitors--; // Клиент обработан
            if (totalVisitors == 0 && queue.isEmpty()) {
                shutdown();
            }
        } finally {
            lock.unlock();
        }
    }

    // ✅ Завершаем потоки, когда все клиенты обслужены
    public void shutdown() {
        executor.shutdown();
        System.out.println("✅ Все посетители обслужены. Кальянная закрывается.");
        System.exit(0); // Завершаем программу
    }
}
