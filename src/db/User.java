public class User {
    private int user_id;
    private String user_name;
    private String email;
    private String password;
    private String role;

    public User(int user_id, String user_name, String email, String password, String role) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String user_name, String email, String password, String role) {
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /// setter and getter for every parameter
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_name() {
        return user_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }

}
