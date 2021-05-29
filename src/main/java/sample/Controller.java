package sample;

import Server.Client;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Controller {

    static DataController Data;

    //Tab students
    @FXML AnchorPane studentsHomePane;

    @FXML AnchorPane newStudentPane;
    @FXML TextField newStudentNameField;
    @FXML TextField newStudentSurnameField;
    @FXML DatePicker newStudentBirthdayPicker;

    @FXML AnchorPane studentsListPane;
    @FXML TableView<Student> studentTableView;
        @FXML TableColumn<Student, Integer> idColumn;
        @FXML TableColumn<Student, String> nameColumn;
        @FXML TableColumn<Student, String> surnameColumn;
        @FXML TableColumn<Student, String> birthdayColumn;
    @FXML TextField studentListSearch;

    //Tab groups
    @FXML MenuButton selectGroupButton;
    @FXML TextField newGroupNameField;
    @FXML TextField searchStudentList;
    @FXML TextField searchGroupStudents;
    @FXML TableView<Student> studentListView;
        @FXML TableColumn<Student, Integer> idColumn1;
        @FXML TableColumn<Student, String> nameColumn1;
        @FXML TableColumn<Student, String> surnameColumn1;
        @FXML TableColumn<Student, String> birthdayColumn1;

    @FXML TableView<Student> groupStudentsList;
        @FXML TableColumn<Student, Integer> idColumn2;
        @FXML TableColumn<Student, String> nameColumn2;
        @FXML TableColumn<Student, String> surnameColumn2;
        @FXML TableColumn<Student, String> birthdayColumn2;

    @FXML Button listToGroup;
    @FXML Button groupToList;

    //Tab courses

    @FXML MenuButton selectCourseButton;
    @FXML TextField newCourseNameField;
    @FXML TableView<Group> groupListView;
        @FXML TableColumn<Group, String> groupNameColumn;
    @FXML TableView<Group> groupsInCourseView;
        @FXML TableColumn<Group, String> groupNameColumn1;
    @FXML Button listToCourse;
    @FXML Button courseToList;

    //Tab attendance
    @FXML AnchorPane attendanceHomePane;

    @FXML AnchorPane markAttendancePane;
    @FXML DatePicker attendanceDatePicker;
    @FXML MenuButton attendanceCoursePicker;
    @FXML MenuButton attendanceGroupPicker;
    @FXML VBox attendanceVBox;

    @FXML AnchorPane attendancePane;
    @FXML TableView<Attendance> attendanceTableView;
        @FXML TableColumn<Attendance, String> dateColumn;
        @FXML TableColumn<Attendance, String> courseColumn;
        @FXML TableColumn<Attendance, Boolean> attendedColumn;
    @FXML DatePicker fromPicker;
    @FXML DatePicker toPicker;


    @FXML AnchorPane selectionPane;
    @FXML TextField searchField;
    @FXML ListView<Student> studentList;
    @FXML ListView<Group> groupList;
    @FXML ListView<Course> courseList;
    @FXML Button selectButton;
    @FXML Label nameLabel;
    @FXML Button pdfButton;

    //Chat tab
    @FXML TextField nicknameField;
    @FXML Label nicknameLb;
    @FXML TextArea chatArea;
    @FXML TextField chatField;
    @FXML AnchorPane chatPane;
    @FXML Button sendButton;



    static void start() throws IOException, InvalidFormatException {
        Data = new DataController();

        File startSetting = new File("StartSettings.txt");
        BufferedReader reader = new BufferedReader(new FileReader(startSetting));
        String filePath = reader.readLine();
        if(filePath != null && new File(filePath).exists())
        {
            Data.loadDataFromFile(new File(filePath));
        }
        else
        {
            Writer writer = new FileWriter("StartSettings.txt");
            writer.write("");
            writer.close();
        }
        reader.close();
    }

    //Student tab methods
    @FXML void openNewStudentPane()
    {
        studentsHomePane.setVisible(false);
        newStudentPane.setVisible(true);
        newStudentNameField.clear();
        newStudentSurnameField.clear();
        newStudentBirthdayPicker.setValue(LocalDate.now());
    }

    @FXML void openStudentListPane()
    {
        studentsListPane.setVisible(true);
        studentsHomePane.setVisible(false);
        loadStudentsTableView();
    }

    @FXML void openStudentHomePane()
    {
        newStudentPane.setVisible(false);
        studentsListPane.setVisible(false);
        studentsHomePane.setVisible(true);
    }

    @FXML void addStudent()
    {
        Data.addStudent(newStudentNameField.getText(), newStudentSurnameField.getText(), newStudentBirthdayPicker.getValue());
        openStudentHomePane();
    }

    @FXML void deleteStudent()
    {
        Student selected = studentTableView.getSelectionModel().getSelectedItem();
        Data.removeStudent(selected);
        loadStudentsTableView();
    }

    void loadStudentsTableView()
    {
        ObservableList<Student> studentObservableList =  FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        studentObservableList.addAll(Data.Students);

        FilteredList<Student> studentFilteredList = new FilteredList<>(studentObservableList);

        setStudentSearchFilter(studentListSearch, studentFilteredList);

        studentTableView.setItems(studentFilteredList);
    }

    //Group tab methods

    Group selectedGroup = null;

    @FXML void fillGroupSelection()
    {
        selectGroupButton.getItems().clear();
        Data.Groups.forEach(group -> {
            MenuItem mi =  new MenuItem(group.name);
            mi.setOnAction(event -> selectGroup(group));
            selectGroupButton.getItems().add(mi);
        });
    }

    void selectGroup(Group group)
    {
        selectGroupButton.setText(group.name);
        selectedGroup = group;
        fillTableViews(group);
    }

    void fillTableViews(Group group)
    {
        idColumn1.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn1.setCellValueFactory(new PropertyValueFactory<>("surname"));
        birthdayColumn1.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        idColumn2.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn2.setCellValueFactory(new PropertyValueFactory<>("surname"));
        birthdayColumn2.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        ObservableList<Student> studentList = FXCollections.observableArrayList();
        Data.Students.forEach(student -> {
            if(!group.Students.contains(student)) studentList.add(student);
        });

        ObservableList<Student> groupStudentList = FXCollections.observableArrayList();
        groupStudentList.addAll(group.Students);

        FilteredList<Student> studentFilteredList = new FilteredList<>(studentList);
        setStudentSearchFilter(searchStudentList, studentFilteredList);
        studentListView.setItems(studentFilteredList);

        FilteredList<Student> groupStudentFilteredList = new FilteredList<>(groupStudentList);
        setStudentSearchFilter(searchGroupStudents, groupStudentFilteredList);
        groupStudentsList.setItems(groupStudentFilteredList);
    }

    @FXML void createGroup()
    {
        if(!newGroupNameField.getText().isEmpty())
        {
            Group group = Data.addGroup(newGroupNameField.getText());
            fillGroupSelection();
        }
    }

    @FXML void deleteGroup()
    {
        if(selectedGroup != null) {
            Data.removeGroup(selectedGroup);
            selectGroupButton.setText("Select group");
            studentListView.setItems(null);
            groupStudentsList.setItems(null);
            selectedGroup = null;
            fillGroupSelection();
        }
    }

    @FXML void addStudentToGroup()
    {
        Data.addStudentToGroup(selectedGroup, studentListView.selectionModelProperty().get().getSelectedItem());
        fillTableViews(selectedGroup);
    }

    @FXML void removeStudentFromGroup()
    {
        Data.removeStudentFromGroup(selectedGroup, groupStudentsList.selectionModelProperty().get().getSelectedItem());
        fillTableViews(selectedGroup);
    }

    void resetGroupTab()
    {
        studentListView.setItems(null);
        groupStudentsList.setItems(null);
        selectedGroup = null;
        selectGroupButton.setText("Select group");
        fillGroupSelection();
    }

    //Courses tab methods

    Course selectedCourse = null;

    @FXML void fillCourseSelection()
    {
        selectCourseButton.getItems().clear();
        Data.Courses.forEach(course -> {
            MenuItem mi = new MenuItem(course.name);
            mi.setOnAction(e -> selectCourse(course));
            selectCourseButton.getItems().add(mi);
        });
    }

    void selectCourse(Course course)
    {
        selectedCourse = course;
        selectCourseButton.setText(course.name);
        fillCourseTableViews(course);
    }

    void fillCourseTableViews(Course course)
    {
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupNameColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<Group> groupObservableList = FXCollections.observableArrayList();
        Data.Groups.forEach(group -> {
            if(!course.Groups.contains(group)) groupObservableList.add(group);
        });

        ObservableList<Group> groupsInCourseList = FXCollections.observableArrayList();
        groupsInCourseList.addAll(course.Groups);

        groupListView.setItems(groupObservableList);
        groupsInCourseView.setItems(groupsInCourseList);
    }

    @FXML void createCourse()
    {
        if(!newCourseNameField.getText().isEmpty())
        {
            Course course = Data.addCourse(newCourseNameField.getText());
            fillCourseSelection();
        }
    }

    @FXML void deleteCourse()
    {
        if(selectedCourse != null)
        {
            Data.removeCourse(selectedCourse);
            selectCourseButton.setText("Select course");
            groupsInCourseView.setItems(null);
            groupListView.setItems(null);
            selectedCourse = null;
            fillCourseSelection();
        }
    }

    @FXML void addGroupToCourse()
    {
        Data.addGroupToCourse(selectedCourse, groupListView.selectionModelProperty().get().getSelectedItem());
        fillCourseTableViews(selectedCourse);
    }

    @FXML void removeGroupFromCourse()
    {
        Data.removeGroupFromCourse(selectedCourse, groupsInCourseView.selectionModelProperty().get().getSelectedItem());
        fillCourseTableViews(selectedCourse);
    }

    void resetCourseTab()
    {
        groupListView.setItems(null);
        groupsInCourseView.setItems(null);
        selectedCourse = null;
        selectCourseButton.setText("Select course");
        fillCourseSelection();
    }

    //Attendance tab methods

    Group selectedAttendanceGroup = null;
    Course selectedAttendanceCourse = null;
    Set<AttendanceMarkerElement> selectedAttendances = new HashSet<>();

    @FXML void openAttendanceMarker()
    {
        markAttendancePane.setVisible(true);
        attendanceHomePane.setVisible(false);
        resetAttendanceMarker();
    }

    @FXML void closeAttendanceMarker()
    {
        if(markAttendancePane.isVisible()) {
            markAttendancePane.setVisible(false);
            attendanceHomePane.setVisible(true);
            resetAttendanceMarker();
        }
    }

    void resetAttendanceMarker()
    {
        attendanceDatePicker.setValue(LocalDate.now());
        attendanceDatePicker.valueProperty().addListener(observable -> {
            fillCoursePicker();
            attendanceCoursePicker.setDisable(false);
            if(selectedAttendanceCourse != null && selectedAttendanceGroup != null) {
                fillAttendanceMarker();
            }
        });
        selectedAttendanceGroup = null;
        selectedAttendanceCourse = null;
        attendanceCoursePicker.setDisable(true);
        attendanceCoursePicker.setText("Course");
        attendanceGroupPicker.setDisable(true);
        attendanceGroupPicker.setText("Group");
        attendanceVBox.getChildren().clear();
    }

    void fillCoursePicker()
    {
        attendanceCoursePicker.getItems().clear();
        Data.Courses.forEach(course -> {
            MenuItem mi = new MenuItem(course.name);
            mi.setOnAction(e -> {
                selectedAttendanceCourse = course;
                attendanceCoursePicker.setText(course.name);
                attendanceGroupPicker.setDisable(false);
                fillGroupPicker();
                attendanceGroupPicker.setText("Group");
                selectedAttendanceGroup = null;
                selectedAttendances.clear();
                attendanceVBox.getChildren().clear();
            });
            attendanceCoursePicker.getItems().add(mi);
        });
    }

    void fillGroupPicker()
    {
        attendanceGroupPicker.getItems().clear();
        selectedAttendanceCourse.Groups.forEach(group -> {
            MenuItem mi = new MenuItem(group.name);
            mi.setOnAction(event -> {
                selectedAttendanceGroup = group;
                attendanceGroupPicker.setText(group.name);
                fillAttendanceMarker();
            });
            attendanceGroupPicker.getItems().add(mi);
        });
    }

    void fillAttendanceMarker()
    {
        selectedAttendances.clear();
        attendanceVBox.getChildren().clear();
        selectedAttendanceGroup.Students.forEach(student -> {
            Attendance attendance = Data.getAttendance(student, attendanceDatePicker.getValue(), selectedAttendanceCourse);
            AttendanceMarkerElement attendanceMarkerElement = new AttendanceMarkerElement(attendance);
            attendanceVBox.getChildren().add(attendanceMarkerElement.pane);
            selectedAttendances.add(attendanceMarkerElement);
        });
    }

    @FXML void saveAttendances()
    {
        selectedAttendances.forEach(attendanceMarkerElement -> {
          attendanceMarkerElement.attendance.attended = attendanceMarkerElement.attended.isSelected();
          Data.saveAttendance(attendanceMarkerElement.attendance);
        });
    }

    @FXML void attendanceBack()
    {
        selectionPane.setVisible(true);
        attendancePane.setVisible(false);
        dateColumn.setVisible(true);
        courseColumn.setVisible(true);
        attendedColumn.setVisible(true);
    }

    @FXML void selectStudent()
    {
        ObservableList<Student> observableList = FXCollections.observableArrayList();
        observableList.addAll(Data.Students);
        attendanceHomePane.setVisible(false);
        selectionPane.setVisible(true);

        FilteredList<Student> filteredList = new FilteredList<>(observableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(student -> {
            return student.toString().contains(newValue);
        }));
        studentList.setItems(filteredList);
        studentList.setVisible(true);
        selectButton.setOnAction(event -> {
           if(!studentList.selectionModelProperty().get().isEmpty()) showAttendances(studentList.selectionModelProperty().get().getSelectedItem());
        });
    }

    void showAttendances(Student student)
    {
        selectionPane.setVisible(false);
        attendancePane.setVisible(true);
        fromPicker.setValue(LocalDate.now());
        toPicker.setValue(LocalDate.now());
        fillStudentAttendance(student);
        nameLabel.setText(student.name + " " + student.getSurname() + " attendance");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        attendedColumn.setCellValueFactory(new PropertyValueFactory<>("attended"));

        fromPicker.valueProperty().addListener(observable -> fillStudentAttendance(student));

        toPicker.valueProperty().addListener(observable -> fillStudentAttendance(student));
    }

    void fillStudentAttendance(Student student)
    {
        ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
        for(Attendance attendance : Data.Attendances)
        {
            if(attendance.student.equals(student) && attendance.date.compareTo(fromPicker.getValue()) >= 0 && attendance.date.compareTo(toPicker.getValue()) <= 0) attendanceList.add(attendance);
        }
        Comparator<Attendance> comparator = Comparator.comparing(Attendance::getDate);
        FXCollections.sort(attendanceList, comparator);
        attendanceTableView.setItems(attendanceList);
        pdfButton.setOnAction(event -> {
            try {
                exportToPDF(student.name + " " + student.getSurname() +" attendance", attendanceList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }


    @FXML void selectGroup()
    {
        ObservableList<Group> observableList = FXCollections.observableArrayList();
        observableList.addAll(Data.Groups);
        attendanceHomePane.setVisible(false);
        selectionPane.setVisible(true);

        FilteredList<Group> filteredList = new FilteredList<>(observableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(group -> {
                return group.toString().contains(newValue);
            });
        });
        groupList.setItems(filteredList);
        groupList.setVisible(true);
        selectButton.setOnAction(event -> {
            if(!groupList.selectionModelProperty().get().isEmpty()) showAttendances(groupList.selectionModelProperty().get().getSelectedItem());
        });
    }

    void showAttendances(Group group)
    {
        selectionPane.setVisible(false);
        attendancePane.setVisible(true);
        fromPicker.setValue(LocalDate.now());
        toPicker.setValue(LocalDate.now());
        fillGroupAttendance(group);
        nameLabel.setText(group.name + " timetable");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        //attendedColumn.setCellValueFactory(new PropertyValueFactory<>("attended"));
        attendedColumn.setVisible(false);

        fromPicker.valueProperty().addListener(observable -> fillGroupAttendance(group));

        toPicker.valueProperty().addListener(observable -> fillGroupAttendance(group));
    }

    void fillGroupAttendance(Group group)
    {
        ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

        for(Course course : Data.Courses)
        {
            if(course.Groups.contains(group))
            {
                for(LocalDate start = fromPicker.getValue(); start.compareTo(toPicker.getValue()) <= 0; start = start.plusDays(1))
                {
                    LocalDate finalStart = start;
                    Optional<Attendance> aa = Data.Attendances.stream().filter(attendance -> (attendance.course.equals(course) && attendance.date.equals(finalStart))).findFirst();
                    if(aa.isPresent()) attendanceList.add(aa.get());
                }
            }
        }
        Comparator<Attendance> comparator = Comparator.comparing(Attendance::getDate);
        FXCollections.sort(attendanceList, comparator);
        attendanceTableView.setItems(attendanceList);
        pdfButton.setOnAction(event -> {
            try {
                exportToPDF(group.name + " timetable", attendanceList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }


    @FXML void selectCourse()
    {
        ObservableList<Course> observableList = FXCollections.observableArrayList();
        observableList.addAll(Data.Courses);
        attendanceHomePane.setVisible(false);
        selectionPane.setVisible(true);

        FilteredList<Course> filteredList = new FilteredList<>(observableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(course -> {
                return course.toString().contains(newValue);
            });
        });
        courseList.setItems(filteredList);
        courseList.setVisible(true);
        selectButton.setOnAction(event -> {
            if(!courseList.selectionModelProperty().get().isEmpty()) showAttendances(courseList.selectionModelProperty().get().getSelectedItem());
        });
    }

    void showAttendances(Course course)
    {
        selectionPane.setVisible(false);
        attendancePane.setVisible(true);
        fromPicker.setValue(LocalDate.now());
        toPicker.setValue(LocalDate.now());
        fillCourseAttendance(course);
        nameLabel.setText(course.name + " timetable");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        courseColumn.setVisible(false);
        attendedColumn.setVisible(false);

        fromPicker.valueProperty().addListener(observable -> fillCourseAttendance(course));

        toPicker.valueProperty().addListener(observable -> fillCourseAttendance(course));
    }

    void fillCourseAttendance(Course course)
    {
        ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

        //Get filtered values
        for(LocalDate start = fromPicker.getValue(); start.compareTo(toPicker.getValue()) <= 0; start = start.plusDays(1))
        {
            LocalDate finalStart = start;
            Optional<Attendance> aa = Data.Attendances.stream().filter(attendance -> (attendance.course.equals(course) && attendance.date.equals(finalStart))).findFirst();
            if(aa.isPresent()) attendanceList.add(aa.get());
        }

        //Sort by date
        Comparator<Attendance> comparator = Comparator.comparing(Attendance::getDate);
        FXCollections.sort(attendanceList, comparator);

        attendanceTableView.setItems(attendanceList);
        pdfButton.setOnAction(event -> {
            try {
                exportToPDF(course.name + " timetable", attendanceList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML void closeSelection()
    {
        studentList.setItems(null);
        studentList.setVisible(false);
        groupList.setItems(null);
        groupList.setVisible(false);
        courseList.setItems(null);
        courseList.setVisible(false);
        searchField.clear();
        selectionPane.setVisible(false);
        attendanceHomePane.setVisible(true);
    }


    //File methods
    @FXML void newFile()
    {
        Data.clearData();
        attendanceBack();
        closeSelection();
        openStudentHomePane();
        resetCourseTab();
        resetGroupTab();
    }

    @FXML void saveToFile() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLXS", "*.xlsx" ));
        File file = fc.showSaveDialog(attendanceHomePane.getScene().getWindow());
        Data.saveDataToFile(file);

        Writer writer = new FileWriter("StartSettings.txt");
        writer.write(file.getAbsolutePath());
        writer.close();
    }

    @FXML void loadFromFile() throws IOException, InvalidFormatException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLXS", "*.xlsx"));
        File file = fc.showOpenDialog(attendanceHomePane.getScene().getWindow());
        Data.loadDataFromFile(file);
    }

    @FXML void exportToPDF(String text, ObservableList<Attendance> attendanceList) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fc.showSaveDialog(attendanceHomePane.getScene().getWindow());

        PdfWriter pdfWriter = new PdfWriter(file.getAbsolutePath());
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        pdfDocument.addNewPage();

        Document document = new Document(pdfDocument);

        //Add name
        Paragraph para = new Paragraph(text);
        document.add(para);

        //Add filtered date
        document.add(new Paragraph("From: " + fromPicker.getValue() + " To: " + toPicker.getValue()));

        float[] cellSizes;

        if(courseColumn.isVisible() && attendedColumn.isVisible()) cellSizes = new float[]{ 100f, 100f, 100f};
        else if(courseColumn.isVisible()) cellSizes = new float[]{100f, 100f};
        else cellSizes = new float[]{100f};

        Table table = new Table(cellSizes);

        //Add column names
        table.addCell(new Cell().add("Date").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(2)));
        if(courseColumn.isVisible()) table.addCell(new Cell().add("Course").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(2)));
        if(attendedColumn.isVisible()) table.addCell(new Cell().add("Attended").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(2)));

        //Add attendance
        for(Attendance attendance : attendanceList)
        {
            table.addCell(new Cell().add(attendance.date.toString()));
            if(courseColumn.isVisible()) table.addCell(new Cell().add(attendance.course.name));
            if(attendedColumn.isVisible()) table.addCell(new Cell().add(Boolean.toString(attendance.attended)));
        }

        document.add(table);
        document.close();
    }

    //Extra methods
    void setStudentSearchFilter(TextField searchStudentList, FilteredList<Student> studentFilteredList)
    {
        searchStudentList.textProperty().addListener((observable, oldValue, newValue) -> {
            studentFilteredList.setPredicate(student -> {
                if(newValue == null || newValue.isEmpty()) return true;

                String filter = newValue.toLowerCase();

                if(student.getName().toLowerCase().contains(filter)) return true;
                else if(student.getSurname().toLowerCase().contains(filter)) return true;
                else if(Integer.toString(student.getID()).contains(filter)) return true;
                else if(student.getBirthday().contains(filter)) return true;
                else return false;
            });
        });
    }

    //********************************************Chat*******************************************************

    Client client = new Client("0.0.0.0", 55555, data -> Platform.runLater(()->{
        chatArea.appendText(data.toString() + "\n");
        if(data.toString().equals("Connection closed")) sendButton.setDisable(true);
    }));

    @FXML void openChat() throws Exception {
        chatPane.setVisible(true);
        nicknameLb.setText(nicknameField.getText());
        sendButton.setDisable(false);
        client.startConnection();
    }

    @FXML void  closeChat() throws Exception
    {
        client.closeConnection();
        chatPane.setVisible(false);
    }

    @FXML void sendMessage() throws Exception {
        client.send(nicknameLb.getText()+ ": " + chatField.getText());
        chatField.clear();
    }
}


class AttendanceMarkerElement
{
    AnchorPane pane;
    Label label;
    RadioButton attended;
    RadioButton notAttended;

    Attendance attendance;

    AttendanceMarkerElement(Attendance attendance)
    {
        this.attendance = attendance;
        ToggleGroup group = new ToggleGroup();

        attended = new RadioButton();
        attended.setToggleGroup(group);
        attended.setOnAction(event -> toggleChange());
        attended.setLayoutX(600.0);
        attended.setLayoutY(20.0);
        attended.setText("Attended");

        notAttended = new RadioButton();
        notAttended.setToggleGroup(group);
        notAttended.setOnAction(event -> toggleChange());
        notAttended.setLayoutX(700.0);
        notAttended.setLayoutY(20.0);
        notAttended.setText("Didn't attend");

        if(attendance.attended) attended.selectedProperty().set(true);
        else notAttended.selectedProperty().set(true);

        label = new Label(attendance.student.ID + " " + attendance.student.getName() + " " + attendance.student.getSurname());
        label.setLayoutY(20.0);
        label.setLayoutX(14.0);

        pane = new AnchorPane();
        pane.prefHeight(57.0);
        pane.prefWidth(800.0);
        pane.setStyle("-fx-background-color:lightGrey");
        pane.setMinSize(800.0, 57.0);

        pane.getChildren().addAll(label, attended, notAttended);
    }

    void toggleChange()
    {
        attendance.attended = attended.isSelected();
    }
}