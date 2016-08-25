package win.doyto.fzone.admin.controller;

import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import win.doyto.fzone.admin.common.AppContext;
import win.doyto.web.WebContext;
import win.doyto.fzone.model.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-23.
 */
@Controller
public class AngularController {
    @Resource
    private MenuController menuController;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        if (AppContext.getLoginUser() == null) {
            model.addAttribute("redirect", WebContext.getFullURL());
            return "redirect:/login";
        }
        List<Menu> menuList = menuController.getMenuList(true);
        String menuListStr = JSON.toJSONString(menuList, new SimplePropertyPreFilter(Menu.class, "url", "name", "label", "html"));
        model.addAttribute("menuList", menuListStr);
        return "/index.jsp";
    }
}
