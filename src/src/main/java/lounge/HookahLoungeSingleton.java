package lounge;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HookahLoungeSingleton {
    private static volatile HookahLoungeSingleton instance;
    private final HookahLounge lounge;
    private static final Lock lock = new ReentrantLock();

    private HookahLoungeSingleton(int hookahCount) {
        this.lounge = new HookahLounge(hookahCount);
    }

    public static HookahLoungeSingleton getInstance(int hookahCount) {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new HookahLoungeSingleton(hookahCount);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public HookahLounge getLounge() {
        return lounge;
    }
}
