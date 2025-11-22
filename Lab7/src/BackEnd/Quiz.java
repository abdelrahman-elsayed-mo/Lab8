/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;

import Quiz.Question;
import Quiz.QuizAttempt;
import Quiz.StudentAnswer;
import java.util.ArrayList;


public class Quiz { 
   private String quizID;
   private String name;
   private ArrayList<Question> questions;
   private int maxAttempts = 3;

    public Quiz(String quizID, String name, ArrayList<Question> questions) {
        this.quizID = quizID;
        this.name = name;
        this.questions = questions;
    }

    public String getQuizID() {
        return quizID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    
    
    public double evaluate(QuizAttempt attempt){
      int score =0;
      
      for(StudentAnswer answer :attempt.getAnswers()){
          if(answer.isCorrect()){
              score++;
          }
      }
      double S = (score*100)/questions.size();
      return S;
    }
        
}
