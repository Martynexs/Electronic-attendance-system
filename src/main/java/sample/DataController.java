package sample;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class DataController {
    ArrayList<Student> Students;
    ArrayList<Group> Groups;
    ArrayList<Course> Courses;
    ArrayList<Attendance> Attendances;

    DataController()
    {
        Students = new ArrayList<>();
        Groups = new ArrayList<>();
        Courses = new ArrayList<>();
        Attendances = new ArrayList<>();
    }

    void addStudent(String name, String surname, LocalDate birthday)
    {
        Students.add(new Student(name, surname, birthday));
    }

    void removeStudent(Student student)
    {
        Students.remove(student);

        for(Group group : Groups)
        {
            group.Students.remove(student);
        }

        for(Course course : Courses)
        {
            course.Students.remove(student);
        }

        Attendances.removeIf(attendance -> attendance.student.equals(student));
    }

    Group addGroup(String name)
    {
        AtomicReference<Group> newGroup = new AtomicReference<>();
        newGroup.set(null);
        Groups.forEach(group -> {
            if(group.name.equals(name)) newGroup.set(group);
        });

        if(newGroup.get() != null) return newGroup.get();
        else
        {
            newGroup.set(new Group(name));
            Groups.add(newGroup.get());
            return newGroup.get();
        }
    }

    void removeGroup(Group group)
    {
        Groups.remove(group);
        for(Course course : Courses)
        {
            if(course.Groups.contains(group)) course.removeGroup(group);
        }
    }

    void addStudentToGroup(Group group, Student student)
    {
        if(!group.Students.contains(student))
        {
            group.addStudent(student);
        }
    }

    void removeStudentFromGroup(Group group, Student student)
    {
        group.removeStudent(student);
    }

    Course addCourse(String name)
    {
        AtomicReference<Course> newCourse = new AtomicReference<>();
        newCourse.set(null);
        Courses.forEach(course -> {
            if(course.name.equals(name)) newCourse.set(course);
        });

        if(newCourse.get() != null) return newCourse.get();
        else
        {
            newCourse.set(new Course(name));
            Courses.add(newCourse.get());
            return newCourse.get();
        }
    }

    void removeCourse(Course course)
    {
        Attendances.removeIf(attendance -> attendance.course.equals(course));
        Courses.remove(course);
    }

    void addGroupToCourse(Course course, Group group)
    {
        if(!course.Groups.contains(group))
        {
            course.addGroup(group);
        }
    }

    void removeGroupFromCourse(Course course, Group group)
    {
        course.removeGroup(group);
    }

    Attendance getAttendance(Student student, LocalDate date, Course course)
    {
        Optional<Attendance> result =  Attendances.stream().filter(attendance -> (attendance.student.equals(student) && attendance.date.equals(date) && attendance.course.equals(course))).findFirst();
        if (result.isPresent()) return result.get();
        else return new Attendance(student, date, course, false);
    }

    void saveAttendance(Attendance attendance)
    {
        if(!Attendances.contains(attendance))
        {
            Attendances.add(attendance);
        }
    }

    void saveDataToFile(File file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheetStudents = workbook.createSheet("Students");

        int irow = 0;

        for(Student student : Students)
        {
            XSSFRow row = sheetStudents.createRow(irow++);
            row.createCell(0).setCellValue(student.getName());
            row.createCell(1).setCellValue(student.getSurname());
            row.createCell(2).setCellValue(student.ID);
            row.createCell(3).setCellValue(student.getBirthday());
        }

        XSSFSheet sheetGroups = workbook.createSheet("Groups");
        irow = 0;
        for(Group group : Groups)
        {
            XSSFRow row = sheetGroups.createRow(irow++);
            row.createCell(0).setCellValue(group.name);
            int icell = 1;
            for(Student student : group.Students)
            {
                row.createCell(icell++).setCellValue(student.ID);
            }
        }

        XSSFSheet sheetCourses = workbook.createSheet("Courses");
        irow = 0;
        for(Course course : Courses)
        {
            XSSFRow row = sheetCourses.createRow(irow++);
            row.createCell(0).setCellValue(course.name);

            int icell = 1;
            for(Group group : course.Groups)
            {
                row.createCell(icell++).setCellValue(group.name);
            }
        }

        XSSFSheet sheetAttendance = workbook.createSheet("Attendance");
        irow = 0;
        for(Attendance attendance : Attendances)
        {
            XSSFRow row = sheetAttendance.createRow(irow++);
            row.createCell(0).setCellValue(attendance.student.ID);
            row.createCell(1).setCellValue(attendance.date.toString());
            row.createCell(2).setCellValue(attendance.course.name);
            row.createCell(3).setCellValue(attendance.attended);
        }

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }

    void loadDataFromFile(File file) throws IOException {
        Students.clear();
        Groups.clear();
        Courses.clear();
        Attendances.clear();

        Workbook workbook = WorkbookFactory.create(file);

        Sheet sheetStudents = workbook.getSheet("Students");
        Iterator<Row> irow = sheetStudents.rowIterator();
        //Add students
        while(irow.hasNext())
        {
            Row row = irow.next();
            Student student = new Student(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue(), LocalDate.parse(row.getCell(3).getStringCellValue()), (int)row.getCell(2).getNumericCellValue());
            Students.add(student);
        }

        //Add groups
        Sheet sheetGroups = workbook.getSheet("Groups");
        irow = sheetGroups.rowIterator();
        while (irow.hasNext())
        {
            Row row = irow.next();
            Group group = new Group(row.getCell(0).getStringCellValue());
            Iterator<Cell> icell = row.cellIterator();
            icell.next();
            while (icell.hasNext())
            {
                Cell cell = icell.next();
                Optional<Student> st =  Students.stream().filter(student -> (student.ID == (int)cell.getNumericCellValue())).findFirst();
                if (st.isPresent()) group.addStudent(st.get());
            }
            Groups.add(group);
        }

        //Add courses
        Sheet sheetCourses = workbook.getSheet("Courses");
        irow = sheetCourses.rowIterator();
        while(irow.hasNext())
        {
            Row row = irow.next();
            Course course = new Course(row.getCell(0).getStringCellValue());
            Iterator<Cell> icell = row.cellIterator();
            icell.next();
            while (icell.hasNext())
            {
                Cell cell = icell.next();
                Optional<Group> gr = Groups.stream().filter(group -> (group.name.equals(cell.getStringCellValue()))).findFirst();
                if(gr.isPresent()) course.addGroup(gr.get());
            }
            Courses.add(course);
        }

        //Add attendance
        Sheet sheetAttendance = workbook.getSheet("Attendance");
        irow = sheetAttendance.rowIterator();
        while (irow.hasNext())
        {
            Row row = irow.next();
            Optional<Student> student = Students.stream().filter(st -> (st.ID == (int)row.getCell(0).getNumericCellValue())).findFirst();
            Optional<Course> course = Courses.stream().filter(cr -> (cr.name.equals(row.getCell(2).getStringCellValue()))).findFirst();
            Attendance attendance = null;
            if(student.isPresent() && course.isPresent())  attendance = new Attendance(student.get(), LocalDate.parse(row.getCell(1).getStringCellValue()), course.get(), row.getCell(3).getBooleanCellValue());
            Attendances.add(attendance);
        }
    }

    void clearData()
    {
        Students.clear();
        Groups.clear();
        Courses.clear();
        Attendances.clear();
    }

}
