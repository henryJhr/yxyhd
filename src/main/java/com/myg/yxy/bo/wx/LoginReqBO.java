package com.myg.yxy.bo.wx;

import lombok.Data;

/**
 * @Classname LoginReqBO
 * @Description TODO
 * @Date 2022/3/20 21:10
 * @Created 刘振华
 */
@Data
public class LoginReqBO {
    private Long userId;
    private String nickName;
    private String avatarUrl;
}
