package win.doyto.rbac;

import java.util.List;

/**
 * Description goes here.
 *
 * @author f0rb
 * @version 1.0.0, 2012-11-02
 */
public interface RBACUser<R extends RBACRole> extends RBACRank{

     List<R> getRoles();
}
