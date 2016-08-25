package win.doyto.fzone.admin.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import win.doyto.fzone.model.User;
import win.doyto.util.EncryptUtils;
import win.doyto.web.RequestUtils;

import static win.doyto.web.WebContext.*;

/**
 * Class {@link win.doyto.fzone.admin.common.AppContext} goes here.
 *
 * @author Administrator
 * @version 1.0.1 2013-01-05 9:45
 */
@Slf4j
public final class AppContext {

    private static final String DOMAIN = "fzone.win";
    private static final MessageFormat accountMF = new MessageFormat("{0}#{1}@{2}");

    @SuppressWarnings("unused")
    private AppContext() {
    }

    public static String getIp() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static void setLoginUser(HttpSession session, final User loginUser) {
        if (session != null) {
            session.setAttribute(AppConstant.Session.LOGIN_USER, loginUser.toSessionUser());
            session.setAttribute(AppConstant.Session.LOGIN_USER_ID, loginUser.getId());
            session.setAttribute(AppConstant.Session.LOGIN_USERNAME, loginUser.getUsername());
        } else {
            log.error("Session is null!");
        }
    }

    public static User getLoginUser() {
        return (User) getSessionAttr(AppConstant.Session.LOGIN_USER);
    }

    public static void setLoginUser(final User loginUser) {
        setLoginUser(getSession(), loginUser);
    }

    public static Integer getLoginUserId() {
        return (Integer) getSessionAttr(AppConstant.Session.LOGIN_USER_ID);
    }

    public static String getLoginUsername() {
        return (String) getSessionAttr(AppConstant.Session.LOGIN_USERNAME);
    }

    public static User getLoginUser(HttpSession session) {
        if (session == null) {
            log.warn("Session is null!");
            return null;
        }
        return (User) session.getAttribute(AppConstant.Session.LOGIN_USER);
    }

    public static void removeLoginUser() {
        removeSessionAttr(AppConstant.Session.LOGIN_USER);
    }

    public static void setLoginCookies(final User user, boolean autologin) {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        int maxAge = autologin ? AppConstant.Cookie.HOLD_DAY : -1;

        String uuid = accountMF.format(new Object[]{user.getUsername(), user.getPassword().hashCode(), user.getEmail()});
        String account = EncryptUtils.encrypt(uuid);
        response.addCookie(RequestUtils.createCookie(request, DOMAIN, AppConstant.Cookie.ACCOUNT, account, maxAge));
        if (user.getLastLogin() != null) {
            response.addCookie(RequestUtils.createCookie(request, DOMAIN, AppConstant.Cookie.LAST_LOGIN, user.getLastLogin().toString(), -1));
        }
        if (user.getNickname() != null) {
            try {
                response.addCookie(RequestUtils.createCookie(request, DOMAIN, AppConstant.Cookie.NICKNAME, URLEncoder.encode(user.getNickname(), "UTF-8"), maxAge));
            } catch (UnsupportedEncodingException e) { //never happen
            }
        }
    }

    public static void clearLoginCookies() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        response.addCookie(RequestUtils.createCookie(request, AppConstant.Cookie.ACCOUNT, DOMAIN, "", 0));
        response.addCookie(RequestUtils.createCookie(request, AppConstant.Cookie.LAST_LOGIN, DOMAIN, "", 0));
        response.addCookie(RequestUtils.createCookie(request, AppConstant.Cookie.NICKNAME, DOMAIN, "", 0));
    }
}
