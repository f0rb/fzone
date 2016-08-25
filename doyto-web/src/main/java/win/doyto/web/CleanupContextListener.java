package win.doyto.web;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Web应用关闭时，清理第三方类库的线程.
 *
 * @author Yuan Zhen
 * @version 1.0.0  2016-08-16
 */
@Slf4j
public class CleanupContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
    }

    public void contextDestroyed(ServletContextEvent sce) {
        shutdownEhCache();
        shutdownHazelcast();
        deregisterDrivers();
        shutdownMySQL();
    }

    private void shutdownEhCache() {
        // 显式调用Ehcache的关闭方法，不然会导致Tomcat无法关闭。
        try {
            Class<?> cls = Class.forName("net.sf.ehcache.CacheManager");
            Method getInstanceMtd = (cls == null ? null : cls.getMethod("getInstance"));
            Method shutdownMtd = (cls == null ? null : cls.getMethod("shutdown"));
            if (getInstanceMtd != null && shutdownMtd != null) {
                log.info("EhCache.CacheManager shutdown");
                Object cacheManager = getInstanceMtd.invoke(null);
                shutdownMtd.invoke(cacheManager);
                log.info("EhCache.CacheManager shutdown successfully.");
            }
        } catch (Exception e) {
            log.info(e.toString());
        }
    }

    private void shutdownHazelcast() {
        try {
            Class<?> cls = Class.forName("com.hazelcast.instance.HazelcastInstanceFactory");
            Method shutdownMtd = (cls == null ? null : cls.getMethod("shutdownAll"));
            if (shutdownMtd != null) {
                log.info("com.hazelcast.core.Hazelcast.shutdownAll");
                shutdownMtd.invoke(null);
                log.info("com.hazelcast.core.Hazelcast.shutdownAll successfully.");
            }
        } catch (Exception e) {
            log.info(e.toString());
        }
    }

    private void deregisterDrivers() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
                log.info(String.format("Driver %s deregistered", d));
            } catch (SQLException ex) {
                log.warn(String.format("Error deregistering driver %s", d), ex);
            }
        }
    }

    private void shutdownMySQL() {
        try {
            Class<?> cls = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
            Method method = (cls == null ? null : cls.getMethod("shutdown"));
            if (method != null) {
                log.info("MySQL connection cleanup thread shutdown");
                method.invoke(null);
                log.info("MySQL connection cleanup thread shutdown successfully");
            }
        } catch (Throwable thr) {
            log.error("Failed to shutdown SQL connection cleanup thread", thr);
        }
    }
}
