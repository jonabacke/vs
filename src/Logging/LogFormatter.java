package Logging;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Override
    public String format(LogRecord record) {
        List<String> names = Arrays.asList(record.getSourceClassName().split("\\."));
        String moduleName;


        if (names.size() == 0) {
            moduleName = record.getSourceClassName() + " " + record.getSourceMethodName();
        } else {
            moduleName = names.get(names.size() - 1) + " " + record.getSourceMethodName();
        }

        String color = ANSI_BLUE;
        if (record.getLevel() == Level.WARNING) {
            color = ANSI_YELLOW;
        } else if (record.getLevel() == Level.SEVERE) {
            color = ANSI_RED;
        } else if (record.getLevel() == Level.INFO) {
            color = ANSI_GREEN;
        }

        return color + "[" + moduleName + "] " + record.getMessage() + ANSI_RESET + "\n";
    }

}
