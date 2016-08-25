package win.doyto.fzone.admin.controller;

import java.util.*;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import win.doyto.fzone.admin.common.AppConstant;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.fzone.mapper.MenuMapper;
import win.doyto.fzone.model.Menu;
import win.doyto.fzone.model.User;
import win.doyto.rbac.RBACCheck;
import win.doyto.rbac.RBACConstant;
import win.doyto.web.ResponseObject;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-08-02.
 */
@RestController
@RequestMapping("api/menu")
public class MenuController {
    @Resource
    private MenuMapper menuMapper;

    @RequestMapping(method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject query(Menu menu) {
        ResponseObject ret = new ResponseObject();
        List<Menu> menuList = menuMapper.query(menu);
        long count = menuMapper.count(menu);
        ret.setSuccess(true);
        ret.setResult(menuList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping("parent")
    @RBACCheck
    public ResponseObject parent() {
        ResponseObject ret = new ResponseObject();
        List<Menu> parentList = menuMapper.parentList();
        ret.setSuccess(true);
        ret.setResult(parentList);
        return ret;
    }

    public List<Menu> getMenuList() {
        return getMenuList(false);
    }

    public List<Menu> getMenuList(boolean onlyLeaf) {
        List<Menu> menuList;

        User oper = AppContext.getLoginUser();
        if (AppConstant.ROOT.equals(oper.getUsername())) {
            Menu query = new Menu();
            if (onlyLeaf) {
                query.setOnlyLeaf(true);
            }
            query.setValid(true);
            menuList = menuMapper.query(query);
        } else {
            //根据用户
            List<Integer> menuIdList = menuMapper.getMenuIdsByUserId(oper.getId());
            menuList = new ArrayList<>();
            for (int i = 0; i < menuIdList.size(); i++) {
                Integer menuId = menuIdList.get(i);
                Menu menu = menuMapper.get(menuId);//走缓存
                if (menu != null && menu.getValid()) {
                    menuList.add(menu);
                    if (!onlyLeaf) {// 查叶节点的父节点
                        Integer parentId = menu.getParentId();
                        if (parentId != null && !menuIdList.contains(parentId)) {
                            menuIdList.add(parentId);
                        }
                    }
                }
            }
        }
        return menuList;
    }
    /**
     * 按json格式返回树型结构的菜单
     *
     */
    @RequestMapping("tree")
    @RBACCheck(roles = {RBACConstant.ADMIN, RBACConstant.VIP, RBACConstant.NORMAL})
    public ResponseObject tree(){
        ResponseObject ret = new ResponseObject();
        List<Menu> menuList = getMenuList();

        Menu rootMenu = null;
        Map<Integer, Menu> treeMap = new HashMap<>();
        for (Menu mobileMenu : menuList) {
            treeMap.put(mobileMenu.getId(), mobileMenu);
            if (Objects.equals(mobileMenu.getName(), "admin")) {
                rootMenu = mobileMenu;
            }
        }
        //如果查询结果中找不到根菜单, 则伪造一个存到treeMap里
        /*if (rootMenu == null) {
            rootMenu = new Menu();
            rootMenu.setId(0);
            rootMenu.setSubmenu(new ArrayList<Menu>());
            treeMap.put(rootMenu.getId(), rootMenu);
        }*/

        // 将子菜单加入到父菜单的sub数组中
        //忽略父菜单的url项
        menuList.stream().filter(menu -> menu.getParentId() != null).forEach(menu -> {
            Menu parent = treeMap.get(menu.getParentId());
            if (parent.getSubmenu() == null) {
                parent.setSubmenu(new ArrayList<>());
                parent.setUrl(null);//忽略父菜单的url项
            }
            parent.getSubmenu().add(menu);
        });
        ret.setSuccess(true);
        ret.setResult(rootMenu);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    @RBACCheck
    public ResponseObject add(@RequestBody Menu menu) {
        ResponseObject ret = new ResponseObject();
        if (menu.getParentId() == null) {
            ret.setMessage("请选择父菜单");
            return ret;
        }
        if (!menuMapper.hasValueOnColumn("id", String.valueOf(menu.getParentId()))) {
            ret.setMessage("父菜单不存在");
            return ret;
        }
        if (StringUtils.isBlank(menu.getName())) {
            ret.setMessage("请输入菜单名称");
            return ret;
        }
        if (menuMapper.hasValueOnColumn("name", menu.getName())) {
            ret.setMessage("菜单名称已存在");
            return ret;
        }
        User oper = AppContext.getLoginUser();
        menu.setRank(oper.getRank());
        menu.setCreateUserId(AppContext.getLoginUserId());
        menuMapper.insert(menu);
        ret.setSuccess(true);
        ret.setResult(menu);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject get(@PathVariable("id") String id) {
        ResponseObject ret = new ResponseObject();
        Menu menu = menuMapper.get(id);
        ret.setSuccess(true);
        ret.setResult(menu);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @RBACCheck
    public ResponseObject update(@RequestBody Menu menu) {
        ResponseObject ret = new ResponseObject();
        Menu origin = menuMapper.get(menu.getId());
        if (origin == null) {
            ret.setMessage("原始记录未找到");
            return ret;
        }
        User oper = AppContext.getLoginUser();
        if (oper.getRank() > menu.getRank()) {
            ret.setMessage("您没有编辑该菜单的权限");
            return ret;
        }
        if (StringUtils.isNotBlank(menu.getName())) {
            if (!StringUtils.equals(menu.getName(), origin.getName()) && menuMapper.hasValueOnColumn("name", menu.getName())) {
                ret.setMessage("菜单名称已存在");
                return ret;
            }
            origin.setName(menu.getName());
        }
        if (StringUtils.isNotBlank(menu.getUrl())) {
            origin.setUrl(menu.getUrl());
        }
        if ((menu.getSequence()) != null) {
            origin.setSequence(menu.getSequence());
        }
        origin.setParentId(menu.getParentId());
        origin.setUpdateUserId(AppContext.getLoginUserId());
        origin.setUpdateTime(new Date());
        menuMapper.update(origin);
        ret.setSuccess(true);
        ret.setResult(origin);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @RBACCheck
    public ResponseObject delete(@PathVariable("id") String id) {
        ResponseObject ret = new ResponseObject();
        menuMapper.delete(id);
        ret.setSuccess(true);
        return ret;
    }
}
