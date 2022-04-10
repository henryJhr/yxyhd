package com.myg.yxy.utils;

import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.myg.yxy.dao.ActivityDao;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.service.ActivityService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ActivityTime {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");


    @Autowired
    private ActivityDao activityService;



    @Scheduled(cron = "0 */1 * * * ?")
    //@Scheduled(fixedRate = 1000)
    public void execute(){
        Date sysDate = new Date();
        ActivityModel activityModel = new ActivityModel();
        activityModel.setStatus(0);
        List<ActivityModel> Impending =  activityService.getList(activityModel);
        for(ActivityModel value:Impending){
            value.setStatus(1);
            if (value.getTime().after(sysDate)){
               activityService.getUpdateById(value);
            }
        }

        ActivityModel activityModel1 = new ActivityModel();
        activityModel.setStatus(1);
        List<ActivityModel> activityRun =  activityService.getList(activityModel1);
        for(ActivityModel value:activityRun){
            value.setStatus(2);
            if (value.getEndTime().after(sysDate)){
                activityService.getUpdateById(value);
            }
        }

    }

}
