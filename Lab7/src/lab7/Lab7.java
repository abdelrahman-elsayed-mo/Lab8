/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab7;

import FrontEnd.LoginFrame;
import Services.UserService;
import databse.JsonDatabaseManager;


public class Lab7 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
           JsonDatabaseManager dbManager = new JsonDatabaseManager();
    UserService userService = new UserService(dbManager);

    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new LoginFrame(userService).setVisible(true);
        }
    });
    }
    
}
