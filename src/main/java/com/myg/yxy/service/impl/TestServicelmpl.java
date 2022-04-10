package com.myg.yxy.service.impl;

import com.hxsoft.hsf.boot.framework.service.impl.BaseServiceImpl;
import com.myg.yxy.dao.ActivityDao;
import com.myg.yxy.dao.TestMapper;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.TestModel;
import com.myg.yxy.model.rul.rulTest;
import com.myg.yxy.service.ActivityService;
import com.myg.yxy.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
@Service
public class TestServicelmpl extends BaseServiceImpl<TestMapper, TestModel> implements TestService {

    @Autowired
    private TestMapper TestDao;


    public rulTest test(TestModel titles) {


        int fen = 0;
        Map table = new HashMap();
        for (int i = 0; i < titles.getTest().length; i++) {
            if (i > 6 && i < 11) {
                fen += 10 - titles.getTest()[i];
            } else{
                fen += titles.getTest()[i];
            }

        }
        System.out.println(fen);

        table.put("Rank",fen);
        table.put("Age",titles.getAge());
        table.put("Sex",titles.getSex());
        table.put("Sexuality",titles.getEdu());
        TestModel test = new TestModel();
        test.setRankc(fen);
        test.setAge(titles.getAge());
        test.setSex(titles.getSex());
        test.setEdu(titles.getEdu());
        System.out.println(test);


        TestDao.insert(test);
       rulTest rulTest = new rulTest();
       rulTest.setAvgScore(TestDao.AvgRank());
       rulTest.setScore((double) fen);


        return rulTest;
    }
}
