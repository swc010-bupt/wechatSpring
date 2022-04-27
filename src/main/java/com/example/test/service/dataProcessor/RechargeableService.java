package com.example.test.service.dataProcessor;

import com.example.test.bean.User;
import com.example.test.vo.SchoolAvail;


import java.util.List;

/**
 * 1. 拿到用户信息：报考学校名称、报考专业名称（含代码）、政治分数、外语分数、专业课一分数、专业课二分数
 * 2. 根据以下参数寻找所有可以的学校及专业：
 *      专业代码前n位是否相同
 *      是否在分数线附件（前后8分）
 *      学校类型
 *      学校位置类型
 *      专业强度
 *      是否学硕
 *      是否全日制
 * 3. 排序优先级：学校类型、专业强度、是否全日制、是否学硕、学校位置
 */
public interface RechargeableService {

    String getSchoolType(String schoolName);

    /**
     * 补充用户信息：学校类型，学校位置类型，专业强度
     */
    User addInformation(User user);

    /**
     * 扫描文件，寻找适合用户调剂的学校及专业
     * 是否调剂，不调剂是0，调剂是1
     */
    List<SchoolAvail> getSchoolAvail(User user, int adjust);

    /**
     * 调剂类型：
     * 1、本专业内调剂（6位专业代码相同）
     * 2、一级学科内调剂（前4位专业代码相同）
     * 3、大的门类内调剂（前2位专业代码相同）
     */
    List<SchoolAvail> adjustType(User user,int type,List<SchoolAvail> schoolAvails);

    String rechargeable(User user);
    String free(User user);



    /**
     * 等级评定
     * 根据用户成绩评定调剂危险等级
     * 1.特别危险
     * 2.比较危险
     * 3.正常
     */
    int typeEstimation(User user);

    /**
     * 特别危险等级：
     * 挑选该专业（六位数/四位数和两位数）被录取最低分的10所学校
     * 只能在B类高校中做选择
     */
    String typeFir(User user);
    String typeSec(User user);
    String typeThr(User user);

    /**
     *
     * @param user
     * @param schoolAvails
     * @param title
     * @param order 是否顺序遍历？type1/2为false，type3为true
     * @return
     */
    String printTemplate(User user,List<SchoolAvail> schoolAvails,String[]title,boolean order);

    /**
     * 判断是否为特殊专业
     * 1.管理类
     * 2.法律
     * 3.医学
     * 4.常规
     */
    int specialMajor(User user);

}
