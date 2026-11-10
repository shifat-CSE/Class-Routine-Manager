package com.example.class_routine_manager;

public class ClassRoutine {
    private int id;
    private String subject, code, teacher, room, day, startTime, endTime;

    public ClassRoutine() {}

    public ClassRoutine(int id, String subject, String code, String teacher, String room, String day, String startTime, String endTime) {
        this.id = id;
        this.subject = subject;
        this.code = code;
        this.teacher = teacher;
        this.room = room;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubject() { return subject; }
    public String getCode() { return code; }
    public String getTeacher() { return teacher; }
    public String getRoom() { return room; }
    public String getDay() { return day; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}