/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;

import Utils.IdGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<Lesson> lessons;

    private List<String> students;

    public Course(String title, String description, String instructorId) {
        this.courseId = new IdGenerator().generateCourseId();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
       
        this.students = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public Lesson getLessonById(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    public boolean removeLessonById(String lessonId) {
        Lesson lessonToRemove = getLessonById(lessonId);
        if (lessonToRemove != null) {
            return this.lessons.remove(lessonToRemove);
        }
        return false;
    }

    public List<String> getStudents() {
        return students;
    }

    public void addStudent(String studentId) {
        if (!this.students.contains(studentId)) {
            this.students.add(studentId);
        }
    }
    
    public boolean removeStudent(String studentId) {
    return this.students.remove(studentId);
}
}
    

