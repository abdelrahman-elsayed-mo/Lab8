/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.util.*;

public class IdGenerator {

    private static final Random randomId = new Random();

    private static String generateId(String prefix) {
        int number = 10000 + randomId.nextInt(99999);

        return prefix + number;
    }

    public static String generateUserId() {
        return generateId("U");
    }

    public static String generateCourseId() {
        return generateId("C");
    }

    public static String generateLessonId() {
        return generateId("L");
    }

    public static String generateQuizId() {
        return generateId("Q");

    }

    public static String generateAttemptId() {
        return generateId("A");
    }

    public static String generateQuestionId() {
        return generateId("qu");
    }
}
