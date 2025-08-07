/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;

/**
 * <P>
 * 工具类，主要用于从request对象中获取用户真实ip地址
 * </p>
 *
 * @author jianghuipeng
 * @since 2021-09-14
 */
@Slf4j
@Component
public class IPUtils {
    
    private final static String UNKNOWN_STR = "unknown";

    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String path;
    
    /**
     * 获取客户端IP地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isEmptyIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (isEmptyIP(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (isEmptyIP(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                    if (isEmptyIP(ip)) {
                        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                        if (isEmptyIP(ip)) {
                            ip = request.getRemoteAddr();
                            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                                // 根据网卡取本机配置的IP
                                ip = getLocalAddr();
                            }
                        }
                    }
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!isEmptyIP(ip)) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
    
    private static boolean isEmptyIP(String ip) {
        if (StrUtil.isEmpty(ip) || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取本机的IP地址
     */
    public static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error", e);
        }
        return "";
    }


    public static String getRealIP(HttpServletRequest request) {
        // 获取客户端ip地址
        String ipString = request.getHeader("x-forwarded-for");

        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }
        /*
         * 对于获取到多ip的情况下，找到公网ip.
         */
        String sIP = null;
        if (ipString != null && !ipString.contains("unknown") && ipString.indexOf(",") > 0) {
            String[] ipsz = ipString.split(",");
            for (String anIpsz : ipsz) {
                if (!isInnerIP(anIpsz.trim())) {
                    sIP = anIpsz.trim();
                    break;
                }
            }
            /*
             * 如果多ip都是内网ip，则取第一个ip.
             */
            if (null == sIP) {
                sIP = ipsz[0].trim();
            }
            ipString = sIP;
        }
        if (ipString != null && ipString.contains("unknown")){
            ipString =ipString.replaceAll("unknown,", "");
            ipString = ipString.trim();
        }
        if ("".equals(ipString) || null == ipString){
            ipString = "127.0.0.1";
        }
        return ipString;
    }

    /**
     * 判断IP是否是内网地址
     * @param ipAddress ip地址
     * @return 是否是内网地址
     */
    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp;
        long ipNum = getIpNum(ipAddress);
        /**
         私有IP：A类  10.0.0.0-10.255.255.255
         B类  172.16.0.0-172.31.255.255
         C类  192.168.0.0-192.168.255.255
         当然，还有127这个网段是环回地址
         **/
        long aBegin = getIpNum("10.0.0.0");
        long aEnd = getIpNum("10.255.255.255");

        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");

        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
                || ipAddress.equals("127.0.0.1");
        return isInnerIp;
    }

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }
    
    //内网ip地址
    public static String INTRANET_IP = getIntranetIp();
    //外网ip地址
    public static String INTERNET_IP = getInternetIp();
    
    public static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "X-REAL-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "REMOTE-HOST" };
    /**
     * 获取内网ip
     * @return
     */
    private static String getIntranetIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }catch (Exception e){
            log.error("{}",e);
        }
        return null;
    }
    
    /**
     * 获取外网ip
     * @return
     */
    public static String getInternetIp(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if(networkInterfaces!=null){
                while (networkInterfaces.hasMoreElements()){
                    Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                    if(inetAddresses!=null){
                        while (inetAddresses.hasMoreElements()){
                            InetAddress ip = inetAddresses.nextElement();
                            if(ip!=null && ip instanceof Inet4Address && ip.isSiteLocalAddress() && !ip.getHostAddress().equals(INTRANET_IP)){
                                return  ip.getHostAddress();
                            }
                        }
                    }
                }
            }
            return INTRANET_IP;
        } catch (SocketException e) {
            log.error("{}",e);
        }
        return null;
    }
    
    /**
     * 获取客户端的IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String clientIp = _getClientIp(request);
        if (null != clientIp && !clientIp.trim().isEmpty()) {
            return clientIp;
        }
        return request.getRemoteAddr();
    }
    
    private static String _getClientIp(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
    
    /**
     * 获取实际请求IP
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        String ip =null;
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            //对于通过多个代理的情况，最后IP为客户端真实IP,多个IP按照','分割
            int position = ip.indexOf(",");
            if (position > 0) {
                ip = ip.substring(0, position);
            }
        }
        return ip;
    }
    
    /**
     * @Description:获取nginx代理路径
     * @Author:     kuangyu
     * @Date:       2021年7月16日
     */
    public static String getDomianPath(HttpServletRequest request){
        String proxyPath = request.getHeader("DomainPath");
        if (proxyPath != null) {
            return proxyPath;
        }
        
        return "";
    }

    public String getIpPort() {
        try {
            return Inet4Address.getLocalHost().getHostAddress() + ":" + port;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAccessPath() {
        try {
            return StrUtil.format("http://{}:{}{}", Inet4Address.getLocalHost().getHostAddress(), port, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
