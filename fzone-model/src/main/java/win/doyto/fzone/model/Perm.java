package win.doyto.fzone.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import win.doyto.fzone.common.PageableModel;
import win.doyto.rbac.RBACPermission;

public class Perm extends PageableModel<Perm> implements RBACPermission {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull(message = "权限名称不能为空")
    @Size(max = 50)
    private String name;

    private String memo;

    private Date createTime;

    private Integer createUserId;

    private Boolean valid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Override
    public String id() {
        return id;
    }
}