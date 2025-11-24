    package Services;

import BackEnd.*;
import BackEnd.Quiz;
import Quiz.Question;
import Quiz.QuizAttempt;
import Quiz.StudentAnswer;
import Utils.IdGenerator;
import Utils.InputValidator;
import databse.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class InstructorService {

    private JsonDatabaseManager dbManager;
    private Instructor currentInstructor;
    private CourseService courseService; 
    private QuizService quizService; 
    private UserService userService;
    


     public InstructorService(JsonDatabaseManager dbManager, User currentUser, CourseService courseService, QuizService quizService, UserService userService) {
        this.dbManager = dbManager;
        this.courseService = courseService;
        this.quizService = quizService;
        this.userService = userService;
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
        Course course = (Course) dbManager.getCourseById(courseId);
        String instructorId = this.currentInstructor.getUserId();
        if (course == null) {
            return false;
        }

        if (!course.getInstructorId().equals(instructorId)) {
            return false;
        }
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            return false;
        }

        String quizId = new IdGenerator().generateQuizId();
        Quiz Q = new Quiz(quizId, quizTitle, (ArrayList<Question>) questions);

        lesson.setQuiz(Q);

        dbManager.saveCourse(course);
        dbManager.saveQuiz(Q);

        return true;
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
    public double calculateCourseAverageScore(String courseId) {
    
    List<QuizAttempt> allAttempts = quizService.getQuizAttemptsForCourse(courseId);
    
    if (allAttempts == null || allAttempts.isEmpty()) {
        return 0.0;
    }

    double totalScoreSum = allAttempts.stream()
            .mapToDouble(QuizAttempt::getScore) 
            .sum();

    return totalScoreSum / allAttempts.size();
}
    public Map<String, Double> getQuestionDifficulty(String courseId) {
    List<StudentAnswer> allAnswers = quizService.getAllStudentAnswersForCourse(courseId);
    
    if (allAnswers == null || allAnswers.isEmpty()) {
        return new HashMap<>();
    }

    Map<String, List<StudentAnswer>> answersByQuestion = allAnswers.stream()
            .collect(Collectors.groupingBy(StudentAnswer::getQuestionId));

    Map<String, Double> difficultyMap = new HashMap<>();
    for (Map.Entry<String, List<StudentAnswer>> entry : answersByQuestion.entrySet()) {
        String questionId = entry.getKey();
        List<StudentAnswer> answers = entry.getValue();

        long correctCount = answers.stream()
                .filter(StudentAnswer::isCorrect)
                .count();
        
        double difficultyPercentage = (double) correctCount / answers.size();
        difficultyMap.put(questionId, difficultyPercentage * 100);
    }
    return difficultyMap;
}
    public Map<String, Double> getStudentPerformanceInCourse(String courseId) {
    List<Student> students = viewEnrolledStudents(courseId);
    Map<String, Double> performanceMap = new HashMap<>();

    for (Student student : students) {
        double avgScore = quizService.calculateStudentAverageScoreInCourse(student.getUserId(), courseId);
        performanceMap.put(student.getUsername(), avgScore);
    }
    return performanceMap;
}
    public double calculateCourseAverageCompletion(String courseId) {
    Course course = dbManager.getCourseById(courseId);
    if (course == null || course.getStudents().isEmpty()) {
        return 0.0;
    }

    int totalLessonsInCourse = course.getLessons().size();
    if (totalLessonsInCourse == 0) {
        return 0.0;
    }

    double totalCompletionPercentage = 0.0;
    int studentCount = 0; 
    for (String studentId : course.getStudents()) {
        User user = dbManager.getUserById(studentId);
        
        if (user instanceof Student) {
            Student student = (Student) user;
            studentCount++; 
            
            List<String> completedLessons = student.getCompletedLessons(courseId); 
            int completedCount = completedLessons.size();
            
            double studentCompletion = ((double) completedCount / totalLessonsInCourse) * 100;
            totalCompletionPercentage += studentCompletion;
        }
    }

    return (studentCount > 0) ? (totalCompletionPercentage / studentCount) : 0.0;
}


}

    
