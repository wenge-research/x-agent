package com.wenge.model.vo;

import com.wenge.model.entity.KnowledgeUrlData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/6/19 17:27
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParserUrlDetailVo {
    private String pageUrl;
    private String title;
    private String createTime;
    private String updateTime;
    private Integer wordCount;
    private Integer avgParagraphsCount;
    private Integer paragraphsNum;

    private Long totalRow;
    private Integer totalPage;
    private Integer pageNo;
    private Integer pageSize;

    List<KnowledgeUrlData> dataList;
}