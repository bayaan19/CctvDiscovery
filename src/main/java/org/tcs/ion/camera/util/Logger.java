package org.tcs.ion.camera.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private Logger() {
    }

    public static String currentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String currentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static void log(String text) {
        System.out.printf("%s\t%s%n", currentDateTime(), text.replaceAll("[\\t\\n\\r]+", " "));
    }

    public static void log(Exception e) {
        System.err.printf("%s --\t%s%n", currentDateTime(), e.getMessage());
    }

    public static void msg(String text) {
        System.out.printf("%n-- %s%n%n", text);
    }
}
