package win.doyto.fzone.admin.common;

import java.util.Locale;

/**
 * UIString.
 *
 * @author f0rb
 * @version 1.0.0 2010-5-29
 */
public interface AppConstant {
    String SLASH = "/";
    String BACKSLASH = "\\";
    String CAPTCHA_IMAGE = "captcha.png";
    String RELOAD_CONFIG = "reload";

    String ROOT = "root";

    String REDIRECT_URL = "redirectURL";
    String DEFAULT_LOCALE_NAME = "html_locale";
    Locale DEFAULT_LOCALE = Locale.CHINA;


    interface Cookie {
        String LAST_LOGIN = "last_login"; //last login key
        String ACCOUNT = "account";
        String NICKNAME = "nickname";
        int HOLD_DAY = 14 * 60 * 60 * 24;
    }

    interface Session {
        // 页面上的el表达式可能用到这些常量的值取数据
        String LOGIN_USER = "LOGIN_USER";
        String LOGIN_USER_ID = "LOGIN_USER_ID";
        String LOGIN_USERNAME = "LOGIN_USERNAME";
        String REDIRECT_BACK = "REDIRECT_BACK";
        String REDIRECT = "REDIRECT";
        String CAPTCHA = "CAPTCHA";
    }
}
