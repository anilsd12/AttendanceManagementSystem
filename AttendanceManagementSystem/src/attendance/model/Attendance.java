package attendance.model;

import java.sql.Date;

public class Attendance {
    private int    id;
    private int    studentId;
    private int    subjectId;
    private Date   date;
    private String status;   // PRESENT | ABSENT | LATE
    private int    markedBy;
    private String remarks;

    // Extra display fields (joined from other tables)
    private String studentName;
    private String studentRollId;
    private String subjectName;

    public Attendance() {}

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }

    public int    getStudentId()               { return studentId; }
    public void   setStudentId(int sid)        { this.studentId = sid; }

    public int    getSubjectId()               { return subjectId; }
    public void   setSubjectId(int subjId)     { this.subjectId = subjId; }

    public Date   getDate()                    { return date; }
    public void   setDate(Date d)              { this.date = d; }

    public String getStatus()                  { return status; }
    public void   setStatus(String s)          { this.status = s; }

    public int    getMarkedBy()                { return markedBy; }
    public void   setMarkedBy(int uid)         { this.markedBy = uid; }

    public String getRemarks()                 { return remarks; }
    public void   setRemarks(String r)         { this.remarks = r; }

    public String getStudentName()             { return studentName; }
    public void   setStudentName(String n)     { this.studentName = n; }

    public String getStudentRollId()           { return studentRollId; }
    public void   setStudentRollId(String r)   { this.studentRollId = r; }

    public String getSubjectName()             { return subjectName; }
    public void   setSubjectName(String n)     { this.subjectName = n; }
}
