 package com.dabin.common.constants;

 /**
  * 状态码
  *
  * @author 大彬
  * @date 2021-11-17 20:29
  */
 public class ResultCode {

    public static final ResultCode NORMAL_RETURNED = new ResultCode(1000, "OK");

    public static final ResultCode PARAMAS_ERROR = new ResultCode(1002, "Parameters Error");

    public static final ResultCode DATABASE_ERROR = new ResultCode(1003, "Database Error");

    public static final ResultCode USER_ID_EMPTY = new ResultCode(1004, "用户id为空");

    public static final ResultCode COMMENT_TARGET_ID_EMPTY = new ResultCode(1005, "评论目标id为空");

    public static final ResultCode UNKNOWN = new ResultCode(1006, "Unknow Error");

    public static final ResultCode SERVER_ERROR = new ResultCode(1007, "Server Error");

    public static final ResultCode COMMENT_CONTENT_EMPTY = new ResultCode(1008, "评论内容不能为空");

    public static final ResultCode VOTE_TARGET_NOT_EXIST = new ResultCode(1009, "点赞目标不存在");

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.desc;
    }

    public ResultCode(int code, String description) {
        this.code = code;
        this.desc = description;
    }

    @Override
    public String toString() {
        return this.code + ", " +  this.desc;
    }
}
