package com.myg.yxy.model;



import com.hxsoft.hsf.boot.framework.model.BaseModel;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 行心里测评
 */

public class TestModel extends BaseModel {

    private int id;
    @NotNull

    private Integer Rankc;

    private Integer age;

    private Integer sex;

    private Integer edu;

    private Integer[] test;

    private String openid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRankc() {
        return Rankc;
    }

    public void setRankc(Integer rankc) {
        Rankc = rankc;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getEdu() {
        return edu;
    }

    public void setEdu(Integer edu) {
        this.edu = edu;
    }

    public Integer[] getTest() {
        return test;
    }

    public void setTest(Integer[] test) {
        this.test = test;
    }


    @Override
    public String toString() {
        return "test{" +
                "id=" + id +
                ", Rankc=" + Rankc +
                ", age=" + age +
                ", sex=" + sex +
                ", edu=" + edu +
                ", test=" + Arrays.toString(test) +
                '}';
    }
}
