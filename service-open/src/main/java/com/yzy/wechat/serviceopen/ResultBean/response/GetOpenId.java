package com.yzy.wechat.serviceopen.ResultBean.response;

/**
 * @作者：刘富国
 * @创建时间：2018/2/27 15:59
 */
public class GetOpenId {
    String openid;

    public GetOpenId(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
