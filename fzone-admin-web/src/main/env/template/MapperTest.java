package win.doyto.fzone.mapper;

import java.util.List;
import javax.annotation.Resource;

import win.doyto.fzone.model.{{gen.name | capitalize}};
import win.doyto.fzone.test.SpringTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MapperTest类
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
public class {{gen.name | capitalize}}MapperTest extends SpringTest {

    @Resource
    public {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @Test
    public void testCount() throws Exception {
        int count = {{gen.name}}Mapper.count(new {{gen.name | capitalize}}());
        System.out.println(count);
    }
}