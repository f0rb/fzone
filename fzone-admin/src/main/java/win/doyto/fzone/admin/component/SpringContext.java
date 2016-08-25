package win.doyto.fzone.admin.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring启动时自动注入ApplicationContext对象.
 * 保存spring的applicationContext, 便于访问在spring里配置的bean.
 * 也可在spring配置文件配置<bean class="SpringContext"/>
 *
 * Created by Yuan on 2015/4/30.
 */
@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> clz) {
        return context.getBean(clz);
    }

    public static String getMessage(String code) {
        return context.getMessage(code, null, null);
    }
}
