package win.doyto.fzone.model;

import org.hibernate.validator.constraints.NotEmpty;
import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Generator extends PageableModel {
    private Integer id;

    @NotEmpty
    private String modelName;

    //@NotEmpty
    private String fullName;

    @NotEmpty
    private String name;

    @NotEmpty
    private String displayName;

    private String pathJS;

    private String pathHTML;

    private String pathController;
}