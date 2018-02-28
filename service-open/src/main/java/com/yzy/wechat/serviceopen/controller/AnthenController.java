package com.yzy.wechat.serviceopen.controller;

import com.alibaba.fastjson.JSONObject;
import com.yzy.wechat.serviceopen.ResultBean.ServiceResponse;
import com.yzy.wechat.serviceopen.ResultBean.response.GetApiTicketResponse;
import com.yzy.wechat.serviceopen.ResultBean.response.GetGlobalAccessTokenResponse;
import com.yzy.wechat.serviceopen.ResultBean.response.GetJsApiTicketResponse;
import com.yzy.wechat.serviceopen.ResultBean.response.GetOpenId;
import com.yzy.wechat.serviceopen.entity.Wechat;
import com.yzy.wechat.serviceopen.service.redis.RedisService;
import com.yzy.wechat.serviceopen.service.impl.wechat.WechatServiceImpl;
import com.yzy.wechat.serviceopen.util.HttpSend;
import com.yzy.wechat.serviceopen.util.ServiceResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;


@Controller
public class AnthenController {
	
	private static Logger logger = LoggerFactory.getLogger(AnthenController.class);
	@Autowired
	private WechatServiceImpl wechatService;
	@Autowired
	private RedisService redisService;



	@RequestMapping("/accessPayAnthen")
	public String accessAnthen(HttpServletRequest request){
		logger.info("正在获取用户授权信息");

		String redirectUrl = "";
		try {
			String state = request.getParameter("state");
			String code = request.getParameter("code");
			String appid=request.getParameter("appid");
		    //获取pay appid appsecret
			Wechat payWechat=wechatService.getPayWechat(appid);
			String yzyWechatAppId=payWechat.getAppid();
			String yzyWechatAppsecret=payWechat.getAppsecret();

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
	public ServiceResponse<GetOpenId> getOpenId(HttpServletRequest request){
		logger.info("正在获取用户授权信息");
		try {
			String code = request.getParameter("code");
			String appid =request.getParameter("appid");
		    //获取pay appid appsecret
			Wechat payWechat=wechatService.getPayWechat(appid);
			String yzyWechatAppId=payWechat.getAppid();
			String yzyWechatAppsecret=payWechat.getAppsecret();


			String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
			access_token_url = access_token_url.replace("APPID", yzyWechatAppId).replace("SECRET", yzyWechatAppsecret).replace("CODE", code);
			String tokenRes = HttpSend.sendGet(access_token_url,"json");
			JSONObject tokenJson = (JSONObject) JSONObject.parse(tokenRes);
			String openid = tokenJson.getString("openid");
			return ServiceResponseUtil.success(new GetOpenId(openid));
		} catch (Exception e) {
		    logger.error("获取openid失败");
			e.printStackTrace();
		}
		return ServiceResponseUtil.error("获取openid失败");
	}

	@RequestMapping("/getGlobalAccessToken")
	@ResponseBody
	public ServiceResponse<GetGlobalAccessTokenResponse> getGlobalAccessToken(){
		logger.info("正在获取全局access_token");
		try{
			return ServiceResponseUtil.success(new GetGlobalAccessTokenResponse(redisService.get("wx_access_token_yzy")));
		}catch (Exception e){
			logger.error("获取全局access_token失败");
			e.printStackTrace();
		}
		return ServiceResponseUtil.error("获取全局access_token失败");
	}

	@RequestMapping("/getJsapiTicket")
	@ResponseBody
	public ServiceResponse<GetJsApiTicketResponse> getJsapiTicket(){
		logger.info("正在获取jsapi_ticket");
		try{
			return ServiceResponseUtil.success(new GetJsApiTicketResponse(redisService.get("wx_jsapi_ticket_yzy")));
		}catch (Exception e){
			logger.error("获取jsapi_ticket失败");
			e.printStackTrace();
		}
		return ServiceResponseUtil.error("获取jsapi_ticket失败");
	}

	@RequestMapping("/getApiTicket")
	@ResponseBody
	public ServiceResponse<GetApiTicketResponse> getApiTicket(){
		logger.info("正在获取api_ticket");
		try{
			return ServiceResponseUtil.success(new GetApiTicketResponse(redisService.get("wx_api_ticket_yzy")));
		}catch (Exception e){
			logger.error("获取api_ticket失败");
			e.printStackTrace();
		}
		return ServiceResponseUtil.error("获取api_ticket失败");
	}
}
