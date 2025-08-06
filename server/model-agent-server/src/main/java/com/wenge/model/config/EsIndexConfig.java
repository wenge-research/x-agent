package com.wenge.model.config;

import com.wenge.model.mapper.es.ApplicationInfoVersionIndexMapper;
import com.wenge.model.mapper.es.*;
import com.wenge.oauth.mapper.es.InterfaceCallLogRecordingMapper;
import com.wenge.oauth.mapper.es.UmsOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class EsIndexConfig {

    @Autowired
    private DialogueMapper dialogueMapper;
    @Autowired
    private DialogueStepMapper dialogueStepMapper;
    @Autowired
    private FileDataMapper fileDataMapper;
    @Autowired
    private MultiMediaDataMapper multiMediaDataMapper;
    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;
    @Autowired
    private LlmConversationMapper llmConversationMapper;
    @Autowired
    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;

    @Autowired
    private MatterDataMapper matterDataMapper;

    @Autowired
    private SmartAgentLogMapper smartAgentLogMapper;
    @Autowired
    private KnowledgeStructuredDataMapper knowledgeStructuredDataMapper;

    @Autowired
    private StructuredOriginalDataMapper structuredOriginalDataMapper;

    @Autowired
    private UmsOperationMapper umsOperationMapper;

    @Autowired
    private IntelligentTranslationRecordMapper intelligentTranslationRecordMapper;

    @Autowired
    private GkStoryMapper gkStoryMapper;

    @Autowired
    private LegalAndRegulatoryDataMapper legalAndRegulatoryDataMapper;

    @Autowired
    private XBGCStoryMapper xbgcStoryMapper;


    @Autowired
    private InterfaceCallLogRecordingMapper interfaceCallLogRecordingMapper;

    @Autowired
    private SmartAgentLlmApiRecordMapper smartAgentLlmApiRecordMapper;
    @Autowired
    private ApplicationInfoVersionIndexMapper applicationInfoVersionIndexMapper;

    @Autowired
    private ApiDataMapper apiDataMapper;

    @Autowired
    private DecisionJsonDataMapper decisionJsonDataMapper;

    @Value("${esIndex.apiData}")
    private String apiDataIndex;

    @Value("${esIndex.knowledgeData}")
    private String knowledgeDataIndex;
    @Value("${esIndex.fileData}")
    private String fileDataIndex;
    @Value("${esIndex.multiMediaData}")
    private String multiMediaDataIndex;
    @Value("${esIndex.dialogue}")
    private String dialogueIndex;
    @Value("${esIndex.step}")
    private String stepIndex;
    @Value("${esIndex.llmConversation}")
    private String llmConversationIndex;

    @Value("${esIndex.matterData}")
    private String matterData;
    @Value("${esIndex.logInfo}")
    private String logInfo;
    @Value("${esIndex.urlData}")
    private String urlData;
    @Value("${esIndex.structuredData}")
    private String structuredData;
    @Value("${esIndex.structuredOriginalData}")
    private String structuredOriginalData;
    @Value("${esIndex.umsOperation}")
    private String umsOperation;
    @Value("${esIndex.intelligentTranslationRecord}")
    private String intelligentTranslationRecord;

    @Value("${esIndex.gkStory}")
    private String gkStory;
    @Value("${esIndex.interfaceCallLogRecord}")
    private String interfaceCallLogRecord;

    @Value("${esIndex.legalAndRegulatoryData}")
    private String legalAndRegulatoryData;

    @Value("${esIndex.xbgcStory}")
    private String xbgcStory;
    @Value("${esIndex.llmApiRecord}")
    private String smartAgentLlmApiRecord;
    @Value("${esIndex.applicationInfoVersionIndex}")
    private String applicationInfoVersionIndex;


    @Value("${esIndex.decisionJsonData}")
    private String decisionJsonDataIndex;

    @PostConstruct
    public void initIndex() {
        dialogueMapper.setCurrentActiveIndex(dialogueIndex);
        dialogueStepMapper.setCurrentActiveIndex(stepIndex);
        fileDataMapper.setCurrentActiveIndex(fileDataIndex);
        multiMediaDataMapper.setCurrentActiveIndex(multiMediaDataIndex);
        knowledgeDataMapper.setCurrentActiveIndex(knowledgeDataIndex);
        llmConversationMapper.setCurrentActiveIndex(llmConversationIndex);
        smartAgentLogMapper.setCurrentActiveIndex(logInfo);
        knowledgeUrlDataMapper.setCurrentActiveIndex(urlData);
        knowledgeStructuredDataMapper.setCurrentActiveIndex(structuredData);
        matterDataMapper.setCurrentActiveIndex(matterData);
        structuredOriginalDataMapper.setCurrentActiveIndex(structuredOriginalData);
        umsOperationMapper.setCurrentActiveIndex(umsOperation);
        intelligentTranslationRecordMapper.setCurrentActiveIndex(intelligentTranslationRecord);
        gkStoryMapper.setCurrentActiveIndex(gkStory);
        xbgcStoryMapper.setCurrentActiveIndex(xbgcStory);
        legalAndRegulatoryDataMapper.setCurrentActiveIndex(legalAndRegulatoryData);
        interfaceCallLogRecordingMapper.setCurrentActiveIndex(interfaceCallLogRecord);
        smartAgentLlmApiRecordMapper.setCurrentActiveIndex(smartAgentLlmApiRecord);
        applicationInfoVersionIndexMapper.setCurrentActiveIndex(applicationInfoVersionIndex);
        apiDataMapper.setCurrentActiveIndex(apiDataIndex);
        decisionJsonDataMapper.setCurrentActiveIndex(decisionJsonDataIndex);
    }

}
