package com.example.test.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 调剂的学校及专业
 * 用于 item 中的第一项 排序
 */
@Data
@AllArgsConstructor
public class SchoolAvail {
    //寻找依据
    private String schoolName;
    private String majorNum;
    //排序依据
    private String schoolType;
    private String majorStrength;
    private Boolean fullTime;
    private Boolean academicDegree;
    private String schoolLocation;
}
