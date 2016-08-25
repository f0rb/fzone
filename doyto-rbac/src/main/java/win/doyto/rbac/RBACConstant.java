package win.doyto.rbac;

/**
 * Class com.uniweb.rbac.RBACConstant description goes here.
 *
 * @author f0rb, created at 2012-11-06 09:07
 */
public interface RBACConstant {

    int GUEST_ID = 99;
    int ROOT_ID = 1;
    int ADMIN_ID = 2;
    int NORMAL_ID = 3;
    int TEST_ID = 4;

    // 预置角色的代码
    String ROOT = "root";
    String ADMIN = "admin";
    String VIP = "vip";
    String NORMAL = "normal";
    String GUEST = "guest";
    String DEFAULT = "default";

    String DOT = ".";
    String HYPHEN = "-";
    String RP_CONN = HYPHEN;
    String COLON = ":";
    String P_CONN = COLON;
    String STAR = "*";
    String P_ALL = STAR;

}
