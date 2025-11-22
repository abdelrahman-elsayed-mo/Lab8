/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Quiz;

import java.util.ArrayList;


public class Question {
   private String QuestionId;
    String content;
    String correctAnswer;
    ArrayList<String> options;

    public Question(String QuestionId, String content, String correctAnswer, ArrayList<String> options) {
        this.QuestionId = QuestionId;
        this.content = content;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }
    
   public boolean checkAnswer(String answer){
        return correctAnswer.equals(answer);
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public String getContent() {
        return content;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
    
}

