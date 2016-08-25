package win.doyto.fzone.admin.common;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lombok.extern.slf4j.Slf4j;
import win.doyto.fzone.admin.component.SpringContext;
import win.doyto.fzone.mapper.UserMapper;
import win.doyto.fzone.model.User;

/**
 * Class ${PACKAGE_NAME} description goes here.
 *
 * @author Yuan Zhen
 * @version 1.0.0 11-12-9
 */
@Slf4j
public class AppContextListener implements HttpSessionListener, ServletContextListener {
    AtomicInteger online = new AtomicInteger(0);
    private static UserMapper userMapper;

    private void autoLogin(HttpSession session) {
        //String accountInfo = RequestUtils.getCookieValue(AppContext.getRequest(), AppConstant.Cookie.ACCOUNT);
        //// 用户没有设置自动登录.
        //if (StringHelper.isEmpty(accountInfo)) return;
        //
        //User user = new User();
        //
        //String uuid = EncryptUtils.decrypt(accountInfo);
        //Object[] args;
        //try {
        //    args = parser.parse(uuid);
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //    return;
        //}
        //user.setUsername((String) args[0]);
        //user.setPassword((String) args[1]);
        //user.setLastIp ((String) args[2]);
        //
        //// 解析cookie失败, 可能原因: 用户伪造cookie.
        //if (user.getUsername() == null) return;
        //// cookie里的host跟当前host不一致, 可能原因: 用户移动了cookie.
        //if (!user.getLastIp().equals(AppContext.getIp())) return;
        //User loginUser = userMapper.getByUsernameWithRoles(user.getUsername());
        //// 查询不到指定的User, 可能原因: cookie解析出错, 用户被删除.
        //if (loginUser == null) return;
        //
        //// 这里用加密后的密码的hashcode来进行验证.
        //String passcode = String.valueOf(loginUser.getPassword().hashCode());
        //if (!user.getPassword().equals(passcode)) return;
        //
        //loginUser.setLastIp(AppContext.getIp());
        ////loginUser.online = 1;
        //loginUser.setLastLogin(new Timestamp(System.currentTimeMillis()));
        //userMapper.login(loginUser);
        //AppContext.setLoginUser(session, loginUser);
    }

    private void autoLogout(HttpSession session) {
        User sessionUser = AppContext.getLoginUser(session);
        if (sessionUser != null) {
            userMapper.logout(sessionUser.getId());
        }
    }

    /**
     * 自动登录处理.
     *
     * @param se HttpSessionEvent
     */
    @SuppressWarnings("unchecked")
    public void sessionCreated(HttpSessionEvent se) {
        if (userMapper == null) {
            userMapper = SpringContext.getBean(UserMapper.class);
        }
        autoLogin(se.getSession());
        log.info("当前在线 {} 人", online.incrementAndGet());
        log.info("Session Id : {}", se.getSession().getId());
    }

    /**
     * Session is destroyed.
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        autoLogout(se.getSession());
        int sessionNum = online.get();
        if (sessionNum > 0) {
            sessionNum = online.decrementAndGet();
        }
        log.info("当前在线 {} 人", sessionNum);
        log.info("Session Id : {}", se.getSession().getId());
    }


    public void contextInitialized(ServletContextEvent sce) {
    }

    public void contextDestroyed(ServletContextEvent sce) {
        shutdownEhCache();
        shutdownHazelcast();
        shutdownMySQL();
    }

    private void shutdownHazelcast() {
        try {
            Class<?> cls = Class.forName("com.hazelcast.core.Hazelcast");
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

    private void shutdownMySQL() {
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
        try {
            Class<?> cls = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
            Method method = (cls == null ? null : cls.getMethod("shutdown"));
            if (method != null) {
                log.info("MySQL connection cleanup thread shutdown");
                method.invoke(null);
                log.info("MySQL connection cleanup thread shutdown successfully");
            }
        } catch (Throwable thr) {
            log.error("Failed to shutdown SQL connection cleanup thread: " + thr.getMessage());
            thr.printStackTrace();
        }
    }
}
