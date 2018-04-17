import bean.Sleep;
import bean.Teach;
import bean.Teacher;
import org.junit.Test;
import org.xdove.ioc.BeanFactory;

import java.lang.reflect.InvocationTargetException;

public class TestBeanFactory {

    @Test
    public void injectTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanFactory factory = BeanFactory.getInstance();
        factory.initBean(Teacher.class);
        factory.initBean(Teach.class);
        factory.initBean(Sleep.class);

        Teacher teacher = (Teacher) factory.getBean(Teacher.class);
        factory.inject(teacher);

        teacher.skill();

    }

}
