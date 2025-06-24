import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterForm extends JFrame {
    private JPanel LoginRegisterPanel, Login, Register;
    private JTabbedPane tabbedPane1;
    private JPasswordField loginPasswordField, registerPasswordField;
    private JTextField loginEmailTextField, registerNameTextField, registerEmailTextField;
    private JButton loginButton, registerButton;
    private JComboBox roleBox;

    public LoginRegisterForm() {
        setTitle("Bus Koi? - Login / Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        setContentPane(LoginRegisterPanel);
        setVisible(true);

    }

    private void handleLogin() {
        String email = loginEmailTextField.getText();
        String password = String.valueOf(loginPasswordField.getPassword());

        User user = UserDB.login(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful");
            if (user.getRole().equals("admin")) {
                new AdminDashboard();
            } else if (user.getRole().equals("student")) {
                new StudentDashboard();
            }
            dispose(); // closes login window
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password");
        }
    }

    private void handleRegister() {
        String name = registerNameTextField.getText();
        String email = registerEmailTextField.getText();
        String password = String.valueOf(registerPasswordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        User newUser = new User(name, email, password, role);
        boolean success = UserDB.register(newUser);
        if (success) {
            JOptionPane.showMessageDialog(this, "Register Successful");
        } else {
            JOptionPane.showMessageDialog(this, "Register Failed");
        }
    }
}
