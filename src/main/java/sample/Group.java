package sample;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Group extends BasicObject {
    //String name;
    ArrayList<Student> Students;
    Group(String name)
    {
        super(name);
        Students = new ArrayList<>();
    }

    void addStudent(Student student)
    {
        Students.add(student);
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
