/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;


import BackEnd.Course;
import BackEnd.Lesson;
import BackEnd.Student;
import BackEnd.User;
import databse.JsonDatabaseManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StudentService {

    private JsonDatabaseManager dbManager;
    private Student currentStudent;

    public StudentService(JsonDatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        if (currentUser instanceof Student student) {
            this.currentStudent = student;
        } else {
            throw new IllegalArgumentException("User must be a Student");
        }
    }

public List<Course> browseAvailableCourses() {
    
  List<Course> approvedCourses = new ArrayList<>(); //lab 8
        for (Course course : dbManager.getAllCourses()) {
            if (course.getStatus() == Course.CourseStatus.APPROVED) {
                approvedCourses.add(course);
            }
        }
        return approvedCourses;}
  

public boolean enrollInCourse(String courseId) {
    
    if (currentStudent.getEnrolledCourses().contains(courseId)) {
        return false; 
    }
    
    Course course = dbManager.getCourseById(courseId);
    if (course == null) {
        return false;
    }
    if (course == null || course.getStatus() != Course.CourseStatus.APPROVED) {
            return false;
        } //lab 8
    
    currentStudent.enrollInCourse(courseId);
    course.addStudent(currentStudent.getUserId());
    
    dbManager.saveUser(currentStudent);
    dbManager.saveCourse(course);
    return true;
}

    public List<Course> viewEnrolledCourses() {
        List<Course> courses = new ArrayList<>();
        for (String courseId : currentStudent.getEnrolledCourses()) {
            Course course = (Course) dbManager.getCourseById(courseId);
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Lesson> accessLessons(String courseId) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course != null && currentStudent.getEnrolledCourses().contains(courseId)) {
            return course.getLessons();
        }
        return new ArrayList<>();
    }

    public boolean markLessonAsCompleted(String courseId, String lessonId) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null || course.getLessonById(lessonId) == null) {
            return false;
        }
        
        boolean updated = currentStudent.markLessonAsCompleted(courseId, lessonId);
        if (updated) {
            dbManager.saveUser(currentStudent);
        }
        return updated;
    }
    
    public double getCourseProgress(String courseId) {
        Course course = (Course) dbManager.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }
        return currentStudent.getCourseProgressPercentage(course);
    }
        public List<String> getCompletedLessons(String courseId) {
        return currentStudent.getCompletedLessons(courseId);
    }
}