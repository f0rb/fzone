package win.doyto.rbac;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 受到权限系统保护的业务.
 * <p/>
 * 可以配置业务
 *
 * @author f0rb, created at 2012-10-22 14:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RBACCheck {
    String value() default "";//权限名称

    String memo() default "";//权限的备注

    boolean isLimited() default true;//是否启用该权限

    String[] roles() default {RBACConstant.ADMIN};//默认授予的角色
}
