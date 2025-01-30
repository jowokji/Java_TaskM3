package org.example;

import lounge.HookahLounge;
import lounge.HookahLoungeSingleton;
import visitor.Visitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        List<String> fileLines;
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("visitors.txt");

        if (inputStream == null) {
            logger.error("Error: config.txt not found in resources.");
            throw new RuntimeException("Error: config.txt not found in resources.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            fileLines = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to read config file");
           throw new RuntimeException("Failed to read config file");
        }

        int hookahCount = Integer.parseInt(fileLines.get(0));
        HookahLoungeSingleton loungeSingleton = HookahLoungeSingleton.getInstance(hookahCount);
        HookahLounge lounge = loungeSingleton.getLounge();

        ExecutorService executorService = Executors.newFixedThreadPool(fileLines.size() - 1);
        List<Visitor> visitors = new ArrayList<>();

        for (int i = 1; i < fileLines.size(); i++) {
            Visitor visitor = new Visitor(fileLines.get(i), lounge);
            visitors.add(visitor);
            executorService.submit(visitor);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Error: Interrupted");
            Thread.currentThread().interrupt();
        }

       logger.info("Simulation finished.");
    }
}
