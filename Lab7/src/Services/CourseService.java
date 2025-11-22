/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

/**
 *
 * @author YOUSSEF FATHY
 */
import BackEnd.Course;
import BackEnd.Lesson;
import BackEnd.Quiz;
import databse.JsonDatabaseManager;
import java.util.ArrayList;
import java.util.List;

public class CourseService {

    private final JsonDatabaseManager dbManager;

    public CourseService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

public List<Course> browseAllCourses() {
    
    return new ArrayList<>(dbManager.getAllCourses());
}

   public Course getCourseDetails(String courseId) {
    return dbManager.getCourseById(courseId);
}


    public List<Lesson> getCourseLessons(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course != null) {
            return course.getLessons();
        }
        return new ArrayList<>();
    }
    
    public Lesson getLessonById(String lessonId){
        return dbManager.getLessonById(lessonId);
    }
    
    public boolean quizAdditionToLesson(String courseId,String lessonId,Quiz quiz){
       Course course = dbManager.getCourseById(courseId);
       if(course ==null)
           return false;
       
       Lesson lesson =course.getLessonById(lessonId);
       if(lesson ==null)
           return false;
       lesson.setQuiz(quiz);
       
       return dbManager.saveCourse(course);
    }
    
    public Quiz getQuizOfLesson(String courseId,String lessonId){
        Lesson lesson = getLessonById(lessonId);
        if(lesson ==null)
           return null;
        return lesson.getQuiz();
    }
}
