package com.example.test.service;

import com.example.test.bean.Form;
import com.example.test.bean.testInfo;
import com.example.test.mapper.FormMapper;
import com.example.test.mapper.testInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class formService {
    @Autowired
    private FormMapper formMapper;

    @Autowired
    private testInfoMapper testInfoMapper;

    public int insert(Form form){
        List<testInfo> testInfo = testInfoMapper.selectBySchoolAndPCode(form.getvSchool(),form.getpCode());

        if (testInfo == null){
            System.out.println("查询失败！！");
            return 0;
        }
        System.out.println("成功插入一条数据");
        return formMapper.insert(form);
    }

    public static void main(String[] args) {
        formService formService = new formService();
        formService.insert(null);
    }
}
