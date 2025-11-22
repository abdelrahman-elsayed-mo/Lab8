package FrontEnd;

import Services.*;
import BackEnd.*;
import databse.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupFrame extends JFrame {
    private UserService userService;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSignup;
    private JButton btnLogin;
    private JLabel lblError;

    public SignupFrame(UserService userService) {
        this.userService = userService;
        setTitle("Create Account");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Sign Up", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
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
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"Student", "Instructor"});
        panel.add(cmbRole, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnSignup = new JButton("Create Account");
        panel.add(btnSignup, gbc);

        gbc.gridy++;
        btnLogin = new JButton("Back to Login");
        panel.add(btnLogin, gbc);

        gbc.gridy++;
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        panel.add(lblError, gbc);

        add(panel, BorderLayout.CENTER);

        btnSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptSignup();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame(userService).setVisible(true);
                dispose();
            }
        });
    }

    private void attemptSignup() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        lblError.setText("");

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblError.setText("All fields are required.");
            return;
        }

       
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            lblError.setText("Invalid email format.");
            return;
        }

        if (password.length() < 6) {
            lblError.setText("Password must be at least 6 characters.");
            return;
        }

        boolean success = userService.signup(username, email, password, role);

        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Account created successfully!\nPlease login with your credentials.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame(userService).setVisible(true);
            dispose();
        } else {
            lblError.setText("Signup failed. Email or username may already exist.");
        }
    }
}