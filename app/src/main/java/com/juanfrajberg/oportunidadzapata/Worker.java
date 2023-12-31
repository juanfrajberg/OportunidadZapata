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
    private String descriptionShort;
    private String descriptionFormal;

    public boolean isShowStudent() {
        return showStudent;
    }

    public void setShowStudent(boolean showStudent) {
        this.showStudent = showStudent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private boolean showStudent;
    private String category;
    private String socialMedia;
    private String username;

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

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionFormal() {
        return descriptionFormal;
    }

    public void setDescriptionFormal(String descriptionFormal) {
        this.descriptionFormal = descriptionFormal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Worker(String fullname, long phone, String time, String email, String job, String student, String course, String division, String descriptionShort, String descriptionFormal, boolean showStudent, String category, String socialMedia, String username, int id) {
        this.fullname = fullname;
        this.phone = phone;
        this.time = time;
        this.email = email;
        this.job = job;
        this.student = student;
        this.course = course;
        this.division = division;
        this.descriptionShort = descriptionShort;
        this.descriptionFormal = descriptionFormal;
        this.showStudent = showStudent;
        this.category = category;
        this.socialMedia = socialMedia;
        this.username = username;
        this.id = id;
    }
}
