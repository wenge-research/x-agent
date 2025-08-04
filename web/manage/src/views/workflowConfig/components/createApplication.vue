<template>
    <el-dialog
        :title="type == 'edit' ? $t('edit') : $t('createModel')"
        :visible.sync="dialogVisibleApplication"
        width="560px"
        :before-close="cancelTemplate"
        class="applicationDialog"
        append-to-body
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
        <div style="position: relative">
            <div class="formOuter">
                <div class="uploadImg">
                   
                    <div>
                        <el-form
                            :model="appForm"
                            :rules="rules"
                            ref="ruleForm"
                            class="demo-ruleForm"
                        >
                            <el-form-item label="">
                                <div class="type-radio-group">
                                    <div v-for="item in typeList" :key="item.value" class="type-radio" :class="{ active: appForm.type === item.value }" @click="handleTypeChange(item.value)">
                                        <div class="title">
                                            <img v-if="item.icon" :src="item.icon" alt="">
                                            <span>{{ item.label }}</span>
                                        </div>
                                        <div class="desc">{{ item.desc }}</div>
                                    </div>
                                    
                                
                                </div>
                                
                            </el-form-item>
                            <el-form-item
                                :label="$t('workflowName')"
                                prop="componentName"
                            >
                                <el-input
                                    v-model="appForm.componentName"
                                    show-word-limit
                                    maxlength="100"
                                    style="width: 100%"
                                />
                            </el-form-item>
                            <el-form-item
                                :label="$t('workflowDescription')"
                                prop="componentDesc"
                            >
                                <el-input
                                    class="inputTextarea"
                                    type="textarea"
                                    :rows="4"
                                    v-model="appForm.componentDesc"
                                    show-word-limit
                                    maxlength="1000"
                                    style="width: 100%"
                                />
                            </el-form-item>
                            
                            <el-form-item label="头像">
                                <div class="uploadOuter">
                                    <el-upload
                                        :action="actionUrl"
                                        :data="{ filePath: 'agent_source' }"
                                        :class="{ hideBox: uploadBtnLogo }"
                                        :file-list="fileListLogo"
                                        :show-file-list="false"
                                        :limit="1"
                                        class="logoAppUpload"
                                        list-type="picture-card"
                                        :on-remove="handleLogoRemove"
                                        :on-success="handleLogoSuccess"
                                    >
                                        <div @mouseenter="imgMouseenter" @mouseleave="imgMouseleave" style="position: relative; height: 100%" v-if="appForm.icon">
                                            <img
                                                :src="appForm.icon"
                                                style="
                                                    width: 80px;
                                                    height: 80px;
                                                    border-radius: 4px;
                                                "
                                            />
                                            <div
                                                v-show="showDeleteIcon"
                                                class="opts-btn"
                                            >
                                                <iconpark-icon name="edit-line" size="18" color="#FFFFFF"></iconpark-icon>
                                                <iconpark-icon  name="delete-bin-4-line" class="delete" size="18" color="#FFFFFF" @click.stop="deleteLogo"></iconpark-icon>
                                            
                                            </div>
                                        </div>
                                        <div v-else style="height: 100%; display: flex; align-items: center; justify-content: center; background: #f2f4f7">
                                            <iconpark-icon name="add-line" size="24" color="#8c939d"></iconpark-icon>
                                        </div>
                                    </el-upload>
                                    <el-button class="ai-btn" type="primary" :loading="imgLoading" @click="getImageUrl">
                                        <img src="@/assets/images/ai-btn.svg" alt="">
                                        AI生成
                                    </el-button>
                                </div>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
            <el-button type="primary" @click="confirmTemplate">{{
                $t("confirm")
            }}</el-button>
        </span>
    </el-dialog>
</template>

<script>
import { getAiImage } from '@/api/app';
export default {
    data() {
        return {
            appForm: {
                type: 2,
                icon: require("@/assets/images/default-icon-workflow.svg"), // 默认图像的URL
                apiKey: "2a1a7fdc65935c4066bc388ed7fb939561279ab5",
                apiSecret: "89e875b8528346a3fd22dfc04685c749",
                applicationId: "fd6bc5c01cb2432cab92fc41a6f6ae91",
                componentName: "",
                applicationCode: "workflow",
                webIcon:
                    `${process.env.VUE_APP_BASE_API}/wos/file/download?fileKey=robot.png`,
                createTime: null,
                createUser: null,
                deleteFlag: "0",
                failureTalk: "",
                greeting:
                    "我可以为您解答政务相关问题，提供政策查询，政策解读和办事指南，为您提供贴心的服务。",
                id: 106,
                componentDesc: "",
                knowledgeFlag: this.$t("yes"),
                sensitiveFlag: this.$t("yes"),
                subjectFlag: "true",
                modelAnswerFlag: this.$t("yes"),
                networkFlag: this.$t("yes"),
                clientLink:
                    `${process.env.VUE_APP_BASE_API}/#/knowledgeDetails/workflow`,
                modelId: "",
                modelName: "deepseek",
                multiDialogueFlag: this.$t("yes"),
                multiDialogueNum: 5,
                ocrFlag: this.$t("no"),
                periodEnd: null,
                periodStart: "2024-06-10 15:35:04",
                publishStatus: "1",
                recommendation: "",
                recommendationNum: 3,
                remark: this.$t("remark"),
                rewritingFlag: "是",
                robotIcon:
                    `${process.env.VUE_APP_BASE_API}/wos/file/download?fileKey=robot.png`,
                systemPrompt:
                    "你是一个中关村开发提供的网站GPT智能客服助手，为市民提供贴心的服务，提供政策解读、办事指南等服务功能，不能和市民闲聊。",
                tailTalk: "",
                ttsId: null,
                updateTime: null,
                updateUser: null,
                userIcon:
                    `${process.env.VUE_APP_BASE_API}/wos/file/download?fileKey=user.png`,
                videoFlag: this.$t("no"),
                virtualHumanFlag: this.$t("no"),
                virtualHumanId: "",
                voiceDialogueFlag: this.$t("yes"),
                api: `${process.env.VUE_APP_BASE_API}/dialogue/dialogueApi`,
                buttonStyle:
                    '<a href="{url}" target="_blanck" style="text-decoration: none;color: #ffffff;color: #ffffff; background-color: #409EFF;border-radius: 5px;padding: 0 4px;">{title}</a>',
                buttonText: this.$t("forMoreDetailsSee"),
                qaTitleScore: 1.91,
                qaContentScore: 0.88,
                qaRangeTitleScore: 0.91,
                qaRangeContentScore: 0.88,
                contentScore: 1.76,
                rangeContentScore: 0.88,
                filterNum: 3,
                prepareNum: 60,
                answerTimeout: null,
                promptTemplate:
                    '#背景环境 # \r\n今天是{date}，为了提高政务服务质量，当市民咨询政务或者生活上相关政务事项时，你的做法是：根据市民的输入的问题从知识库找到相关有效信息，然后根据这些有效信息进行总结归纳，最终回答市民的问题。 \r\n\r\n############# \r\n知识库： \r\n{content} \r\n「【知识库99】是：我是中关村关芯客服，为市民提供贴心的服务，提供政策解读、办事指南等服务功能，不能和市民闲聊」\r\n「【知识库100】是：我是中关村关芯助理，暂时不方便透露大模型」\r\n \r\n############# \r\n输入问题 ： \r\n{question} \r\n\r\n#知识库范围#\r\n「」标签中的内容是知识库内容\r\n\r\n#问题范围#\r\n『』标签中的内容是问题内容\r\n\r\n# 任务目标 # \r\n请阅读并理解<<<知识库>>>中的每段知识库内容，请综合全部信息，并按照以下步骤进行推理，然后根据<<<知识库>>>的相关信息对<<<输入问题>>>进行作答：\r\n1.逐个阅读每一段「」标签中的知识库内容，并提取有效信息。 \r\n2.请分析给出的『』标签中的问题内容，提取问题的真实意图。 \r\n3.根据输入问题的真实意图，从已提取到的有效信息中推理、总结出最终答案。 \r\n4.在最终答案添加敬语、问候语、感谢语等。 \r\n\r\n#护栏#\r\n不要根据你所知的信息和常识来回答这个问题，如果<<<知识库>>>没有足够的信息来回答问题，不要编造信息，请回答：{"答案":"我不知道","引用知识库":"没有相关知识库","问题":输入的问题}\r\n\r\n#范围#\r\n只允许你回答有关<<<知识库>>>中的内容，不得回答任何敏感信息相关的问题\r\n\r\n# 回答风格 # \r\n遵循政府政务客服人员的态度和风格 \r\n# 回答语气 # \r\n用简洁、友好、人文主义的语气回答，在最终答案添加敬语、问候语、感谢语等。\r\n# 受众群体 # \r\n政府政务客服的咨询群众通常是关心自身权益和办理事项的市民，定制你的答案，以针对这些群众想要了解事项的准确信息 \r\n# 回答输出格式 # \r\n用json格式输出，{"答案":最终答案，"引用知识库":"知识库编号或则没有相关知识库","问题":输入的问题} \r\n参照输出示例：{"答案":"我是中关村的关芯客服，为市民提供贴心的服务，提供政策解读、办事指南等服务","引用知识库":"知识库1，知识库2","问题":"你是谁"}\r\n',
                notAnswer: this.$t("iDoNotKnow"),
                disclaimer: "",
                templateId: "bceb62e73415419db621ac4a4d3acada",
                mobileTemplateId: "6",
                identityIcon:
                    `${process.env.VUE_APP_BASE_API}/wos/file/download?fileKey=您好，我是关芯客服备.png`,
                networkChannel: "baidu",
                polishFlag: this.$t("yes"),
                polishPrompt:
                    "你是一个热情的中关村政务客服助手，我提供一个问题和一个答案，请用热情的语气润色答案内容，答案需要添加敬语、问候语、感谢语等，然后重新整理数据排版和格式。我的问题是：{question}，我的答案是：{answer}",
                processStep:
                    "builtIn,interceptSensitive,subjectTalk,findQaTitle,findQaContent,finalCollectStrategy,findAnswerByModel",
                webTitle: this.$t("defaultCustomerService"),
                subjectPrompt:
                    '今天是{date}，我将给你{num}个场景和一个问题，最后一句为问句并且只有一个问句，请分析问句是属于哪个场景。\n以json格式输出{ "场景"}，参考输出例子：{"场景":"【场景1】"}\n所有场景如下：{scenes}\n我的问题是：{question}',
                ipFlag: this.$t("no"),
                docLinkType: "",
                previewDoc: this.$t("yes"),
                recommendQuestionsShowFlag: "1",
                sourceShowFlag: "1",
                relatedMattersShowFlag: "1",
                sttId: null,
                tenantId: null,
                clientAuthChannel: "",
                pcAuthChannel: "",
                toHumanFlag: null,
                inputPlaceholder: this.$t("enterYourQuestionHere"),
                prologue: "",
                backgroundImageUrl: null,
                facadeImageUrl: null,
                llmInfo: null,
                knowledgeIds: [],
                presetQuestionList: [],
                templateRoute: null,
                mobileTemplateRoute: null,
                clientAuthChannelCode: null,
                pcAuthChannelCode: null,
            },
            uploadBtnLogo: false,
            fileListLogo: [],
            actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
            rules: {
                componentName: [
                    {
                        required: true,
                        message: this.$t("pleaseEnterActivityName"),
                        trigger: "blur",
                    },
                ],
                componentDesc: [
                    {
                        required: false,
                        message: this.$t("pleaseSelectActivityArea"),
                        trigger: "blur",
                    },
                ],
            },
            typeList: [
                {label: '工作流', value: 2, desc: '用于处理功能类的请求，可通过顺序执行一系列节点实现某个功能。', icon: require("@/assets/images/workflow-select.svg")},
                {label: '对话流', value: 3, desc: '对话流是基于对话场景的特殊工作流，专门用于处理对话类请求。', icon: require("@/assets/images/dialogue-select.svg")}
            ],
            showDeleteIcon: false,
            imgLoading: false
        };
    },
    props: {
        dialogVisibleApplication: {
            type: Boolean,
            default: false,
        },
        params: Object,
        type: String,
    },
    mounted() {
        if (this.type == "edit") {
            this.appForm = JSON.parse(JSON.stringify(this.params));
        }else {
            this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
        }
    },
    methods: {
        imgMouseenter() {
            console.log("imgMouseenter");
            if (this.appForm.icon) {
                this.showDeleteIcon = true;
            }
        },
        imgMouseleave() {
            console.log("imgMouseleave");
            this.showDeleteIcon = false;
        },
        deleteLogo() {
            this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
            return;
        },
        cancelTemplate() {
          this.$emit("cancelApplication", false);
        },
        confirmTemplate() {
            this.$refs.ruleForm.validate((valid) => {
                if (valid) {
                    if (this.type == "add") {
                        this.$emit("confirmApplication", this.appForm);
                    } else {
                        this.$emit(
                            "confirmEditApplication",
                            this.appForm
                        );
                    }
                } else {
                    return false;
                }
            });
        },
        handleLogoRemove(file, fileList) {
            this.uploadBtnLogo = false;
            this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
            this.fileListLogo = [];
        },
        handleLogoSuccess(response, file, fileList) {
            if ((response.code = "000000")) {
                this.uploadBtnLogo = true;
                this.appForm.icon =
                    response.data && response.data[0]
                        ? response.data[0].url
                        : "";
                this.fileListLogo = [];
            } else {
                this.uploadBtnLogo = false;
                this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
                this.fileListLogo = [];
            }
        },
        handleTypeChange(value) {
			console.log('this.type',this.type)
            // if(this.type == 'edit'){
            //     return;
            // }
            this.appForm.type = value;
            if(value == 2) {
                if(this.appForm.icon.includes("default-dialogue-")) {
                    this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
                }
            }else {
                if(this.appForm.icon.includes("default-workflow-")) {
                    this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
                }
            }
            
        },
        getRandomHeadImgDefaultBgColor() {
            const colors = ['#2BCBCB', '#2AA3CB', '#2B7BCB', '#5B90F9', '#5B69F9', '#9C5BF9', '#C45BF9'];
            const workflowImgList = [
                require('@/assets/images/default-workflow-1.svg'),
                require('@/assets/images/default-workflow-2.svg'),
                require('@/assets/images/default-workflow-3.svg'),
                require('@/assets/images/default-workflow-4.svg'),
                require('@/assets/images/default-workflow-5.svg'),
                require('@/assets/images/default-workflow-6.svg'),
                require('@/assets/images/default-workflow-7.svg'),
            ];
            const dialogueImgList = [
                require('@/assets/images/default-dialogue-1.svg'),
                require('@/assets/images/default-dialogue-2.svg'),
                require('@/assets/images/default-dialogue-3.svg'),
                require('@/assets/images/default-dialogue-4.svg'),
                require('@/assets/images/default-dialogue-5.svg'),
                require('@/assets/images/default-dialogue-6.svg'),
                require('@/assets/images/default-dialogue-7.svg'),
            ];
            const imgList = this.appForm.type == 2 ? workflowImgList : dialogueImgList;
            const randomIndex = Math.floor(Math.random() * imgList.length);
            return imgList[randomIndex];
        },
        getImageUrl() {
            this.$refs.ruleForm.validate(async(valid) => {
                if (valid) {
                    this.imgLoading = true;
                    getAiImage({
                        topic: this.appForm.componentName,
                        description: this.appForm.componentDesc
                    }).then((res) => {
                        if (res.code == "000000") {
                            this.appForm.icon = res.data || this.getRandomHeadImgDefaultBgColor();
                        }else {
                            this.$message.warning('生成失败')
                        }
                        
                        this.imgLoading = false;
                    }).catch(() => {

                        this.imgLoading = false;
                    });
                } else {
                    return false;
                }
            });
        }
    },
};
</script>

<style lang="scss" scoped>
.applicationDialog {
    ::v-deep .el-dialog {
        border-radius: 4px;
        .el-dialog__body {
            padding: 16px 32px;
        }
        .el-dialog__header {
            background: #fff;
            border-radius: 8px 8px 0px 0px;
            .el-dialog__headerbtn {
                top: 36px;
                right: 32px;
            }
        }
        .el-dialog__footer {
            text-align: right;
            padding: 10px 32px 20px;
        }
    }
}
.inputTextarea {
    ::v-deep .el-textarea__inner {
        font-family: MiSans, MiSans;
    }
}
.logoAppUpload {
    text-align: center;
    ::v-deep .el-upload--picture-card {
        border: 0;
        width: 80px;
        height: 80px;
    }
}

.demo-ruleForm {
    ::v-deep .el-form-item__label {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
        line-height: 32px;
    }
}
.flex {
    display: flex;
}
.flex-center {
    display: flex;
    align-items: center;
}

.aligns {
    align-items: center;
}
.just {
    justify-content: space-between;
}
.uploadOuter {
  width: 100%;
  display: flex;
  align-items: end;
  :deep(.ai-btn) {
    height: 32px;
    background: linear-gradient( 270deg, rgba(142, 101, 255, .15) 0%, rgba(23, 71, 229, .15) 100%);
    border-radius: 2px;
    display: inline-flex;
    align-items: center;
    border: 0px;
    padding: 0px 8px;
    
    margin-left: 16px;
    font-size: 14px;
    color: #1747E5;
    span {
      display: inline-flex;
      align-items: center;
    }
    img {
      margin-right: 2px;
      width: 16px;
      height: 16px;
      border: none;
    }
  }
}
.type-radio-group {
    display: flex;
    gap: 16px;
    .type-radio {
        flex: 1;
        height: 96px;
        background: #FFFFFF;
        border-radius: 2px;
        border: 1px solid #D5D8DE;
        padding: 12px;
        cursor: pointer;
        .title {
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #383D47;
            line-height: 24px;
            margin-bottom: 12px;
            img {
                width: 24px;
                height: 24px;
                margin-right: 8px;
            }
        }
        .desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #828894;
            line-height: 18px;
        }
        &.active {
            background: rgba(28,80,253,0.05);
            border: 1px solid #1747E5;
        }
        
    }
}
.opts-btn {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    background: rgba(0, 0, 0, .4);
    backdrop-filter: blur(1px);
    position: absolute;
    bottom: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
}

</style>
