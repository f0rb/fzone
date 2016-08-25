package win.doyto.web;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 将每次请求的request和response对象存入ThreadLocal的Map里以供业务层访问.
 * <p>
 * 在{@link WebContextFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 * 方法中，对于需要的请求调用{@link WebContext#init(HttpServletRequest, HttpServletResponse)}
 * 和{@link WebContext#cleanup()}方法
 *
 * @author yuanzhen
 * @version 2.0, 2012-10-17 16:46:50
 */
@Slf4j
public final class WebContext {
    private static final ThreadLocal<WebContext> contextStore = new ThreadLocal<WebContext>();
    private static final String REDIRECT_MESSAGES = "redirect.messages";
    private static final String REDIRECT_BACK = "redirect.back";
    private static final String REQUEST_REDIRECT = "redirect";
    private static final String SESSION_REDIRECT = "session.redirect";
    private static final String SESSION_CAPTCHA = "session.captcha";
    private static final String EXCEPTION_ATTR = "org.grs.error.exception";
    private static final Map NULL_MESSAGES = new HashMap();
    private static String WEB_REAL_PATH = null;
    private static String WEB_URL = null;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private Locale locale;

    private WebContext(HttpServletRequest req, HttpServletResponse res) {
        request = req;
        response = res;
    }

    public static void init(HttpServletRequest request, HttpServletResponse response) {
        contextStore.set(new WebContext(request, response));
    }

    public static void cleanup() {
        contextStore.remove();
    }

    /**
     * Gets the HTTP servlet request object.
     *
     * @return the HTTP servlet request object.
     */
    public static HttpServletRequest getRequest() {
        return contextStore.get().request;
    }

    /**
     * Gets the HTTP servlet response object.
     *
     * @return the HTTP servlet response object.
     */
    public static HttpServletResponse getResponse() {
        return contextStore.get().response;
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

    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttr(String name) {
        return (T) getSession().getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T removeSessionAttr(String name) {
        T value = getSessionAttr(name);
        getSession().removeAttribute(name);
        return value;
    }

    public static void setSessionAttr(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    public static String getWebUrl() {
        HttpServletRequest request = getRequest();
        if (WEB_URL == null) {
            synchronized (WebContext.class) {
                if (WEB_URL == null) {
                    StringBuffer url = request.getRequestURL();
                    String urlStr = url.substring(0, url.length() - request.getRequestURI().length() + request.getContextPath().length());
                    log.info("WEB_URL: {}", urlStr);
                    if (urlStr.contains("localhost") || urlStr.contains("127.0.0.1")) {//localhost只作为临时地址
                        return urlStr;
                    }
                    WEB_URL = urlStr;
                }
            }
        }
        return WEB_URL;
    }

    public static String getWebPath() {
        if (WEB_REAL_PATH == null) {
            synchronized (WebContext.class) {
                if (WEB_REAL_PATH == null) {
                    String path = getRequest().getServletContext().getRealPath(File.separator);
                    if (!path.endsWith(File.separator)) {//Weblogic获取的路径可能没有'/'，补一个
                        path += File.separator;
                    }
                    WEB_REAL_PATH = path;
                }
            }
        }
        log.info("Web部署路径：path=" + WEB_REAL_PATH);
        return WEB_REAL_PATH;
    }

    public static String removeRedirect() {
        return WebContext.removeSessionAttr(SESSION_REDIRECT);
    }

    /**
     * 将跳转连接redirect设置到session里
     * 若redirect为空, 并且session里不存在跳转连接, 则设置当前访问地址设置到session里
     *
     * @param redirect 跳转连接
     */
    public static void setRedirect(String redirect) {
        if (StringUtils.isNotBlank(redirect)) {
            WebContext.setSessionAttr(SESSION_REDIRECT, redirect);
        } else {
            String url = WebContext.getRequest().getRequestURL().toString();
            String queryString = WebContext.getRequest().getQueryString();
            if (queryString != null) {
                url = url + "?" + queryString;
            }
            WebContext.setSessionAttr(SESSION_REDIRECT, url);
        }
    }

    /** 在session中保存信息以便Redirect后可以访问. */
    public static void setRedirectMessage(String message) {
        Map<String, String> messages = new HashMap<String, String>(2);
        messages.put("redirect", message);
        getSession().setAttribute(REDIRECT_MESSAGES, messages);
    }

    /**
     * 返回并移除存储在session中的信息.
     * <p/>
     * POST提交后redirect，在跳转后的页面显示之前POST处理时保留的信息.
     *
     * @return WebModel.messages
     */
    @SuppressWarnings("unchecked")
    public static Map getRedirectMessages() {
        Object messages = getSession().getAttribute(REDIRECT_MESSAGES);
        getSession().removeAttribute(REDIRECT_MESSAGES);
        return (Map) messages;
    }

    /**
     * 在session中保存信息以便Redirect后可以访问.
     * 如果传入的Map是null, 则存入一个空的Map
     */
    public static void setRedirectMessages(Map messages) {
        getSession().setAttribute(REDIRECT_MESSAGES, messages == null ? NULL_MESSAGES : messages);
    }

    /**
     * 获取重定向URL.
     *
     * @return 优先级：session{@link #REDIRECT_BACK}
     * > request{@link #REQUEST_REDIRECT}
     * > {@link #WEB_URL}
     */
    public static String getRedirectBack() {
        String redirectURL = removeSessionAttr(REDIRECT_BACK);
        if (redirectURL == null) {
            redirectURL = getRequest().getParameter(REQUEST_REDIRECT);
            if (redirectURL == null) {
                redirectURL = getWebUrl();
            }
        }
        return redirectURL;
    }

    public static void setRedirectBack(String referer) {
        // 防止referer属性因跳转到登录页面而被覆盖的情况.
        if (referer != null) {
            getSession().setAttribute(REDIRECT_BACK, referer);
        }
    }

    public static void setRedirectBack() {
        setRedirectBack(getRequest().getHeader("referer"));
    }

    public static Throwable getRuntimeException() {
        return (Throwable) getRequest().getAttribute(EXCEPTION_ATTR);
    }

    public static void setRuntimeException(Throwable t) {
        getRequest().setAttribute(EXCEPTION_ATTR, t);
    }


    public static String getContextRealPath(String path) {
        return getRequest().getServletContext().getRealPath(path);
    }


    public static String getFullURL() {
        String url = getRequest().getRequestURL().toString();
        String queryString = getRequest().getQueryString();
        if (queryString != null) {
            url = url + "?" + queryString;
        }
        return url;
    }

    /**
     * 设置本地化语言.
     * <p>
     * 先从参数中取locale, 若没有, 从cookie中取, 若仍没有, 取浏览器的默认值
     * </p>
     */
    public static Locale getLocale() {
        WebContext context = contextStore.get();
        if (context.locale == null) {
            if (contextStore.get() != null) {
                HttpServletRequest request = getRequest();
                String localeStr = request.getParameter("locale");
                if (StringUtils.isNotEmpty(localeStr)) {
                    context.locale = new Locale(localeStr);
                } else {
                    localeStr = RequestUtils.getCookieValue(request, "locale");
                    context.locale = StringUtils.isNotEmpty(localeStr) ? new Locale(localeStr) : Locale.CHINA;
                }
            } else {
                context.locale = Locale.CHINA;
            }
        }
        return context.locale;
    }

    public static String generateCaptcha() {
        return generateCaptcha(4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    public static String generateCaptcha(int length, String s) {
        if (length < 0) {
            length = 4;
        }
        String captcha = getRandomString(length, s);
        getSession().setAttribute(SESSION_CAPTCHA, captcha);
        return captcha;
    }

    private static String getRandomString(int length, String s) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < length; ++i) {
            sb.append(s.charAt(random.nextInt(len)));
        }
        return sb.toString();
    }

    public static boolean validateCaptcha() {
        String requestCaptcha = getRequest().getParameter("captcha");
        return validateCaptcha(requestCaptcha);
    }

    public static boolean validateCaptcha(String captcha) {
        String sessionCaptcha = removeSessionAttr(SESSION_CAPTCHA);
        // 如果session里面没有产生验证码, 默认通过
        return sessionCaptcha == null || sessionCaptcha.equalsIgnoreCase(captcha);
    }
}
