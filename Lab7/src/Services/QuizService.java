/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import BackEnd.Course;
import BackEnd.Lesson;
import BackEnd.Quiz; 
import Quiz.Question; 
import Quiz.QuizAttempt;
import Quiz.StudentAnswer;
import Utils.IdGenerator; 
import databse.JsonDatabaseManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;
import Quiz.QuizAttempt;

public class QuizService {

    private final JsonDatabaseManager dbManager;

    public QuizService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean submit(QuizAttempt attempt) {
        if (attempt == null) {
            return false;
        }
        Quiz Q = dbManager.getQuizById(attempt.getQuizId());
        
        if (Q == null) {
            return false;
        }
        if (!canSubmit(attempt.getStudentId(), attempt.getQuizId())) {
            return false;
        }

        double score = Q.evaluate(attempt);

        QuizAttempt save = new QuizAttempt(attempt.getAttemptId(), attempt.getStudentId(), attempt.getQuizId(), score, attempt.getAnswers());

        return dbManager.saveQuizAttempt(save);
    }

    // this method makes sure that the attempts is less than 3
    public boolean canSubmit(String studentId, String quizId) {
         Quiz Q = dbManager.getQuizById(quizId);
        if (Q == null) {
            return false;
        }
        
        List<QuizAttempt> QA = getAttemptsOfStudentInQuiz(studentId, quizId);
        
        int count = QA.size();
        int max = Q.getMaxAttempts();

        if (count < max) {
            return true;
        } else {
            return false;
        }
    }

    public int getRemainingAttempts(String studentId, String quizId) {
        List<QuizAttempt> QA = getAttemptsOfStudentInQuiz(studentId, quizId);
        int count = QA.size();
        
         Quiz Q = dbManager.getQuizById(quizId);
         if (Q== null) {
            return 0;
        }
         int max = Q.getMaxAttempts();
        return max-count;
    }

    public List<QuizAttempt> getAttemptsOfStudentInQuiz(String studentId, String quizId) {
        if (studentId == null || quizId == null) {
            return new ArrayList<>();
        }
        return dbManager.getAttemptsOfStudentAndQuiz(studentId, quizId);
    }
    
    public Double getBestScore(String studentId,String quizId){
        List<QuizAttempt> QA = getAttemptsOfStudentInQuiz(studentId, quizId);
        if(QA.isEmpty()){
            return null;
        }
        double max=0;
        for(QuizAttempt Q:QA){
            if(Q.getScore()>max)
                max=Q.getScore();
        }
        
        return max;
    }
    public boolean createAndAssignQuiz(String courseId, String lessonId, String quizTitle, List<Question> questions) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) return false;

        Lesson lesson = dbManager.getLessonById(lessonId);
        if (lesson == null) return false;

        String quizId = new IdGenerator().generateQuizId();
        Quiz quiz = new Quiz(quizId, quizTitle, (ArrayList<Question>) questions);

        lesson.setQuiz(quiz);

        dbManager.saveCourse(course);
        dbManager.saveQuiz(quiz); // Assuming dbManager has saveQuiz
        
        return true;
    }
  public List<QuizAttempt> getQuizAttemptsForCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course == null || course.getLessons() == null) {
            return new ArrayList<>();
        }

        List<String> quizIds = course.getLessons().stream()
                .filter(lesson -> lesson.getQuizId() != null)
                .map(Lesson::getQuizId)
                .collect(Collectors.toList());

        List<QuizAttempt> courseAttempts = new ArrayList<>();
        for (String quizId : quizIds) {
            // تفترض أن dbManager لديها دالة تجلب المحاولات حسب Quiz ID
            courseAttempts.addAll(dbManager.getAttemptsByQuizId(quizId)); 
        }
        return courseAttempts;
    }
  public List<StudentAnswer> getAllStudentAnswersForCourse(String courseId) {
        List<QuizAttempt> courseAttempts = getQuizAttemptsForCourse(courseId);
        
        List<StudentAnswer> allAnswers = new ArrayList<>();
        for (QuizAttempt attempt : courseAttempts) {
            allAnswers.addAll(attempt.getAnswers()); 
        }
        return allAnswers;
    }
  

    public double calculateStudentAverageScoreInCourse(String studentId, String courseId) {
        List<QuizAttempt> allAttempts = getQuizAttemptsForCourse(courseId);
        
        List<QuizAttempt> studentAttempts = allAttempts.stream()
                .filter(attempt -> attempt.getStudentId().equals(studentId))
                .collect(Collectors.toList());

        if (studentAttempts.isEmpty()) {
            return 0.0;
        }

        double totalScoreSum = studentAttempts.stream()
                .mapToDouble(QuizAttempt::getScore)
                .sum();

        return totalScoreSum / studentAttempts.size();
    }

}


