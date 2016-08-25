package win.doyto.fzone.admin.controller;

import win.doyto.fzone.admin.common.SpringTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import win.doyto.fzone.admin.common.AppConstant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ControllerTest类
 *
 * @author Yuanzhen on 2016-06-25.
 */
public class DictControllerTest extends SpringTest {

    @Test
    public void post_dict_query_Test() throws Exception {
        RequestBuilder requestBuilder = get("/api/dict")
                .sessionAttr(AppConstant.Session.LOGIN_USER, admin.toSessionUser())
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.result").isArray())
               .andReturn();
    }
}