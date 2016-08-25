package win.doyto.fzone.admin.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import win.doyto.web.WebContext;

/**
 * Spring启动时自动注入ApplicationContext对象.
 * 保存spring的applicationContext, 便于访问在spring里配置的bean.
 * 也可在spring配置文件配置<bean class="SpringContext"/>
 * <p>
 * Created by Yuan on 2015/4/30.
 */
@Slf4j
@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        try {
            return (T) context.getBean(name);
        } catch (BeansException e) {
            log.warn("未找到bean: " + name, e);
            return null;
        }
    }

    public static <T> T getBean(Class<T> clz) {
        return context.getBean(clz);
    }

    public static String getMessage(String code) {
        return context.getMessage(code, null, WebContext.getLocale());
    }
}
