/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;


import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {

    private List<String> createdCourses;

    public Instructor(String userId, String username, String email, String passwordHash) {
        super(userId, "Instructor", username, email, passwordHash);
        this.createdCourses = new ArrayList<>();
    }

    public List<String> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(List<String> createdCourses) {
        this.createdCourses = createdCourses;
    }
}