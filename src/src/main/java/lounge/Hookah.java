package lounge;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class Hookah {
    private final int id;
    private HookahState state = HookahState.FREE;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Hookah(int id) {
        this.id = id;
    }

    public void useHookah(String visitorName) throws InterruptedException {
        lock.lock();
        try {
            while (state != HookahState.FREE) {
                condition.await(); // Ждем, если кальян занят
            }
            state = HookahState.IN_USE;
            System.out.println(visitorName + " занял кальян #" + id);
        } finally {
            lock.unlock();
        }
    }

    public void releaseHookah() {
        lock.lock();
        try {
            state = HookahState.CLEANING;
            System.out.println("Кальян #" + id + " очищается...");
            TimeUnit.SECONDS.sleep(2);
            state = HookahState.FREE;
            condition.signal();
            System.out.println("Кальян #" + id + " очищен и свободен.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public HookahState getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
    return state == HookahState.FREE;
    }
}
