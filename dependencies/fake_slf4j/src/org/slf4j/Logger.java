package org.slf4j;

public interface Logger {
 void info(String str);
 void warn(String str, Throwable t);
 void warn(String str);
 void error(String str, Throwable t);
 void error(String str);
}
