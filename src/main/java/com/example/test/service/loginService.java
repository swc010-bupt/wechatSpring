package com.example.test.service;

import com.example.test.bean.mUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class loginService {
    @Autowired
    private com.example.test.mapper.mUserMapper mUserMapper;

    //用户登录

    public mUser selectmUserByIdAndPSWD(String userId, String password){
        return mUserMapper.selectmUserByIdAndPSWD(userId,password);
    }

    public mUser selectmUserById(String userId){
        return mUserMapper.selectmUserById(userId);
    }

    //注册新用户
    public void insert(mUser mUser){
        mUserMapper.insert(mUser);
        System.out.println("成功插入一条信息");
    }
//    public void toPDF(String stuName,String password){
//        pdfUtils.getPdf(stuName,password);
//    }
}
