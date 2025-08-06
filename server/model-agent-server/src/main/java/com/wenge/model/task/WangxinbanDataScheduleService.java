package com.wenge.model.task;

import com.wenge.model.dto.param.SyncLegalAndRegulatoryDataParam;
import com.wenge.model.mapper.es.LegalAndRegulatoryDataMapper;
import com.wenge.model.service.LegalAndRegulatoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 网信办数据 定时拉取
 * </p>
 */
@Slf4j
@Component
@Controller
@RequestMapping("/agentTask")
public class WangxinbanDataScheduleService {

    @Autowired
    private LegalAndRegulatoryDataService legalAndRegulatoryDataService;

    @Autowired
    private LegalAndRegulatoryDataMapper legalAndRegulatoryDataMapper;


    /**
     * 每天早上七点执行一次拉取
     */
//    @PostConstruct
    // @Scheduled(cron = "0 0 7 * * ?")
    @GetMapping("/crawlSchedule")
    public void crawlSchedule() {
        log.info("==>定时任务启动，网信办法律法规数据定时拉取！");

        //法律法规
        List<String> lqUrlStr = new ArrayList<>();
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/fl/A09370301index_1.htm"); // 法律
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/xzfg/A09370302index_1.htm"); // 行政法规
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/bmgz/A09370303index_1.htm"); // 部门规章
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/sfjs/A09370304index_1.htm"); // 司法解释
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/gfxwj/A09370305index_1.htm"); // 规范性文件
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/zcwj/A09370306index_1.htm"); // 政策文件
        lqUrlStr.add("https://www.cac.gov.cn/wxzw/zcfg/zcjd/A09370307index_1.htm"); // 政策解读
        lqUrlStr.forEach(url->{
            SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam = new SyncLegalAndRegulatoryDataParam();
            for (int i = 0; i < 1; i++) {
                syncLegalAndRegulatoryDataParam.setTar_page(i + 1);
                syncLegalAndRegulatoryDataParam.setType(1);
                syncLegalAndRegulatoryDataParam.setStart_url(url);
                syncLegalAndRegulatoryDataParam.setTab(url.substring(url.indexOf("zcfg/") + 5, url.lastIndexOf("/")));
                log.info("开始拉取法律法规 中 {} ....", url);
                legalAndRegulatoryDataService.synchronousLegalAndRegulatoryData(syncLegalAndRegulatoryDataParam);
            }
        });
        log.info("法律法规拉取结束....");


        //网信案例库
        List<String> lqUrlStr1 = new ArrayList<>();
        lqUrlStr1.add("https://www.cac.gov.cn/wxzw/hlwnrgl/A093704index_1.htm"); // 网信案例库
        lqUrlStr1.forEach(url->{
            for (int i = 0; i < 1; i++) {
                SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam = new SyncLegalAndRegulatoryDataParam();
                syncLegalAndRegulatoryDataParam.setTar_page(i + 1);
                syncLegalAndRegulatoryDataParam.setType(2);
                syncLegalAndRegulatoryDataParam.setStart_url(url);
                log.info("开始拉取网信案例库 中 {} ....", url);
                legalAndRegulatoryDataService.synchronousLegalAndRegulatoryData(syncLegalAndRegulatoryDataParam);
            }
        });
        log.info("网信案例库 拉取结束....");

        //网信要闻
        List<String> lqUrlStr2 = new ArrayList<>();
        lqUrlStr2.add("https://www.cac.gov.cn/yaowen/wxyw/A093602index_1.htm"); // 网信要闻
        lqUrlStr2.forEach(url->{
            for (int i = 0; i < 1; i++) {
                SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam = new SyncLegalAndRegulatoryDataParam();
                syncLegalAndRegulatoryDataParam.setTar_page(i + 1);
                syncLegalAndRegulatoryDataParam.setType(3);
                syncLegalAndRegulatoryDataParam.setStart_url(url);
                log.info("开始拉取网信要闻 中 {} ....", url);
                legalAndRegulatoryDataService.synchronousLegalAndRegulatoryData(syncLegalAndRegulatoryDataParam);
            }
        });
        log.info("网信要闻 拉取结束....");
        System.out.println("本次拉取结束！！！");
    }
}
