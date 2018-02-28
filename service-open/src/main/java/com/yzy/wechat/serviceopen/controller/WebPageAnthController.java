package com.yzy.wechat.serviceopen.controller;

import com.alibaba.fastjson.JSONObject;
import com.yzy.wechat.serviceopen.ResultBean.ServiceResponse;
import com.yzy.wechat.serviceopen.ResultBean.dto.AccessTokenDTO;
import com.yzy.wechat.serviceopen.ResultBean.response.*;
import com.yzy.wechat.serviceopen.entity.Wechat;
import com.yzy.wechat.serviceopen.service.redis.RedisService;
import com.yzy.wechat.serviceopen.service.wechat.WechatService;
import com.yzy.wechat.serviceopen.util.HttpSend;
import com.yzy.wechat.serviceopen.util.ServiceResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.yzy.wechat.serviceopen.util.Request2Map.getParameterMap;
import static com.yzy.wechat.serviceopen.util.SignUtil.generateSignature;
import static com.yzy.wechat.serviceopen.util.SignUtil.isSignatureValid;

/**
 * 类的功能描述：微信公众平台 相关接口
 * @作者：刘富国
 * @创建时间：2018/2/28 15:16  */
@Controller
public class WebPageAnthController {

    private static final Logger logger = LoggerFactory.getLogger(WebPageAnthController.class);
    @Autowired
    private WechatService wechatService;
    @Autowired
    private RedisService redisService;


    @RequestMapping("/getAccessToken")
    @ResponseBody
    /** 获取网页授权 access_token */
    public ServiceResponse<GetAccessTokenResponse> getAccessToken(HttpServletRequest request) {
        try {
            logger.info("正在获取网页授权access_token:");
            String appid = request.getParameter("appid");
            String code = request.getParameter("code");
            AccessTokenDTO accessTokenDTO=wechatService.getWebPageAccessToken(code,appid);
            if(StringUtils.isEmpty(accessTokenDTO.getMessage())){
                return ServiceResponseUtil.success(accessTokenDTO.getAccessToken());
            }else{
                return ServiceResponseUtil.error(accessTokenDTO.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取网页授权access_token失败！");
        }
        return ServiceResponseUtil.error("获取网页授权access_token失败！");
    }

    @RequestMapping("/getOpenId")
    @ResponseBody
    /** 获取网页授权 openid */
    public ServiceResponse<GetOpenIdResponse> getOpenId(HttpServletRequest request){
        try {
            logger.info("正在获取网页授权获取网页授权 openid :");
            String appid = request.getParameter("appid");
            String code = request.getParameter("code");
            AccessTokenDTO accessTokenDTO=wechatService.getWebPageAccessToken(code,appid);
            if(StringUtils.isEmpty(accessTokenDTO.getMessage())){
                return ServiceResponseUtil.success(accessTokenDTO.getOpenid());
            }else{
                return ServiceResponseUtil.error(accessTokenDTO.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取网页授权获取网页授权 openid 失败！");
        }
        return ServiceResponseUtil.error("获取网页授权access_token失败！");
    }

    @RequestMapping("/getGlobalAccessToken")
    @ResponseBody
    /** 获取全局 access_token */
    public ServiceResponse<GetGlobalAccessTokenResponse> getGlobalAccessToken(HttpServletRequest request) {
        try {
            logger.info("正在获取全局access_token");
            return ServiceResponseUtil.success(new GetGlobalAccessTokenResponse(redisService.get("wx_access_token_yzy")));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取全局access_token失败");
        }
        return ServiceResponseUtil.error("获取全局access_token失败");
    }

    @RequestMapping("/getJsapiTicket")
    @ResponseBody
    /** 获取 jsapi_ticket */
    public ServiceResponse<GetJsApiTicketResponse> getJsapiTicket(HttpServletRequest request) {
        try {
            logger.info("正在获取jsapi_ticket");
            return ServiceResponseUtil.success(new GetJsApiTicketResponse(redisService.get("wx_jsapi_ticket_yzy")));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取jsapi_ticket失败");
        }
        return ServiceResponseUtil.error("获取jsapi_ticket失败");
    }

    @RequestMapping("/getApiTicket")
    @ResponseBody
    /** 获取 api_ticket */
    public ServiceResponse<GetApiTicketResponse> getApiTicket(HttpServletRequest request) {
        try {
            logger.info("正在获取api_ticket");
            return ServiceResponseUtil.success(new GetApiTicketResponse(redisService.get("wx_api_ticket_yzy")));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取api_ticket失败");
        }
        return ServiceResponseUtil.error("获取api_ticket失败");
    }
}
