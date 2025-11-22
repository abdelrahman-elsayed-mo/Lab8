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
import databse.JsonDatabaseManager;
import java.util.ArrayList;
import java.util.List;


public class AdminService {

    private JsonDatabaseManager dbManager;

    public AdminService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    
    public List<Course> getPendingCourses() {
        List<Course> pending = new ArrayList<>();
        for (Course course : dbManager.getAllCourses()) {
            if (course.getStatus() == Course.CourseStatus.PENDING) {
                pending.add(course);
            }
        }
        return pending;
    }

   
    public boolean approveCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course != null && course.getStatus() == Course.CourseStatus.PENDING) {
            course.setStatus(Course.CourseStatus.APPROVED);
            dbManager.saveCourse(course); 
            return true;
        }
        return false;
    }

    
    public boolean rejectCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course != null && course.getStatus() == Course.CourseStatus.PENDING) {
            course.setStatus(Course.CourseStatus.REJECTED);
            dbManager.saveCourse(course);
            return true;
        }
        return false;
    }
}