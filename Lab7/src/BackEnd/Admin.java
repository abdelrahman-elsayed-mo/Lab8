/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;

/**
 *
 * @author YOUSSEF FATHY
 */
public class Admin extends User {

    public Admin(String userId, String username, String email, String passwordHash) {
        super(userId, "Admin", username, email, passwordHash);
    }
        public Admin() {
        super("", "Admin", "", "", "");
        
    }
}