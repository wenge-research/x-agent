package com.wenge.oauth.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoteCommandUtil {

    private String host;
    private String username;
    private String password;
    private int port;


    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            // 创建SSH会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 创建执行命令的通道
            channel = (ChannelExec) session.openChannel("exec");
            log.info("执行命令: " + command);
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            // 获取命令执行结果
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                output.append(new String(buffer, 0, bytesRead));
            }

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        log.info("执行输出结果: " + output);
        return output.toString();
    }


    public boolean testSSHConnection() {
        JSch jsch = new JSch();
        Session session = null;
        try {
            // 创建SSH会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no"); // 忽略主机密钥检查
            session.connect(5000); // 设置连接超时时间为5秒
            return true; // 连接成功
        } catch (JSchException e) {
            e.printStackTrace();
            return false; // 连接失败
        } finally {
            if (session != null) {
                session.disconnect(); // 关闭会话
            }
        }
    }


}
