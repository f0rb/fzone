package win.doyto.fzone.admin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.fzone.mapper.RoleMapper;
import win.doyto.fzone.mapper.UserMapper;
import win.doyto.fzone.model.Role;
import win.doyto.fzone.model.User;
import win.doyto.rbac.RBACCheck;
import win.doyto.util.EncryptUtils;
import win.doyto.web.ResponseObject;

/**
 * 用户管理模块基本操作。
 *
 * @author Yuan Zhen on 2015-07-30.
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;

    @RequestMapping(method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject query(User user){
        ResponseObject ret = new ResponseObject();
        User oper = AppContext.getLoginUser();//当前操作人员
        user.setRankGt(oper.getRank());
        List<User> userList = userMapper.query(user);
        long count = userMapper.count(user);
        ret.setSuccess(true);
        ret.setResult(userList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    @RBACCheck
    public ResponseObject add(@RequestBody User user, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        if (userMapper.hasValueOnColumn("username", user.getUsername()) ) {
            ret.setMessage("用户名称已存在");
            return ret;
        }
        if (userMapper.hasValueOnColumn("mobile", user.getMobile()) ) {
            ret.setMessage("手机号码已存在");
            return ret;
        }
        if (userMapper.hasValueOnColumn("nickname", user.getNickname())) {
            ret.setMessage("昵称已被占用");
            return ret;
        }
        if (userMapper.hasValueOnColumn("email", user.getEmail())) {
            ret.setMessage("邮箱已被绑定");
            return ret;
        }
        //user.setCreateUserId(AppContext.getLoginUserId());
        user.setPassword(EncryptUtils.encryptPassword(user.getUsername(), user.getPassword()));
        user.setValid(true);

        User oper = AppContext.getLoginUser();//当前操作人员

        List<Role> rolesToAdd = new ArrayList<>();
        if (!checkRoles(oper, user.getRoles(), rolesToAdd, ret)) {
            return ret;
        }

        user.setRank(rolesToAdd.get(0).getRank());
        int num = userMapper.insert(user);
        if (num <= 0) {
            ret.setMessage("添加用户失败");
            return ret;
        }
        for (Role role : rolesToAdd) {
            roleMapper.assignRole(user.getId(), role.getId(), oper.getId());
        }

        ret.setSuccess(true);
        ret.setResult(user);
        return ret;
    }

    /**
     * 更新用户的昵称, 手机, 邮箱
     *
     * @param user user
     * @return 成功或者错误信息
     */
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    @RBACCheck
    public ResponseObject save(@RequestBody User user) {
        ResponseObject ret = new ResponseObject();
        User target = userMapper.getWithRoles(user.getId());
        if (target == null) {
            ret.setMessage("原始记录不存在");
            return ret;
        }
        User oper = AppContext.getLoginUser();//当前操作人员
        Short operRank = oper.getRank();

        if (operRank > target.getRank()) {
            ret.setMessage("不能更改同等或更高级别的用户");
            return ret;
        }

        if (!StringUtils.equals(user.getNickname(), target.getNickname()) && userMapper.hasValueOnColumn("nickname", user.getNickname())) {
            ret.setMessage("昵称已被占用");
            return ret;
        }
        if (!StringUtils.equals(user.getMobile(), target.getMobile()) && userMapper.hasValueOnColumn("mobile", user.getMobile())) {
            ret.setMessage("手机号码已被绑定");
            return ret;
        }
        if (!StringUtils.equals(user.getEmail(), target.getEmail()) && userMapper.hasValueOnColumn("email", user.getEmail())) {
            ret.setMessage("邮箱已被绑定");
            return ret;
        }
        target.setNickname(user.getNickname());
        target.setMobile(user.getMobile());
        target.setEmail(user.getEmail());

        target.setUpdateTime(new Date());
        target.setUpdateUserId(AppContext.getLoginUserId());
        userMapper.updateByIdSelective(target);

        return ret;
    }

    /**
     * 更新用户角色和用户的rank
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "{id}/role", method = RequestMethod.POST)
    @Transactional
    @RBACCheck
    public ResponseObject updateRoles(@RequestBody User user) {
        ResponseObject ret = new ResponseObject();
        User target = userMapper.getWithRoles(user.getId());
        if (target == null) {
            ret.setMessage("原始记录不存在");
            return ret;
        }

        User oper = AppContext.getLoginUser();//当前操作人员
        if (oper.getRank() > target.getRank()) {
            ret.setMessage("不能更改同等或更高级别用户的角色");
            return ret;
        }

        List<Role> rolesToAdd = new ArrayList<>();
        if (!checkRoles(oper, user.getRoles(), rolesToAdd, ret)) {
            return ret;
        }

        roleMapper.removeRolesByUserId(user.getId());
        for (Role role : rolesToAdd) {
            roleMapper.assignRole(user.getId(), role.getId(), oper.getId());
        }
        target.setRank(rolesToAdd.get(0).getRank());
        target.setUpdateTime(new Date());
        target.setUpdateUserId(oper.getId());
        userMapper.updateByIdSelective(target);

        return ret;
    }

    /**
     *
     * @param oper 当前操作用户
     * @param rolesToCheck
     * @param rolesToAdd
     * @param ret
     * @return
     */
    private boolean checkRoles(User oper, List<Role> rolesToCheck, List<Role> rolesToAdd, ResponseObject ret) {
        if (rolesToCheck == null || rolesToCheck.isEmpty()) {
            ret.setMessage("必须为用户授予至少一个角色");
            return false;
        }
        Short operRank = oper.getRank();
        for (Role role : rolesToCheck) {
            // TODO 不在循环里做角色查询(不过如果mybatis配了二级缓存的话好像不影响啊)
            role = roleMapper.get(role.getId());
            // 管理员只能授予更低级别的权限
            if (operRank < role.getRank()) {
                rolesToAdd.add(role);
                if (!Objects.equals(role.getRank(), rolesToAdd.get(0).getRank())) {
                    log.warn("管理员{}试图授予用户不同级别的角色: {}和{}!",
                             oper.getUsername(), rolesToAdd.get(0).getName(), role.getName());

                    ret.setMessage("不能为用户授予不同级别的角色:" + rolesToAdd.get(0).getName() + "," + role.getName());
                    return false;
                }
            } else {
                log.warn("级别为{}的管理员{}试图授予级别为{}的角色{}!",
                         operRank, oper.getUsername(), role.getRank(), role.getName());

                ret.setMessage("当前管理员无法授予角色:" + role.getName());
                return false;
            }
        }
        return true;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        User user = userMapper.getWithRoles(id);
        if (user == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        ret.setSuccess(true);
        ret.setResult(user);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    @RBACCheck
    public ResponseObject delete(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        User origin = userMapper.get(id);
        if (origin == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        origin.setValid(false);

        origin.setUpdateTime(new Date());
        origin.setUpdateUserId(AppContext.getLoginUserId());
        userMapper.updateByIdSelective(origin);

        // 删除用户角色
        roleMapper.removeRolesByUserId(origin.getId());
        ret.setSuccess(true);
        return ret;
    }

}
