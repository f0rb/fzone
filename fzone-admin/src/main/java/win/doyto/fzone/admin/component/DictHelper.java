package win.doyto.fzone.admin.component;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import win.doyto.fzone.mapper.DictMapper;
import win.doyto.fzone.model.Dict;
import org.springframework.stereotype.Component;

/**
 * 类描述
 *
 * @author Yuanzhen on 2016-06-30.
 */
@Component
public class DictHelper {
    @Resource
    private DictMapper dictMapper;

    public String get(String key) {
        Dict dict = dictMapper.getByKey(key);

        return dict == null ? "" : StringUtils.trimToEmpty(dict.getValue());
    }
}
