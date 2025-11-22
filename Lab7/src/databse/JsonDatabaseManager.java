/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package databse;

import BackEnd.Course;
import BackEnd.Instructor;
import BackEnd.Lesson;
import BackEnd.Quiz;
import BackEnd.Student;
import BackEnd.User;
import Quiz.QuizAttempt;
import Utils.IdGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonDatabaseManager {

    private static final String USERS_FILE = "users.json";
    private static final String COURSES_FILE = "courses.json";
    private static final String QUIZZES_FILE = "quizzes.json";
    private static final String QUIZ_ATTEMPTS_FILE = "quiz_attempts.json";

    public Map<String, User> userDatabase;
    private Map<String, Course> courseDatabase;
    private Gson gson;

    private List<Quiz> quizdata = new ArrayList<>();
    private List<QuizAttempt> quizAttemptData = new ArrayList<>();

    public JsonDatabaseManager() {

        RuntimeTypeAdapterFactory<User> adapter = RuntimeTypeAdapterFactory
                .of(User.class, "role", true) // ADD 'true' here
                .registerSubtype(Student.class, "Student")
                .registerSubtype(Instructor.class, "Instructor");

        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .setPrettyPrinting()
                .create();

        this.userDatabase = loadUsers();
        this.courseDatabase = loadCourses();
    }

    private Map<String, User> loadUsers() {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type type = new TypeToken<HashMap<String, User>>() {
            }.getType();
            Map<String, User> users = gson.fromJson(reader, type);
            return users != null ? users : new HashMap<>();
        } catch (IOException e) {
            System.out.println("No users.json file found, starting with empty database.");
            return new HashMap<>();
        } catch (JsonParseException e) {
            System.err.println("Error parsing users.json: " + e.getMessage());
            System.err.println("Starting with empty user database.");
            return new HashMap<>();
        }
    }

    private Map<String, Course> loadCourses() {
        try (FileReader reader = new FileReader(COURSES_FILE)) {
            Type type = new TypeToken<HashMap<String, Course>>() {
            }.getType();
            Map<String, Course> courses = gson.fromJson(reader, type);
            return courses != null ? courses : new HashMap<>();
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(this.userDatabase, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCourses() {
        try (FileWriter writer = new FileWriter(COURSES_FILE)) {
            gson.toJson(this.courseDatabase, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User user) {
        try {
            userDatabase.put(user.getUserId(), user);
            saveUsers();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveCourse(Course course) {
        try {
            courseDatabase.put(course.getCourseId(), course);
            saveCourses();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCourse(String courseId) {
        Course course = courseDatabase.get(courseId);
        if (course != null) {

            User instructor = userDatabase.get(course.getInstructorId());
            if (instructor instanceof Instructor) {
                ((Instructor) instructor).getCreatedCourses().remove(courseId);
                saveUser(instructor);
            }

            for (String studentId : course.getStudents()) {
                User user = userDatabase.get(studentId);
                if (user instanceof Student) {
                    ((Student) user).getEnrolledCourses().remove(courseId);
                    ((Student) user).getProgress().remove(courseId);
                    saveUser(user);
                }
            }

            courseDatabase.remove(courseId);
            saveCourses();
        }
    }

    public User getUserByEmail(String email) {
        for (User user : userDatabase.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public User getUserById(String userId) {
        return userDatabase.get(userId);
    }

    public User getUserByUsername(String username) {
        for (User user : userDatabase.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public String generateNewUserId() {
        IdGenerator ID = new IdGenerator();
        return ID.generateUserId();
    }

    public Course getCourseById(String courseId) {
        return courseDatabase.get(courseId);
    }

    public Collection<Course> getAllCourses() {
        return courseDatabase.values();
    }

    public List<Student> getStudentsByCourseId(String courseId) {
        List<Student> enrolledStudents = new ArrayList<>();
        Course course = getCourseById(courseId);
        if (course != null) {
            for (String studentId : course.getStudents()) {
                User user = getUserById(studentId);
                if (user instanceof Student) {
                    Student student = (Student) user;
                    enrolledStudents.add(student);
                }
            }
        }
        return enrolledStudents;
    }

    public Lesson getLessonById(String lessonId) {
        for (Course course : courseDatabase.values()) {
            if (course.getLessons() != null) {
                for (BackEnd.Lesson lesson : course.getLessons()) {
                    if (lesson.getLessonId().equals(lessonId)) {
                        return lesson;
                    }
                }
            }
        }
        return null;
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        List<Course> instructorCourses = new ArrayList<>();
        for (Course course : courseDatabase.values()) {
            if (course.getInstructorId().equals(instructorId)) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }

    public List<Student> getAllStudents() {
        List<Student> allStudents = new ArrayList<>();
        for (User user : userDatabase.values()) {
            if (user instanceof Student) {
                allStudents.add((Student) user);
            }
        }
        return allStudents;
    }

    private List<Quiz> loadQuizzes() {
        try (FileReader read = new FileReader(QUIZZES_FILE)) {
            Type T = new TypeToken<List<Quiz>>() {
            }.getType();
            List<Quiz> Quizzes = gson.fromJson(read, T);
            if (Quizzes != null) {
                return Quizzes;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveQuizzes() {
        try (FileWriter write = new FileWriter(QUIZZES_FILE)) {
            gson.toJson(this.quizdata, write);
        } catch (IOException ex) {
            Logger.getLogger(JsonDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<QuizAttempt> loadQuizAttempts() {
        try (FileReader read = new FileReader(QUIZ_ATTEMPTS_FILE)) {
            Type T = new TypeToken<List<Quiz>>() {
            }.getType();
            List<QuizAttempt> QuizAttempts = gson.fromJson(read, T);
            if (QuizAttempts != null) {
                return QuizAttempts;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveQuizAttempts() {
        try (FileWriter write = new FileWriter(QUIZ_ATTEMPTS_FILE)) {
            gson.toJson(this.quizAttemptData, write);
        } catch (IOException ex) {
            Logger.getLogger(JsonDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Quiz getQuizById(String quizId) {
        for (Quiz Q : quizdata) {
            if (Q.getQuizID().equals(quizId)) {
                return Q;
            }
        }
        return null;
    }

    public List<QuizAttempt> getAttemptsOfStudentAndQuiz(String studentId, String quizId) {
        List<QuizAttempt> QuizAttempts = new ArrayList<>();
        for (QuizAttempt QA : quizAttemptData) {
            if (QA.getStudentId().equals(studentId) && QA.getQuizId().equals(quizId)) {
                QuizAttempts.add(QA);
            }
        }
        return QuizAttempts;
    }
    
    public boolean saveQuiz(Quiz quiz) {
    try {
        quizdata.add(quiz);
        saveQuizzes();
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean saveQuizAttempt(QuizAttempt attempt) {
    try {
        quizAttemptData.add(attempt);
        saveQuizAttempts();
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}




}
