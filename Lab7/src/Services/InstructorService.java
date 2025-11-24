package Services;

import BackEnd.*;
import BackEnd.Quiz;
import Quiz.Question;
import Utils.IdGenerator;
import Utils.InputValidator;
import databse.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InstructorService {

    private JsonDatabaseManager dbManager;
    private Instructor currentInstructor;

    public InstructorService(JsonDatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        if (currentUser instanceof Instructor) {
            this.currentInstructor = (Instructor) currentUser;
        } else {
            throw new IllegalArgumentException("User must be an Instructor");
        }
    }

    public Course createCourse(String title, String description) {
        if (!InputValidator.isRequiredFieldValid(title) || !InputValidator.isRequiredFieldValid(description)) {
            return null;
        }

        Course course = new Course(title, description, currentInstructor.getUserId());
        boolean saved = dbManager.saveCourse(course);

        if (saved) {
            currentInstructor.getCreatedCourses().add(course.getCourseId());
            dbManager.saveUser(currentInstructor);
            return course;
        }
        return null;
    }

    public boolean editCourse(String courseId, String newTitle, String newDescription) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }

        if (InputValidator.isRequiredFieldValid(newTitle)) {
            course.setTitle(newTitle);
        }
        if (InputValidator.isRequiredFieldValid(newDescription)) {
            course.setDescription(newDescription);
        }

        dbManager.saveCourse(course);
        return true;
    }

    public boolean deleteCourse(String courseId) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }

        dbManager.deleteCourse(courseId);

        currentInstructor.getCreatedCourses().remove(courseId);
        dbManager.saveUser(currentInstructor);

        return true;
    }

    public Lesson addLesson(String courseId, String lessonTitle, String lessonContent) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return null;
        }

        if (!InputValidator.isRequiredFieldValid(lessonTitle) || !InputValidator.isRequiredFieldValid(lessonContent)) {
            return null;
        }

        Lesson lesson = new Lesson(lessonTitle, lessonContent);
        course.addLesson(lesson);
        dbManager.saveCourse(course);

        return lesson;
    }

    public boolean editLesson(String courseId, String lessonId, String newTitle, String newContent) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }

        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }

        if (InputValidator.isRequiredFieldValid(newTitle)) {
            lesson.setTitle(newTitle);
        }
        if (InputValidator.isRequiredFieldValid(newContent)) {
            lesson.setContent(newContent);
        }

        dbManager.saveCourse(course);
        return true;
    }

    public boolean deleteLesson(String courseId, String lessonId) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }

        boolean removed = course.removeLessonById(lessonId);
        if (removed) {
            dbManager.saveCourse(course);
        }
        return removed;
    }

    public List<Student> viewEnrolledStudents(String courseId) {
        Course course = (Course) dbManager.getCourseById(courseId);

        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return new ArrayList<>();
        }

        return dbManager.getStudentsByCourseId(courseId);
    }

    public List<Course> getInstructorCourses() {
        List<Course> instructorCourses = new ArrayList<>();
        Collection<Course> allCourses = dbManager.getAllCourses();
        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructor.getUserId())) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }

    public List<Lesson> getAllLessons() {
        List<Lesson> allLessons = new ArrayList<>();
        Collection<Course> allCourses = dbManager.getAllCourses();
        for (Course course : allCourses) {
            if (course.getInstructorId().equals(currentInstructor.getUserId())) {
                allLessons.addAll(course.getLessons());
            }
        }
        return allLessons;
    }

    public boolean createQuiz( String courseId, String lessonId, String quizTitle, List<Question> questions) {
         try {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            System.out.println("Course not found: " + courseId);
            return false;
        }

        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            System.out.println("Instructor doesn't own this course");
            return false;
        }

        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            System.out.println("Lesson not found: " + lessonId);
            return false;
        }

        // Check if quiz already exists
        if (lesson.getQuiz() != null) {
            System.out.println("Quiz already exists for this lesson");
            return false;
        }

        String quizId = new IdGenerator().generateQuizId();
        Quiz quiz = new Quiz(quizId, quizTitle, new ArrayList<>(questions));
        
        // Set the quiz to the lesson
        lesson.setQuiz(quiz);
        
        // Save both course and quiz
        boolean courseSaved = dbManager.saveCourse(course);
        boolean quizSaved = dbManager.saveQuiz(quiz);
        
        System.out.println("Quiz creation - Course saved: " + courseSaved + ", Quiz saved: " + quizSaved);
        
        return courseSaved && quizSaved;
    } catch (Exception e) {
        System.out.println("Error in createQuiz: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    }

    public boolean editQuiz(String courseId, String lessonId, String quizId, String Title, List<Question> Questions) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return false;
        }
        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }
        Quiz Q = lesson.getQuiz();
        if (Q == null || !Q.getQuizID().equals(quizId)) {
            return false;
        }

        if (InputValidator.isRequiredFieldValid(Title)) {
            Q.setName(Title);
        }

        if (Questions != null) {
            Q.setQuestions((ArrayList<Question>) Questions);
        }

        dbManager.saveCourse(course);
        dbManager.saveQuiz(Q);

        return true;

    }

    public boolean addQuestionToQuiz(String courseId, String lessonId, Question question) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return false;
        }
        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }
        Quiz Q = lesson.getQuiz();
        if (Q == null) {
            return false;
        }

        Q.getQuestions().add(question);

        dbManager.saveCourse(course);
        dbManager.saveQuiz(Q);

        return true;
    }

    public boolean removeQuestionFromQuiz(String courseId, String lessonId, Question question) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return false;
        }
        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }
        Quiz Q = lesson.getQuiz();
        if (Q == null) {
            return false;
        }

        boolean remove = false;
        for (Question q : Q.getQuestions()) {
            if (q.getQuestionId() == question.getQuestionId()) {
                remove = Q.getQuestions().remove(q);
            }

        }
        if (remove) {
            dbManager.saveCourse(course);
            dbManager.saveQuiz(Q);
        }
        return remove;

    }

    public boolean deleteQuiz(String courseId, String lessonId, String quizId) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return false;
        }
        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }
        Quiz Q = lesson.getQuiz();
        if (Q == null || !Q.getQuizID().equals(quizId)) {
            return false;
        }
        lesson.setQuiz(null);

        boolean remove = dbManager.deleteQuiz(quizId);

        if (remove) {
            dbManager.saveCourse(course);
            dbManager.saveQuiz(Q);
        }
        return remove;

    }

    public Quiz getQuiz(String courseId, String lessonId) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return null;
        }
        if (!course.getInstructorId().equals(currentInstructor.getUserId())) {
            return null;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return null;
        }
        Quiz Q = lesson.getQuiz();

        return Q;

    }

    public boolean updateQuestionInQuiz(String courseId, String lessonId, String questionId,
            String newContent, String newCorrectAnswer, List<String> newOptions) {
        Course course = dbManager.getCourseById(courseId);
        if (course == null || !course.getInstructorId().equals(currentInstructor.getUserId())) {
            return false;
        }
       
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null || !lesson.containsQuiz()) {
            return false;
        }        

        Quiz quiz = lesson.getQuiz();
        for (Question q : quiz.getQuestions()) {
            if (q.getQuestionId().equals(questionId)) {

                if (InputValidator.isRequiredFieldValid(newContent)) {
                    q.setContent(newContent);
                }
                if (InputValidator.isRequiredFieldValid(newCorrectAnswer)) {
                    q.setCorrectAnswer(newCorrectAnswer);
                }
                if (newOptions != null && !newOptions.isEmpty()) {
                    q.setOptions(new ArrayList<>(newOptions));
                }

                dbManager.saveCourse(course);
                dbManager.saveQuiz(quiz);
                return true;
            }
        }
        return false;
    }

}
