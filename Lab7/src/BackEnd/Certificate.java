
package BackEnd;

import java.time.LocalDate;
import java.util.UUID;


public class Certificate {
    
    private final String certificateID;
    private String studentID;
    private String courseID;
    private LocalDate issueDate;
    
    public Certificate(String sID , String cID)
    {
        this.certificateID="SKILL FORGE CERT-"+UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.courseID= cID;
        this.studentID= sID;
        this.issueDate= LocalDate.now();    
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getCertificateID() {
        return certificateID;
    }
  
    
}
