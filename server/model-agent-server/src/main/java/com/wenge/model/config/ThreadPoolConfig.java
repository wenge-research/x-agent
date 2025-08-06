package com.wenge.model.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * todo yayi-chat、yayi-file
 *
 * @author zwc
 * @since 2023/6/9
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "dialogueByStreamPool")
    public ThreadPoolExecutor dialogueByStreamPool() {
        final int cpuCount = Runtime.getRuntime().availableProcessors();
        final int maxPoolSize = (int) (cpuCount * 0.8 * 2);
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dialogue-stream-pool-%d").build();

        return new ThreadPoolExecutor(cpuCount, maxPoolSize, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "dialogueByCvPool")
    public ThreadPoolExecutor dialogueByCvPool() {
        final int cpuCount = Runtime.getRuntime().availableProcessors();
        final int maxPoolSize = (int) (cpuCount * 0.8 * 2);
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dialogue-cv-pool-%d").build();

        return new ThreadPoolExecutor(cpuCount, maxPoolSize, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "yayiPaserUrlPool")
    public ThreadPoolExecutor yayiPaserUrlPool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("yayi-paser-url-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "addressAnalysisPool")
    public ThreadPoolExecutor addressAnalysisPool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("address-analysis-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "fileAnalysisPool")
    public ThreadPoolExecutor fileAnalysisPool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("address-analysis-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "imageAnalysisPool")
    public ThreadPoolExecutor imageAnalysisPool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("address-analysis-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "dataSourceStructurePool")
    public ThreadPoolExecutor dataSourceStructurePool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("yayi-paser-url-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "excelParserPool")
    public ThreadPoolExecutor excelParserPool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("yayi-paser-url-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "excelStructurePool")
    public ThreadPoolExecutor excelStructurePool() {
        return new ThreadPoolExecutor(
                20,
                200,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("yayi-paser-url-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * manus线程池
     * @return
     */
    @Bean(name = "manusPool")
    public ThreadPoolExecutor manusPool() {
        return new ThreadPoolExecutor(
                32,
                32,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("manusPool-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }


    /**
     * 推演线程池
     * @return
     */
    @Bean(name = "decisionOne")
    public ThreadPoolExecutor decisionOne() {
        return new ThreadPoolExecutor(
                4,
                32,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("decisionOne-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
