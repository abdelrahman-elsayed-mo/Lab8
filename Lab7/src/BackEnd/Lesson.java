/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;


import Utils.IdGenerator;
import java.util.ArrayList;
import java.util.List;


public class Lesson {

    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private Quiz quiz;

    public Lesson(String title, String content) {
        this.lessonId = new IdGenerator().generateLessonId();
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
        this.quiz=null;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getResources() {
        return resources;
    }

    public void addResource(String resource) {
        this.resources.add(resource);
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    public boolean containsQuiz(){
        if(this.quiz != null)
            return true;
        
        return false;
    }
}
