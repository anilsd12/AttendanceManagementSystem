package attendance.model;

public class User {
    private int    id;
    private String username;
    private String password;
    private String fullName;
    private String role;   // ADMIN | TEACHER | STUDENT

    public User() {}

    public User(int id, String username, String fullName, String role) {
        this.id       = id;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
    }

    // ── Getters & setters ───────────────────────────────────
    public int    getId()       { return id; }
    public void   setId(int id) { this.id = id; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    public String getFullName()              { return fullName; }
    public void   setFullName(String n)      { this.fullName = n; }

    public String getRole()                  { return role; }
    public void   setRole(String r)          { this.role = r; }

    @Override public String toString()       { return fullName + " (" + role + ")"; }
}
