package com.myg.yxy.dao;

import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.dao.BaseDao;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.model.rul.rulID;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDao extends BaseDao<UserModel> {

    List<UserModel> getListPage(UserModel userModel, PaginationParam paginationParam);

    Integer checkById(UserModel user);

    Long getUserId(UserModel user);

    int insert(UserModel code);

    int updateById(UserModel user);
}
