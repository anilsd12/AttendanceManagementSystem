package attendance.model;

public class Subject {
    private int    id;
    private String subjectCode;
    private String subjectName;
    private String department;
    private int    teacherId;

    public Subject() {}

    public Subject(int id, String subjectCode, String subjectName, String department) {
        this.id          = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.department  = department;
    }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }

    public String getSubjectCode()              { return subjectCode; }
    public void   setSubjectCode(String c)      { this.subjectCode = c; }

    public String getSubjectName()              { return subjectName; }
    public void   setSubjectName(String n)      { this.subjectName = n; }

    public String getDepartment()               { return department; }
    public void   setDepartment(String d)       { this.department = d; }

    public int    getTeacherId()                { return teacherId; }
    public void   setTeacherId(int tid)         { this.teacherId = tid; }

    @Override public String toString()          { return subjectCode + " - " + subjectName; }
}
