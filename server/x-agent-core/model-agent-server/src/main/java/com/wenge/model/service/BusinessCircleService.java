package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.LhmzParam;
import com.wenge.model.dto.result.PolicyRewardInfoResult;
import com.wenge.model.entity.*;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface BusinessCircleService extends IService<BusinessCircle> {
    Result replaceR();

    List<IndustrialSpace> getAllIndustrialSpaces(LhmzParam lhmzParam);

    List<IndustrialSpace2> getAllIndustrialSpaces2(LhmzParam lhmzParam);


    List<BankBranch> getAllBankBranchs(LhmzParam lhmzParam);

    List<AffordableHousingProject> getAllAffordableHousingProjects(LhmzParam lhmzParam);

    List<RealEstateSale> getAllRealEstateSales(LhmzParam lhmzParam);

    List<SupermarketInfo> getAllSupermarketInfos(LhmzParam lhmzParam);

    void addInvestmentLead(InvestmentLead investmentLead);

    Page<InvestmentLead> getAllInvestmentLead(LhmzParam lhmzParam);

    List<BusinessCircle> getAllBusinessCircles(LhmzParam lhmzParam);

    List<PolicyRewardInfoResult> getAllPolicyRewardInfo(LhmzParam lhmzParam);

    /**
     * 查询产业类型
     * @return
     */
    List<String> queryIndustryType();

    public void addLhmzMessage(LhmzMessage lhmzMessage);

    public Page<LhmzMessage> getAllLhmzMessage(LhmzParam lhmzParam);

    List<ApplicationBusinessRelation> getApplicationBusinessType(LhmzParam lhmzParam);

    void deleteInvestmentLead(List<String> idList);

    void deleteMessage(List<String> idList);
}
