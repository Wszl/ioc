package bean;

import org.xdove.ioc.annotation.Autowired;

public class Teacher {


    @Autowired
    private Teach teach;
    @Autowired
    private Sleep sleep;

    public void skill() {
        System.out.println("teacher's skill: \n");
        teach.teach();
        sleep.sleep();
    }
}
