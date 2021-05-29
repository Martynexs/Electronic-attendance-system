package sample;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Course extends BasicObject {
    //String name;
    Set<Group> Groups;
    Set<Student> Students;

    Course(String name)
    {
        super(name);
        Groups = new HashSet<>();
        Students = new HashSet<>();
    }

    void addGroup(Group group)
    {
        Groups.add(group);
        Students.addAll(group.Students);
    }

    void removeGroup(Group group)
    {
        Groups.remove(group);

        //Remove students that don't belong to other groups in the course
        group.Students.forEach(student -> {
            AtomicBoolean delete = new AtomicBoolean(true);
            Groups.forEach(gr ->{
                if(gr.Students.contains(student)) delete.set(false);
                    });
            if(delete.get()) removeStudent(student);
        });
    }

    void removeStudent(Student student)
    {
        Students.remove(student);
    }

    public String toString()
    {
        return name;
    }


}

