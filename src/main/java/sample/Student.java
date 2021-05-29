package sample;

import java.time.LocalDate;
import java.util.Random;

public class Student extends BasicObject{
    int ID;
    //private String name;
    private String surname;
    private LocalDate birthday;

    public Student(String name, String surname, LocalDate birthday)
    {
        super(name);
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        Random rd = new Random();
        this.ID = birthday.getDayOfYear() + LocalDate.now().getDayOfYear() + rd.nextInt(8000);
    }

    public Student(String name, String surname, LocalDate birthday, int ID)
    {
        super(name);
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.ID = ID;
    }

    //void setName(String name){this.name = name;}
    public String getName(){return name;}

    void setSurname(String surname){this.surname = surname;}
    public String getSurname(){return surname;}

    void setID(int id){this.ID = id;}
    public int getID(){return  ID;}

    void setBirthday(LocalDate birthday){this.birthday = birthday;}
    public String getBirthday() { return birthday.toString();}

    public String toString()
    {
        return ID + " " + name + " " + surname;
    }
}