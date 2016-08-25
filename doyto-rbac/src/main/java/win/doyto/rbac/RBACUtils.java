package win.doyto.rbac;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description goes here.
 *
 * @author f0rb
 * @version 1.0.0, 2012-11-02
 */
public final class RBACUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RBACUtils.class);

    private static RBACSession<RBACRole> session;
    private static final List<RBACRole> INTERNAL_ROLES = new ArrayList<>();

    private RBACUtils() {
    }

    /**
     *
     * @param manager
     * @param initRoles 系统预置的角色
     * @param initPermissions
     * @param rolePermissionCache
     */
    public synchronized static
    void init(RBACManager<RBACRole> manager, List<? extends RBACRole> initRoles, List<RBACPermission> initPermissions, List<String[]> rolePermissionCache) {

        if (session == null) {
            try {
                session = RBACSession.createSession(manager, initPermissions);
                INTERNAL_ROLES.addAll(initRoles);
                //for (RBACRole role : INTERNAL_ROLES) {
                //    insertRole(role);
                //}
                //grantPermissionToRole(String.valueOf(RBACConstant.ROOT_ID), RBACConstant.P_ALL);
                for (String[] rp : rolePermissionCache) {
                    grantPermissionToRole(rp[0], rp[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);//
            }
        }
    }

    //public synchronized static void init(RBACManager<RBACRole> manager, List<RBACRole> initRoles, String basePackage, String serviceSuffix) {
    //
    //    if (session == null) {
    //        try {
    //            List<String> initPermissions = new ArrayList<>();
    //            List<String[]> rolePermissionCache = new ArrayList<>();
    //            initPermissionAndGrant(basePackage, serviceSuffix, initPermissions, rolePermissionCache);
    //            session = RBACSession.createSession(manager, initPermissions);
    //            INTERNAL_ROLES.addAll(initRoles);
    //            for (RBACRole role : INTERNAL_ROLES) {
    //                insertRole(role);
    //            }
    //            grantPermissionToRole(String.valueOf(RBACConstant.ROOT_ID), RBACConstant.P_ALL);
    //            grantPermissionToRole(String.valueOf(RBACConstant.ADMIN_ID), RBACConstant.P_ALL);
    //            for (String[] rp : rolePermissionCache) {
    //                grantPermissionToRole(rp[0], rp[1]);
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //}

    //private static void initPermissionAndGrant(
    //        String basePackage,
    //        String serviceSuffix,
    //        List<String> initPermissions,
    //        List<String[]> rolePermissionCache
    //) {
    //    initPermissions.add(RBACConstant.P_ALL);
    //    List<Class> classes = getServices(basePackage);
    //    for (Class clazz : classes) {
    //        String model = clazz.getSimpleName().replace(serviceSuffix, "");
    //        initPermissions.add(RBACSession.permissionFrom(model, RBACConstant.P_ALL));
    //        Method[] methods = clazz.getMethods();
    //        for (Method method : methods) {
    //            RBACCheck limitedService = method.getAnnotation(RBACCheck.class);
    //            if (limitedService != null && limitedService.isLimited()) {
    //                String service = limitedService.value().equals("") ? method.getName() : limitedService.value();
    //                String permission = RBACSession.permissionFrom(model, service);
    //                initPermissions.add(permission);
    //                for (String rolecode : limitedService.roles()) {
    //                    rolePermissionCache.add(new String[] {rolecode,  permission});
    //                }
    //            }
    //        }
    //    }
    //}

    //private static List<Class> getServices(String... packages) {
    //    List<Class> serviceImplList = new ArrayList<>();
    //    /*File classes = new File(RBACUtils.class.getResource("/").getPath().replace("test-", ""));
    //
    //    for (String packageName : packages) {
    //        if (packageName.endsWith(".")) packageName = packageName.replaceAll("\\.+$", "");
    //        File base = new File(classes, packageName.replaceAll("\\.", "/"));
    //        if (!base.isDirectory()) {
    //            LOGGER.info("{} is not a directory", base.getPath());
    //            continue;
    //        }
    //
    //        Collection<File> files = FileUtils.listFiles(base, new String[]{"class"}, true);
    //        for (File file : files) {
    //            if (file.isFile() && file.getName().endsWith("ServiceImpl.class")) {
    //                try {
    //                    String className = file.getPath().replace(classes.getPath() + File.separator, "")
    //                            .replace(".class", "").replace(File.separator, ".");
    //                    serviceImplList.add(Class.forName(className));
    //                } catch (ClassNotFoundException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }
    //    }*/
    //    return serviceImplList;
    //}

    public static void revokePermissionFromRole(String role, String permission) {
        session.revokePermissionFromRole(role, permission);
    }

    public static void grantPermissionToRole(String role, String permission) {
        session.grantPermissionToRole(role, permission);
    }

    //public static boolean isPermitted(List<? extends RBACRole> roles, String model, String service) {
    //    if (roles == null) return false;
    //    for (RBACRole role : roles) if (session.isPermitted(role.role(), model, service)) return true;
    //    return false;
    //}
    //
    //public static boolean isPermitted(RBACRole role, String model, String service) {
    //    return role != null && session.isPermitted(role.role(), model, service);
    //}

    //public static boolean isPermitted(RBACRole role, String permission) {
    //    return role != null && session.isPermitted(role.role(), permission);
    //}

    public static boolean isRolePermitted(String role, String permission) {
        return role != null && session.isPermitted(role, permission);
    }

    public static boolean isPermitted(RBACUser<? extends RBACRole> user, String permission) {
        if (user == null) {
            return false;
        }
        List<? extends RBACRole> roles = user.getRoles();
        if (roles == null) {
            return false;
        }
        for (RBACRole r : roles) {
            String role = r.id();
            if (session.isPermitted(role, permission)) {
                return true;
            }
        }
        return false;
    }

    /*public static boolean isPermitted(String model, String service) {
        RBACUser<RBACRole> user = AppContext.getLoginUser();
        if (user == null) return false;
        List<RBACRole> roles = user.getRoles();
        if (roles == null) return false;
        for (RBACRole role : roles) if (isPermitted(role, model, service)) return true;
        return false;
    }*/

    public static RBACRole getRole(String role) {
        return session.getRole(role);
    }

    public static boolean containRole(String role) {
        return getRole(role) != null;
    }

    public static List<RBACRole> listRole() {
        return session.listRole();
    }

    public static List<String> listPermissions() {
        return session.listPermissions();
    }

    public static void insertRole(RBACRole role) {
        session.insertRole(role);
    }

    public static void deleteRole(RBACRole role) {
        session.deleteRole(role);
    }

    public static void updateRole(RBACRole role) {
        session.updateRole(role);
    }

    public static boolean isInternalRole(RBACRole role) {
        for (RBACRole o : INTERNAL_ROLES) {
             if (o.id().equals(role.id())) {
                 return true;
             }
        }
        return false;
    }
}
