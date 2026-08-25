package com.example.seed.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "member")
public class Member {

    @Id
    private Integer id;

    @Column(name = "login_id", nullable = false, length = 45)
    private String loginId;

    @Column(name = "pw", nullable = false, length = 45)
    private String pw;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "role", length = 45)
    private String role;

    @Column(name = "avatar_url", length = 45)
    private String avatarUrl;

    @Column(name = "status", length = 45)
    private String status;

    @Column(name = "class", length = 45)
    private String className;

    @Column(name = "job", length = 45)
    private String job;

    @Column(name = "salary")
    private Integer salary;

    @Column(name = "cs_attendance", precision = 5, scale = 4)
    private BigDecimal csAttendance;

    @Column(name = "tax1_name", length = 45)
    private String tax1Name;

    @Column(name = "tax1_amount")
    private Integer tax1Amount;

    @Column(name = "tax2_name", length = 45)
    private String tax2Name;

    @Column(name = "tax2_amount")
    private Integer tax2Amount;

    @Column(name = "tax3_name", length = 45)
    private String tax3Name;

    @Column(name = "tax3_amount")
    private Integer tax3Amount;

    @Column(name = "tax4_name", length = 45)
    private String tax4Name;

    @Column(name = "tax4_amount")
    private Integer tax4Amount;

    @Column(name = "tax5_name", length = 45)
    private String tax5Name;

    @Column(name = "tax5_amount")
    private Integer tax5Amount;

    protected Member() {
    }

    public Integer getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPw() {
        return pw;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getClassName() {
        return className;
    }

    public String getJob() {
        return job;
    }

    public Integer getSalary() {
        return salary;
    }

    public BigDecimal getCsAttendance() {
        return csAttendance;
    }

    public String getTax1Name() {
        return tax1Name;
    }

    public Integer getTax1Amount() {
        return tax1Amount;
    }

    public String getTax2Name() {
        return tax2Name;
    }

    public Integer getTax2Amount() {
        return tax2Amount;
    }

    public String getTax3Name() {
        return tax3Name;
    }

    public Integer getTax3Amount() {
        return tax3Amount;
    }

    public String getTax4Name() {
        return tax4Name;
    }

    public Integer getTax4Amount() {
        return tax4Amount;
    }

    public String getTax5Name() {
        return tax5Name;
    }

    public Integer getTax5Amount() {
        return tax5Amount;
    }
}