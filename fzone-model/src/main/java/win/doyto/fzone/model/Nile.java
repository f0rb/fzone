package win.doyto.fzone.model;

import java.io.File;
import java.util.Date;
import javax.validation.constraints.NotNull;

import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Nile extends PageableModel<Nile> {

    public String id;

    public String parentId;

    public String name;

    public String type;

    private Integer size;

    @NotNull(message = "ownerId不能为空")
    private Integer ownerId;

    private String md5;

    private String sha1;

    private String mime;

    private boolean directory;

    private Date createTime;

    private Date updateTime;

    //public boolean isDirectory() {
    //    return type == 0;
    //}

    private File file;

    private String sourceId;
}