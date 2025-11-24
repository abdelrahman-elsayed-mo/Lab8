package FrontEnd;

import Services.*;
import BackEnd.*;
import databse.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private UserService userService;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSignup;
    private JLabel lblError;

    public LoginFrame(UserService userService) {
        this.userService = userService;
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Login");
        panel.add(btnLogin, gbc);

        gbc.gridy++;
        btnSignup = new JButton("Create Account");
        panel.add(btnSignup, gbc);

        gbc.gridy++;
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        panel.add(lblError, gbc);

        add(panel, BorderLayout.CENTER);

        btnSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignupFrame(userService).setVisible(true);
                dispose();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        lblError.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return;
        }

        
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            lblError.setText("Invalid email format.");
            return;
        }

        User loggedInUser = userService.login(email, password);

        if (loggedInUser != null) {
            dispose(); 

            if (loggedInUser.getRole().equalsIgnoreCase("Student")) {
                JsonDatabaseManager db = new JsonDatabaseManager();
                new StudentDashboard(db, loggedInUser.getUserId()).setVisible(true);
            } 
            else if (loggedInUser.getRole().equalsIgnoreCase("Instructor")) {
                JsonDatabaseManager db = new JsonDatabaseManager(); // Added for consistency
                UserService userService = new UserService(db);
                QuizService quizService = new QuizService(db);
                CourseService courseService = new CourseService(db);
                InstructorService instructorService = null;
                new InstructorDashboard( db,loggedInUser.getUserId(),instructorService,courseService,quizService, userService).setVisible(true);
            }

             else if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                JsonDatabaseManager db = new JsonDatabaseManager();
                Admin admin = (Admin) loggedInUser;
                AdminService adminService = new AdminService(db);
                new AdminDashboard(admin, adminService).setVisible(true);
            }
        } else {
            lblError.setText("Invalid email or password.");
        }
    }
    
    public static void main(String args[]) {
    JsonDatabaseManager dbManager = new JsonDatabaseManager();
    UserService userService = new UserService(dbManager);

    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new LoginFrame(userService).setVisible(true);
        }
    });

    
}
}
