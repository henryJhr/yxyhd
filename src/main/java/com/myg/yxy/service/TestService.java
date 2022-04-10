package com.myg.yxy.service;

import com.hxsoft.hsf.boot.framework.service.BaseService;
import com.myg.yxy.model.TestModel;
import com.myg.yxy.model.rul.rulTest;

public interface TestService extends BaseService<TestModel> {
    rulTest test(TestModel titles);
}
