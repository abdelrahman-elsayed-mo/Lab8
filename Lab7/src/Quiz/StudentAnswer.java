/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Quiz;


public class StudentAnswer {
    private String questionId; 
    private String studentAnswer;
    private boolean isCorrect;

    public StudentAnswer(String questionId, String studentAnswer, boolean isCorrect) {
        this.questionId = questionId;
        this.studentAnswer = studentAnswer;
        this.isCorrect = isCorrect;
    }
    
    
       public boolean isCorrect() {
        return isCorrect;
    }

    public String getQuestionId() {
       return questionId;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }
    
       
}



