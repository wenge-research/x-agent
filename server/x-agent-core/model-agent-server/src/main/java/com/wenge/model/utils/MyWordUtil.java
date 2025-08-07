package com.wenge.model.utils;


import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.wenge.model.enums.HeadStyleEnum;
import com.wenge.model.enums.ParagraphEnum;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyWordUtil {

    private XWPFDocument document;

    public MyWordUtil(XWPFDocument document) {
        this.document = document;

        HeadStyleEnum[] values = HeadStyleEnum.values();
        for (HeadStyleEnum value : values) {
            addCustomHeadingStyle(document, value.getName(), value.getHeadingLevel());
        }
    }

    /**
     * 替换文档中的内容
     *
     * @param params 待填充的数据
     *               params.put("key",value) 文档中对应为 ${key}
     */
    public void replaceInPara(Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = document.getParagraphsIterator();
        while (iterator.hasNext()) {
            replaceInPara(iterator.next(), params);
        }
    }


    /**
     * 遍历所有表格，替换表格里面的变量
     *
     * @param params 参数
     */
    public void replaceInAllTable(Map<String, Object> params) {
        Iterator<XWPFTable> iterator = document.getTablesIterator();
        while (iterator.hasNext()) {
            replaceInTable(iterator.next(), params);
        }
    }

    /**
     * 替换指定表格中的变量
     *
     * @param tabIndex 表格下标
     * @param params   替换参数
     */
    public void replaceInTable(int[] tabIndex, Map<String, Object> params) {
        List<XWPFTable> tables = document.getTables();
        for (int index : tabIndex) {
            replaceInTable(tables.get(index), params);
        }
    }

    /**
     * 替换表格中的变量
     *
     * @param table
     * @param params
     */
    private void replaceInTable(XWPFTable table, Map<String, Object> params) {
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        //判断表格中是否有 ${} 有就表示需要替换值
        if (matcher(table.getText()).find()) {
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        replaceInPara(para, params);
                    }
                }
            }
        }
    }

    /**
     * 在表格中新增行数并填充数据
     *
     * @param table    需要插入数据的表格
     * @param rowDatas 插入数据集合（注：填充的数据要与单元格的数量保持一致）
     */
    public void insertTableRow(XWPFTable table, List<String[]> rowDatas) {
        for (String[] cellDatas : rowDatas) {
            XWPFTableRow row = table.createRow();
            List<XWPFTableCell> cells = row.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                cells.get(j).setText(cellDatas[j]);
            }
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para   要替换的段落
     * @param params 参数
     */
    private void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        StringBuilder runText = new StringBuilder();

        if (matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            int j = runs.size();
            for (int i = 0; i < j; i++) {
                runText.append(runs.get(0).toString());
                //保留最后一个段落，在这段落中替换值，保留原有段落样式
                if (!((j - 1) == i)) {
                    para.removeRun(0);
                }
            }
            String text = runText.toString();
            Matcher matcher;
            while ((matcher = matcher(text)).find()) {
                text = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
            }
            runs.get(0).setText(text, 0);

        }
    }

    /**
     * 创建标题
     *
     * @param text
     */
    public void createTitle(String text, HeadStyleEnum headStyle) {
        createTitle(text, headStyle, null);
    }

    /**
     * 创建标题
     *
     * @param text
     */
    public void createTitle(String text, HeadStyleEnum headStyle, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle(headStyle.getName());
        XWPFRun createRun = paragraph.insertNewRun(0);
        createRun.setStyle(headStyle.getName());
        createRun.setText(text);
        createRun.setFontFamily("宋体");

        createRun.setFontSize(headStyle.getFontSize());
        createRun.setBold(true);
        paragraph.setSpacingAfter(headStyle.getSpacingAfter());
        paragraph.setSpacingBefore(headStyle.getSpacingBefore());
        //对齐方式
        if (null != alignment) {
            paragraph.setAlignment(alignment);
        } else {
            paragraph.setAlignment(headStyle.getAlignment());
        }
    }

    /**
     * 创建段落
     *
     * @param data
     */
    public void createParagraph(String data, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun createRun = paragraph.createRun();
        createRun.setText(data);
        createRun.setFontFamily("宋体");
        createRun.setFontSize(12);
        // 字间距
        createRun.setCharacterSpacing(12);


        paragraph.setFirstLineIndent(100);
        paragraph.setIndentationFirstLine(600);
        paragraph.setSpacingAfter(10);
        paragraph.setSpacingBefore(10);
        // 行间距
        paragraph.setSpacingBetween(1.5);
        if (null != alignment) {
            paragraph.setAlignment(alignment);
        } else {
            paragraph.setAlignment(ParagraphAlignment.BOTH);
        }

    }

    /**
     * 创建表格
     *
     * @param tableList
     */
    public void createTable(List<List<String>> tableList) {
        XWPFTable table = document.createTable(tableList.size(), tableList.get(0).size());
        //设置表格的宽度
        CTTblWidth comTableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        comTableWidth.setType(STTblWidth.PCT);
        comTableWidth.setW(BigInteger.valueOf(5000));

        // 填充数据
        int length = tableList.size();
        for (int i = 0; i < length; i++) {
            XWPFTableRow row = table.getRow(i);
            List<XWPFTableCell> cells = row.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                cells.get(j).setText(tableList.get(i).get(j));
            }
        }

    }


    /**
     * 饼图
     *
     * @param title      图的标题
     * @param valueTitle 图种类名称
     * @param values     图种类的值
     */
    public void createPieChart(String title, List<String> valueTitle, List<Double> values) {
        try {
            XWPFChart chart = document.createChart(15 * Units.EMU_PER_CENTIMETER, 10 * Units.EMU_PER_CENTIMETER);
            // 标题
            chart.setTitleText(title);
            // 标题是否覆盖图表
            chart.setTitleOverlay(false);

            // 图例位置
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.RIGHT);

            XDDFCategoryDataSource categorys = XDDFDataSourcesFactory.fromArray(valueTitle.stream().toArray(String[]::new));
            XDDFNumericalDataSource<Double> numerical = XDDFDataSourcesFactory.fromArray(values.stream().toArray(Double[]::new));
            XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
            // 设置为可变颜色
            data.setVaryColors(true);
            // 图表加载数据
            data.addSeries(categorys, numerical);
            // 绘制
            chart.plot(data);

            CTDLbls dLbls = chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).addNewDLbls();
            dLbls.addNewShowVal().setVal(false);//不显示值
            dLbls.addNewShowLegendKey().setVal(false);
            dLbls.addNewShowCatName().setVal(false);//类别名称
            dLbls.addNewShowSerName().setVal(false);//不显示系列名称
            dLbls.addNewShowPercent().setVal(true);//显示百分比
            dLbls.addNewShowLeaderLines().setVal(true); //显示引导线
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 柱状图
     *
     * @param title      标题
     * @param xAxisTitle X轴标题
     * @param yAxisTitle Y轴标题
     * @param categorys  种类
     * @param values     值
     */
    public void createBarChart(String title, String xAxisTitle, String yAxisTitle, List<String> categorys, Map<String, List<Number>> values) {
        try {

            XWPFChart chart = document.createChart(15 * Units.EMU_PER_CENTIMETER, 10 * Units.EMU_PER_CENTIMETER);
            // 标题
            chart.setTitleText(title);
            // 标题覆盖
            chart.setTitleOverlay(false);
            // 图例位置
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);
            legend.setOverlay(true);

            //X轴属性
            XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            xAxis.setTitle(xAxisTitle);

            // Y轴属性
            XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
            yAxis.setTitle(yAxisTitle);
            yAxis.setCrosses(AxisCrosses.AUTO_ZERO);
            yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

            XDDFChartData data = chart.createData(ChartTypes.BAR, xAxis, yAxis);
            data.setVaryColors(true);
            ((XDDFBarChartData) data).setBarDirection(BarDirection.COL); // 设置方向为竖状

            XDDFDataSource<String> categoriesData = XDDFDataSourcesFactory.fromArray(categorys.stream().toArray(String[]::new));
            values.forEach((k, v) -> {
                XDDFChartData.Series series = data.addSeries(categoriesData, XDDFDataSourcesFactory.fromArray(v.stream().toArray(Number[]::new)));
                series.setTitle(k, null);
            });
            chart.plot(data);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 折线图
     *
     * @param title      标题
     * @param xAxisTitle X轴标题
     * @param yAxisTitle Y轴标题
     * @param categorys  种类
     * @param values     值
     */
    public void createLineChart(String title, String xAxisTitle, String yAxisTitle, List<String> categorys, Map<String, List<Number>> values) {
        try {
            XWPFChart chart = document.createChart(15 * Units.EMU_PER_CENTIMETER, 10 * Units.EMU_PER_CENTIMETER);

            // 标题
            chart.setTitleText(title);
            // 标题覆盖
            chart.setTitleOverlay(false);
            // 图例位置
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP);
            legend.setOverlay(true);

            //X轴属性
            XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            xAxis.setTitle(xAxisTitle);

            //Y轴属性
            XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
            yAxis.setTitle(yAxisTitle);

            // 折线图，
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, xAxis, yAxis);

            XDDFCategoryDataSource countries = XDDFDataSourcesFactory.fromArray(categorys.stream().toArray(String[]::new));
            // 加载数据
            values.forEach((k, v) -> {
                XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(countries, XDDFDataSourcesFactory.fromArray(v.stream().toArray(Number[]::new)));
                series.setTitle(k, null); // 折线图例标题
                series.setMarkerStyle(MarkerStyle.CIRCLE); // 设置标记样式
            });
            // 绘制
            chart.plot(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 柱状图+折线图
     *
     * @param title      标题
     * @param xAxisTitle X轴标题
     * @param yAxisTitle Y轴标题
     * @param categorys  种类
     */
    public void createBarLineChart(String title, String xAxisTitle, String yAxisTitle, List<String> categorys, Map<String, List<Number>> lineValues, Map<String, List<Number>> barValues) {
        try {
            XWPFChart chart = document.createChart(15 * Units.EMU_PER_CENTIMETER, 10 * Units.EMU_PER_CENTIMETER);

            // 标题
            chart.setTitleText(title);
            // 标题覆盖
            chart.setTitleOverlay(false);
            // 图例位置
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP);
            legend.setOverlay(true);

            //X轴属性
            XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            xAxis.setTitle(xAxisTitle);

            //Y轴属性
            XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
            yAxis.setTitle(yAxisTitle);
            yAxis.setCrosses(AxisCrosses.AUTO_ZERO);
            yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

            // 折线图，
            XDDFLineChartData lineData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, xAxis, yAxis);
            // 柱状图
            XDDFChartData barData = chart.createData(ChartTypes.BAR, xAxis, yAxis);
            barData.setVaryColors(true);
            ((XDDFBarChartData) barData).setBarDirection(BarDirection.COL); // 设置方向为竖状

            XDDFCategoryDataSource countries = XDDFDataSourcesFactory.fromArray(categorys.stream().toArray(String[]::new));
            // 加载数据
            barValues.forEach((k, v) -> {
                XDDFChartData.Series barSeries = barData.addSeries(countries, XDDFDataSourcesFactory.fromArray(v.stream().toArray(Number[]::new)));
                barSeries.setTitle(k, null);

                CTChart ctChart = chart.getCTChart();
                CTPlotArea ctPlotArea = ctChart.getPlotArea();
                CTBarChart ctBarChart = ctPlotArea.getBarChartArray(0);
                CTBarSer ctBarSer = ctBarChart.getSerArray(0);
                CTDLbls newDLbls = ctBarSer.addNewDLbls();//数据标签
                CTBoolean ctBoolean = ctBarChart.addNewVaryColors();// 多样颜色，true 选用category作为图例项，false选用系列作为图例项，最好false
                ctBoolean.setVal(false);
                // 数据标签 显示类别名称
                newDLbls.setShowCatName(ctBoolean);
                // 数据标签 显示序列名称
                newDLbls.setShowSerName(ctBoolean);
                newDLbls.setShowPercent(ctBoolean);
                newDLbls.setShowBubbleSize(ctBoolean);
                newDLbls.setShowLeaderLines(ctBoolean);
                newDLbls.setShowLegendKey(ctBoolean);//图例化标签
                newDLbls.addNewShowVal().setVal(true);//数据标签 显示值
            });

            // 加载数据
            lineValues.forEach((k, v) -> {
                XDDFLineChartData.Series lineSeries = (XDDFLineChartData.Series) lineData.addSeries(countries, XDDFDataSourcesFactory.fromArray(v.stream().toArray(Number[]::new)));
                lineSeries.setTitle(k, null); // 折线图例标题
                lineSeries.setMarkerStyle(MarkerStyle.CIRCLE); // 设置标记样式

//                CTChart ctChart = chart.getCTChart();
//                CTPlotArea ctPlotArea = ctChart.getPlotArea();
//                CTLineChart ctBarChart = ctPlotArea.getLineChartArray(0);
//                CTLineSer ctBarSer = ctBarChart.getSerArray(0);
//                CTDLbls newDLbls = ctBarSer.addNewDLbls();//数据标签
//                CTBoolean ctBoolean = ctBarChart.addNewVaryColors();// 多样颜色，true 选用category作为图例项，false选用系列作为图例项，最好false
//                ctBoolean.setVal(false);
//                // 数据标签 显示类别名称
//                newDLbls.setShowCatName(ctBoolean);
//                // 数据标签 显示序列名称
//                newDLbls.setShowSerName(ctBoolean);
//                newDLbls.setShowPercent(ctBoolean);
//                newDLbls.setShowBubbleSize(ctBoolean);
//                newDLbls.setShowLeaderLines(ctBoolean);
//                newDLbls.setShowLegendKey(ctBoolean);//图例化标签
//                newDLbls.addNewShowVal().setVal(true);//数据标签 显示值

            });
            // 绘制
            chart.plot(lineData);
            chart.plot(barData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public void imageAdd(int width, int height, String imagePath) {
        FileInputStream fileInputStream = null;
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(imagePath));
            XWPFParagraph para = document.createParagraph();
            // 创建图像插入点
            XWPFRun run = para.createRun();
            run.setText(" "); // 在插入点之前添加空格，以使图片居中
            int format = Document.PICTURE_TYPE_PNG; // 图片格式
            int pictureWidth = Units.toEMU(bufferedImage.getWidth()); // 图片宽度（以EMU为单位）
            int pictureHeight = Units.toEMU(bufferedImage.getHeight()); // 图片高度（以EMU为单位）
            fileInputStream = new FileInputStream(imagePath);
            run.addPicture(fileInputStream, format, imagePath, pictureWidth, pictureHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    private void insertImage(XWPFParagraph paragraph, String imagePath, XWPFDocument document) throws IOException {
//        FileInputStream imageStream = new FileInputStream(imagePath);
//        XWPFPicture xwpfPicture = document.addPictureData(imageStream, XWPFPictureData.PICTURE_TYPE_JPEG);
//        imageStream.close();
//
//        int width = xwpfPicture.getWidth();
//        int height = xwpfPicture.getHeight();
//
//        // 如果需要调整图片大小，可以使用Units.toEMU方法
//        int targetWidth = Units.toEMU(200); // 200 像素
//        int targetHeight = Units.toEMU(150); // 150 像素
//
//        if (width > targetWidth || height > targetHeight) {
//            xwpfPicture.setWidth(targetWidth);
//            xwpfPicture.setHeight(targetHeight);
//        }
//
//        // 插入图片到段落
//        String blipId = paragraph.getDocument().addPictureData(imageStream, XWPFPictureData.PICTURE_TYPE_JPEG);
//        document.createPicture(blipId, document.getNextPicNameNumber(XWPFPicture.PICTURE_TYPE_JPEG), targetWidth, targetHeight);
//    }

    /**
     * 增加自定义标题样式。这里用的是stackoverflow的源码
     *
     * @param docxDocument 目标文档
     * @param strStyleId 样式名称
     * @param headingLevel 样式级别
     */
    public static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        // style shows up in the formats bar
        ctStyle.setQFormat(onoffnull);

        // style defines a heading of the given level
        CTPPrGeneral ppr = CTPPrGeneral.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);
        //ctStyle.setPPr(ppr);
        //CTPPrGeneral ctpPrGeneral = ctStyle.addNewPPr();
        //ctpPrGeneral.setOutlineLvl(indentNumber);

        XWPFStyle style = new XWPFStyle(ctStyle);
        // is a null op if already defined
        XWPFStyles styles = docxDocument.createStyles();

        style.setType(STStyleType.PARAGRAPH);

        styles.addStyle(style);

    }

    public void createWord(List<WordBean> wordBeanList) {
        for (WordBean wordBean : wordBeanList) {
            switch (wordBean.getType()) {
                case TITLE:
                    createTitle(wordBean.getText(), wordBean.getTitleLevel(), wordBean.getAlignment());
                    break;
                case TEXT:
                    createParagraph(wordBean.getText(), wordBean.getAlignment());
                    break;
                case PIE:
                    createPieChart(wordBean.getCharTitle(), wordBean.getPieTitleList(), wordBean.getPieDataList());
                    break;
                case LINE:
                    createLineChart(wordBean.getCharTitle(), wordBean.getLineXAxisTitle(), wordBean.getLineYAxisTitle(), wordBean.getQuarterList(), wordBean.getLineMapData());
                    break;
                case BAR:
                    createBarChart(wordBean.getCharTitle(), wordBean.getLineXAxisTitle(), wordBean.getLineYAxisTitle(), wordBean.getQuarterList(), wordBean.getBarMapData());
                    break;
                case LINE_BAR:
                    createBarLineChart(wordBean.getCharTitle(), wordBean.getLineXAxisTitle(), wordBean.getLineYAxisTitle(), wordBean.getQuarterList(), wordBean.getLineMapData(), wordBean.getBarMapData());
                    break;
                case TABLE:
                    createTable(wordBean.getTableDataList());
                    break;
                case IMAGE:
                    imageAdd(wordBean.getImgWidth(), wordBean.getImgHeight(), wordBean.getImagePath());
                    break;

                default:
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 创建一个新的文档
        XWPFDocument document = new XWPFDocument();

        List<WordBean> wordBeanList = Lists.newArrayList();
        WordBean title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_1);
        title.setText("龙华区现行幼儿园收费备案价格分析报告");
        wordBeanList.add(title);

        WordBean text = new WordBean();
        text.setType(ParagraphEnum.TEXT);
        text.setText("根据《深圳市发展和改革委员会、深圳市教育局关于印发<深圳市民办幼儿园收费备案指引>的通知》（深发改〔2019〕243号，以下简称《指引》），区教育局向我局提出幼儿园收费备案初审意见，我局提出备案意见。为规范幼儿园价格备案管理，现将龙华区现有幼儿园价格进行分析如下：");
        wordBeanList.add(text);

        title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_2);
        title.setText("一、龙华区幼儿园总体情况");
        wordBeanList.add(title);

        text = new WordBean();
        text.setType(ParagraphEnum.TEXT);
        text.setText("截至2023年6月，龙华区共有幼儿园246所，其中公办园133所，民办园113所。在等级幼儿园建设方面，省一级幼儿园7所、市一级幼儿园93所、区一级幼儿园118所，优质园率为42.7%，规范化园率为95.1%。各街道幼儿园园所数：大浪街道44、福城街道31、观湖街道38、观澜街道24、龙华街道51、民治街道58，总计246所。\r\n" +
                "为综合分析截止2023年6月价格备案的总体情况，考虑把截止2023年6月完成备案251的所幼儿园备案价格作为参照依据(其中有1所为首次备案的幼儿园，有2所为无需备案的分园幼儿园，有5所为已备案2023年下半年开园的幼儿园）。参考数据剔除观澜湖幼儿园（保教费为9950元/生/月）、妙百睿幼儿园（保教费为6200元/生/月）、卓乐第二幼儿园（保教费为3400元/生/月）3家幼儿园的极值数据，同时剔除1所首次备案幼儿园和2所无需备案的分园幼儿园，总样本245所。其中，大浪街道43所、福城街道30所、观湖街道39所、观澜街道23所、龙华街道51所、民治街道59所。");
        wordBeanList.add(text);


        title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_2);
        title.setText("二、价格分析");
        wordBeanList.add(title);

        title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_3);
        title.setText("（一）保教费");
        wordBeanList.add(title);

        title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_4);
        title.setText("1.总体情况");
        wordBeanList.add(title);

        text = new WordBean();
        text.setType(ParagraphEnum.TEXT);
        text.setText("从全区幼儿园保教费的总体情况看，2023年上半年全区幼儿园保教费位于680-3200元/生/月范围内，平均水平为1084元/生/月（2022年为1061元/生/月）,较2022年增长2.17%。分街道看，大浪街道、龙华街道、民治街道的保教费平均水平较高，观澜街道、福城街道、观湖街道的保教费平均水平较低。");
        wordBeanList.add(text);


        WordBean paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.TABLE);
        List<List<String>> table2 = new ArrayList<>();
        table2.add(ListUtil.toList("", "全区", "民治街道", "龙华街道", "大浪街道", "福城街道", "观澜街道", "观湖街道"));
        paragraph.setTableDataList(table2);
        wordBeanList.add(paragraph);

        paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.PIE);
        paragraph.setPieTitleList(ListUtil.toList("680(含)≤1000（不含）", "1000(含)≤2000（不含）", "2000(含)≤3500（不含）"));
        paragraph.setPieDataList(ListUtil.toList(64.9, 30.6, 4.5));
        paragraph.setCharTitle("全区幼儿园保教费分布情况");
        wordBeanList.add(paragraph);


        paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.LINE);
        paragraph.setLineXAxisTitle("保教费（元）");
        paragraph.setLineYAxisTitle("数量（所）");
        paragraph.setQuarterList(ListUtil.toList("680", "810", "820", "890", "900", "901", "902", "903", "904", "905", "906", "907"));
        Map<String, List<Number>> map = new HashMap<>();
        map.put("", ListUtil.toList(6688, 4399, 5327, 6379, 6688, 4399, 5327, 6379, 6688, 4399, 5327, 6379));
        paragraph.setLineMapData(map);
        paragraph.setCharTitle("全区幼儿园保教费分布情况");
        wordBeanList.add(paragraph);

        paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.BAR);
        paragraph.setLineXAxisTitle("保教费（元）");
        paragraph.setLineYAxisTitle("数量（所）");
        paragraph.setQuarterList(ListUtil.toList("680", "810", "820", "890", "900", "901", "902", "903", "904", "905", "906", "907"));
        paragraph.setBarMapData(map);
        paragraph.setCharTitle("全区幼儿园保教费分布图");
        wordBeanList.add(paragraph);


        paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.LINE_BAR);
        paragraph.setLineXAxisTitle("保教费（元）");
        paragraph.setLineYAxisTitle("数量（所）");
        paragraph.setQuarterList(ListUtil.toList("680", "810", "820", "890", "900", "901", "902", "903", "904", "905", "906", "907"));
        paragraph.setLineMapData(map);
        paragraph.setBarMapData(map);
        paragraph.setCharTitle("全区幼儿园保教费分布图");
        wordBeanList.add(paragraph);

        paragraph = new WordBean();
        paragraph.setType(ParagraphEnum.IMAGE);
        paragraph.setImgWidth(100);
        paragraph.setImgHeight(100);
        paragraph.setImagePath("D:\\work\\gitlab\\conomic-delivery\\city-project\\longyangpt\\longyan-gpt-server\\1111122.png");
        wordBeanList.add(paragraph);

        MyWordUtil myWordUtil = new MyWordUtil(document);
        myWordUtil.createWord(wordBeanList);

        // 保存文档到文件
        try (FileOutputStream out = new FileOutputStream("D:\\work\\gitlab\\conomic-delivery\\city-project\\longyangpt\\longyan-gpt-server\\4444.docx")) {
            String content = "今天是个好日子";

            Map<String, Object> params = new HashMap<>();
            params.put("title", "真不错");
            params.put("content", content);
            params.put("header1", "大家都开心");
            params.put("header2", "好的");
            params.put("header3", "How are you?");
            params.put("header4", "fine");
            params.put("header5", "thank you!");

            List<String[]> table1 = new ArrayList<>();
            table1.add(new String[]{"1", "11", "22", "33", "44"});
            table1.add(new String[]{"2", "55", "66", "77", "88"});
            table1.add(new String[]{"2", "99", "1010", "1111", "1212"});

//			List<String[]> table2 = new ArrayList<>();
//			table2.add(new String[]{"1", "1313", "1414", "1515"});
//			table2.add(new String[]{"2", "1616", "1717", "1818"});
//			table2.add(new String[]{"3", "1919", "2020", "2121"});
            //饼图数据
//			String[] title = new String[]{"1", "2", "3", "4", "5"};
//			Double[] value = new Double[]{70.34, 40.5, 54.9, 334.8, 546.2};
            // 柱状图、折线图 数据

//			String[] quarter = new String[]{"第一年", "第二年", "第三年", "第四年"};
//			Map<String, Number[]> map = new HashMap<>();
//			map.put("姜文", new Integer[]{6688, 4399, 5327, 6379});
//			map.put("周润发", new Integer[]{6799, 7499, 6429, 7379});
//			map.put("葛优", new Integer[]{5899, 6599, 7665, 8573});
//			System.out.println(System.getProperty("user.dir"));
//				String inPath = System.getProperty("user.dir") + "\\poi_demo\\src\\main\\resources\\template\\Template.docx";
//				String outPath = "D:\\word文件.docx";



//			MyWordUtil.addCustomHeadingStyle(document, "1", 1);




//			XWPFParagraph paragraph1 = document.createParagraph();


            // 替换段落中的参数
//			WordUtil.replaceInPara(document, params);
            // 替换表格中的参数
//			WordUtil.replaceInTable(document, new int[]{0}, params);
            // 给表格追加记录
//			WordUtil.insertTableRow(document.getTableArray(0), table1);
            // 创建表格
//			WordUtil.createTitle(document.createParagraph(), "二、表格二");

//			WordUtil.createTable(document, table2);
//			// 创建饼图
//			WordUtil.createTitle(document.createParagraph(), "三、饼图");
//			WordUtil.createPieChart(document, "豆瓣评分", title, value);
//
//			WordUtil.createTitle(document.createParagraph(), "四、柱形图");
//			// 创建柱状图
//			WordUtil.createBarChart(document, "标题", null, null, quarter, map);
//			WordUtil.createTitle(document.createParagraph(), "五、条形图");
//			// 创建柱状图
//			WordUtil.createLineChart(document, "标题", "季度", "数值", quarter, map);
//			WordUtil.createTitle(document.createParagraph(), "六、混合图");
//			// 创建柱状图
//			WordUtil.createBarLineChart(document, "标题", null, null, quarter, map);
//			document.write(os);

//			createTableOfContents(document);



            document.write(out);
            System.out.println("文档已创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTableOfContents(XWPFDocument document) {
        // 添加目录索引
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFParagraph toc = document.createParagraph();
        CTP ctp = toc.getCTP();
        CTSimpleField tocField = ctp.addNewFldSimple();
        tocField.setInstr("TOC \\o \"1-3\" \\h \\z \\u");


//		Map<String, Integer> headingPageMap = new HashMap<>();
//
//		// 获取文档中的所有段落
//		List<XWPFParagraph> paragraphs = document.getParagraphs();
//
//		// 记录每个标题出现的页码
//		int currentPage = 1;
//		for (XWPFParagraph paragraph : paragraphs) {
//			String style = paragraph.getStyle();
//			if (style != null && style.startsWith("标题 1-")) {
//				headingPageMap.put(paragraph.getText(), currentPage);
//			}
//			// 模拟文档内容增加，这里简单地以每个段落为一页
//			currentPage++;
//		}
//
//		// 添加目录
//		XWPFParagraph tocParagraph = document.createParagraph();
//		XWPFRun tocRun = tocParagraph.createRun();
//		tocRun.setText("Table of Contents");
//		tocRun.addCarriageReturn();
//
//		// 遍历记录的标题信息，将标题和页码添加到目录
//		for (Map.Entry<String, Integer> entry : headingPageMap.entrySet()) {
//			String text = entry.getKey();
//			int pageNumber = entry.getValue();
//			String tocEntry = getTocEntry(text, pageNumber);
//			tocRun.setText(tocEntry, 0);
//			tocRun.addCarriageReturn();
//		}
    }

    private static String getTocEntry(String text, int pageNumber) {
        return text + " - Page " + pageNumber;
    }

    private void addTable(XWPFDocument document, List<List<String>> tableData) {
        XWPFTable table = document.createTable();

        for (List<String> rowData : tableData) {
            XWPFTableRow row = table.createRow();
            for (String cellData : rowData) {
                XWPFTableCell cell = row.createCell();
                cell.setText(cellData);
            }
        }
    }


    private void addChart(XWPFDocument document, List<Double> chartData) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFChart chart = document.createChart();


        XWPFRun run = paragraph.createRun();

        // 添加图表数据
//		PackagePart xwpfChart = new ZipPackagePart();
//		new XWPFChartFactory();
//		XWPFChart chart = XWPFChartFactory.createBarChart(document, run, XWPFChartFactory.CHART_STYLE_BAR, 300, 300, true);
//		XWPFChartSeries series = chart.createSeries();
//		series.addCategoryData(new XWPFChartCategoryData("数据", chartData));
//
//		// 设置图表轴和图例
//		XWPFChartAxis bottomAxis = chart.getBottomAxis();
//		bottomAxis.setTitle("X轴");
//
//		XWPFChartAxis leftAxis = chart.getLeftAxis();
//		leftAxis.setTitle("Y轴");
//
//		XWPFChartLegend legend = chart.getLegend();
//		legend.setPosition(XWPFChartLegend.Position.BOTTOM);

        // 如果需要添加图片，可以使用以下代码
        // byte[] imageData = ... // 从文件或其他来源获取图像数据
        // XWPFPicture picture = run.addPicture(imageData, XWPFPicture.DEFAULT_PICTURE_TYPE, "image.png", Units.toEMU(200), Units.toEMU(200));

        // 或者，如果图像数据已经添加到文档中，可以使用以下代码
        // XWPFPictureData pictureData = document.addPictureData(imageData, XWPFPicture.DEFAULT_PICTURE_TYPE);
        // String relationshipId = document.addPicture(pictureData, XWPFPicture.DEFAULT_PICTURE_TYPE);
        // XWPFPicture picture = run.addPicture(relationshipId, Units.toEMU(200), Units.toEMU(200));
    }


    public static void replaceChart(POIXMLDocumentPart poixmlDocumentPart,
                                    List<String> titleArr, List<String> fldNameArr, List<Map<String, String>> listItemsByType) {
        XWPFChart chart = (XWPFChart) poixmlDocumentPart;
        chart.getCTChart();

        //根据属性第一列名称切换数据类型
        CTChart ctChart = chart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        //柱状图
        CTBarChart barChart = plotArea.getBarChartArray(0);
        List<CTBarSer> BarSerList = barChart.getSerList();  // 获取柱状图单位

        //折线图
//		CTLineChart lineChart = plotArea.getLineChartArray(0);
//		List<CTLineSer> lineSerList = lineChart.getSerList();   // 获取折线图单位


        //刷新柱状图
        refreshStrGraphContent(barChart, BarSerList, listItemsByType, fldNameArr, 1);

        //刷新折线图
//		refreshGraphContent(lineChart, lineSerList, listItemsByType, fldNameArr, 1);
    }


    public static void refreshStrGraphContent(Object typeChart,
                                              List<?> serList, List<Map<String, String>> dataList, List<String> fldNameArr, int position) {
        //更新数据区域
        for (int i = 0; i < serList.size(); i++) {
            CTAxDataSource cat = null;
            CTNumDataSource val = null;
            //折线图
            CTLineSer ser = ((CTLineChart) typeChart).getSerArray(i);
            //柱状图
            //CTBarSer ser = ((CTBarChart) typeChart).getSerArray(i);

            cat = ser.getCat();
            // 获取图表的值
            val = ser.getVal();
            CTStrData strData = cat.getStrRef().getStrCache();
            CTNumData numData = val.getNumRef().getNumCache();
            strData.setPtArray((CTStrVal[]) null);
            numData.setPtArray((CTNumVal[]) null);

            long idx = 0;
            for (int j = 0; j < dataList.size(); j++) {
                //判断获取的值是否为空
                String value = "0";
                if (dataList.get(j).get(fldNameArr.get(i + position)) != null) {
                    value = new BigDecimal(dataList.get(j).get(fldNameArr.get(i + position))).toString();
                }

                CTNumVal numVal = numData.addNewPt();//序列值
                numVal.setIdx(idx);
                numVal.setV(value);

                CTStrVal sVal = strData.addNewPt();//序列名称
                sVal.setIdx(idx);
                sVal.setV(dataList.get(j).get(fldNameArr.get(0)));
                idx++;
            }
            numData.getPtCount().setVal(idx);
            strData.getPtCount().setVal(idx);

        }
    }

    /**
     * 饼图
     *
     * @param document
     * @param title      图的标题
     * @param valueTitle 图种类名称
     * @param values     图种类的值
     */
    public static void createPieChart(XWPFDocument document, String title, String[] valueTitle, Double[] values) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {

        XWPFChart chart = document.createChart(15 * Units.EMU_PER_CENTIMETER, 10 * Units.EMU_PER_CENTIMETER);
        // 标题
        chart.setTitleText(title);
        // 标题是否覆盖图表
        chart.setTitleOverlay(false);

        // 图例位置
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryDataSource categorys = XDDFDataSourcesFactory.fromArray(valueTitle);

        XDDFNumericalDataSource<Double> numerical = XDDFDataSourcesFactory.fromArray(values);

        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        // 设置为可变颜色
        data.setVaryColors(true);
        // 图表加载数据
        data.addSeries(categorys, numerical);
        // 绘制
        chart.plot(data);

        CTDLbls dLbls = chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).addNewDLbls();
        dLbls.addNewShowVal().setVal(false);//不显示值
        dLbls.addNewShowLegendKey().setVal(false);
        dLbls.addNewShowCatName().setVal(true);//类别名称
        dLbls.addNewShowSerName().setVal(false);//不显示系列名称
        dLbls.addNewShowPercent().setVal(true);//显示百分比
        dLbls.addNewShowLeaderLines().setVal(true); //显示引导线

    }
}
