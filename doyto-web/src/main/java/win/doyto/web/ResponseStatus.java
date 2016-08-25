package win.doyto.web;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-05-27.
 */
public enum ResponseStatus {
    LOGIN_EXPIRED("0001", "登录超时, 请刷新重试!"),
    ACCESS_DENIED("0002", "拒绝访问!"),;

    private String code;
    private String message;

    ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String resolve(String code) {
        for (ResponseStatus responseStatus : ResponseStatus.values()) {
            if (responseStatus.code.equals(code)) {
                return responseStatus.message;
            }
        }
        return "未知错误[" + code + "]";
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
