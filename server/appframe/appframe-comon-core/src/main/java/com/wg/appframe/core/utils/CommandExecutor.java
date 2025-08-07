/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * <p>
 * 执行CMD命令工具类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-08-04
 */
@Slf4j
public class CommandExecutor {

    /**
     * 执行一个命令且返回结果
     *
     * @param command commands
     * @return 返回
     * @throws Exception 异常
     */
    public static String executeCommandForResult(String command) throws Exception {
        log.info("==>>execute command:{}", command);
        return executeCommandForResult(new String[]{command});
    }

    /**
     * 执行多个命令且返回结果
     *
     * @param commands commands
     * @return 返回
     * @throws Exception 异常
     */
    public static String executeCommandForResult(String[] commands) throws Exception {
        log.info("==>>execute command:{}", Arrays.toString(commands));
        InputStream stderr = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);
            int ret = proc.waitFor();
            if (ret == 0) {
                stderr = proc.getInputStream();
            } else {
                stderr = proc.getErrorStream();
            }

            isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            log.info("==>> execute command finished,result:{}", builder);
            return builder.toString();
        } catch (Exception e) {
            log.error("==>>execute command found error.", e);
            throw new GlobalException(ResultCodeEnum.SYSTEM_ERROR);
        } finally {
            if (stderr != null) {
                stderr.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * 执行命令
     *
     * @param command 命令
     * @return 返回
     * @throws Exception 异常
     */
    public static boolean executeCommand(String command) throws Exception {
        log.info("==>>execute command:{}", command);
        return executeCommand(new String[]{command});
    }

    /**
     * 执行cmd 命令
     *
     * @param commands 命令
     * @return 返回
     * @throws Exception 异常
     */
    public static boolean executeCommand(String[] commands) throws Exception {
        log.info("==>>execute command:{}", Arrays.toString(commands));
        InputStream stderr = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);
            int code = proc.waitFor();
            if (code != 0) {
                return false;
            }
            stderr = proc.getErrorStream();
            isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
            }
            log.info("==>> execute command finished .");
            return true;
        } catch (Exception e) {
            log.error("==>>execute command found error.", e);
            throw new GlobalException(ResultCodeEnum.SYSTEM_ERROR);
        } finally {
            if (stderr != null) {
                stderr.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }
}

