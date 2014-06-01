package com.github.christophpickl.seetheeye;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public final class Log4j {

    private Log4j() {}

    public static void init() {
        ConsoleAppender appender = new ConsoleAppender(new PatternLayout("[%5p] %c - %m%n"));
        appender.activateOptions();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.ALL);
        root.addAppender(appender);
    }

}
