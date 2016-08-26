package win.doyto.fzone.model;

import java.util.Date;
import javax.validation.constraints.NotNull;

import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Dict extends PageableModel<Dict> {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "字典名称不能为空")
    private String name;

    private Integer parentId;

    private Short rank;

    @NotNull(message = "字典主键不能为空")
    private String key;

    private String value;

    private String memo;

    private Boolean leaf;

    private Short sequence;

    private Boolean asDefault;

    private Date createTime;

    private Integer createUserId;

    private Date updateTime;

    private Integer updateUserId;

    private Boolean valid;

}