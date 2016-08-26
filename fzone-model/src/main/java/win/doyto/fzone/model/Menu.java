package win.doyto.fzone.model;

import java.util.Date;
import java.util.List;

import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Menu extends PageableModel<Menu> {
    private static final long serialVersionUID = 1L;

    public static final Menu Root = new Menu();
    static {
        Root.setId(0);
        Root.setName("root");
        Root.setLabel("根目录");
        Root.setRank((short) 1);
        Root.setUrl("#");
        Root.setValid(true);
    }

    private Integer id;

    private String name;

    private String url;

    private String html;

    private Short sequence;

    private Integer parentId;

    private String label;

    private Short rank;

    private String parentName;

    private Date createTime;

    private Integer createUserId;

    private String createUserName;

    private Date updateTime;

    private Integer updateUserId;

    private String updateUserName;

    private Boolean valid;

    private List<Menu> submenu;

    private Boolean onlyLeaf;
}