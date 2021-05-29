package sample;

import java.time.LocalDate;

public class Attendance {
    Student student;
    LocalDate date;
    Course course;
    boolean attended;

    Attendance(Student student, LocalDate date, Course course, boolean attended)
    {
        this.student = student;
        this.date = date;
        this.course = course;
        this.attended = attended;
    }

    public Student getStudent() {
        return student;
    }

    public LocalDate getDate() {
        return date;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isAttended() {
        return attended;
    }
}
