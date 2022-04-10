package com.myg.yxy.model;

import com.hxsoft.hsf.boot.framework.model.BaseModel;
import lombok.Data;

/**
 * @ClassName: BusiActivityUserPo
 * @TableName: busi_activity_user
 * @Description: TODO
 * @Author: 刘振华
 * @Date: 2022-03-27 08:09:55
 */
@Data
public class ActivityUserModel extends BaseModel {

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 活动ID
	 */
	private Long activityId;
	/**
	 * 关注状态
	 */
	private Integer status;
}