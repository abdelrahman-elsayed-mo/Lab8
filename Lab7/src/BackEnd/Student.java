/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    private List<String> enrolledCourses;
    private Map<String, List<String>> progress;

    public Student(String userId, String username, String email, String passwordHash) {
        super(userId, "Student", username, email, passwordHash);
        this.enrolledCourses = new ArrayList<>();
        this.progress = new HashMap<>();
    }

  

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void enrollInCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            progress.putIfAbsent(courseId, new ArrayList<>());
        }
    }

    public Map<String, List<String>> getProgress() {
        return progress;
    }

    public List<String> getCompletedLessons(String courseId) {
        return progress.getOrDefault(courseId, new ArrayList<>());
    }

    public boolean markLessonAsCompleted(String courseId, String lessonId) {
        if (!enrolledCourses.contains(courseId)) {
            return false;
        }
        
        List<String> completed = progress.get(courseId);
        if (!completed.contains(lessonId)) {
            completed.add(lessonId);
            return true;
        }
        return false;
    }
    
    public double getCourseProgressPercentage(Course course) {
        if (course == null || !enrolledCourses.contains(course.getCourseId())) {
            return 0.0;
        }
        
        int totalLessons = course.getLessons().size();
        if (totalLessons == 0) {
            return 0.0;
        }
        
        int completedLessons = getCompletedLessons(course.getCourseId()).size();
        return ((double) completedLessons / totalLessons) * 100.0;
    }
}
