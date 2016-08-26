package win.doyto.rbac;

import java.util.List;
import java.util.Set;

/**
 * RBAC管理接口.
 *
 * @author f0rb,
 * @version 1.0.0, 2012-10-31 14:40
 */
public interface RBACManager<R extends RBACRole> {

    int insertPermission(RBACPermission permission);

    void removePermission(String permission);

    void removePermissions(List<RBACPermission> permission);

    boolean hasPermission(String permission);

    boolean checkIfRoleHasPermission(String role, String permission);

    List<String> listPermission();

    boolean hasRole(R role);

    int insertRole(R role);

    int updateRole(R role);

    /**
     * 删除角色及其授权.
     *
     * @param role 角色名
     */
    void removeRole(String role);

    List<R> listRole();

    int grantPermissionToRole(String role, String permission);

    void revokePermissionFromRole(String role, String permission);

    void revokePermissionByRole(String role);

    Set<String> listRolePermission();
}
