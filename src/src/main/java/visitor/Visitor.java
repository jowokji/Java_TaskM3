package visitor;

import lounge.Hookah;
import queue.QueueManager;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Visitor implements Callable<Void> {
    private String visitorName;
    private boolean isVIP;
    private Hookah hookah;
    private QueueManager queueManager;

    // ✅ Конструктор для TXT-инициализации
    public Visitor(String visitorName, boolean isVIP) {
        this.visitorName = visitorName;
        this.isVIP = isVIP;
    }

    public void setHookah(Hookah hookah) {
        this.hookah = hookah;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public Void call() {
        try {
            if (hookah == null) {
                System.err.println(visitorName + " не получил кальян!");
                return null;
            }

            synchronized (hookah) {
                if (!hookah.isAvailable()) {
                    if (isVIP) {
                        System.out.println(visitorName + " (VIP) получает приоритет и занимает кальян.");
                    } else {
                        System.out.println(visitorName + " не нашел свободного кальяна и встал в очередь.");
                        queueManager.addToQueue(this);
                        return null;
                    }
                }
                hookah.useHookah(visitorName);
            }

            TimeUnit.SECONDS.sleep(5);

            synchronized (hookah) {
                System.out.println(visitorName + " покурил и освобождает кальян #" + hookah.getId());
                hookah.releaseHookah();
            }

            if (!queueManager.isQueueEmpty()) {
                Visitor nextVisitor = queueManager.getNextVisitor();
                if (nextVisitor != null) {
                    queueManager.executeVisitor(nextVisitor);
                }
            }
        } catch (InterruptedException e) {
            System.err.println(visitorName + " был прерван во время использования кальяна");
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public boolean isVIP() {
        return isVIP;
    }
}
