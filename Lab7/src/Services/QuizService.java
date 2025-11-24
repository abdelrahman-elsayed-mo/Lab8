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
        System.out.println("Attempt is null");
        return false;
    }
    
    Quiz Q = dbManager.getQuizById(attempt.getQuizId());
    if (Q == null) {
        System.out.println("Quiz not found: " + attempt.getQuizId());
        return false;
    }

    // Check attempts before submission
    if (!canSubmit(attempt.getStudentId(), attempt.getQuizId())) {
        System.out.println("Cannot submit - no attempts remaining");
        return false;
    }

    try {
        double score = Q.evaluate(attempt);
        System.out.println("Quiz evaluated. Score: " + score);

        // Create new attempt with calculated score
        QuizAttempt savedAttempt = new QuizAttempt(
            attempt.getAttemptId(), 
            attempt.getStudentId(), 
            attempt.getQuizId(), 
            score, 
            new ArrayList<>(attempt.getAnswers())
        );

        boolean saved = dbManager.saveQuizAttempt(savedAttempt);
        System.out.println("Attempt saved: " + saved);
        return saved;
    } catch (Exception e) {
        System.out.println("Error in submit: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
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
         if (studentId == null || quizId == null) {
        return 0;
    }
    
    List<QuizAttempt> attempts = getAttemptsOfStudentInQuiz(studentId, quizId);
    int attemptCount = attempts.size();
    
    Quiz quiz = dbManager.getQuizById(quizId);
    if (quiz == null) {
        return 0;
    }
    
    int remaining = quiz.getMaxAttempts() - attemptCount;
    return Math.max(0, remaining);
    }

    public List<QuizAttempt> getAttemptsOfStudentInQuiz(String studentId, String quizId) {
        if (studentId == null || quizId == null) {
            return new ArrayList<>();
        }
        return dbManager.getAttemptsOfStudentAndQuiz(studentId, quizId);
    }

    public Double getBestScore(String studentId, String quizId) {
        List<QuizAttempt> QA = getAttemptsOfStudentInQuiz(studentId, quizId);
        if (QA.isEmpty()) {
            return null;
        }
        double max = 0;
        for (QuizAttempt Q : QA) {
            if (Q.getScore() > max) {
                max = Q.getScore();
            }
        }
        return max;
    }
}
