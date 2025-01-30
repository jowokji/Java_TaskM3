package lounge;

import queue.QueueManager;
import visitor.Visitor;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HookahLounge {
    private final List<Hookah> hookahs;
    private final QueueManager queueManager = QueueManager.getInstance();

    public HookahLounge(int numOfHookahs) {
        this.hookahs = createHookahs(numOfHookahs);
        loadVisitorsFromTxt(); // ЗАГРУЗКА ПОСЕТИТЕЛЕЙ ИЗ TXT
    }

    private List<Hookah> createHookahs(int numOfHookahs) {
        return java.util.stream.IntStream.range(1, numOfHookahs + 1)
                .mapToObj(Hookah::new)
                .toList();
    }

    // ✅ ЧТЕНИЕ ПОСЕТИТЕЛЕЙ ИЗ TXT-ФАЙЛА
    private void loadVisitorsFromTxt() {

        ClassLoader classLoader = HookahLounge.class.getClassLoader();
        URL resource = classLoader.getResource("visitors.txt");

        if (resource == null) {
           // logger.error("File not found: {}","visitors.txt");
            throw new IllegalArgumentException("File not found: " + "visitors.txt");
        }


        try (BufferedReader reader = new BufferedReader(new FileReader( new File(resource.toURI())))) {
            String line;
            List<Visitor> visitors = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" "); // Разделяем строку по пробелу
                if (parts.length == 2) {
                    String name = parts[0]; // Имя посетителя
                    boolean isVIP = Boolean.parseBoolean(parts[1]); // Преобразуем true/false

                    Visitor visitor = new Visitor(name, isVIP); // Создаем посетителя
                    visitors.add(visitor);
                }
            }

            Random random = new Random();
            for (Visitor visitor : visitors) {
                Hookah assignedHookah = hookahs.get(random.nextInt(hookahs.size())); // Назначаем случайный кальян
                visitor.setHookah(assignedHookah);
                visitor.setQueueManager(queueManager);
                queueManager.executeVisitor(visitor); // Запускаем посетителя
            }
            queueManager.shutdown();
            System.out.println(" Посетители загружены из TXT!");

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    public void startSimulation() {
        System.out.println("🔥 Кальянная открылась!");
    }

    public static void main(String[] args) {
        HookahLounge lounge = new HookahLounge(3); // 3 кальяна
        lounge.startSimulation();
    }
}
