package win.doyto.fzone.admin.component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-08-05.
 */
public abstract class PasswordMixIn {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    abstract String getPassword();
}
