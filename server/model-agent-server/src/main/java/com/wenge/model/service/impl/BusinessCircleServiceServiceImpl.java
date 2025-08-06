package com.wenge.model.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.LhmzParam;
import com.wenge.model.dto.result.ContactInfoResult;
import com.wenge.model.dto.result.PolicyRewardInfoResult;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.*;
import com.wenge.model.service.BusinessCircleService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mybatisflex.core.paginate.Page;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import static com.wenge.model.entity.table.ApplicationBusinessRelationTableDef.APPLICATION_BUSINESS_RELATION;
import static com.wenge.model.entity.table.IndustrialSpace2TableDef.INDUSTRIAL_SPACE2;
import static com.wenge.model.entity.table.InvestmentLeadTableDef.INVESTMENT_LEAD;
import static com.wenge.model.entity.table.LhmzMessageTableDef.LHMZ_MESSAGE;
import static com.wenge.model.entity.table.PolicyRewardInfoTableDef.POLICY_REWARD_INFO;
import static com.wenge.model.entity.table.SupermarketInfoTableDef.SUPERMARKET_INFO;


@Service
public class BusinessCircleServiceServiceImpl extends ServiceImpl<BusinessCircleMapper,
        BusinessCircle> implements BusinessCircleService {

    @Autowired
    private IndustrialSpaceMapper industrialSpaceMapper;
    @Autowired
    private IndustrialSpace2Mapper industrialSpace2Mapper;

    @Autowired
    private AffordableHousingProjectMapper affordableHousingProjectMapper;

    @Autowired
    private BankBranchMapper bankBranchMapper;

    @Autowired
    private RealEstateSaleMapper realEstateSaleMapper;

    @Autowired
    private InvestmentLeadMapper investmentLeadMapper;

    @Autowired
    private SupermarketInfoMapper supermarketInfoMapper;

    @Autowired
    private PolicyRewardInfoMapper policyRewardInfoMapper;

    @Autowired
    private LhmzMessageMapper messageMapper;

    @Autowired
    private ApplicationBusinessRelationMapper  applicationBusinessRelationMapper;

    @Override
    public Result replaceR() {
//        List<BusinessCircle> businessCircles = list();
//        businessCircles.forEach(businessCircle -> {
//
//            businessCircle.setCircleName(businessCircle.getCircleName().replaceAll("\\r", ""));
//
//        });
//        updateBatch(businessCircles);
//
//
        List<IndustrialSpace2> industrialSpaces2 = industrialSpace2Mapper.selectAll();
//        String subFix = ".png";
        industrialSpaces2.forEach(industrialSpace -> {
//            industrialSpace.setIndustryPic(preFix + industrialSpace.getSerialNumber() + subFix);
//            industrialSpace.setLatitude(industrialSpace.getLatitude().replaceAll("\\r", ""));
//            industrialSpace.setLongitude(industrialSpace.getLongitude().replaceAll("\\r", ""));
//
            try {
                cleanStringFields(industrialSpace);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            industrialSpace2Mapper.update(industrialSpace);
        });

        List<SupermarketInfo> supermarketInfos = supermarketInfoMapper.selectAll();
        supermarketInfos.forEach(supermarketInfo -> {
            try {
                cleanStringFields(supermarketInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
//            supermarketInfo.setLatitude(supermarketInfo.getLatitude().replaceAll("\\r", ""));
//            supermarketInfo.setLongitude(supermarketInfo.getLongitude().replaceAll("\\r", ""));
            supermarketInfoMapper.update(supermarketInfo);
        });
//
//        List<BankBranch> bankBranches = bankBranchMapper.selectAll();
//        bankBranches.forEach(bankBranch -> {
//            bankBranch.setBranchName(bankBranch.getBranchName().replaceAll("\\r", ""));
//            bankBranch.setAddress(bankBranch.getAddress().replaceAll("\\r", ""));
//            bankBranch.setContactPerson(bankBranch.getContactPerson().replaceAll("\\r", ""));
//            bankBranch.setContactPhone(bankBranch.getContactPhone().replaceAll("\\r", ""));
//            bankBranchMapper.update(bankBranch);
//        });
//
//        List<AffordableHousingProject> affordableHousingProjects = affordableHousingProjectMapper.selectAll();
//        affordableHousingProjects.forEach(affordableHousingProject -> {
//            affordableHousingProject.setHousingType(affordableHousingProject.getHousingType().replaceAll("\\r", ""));
//            affordableHousingProject.setAddress(affordableHousingProject.getAddress().replaceAll("\\r", ""));
//            affordableHousingProject.setCompletionDate(affordableHousingProject.getCompletionDate().replaceAll("\\r", ""));
//            affordableHousingProject.setProjectName(affordableHousingProject.getProjectName().replaceAll("\\r", ""));
//            affordableHousingProject.setTotalFloorArea(affordableHousingProject.getTotalFloorArea().replaceAll("\\r", ""));
//            affordableHousingProject.setNumberOfUnits(affordableHousingProject.getNumberOfUnits().replaceAll("\\r", ""));
//            affordableHousingProjectMapper.update(affordableHousingProject);
//        });
//
//        List<RealEstateSale> realEstateSales = realEstateSaleMapper.selectAll();
//        realEstateSales.forEach(realEstateSale -> {
//            realEstateSale.setSalesDate(realEstateSale.getSalesDate().replaceAll("\\r", ""));
//            realEstateSale.setTotalFloorArea(realEstateSale.getTotalFloorArea().replaceAll("\\r", ""));
//            realEstateSale.setNumberOfUnitsSold(realEstateSale.getNumberOfUnitsSold().replaceAll("\\r", ""));
//            realEstateSale.setProjectName(realEstateSale.getProjectName().replaceAll("\\r", ""));
//            realEstateSale.setUnitName(realEstateSale.getUnitName().replaceAll("\\r", ""));
//            realEstateSaleMapper.update(realEstateSale);
//        });
////        List<PolicyRewardInfo> policyRewardInfos = policyRewardInfoMapper.selectAll();
//        policyRewardInfos.forEach(policyRewardInfo -> {
//            policyRewardInfo.setRegionLevel(policyRewardInfo.getRegionLevel().replaceAll("\\r", "").trim());
//        });

        return Result.success();
    }

    @Override
    public List<IndustrialSpace> getAllIndustrialSpaces(LhmzParam lhmzParam) {
        return industrialSpaceMapper.selectAll();
    }

    @Override
    public List<IndustrialSpace2> getAllIndustrialSpaces2(LhmzParam lhmzParam) {
        Wrappers wrappers = Wrappers.init()
                .where(StringUtils.isNotBlank(lhmzParam.getPurpose()),
                        INDUSTRIAL_SPACE2.PURPOSE.like(lhmzParam.getPurpose()))
                .and(StringUtils.isNotBlank(lhmzParam.getIndustryName()), INDUSTRIAL_SPACE2.INDUSTRY_NAME.like(lhmzParam.getIndustryName()))
                .and(StringUtils.isNotBlank(lhmzParam.getLatitude()), INDUSTRIAL_SPACE2.LATITUDE.like(lhmzParam.getLatitude()))
                .and(StringUtils.isNotBlank(lhmzParam.getLongitude()), INDUSTRIAL_SPACE2.LONGITUDE.like(lhmzParam.getLongitude()))
                .and(StringUtils.isNotBlank(lhmzParam.getNearbySubway()), INDUSTRIAL_SPACE2.NEARBY_SUBWAY.eq(lhmzParam.getNearbySubway()))
                .and(StringUtils.isNotBlank(lhmzParam.getMaxTotalArea()), INDUSTRIAL_SPACE2.TOTAL_AREA.le(lhmzParam.getMaxTotalArea()))
                .and(StringUtils.isNotBlank(lhmzParam.getMinTotalArea()), INDUSTRIAL_SPACE2.TOTAL_AREA.ge(lhmzParam.getMinTotalArea()))
                .and(StringUtils.isNotBlank(lhmzParam.getRent()), INDUSTRIAL_SPACE2.RENT.ge(lhmzParam.getRent()))


                .orderBy(INDUSTRIAL_SPACE2.SERIAL_NUMBER.asc(), INDUSTRIAL_SPACE2.ID.asc());
        List<IndustrialSpace2> industrialSpace2s = industrialSpace2Mapper.selectListByQuery(wrappers);
        List<IndustrialSpace2> finalIndustrialSpace2s = Lists.newArrayList();
        industrialSpace2s.forEach(industrialSpace2 -> {
            boolean flag = true;
//            if (StringUtils.isNotBlank(industrialSpace2.getTotalArea())
//                    && StringUtils.isNotBlank(lhmzParam.getMinTotalArea())
//                    && StringUtils.isNotBlank(lhmzParam.getMaxTotalArea())) {
//                String[] split = industrialSpace2.getTotalArea().split("\\|");
//                if (split.length>0) {
//                    flag = isRangeContained(lhmzParam.getMinTotalArea(), lhmzParam.getMaxTotalArea(),
//                            split[0], split[split.length-1]);
//                }
//            }
            if (StringUtils.isNotBlank(industrialSpace2.getAreaPerFloor())
                    && StringUtils.isNotBlank(lhmzParam.getMinAreaPerFloor())
                    && StringUtils.isNotBlank(lhmzParam.getMaxAreaPerFloor())) {
                String[] split = industrialSpace2.getAreaPerFloor().split("\\|");
                if (split.length>0) {
                    flag = isRangeContained(lhmzParam.getMinAreaPerFloor(), lhmzParam.getMaxAreaPerFloor(),
                            split[0], split[split.length-1]);
                }
            }
            List<ContactInfoResult> contactInfoResults = new ArrayList<>();
            String contactPerson = industrialSpace2.getContactPerson();
            if (StringUtils.isNotBlank(contactPerson)) {

                String[] personsSplit = contactPerson.split("\\|");
                String personsInformation = industrialSpace2.getContactInformation();
                if (StringUtils.isNotBlank(personsInformation)) {
                    String[] personsContactInformation = personsInformation.split("\\|");
                    for (int i = 0; i < personsContactInformation.length; i++) {
                        ContactInfoResult contactInfoResult = new ContactInfoResult();
                        String userName;
                        if (i > personsSplit.length -1) {
                            userName = personsSplit[0];
                        } else {
                            userName = personsSplit[i];
                        }
                        String userPhone = personsContactInformation[i];
                        contactInfoResult.setPhone(userPhone);
                        contactInfoResult.setUserName(userName);
                        contactInfoResults.add(contactInfoResult);
                    }
                    industrialSpace2.setContactInfoResults(contactInfoResults);
                }
            }
            if (flag) {
                finalIndustrialSpace2s.add(industrialSpace2);
            }
        });
        return finalIndustrialSpace2s;
    }

    /**
     * 比价区间大小
     * @param targetStart 目标开始数值
     * @param targetEnd 目标结束数值
     * @param rangeStart 被比价开始数值
     * @param rangeEnd 被比价结束数值
     * @return
     */
    public static boolean isRangeContained(String targetStart, String targetEnd,
                                           String rangeStart, String rangeEnd) {
        try {
            BigDecimal tStart = new BigDecimal(targetStart);
            BigDecimal tEnd = new BigDecimal(targetEnd);
            BigDecimal rStart = new BigDecimal(rangeStart);
            BigDecimal rEnd = new BigDecimal(rangeEnd);

            if (tStart.compareTo(tEnd) > 0) {
                throw new IllegalArgumentException("目标区间起始大于结束");
            }
            if (rStart.compareTo(rEnd) > 0) {
                throw new IllegalArgumentException("包含区间起始大于结束");
            }
            // 判断目标区间是否完全落在包含区间内
            return tStart.compareTo(rStart) <= 0 && tEnd.compareTo(rEnd) >= 0;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<BankBranch> getAllBankBranchs(LhmzParam lhmzParam) {
        return bankBranchMapper.selectAll();
    }

    @Override
    public List<AffordableHousingProject> getAllAffordableHousingProjects(LhmzParam lhmzParam) {
        return affordableHousingProjectMapper.selectAll();
    }

    @Override
    public List<RealEstateSale> getAllRealEstateSales(LhmzParam lhmzParam) {
        return realEstateSaleMapper.selectAll();
    }

    @Override
    public List<SupermarketInfo> getAllSupermarketInfos(LhmzParam lhmzParam) {
        Wrappers wrappers = Wrappers.init()
                .where(StringUtils.isNotBlank(lhmzParam.getLatitude()), SUPERMARKET_INFO.LATITUDE.eq(lhmzParam.getLatitude()))
                .and(StringUtils.isNotBlank(lhmzParam.getLongitude()), SUPERMARKET_INFO.LONGITUDE.eq(lhmzParam.getLongitude()))
                .and(StringUtils.isNotBlank(lhmzParam.getQueryContent()),
                        SUPERMARKET_INFO.NAME.like(lhmzParam.getQueryContent())
                                .or(SUPERMARKET_INFO.ADDRESS.like(lhmzParam.getQueryContent())))
                .orderBy(SUPERMARKET_INFO.SERIAL_NUMBER.asc());
        List<SupermarketInfo> supermarketInfos = supermarketInfoMapper.selectListByQuery(wrappers);
        for (SupermarketInfo supermarketInfo : supermarketInfos) {
            List<ContactInfoResult> contactInfoResults = new ArrayList<>();
            String[] split = supermarketInfo.getContact().split("\\|");
            for (String str : split) {
                ContactInfoResult contactInfoResult = new ContactInfoResult();
                String[] contactStr = str.split("-");
                contactInfoResult.setUserName(contactStr[0]);
                contactInfoResult.setPhone(contactStr[1]);
                contactInfoResults.add(contactInfoResult);
            }
            supermarketInfo.setContactInfoResults(contactInfoResults);
        }
        return supermarketInfos;
    }

    @Override
    public void addInvestmentLead(InvestmentLead investmentLead) {
        String now = DateUtil.now();
        investmentLead.setCreateTime(now);
        investmentLead.setUpdateTime(now);
        investmentLeadMapper.insert(investmentLead);
    }

    @Override
    public Page<InvestmentLead> getAllInvestmentLead(LhmzParam lhmzParam) {
        Wrappers<Object> wrapper = Wrappers.init();
        wrapper.and(INVESTMENT_LEAD.DELETE_FLAG.eq(0));
        if (Objects.isNull(lhmzParam.getPageNo()) && Objects.isNull(lhmzParam.getPageSize())) {
            lhmzParam.setPageNo(1);
            lhmzParam.setPageNo(20);
        }
        if (lhmzParam.getSort() != null && StrUtil.isNotBlank(lhmzParam.getOrder())) {
            wrapper.orderBy(lhmzParam.getOrder() + " " + lhmzParam.getSort());
        } else {
            wrapper.orderBy(INVESTMENT_LEAD.CREATE_TIME.desc());
        }
        return investmentLeadMapper.paginateAs(Page.of(lhmzParam.getPageNo(), lhmzParam.getPageSize()), wrapper, null);
    }

    @Override
    public void addLhmzMessage(LhmzMessage lhmzMessage) {
        String now = DateUtil.now();
        lhmzMessage.setCreateTime(now);
        lhmzMessage.setUpdateTime(now);
        messageMapper.insert(lhmzMessage);
    }

    @Override
    public Page<LhmzMessage> getAllLhmzMessage(LhmzParam lhmzParam) {
        Wrappers<Object> wrapper = Wrappers.init();
        wrapper.and(LHMZ_MESSAGE.DELETE_FLAG.eq(0));
        if (Objects.isNull(lhmzParam.getPageNo()) && Objects.isNull(lhmzParam.getPageSize())) {
            lhmzParam.setPageNo(1);
            lhmzParam.setPageNo(20);
        }
        if (lhmzParam.getSort() != null && StrUtil.isNotBlank(lhmzParam.getOrder())) {
            wrapper.orderBy(lhmzParam.getOrder() + " " + lhmzParam.getSort());
        } else {
            wrapper.orderBy(LHMZ_MESSAGE.CREATE_TIME.desc());
        }
        return messageMapper.paginateAs(Page.of(lhmzParam.getPageNo(), lhmzParam.getPageSize()), wrapper, null);
    }

    @Override
    public List<PolicyRewardInfoResult> getAllPolicyRewardInfo(LhmzParam lhmzParam) {
        List<PolicyRewardInfoResult> policyRewardInfoResults = new ArrayList<>();
        Wrappers wrappers = Wrappers.init()
                .where(StringUtils.isNotBlank(lhmzParam.getRegionLevel()), POLICY_REWARD_INFO.REGION_LEVEL.eq(lhmzParam.getRegionLevel()))
                .and(StringUtils.isNotBlank(lhmzParam.getIndustryType()), POLICY_REWARD_INFO.INDUSTRY_TYPE.eq(lhmzParam.getIndustryType()))
                .and(StringUtils.isNotBlank(lhmzParam.getQueryContent()),
                        POLICY_REWARD_INFO.INDUSTRY_TYPE.like(lhmzParam.getQueryContent())
                                .or(POLICY_REWARD_INFO.REGION.like(lhmzParam.getQueryContent())))
                .orderBy(POLICY_REWARD_INFO.SERIAL_NUMBER.asc());
        List<PolicyRewardInfo> policyRewardInfos = policyRewardInfoMapper.selectListByQuery(wrappers);

        // 按 region 和 regionLevel 多级分组
        Map<String, Map<String, List<PolicyRewardInfo>>> groupedByRegionAndCity = policyRewardInfos.stream()
                .collect(Collectors.groupingBy(PolicyRewardInfo::getRegion,
                        Collectors.groupingBy(PolicyRewardInfo::getRegionLevel)
                ));
        groupedByRegionAndCity.forEach((region, cityMap) -> {
            cityMap.forEach((city, list) -> {
                PolicyRewardInfo policyRewardInfo = list.get(0);
                PolicyRewardInfoResult policyRewardInfoResult = new PolicyRewardInfoResult();
                policyRewardInfoResult.setRegion(policyRewardInfo.getRegion());
                policyRewardInfoResult.setRegionLevel(policyRewardInfo.getRegionLevel());
                policyRewardInfoResult.setIndustryType(policyRewardInfo.getIndustryType());
                policyRewardInfoResult.setReceivingUnit(policyRewardInfo.getReceivingUnit());
                policyRewardInfoResult.setRewardSize(list.size());
                policyRewardInfoResult.setPolicyRewardInfoList(list);
                policyRewardInfoResults.add(policyRewardInfoResult);
            });
        });
        return policyRewardInfoResults;
    }

    @Override
    public List<String> queryIndustryType() {
        Wrappers wrappers = Wrappers.init().select(POLICY_REWARD_INFO.INDUSTRY_TYPE)
                .groupBy(POLICY_REWARD_INFO.INDUSTRY_TYPE);
        return policyRewardInfoMapper.selectListByQueryAs(wrappers, String.class);
    }

    @Override
    public List<BusinessCircle> getAllBusinessCircles(LhmzParam lhmzParam) {
        Wrappers wrappers = Wrappers.init();
        return list(wrappers);
    }

    /**
     * 遍历对象的所有字段，如果是 String 类型，则去除 \n 和 \r
     */
    public static void cleanStringFields(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段

            if (field.getType() == String.class) {
                String value = (String) field.get(obj);
                if (value != null) {
                    String cleanedValue = value.replaceAll("[\\n\\r]", "");
                    field.set(obj, cleanedValue);
                }
            }
        }
    }

    @Override
    public List<ApplicationBusinessRelation> getApplicationBusinessType(LhmzParam lhmzParam) {
        Wrappers wrappers = Wrappers.init();
        wrappers.and(APPLICATION_BUSINESS_RELATION.APPLICATION_ID.eq(lhmzParam.getApplicationId()));
        return applicationBusinessRelationMapper.selectListByQuery(wrappers);
    }

    @Override
    public void deleteInvestmentLead(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        investmentLeadMapper.deleteBatchByIds(idList);
    }

    @Override
    public void deleteMessage(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        messageMapper.deleteBatchByIds(idList);
    }
}