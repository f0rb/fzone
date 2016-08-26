package win.doyto.fzone.model;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import win.doyto.fzone.common.PageableModel;
import win.doyto.rbac.RBACRole;

@Getter
@Setter
@NoArgsConstructor
public class Role extends PageableModel<Role> implements RBACRole {
    private static final long serialVersionUID = 1L;

    //public static final Role ROOT = new Role(RBACConstant.ROOT, "超级管理员", (short) 1);
    //public static final Role ADMIN = new Role(RBACConstant.ADMIN, "系统管理员", (short) 10);
    //public static final Role VIP = new Role(RBACConstant.VIP, "高级用户", (short) 30);
    //public static final Role NORMAL = new Role(RBACConstant.NORMAL, "普通用户", (short) 50);
    //public static final Role GUEST = new Role(RBACConstant.GUEST, "访客", (short) 99);
    //public static final Role DEFAULT = new Role(RBACConstant.DEFAULT, "默认用户", Short.MAX_VALUE);

    public static final Integer ROOT_ID = 1;

    private Integer id;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    @NotNull(message = "角色级别不能为空")
    private Short rank;

    @NotBlank(message = "角色代码不能为空")
    private String code;

    private String memo;

    private Date createTime;

    private Integer createUserId;

    private Date updateTime;

    private Integer updateUserId;

    private Boolean valid;

    private List<Integer> menus;

    public Role(int id) {
        this.id = id;
    }

    public Role(String code, String name, Short rank) {
        this.code = code;
        this.name = name;
        this.rank = rank;
    }

    @Override
    public String id() {
        return String.valueOf(id);
    }

}