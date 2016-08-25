package win.doyto.fzone.admin.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import win.doyto.fzone.admin.controller.AdminController;
import win.doyto.fzone.mapper.RoleMapper;
import win.doyto.fzone.model.Perm;
import win.doyto.fzone.model.Role;
import win.doyto.rbac.RBACCheck;
import win.doyto.rbac.RBACManager;
import win.doyto.rbac.RBACUtils;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-05-09.
 */
@Slf4j
public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PlatformTransactionManager transactionManager;

    @Transactional
    protected void handlerMethodsInitialized(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        Map<String, Perm> permMap = new HashMap<>();
        Map<String, String[]> permRoleMap = new HashMap<>();

        // 根据 Spring MVC 的 RequestMapping 注解收集系统预置的权限及授权的角色
        for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
            if (handlerMethod.getBeanType().equals(AdminController.class)) {
                continue;
            }

            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            Perm perm = new Perm();
            perm.setId(Integer.toHexString(handlerMethod.toString().hashCode()));
            perm.setName(methodsCondition + ":" + patternsCondition);
            perm.setMemo("自动生成权限 - " + methodsCondition + ":" + patternsCondition);
            perm.setValid(true);
            perm.setCreateUserId(Role.ROOT_ID);

            RBACCheck rbacCheck = handlerMethod.getMethodAnnotation(RBACCheck.class);
            if (rbacCheck != null) {
                if (!rbacCheck.isLimited()) {
                    continue;//不检查当前权限
                }
                if (rbacCheck.roles().length > 0) {
                    permRoleMap.put(perm.getId(), rbacCheck.roles());
                }
                if (rbacCheck.value().length() > 0) {//权限名称
                    perm.setId(Integer.toHexString(rbacCheck.value().hashCode()));
                    perm.setName(rbacCheck.value());
                }
                if (rbacCheck.memo().length() > 0) {//备注
                    perm.setMemo(rbacCheck.memo());
                }
            }
            permMap.put(perm.getName(), perm);

            log.info("{} {}", requestMappingInfo.getMethodsCondition(), requestMappingInfo.getPatternsCondition());
        }

        List<String[]> rolePermList = new ArrayList<>();
        for (String perm : permRoleMap.keySet()) {
            for (String rolecode : permRoleMap.get(perm)) {
                Role role = roleMapper.getByCode(rolecode);
                rolePermList.add(new String[] {role.id(), perm} );
            }
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            RBACUtils.init((RBACManager)roleMapper, roleMapper.listRole(), new ArrayList<>(permMap.values()), rolePermList);
        }
        catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
        transactionManager.commit(status);
    }
}
