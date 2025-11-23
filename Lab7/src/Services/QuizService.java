/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import BackEnd.Quiz;
import Quiz.QuizAttempt;
import databse.JsonDatabaseManager;
import java.util.ArrayList;
import java.util.List;

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
}
