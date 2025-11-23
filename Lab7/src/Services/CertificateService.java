package Services;

import databse.JsonDatabaseManager;
import BackEnd.Lesson;
import java.util.ArrayList;


public class CertificateService {
    
    
    private final JsonDatabaseManager dbManager;
 
    public CertificateService (JsonDatabaseManager db)
    {
        this.dbManager=db;
    }
    
    public boolean isCourseCompleted(String studentId , String courseId)
    {
       
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        CourseService cs = new CourseService (dbManager); 
        lessons=(ArrayList<Lesson>) cs.getCourseLessons(courseId);
        QuizService qs = new QuizService (dbManager);
        for (Lesson l : lessons)
        {
            if(qs.getBestScore(studentId, l.getQuiz().getQuizID())==null ||qs.getBestScore(studentId, l.getQuiz().getQuizID())<=50)
                return  false ;
            else 
                continue ;
            
                
        }
        return true ;
    }
    
    
    
}


