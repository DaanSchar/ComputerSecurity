package org.schar.cybersecurity.common.io;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM HH:mm:ss");

    public static void info(String string, Object... object) {
        try {
            File logFile = new File("src/main/resources/logs/log.txt");
            var date = LocalDateTime.now();
            String log = "[" + dateFormatter.format(date) + "] " + String.format(string, object);
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(log);
            writer.append("\n");
            writer.flush();
            writer.close();
            System.out.println(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
