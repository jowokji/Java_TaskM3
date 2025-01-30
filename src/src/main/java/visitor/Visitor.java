package visitor;

import lounge.HookahLounge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Visitor implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private final String name;
    private final HookahLounge lounge;
    private VisitorState state;

    public Visitor(String name, HookahLounge lounge) {
        this.name = name;
        this.lounge = lounge;
        this.state = VisitorState.WAITING;
    }

    public String getName() {
        return name;
    }

    public void setState(VisitorState state) {
        this.state = state;
    }

    @Override
    public void run() {
        if (lounge.tryEnterLounge()) {
            lounge.useHookah(this);
        } else {
           logger.info(name + " is waiting outside.");
        }
    }
}

