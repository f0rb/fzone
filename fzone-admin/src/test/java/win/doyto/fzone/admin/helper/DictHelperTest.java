package win.doyto.fzone.admin.helper;

import javax.annotation.Resource;

import win.doyto.fzone.admin.common.SpringTest;
import org.junit.Test;
import win.doyto.fzone.admin.component.DictHelper;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-30.
 */
public class DictHelperTest extends SpringTest{

    @Resource
    DictHelper dictHelper;

    @Test
    public void testGet() throws Exception {
        System.out.println(dictHelper.get("weixin"));
        System.out.println(dictHelper.get("weixin.appid"));
    }
}