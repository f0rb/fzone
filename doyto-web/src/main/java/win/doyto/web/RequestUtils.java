package win.doyto.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * Date: 2010-2-25
 * Time: 21:48:34
 *
 * @author f0rb
 */
@SuppressWarnings("unused")
public class RequestUtils {
    private static Pattern ipPattern = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

    private RequestUtils() {
    }

    public static Cookie createCookie(HttpServletRequest request, String domain, String name, String value) {
        return createCookie(request, domain, name, value, 0);
    }

    public static Cookie createCookie(HttpServletRequest request, String domain, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        // http proxy
        String host = request.getHeader("x-forwarded-host");
        if (host == null) {
            host = request.getServerName();
        }
        if (host.toLowerCase().contains(domain)) { // 非IP
            cookie.setDomain("." + domain);
        }
        cookie.setPath("/");
        return cookie;
    }

    /**
     * 获取COOKIE.
     *
     * @param request HttpServletRequest Object
     * @param name    the name of the cookie
     * @return The cookie named $name if exists, otherwise null.
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) return cookie;
        }
        return null;
    }

    /**
     * 获取COOKIE的值.
     *
     * @param request HttpServletRequest Object
     * @param name    the name of the cookie
     * @return The cookie's value named $name if exists, otherwise null.
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * 获取header信息，名字大小写无关.
     *
     * @param request HttpServletRequest Object
     * @param name    the name of the header
     * @return the header's value correspond to the name
     */
    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value != null) return value;
        Enumeration<?> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String n = (String) names.nextElement();
            if (n.equalsIgnoreCase(name)) {
                return request.getHeader(n);
            }
        }
        return null;
    }

    /**
     * 获取客户端IP地址，此方法用在proxy环境中.
     *
     * @param request HttpServletRequest Object
     * @return Remote Address
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null) {
            String[] ips = ip.split(",");
            for (String tmpIp : ips) {
                tmpIp = tmpIp.trim();
                if ("".equals(tmpIp)) continue;
                if (isIp(tmpIp) && !tmpIp.startsWith("10.") && !tmpIp.startsWith("192.168.") && !"127.0.0.1".equals(tmpIp)) {
                    return tmpIp;
                }
            }
        }
        ip = request.getHeader("x-real-ip");
        if (isIp(ip)) return ip;
        ip = request.getRemoteAddr();
        if (ip.indexOf('.') == -1) ip = "127.0.0.1";
        return ip;
    }

    public static boolean isIp(String addr) {
        if (addr == null || addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        Matcher mat = ipPattern.matcher(addr);
        return mat.find();
    }

    /**
     * 判断是否为搜索引擎.
     *
     * @param request HttpServletRequest Object
     * @return true if the "user-agent" contains certain string below, otherwise false
     */
    public static boolean isRobot(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        return ua != null && (ua.contains("Baiduspider") || ua.contains("Googlebot") || ua.contains("sogou") || ua.contains("sina") || ua.contains("iaskspider") || ua.contains("ia_archiver") || ua.contains("Sosospider") || ua.contains("YoudaoBot") || ua.contains("yahoo") || ua.contains("yodao") || ua.contains("MSNBot") || ua.contains("spider") || ua.contains("Twiceler") || ua.contains("Sosoimagespider") || ua.contains("naver.com/robots") || ua.contains("Nutch") || ua.contains("spider"));
    }

    public static String getWebUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
    }

    public static String serializeParameters(HttpServletRequest request, List<String> ignoredParams) {
        List<String> paramPairs = new ArrayList<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String[] paramPair = new String[2];
            paramPair[0] = paramNames.nextElement();
            if (ignoredParams.contains(paramPair[0])) continue;
            paramPair[1] = StringUtils.join(request.getParameterValues(paramPair[0]), ",");
            paramPairs.add(StringUtils.join(paramPair, "="));
        }
        return StringUtils.join(paramPairs, "&");
    }
}
