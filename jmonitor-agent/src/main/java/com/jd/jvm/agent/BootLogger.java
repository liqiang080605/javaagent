package com.jd.jvm.agent;
/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */



import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * @author Woonduk Kang(emeroad)
 */
public final class BootLogger {

//    private static final int LOG_LEVEL;
//    private final String loggerName;
    private final String messagePattern;
    private final PrintStream out;
    private final PrintStream err;


    static {
        setup();
    }

    private static void setup() {
        // TODO setup BootLogger LogLevel
        // low priority
//        String logLevel = System.getProperty("pinpoint.agent.bootlogger.loglevel");
//        logLevel = ???
    }


    public BootLogger(String loggerName) {
        this(loggerName, System.out, System.err);
    }

    // for test
    BootLogger(String loggerName, PrintStream out, PrintStream err) {
        if (loggerName == null) {
            throw new NullPointerException("loggerName must not be null");
        }
//        this.loggerName = loggerName;
        this.messagePattern = "{0,date,yyyy-MM-dd HH:mm:ss SSS} [{1}](" + loggerName + ") {2}";
        this.out = out;
        this.err = err;
    }

    static BootLogger getLogger(String loggerName) {
        return new BootLogger(loggerName);
    }

    private String format(String logLevel, String msg) {
        MessageFormat messageFormat = new MessageFormat(messagePattern);
        final long date = System.currentTimeMillis();
        Object[] parameter = {date, logLevel, msg};
        return messageFormat.format(parameter);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        String formatMessage = format("INFO ",  msg);
        this.out.println(formatMessage);
    }


    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String msg) {
        String formatMessage = format("WARN ", msg);
        this.err.println(formatMessage);
    }

    public void warn(String msg, Throwable throwable) {
        warn(msg);
        throwable.printStackTrace(this.err);
    }
    
    public void error(String msg) {
    	String formatMessage = format("ERROR", msg);
        this.err.println(formatMessage);
    }
    
    public void error(String msg, Throwable throwable) {
        error(msg);
        throwable.printStackTrace(this.err);
    }
}
