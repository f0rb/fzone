package win.doyto.fzone.admin.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import win.doyto.fzone.admin.common.AppContext;
import win.doyto.web.ResponseObject;
import win.doyto.fzone.mapper.RoleMapper;
import win.doyto.fzone.model.Role;
import win.doyto.fzone.model.User;
import win.doyto.rbac.RBACCheck;
import win.doyto.rbac.RBACUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理模块基本操作。
 *
 * @author Yuanzhen on .
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Resource
    private RoleMapper roleMapper;

    @RequestMapping(method = RequestMethod.GET)
    @RBACCheck(memo = "角色查询")
    public ResponseObject query(Role role){
        ResponseObject ret = new ResponseObject();
        List roleList = roleMapper.query(role);
        long count = roleMapper.count(role);
        ret.setSuccess(true);
        ret.setResult(roleList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @RBACCheck(memo = "角色列表")
    public ResponseObject list(){
        ResponseObject ret = new ResponseObject();
        Short operRank = AppContext.getLoginUser().getRank();
        List roleList = roleMapper.listRoleByRank(operRank);
        ret.setSuccess(true);
        ret.setResult(roleList);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    @RBACCheck(memo = "角色添加")
    public ResponseObject add(@RequestBody @Valid Role role, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }

        if (roleMapper.hasRoleName(role.getName()) ) {
            ret.setMessage("角色名称已存在");
            return ret;
        }
        if (roleMapper.hasRoleCode(role.getCode()) ) {
            ret.setMessage("角色代码已存在");
            return ret;
        }
        if (role.getRank() <= AppContext.getLoginUser().getRank()) {
            ret.setMessage("不能添加同等或更高级别的角色");
            return ret;
        }
        role.setId(null);
        //role.setCode(null);
        role.setCreateUserId(AppContext.getLoginUserId());
        role.setValid(true);
        RBACUtils.insertRole(role);
        ret.setSuccess(true);
        ret.setResult(role);
        return ret;
    }

    /**
     * 更新用户的名称, 级别, 备注
     *
     * @param role 包含name, rank, memo
     * @param result JSR303校验结果
     * @return ResponseObject
     */
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @RBACCheck(memo = "角色更新")
    public ResponseObject update(@RequestBody @Valid Role role, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }

        Role target = (Role) RBACUtils.getRole(role.id());
        if (target == null) {
            ret.setMessage("原始记录不存在");
            return ret;
        }
        Short operRank = AppContext.getLoginUser().getRank();
        if (target.getRank() <= operRank) {
            ret.setMessage("不能操作同等或更高级别的角色");
            return ret;
        }

        target.setName(role.getName());
        target.setMemo(role.getMemo());
        target.setUpdateTime(new Date());
        target.setUpdateUserId(AppContext.getLoginUserId());
        RBACUtils.updateRole(target);
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @RBACCheck(memo = "角色获取")
    public ResponseObject get(@PathVariable("id") String id) {
        ResponseObject ret = new ResponseObject();
        Role role = (Role) RBACUtils.getRole(id);
        if (role == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        role.setMenus(roleMapper.listMenuIdsByRoleId(id));
        ret.setSuccess(true);
        ret.setResult(role);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @RBACCheck(memo = "角色删除")
    public ResponseObject delete(@PathVariable("id") String id) {
        ResponseObject ret = new ResponseObject();
        Role role = (Role) RBACUtils.getRole(id);
        if (role == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        if (RBACUtils.isInternalRole(role)) {
            ret.setMessage("不允许删除预置角色");
            return ret;
        }
        RBACUtils.deleteRole(role);
        ret.setSuccess(true);
        ret.setResult(role);
        return ret;
    }


    /**
     * 更新用户角色和用户的rank
     *
     * @return
     */
    @RequestMapping(value = "{id}/menu", method = RequestMethod.POST)
    @Transactional
    @RBACCheck(memo = "角色配置菜单")
    public ResponseObject configMenu(@RequestBody List<String> menusToAdd, @PathVariable("id") String roleId) {
        ResponseObject ret = new ResponseObject();
        Role target = (Role) RBACUtils.getRole(roleId);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }

        User oper = AppContext.getLoginUser();//当前操作人员
        if (oper.getRank() > target.getRank()) {
            ret.setMessage("不能操作同等或更高级别的角色");
            return ret;
        }

        roleMapper.removeMenusByRoleId(roleId);
        for (String menuId : menusToAdd) {
            roleMapper.assignMenu(menuId, roleId, oper.getId());
        }
        return ret;
    }
}