package win.doyto.fzone.model;

<div ng-repeat="import in imports track by $index">import {{import}};</div>

import javax.validation.constraints.NotNull;

import win.doyto.fzone.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class {{gen.name | capitalize}} extends PageableModel&lt;{{gen.name | capitalize}}&gt; {
    private static final long serialVersionUID = 1L;

<div ng-repeat="column in columns"><span ng-if="['id', 'createTime', 'createUserId', 'valid'].indexOf(column.field) < 0 && column.nullable == 'NO'">
    @NotNull(message = "{{column.field}}不能为空")</span>
    private {{column.type}} {{column.field}};
</div>
}