package Services;

import BackEnd.Certificate;
import databse.JsonDatabaseManager;
import BackEnd.Lesson;
import BackEnd.User;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CertificateService {

    private final JsonDatabaseManager dbManager;

    public CertificateService(JsonDatabaseManager db) {
        this.dbManager = db;
    }

    public boolean isCourseCompleted(String studentId, String courseId) {

        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        CourseService cs = new CourseService(dbManager);
        lessons = (ArrayList<Lesson>) cs.getCourseLessons(courseId);
        QuizService qs = new QuizService(dbManager);
        for (Lesson l : lessons) {

        if (l.getQuiz() == null) {
            System.out.println( l.getLessonId() + " has no quiz.");
            return false;
        }

        String quizId = l.getQuiz().getQuizID();

        Double bestScore = qs.getBestScore(studentId, quizId);
        
        if (bestScore == null || bestScore <= 50) {
            return false;
        }
    }
        return true;
    }

    public Certificate GenerateCertificate(String studentId, String courseId) {
        if (!isCourseCompleted(studentId, courseId)) {
            return null;
        } else {
            Certificate cert = new Certificate(studentId, courseId);
            StoreCertificate(cert);
            return cert;

        }
    }

    public ObjectNode ConvertCertificateToJSON(Certificate cert) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode certificateJson = objectMapper.createObjectNode();

        certificateJson.put("certificateId", cert.getCertificateID());
        certificateJson.put("studentId", cert.getStudentID());
        certificateJson.put("courseId", cert.getCourseID());
        certificateJson.put("issueDate", cert.getIssueDate().toString());

        return certificateJson;

    }

    public void StoreCertificate(Certificate cert) {
        String studentId = cert.getStudentID();
        ObjectMapper objectMapper = new ObjectMapper();
        
        ObjectMapper Mapper = new ObjectMapper();
        User user = dbManager.getUserById(studentId);
        ObjectNode userData = Mapper.valueToTree(user);
        if (userData == null) {
            return;
        }
        ObjectNode certificateJson = ConvertCertificateToJSON(cert);
        if (!userData.has("certificates")) {
            userData.set("certificates", objectMapper.createArrayNode());
        }

        var certificatesArray = userData.withArray("certificates");
        certificatesArray.add(certificateJson);

        dbManager.saveUsers();

    }
    
}
