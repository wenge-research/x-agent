package com.wenge.oauth.constants;

public interface RedisConstant {

    String BASE = "smart-agent:";

    String OAUTH = BASE + "oauth:";
    String LOCK = BASE + "lock:";
    String RUN = BASE + "run:";

    String TOKEN = OAUTH + "token:";
    String TOKEN_ACCOUNT = TOKEN + "account:";
    String LOCK_CAC = LOCK + "cac:";
    String RUN_CAC = RUN + "cac:";
    String LOCK_URL_PARSER = LOCK + "urlParser:";
    String RUN_URL_PARSER = RUN + "urlParser:";
    String LOCK_SYNC_KNOWLEDGE = LOCK + "syncKnowledge:";
    String RUN_SYNC_KNOWLEDGE = RUN + "syncKnowledge:";
    String LOCK_SYNC_WORKFLOW_TRIGGER = LOCK + "workflowTrigger:";
    String RUN_SYNC_WORKFLOW_TRIGGER = RUN + "workflowTrigger:";
    String LOCK_SYNC_WORKFLOW_TRIGGER_RUNNING = LOCK + "workflowTriggerRunning:";
    String RUN_SYNC_WORKFLOW_TRIGGER_RUNNING = RUN + "workflowTriggerRunning:";
    String LOCK_SYNC_CALL_API = LOCK + "callApi:";
    String RUN_SYNC_CALL_API  = RUN + "callApi:";
    String LOCK_SYNC_CALL_API_RUNNING = LOCK + "callApiRunning:";
    String RUN_SYNC_CALL_API_RUNNING  = RUN + "callApiRunning:";

    String LOCK_PARSE_DOC = LOCK + "parse_doc:";
    String LOCK_HK_PARSE_DOC = LOCK + "hk_parse_doc:";
    String LOCK_STORY = LOCK + "story:";
    String LOCK_GUAXIN = LOCK + "guaxin:";
    String LOCK_DIVISION = LOCK + "division:";


    String LOCK_LH_ONLINE = LOCK + "lh_online:";
    String RUN_LH_ONLINE = RUN + "lh_online:";
    String LOCK_LH_ONLINE_GUI = LOCK + "lh_online_gui:";
    String RUN_LH_ONLINE_GUI = RUN + "lh_online_gui:";
    String LOCK_MEDIUM = LOCK + "medium:";
    String RUN_MEDIUM = RUN + "medium:";
    String LOCK_IMAGE = LOCK + "image:";
    String RUN_IMAGE = RUN + "image:";
    String LOCK_WH_DATA = LOCK + "wh_data:";
    String RUN_WH_DATA = RUN + "wh_data:";
    String LOCK_WORD2PDF = LOCK + "Word2Pdf:";
    String RUN_WORD2PDF = RUN + "Word2Pdf:";
    String LOCK_YAYI_DOC = LOCK + "yayi_doc:";
    String QYWX_TOKEN = "qywx:token:";

    String LOGIN_SMS = BASE + "login_sms:";
    String LOGIN_SMS_IP = BASE + "login_sms_ip:";


    // 西部国传定时任务锁
    String XBGC_LOCK_STORY = LOCK + "xbgc_story:";


}
