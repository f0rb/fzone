package win.doyto.fzone.admin.controller;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import win.doyto.fzone.admin.common.AppConstant;
import win.doyto.fzone.admin.common.SpringTest;
import win.doyto.fzone.mapper.UserMapper;
import win.doyto.fzone.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 类描述
 *
 * @author Yuanzhen on 2015-12-30.
 */
public class MenuControllerTest extends SpringTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void post_menu_query_Test() throws Exception {
        mockMvc.perform(get("/api/menu").accept(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.result").isArray())
               .andReturn();
    }

    @Test
    public void post_menu_tree_Test() throws Exception {
        User oper = userMapper.getByUsernameWithRoles("admin");
        RequestBuilder requestBuilder = post("/api/menu/tree")
                .sessionAttr(AppConstant.Session.LOGIN_USER, oper.toSessionUser())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
               .andDo(print())//输出MvcResult到控制台
               .andExpect(content().contentType("application/json;charset=UTF-8"))
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.result.name").value("根目录"))
               .andExpect(jsonPath("$.result.submenu").isArray())
               .andExpect(status().isOk())//验证状态码
               .andReturn();
    }
}