/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Quiz;


public class StudentAnswer {
    private Question question;
   private String studentAnswer;

    public StudentAnswer(Question question, String studentAnswer) {
        this.question = question;
        this.studentAnswer = studentAnswer;
    }
    
    
       public boolean isCorrect() {
        return question.checkAnswer(studentAnswer);
    }

    public Question getQuestion() {
        return question;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }
    
       
}
