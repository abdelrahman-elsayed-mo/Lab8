    package FrontEnd;

import Services.*;
import BackEnd.*;
import databse.*;
import Quiz.Question;
import Utils.IdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class InstructorDashboard extends JFrame {

    private String currentInstructorId;
    private JsonDatabaseManager dbManager;
    private InstructorService instructorService;
    private CourseService courseService;
    private QuizService quizService;
    private UserService userService;
    private JTable coursesTable;
    private JTable lessonsTable;
    private JTable studentsTable;
    private JTable quizTable;
    private DefaultTableModel coursesTableModel;
    private DefaultTableModel lessonsTableModel;
    private DefaultTableModel studentsTableModel;
    private DefaultTableModel quizTableModel;

    private JButton createCourseButton;
    private JButton editCourseButton;
    private JButton deleteCourseButton;
    private JButton addLessonButton;
    private JButton editLessonButton;
    private JButton deleteLessonButton;
    private JButton viewStudentsButton;
    private JButton logoutButton;

    // Quiz Management Buttons
    private JButton createQuizButton;
    private JButton editQuizButton;
    private JButton deleteQuizButton;
    private JButton addQuestionButton;
    private JButton removeQuestionButton;
    private JButton editQuestionButton;

    // Selection components for quizzes
    private JComboBox<String> courseCombo;
    private JComboBox<String> lessonCombo;
    private Map<String, Course> courseMap;
    private Map<String, Lesson> lessonMap;

    public InstructorDashboard(JsonDatabaseManager dbManager, String instructorId, InstructorService instructorService, CourseService courseService, QuizService quizService, UserService userService) {
        this.currentInstructorId = instructorId;
        this.dbManager = dbManager;
        this.instructorService = instructorService;
        this.courseService = courseService;
        this.quizService = quizService;
        this.userService = userService;

        User currentUser = dbManager.getUserById(instructorId);
        if (currentUser instanceof Instructor) {
            this.instructorService = new InstructorService(dbManager, currentUser, this.courseService, this.quizService, this.userService);
        } else {
            JOptionPane.showMessageDialog(this, "Error: User is not an instructor");
            System.exit(1);
        }

        initializeUI();
        loadInstructorCourses();
        loadAllLessons();
        loadEnrolledStudents();
        initializeQuizComponents();
    }

    private void initializeUI() {
        setTitle("Instructor Dashboard - Skill Forge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Instructor Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        tabbedPane.addTab("My Courses", createCoursesPanel());
        tabbedPane.addTab("All Lessons", createLessonsPanel());
        tabbedPane.addTab("Enrolled Students", createStudentsPanel());
        tabbedPane.addTab("Quiz Management", createQuizManagementPanel());
        tabbedPane.addTab("Performance Analytics", createAnalyticsPanel());
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createQuizManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Course and Lesson"));

        selectionPanel.add(new JLabel("Course:"));
        courseCombo = new JComboBox<>();
        selectionPanel.add(courseCombo);

        selectionPanel.add(new JLabel("Lesson:"));
        lessonCombo = new JComboBox<>();
        selectionPanel.add(lessonCombo);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshQuizData());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        String[] columns = {"Question ID", "Content", "Correct Answer", "Options"};
        quizTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quizTable = new JTable(quizTableModel);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane quizScrollPane = new JScrollPane(quizTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        createQuizButton = new JButton("Create Quiz");
        editQuizButton = new JButton("Edit Quiz");
        deleteQuizButton = new JButton("Delete Quiz");
        addQuestionButton = new JButton("Add Question");
        removeQuestionButton = new JButton("Remove Question");
        editQuestionButton = new JButton("Edit Question");

        buttonPanel.add(createQuizButton);
        buttonPanel.add(editQuizButton);
        buttonPanel.add(deleteQuizButton);
        buttonPanel.add(addQuestionButton);
        buttonPanel.add(removeQuestionButton);
        buttonPanel.add(editQuestionButton);

        createQuizButton.addActionListener(e -> createQuiz());
        editQuizButton.addActionListener(e -> editQuiz());
        deleteQuizButton.addActionListener(e -> deleteQuiz());
        addQuestionButton.addActionListener(e -> addQuestion());
        removeQuestionButton.addActionListener(e -> removeQuestion());
        editQuestionButton.addActionListener(e -> editQuestion());

        courseCombo.addActionListener(e -> updateLessonCombo());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(quizScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void initializeQuizComponents() {
        refreshCourseCombo();
        updateLessonCombo();
        refreshQuizData();
    }

    private void refreshCourseCombo() {
        courseCombo.removeAllItems();
        courseMap = new HashMap<>();

        List<Course> instructorCourses = getInstructorCourses();
        for (Course course : instructorCourses) {
            String display = course.getTitle() + " (" + course.getCourseId() + ")";
            courseCombo.addItem(display);
            courseMap.put(display, course);
        }
    }

    private void updateLessonCombo() {
        lessonCombo.removeAllItems();
        lessonMap = new HashMap<>();

        String selectedCourse = (String) courseCombo.getSelectedItem();
        if (selectedCourse != null) {
            Course course = courseMap.get(selectedCourse);
            if (course != null) {
                for (Lesson lesson : course.getLessons()) {
                    String display = lesson.getTitle() + " (" + lesson.getLessonId() + ")";
                    lessonCombo.addItem(display);
                    lessonMap.put(display, lesson);
                }
            }
        }
        refreshQuizData();
    }

    private void refreshQuizData() {
        quizTableModel.setRowCount(0);

        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson != null) {
            Lesson lesson = lessonMap.get(selectedLesson);
            if (lesson != null && lesson.getQuiz() != null) {
                Quiz quiz = lesson.getQuiz();
                for (Question question : quiz.getQuestions()) {
                    String options = String.join(", ", question.getOptions());
                    quizTableModel.addRow(new Object[]{
                        question.getQuestionId(),
                        question.getContent(),
                        question.getCorrectAnswer(),
                        options
                    });
                }
            }
        }
        updateButtonStates();
    }

    private void updateButtonStates() {
        String selectedLesson = (String) lessonCombo.getSelectedItem();
        boolean hasLesson = selectedLesson != null;
        boolean hasQuiz = false;
        boolean hasQuestions = false;

        if (hasLesson) {
            Lesson lesson = lessonMap.get(selectedLesson);
            hasQuiz = lesson != null && lesson.getQuiz() != null;
            if (hasQuiz) {
                hasQuestions = !lesson.getQuiz().getQuestions().isEmpty();
            }
        }

        createQuizButton.setEnabled(hasLesson && !hasQuiz);
        editQuizButton.setEnabled(hasQuiz);
        deleteQuizButton.setEnabled(hasQuiz);
        addQuestionButton.setEnabled(hasQuiz);
        removeQuestionButton.setEnabled(hasQuiz && hasQuestions);
        editQuestionButton.setEnabled(hasQuiz && hasQuestions);
    }

    private void createQuiz() {
        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }
        Lesson lesson = lessonMap.get(selectedLesson);
        Course course = findCourseByLesson(lesson.getLessonId());

        JTextField titleField = new JTextField(20);
        Object[] message = {   "Quiz Title:", titleField };

        int option = JOptionPane.showConfirmDialog(this, message, "Create Quiz",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Quiz title cannot be empty!");
                return;
            }

            List<Question> questions = new ArrayList<>();

            boolean success = instructorService.createQuiz( course.getCourseId(),lesson.getLessonId(),title,questions );
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Quiz created successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create quiz.");
            }
        }
    }

    private void editQuiz() {
        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }
        Lesson lesson = lessonMap.get(selectedLesson);
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz found for this lesson.");
            return;
        }
        Quiz quiz = lesson.getQuiz();
        Course course = findCourseByLesson(lesson.getLessonId());

        JTextField titleField = new JTextField(quiz.getName(), 20);
        Object[] message = {
            "Quiz Title:", titleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Quiz",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Quiz title cannot be empty!");
                return;
            }

            boolean success = instructorService.editQuiz(
                    course.getCourseId(),
                    lesson.getLessonId(),
                    quiz.getQuizID(),
                    title,
                    quiz.getQuestions()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Quiz updated successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update quiz.");
            }
        }
    }

    private void deleteQuiz() {
        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        Lesson lesson = lessonMap.get(selectedLesson);
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz found for this lesson.");
            return;
        }
        Quiz quiz = lesson.getQuiz();
        Course course = findCourseByLesson(lesson.getLessonId());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the quiz: " + quiz.getName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = instructorService.deleteQuiz(
                    course.getCourseId(),
                    lesson.getLessonId(),
                    quiz.getQuizID()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Quiz deleted successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete quiz.");
            }
        }
    }

    private void addQuestion() {
        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        Lesson lesson = lessonMap.get(selectedLesson);
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz found for this lesson.");
            return;
        }

        Course course = findCourseByLesson(lesson.getLessonId());

        JTextField contentField = new JTextField(30);
        JTextField correctAnswerField = new JTextField(20);
        JTextArea optionsArea = new JTextArea(5, 30);
        optionsArea.setLineWrap(true);
        JScrollPane optionsScroll = new JScrollPane(optionsArea);
        optionsArea.setToolTipText("Enter one option per line");

        Object[] message = {
            "Question Content:", contentField,
            "Correct Answer:", correctAnswerField,
            "Options (one per line):", optionsScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Question",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String content = contentField.getText().trim();
            String correctAnswer = correctAnswerField.getText().trim();
            String optionsText = optionsArea.getText().trim();

            if (content.isEmpty() || correctAnswer.isEmpty() || optionsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            ArrayList<String> options = new ArrayList<>();
            String[] optionsArray = optionsText.split("\\r?\\n");
            for (String opt : optionsArray) {
                if (!opt.trim().isEmpty()) {
                    options.add(opt.trim());
                }
            }

            IdGenerator ID = new IdGenerator();
            String questionId = ID.generateQuestionId();
            Question newQuestion = new Question(questionId, content, correctAnswer, options);

            boolean success = instructorService.addQuestionToQuiz(
                    course.getCourseId(),
                    lesson.getLessonId(),
                    newQuestion
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Question added successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add question.");
            }
        }
    }

    private void removeQuestion() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to remove.");
            return;
        }

        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        Lesson lesson = lessonMap.get(selectedLesson);
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz found for this lesson.");
            return;
        }

        Course course = findCourseByLesson(lesson.getLessonId());
        String questionId = (String) quizTableModel.getValueAt(selectedRow, 0);
        
        Question questionToRemove = null;
        for (Question q : lesson.getQuiz().getQuestions()) {
            if (q.getQuestionId().equals(questionId)) {
                questionToRemove = q;
                break;
            }
        }

        if (questionToRemove == null) {
            JOptionPane.showMessageDialog(this, "Question not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this question?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = instructorService.removeQuestionFromQuiz(
                    course.getCourseId(),
                    lesson.getLessonId(),
                    questionToRemove
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Question removed successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove question.");
            }
        }
    }

    private void editQuestion() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to edit.");
            return;
        }

        String selectedLesson = (String) lessonCombo.getSelectedItem();
        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        Lesson lesson = lessonMap.get(selectedLesson);
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz found for this lesson.");
            return;
        }

        Course course = findCourseByLesson(lesson.getLessonId());
        String questionId = (String) quizTableModel.getValueAt(selectedRow, 0);

        Question questionToEdit = null;
        for (Question q : lesson.getQuiz().getQuestions()) {
            if (q.getQuestionId().equals(questionId)) {
                questionToEdit = q;
                break;
            }
        }

        if (questionToEdit == null) {
            JOptionPane.showMessageDialog(this, "Question not found.");
            return;
        }

        JTextField contentField = new JTextField(questionToEdit.getContent(), 30);
        JTextField correctAnswerField = new JTextField(questionToEdit.getCorrectAnswer(), 20);
        JTextArea optionsArea = new JTextArea(5, 30);
        optionsArea.setLineWrap(true);
        JScrollPane optionsScroll = new JScrollPane(optionsArea);

        StringBuilder optionsText = new StringBuilder();
        for (String option : questionToEdit.getOptions()) {
            optionsText.append(option).append("\n");
        }
        optionsArea.setText(optionsText.toString());
        optionsArea.setToolTipText("Enter one option per line");

        Object[] message = {
            "Question Content:", contentField,
            "Correct Answer:", correctAnswerField,
            "Options (one per line):", optionsScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Question",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String content = contentField.getText().trim();
            String correctAnswer = correctAnswerField.getText().trim();
            String newOptionsText = optionsArea.getText().trim();

            if (content.isEmpty() || correctAnswer.isEmpty() || newOptionsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            List<String> newOptions = new ArrayList<>();
            String[] optionsArray = newOptionsText.split("\\r?\\n");
            for (String opt : optionsArray) {
                if (!opt.trim().isEmpty()) {
                    newOptions.add(opt.trim());
                }
            }

            boolean success = instructorService.updateQuestionInQuiz(
                    course.getCourseId(),
                    lesson.getLessonId(),
                    questionId,
                    content,
                    correctAnswer,
                    newOptions
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Question updated successfully!");
                refreshQuizData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update question.");
            }
        }
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Course ID", "Title", "Description", "Students", "Lessons"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(coursesTableModel);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(coursesTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        createCourseButton = new JButton("Create New Course");
        editCourseButton = new JButton("Edit Course");
        deleteCourseButton = new JButton("Delete Course");

        buttonPanel.add(createCourseButton);
        buttonPanel.add(editCourseButton);
        buttonPanel.add(deleteCourseButton);

        createCourseButton.addActionListener(e -> createCourse());
        editCourseButton.addActionListener(e -> editCourse());
        deleteCourseButton.addActionListener(e -> deleteCourse());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLessonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Lesson ID", "Title", "Course", "Content Preview"};
        lessonsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lessonsTable = new JTable(lessonsTableModel);
        lessonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(lessonsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addLessonButton = new JButton("Add Lesson");
        editLessonButton = new JButton("Edit Lesson");
        deleteLessonButton = new JButton("Delete Lesson");

        buttonPanel.add(addLessonButton);
        buttonPanel.add(editLessonButton);
        buttonPanel.add(deleteLessonButton);

        addLessonButton.addActionListener(e -> addLesson());
        editLessonButton.addActionListener(e -> editLesson());
        deleteLessonButton.addActionListener(e -> deleteLesson());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Student ID", "Username", "Email", "Course", "Progress"};
        studentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(studentsTableModel);
        JScrollPane scrollPane = new JScrollPane(studentsTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadInstructorCourses() {
        coursesTableModel.setRowCount(0);
        Collection<Course> allCourses = dbManager.getAllCourses();

        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructorId)) {
                int studentCount = course.getStudents().size();
                int lessonCount = course.getLessons().size();
                coursesTableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    studentCount + " students",
                    lessonCount + " lessons"
                });
            }
        }
    }

    private void loadAllLessons() {
        lessonsTableModel.setRowCount(0);
        Collection<Course> allCourses = dbManager.getAllCourses();

        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructorId)) {
                for (Lesson lesson : course.getLessons()) {
                    String contentPreview = lesson.getContent().length() > 50
                            ? lesson.getContent().substring(0, 50) + "..." : lesson.getContent();
                    lessonsTableModel.addRow(new Object[]{
                        lesson.getLessonId(),
                        lesson.getTitle(),
                        course.getTitle(),
                        contentPreview
                    });
                }
            }
        }
    }

    private void loadEnrolledStudents() {
        studentsTableModel.setRowCount(0);
        Collection<Course> allCourses = dbManager.getAllCourses();
        List<Student> allStudents = getAllStudents();

        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructorId)) {
                for (String studentId : course.getStudents()) {
                    Student student = findStudentById(allStudents, studentId);
                    if (student != null) {

                        int totalLessons = course.getLessons().size();
                        int completedLessons = student.getCompletedLessons(course.getCourseId()).size();
                        String progress = totalLessons > 0
                                ? (completedLessons * 100 / totalLessons) + "%" : "0%";

                        studentsTableModel.addRow(new Object[]{
                            student.getUserId(),
                            student.getUsername(),
                            student.getEmail(),
                            course.getTitle(),
                            progress
                        });
                    }
                }
            }
        }
    }

    private void createCourse() {
        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        Object[] message = {
            "Course Title:", titleField,
            "Course Description:", descriptionScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New Course",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course title cannot be empty!");
                return;
            }

            Course newCourse = instructorService.createCourse(title, description);
            if (newCourse != null) {
                loadInstructorCourses();
                JOptionPane.showMessageDialog(this, "Course created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create course. Please try again.");
            }
        }
    }

    private void editCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.");
            return;
        }

        String courseId = (String) coursesTableModel.getValueAt(selectedRow, 0);
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found!");
            return;
        }

        JTextField titleField = new JTextField(course.getTitle());
        JTextArea descriptionArea = new JTextArea(course.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        Object[] message = {
            "Course Title:", titleField,
            "Course Description:", descriptionScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Course",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course title cannot be empty!");
                return;
            }

            boolean success = instructorService.editCourse(courseId, title, description);
            if (success) {
                loadInstructorCourses();
                JOptionPane.showMessageDialog(this, "Course updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update course.");
            }
        }
    }

    private void deleteCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.");
            return;
        }

        String courseId = (String) coursesTableModel.getValueAt(selectedRow, 0);
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the course: " + course.getTitle() + "?\nThis will also delete all associated lessons.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = instructorService.deleteCourse(courseId);
            if (success) {
                loadInstructorCourses();
                loadAllLessons();
                loadEnrolledStudents();
                JOptionPane.showMessageDialog(this, "Course deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course.");
            }
        }
    }

    private void addLesson() {
        List<Course> instructorCourses = getInstructorCourses();
        if (instructorCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create a course first before adding lessons.");
            return;
        }

        JComboBox<String> courseCombo = new JComboBox<>();
        Map<String, Course> courseMap = new HashMap<>();
        for (Course course : instructorCourses) {
            courseCombo.addItem(course.getTitle() + " (" + course.getCourseId() + ")");
            courseMap.put(course.getTitle() + " (" + course.getCourseId() + ")", course);
        }

        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(10, 30);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        JTextArea resourcesArea = new JTextArea(3, 30);
        resourcesArea.setLineWrap(true);
        JScrollPane resourcesScroll = new JScrollPane(resourcesArea);
        resourcesArea.setToolTipText("Enter one resource per line (URLs, file names, etc.)");

        Object[] message = {
            "Select Course:", courseCombo,
            "Lesson Title:", titleField,
            "Lesson Content:", contentScroll,
            "Resources (one per line):", resourcesScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Lesson",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String resourcesText = resourcesArea.getText().trim();  // ✅ GET RESOURCES
            Course selectedCourse = courseMap.get(courseCombo.getSelectedItem());

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and content cannot be empty!");
                return;
            }

            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid course.");
                return;
            }

            Lesson newLesson = instructorService.addLesson(selectedCourse.getCourseId(), title, content);

            if (newLesson != null && !resourcesText.isEmpty()) {
                String[] resourcesArray = resourcesText.split("\\r?\\n");
                for (String resource : resourcesArray) {
                    if (!resource.trim().isEmpty()) {
                        newLesson.addResource(resource.trim());
                    }
                }

                dbManager.saveCourse(selectedCourse);
            }

            if (newLesson != null) {
                loadAllLessons();
                loadInstructorCourses();
                JOptionPane.showMessageDialog(this, "Lesson added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add lesson.");
            }
        }
    }
    private void editLesson() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to edit.");
            return;
        }

        String lessonId = (String) lessonsTableModel.getValueAt(selectedRow, 0);
        Lesson lesson = findLessonById(lessonId);
        if (lesson == null) {
            JOptionPane.showMessageDialog(this, "Lesson not found!");
            return;
        }

        Course lessonCourse = findCourseByLesson(lessonId);
        if (lessonCourse == null) {
            JOptionPane.showMessageDialog(this, "Could not find the course for this lesson.");
            return;
        }

        JTextField titleField = new JTextField(lesson.getTitle());
        JTextArea contentArea = new JTextArea(lesson.getContent(), 10, 30);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        JTextArea resourcesArea = new JTextArea(3, 30);
        resourcesArea.setLineWrap(true);
        JScrollPane resourcesScroll = new JScrollPane(resourcesArea);

        StringBuilder existingResources = new StringBuilder();
        for (String resource : lesson.getResources()) {
            existingResources.append(resource).append("\n");
        }
        resourcesArea.setText(existingResources.toString());
        resourcesArea.setToolTipText("Enter one resource per line (URLs, file names, etc.)");

        Object[] message = {
            "Lesson Title:", titleField,
            "Lesson Content:", contentScroll,
            "Resources (one per line):", resourcesScroll
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Lesson",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String resourcesText = resourcesArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and content cannot be empty!");
                return;
            }

            lesson.setTitle(title);
            lesson.setContent(content);

            lesson.getResources().clear();
            if (!resourcesText.isEmpty()) {
                String[] resourcesArray = resourcesText.split("\\r?\\n");
                for (String resource : resourcesArray) {
                    if (!resource.trim().isEmpty()) {
                        lesson.addResource(resource.trim());
                    }
                }
            }

            boolean success = dbManager.saveCourse(lessonCourse);
            if (success) {
                loadAllLessons();
                JOptionPane.showMessageDialog(this, "Lesson updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update lesson.");
            }
        }
    }
    private void deleteLesson() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete.");
            return;
        }

        String lessonId = (String) lessonsTableModel.getValueAt(selectedRow, 0);
        Lesson lesson = findLessonById(lessonId);
        if (lesson == null) {
            JOptionPane.showMessageDialog(this, "Lesson not found!");
            return;
        }

        Course lessonCourse = findCourseByLesson(lessonId);
        if (lessonCourse == null) {
            JOptionPane.showMessageDialog(this, "Could not find the course for this lesson.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the lesson: " + lesson.getTitle() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = instructorService.deleteLesson(lessonCourse.getCourseId(), lessonId);
            if (success) {
                loadAllLessons();
                loadInstructorCourses(); // Refresh course lesson count
                JOptionPane.showMessageDialog(this, "Lesson deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete lesson.");
            }
        }
    }

    private List<Course> getInstructorCourses() {
        List<Course> instructorCourses = new ArrayList<>();
        Collection<Course> allCourses = dbManager.getAllCourses();
        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructorId)) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }

    private Lesson findLessonById(String lessonId) {
        Collection<Course> allCourses = dbManager.getAllCourses();
        for (Course course : allCourses) {
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getLessonId().equals(lessonId)) {
                    return lesson;
                }
            }
        }
        return null;
    }

    private Course findCourseByLesson(String lessonId) {
        Collection<Course> allCourses = dbManager.getAllCourses();
        for (Course course : allCourses) {
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getLessonId().equals(lessonId)) {
                    return course;
                }
            }
        }
        return null;
    }

    private List<Student> getAllStudents() {
        List<Student> allStudents = new ArrayList<>();
        for (User user : dbManager.userDatabase.values()) {
            if (user instanceof Student) {
                allStudents.add((Student) user);
            }
        }
        return allStudents;
    }

    private Student findStudentById(List<Student> students, String studentId) {
        for (Student student : students) {
            if (student.getUserId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame(new UserService(dbManager)).setVisible(true);
            dispose();
        }
    }
    private JPanel createAnalyticsPanel() {
        JPanel analyticsPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel courseLabel = new JLabel("Select Course for Analysis:");
        JComboBox<Course> courseSelector = new JComboBox<>();
        JButton viewChartsButton = new JButton("View Performance Charts 📊"); 
        
        selectionPanel.add(courseLabel);
        selectionPanel.add(courseSelector);
        selectionPanel.add(viewChartsButton);
        
        JPanel chartPlaceholder = new JPanel(new GridBagLayout());
        chartPlaceholder.add(new JLabel("Click 'View Performance Charts' to open the visual analytics window."), new GridBagConstraints());
        
        analyticsPanel.add(selectionPanel, BorderLayout.NORTH);
        analyticsPanel.add(chartPlaceholder, BorderLayout.CENTER); 
        
        List<Course> instructorCourses = getInstructorCourses(); 
        for (Course course : instructorCourses) {
            courseSelector.addItem(course);
        }
        
        viewChartsButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseSelector.getSelectedItem();
            if (selectedCourse != null) {
                openChartFrame(selectedCourse.getCourseId(), selectedCourse.getTitle()); 
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (!instructorCourses.isEmpty()) {
             courseSelector.setSelectedIndex(0);
        }

        return analyticsPanel;
    }


    private void openChartFrame(String courseId, String courseTitle) {
        try {
            // Collect required data
            double avgScore = instructorService.calculateCourseAverageScore(courseId);
            double avgCompletion = instructorService.calculateCourseAverageCompletion(courseId);
            Map<String, Double> difficultyMap = instructorService.getQuestionDifficulty(courseId);
            Map<String, Double> studentPerformance = instructorService.getStudentPerformanceInCourse(courseId);

            // Launch the chart frame
            ChartFrame chartFrame = new ChartFrame(courseTitle, avgScore, avgCompletion, difficultyMap, studentPerformance);
            chartFrame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error preparing chart data: " + ex.getMessage(), "Analysis Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}

    
