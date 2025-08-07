package com.wg.appframe.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.wg")
@Slf4j
public class CoreAutoConfiguration {

    public CoreAutoConfiguration() {
        log.info("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓 appframe-comon-core start 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
    }
}
