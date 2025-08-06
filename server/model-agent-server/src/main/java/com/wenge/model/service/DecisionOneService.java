package com.wenge.model.service;

import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.DecisionJsonData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DecisionOneService {

    Result<List<DecisionScenariosResult>> scenarios(EmptyParam param);

    SseEmitter connectDecision(DecisionProcessParam param);

    void exportDecision(StringParam param, HttpServletResponse response);

    Result<DecisionInitResult> initDecision(StringParam param);

    Result<DecisionStatusResult> decisionStatus(StringParam param);

    Result<DecisionRunResult> runDecision(DecisionRunParam param);

    Result<JSONObject> plans(DecisionPlansParam param);

    Result<List<JSONObject>> agents(StringParam param);

    Result<JSONObject> environment(StringParam param);

    Result<String> saveJson(DecisionJsonSaveParam param);

    Result<DecisionJsonData> getDecisionJson(StringParam param);

    Result<JSONObject> interactions(StringParam param);

    Result<JSONObject> actions(StringParam param);

    Result<JSONObject> decisions(StringParam param);

    SseEmitter decisionsReport(DecisionsReportParam param);

    SseEmitter decisionsSummary(DecisionsReportParam param);
}
