package com.example.seed.dto;

public class ClassroomStudentEconomyResponse {

    private final Integer studentId;
    private final String name;
    private final String jobName;
    private final Integer balance;
    private final String creditGrade;

    public ClassroomStudentEconomyResponse(
            Integer studentId,
            String name,
            String jobName,
            Integer balance,
            String creditGrade
    ) {
        this.studentId = studentId;
        this.name = name;
        this.jobName = jobName;
        this.balance = balance;
        this.creditGrade = creditGrade;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getJobName() {
        return jobName;
    }

    public Integer getBalance() {
        return balance;
    }

    public String getCreditGrade() {
        return creditGrade;
    }
}