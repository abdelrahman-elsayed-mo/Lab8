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
            if (course.getStatus() == Course.CourseStatus.pending) {
                pending.add(course);
            }
        }
        return pending;
    }

    // [LAB 8 NEW] Logic to change status to APPROVED
    public boolean approveCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course != null && course.getStatus() == Course.CourseStatus.pending) {
            course.setStatus(Course.CourseStatus.aprroved);
            dbManager.saveCourse(course); 
            return true;
        }
        return false;
    }

    
    public boolean rejectCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course != null && course.getStatus() == Course.CourseStatus.pending) {
            course.setStatus(Course.CourseStatus.rejected);
            dbManager.saveCourse(course);
            return true;
        }
        return false;
    }
}