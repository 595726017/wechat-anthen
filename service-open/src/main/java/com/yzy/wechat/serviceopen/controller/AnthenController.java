package com.yzy.wechat.serviceopen.controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import com.yzy.wechat.serviceopen.util.HttpSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;


@Controller
public class AnthenController {
	
	 private static Logger logger = LoggerFactory.getLogger(AnthenController.class);  

	@Value(value = "${yzy.paywechat.appid}")
	private String yzyWechatAppId;
	
	@Value(value = "${yzy.paywechat.appsecret}")
	private String yzyWechatAppsecret;
	
	@RequestMapping("/accessPayAnthen")
	public String accessAnthen(HttpServletRequest request){
		logger.info("正在获取用户授权信息");
		String redirectUrl = "";
		try {
			String state = request.getParameter("state");
			String code = request.getParameter("code");
	        String[] states = state.split("_");
	        redirectUrl = states[0];
	        String payNo = states[1].split(":")[1];
	        String orderId = states[2].split(":")[1];
//	        String billNo = states[3].split(":")[1];
			String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
			access_token_url = access_token_url.replace("APPID", yzyWechatAppId).replace("SECRET", yzyWechatAppsecret).replace("CODE", code);
			String tokenRes = HttpSend.sendGet(access_token_url,"json");
			JSONObject tokenJson = (JSONObject) JSONObject.parse(tokenRes);
			String access_token = tokenJson.getString("access_token");
			String openid = tokenJson.getString("openid");
			
			String access_user_url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
			access_user_url = access_user_url.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);
			String userInfo = HttpSend.sendGet(access_user_url,"json");
			JSONObject userInfoJson = JSONObject.parseObject(userInfo);
			userInfoJson.put("appId", yzyWechatAppId);
			userInfoJson.put("payNo", payNo);
			userInfoJson.put("openid", openid);
			userInfoJson.put("orderId", orderId);
//			userInfoJson.put("billNo", billNo);
			String param = URLEncoder.encode(URLEncoder.encode(userInfoJson.toJSONString(), "UTF-8"), "UTF-8");
			redirectUrl = redirectUrl + "?payCode=" + param;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" +redirectUrl;
	}
	
	@RequestMapping("/getOpenId")
	@ResponseBody
	public String getOpenId(HttpServletRequest request){
		logger.info("正在获取用户授权信息");
		try {
			String code = request.getParameter("code");
			String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
			access_token_url = access_token_url.replace("APPID", yzyWechatAppId).replace("SECRET", yzyWechatAppsecret).replace("CODE", code);
			String tokenRes = HttpSend.sendGet(access_token_url,"json");
			JSONObject tokenJson = (JSONObject) JSONObject.parse(tokenRes);
			String openid = tokenJson.getString("openid");
			return openid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}