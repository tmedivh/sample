package com.model;

import javax.persistence.*;

@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 名称
     */
    private String name;

    /**
     * 身份证
     */
    @Column(name = "identity_id")
    private String identityId;

    /**
     * 真是姓名
     */
    @Column(name = "identity_name")
    private String identityName;

    /**
     * 性别 0男 1女
     */
    private Boolean sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱地址
     */
    private String mail;

    /**
     * 密码
     */
    private String password;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取手机号
     *
     * @return phone - 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取身份证
     *
     * @return identity_id - 身份证
     */
    public String getIdentityId() {
        return identityId;
    }

    /**
     * 设置身份证
     *
     * @param identityId 身份证
     */
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    /**
     * 获取真是姓名
     *
     * @return identity_name - 真是姓名
     */
    public String getIdentityName() {
        return identityName;
    }

    /**
     * 设置真是姓名
     *
     * @param identityName 真是姓名
     */
    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    /**
     * 获取性别 0男 1女
     *
     * @return sex - 性别 0男 1女
     */
    public Boolean getSex() {
        return sex;
    }

    /**
     * 设置性别 0男 1女
     *
     * @param sex 性别 0男 1女
     */
    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取邮箱地址
     *
     * @return mail - 邮箱地址
     */
    public String getMail() {
        return mail;
    }

    /**
     * 设置邮箱地址
     *
     * @param mail 邮箱地址
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}