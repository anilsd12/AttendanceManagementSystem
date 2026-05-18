package attendance.model;

public class Student {
    private int    id;
    private String studentId;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private int    userId;

    public Student() {}

    public Student(int id, String studentId, String fullName,
                   String email, String phone, String department) {
        this.id         = id;
        this.studentId  = studentId;
        this.fullName   = fullName;
        this.email      = email;
        this.phone      = phone;
        this.department = department;
    }

    public int    getId()                     { return id; }
    public void   setId(int id)               { this.id = id; }

    public String getStudentId()              { return studentId; }
    public void   setStudentId(String sid)    { this.studentId = sid; }

    public String getFullName()               { return fullName; }
    public void   setFullName(String n)       { this.fullName = n; }

    public String getEmail()                  { return email; }
    public void   setEmail(String e)          { this.email = e; }

    public String getPhone()                  { return phone; }
    public void   setPhone(String p)          { this.phone = p; }

    public String getDepartment()             { return department; }
    public void   setDepartment(String d)     { this.department = d; }

    public int    getUserId()                 { return userId; }
    public void   setUserId(int uid)          { this.userId = uid; }

    @Override public String toString()        { return studentId + " - " + fullName; }
}
