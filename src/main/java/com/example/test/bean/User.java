package com.example.test.bean;

import lombok.Data;

/**
 * 拿到用户信息
 */
@Data
public class User {
    private String userName;        //用户姓名
    private String schoolName;      //报考学校名称
    private String majorNum;        //专业代码
    private String majorName;       //专业名称
    private Boolean fullTime;       //是否全日制
    private Boolean academicDegree; //是否学硕

    private Double politicalPoints; //政治
    private Double foreignLanguages;//外语
    private Double businessFir;     //专业课一
    private Double businessSec;     //专业课二
    private Double totalPoints;     //总分

    public User(String userName, String schoolName, String majorNum, String majorName, Boolean fullTime, Boolean academicDegree, Double politicalPoints, Double foreignLanguages, Double businessFir, Double businessSec, Double totalPoints) {
        this.userName = userName;
        this.schoolName = schoolName;
        this.majorNum = majorNum;
        this.majorName = majorName;
        this.fullTime = fullTime;
        this.academicDegree = academicDegree;
        this.politicalPoints = politicalPoints;
        this.foreignLanguages = foreignLanguages;
        this.businessFir = businessFir;
        this.businessSec = businessSec;
        this.totalPoints = totalPoints;
    }

    //查表获得
    private String schoolType;
    private String schoolLocation;
    private String majorStrength;
}
