package win.doyto.fzone.model;


import win.doyto.fzone.common.CommonModel;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-16.
 */
public class Column extends CommonModel<Column> {
    private static final long serialVersionUID = 1L;

    private String field;
    private String type;
    private String nullable;
    private String key;
    private String defaulted;
    private String extra;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaulted() {
        return defaulted;
    }

    public void setDefaulted(String defaulted) {
        this.defaulted = defaulted;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
