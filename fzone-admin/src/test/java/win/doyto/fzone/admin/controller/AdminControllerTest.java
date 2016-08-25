package win.doyto.fzone.admin.controller;

import win.doyto.fzone.admin.common.SpringTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-13.
 */
public class AdminControllerTest extends SpringTest {

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/login").param("username", "admin").param("password", "admin"))
               .andDo(print())//输出MvcResult到控制台
               .andExpect(status().is3xxRedirection());
    }
}