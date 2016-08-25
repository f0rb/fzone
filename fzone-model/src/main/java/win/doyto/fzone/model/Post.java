package win.doyto.fzone.model;

import java.util.Date;
import javax.validation.constraints.NotNull;

import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Post extends PageableModel<Post> {
    public static Integer STATE_NEW = 1;
    public static Integer STATE_DRAFT = 2;
    public static Integer STATE_PUB = 3;
    private String id;
    private String content;
    //@NotNull(message = "iview不能为空")
    private Integer iview;
    private String locked;
    @NotNull(message = "preview不能为空")
    private String preview;
    @NotNull(message = "title不能为空")
    private String title;
    private Integer categoryId;
    private Integer zoneId;
    private Integer userId;
    private Date createTime;
    private Date updateTime;
    private String nileId;
    private STATE state;
    //VIEW字段
    private String author;

    public enum STATE {
        NEW, DRAFT, PUB
    }

    //查询字段
    //private List<STATE> stateIn;
    //private List<STATE> stateNot;
}