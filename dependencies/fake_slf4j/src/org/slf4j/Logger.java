package org.slf4j;

public interface Logger {
 void info(String str);
 void info(String format, Object... arguments);
 void warn(String str, Throwable t);
 void warn(String str);
 void warn(String format, Object... arguments);
 void error(String str, Throwable t);
 void error(String str);
 void trace(String str);
 void trace(String format, Object... arguments);
 void debug(String format, Object... arguments);
}

