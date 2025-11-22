/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FrontEnd;

/**
 *
 * @author YOUSSEF FATHY
 */
import BackEnd.Admin;
import BackEnd.Course;
import Services.AdminService;
import Services.UserService;
import databse.JsonDatabaseManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AdminDashboard extends JFrame {

    private Admin admin;
    private AdminService service;
    private JList<String> courseList;
    private DefaultListModel<String> listModel;
    private List<Course> pendingCourses;

    public AdminDashboard(Admin admin, AdminService service) {
        this.admin = admin;
        this.service = service;
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        refreshList();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Welcome Admin: " + admin.getUsername()));
        add(topPanel, BorderLayout.NORTH);
        
        
        listModel = new DefaultListModel<>();
        courseList = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(courseList);
        add(scroll, BorderLayout.CENTER);
        
        
        JPanel botPanel = new JPanel();
        botPanel.setLayout(new FlowLayout());

        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnLogout = new JButton("Logout");
        
        botPanel.add(btnApprove);
        botPanel.add(btnReject);
        botPanel.add(btnLogout);
        
        
        add(botPanel, BorderLayout.SOUTH);

   
        
        btnApprove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = courseList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "Select a course first!");
                    return;
                }
                Course c = pendingCourses.get(index);
                boolean success = service.approveCourse(c.getCourseId());

                if (success) {
                    JOptionPane.showMessageDialog(null, "Course Approved!");
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(null, "Error approving course.");
                }
            }
        });

        btnReject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = courseList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "Select a course first!");
                    return;
                }

                Course c = pendingCourses.get(index);
                boolean success = service.rejectCourse(c.getCourseId());

                if (success) {
                    JOptionPane.showMessageDialog(null, "Course Rejected.");
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(null, "Error rejecting course.");
                }
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JsonDatabaseManager dbManager = new JsonDatabaseManager();
            new LoginFrame(new UserService(dbManager)).setVisible(true);
            dispose();
        }
               
            }
        });
    }

    private void refreshList() {
        listModel.clear();
        pendingCourses = service.getPendingCourses();

        if (pendingCourses.isEmpty()) {
            listModel.addElement("No pending courses found.");
        } else {
            for (Course c : pendingCourses) {
                
                String display = c.getTitle() + " (By: " + c.getInstructorId() + ")";
                listModel.addElement(display);
            }
        }
    }
}