package com.myg.yxy.dao;





import com.hxsoft.hsf.boot.framework.dao.BaseDao;
import com.myg.yxy.model.TestModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper extends BaseDao {

    int insert(TestModel record);


    Double AvgRank();

}
