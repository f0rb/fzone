package win.doyto.fzone.model;

import java.util.regex.Pattern;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-23.
 */
public enum UserValidator {
    //用户名: 字母开头字母数字下划线组成的4-31位长度的字符串
    USERNAME("^[a-zA-Z]\\w{3,30}$"),
    EMAIL("^\\w+([-+\\._]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"),
    MOBILE("^(13\\d|15[^4,\\D]|17[13678]|18\\d)\\d{8}|170[^346,\\D]\\d{7})$"),
    NICKNAME("[-_\\.\\w\\u4e00-\\u9fa5]+"),;

    private Pattern p;

    UserValidator(String regex) {
        p = Pattern.compile(regex);
    }

    public boolean matches(CharSequence input) {
        return p.matcher(input).matches();
    }
}