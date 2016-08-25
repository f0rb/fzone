package win.doyto.fzone.admin.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.fzone.model.User;
import win.doyto.rbac.RBACUtils;
import win.doyto.web.ResponseObject;
import win.doyto.web.ResponseStatus;

/**
 * 拦截访问请求进行权限验证
 *
 * @author Yuanzhen on 2016-05-09.
 */
@Slf4j
public class RBACInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (AppContext.getLoginUser() == null) {
            ResponseObject ret = new ResponseObject(ResponseStatus.LOGIN_EXPIRED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().append(JSON.toJSONString(ret)).flush();
            return false;
        }
        User oper = AppContext.getLoginUser();
        if (!"root".equals(oper.getUsername())) {
            String permission = Integer.toHexString(handler.toString().hashCode());
            if (!RBACUtils.isPermitted(oper, permission)) {
                ResponseObject ret = new ResponseObject(ResponseStatus.ACCESS_DENIED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().append(JSON.toJSONString(ret)).flush();
                return false;
            }
        }
        log.info(handler.toString());

        return true;
    }
}
