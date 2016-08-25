package win.doyto.fzone.admin.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.fzone.mapper.PermMapper;
import win.doyto.fzone.model.Perm;
import win.doyto.web.ResponseObject;

/**
 * 权限管理模块基本操作。
 *
 * @author Yuanzhen on 2015-08-02.
 */
@RestController
@RequestMapping("/api/perm")
public class PermController {
    @Resource
    private PermMapper permMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseObject query(Perm perm){
        ResponseObject ret = new ResponseObject();
        List<Perm> permList = permMapper.query(perm);
        long count = permMapper.count(perm);
        ret.setSuccess(true);
        ret.setResult(permList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseObject save(@RequestBody @Valid Perm perm, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        if (StringUtils.isBlank(perm.getName())) {
            ret.setMessage("权限名称不能为空");
            return ret;
        } else if (permMapper.hasPerm(perm.getName()) ) {
            ret.setMessage("权限名称已存在");
            return ret;
        }
        perm.setCreateUserId(AppContext.getLoginUserId());
        perm.setValid(true);
        permMapper.insert(perm);
        ret.setSuccess(true);
        ret.setResult(perm);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseObject saveUpdate(@RequestBody @Valid Perm perm, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Perm origin = permMapper.get(perm.getId());
        if (origin == null) {
            ret.setMessage("原始记录不存在");
            return ret;
        }
        if (StringUtils.isNotBlank(perm.getName())) {
            origin.setName(perm.getName());
        }
        if (StringUtils.isNotBlank(perm.getMemo())) {
            origin.setMemo(perm.getMemo());
        }
        permMapper.update(origin);
        ret.setSuccess(true);
        ret.setResult(origin);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Perm perm = permMapper.get(id);
        if (perm == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        ret.setSuccess(true);
        ret.setResult(perm);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseObject delete(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Perm perm = permMapper.get(id);
        if (perm == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        permMapper.delete(id);
        ret.setSuccess(true);
        ret.setResult(perm);
        return ret;
    }
}
