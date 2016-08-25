package win.doyto.web;

/**
 * 返回给移动客户端的JSON对象的结构
 *
 * @author Yuanzhen on 2015-09-07.
 */
@lombok.Getter
@lombok.Setter
public class ResponseObject {
    private Boolean success = true;
    private String message;
    private String code = "0000";
    private Object result;
    private Long total;

    public ResponseObject() {
    }

    public ResponseObject(String code) {
        success = false;
        this.code = code;
        message = ResponseStatus.resolve(code);//getMessageByCode
    }

    public ResponseObject(ResponseStatus status) {
        success = false;
        code = status.getCode();
        message = status.getMessage();
    }

    public Boolean getSuccess() {
        return success && (message == null || message.length() == 0);
    }

}
