/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import Quiz.QuizAttempt;
import databse.JsonDatabaseManager;


public class QuizService {
     private final JsonDatabaseManager dbManager;

    public QuizService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
     
    public boolean submit(QuizAttempt attempt){
     
        
        return true;   
    }
}
