package win.doyto.fzone.admin.common;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import win.doyto.fzone.mapper.UserMapper;
import win.doyto.fzone.model.User;
import win.doyto.web.WebContextFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@Rollback
public abstract class SpringTest {

    @Autowired
    private WebApplicationContext wac;
    protected MockMvc mockMvc;

    @Resource
    private UserMapper userMapper;
    protected User root;
    protected User admin;
    protected User test;

    @Before
    public void setup() {
        WebContextFilter webContextFilter = new WebContextFilter();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                                 .addFilter(webContextFilter, "/*")
                                 .build();

        root = userMapper.getByUsernameWithRoles("root");
        admin = userMapper.getByUsernameWithRoles("admin");
        test = userMapper.getByUsernameWithRoles("test");
    }
}
