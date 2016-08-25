package win.doyto.fzone.admin.component;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-08-05.
 */
public abstract class MixIn {
    @JsonIgnore
    abstract String getHandler();
    @JsonIgnore
    abstract String getDomain();
    @JsonIgnore
    abstract String getAction();
    @JsonIgnore
    abstract String getOffset();
    @JsonIgnore
    abstract Boolean getDesc();
    @JsonIgnore
    abstract Boolean getValid();
}
