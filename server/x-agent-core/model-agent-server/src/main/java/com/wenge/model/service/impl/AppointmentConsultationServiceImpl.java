package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.AppointmentConsultationParam;
import com.wenge.model.dto.template.AppointmentConsultationDTO;
import com.wenge.model.entity.AppointmentConsultation;
import com.wenge.model.mapper.AppointmentConsultationMapper;
import com.wenge.model.service.AppointmentConsultationService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wenge.model.entity.table.AppointmentConsultationTableDef.APPOINTMENT_CONSULTATION;

@Slf4j
@Service
public class AppointmentConsultationServiceImpl extends ServiceImpl<AppointmentConsultationMapper,
        AppointmentConsultation> implements AppointmentConsultationService {

    // 中国大陆手机号（11位，以13/14/15/17/18/19开头）
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    // 邮箱格式（基本格式：用户名@域名）
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Map<Integer, String> COMPANY_TYPE_MAP = new HashMap<>();

    static {
        COMPANY_TYPE_MAP.put(1, "初创公司");
        COMPANY_TYPE_MAP.put(2, "小微公司");
        COMPANY_TYPE_MAP.put(3, "中型公司");
        COMPANY_TYPE_MAP.put(4, "大型公司");
        COMPANY_TYPE_MAP.put(5, "国有企业");   // 新增
        COMPANY_TYPE_MAP.put(6, "政府部门");   // 新增
    }

    @Autowired
    private AppointmentConsultationMapper appointmentConsultationMapper;

    @Override
    public Result add(AppointmentConsultation appointmentConsultation) {
        // 参数验证
        if (appointmentConsultation == null) return Result.fail("参数为空");
        // 手机号验证
        String phone = appointmentConsultation.getPhone();
        if (phone != null && !phone.matches(PHONE_REGEX)) return Result.fail("手机号格式错误");
        // 邮箱验证
        String email = appointmentConsultation.getEmail();
        if (email != null && !email.matches(EMAIL_REGEX)) return Result.fail("邮箱格式错误");

        AppointmentConsultation updateAppointmentConsultation = new AppointmentConsultation();
        BeanUtils.copyProperties(appointmentConsultation, updateAppointmentConsultation);
        updateAppointmentConsultation.setConsultId(IdUtil.simpleUUID());
        appointmentConsultationMapper.insert(updateAppointmentConsultation, true);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result setStatus(AppointmentConsultationParam param) {
        // 参数验证
        if (param == null) return Result.fail("参数为空");
        // ID验证
        if (ObjectUtil.isEmpty(param.getId())) return Result.fail("id为空");
        // 状态验证
        if (ObjectUtil.isEmpty(param.getStatus()) || param.getStatus() < 0 || param.getStatus() > 2)
            return Result.fail("状态为空或错误");
        Long appointmentConsultId = param.getId();
        AppointmentConsultation appointmentConsultation = getById(appointmentConsultId);
        appointmentConsultation.setStatus(param.getStatus());
        appointmentConsultation.setHandler(AppContextHolder.getAccountName());
        appointmentConsultation.setHandlerId(String.valueOf(AppContextHolder.getTokenUserInfo().getId()));
        appointmentConsultation.setUpdateTime(DateUtil.getCurrentTime());
        appointmentConsultationMapper.update(appointmentConsultation);
        return Result.success("修改状态成功");
    }

    @Override
    public Result<Page<AppointmentConsultation>> getAppointmentConsultations(AppointmentConsultationParam param) {
        if (param == null) {
            return Result.fail("参数错误");
        }
        if (param.getPageNo() == null) {
            param.setPageNo(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        // 获取搜索时间范围
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNotBlank(param.getEndTime())) {
                endDate = format.parse(param.getEndTime());
            }
            if (StringUtils.isNotBlank(param.getStartTime())) {
                startDate = format.parse(param.getStartTime());
            }
        } catch (ParseException e) {
            log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
            e.printStackTrace();
        }
        Wrappers wrappers = Wrappers.init()
                .and(StringUtils.isNotBlank(param.getPhone()), APPOINTMENT_CONSULTATION.PHONE.like(param.getPhone())) // 根据电话号码查询
                .and(StringUtils.isNotBlank(param.getCompanyName()), APPOINTMENT_CONSULTATION.COMPANY_NAME.eq(param.getCompanyName())) // 根据公司名称查询
                .and(ObjectUtil.isNotEmpty(param.getCompanyType()), APPOINTMENT_CONSULTATION.COMPANY_TYPE.le(param.getCompanyType())) // 根据公司类型查询
                .and(StringUtils.isNotBlank(param.getHandler()), APPOINTMENT_CONSULTATION.HANDLER.eq(param.getHandler())) // 根据表单处理人查询
                .and(StringUtils.isNotBlank(param.getEndTime()), APPOINTMENT_CONSULTATION.CREATE_TIME.le(endDate)) // 时间范围查询
                .and(StringUtils.isNotBlank(param.getStartTime()), APPOINTMENT_CONSULTATION.CREATE_TIME.ge(startDate))
                .orderBy(APPOINTMENT_CONSULTATION.CREATE_TIME.desc());
        Page<AppointmentConsultation> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public void export(AppointmentConsultationParam param, HttpServletResponse response) {
        param.setPageNo(1);
        param.setPageSize(Integer.MAX_VALUE);
        final Result<Page<AppointmentConsultation>> result = this.getAppointmentConsultations(param);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Page<AppointmentConsultation> page = result.getData();
        List<AppointmentConsultation> list = new ArrayList<>();
        if (page != null) {
            list = page.getRecords();
        }
        String fileName = "合作咨询.xlsx";
        List<AppointmentConsultationDTO> res = new ArrayList<>();
        AtomicInteger serialNumber = new AtomicInteger(1);

        list.forEach(item -> {
            res.add(new AppointmentConsultationDTO(
                    "" + (serialNumber.getAndIncrement()),
                    item.getCreateTime(),
                    item.getBusinessScenarios(),
                    item.getName(),
                    item.getPhone(),
                    item.getEmail(),
                    item.getCompanyName(),
                    item.getCompanyWebsite(),
                    COMPANY_TYPE_MAP.getOrDefault(item.getCompanyType(), "未知类型"),
                    item.getPosition()
            ));
        });
        process(res, fileName, AppointmentConsultationDTO.class, response);
    }

    private void process(List<?> target, String fileName, Class<?> clazs, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        WriteCellStyle globalStyle = new WriteCellStyle();
        globalStyle.setBorderLeft(BorderStyle.THIN); // 边框样式
        globalStyle.setBorderRight(BorderStyle.THIN);
        globalStyle.setBorderTop(BorderStyle.THIN);
        globalStyle.setBorderBottom(BorderStyle.THIN);
        globalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // 背景颜色
        globalStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        globalStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 水平居中
        globalStyle.setWrapped(true);

        try {
            EasyExcel.write(response.getOutputStream(), clazs)
                    .registerWriteHandler(new HorizontalCellStyleStrategy(globalStyle, globalStyle))
                    .registerWriteHandler(new CustomColumnWidthStrategy())
                    .excelType(ExcelTypeEnum.XLS)
                    .autoCloseStream(Boolean.TRUE)
                    .sheet("合作咨询报表")
                    .doWrite(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CustomColumnWidthStrategy extends AbstractColumnWidthStyleStrategy {

        private static final int BUSINESS_CONTENT_COLUMN_INDEX = 2; // 业务内容列的索引，索引从0开始，2是业务场景
        private static final int BUSINESS_CONTENT_WIDTH = 100 * 256; // 100 字符宽度
        private final Set<Integer> processedColumns = new HashSet<>(); // 防止重复设置列宽

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
            Sheet sheet = writeSheetHolder.getSheet();
            int columnIndex = cell.getColumnIndex();

            // 避免重复设置列宽
            if (processedColumns.contains(columnIndex)) {
                return;
            }

            // “业务内容”列，设置固定宽度
            if (columnIndex == BUSINESS_CONTENT_COLUMN_INDEX) {
                sheet.setColumnWidth(columnIndex, BUSINESS_CONTENT_WIDTH);
                processedColumns.add(columnIndex);
            }

            // 其他列：设置最小宽度
            else {
                sheet.setColumnWidth(columnIndex, 15 * 256);
                processedColumns.add(columnIndex);
            }
        }
    }
}
