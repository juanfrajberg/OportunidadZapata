package com.juanfrajberg.oportunidadzapata;

//Clase para guardar la información de manera más fácil al subirla a la DB

public class Worker {
    private String fullname;
    private long phone;
    private String time;
    private String email;
    private String job;
    private String student;
    private String course;
    private String division;
    private int id;
    private String description;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Worker(String fullname, long phone, String time, String email, String job, String student, String course, String division, String description, int id) {
        this.fullname = fullname;
        this.phone = phone;
        this.time = time;
        this.email = email;
        this.job = job;
        this.student = student;
        this.course = course;
        this.division = division;
        this.id = id;
        this.description = description;
    }
}
