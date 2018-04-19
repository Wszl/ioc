import bean.Teacher;
import org.junit.Test;
import org.xdove.ioc.ApplicationContext;
import org.xdove.ioc.exception.NoSuchBeanException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TestBeanFactory {

    @Test
    public void injectTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchBeanException {
        //init ioc
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.init();
        //get bean
        Teacher teacher = (Teacher) applicationContext.getBean(Teacher.class);
        //do something
        teacher.skill();

    }

}
