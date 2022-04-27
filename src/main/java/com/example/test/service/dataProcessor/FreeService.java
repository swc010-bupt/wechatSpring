package com.example.test.service.dataProcessor;


import com.example.test.bean.User;

public interface FreeService {
    String free(User user);

    /**
     * 过线率
     * @param type A/B
     * @return
     */
    double guoXianLv(User user,String type);

    double fuShiLv(User user);

    double shangAnLv(User user);

    /**
     * 调剂上岸率
     * @param type A/B
     * @return
     */
    double tiaoJI(User user,String type);

    /**
     * 根据用户信息 在 平均分  表中查找xp
     * @param user
     * @return
     */
    double getXP(User user);
}
