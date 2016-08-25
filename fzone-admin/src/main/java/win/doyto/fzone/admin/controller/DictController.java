package win.doyto.fzone.admin.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.fzone.mapper.DictMapper;
import win.doyto.fzone.model.Dict;
import win.doyto.fzone.model.User;
import win.doyto.rbac.RBACCheck;
import win.doyto.web.ResponseObject;

/**
 * 数据字典管理模块基本操作。
 *
 * @author Yuanzhen on 2016-06-29.
 */
@RestController
@RequestMapping("/api/dict")
public class DictController {
    @Resource
    private DictMapper dictMapper;

    @RequestMapping(method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject query(Dict dict){
        ResponseObject ret = new ResponseObject();
        dict.setRank(AppContext.getLoginUser().getRank());
        List dictList = dictMapper.query(dict);
        long count = dictMapper.count(dict);
        ret.setResult(dictList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    @RBACCheck
    public ResponseObject add(@RequestBody @Valid Dict dict, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        User oper = AppContext.getLoginUser();

        dict.setRank(oper.getRank());
        dict.setCreateUserId(oper.getId());
        dictMapper.insert(dict);
        ret.setResult(dict);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @RBACCheck
    public ResponseObject update(@RequestBody @Valid Dict dict, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Dict target = dictMapper.get(dict.getId());
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        target.setName(dict.getName());
        target.setParentId(dict.getParentId());
        //target.setRank(dict.getRank());//rank不允许修改
        target.setKey(dict.getKey());
        target.setValue(dict.getValue());
        target.setMemo(dict.getMemo());
        target.setLeaf(dict.getLeaf());
        target.setSequence(dict.getSequence());
        target.setAsDefault(dict.getAsDefault());
        target.setValid(dict.getValid());

        target.setUpdateUserId(AppContext.getLoginUserId());
        target.setUpdateTime(new Date());
        dictMapper.update(target);
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @RBACCheck
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Dict target = dictMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @RBACCheck
    public ResponseObject delete(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Dict target = dictMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        dictMapper.delete(id);
        ret.setResult(target);
        return ret;
    }
}