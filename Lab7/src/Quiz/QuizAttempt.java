/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Quiz;

import BackEnd.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAttempt {
    
    private String attemptId;
    private String studentId;
    private String quizId;
    private double score;
    ArrayList<StudentAnswer> answers;

    public QuizAttempt(String attemptId, String studentId, String quizId, double score, ArrayList<StudentAnswer> answers) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.score = score;
        this.answers = answers;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public double getScore() {
        return score;
    }

    public ArrayList<StudentAnswer> getAnswers() {
        return answers;
    }
    
    public void addAnswer(StudentAnswer answer){
        answers.add(answer);
    }
    
    //submit method
    
    public double submitQuiz(Quiz quiz){
        this.score= quiz.evaluate(this);
        return score;
    }

    
}

