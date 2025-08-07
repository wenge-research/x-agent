package com.wenge.oauth.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.wenge.oauth.annotation.InterfaceCallLog;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wenge.oauth.entity.InterfaceCallLogRecording;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.OauthLoginLogMapper;
import com.wenge.oauth.service.InterfaceCallLogService;
import com.wenge.oauth.service.UmsOperationService;
import com.wenge.oauth.util.IPUtils;
import com.wenge.oauth.util.RequestUtils;
import com.wenge.oauth.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: caohaifeng
 * @date: 2024/8/23 9:58
 * @Description: 统一日志处理切面
 * @Version:1.0
 **/
@Aspect
@Component
@Order(1)
public class WebLogAspect implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);
    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final  String OPERATOR_TAG = "OperatorTag";
    private static final  String AUTHORIZATION = "Authorization";
    private static final  String USER_AGENT = "User-Agent";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //应用列表缓存-日志使用
    private Map<String, Map<String, Object>> applicationListMap = new ConcurrentHashMap<>();


    @Value("${system-log-manage.file-size}")
    private Long logFileSize;
    @Value("${system-log-manage.isopen-cache-file}")
    private Boolean isOpenCacheFile = false;

    @Autowired
    private UmsOperationService umsOperationService;
    @Autowired
    private InterfaceCallLogService interfaceCallLogService;
    @Autowired
    private OauthLoginLogMapper oauthLoginLogMapper;

    @Pointcut("execution(public * com.wenge.*.controller..*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(value = "webLog()", returning = "ret")
    public void doAfterReturning(Object ret) throws Throwable {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long t1 = System.currentTimeMillis();
        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String operatorAccountName = null;
        String authTokenAccount = null;
        String operatorUserName = null;
        Long operatorUserId = null;
        String operatorTenantId = null;
        try {
            String authToken =  request.getHeader(this.AUTHORIZATION);
            if(!StringUtils.isEmpty(authToken)){
                authTokenAccount = authToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //在请求头中没有找到用户信息就根据上下文对象中查找 上下文也查到不到就不找了（说明没有登录访问的日志记录）
        if (operatorUserId == null) {
            try {
                // 获取当前用户信息
                TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                if (tokenUserInfo != null && tokenUserInfo.getId() != null) {
                    operatorAccountName = tokenUserInfo.getAccountName();
                    operatorUserId = tokenUserInfo.getId();
                    operatorTenantId = tokenUserInfo.getTenantId();
                    operatorUserName = tokenUserInfo.getUserName();
                }
            }catch (Exception e){
//                e.printStackTrace();
            }
        }
        UmsOperation esweblog = new UmsOperation();//通过调用自己写的方法写入es
        esweblog.setOperatorTagAccountName(operatorAccountName);
        esweblog.setOperatorTagUserId(operatorUserId);
        esweblog.setOperatorTagTenantId(operatorTenantId);
        esweblog.setOperatorTagUserName(operatorUserName);
        esweblog.setOperatorSource("null-no"); //用户操作日志页面不展示


        long t2 = System.currentTimeMillis();
        //记录请求信息(通过logstash传入elasticsearch)
        Object result = joinPoint.proceed();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(ApiOperation.class)) {
            ApiOperation log = method.getAnnotation(ApiOperation.class);
			esweblog.setDescription(log.value());
            esweblog.setOperatorSource("annotate-no");
        }
        Object parameter = getParameter(method, joinPoint.getArgs());

        //日志注解
        if (method.isAnnotationPresent(UmsOperationLog.class)) {
            UmsOperationLog umsOperationLog = method.getAnnotation(UmsOperationLog.class);
            esweblog.setDescription(umsOperationLog.description());
            esweblog.setLogType(umsOperationLog.logType());
            esweblog.setBelongModule(umsOperationLog.belongModule());
            esweblog.setBelongModuleName(umsOperationLog.belongModuleName());
            esweblog.setObjectUuid(umsOperationLog.objectUuid());
            esweblog.setObjectName(umsOperationLog.objectName());
            esweblog.setObjectType(umsOperationLog.objectType());
            esweblog.setOperatorSource("annotate-UmsOperationLog");

            if("应用".equals(umsOperationLog.objectType())){
                final JSONObject parseObjectJson = JSONObject.parseObject(JSON.toJSONString(parameter));
                if (parseObjectJson.get("applicationId") != null) {
                    String applicationId = parseObjectJson.get("applicationId") + "";
                    esweblog.setObjectUuid(applicationId);
                    if (applicationListMap.get(applicationId) != null) {
                        esweblog.setObjectName((Map<String, Object>) applicationListMap.get(applicationId).get("application_name") + "");
                    }else {
                        final Map<String, Object> applicationIdMap= oauthLoginLogMapper.getApplicationInfoByAppId(applicationId);
                        if (applicationIdMap != null) {
                            esweblog.setObjectName(applicationIdMap.get("application_name") + "");
                            applicationListMap.put(applicationIdMap.get("id") + "", applicationIdMap);
                        }
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        //封装需要写入es日志记录的数据
		try {
            String uuid = (UUID.randomUUID() + startTime.get().toString());
            esweblog.setId(uuid);
			esweblog.setUuid(uuid);
			esweblog.setBasePath(RequestUtils.getBasePath(request));
			esweblog.setUri(request.getRequestURI());
			esweblog.setUrl(request.getRequestURL().toString());
			esweblog.setMethod(request.getMethod());
			esweblog.setIp(IPUtils.getIpAddr(request));
			esweblog.setAccessTime(sdf.format(new Date(startTime.get())));
			esweblog.setInsertTime(sdf.format(new Date()));
			esweblog.setSpend((int) (endTime - startTime.get()));

			try {
                //出参 入参内容太大了 暂时先不需要20240826
                if (parameter != null) {
                    if (parameter instanceof InputStreamSource) {
                        esweblog.setParameter("file");
                    } else if (parameter instanceof Map) {
                        Map map = (Map) parameter;
                        List<String> keys = Lists.newArrayList();
                        map.forEach((k, v) -> {
                            if (v instanceof InputStreamSource) {
                                keys.add(k.toString());
                            }
                        });
                        // 移除 key=file 的
                        ((Map) parameter).entrySet().removeIf(entry -> ((Map.Entry) entry).getValue() instanceof InputStreamSource);
                    }
                    esweblog.setParameter(JSON.toJSONString(parameter));
                }
            }catch (Exception e){
			    e.printStackTrace();
            }

            if (method.isAnnotationPresent(InterfaceCallLog.class)) {
                esweblog.setResult(JSON.toJSONString(result));
            }

			//获取浏览器信息
			try {
				String userAgent = request.getHeader(this.USER_AGENT);
				//转成UserAgent对象
				UserAgent useragent = UserAgent.parseUserAgentString(userAgent);
				//获取浏览器信息
				Browser browser = useragent.getBrowser();
				//获取系统信息
				OperatingSystem os = useragent.getOperatingSystem();
				//系统名称
				String systemName = os.getName();
				//浏览器名称
				String browserName = browser.getName();
				esweblog.setUserAgent(userAgent);
				esweblog.setBrowserName(browserName);
				esweblog.setSystemName(systemName);
			} catch (Exception e) {
			    LOGGER.error("浏览器信息获取异常");
			}
            long t3 = System.currentTimeMillis();

			//插件API日志记录
            if (method.isAnnotationPresent(InterfaceCallLog.class)) {
                InterfaceCallLogRecording interfaceCallLogRecording = new InterfaceCallLogRecording();
                BeanUtils.copyProperties(esweblog, interfaceCallLogRecording);
                interfaceCallLogRecording.setAuthTokenAccount(authTokenAccount);
                try {
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(authTokenAccount)){
                        if (authTokenAccount.startsWith(TokenUtil.SIGN)) { //插件绑定的用户鉴权
                            //根据token解析用户信息
                            final String[] strings = TokenUtil.parseToken(authTokenAccount);
                            if (strings != null && strings.length > 0) {
                                String authUserInfo = "";
                                for (int i = 0; i < strings.length; i++) {
                                    if (i == strings.length - 1) {
                                        authUserInfo += strings[i];
                                    }else{
                                        authUserInfo += strings[i] + "@";
                                    }
                                }
                                interfaceCallLogRecording.setAuthUserInfo(authUserInfo);
                                interfaceCallLogRecording.setModelPluginApiId(strings[0]);
                                interfaceCallLogRecording.setAuthUserInfoId(strings[1]);
                                interfaceCallLogRecording.setAuthUserInfoType("1");
                            }
                        }else { //系统页面运行 -记录登录人的信息
                            interfaceCallLogRecording.setAuthUserInfo("--");
                            interfaceCallLogRecording.setAuthUserInfoId(operatorUserId + "");
                            interfaceCallLogRecording.setAuthUserInfoType("2");
                        }
                    }else { //无鉴权用户
                        interfaceCallLogRecording.setAuthUserInfo("--");
                        interfaceCallLogRecording.setAuthUserInfoId("--");
                        interfaceCallLogRecording.setAuthUserInfoType("3");
                    }
                    interfaceCallLogService.addInterfaceCallLog(interfaceCallLogRecording);
                }catch (Exception e){
//                    e.printStackTrace();
                    LOGGER.error("模型计算-插件日志token解析异常：{}", e.getMessage());
                }
            }
			if(isOpenCacheFile){
                //先将日志记录写入文件 然后异步刷入es索引中
                saveEsWebLogLocalFile(esweblog);
            }else {
                umsOperationService.addUmsOperationg(esweblog);
            }
            long t4 = System.currentTimeMillis();
            LOGGER.info("isOpenCacheFile： " + isOpenCacheFile + " >>> WEB LOG TIME >>> \nt1:" + t1 + " t2:" + t2 + " t3:" + t3 + " t4:" + t4 + "\n 日志记录总耗时:" + (t4 - t1) / 1000);
		} catch (Exception e) {// 防止插入日志失败 失败也要返回数据 保证系统鲁棒性
		    e.printStackTrace();
			LOGGER.error("saveEsWebLog error:{}", e.getMessage());
		}
        return result;
    }

    /**
     * 接口日志写入本地文件
     */
    public void saveEsWebLogLocalFile(UmsOperation umsOperation){
        File logDir = new File("operlog");
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        try{
            String esweblogstr = JSONObject.toJSONString(umsOperation);
            JSONObject parseObject = JSONObject.parseObject(esweblogstr);
            File logFile = new File("operlog" + File.separator + "oper.log");

            if(!logFile.exists()){
                logFile.createNewFile();
            }
            //先判断大小
            Long fileSize = FileUtils.sizeOf(logFile);
            //大于xxKB
            if (fileSize / 1024 > logFileSize) {
                String newFileName = "history.log" + "_" + System.currentTimeMillis();
                FileUtils.moveFile(logFile, new File("operlog" + File.separator + newFileName));
                logFile.createNewFile();
            }
            FileUtils.writeStringToFile(logFile,parseObject.toString()+"\n",true);
        }catch (Exception e){
            e.printStackTrace();
        }
//        LOGGER.info("{}", "日志写入文件成功");
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }
}
