package lounge;

import visitor.Visitor;
import visitor.VisitorState;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HookahLounge {
    private static final Logger logger = LogManager.getLogger();
    private final Semaphore availableHookahs;
    private final Lock loungeLock = new ReentrantLock();

    public HookahLounge(int hookahCount) {
        this.availableHookahs = new Semaphore(hookahCount);
    }

    public boolean tryEnterLounge() {
        if (loungeLock.tryLock()) {
            loungeLock.unlock();
            return true;
        }
        return false;
    }

    public void useHookah(Visitor visitor) {
        try {
            availableHookahs.acquire();
            visitor.setState(VisitorState.USING_HOOKAH);
           logger.info(visitor.getName() + " is using a hookah.");
            TimeUnit.SECONDS.sleep(2);
            visitor.setState(VisitorState.FINISHED);
            logger.info(visitor.getName() + " has finished using the hookah.");
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            availableHookahs.release();
        }
    }
}

