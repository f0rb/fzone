package win.doyto.rbac;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class com.uniweb.rbac.RBACSession description goes here.
 *
 * @author Yuanzhen on 2012-11-02
 */
public class RBACSession<R extends RBACRole> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RBACSession.class);

    private final RBACManager<R> manager;
    private final Map<String, R> roleMap = new TreeMap<>((s1, s2) -> {
        try {
            Integer i1 = Integer.valueOf(s1);
            Integer i2 = Integer.valueOf(s2);
            return i1 - i2;
        } catch (NumberFormatException e) {
            return s1.compareTo(s2);
        }
    });
    private final Set<String> permissionSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final RolePermissionHolder holder;


    public static <R extends RBACRole>
    RBACSession<R> createSession(RBACManager<R> rbacManager) {
        return createSession(rbacManager, new ArrayList<>());
    }

    public static <R extends RBACRole>
    RBACSession<R> createSession(RBACManager<R> rbacManager, List<RBACPermission> permissions) {
        return new RBACSession<>(rbacManager, new ArrayList<>(permissions));
    }

    RBACSession(RBACManager<R> rbacManager, List<RBACPermission> permissions) {
        manager = rbacManager;

        if (!permissions.isEmpty()) {
            // 使用提供的权限列表初始化会话管理的权限
            List<String> oldPermissions = manager.listPermission();
            List<RBACPermission> newPermissions = new ArrayList<>();

            for (RBACPermission permission : permissions) {
                if (!oldPermissions.remove(permission.id())) { // 移出重复的权限
                    newPermissions.add(permission);// 保留新增的权限
                }
            }

            // TODO 不要在循环里做添加删除操作
            // 添加新增的权限到数据库
            for (RBACPermission newPermission : newPermissions) {
                manager.insertPermission(newPermission);
            }

            // 删除无效的权限
            for (String oldPermission : oldPermissions) {
                manager.removePermission(oldPermission);
            }
        } else {
            LOGGER.info("初始化权限列表为空, 跳过更新权限!");
        }

        // 缓存系统权限
        permissionSet.addAll(manager.listPermission());

        // 缓存系统角色
        for (R role : manager.listRole()) {
            roleMap.put(role.id(), role);
        }

        // 初始化角色权限控制器
        holder = new RolePermissionHolder();
    }

    public static String permissionFrom(String model, String service) {
        return model + RBACConstant.P_CONN + service;
    }

    boolean hasPermission(String permission) {
        return permissionSet.contains(permission);
    }

    public void grantPermissionToRole(String role, String permission) {
        if (hasPermission(permission)) {
            if (!holder.exist(role, permission)) {
                holder.grant(role, permission);
                LOGGER.info("Grant: role-permission pair: {}-{}", role, permission);
            } /*else {
                LOGGER.info("Grant error: role-permission pair exists: {}-{}", role, permission);
            }*/
        } else {
            LOGGER.warn("Grant error: we don't have permission ({}) in our system", permission);
        }
    }

    public void revokePermissionFromRole(String role, String permission) {
        if (!holder.exist(role, permission)) {
            LOGGER.warn("revoke-error: role-permission pair does not exist: {}-{}", role, permission);
            return;
        }
        holder.revoke(role, permission);
    }

    public boolean isPermitted(String role, String model, String service) {
        return isPermitted(role, RBACConstant.P_ALL)                             // 所有权限
                || isPermitted(role, permissionFrom(model, RBACConstant.P_ALL))        // 模块级别的权限
                || isPermitted(role, permissionFrom(model, service));     // 业务级别的权限
    }

    public boolean isPermitted(String role, String permission) {
        return holder.exist(role, permission);
    }

    public void insertRole(R role) {
        if (!manager.hasRole(role)) {
            manager.insertRole(role);
        }
        roleMap.put(role.id(), role);
    }

    public void updateRole(R role) {
        if (manager.hasRole(role)) {
            manager.updateRole(role);
        }
        roleMap.put(role.id(), role);
    }

    /**
     * 删除系统中的角色，并且删除对应的授权。
     *
     * @param role role
     */
    public void deleteRole(R role) {
        if (manager.hasRole(role)) {
            // 删除了角色后先删除角色所有的权限
            holder.revoke(role.id());

            manager.removeRole(role.id());// 从数据库中删除角色
            roleMap.remove(role.id());// 从缓存中删除角色
        }
    }

    public R getRole(String role) {
        return roleMap.get(role);
    }

    public List<R> listRole() {
        return new ArrayList<>(roleMap.values());
    }

    public List<String> listPermissions() {
        return new ArrayList<>(permissionSet);
    }

    class RolePermissionHolder {
        private Set<String> lookup = manager.listRolePermission();

        boolean exist(String role, String permission) {
            return lookup.contains(keyRolePermission(role, permission));
        }

        void grant(String role, String permission) {
            manager.grantPermissionToRole(role, permission);
            lookup.add(keyRolePermission(role, permission));
        }

        void revoke(String role, String permission) {
            manager.revokePermissionFromRole(role, permission);
            lookup.remove(keyRolePermission(role, permission));
        }

        void revoke(String role) {
            manager.revokePermissionByRole(role);
            for (String key : lookup) {
                if (key.startsWith(role)) {
                    lookup.remove(key);
                }
            }
        }

        private String keyRolePermission(String role, String permission) {
            return role + "-" + permission;
        }
    }
}
