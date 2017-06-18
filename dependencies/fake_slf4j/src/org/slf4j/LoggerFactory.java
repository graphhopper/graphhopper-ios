package org.slf4j;

public class LoggerFactory {
    public static Logger getLogger(Class cl) {
        final String name = cl.getSimpleName();
        return new Logger() {
            public void info(String str) {
               log("INFO", str, null);
            }
            
            public void warn(String str, Throwable t) {
                log("WARN", str, t);
            }                                      
            
            public void warn(String str) {
                log("WARN", str, null);
            }

            public void warn(String format, Object... arguments) {
                log("WARN", format, null);
            }
            
            public void error(String str, Throwable t) {
                log("ERROR", str, t);
            }                                      
            
            public void error(String str) {
                log("ERROR", str, null);
            }
            
            public void log(String type, String message, Throwable t) {
               if(t == null)
                 System.out.println(new java.util.Date().toString() + " " + type + " " + message);
               else
                 System.out.println(new java.util.Date().toString() + " " + type + " " + message + " " + t.toString());                                  
            }
        };
    }
}