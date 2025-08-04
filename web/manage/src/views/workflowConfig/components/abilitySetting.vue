<template>
    <div class="outerDialog" v-loading="workflowLoading">
        <div class="headBar">
            <div class="leftSlide">
              <img
                  src="@/assets/images/arrow-go-back-fill.svg"
                  @click="closeDialog"
                  class="back-icon"
                />
                <img :src="component.icon || defaultImage" class="leftSlide-logo" />
                <div class="titleIcon">
                    <p :title="component.componentName || $t('noApplicationName')" class="tit">
                        {{ component.componentName || $t("noApplicationName") }}
                        <!-- <el-tooltip :disabled="!component.componentDesc" :content="component.componentDesc">
                            <iconpark-icon name="information-line" color="#383d47" style="margin-left: 4px;cursor: pointer" size="16"></iconpark-icon>
                        </el-tooltip> -->
                        <img
                          src="@/assets/images/appManagement/edit-line.svg"
                          class="edit-icon"
                          @click="openEditDialog"
                          v-if="isAdminOrUser"
                        />
                    </p>
                   <!-- <p :title="component.componentDesc || $t('noDescription')" class="decs">
                      {{component.componentDesc || $t('noDescription')}}11
                    </p> -->
                    <p>自动保存于 {{ saveTime }}</p>
                </div>
            </div>
            <div class="rightSlide">
                <!-- <el-button
          class="btns"
          v-permission="'issue'"
          @click="openSettingDialog"
        >
          <span>{{ $t("addFeatures") }}</span>
        </el-button>
        <el-button class="btns" v-permission="'issue'" @click="runApp">
          <span>{{ $t("run") }}</span>
        </el-button> -->
                <el-button
                    class="btn"
                    :loading="saveLoading"
                    @click="submitAddApp(1)"
                     v-if="isAdminOrUser"
                >
                    <img src="@/assets/images/save-line.svg" /><span>{{ $t("saveDraft") }}</span>
                </el-button>
                <el-button
                    class="btns"
                    v-permission="'issue'"
                    style=" margin: 0 0 0 16px; "
                    :loading="publishLoading"
                    @click="submitAddApp(2)"
                    v-if="isAdminOrUser"
                >
                    
                    <span class="btns-inner" v-if="!publishLoading"> <iconpark-icon name="send-plane-2-fill" color="#fff" ></iconpark-icon> {{ $t("save") }}</span>
                </el-button>
                <el-tooltip
                    class="item"
                    effect="dark"
                    content="创建副本"
                    placement="bottom"
                >
                    <div
                        class="copy-btn"
                        @click="copyHandler"
                        v-loading="copyLoading"
                    >
                        <iconpark-icon
                            name="file-copy-line"
                            size="16"
                            color="#828894"
                        ></iconpark-icon>
                    </div>
                </el-tooltip>
            </div>
        </div>
        <div class="dialogPower">
            <dragDemo
                v-if="showDemo"
                ref="dragDemo"
                :tabs="isAdminOrUser?tabs:personTabs"
                :type="'workflow'"
                :params="component"
                :canvas="component.canvas"
            ></dragDemo>
        </div>
        <el-dialog
            :visible.sync="dialogVisible"
            width="800px"
            :show-close="true"
            class="subFlexDialog"
            top="6vh"
            :title="$t('addFeatures')"
            :close-on-press-escape="false"
            destroy-on-close
            append-to-body
            :close-on-click-modal="false"
        >
            <!-- 能力设置放入新页面 -->
            <settingDia
                v-if="dialogVisible"
                @configCloseDialog="configCloseDialog"
                ref="settingDia"
                :appConfigForm="component.applicationInfo"
                @refreshAppList="refreshAppList"
            ></settingDia>
            <div class="footer" style="text-align: center; margin: 30px 0 0 0">
                <el-button type="primary" @click="setConfig">{{
                    $t("confirm")
                }}</el-button>
                <el-button @click="configCloseDialog">{{
                    $t("cancel")
                }}</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { addApplicationPluginData } from "@/api/app";
import dragDemo from "@/views/workflowConfig/dragDemo/index.vue";
import settingDia from "@/views/workflowConfig/components/settingDia/index.vue";
// vuex
import { mapMutations, mapActions } from "vuex";
// api
import {
    saveWorkflow,
    queryWorkflowDetail,
    copyWorkflowApp,
} from "@/api/workflow";

export default {
    components: {
        dragDemo,
        settingDia,
    },
    data() {
        return {
            workflowLoading:false,
            interval:null,
            component: {},
            appForm: {},
            tabs: [
                {
                    label: "节点",
                    ref:'nodeDrawer',
                    icon: "add-circle-line",
                    activeIcon: "add-circle-fill",
                },
                {
                    label: "测试",
                    ref:'runDrawer',
                    icon: "terminal-box-line",
                    activeIcon: "terminal-box-fill",
                },
                // {
                //     label: "展示",
                //     ref:'showDrawer',
                //     icon: "palette-line",
                //     activeIcon: "palette-fill",
                // },
            ],
            personTabs:[
                {
                    label: "测试",
                    ref:'runDrawer',
                    icon: "terminal-box-line",
                    activeIcon: "terminal-box-fill",
                },
            ],
            showDemo: false,
            saveLoading: false,
            publishLoading: false,
            dialogVisible: false,
            copyLoading: false,
            defaultImage: require('@/assets/images/workflow.svg'), // 默认图像的URL
            saveTime: ""
        };
    },
    computed:{
        isAdminOrUser(){
            let obj=JSON.parse(sessionStorage.getItem("user"))
            return obj.powerType==0 || obj.accountName == this.component.createUser
        }
    },
    props: {
        componentId: String,
    },
    beforeDestroy() {
      clearInterval(this.interval)
      // 删除 sessionStorage 中的 startValues 数据
      sessionStorage.removeItem('startValues');
    },
    mounted() {
        this.fetchModleOptions();
        this.fetchKnowledgeList();
        this.queryWorkflowDetail(this.componentId);
        // this.interval = setInterval(() => {
        //   this.submitAddApp(1);
        // }, 60*1000);
        this.$EventBus.$on('saveWorkflowAgain', () => {
          this.submitAddApp(5)
        })
    },
    methods: {
        ...mapMutations(["setFuncData", "setShowDrawerData"]),
        ...mapActions(['fetchModleOptions','fetchKnowledgeList']),
        init(data) {
            this.component = JSON.parse(JSON.stringify(data));
            // vuex初始化功能与展示数据 console.log("component=====", this.component);
            if (
                JSON.stringify(this.component) != "{}" &&
                this.component?.applicationInfo &&
                JSON.stringify(this.component.applicationInfo) != "{}"
            ) {
                this.initFuncData(this.component.applicationInfo);
                this.initShowDrawerData(this.component.applicationInfo);
            }
            this.showDemo = true;
        },
        initFuncData(item) {
            let funcData = {
                ...item,
                id: item.id,
                applicationId: item.applicationId,
                templateId: item.templateId,
                mobileTemplateId: item.mobileTemplateId,
                applicationName: item.applicationName,
                applicationCode: item.applicationCode,
                introduce: item.introduce,
                greeting: item.greeting,
                prologue: item.prologue,
                inputPlaceholder: item.inputPlaceholder,
                webTitle: item.webTitle,
                disclaimer: item.disclaimer,
                docLinkType: item.docLinkType,
                tailTalk: item.tailTalk,
                failureTalk: item.failureTalk,
                virtualHumanId: item.virtualHumanId,
                clientAuthChannel: item.clientAuthChannel,
                pcAuthChannel: item.pcAuthChannel,
                toHumanFlag: item.toHumanFlag,
                ttsId: item.ttsId ? parseInt(item.ttsId) : null,
                sttId: item.sttId ? parseInt(item.sttId) : null,
                buttonStyle: item.buttonStyle,
                buttonText: item.buttonText,
                modelId: item.modelId,
                answerTimeout: item.answerTimeout,
                notAnswer: item.notAnswer,
                promptTemplate: item.promptTemplate,
                subjectPrompt: item.subjectPrompt,
                multiDialogueNum: item.multiDialogueNum,
                recommendation: item.recommendation,
                recommendationNum: item.recommendationNum,
                systemPrompt: item.systemPrompt,
                remark: item.remark,
                contentScore: item.contentScore,
                rangeContentScore: item.rangeContentScore,
                qaTitleScore: item.qaTitleScore,
                qaRangeTitleScore: item.qaRangeTitleScore,
                qaContentScore: item.qaContentScore,
                qaRangeContentScore: item.qaRangeContentScore,
                filterNum: item.filterNum,
                prepareNum: item.prepareNum,
                publishStatus: item.publishStatus,
                logo: item.logo,
                robotIcon: item.robotIcon,
                webIcon: item.webIcon,
                userIcon: item.userIcon,
                identityIcon: item.identityIcon,
                previewDoc: item.previewDoc,
                virtualHumanFlag: ["是", true, "true"].includes(
                    item.virtualHumanFlag
                )
                    ? true
                    : false,
                voiceDialogueFlag: ["是", true, "true"].includes(
                    item.voiceDialogueFlag
                )
                    ? true
                    : false,
                modelAnswerFlag: ["是", true, "true"].includes(
                    item.modelAnswerFlag
                )
                    ? true
                    : false,
                sensitiveFlag: ["是", true, "true"].includes(item.sensitiveFlag)
                    ? true
                    : false,
                networkFlag: ["是", true, "true"].includes(item.networkFlag)
                    ? true
                    : false,
                ipFlag: ["是", true, "true"].includes(item.ipFlag)
                    ? true
                    : false,
                ocrFlag: ["是", true, "true"].includes(item.ocrFlag)
                    ? true
                    : false,
                videoFlag: ["是", true, "true"].includes(item.videoFlag)
                    ? true
                    : false,
                multiDialogueFlag: ["是", true, "true"].includes(
                    item.multiDialogueFlag
                )
                    ? true
                    : false,
                knowledgeFlag: ["是", true, "true"].includes(item.knowledgeFlag)
                    ? true
                    : false,
                sourceShowFlag: item.sourceShowFlag,
                recommendQuestionsShowFlag: item.recommendQuestionsShowFlag,
                rewritingFlag: ["是", true, "true"].includes(item.rewritingFlag)
                    ? true
                    : false,
                subjectFlag: item.subjectFlag,
                presetQuestionList:
                    item.presetQuestionList &&
                    item.presetQuestionList.length > 0
                        ? item.presetQuestionList
                        : [""],
                knowledgeIds: item.knowledgeIds,
                networkChannel: item.networkChannel,
                polishFlag: ["是", true, "true"].includes(item.polishFlag)
                    ? true
                    : false,
                polishPrompt: item.polishPrompt,
                backgroundImageUrl: item.backgroundImageUrl,
                facadeImageUrl: item.facadeImageUrl,
                type:
                    item.type &&
                    [
                        "default",
                        "image",
                        "doc",
                        "code",
                        "ppt",
                        "txt",
                        "excel",
                    ].includes(item.type)
                        ? "file"
                        : item.type,
            };

            if (item.processStep && Array.isArray(item.processStep)) {
                funcData.processStep = [...item.processStep];
            } else {
                funcData.processStep = item.processStep
                    ? item.processStep.split(",")
                    : [];
            }
            this.setFuncData(funcData);
        },
        initShowDrawerData(item) {
            let showDrawerData = {
                webTitle: item.webTitle,
                applicationCode: item.applicationCode,
                robotIcon: item.robotIcon,
                webIcon: item.webIcon,
                userIcon: item.userIcon,
                identityIcon: item.identityIcon,
                templateIcon: "",
                mobileTemplateId: item.mobileTemplateId,
                templateId: item.templateId,
                facadeImageUrl: item.facadeImageUrl,
                backgroundImageUrl: item.backgroundImageUrl,
                greeting: item.greeting,
                inputPlaceholder: item.inputPlaceholder,
            };
            this.setShowDrawerData(showDrawerData);
        },
        openSettingDialog() {
            this.dialogVisible = true;
        },
        setConfig() {
            this.$refs.settingDia.submitAddApp();
        },
        configCloseDialog() {
            this.dialogVisible = false;
        },
        refreshAppList(data) {
            this.component.applicationInfo = JSON.parse(JSON.stringify(data));
        },
        closeDialog() {
            this.fabuSuccessVisible = false;
            this.$emit("configCloseDialog", false);
        },
        runApp() {
            this.$refs.dragDemo.run();
        },

        // 新增应用插件关联表
        addApplicationPluginDataHandler() {
            const applicationId =
                this.$store.state?.workflow?.funcData?.applicationId;
            const pluginList = this.$store.state?.workflow?.funcPluginList;
            addApplicationPluginData({
                applicationId,
                pluginList,
            }).then((res) => {
                if (res.code == "000000") {
                    this.$message.success(this.$t("successed"));
                }
            });
        },
        upadateAbility(val){
          this.component.componentName = val.componentName;
          this.component.componentDesc = val.componentDesc;
          this.component.icon = val.icon;
        },
        submitAddApp(flag) {
            let {nodes,edges,msg} = this.$refs.dragDemo?.getNodesAndEdges(flag, this.component);
            if(!nodes) return false
            if (msg && flag === 2) {
                this.$message({
                    type: "warning",
                    message: msg,
                });
                return;
            }
            const pluginList =
                this.$store.state?.workflow?.funcPluginList || [];
            if (pluginList.length) {
                this.addApplicationPluginDataHandler();
            }
            console.log("nodes", nodes)
            const param = this.$store.state.workflow.funcData;
            let applicationInfo = {
                ...param,
                ...this.$store.state.workflow.showDrawerData,
                virtualHumanFlag: param.virtualHumanFlag ? "是" : "否",
                voiceDialogueFlag: param.voiceDialogueFlag ? "是" : "否",
                modelAnswerFlag: param.modelAnswerFlag ? "是" : "否",
                sensitiveFlag: param.sensitiveFlag ? "是" : "否",
                networkFlag: param.networkFlag ? "是" : "否",
                ipFlag: param.ipFlag ? "是" : "否",
                ocrFlag: param.ocrFlag ? "是" : "否",
                videoFlag: param.videoFlag ? "是" : "否",
                multiDialogueFlag: param.multiDialogueFlag ? "是" : "否",
                knowledgeFlag: param.knowledgeFlag ? "是" : "否",
                rewritingFlag: param.rewritingFlag ? "是" : "否",
                polishFlag: param.polishFlag ? "是" : "否",
                recommendQuestionsShowFlag: param.recommendQuestionsShowFlag
                    ? "1"
                    : "0",
                sourceShowFlag: param.sourceShowFlag ? "1" : "0",
                processStep: param.processStep
                    ? param.processStep.join(",")
                    : "",
                deleteFlag: "0",
                publishStatus: flag == 1 ? "4" : "1", // 发布传1 暂存传4
                makeType: null
            };
            flag === 1 && (this.saveLoading = true);
            flag === 2 && (this.publishLoading = true);
            let canvas  = JSON.stringify(this.$refs.dragDemo.graph.toJSON())
            saveWorkflow({
                ...this.component,
                applicationInfo,
                canvas: canvas,
                nodes,
                nodeRel: edges,
                status: 1,
                updateTime: this.saveTime
            })
                .then((res) => {
                    this.publishLoading = false;
                    this.saveLoading = false;
                    if (res.code == "000000") {
                        // flag === 2 && this.closeDialog();
                        if (flag === 1 || flag === 2) {
                            this.$message({
                                type: "success",
                                message: "已保存",
                            });
                            this.saveTime = res.time?.split(' ')[1]?.split('.')[0];
                            this.queryWorkflowDetail(this.componentId);
                        }
                        if (flag === 5) {
                            this.saveTime = res.time?.split(' ')[1]?.split('.')[0];
                            this.queryWorkflowDetail(this.componentId);
                        }
                    } else {
                        this.$message({
                            type: "error",
                            message: res.msg,
                        });
                    }
                })
                .catch((err) => {
                    this.publishLoading = false
                    this.saveLoading = false
                    this.$message({
                        type: "error",
                        message: err,
                    });
                });
        },
        // 打开弹窗
        openEditDialog() {
          this.$emit('openEditDialog')
        },
        copyHandler() {
            this.copyLoading = true;
            copyWorkflowApp({ componentId: this.componentId })
                .then((res) => {
                    if (res.code == "000000") {
                        this.$message({
                            type: "success",
                            message: this.$t("copySuccessed"),
                        });
                        const componentId = res.data?.component?.componentId;
                        const url = window.location.href?.split("?")[0];
                        window.open(`${url}?workflow_id=${componentId}`);
                    } else {
                        this.$message({
                            type: "error",
                            message: this.$t("copyFailed"),
                        });
                    }
                })
                .finally(() => {
                    this.copyLoading = false;
                });
        },
        copyAppInfo(item) {
            this.$confirm(this.$t("continueCopyApp"), this.$t("tips"), {
                confirmButtonText: this.$t("confirm"),
                cancelButtonText: this.$t("cancel"),
                type: "warning",
            })
                .then(() => {})
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: this.$t("cancelCopy"),
                    });
                });
        },

        queryWorkflowDetail(componentId) {
            //this.workflowLoading = true;
            queryWorkflowDetail({
                componentId,
            })
                .then((res) => {
                    //this.workflowLoading = false;
                    if (res.code == "000000") {
                        this.init(res.data);
						this.$EventBus.$emit("apiEndingNodesList", res.data.nodes);
                        this.saveTime = res.data?.updateTime?.split(' ')[1] || res.time?.split(' ')[1]?.split('.')[0];
                    } else {
                        this.$message({
                            type: "error",
                            message: res.msg,
                        });
                    }
                })
                .catch((err) => {
                    //this.workflowLoading = false;
                    this.$message({
                        type: "error",
                        message: err,
                    });
                });
        },
    },
};
</script>

<style lang="scss" scoped>
.largeModel {
    width: 100%;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 10px 14px;
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

.abilityInter {
    padding: 0 24px;
}

.demo-ruleForm {
    ::v-deep .el-form-item__label {
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 16px;
        color: #383d47;
        line-height: 36px;
    }
    .assModel {
        ::v-deep .el-input__inner {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #383d47;
        }
    }

    .flexOuter {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        width: 100%;
        ::v-deep .el-slider__runway {
            width: 140px;
        }
        > span:first-child {
            display: inline-block;
            width: 120px;
            position: relative;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 16px;
            color: #828894;
        }
    }

    ::v-deep .el-switch .el-switch__label.is-active {
        color: #3666ea;
    }
    ::v-deep .el-switch.is-checked .el-switch__core {
        background: linear-gradient(
            270deg,
            #8e65ff 0%,
            #1c50fd 100%
        ) !important;
        border-radius: 12px;
        border-color: transparent;
    }
    ::v-deep .el-slider__runway {
        height: 4px;
    }
    ::v-deep .el-slider__bar {
        height: 4px;
        background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
        border-radius: 4px;
    }
    ::v-deep .el-slider__button {
        width: 18px;
        height: 18px;
        background: #ffffff;
        box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.12);
        border: 1px solid #f2f5fa;
        margin-top: -4px;
    }
    ::v-deep .el-slider__input {
        width: 110px;
    }
    ::v-deep .el-slider__runway.show-input {
        margin-right: 120px;
    }
}

.modelDebugger {
    height: 56px;
    background: #f2f5fa;

    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1c50fd;
    padding: 15px 20px;
}

.modelName {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #494c4f;
    line-height: 20px;
}

.outerDialog {
    display: flex;
    height: calc(100vh - 80px);
    .dialogPower {
        width: 100%;
        background: #ffffff;
        box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
        border-radius: 4px 0 0 4px;
        // border: 1px solid #e1e4eb;
        position: relative;

        .left-content {
            height: calc(100vh - 230px);
            overflow-y: auto;
        }
        ::-webkit-scrollbar {
            width: 0;
        }
        ::v-deep .el-textarea__inner {
            font-family: MiSans, MiSans;
            &::-webkit-scrollbar {
                width: 12px;
            }
        }
    }
    .previewDebugging {
        flex: 1;
        background: #f2f5fa;
        border-radius: 0px 8px 8px 0px;
        border: 1px solid #e1e4eb;
        padding: 28px 32px 28px 32px;
        box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
        position: relative;
    }
    .headBar {
        background: #F0F4FA!important;
        padding: 16px 32px !important;
        display: flex;
        justify-content: space-between;
        margin-bottom: 0!important;
        width: 100%;
        position: absolute;
        top: 0;
        right: 0;
        border-bottom: 1px solid rgba(0, 0, 0, 0.12);
        
        .leftSlide {
            display: flex;
            justify-content: space-between;
            align-items: center;
            .back-icon {
              width: 20px;
              margin-right: 0 !important;
              height: auto;
            }
            .leftSlide-logo {
                margin-right: 16px;
                cursor: pointer;
            }
            &-logo {
                width: 44px!important;
                height: 44px!important;
                margin: 0 12px 0 16px;
                border-radius: 2px;
            }
            .titleIcon {
                p.tit {
                    display: flex;
                    align-items: center;
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 18px;
                    color: #383d47;
                    line-height: 24px;
                    text-align: left;
                    font-style: normal;
                    margin-bottom: 6px;
                    max-width: 400px;
                    width: fit-content;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    position: relative;
                    padding-right: 20px;
                    .edit-icon {
                      width: 16px;
                      height: 17px;
                      cursor: pointer;
                      position: absolute;
                      right: 0;
                      top: 4px;
                    }
                }
                p.decs {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #828894;
                    line-height: 18px;
                    text-align: left;
                    font-style: normal;
                }
            }
        }
        .rightSlide {
            display: flex;
            justify-content: space-between;
            .preview {
                line-height: 36px;
                margin-right: 28px;
                cursor: pointer;
                > span {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 16px;
                    color: #3666ea;
                    line-height: 24px;
                    text-align: left;
                    font-style: normal;
                    text-transform: none;
                }
                > img {
                    margin-right: 5px;
                }
                > span,
                > img {
                    vertical-align: middle;
                }
            }
            .btn {
                height: 40px;
                color: #3666ea;
                border: 1px solid #3666ea;
                border-radius: 4px;
                background: transparent;
                padding: 10px 20px;
                img {
                    margin-right: 8px !important;
                }
                img,
                span {
                    vertical-align: middle;
                }
            }
            .btns {
                color: #fff;
                height: 40px;
                background: #1C50FD!important;
                border-radius: 4px;
                &-inner {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    iconpark-icon {
                        margin-right: 8px;
                    }
                }
            }
            .copy-btn {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 40px;
                height: 40px;
                background: #fff;
                border-radius: 4px;
                margin-left: 16px;
                cursor: pointer;
            }
        }
    }
}

.marginTop32 {
    margin-top: 32px;
    .widthSpan {
        display: inline-block;
        width: 113px;
        color: #768094 !important;
    }
}
.formTitle {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #383d47;
    line-height: 33px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    margin-bottom: 3px;
    > img {
        width: 16px;
        height: 16px;
    }
    > img,
    span {
        vertical-align: middle;
    }
}

.base-li {
    height: 56px;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 0 12px;
    margin-bottom: 8px;

    .li-name {
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
        text-align: left;
        font-style: normal;

        > img {
            width: 24px;
            height: 24px;
            color: #a4bffe;
            margin-right: 5px;
        }
    }
}

.bottom-btn {
    position: absolute;
    left: 32px;
    bottom: 20px;
    width: calc(100% - 64px);
    height: 40px;
    border-radius: 4px;
    border: 1px solid #3666ea;
    text-align: center;
}

.preview {
    margin-top: 26px;
    height: 144px;
    background: #ffffff;
    border-radius: 16px;
    padding: 20px;

    > img {
        width: 104px;
        height: 104px;
        margin-right: 24px;
    }

    .title1 {
        height: 32px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        line-height: 32px;
        color: #383d47;
        text-align: left;
        font-style: normal;
    }
    .title2 {
        margin-top: 8px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #768094;
    }
}

.question-list {
    display: inline-block;
    height: 48px;
    background: #ffffff;
    border-radius: 2px 12px 12px 12px;
    border: 1px solid #e1e4eb;
    color: #383d47;
    font-size: 16px;
    padding: 0px 16px;
    line-height: 48px;
    margin-top: 8px;
}

.input-box {
    position: absolute;
    left: 32px;
    bottom: 40px;
    width: calc(100% - 64px);
    .prefix {
        width: 40px;
        height: 40px;
        background: #f2f5fa;
        margin-top: 8px;
        border-radius: 4px;

        img {
            width: 15px;
            height: 15px;
            margin-top: 12.5px;
        }
    }

    ::v-deep .el-input__inner {
        height: 56px;
        background: #ffffff;
        box-shadow: 0px 6px 20px 0px rgba(34, 107, 144, 0.1);
        border-radius: 12px;
        border: 1px solid #d1e0fe;
    }
    ::v-deep .el-input__icon {
        width: 40px;
        font-size: 24px;
    }
    ::v-deep .el-input--prefix .el-input__inner {
        padding-left: 60px;
    }
    ::v-deep .el-input--suffix .el-input__inner {
        padding-right: 60px;
    }
}

.dialog {
    margin-top: 20px;
    background: #f2f5fa;
    border-radius: 4px;
    padding: 16px;

    .title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #383d47;
        line-height: 28px;
        text-align: left;
        font-style: normal;
        text-transform: none;
    }
    .func-iconImg {
        width: 20px;
        height: 20px;
        border-radius: 4px;
        margin-right: 8px;
    }
}
.marginTop10 {
    margin-top: 10px;
}
.boxName {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
    text-align: left;
    font-style: normal;
}
.disabled {
    padding: 4px 10px;
    color: #828894;
    background: #e1e4eb;
    border-radius: 20px;
}
.my-el-input-number[data-unit] {
    --el-input-number-unit-offset-x: 45px;
    position: relative;
}
.my-el-input-number[data-unit]::after {
    content: attr(data-unit);
    height: 100%;
    display: flex;
    align-items: center;
    position: absolute;
    top: 0;
    right: var(--el-input-number-unit-offset-x);
    color: #999999;
}
.my-el-input-number[data-unit] .el-input__inner {
    padding-left: 30px;
    padding-right: calc(var(--el-input-number-unit-offset-x) + 12px);
}
.defaultDialo {
    ::v-deep .el-dialog {
        border-radius: 4px;
        .el-dialog__body {
            padding: 16px 32px;
        }
        .el-dialog__header {
            background: linear-gradient(
                180deg,
                rgba(43, 88, 213, 0.1) 0%,
                rgba(43, 88, 213, 0) 100%
            );
            border-radius: 8px 8px 0px 0px;
        }
        .el-dialog__footer {
            text-align: right;
        }
    }
    ::v-deep .el-textarea__inner {
        font-family: MiSans, MiSans;
    }
    .tipsText {
        font-weight: 400;
        font-size: 16px;
        line-height: 20px;
        color: #828894;
    }
}
.dialog-title {
    display: flex;
    align-items: center;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    img {
        width: 20px;
        height: 20px;
        margin-right: 10px;
    }
}
.edit-icon {
    width: 16px !important;
    height: 17px;
    cursor: pointer;
    margin-left: 6px;
}
.abilityTabs {
    .tabs {
        display: inline-block;
        height: 40px;
        line-height: 40px;
        color: #b4bccc;
        font-size: 16px;
        margin-right: 20px;
        cursor: pointer;

        &.active {
            color: #383d47;
            font-size: 16px;
            font-weight: 500;
            border-bottom: 2px solid #1c50fd;
        }
    }
}
::v-deep .subFlexDialog {
    padding: 0 33px 33px;
    .headBar {
        background: #f2f5fa;
        padding: 16px 32px;
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
        width: 100%;
        position: absolute;
        top: 0;
        right: 0;
        img {
            width: 15px;
            height: 15px;
        }
        
        .leftSlide {
            display: flex;
            justify-content: space-between;
            align-items: center;
            .titleIcon {
                p:first-child {
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 20px;
                    color: #383d47;
                    line-height: 24px;
                    text-align: left;
                    font-style: normal;
                    margin-bottom: 6px;
                }
                p:last-child {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #768094;
                    line-height: 18px;
                    text-align: left;
                    font-style: normal;
                }
            }
        }
        .rightSlide {
            display: flex;
            justify-content: space-between;
            .preview {
                line-height: 36px;
                margin-right: 28px;
                cursor: pointer;
                > span {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 16px;
                    color: #3666ea;
                    line-height: 24px;
                    text-align: left;
                    font-style: normal;
                    text-transform: none;
                }
                > img {
                    margin-right: 5px;
                }
                > span,
                > img {
                    vertical-align: middle;
                }
            }
            .btn {
                height: 40px;
                color: #3666ea;
                border: 1px solid #3666ea;
                border-radius: 4px;
                padding: 10px 20px;
                img {
                    margin-right: 8px !important;
                }
                img,
                span {
                    vertical-align: middle;
                }
            }
            .btns {
                color: #fff;
                height: 40px;
                background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
                border-radius: 4px;
                img {
                    margin-right: 5px;
                }
                img,
                span {
                    vertical-align: middle;
                }
            }
        }
    }
    .dialogTitle {
        margin-top: 30px;
        > img {
            width: 22px;
            height: 22px;
            margin-right: 4px;
        }
        > span {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 20px;
            color: #383d47;
            line-height: 28px;
            text-align: left;
            font-style: normal;
        }
        > img,
        > span {
            vertical-align: middle;
        }
    }
    .el-dialog__body {
        background: #fff;
        padding: 0 0 20px;
        position: relative;
        .verticalLine {
            width: 1px;
            background: rgba(0, 0, 0, 0.12);
            height: calc(100% + 32px);
            position: absolute;
            right: 0;
            top: 0;
        }
        .formOuter {
            width: 576px;
            margin-top: 24px;
            padding-right: 30px;
            .el-textarea {
                .el-textarea__inner {
                    border-radius: 4px;
                    border: 1px solid #e1e4eb;
                }
            }
            .el-input:not(.el-input-group) {
                .el-input__inner {
                    border-radius: 4px;
                    border: 1px solid #e1e4eb;
                }
            }
            .marginTop32 {
                margin-top: 32px;
                .el-upload-list__item {
                    display: flex;
                    align-items: center;
                }
                .identityUpload {
                    .el-upload-list--picture-card
                        .el-upload-list__item-thumbnail {
                        height: auto !important;
                    }
                    .el-upload--picture-card {
                        width: 104px;
                        height: 104px;
                        border: none;
                        background: #f2f4f7;
                        > div {
                            height: 0;
                            background: pink;
                            position: relative;
                            top: -19px;
                        }
                    }
                    .el-upload-list__item {
                        width: 104px;
                        height: 104px;
                    }
                }
            }
            .formTitle {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 16px;
                color: #383d47;
                line-height: 33px;
                text-align: left;
                font-style: normal;
                text-transform: none;
                margin-bottom: 3px;
                > img {
                    width: 16px;
                    height: 16px;
                }
                > img,
                span {
                    vertical-align: middle;
                }
            }
            .uploadImg {
                display: flex;
                justify-content: space-between;
                .logoUpload {
                    .el-upload-list__item {
                        display: flex;
                        align-items: center;
                    }
                    .el-upload-list--picture-card
                        .el-upload-list__item-thumbnail {
                        height: auto !important;
                    }
                    .el-upload--picture-card {
                        width: 104px;
                        height: 104px;
                        border: none;
                        background: #f2f4f7;
                        > div {
                            height: 0;
                            background: pink;
                            position: relative;
                            top: -19px;
                        }
                    }
                    .el-upload-list__item {
                        width: 104px;
                        height: 104px;
                    }
                }
                .el-textarea {
                    margin-top: 16px;
                }
            }
            .formFlexOuter {
                display: flex;
                justify-content: flex-start;
                margin-top: 24px;
                .el-input-number__increase {
                    border-radius: 0 4px 4px 0;
                }
                .el-input-number__decrease {
                    border-radius: 4px 0 0 4px;
                }
                .assModel {
                    .el-input__inner {
                        height: 56px;
                        font-size: 16px;
                        color: #000;
                        font-weight: 500;
                    }
                }
                .robotImg {
                    margin-left: 32px;
                    position: relative;
                    .el-upload-list--picture-card
                        .el-upload-list__item-thumbnail {
                        height: auto !important;
                    }
                    .el-upload-list__item {
                        width: 40px !important;
                        height: 40px !important;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                    }
                    .el-upload--picture-card {
                        width: 40px;
                        height: 40px;
                        border-radius: 50%;
                        position: relative;
                        border: none;
                        background: #f2f4f7;
                        .el-icon-plus {
                            font-size: 14px;
                            position: absolute;
                            top: calc(50% - 7px);
                            right: calc(50% - 7px);
                        }
                    }
                }
                .widthSpan {
                    display: inline-block;
                    width: 113px;
                    color: #768094 !important;
                }
                .el-switch__label > span {
                    font-size: 16px !important;
                }
            }
        }
        .collapseStyle {
            border-top: none;
            .el-collapse-item__arrow {
                display: none;
            }
            .el-collapse-item__header {
                position: relative;
            }
            .el-collapse-item {
                .svgTransform {
                    transform: rotate(-90deg);
                    transition: all 0.2s;
                }
                &.is-active {
                    .svgTransform {
                        transform: rotate(0deg);
                    }
                }
            }
            .el-collapse-item__wrap {
                padding-left: 10px;
            }
            .collapseHeader {
                > div {
                    > span {
                        color: #383d47;
                        font-size: 16px !important;
                        font-weight: 500;
                    }
                }
                img,
                span {
                    vertical-align: middle;
                }
            }
        }
    }
}
</style>
