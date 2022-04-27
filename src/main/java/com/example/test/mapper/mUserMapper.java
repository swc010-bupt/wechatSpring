package com.example.test.mapper;

import com.example.test.bean.mUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface mUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    int insert(mUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    int insertSelective(mUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    mUser selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(mUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table m_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(mUser record);

    mUser selectmUserByIdAndPSWD(String userId, String password);

    mUser selectmUserById(String userId);
}