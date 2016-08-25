package win.doyto.fzone.admin.controller;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import win.doyto.fzone.admin.common.AppConstant;
import win.doyto.fzone.admin.common.AppContext;
//import win.doyto.fzone.admin.common.AppMessage;
import win.doyto.fzone.mapper.UserMapper;
import win.doyto.fzone.model.Menu;
import win.doyto.fzone.model.User;
import win.doyto.util.CaptchaUtils;
import win.doyto.util.EncryptUtils;
import win.doyto.web.ResponseObject;
import win.doyto.web.WebContext;

/**
 * 后台登录和退出
 *
 * @author Yuanzhen on 2015-08-01.
 */
@Slf4j
@Controller
public class AdminController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageSource messageSource;
    
    @Resource
    private MenuController menuController;

    @RequestMapping("/")
    public String index(Model model) {
        if (AppContext.getLoginUser() == null) {
            model.addAttribute("redirect", WebContext.getFullURL());
            return "redirect:/login";
        }
        List<Menu> menuList = menuController.getMenuList(true);
        String menuListStr = JSON.toJSONString(menuList, new SimplePropertyPreFilter(Menu.class, "url", "name", "label", "html"));
        model.addAttribute("menuList", menuListStr);
        return "forward:/index.jsp";
    }

    @RequestMapping(value = {"login"}, method = RequestMethod.GET)
    public String login(String redirect) {
        if (StringUtils.isNotEmpty(redirect)) {
            WebContext.setRedirect(redirect);
        }
        return "login";
    }

    /**
     * Method login ...
     *
     * @param user of type UserDTO
     * @return "login", "redirect:/"
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user") User user, String redirect) {
        final String username = user.getUsername();
        final String password = user.getPassword();
        if (!WebContext.validateCaptcha()) {
            user.addMessage("login", messageSource.getMessage("captcha.invalid", null, WebContext.getLocale()));
            return "login";
        }
        User originUser = userMapper.getByUsernameWithRoles(username);
        if (originUser == null) {
            user.addMessage("login", messageSource.getMessage("user.username.nonexistent", null, WebContext.getLocale()));
            return "login";
        }
        String encryptPassword = EncryptUtils.encryptPassword(username, password);
        if (!originUser.getPassword().equalsIgnoreCase(encryptPassword)) {
            user.addMessage("login", messageSource.getMessage("user.password.incorrect", null, WebContext.getLocale()));
            return "login";
        }
        doLogin(originUser);
        log.info("访问后台主页");
        if (StringUtils.isEmpty(redirect)) {
            redirect = WebContext.removeRedirect();
        }
        return StringUtils.isEmpty(redirect) ? "redirect:/" : "redirect:" + redirect;
    }

    @RequestMapping(value = "login-user", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject loginUser() {
        ResponseObject ret = new ResponseObject();
        User user = AppContext.getLoginUser();
        if (user == null) {
            ret.setMessage("请先登录");
            return ret;
        }
        ret.setSuccess(true);
        ret.setResult(user);
        return ret;
    }

    // 经过登录验证, 或者激活, 或者重置密码后, 执行login操作
    private void doLogin(User o) {
        o.setLastIp(AppContext.getIp());
        o.setLastLogin(new Timestamp(System.currentTimeMillis()));
        userMapper.login(o);
        AppContext.setLoginUser(o);//将user保存到session
    }

    /**
     * Method logout ...
     *
     * @return true if service succeed, otherwise false
     */
    @RequestMapping("logout")
    public String logout() {
        log.info("用户注销登录");
        User sessionUser = AppContext.getLoginUser();
        if (sessionUser != null) {
            userMapper.logout(sessionUser.getId());
            AppContext.removeLoginUser();
            AppContext.clearLoginCookies();
        }
        return "redirect:/";
    }

    @RequestMapping(value = AppConstant.CAPTCHA_IMAGE)
    @ResponseBody
    public BufferedImage captcha() {
        return CaptchaUtils.getImage(WebContext.generateCaptcha());
    }
}
