<template>
    <div class="container">
        <div class="create-voice" :style="{width: effectFlag ? '55%' : '100%'}">
            <div class="create-voice-head">
                <div style="display: flex;flex-direction: row; align-items: center;">
                    <div @click="back()" class="backBtn"><img src="@/assets/images/arrow-go-back-fill.svg" alt=""></div>
                    <div class="create-voice-head-left">
                        <div class="voiceImg" v-if="info.category==='语音识别'"><img src="@/assets/images/voiceOutput.svg" alt=""></div>
                        <div class="voiceImg" v-else><img src="@/assets/images/voiceIdentify.svg" alt=""></div>
                        <div class="text">
                            <div>
                                <span class="title">{{info.componentName}}</span>
                                <span @click="editPop(info)"><i class="el-icon-edit" style="cursor:pointer;color:#828894"></i></span>
                            </div>
                            <div class="descript"><span>{{info.introduce}}</span></div>
                        </div>
                    </div>
                </div>
                <div style="display:flex;align-items: center;">
                    <el-button type="primary" style="height: 40px; border-radius: 2px" @click="saveBtn(info.category==='语音识别'?'speechToTextForm':'textToSpeechForm')">
                    <div class="create-voice-head-button">
                        <iconpark-icon name="save-line" color="#fff" size="16"></iconpark-icon><span>{{$t('save')}}</span>
                    </div>
                </el-button>
                <el-button @click="effectFlag=true" style="height: 40px; border-radius: 2px">{{$t("effectTest")}}</el-button>
                </div>

            </div>
            <div class="create-voice-content">
            <!-- <div class="info" :class="[info.category==='语音识别' ? 'speechToText' : 'textToSpeech']">
                <div class="name">
                <img
                    v-if="baseInfoForm?.headPortraitUrl"
                    :src="baseInfoForm.headPortraitUrl"
                    alt=""
                />
                <img v-else src="@/assets/images/default-voice.png" alt="" />
                <div>{{ baseInfoForm.componentName }}</div>
                </div>
                <div class="introduct">
                {{ baseInfoForm.introduce }}
                </div>
                <ul>
                <li class="title">{{$t('authInfo')}}</li>
                <li>
                    <div class="label">APP ID</div>
                    <div class="value">{{ baseInfoForm.appId }}</div>
                </li>
                <li>
                    <div class="label">APP Secret</div>
                    <div class="value">{{ baseInfoForm.apiSecret }}</div>
                </li>
                <li>
                    <div class="label">APP Key</div>
                    <div class="value">{{ baseInfoForm.apiKey }}</div>
                </li>
                <li class="edit-btn" @click="editBaseInfo">
                    <iconpark-icon name="edit-line" color="#1C50FD"></iconpark-icon>{{$t('edit')}}
                </li>
                </ul>
            </div> -->
            <div class="config no-scrollbar">
                <div class="title">
                <!-- <img src="@/assets/images/config-img.png" alt="" /> -->
                <div>{{$t('configItem')}}</div>
                </div>
                <!-- 语音转文本 -->
                <div v-if="info.category==='语音识别'" class="form-box">
                <el-form
                    :model="speechToTextForm"
                    ref="speechToTextForm"
                    :rules="rulesText"
                >
                    <el-form-item :label="$t('language')" prop="language">
                        <el-select v-model="speechToTextForm.language">
                            <el-option
                            v-for="(item, index) in languagesList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="$t('applicationField')" prop="domain">
                        <el-select v-model="speechToTextForm.domain">
                            <el-option
                            v-for="(item, index) in domainList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="$t('dialect')" prop="dialect">
                        <template slot="label">
                                <span>
                                {{$t('dialect')}}
                                </span>
                                <!-- 开启动态修正 -->
                                <!-- <span style="position: relative;left: 314px;">{{$t('openActive')}} <el-switch
                                    v-model="speechToTextForm.status"
                                    active-color="#1747E5"
                                    inactive-color="#CED4E0">
                                    </el-switch>
                                </span> -->
                            <!-- </div> -->
                        </template>
                        <el-select v-model="speechToTextForm.accent">
                            <el-option
                            v-for="(item, index) in accentList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="$t('domainPersonal')" prop="domainPersonal">
                        <el-select v-model="speechToTextForm.domainPersonal">
                            <el-option
                            v-for="(item, index) in domainList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <!-- <el-form-item
                    v-if="speechToTextForm.language == 'zh_cn'"
                    :label="$t('dialect')"
                    prop="accent"
                    >
                    <el-select v-model="speechToTextForm.accent">
                        <el-option
                        v-for="(item, index) in accentList"
                        :key="index"
                        :label="item.label"
                        :value="item.value"
                        />
                    </el-select>
                    </el-form-item> -->
                    <el-form-item :label="$t('silenceTime')">
                    <div class="flex w-full">
                        <el-slider v-model="speechToTextForm.vadEos"></el-slider>
                        <el-input-number
                        v-model="speechToTextForm.vadEos"
                        controls-position="right"
                        @change="handleChange"
                        >
                        </el-input-number>
                        <span style="position:relative;left:-62px;z-index:9;width:0;color:#BCC1CC;">ms</span>
                    </div>
                    </el-form-item>
                    <el-form-item :label="$t('font')">
                    <el-select v-model="speechToTextForm.rlang">
                        <el-option
                        v-for="(item, index) in rlangList"
                        :key="index"
                        :label="item.label"
                        :value="item.value"
                        />
                    </el-select>
                    </el-form-item>
                    <el-form-item label="nbest">
                    <div class="flex w-full">
                        <el-slider v-model="speechToTextForm.nbest" :max="5" :min="0"></el-slider>
                        <el-input-number
                        v-model="speechToTextForm.nbest" :max="5" :min="0"
                        controls-position="right"
                        @change="handleChange"
                        ></el-input-number>
                    </div>
                    </el-form-item>
                    <el-form-item label="wbest">
                    <div class="flex w-fulls">
                        <el-slider v-model="speechToTextForm.wbest" :max="5" :min="0"></el-slider>
                        <el-input-number
                        v-model="speechToTextForm.wbest" :max="5" :min="0"
                        controls-position="right"
                        @change="handleChange"
                        ></el-input-number>
                    </div>
                    </el-form-item>
                    <el-form-item label="">
                    <el-switch
                        style="display: block"
                        v-model="speechToTextForm.ptt"
                        :active-text="$t('enablePunctuation')"
                    >
                    </el-switch>
                    </el-form-item>
                    <el-form-item label="">
                    <el-switch
                        style="display: block"
                        v-model="speechToTextForm.vinfo"
                        :active-text="$t('enableEndpointOffset')"
                    >
                    </el-switch>
                    </el-form-item>
                    <el-form-item label="">
                    <el-switch
                        style="display: block"
                        v-model="speechToTextForm.nunum"
                        :active-text="$t('arabicNumberFormat')"
                    >
                    </el-switch>
                    </el-form-item>
                </el-form>
                </div>
                <!-- 文本转语音 -->
                <div v-else class="form-box">
                    <el-form :model="textToSpeechForm" ref="textToSpeechForm" :rules="rulesVoice">
                        <el-form-item :label="$t('speaker')">
                        <el-select v-model="textToSpeechForm.voicePeople">
                            <el-option
                            v-for="(item, index) in voicePeopleList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item :label="$t('speechRate')">
                        <div class="flex w-full">
                            <el-slider v-model="textToSpeechForm.voiceSpeed"></el-slider>
                            <el-input-number
                            v-model="textToSpeechForm.voiceSpeed"
                            :min="0" :max="100"
                            controls-position="right"
                            @change="handleChange"
                            ></el-input-number>
                        </div>
                        </el-form-item>
                        <el-form-item :label="$t('volume')">
                        <div class="flex w-full">
                            <el-slider v-model="textToSpeechForm.volume"></el-slider>
                            <el-input-number
                            v-model="textToSpeechForm.volume"
                            :min="0" :max="100"
                            controls-position="right"
                            @change="handleChange"
                            ></el-input-number>
                        </div>
                        </el-form-item>
                        <el-form-item :label="$t('pitch')">
                        <div class="flex w-full">
                            <el-slider v-model="textToSpeechForm.pitch"></el-slider>
                            <el-input-number
                            v-model="textToSpeechForm.pitch"
                            :min="0" :max="100"
                            controls-position="right"
                            @change="handleChange"
                            >
                            </el-input-number>
                        </div>
                        </el-form-item>
                        <el-form-item :label="$t('audioEncoding')" prop="audioFrequencyEncoding">
                        <el-select v-model="textToSpeechForm.audioFrequencyEncoding">
                            <el-option
                            v-for="(item, index) in audioFrequencyEncodingList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item :label="$t('audioSampleRate')">
                        <el-select v-model="textToSpeechForm.audioSampleRate">
                            <el-option
                            v-for="(item, index) in audioSampleRateList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item :label="$t('textEncodingFormat')">
                        <el-select v-model="textToSpeechForm.textEncodingFormat">
                            <el-option
                            v-for="(item, index) in textEncodingFormatList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item :label="$t('numberPronunciation')">
                        <el-select v-model="textToSpeechForm.numberPronunciationType">
                            <el-option
                            v-for="(item, index) in numberPronunciationTypeList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item :label="$t('EnglishPronunciationMethod')">
                        <el-select v-model="textToSpeechForm.EnglishPronunciationTypeList">
                            <el-option
                            v-for="(item, index) in EnglishPronunciationTypeList"
                            :key="index"
                            :label="item.label"
                            :value="item.value"
                            />
                        </el-select>
                        </el-form-item>
                        <el-form-item label="">
                        <el-switch
                            style="display: block"
                            v-model="textToSpeechForm.streamFlag"
                            :active-text="$t('enableStreamingReturn')"
                        >
                        </el-switch>
                        </el-form-item>
                        <el-form-item label="">
                        <el-switch
                            style="display: block"
                            v-model="textToSpeechForm.bgm"
                            :active-text="$t('enableBackgroundSound')"
                        >
                        </el-switch>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
            
            </div>
        </div>
        <div class="effect" v-if="effectFlag">
            <div class="title">
            <!-- <img src="@/assets/images/effect-img.png" alt="" /> -->
                <div>{{$t('effectTest')}}</div>
                <div style="cursor: pointer;" @click="effectFlag=false"><i class="el-icon-close"></i></div>
            </div>
            <div v-if="info.category==='语音识别'">
                <el-upload
                    class="upload-demo" style="width: 100%;"
                    drag
                    action="https://jsonplaceholder.typicode.com/posts/"
                    multiple
                >
                    <div class="upload-outer flex">
                    <iconpark-icon
                        name="upload-cloud-2-line"
                        size="36"
                        color="#B4BCCC"
                    ></iconpark-icon>
                    <div class="upload-outer-right">
                        <div class="row1">{{$t('dragFileHereOr')}}<span>{{$t('selectFile')}}</span></div>
                        <div class="row2">{{$t('supportedDocumentFormats')}}</div>
                        <div class="row2">{{$t('sizeLimit')}}</div>
                    </div>
                    </div>
                </el-upload>
                <div class="effect-btn" @click="convertToText()">{{$t('convertToText')}}</div>
            </div>
            <div v-else>
                <el-input
                    v-model="textValue"
                    type="textarea"
                    :rows="5"
                    maxlength="2000"
                    show-word-limit
                />
                <!-- <div class="effect-btn">{{$t('generateVoice')}}</div> -->
                <div class="effect-btn">{{$t('convertToText')}} {{info.category}}</div>
            </div>
            <div class="subtitle">{{$t('outputResult')}}</div>
            <div v-if="info.category==='语音输出'" class="effect-result">{{$t('designToolBenefits1')}}</div>
            <div v-if="info.category==='语音输出'" class="effect-result">{{$t('designToolBenefits2')}}</div>
            <div v-if="info.category==='语音输出'" class="effect-result">{{$t('designToolBenefits3')}}</div>
            <div v-else class="effect-result">
                <audio id="audioPlayer" src="your-audio-file.mp3" controls style="width: 100%;"></audio>
            </div>
        </div>
    </div>
</template>

<script>
import { apiAddInfo } from "@/api/toolManager";
// 语种
const languagesList = [
  {
    label: "中文",
    value: "zh_cn",
  },
  {
    label: "英文",
    value: "en_us",
  },
];
// 应用领域
const domainList = [
  {
    label: "日常用语",
    value: "iat",
  },
  {
    label: "医疗",
    value: "medical",
  },
  {
    label: "政务坐席助手",
    value: "gov-seat-assistant",
  },
  {
    label: "金融坐席助手",
    value: "seat-assistant",
  },
  {
    label: "政务语音分析",
    value: "gov-ansys",
  },
  {
    label: "政务语音导航",
    value: "gov-nav",
  },
  {
    label: "金融语音导航",
    value: "fin-nav",
  },
  {
    label: "金融语音分析",
    value: "fin-ansys",
  },
];
// 方言
const accentList = [
  {
    label: "中文普通话、其他语种",
    value: "mandarin",
  },
];
// 字体
const rlangList = [
  {
    label: "简体中文",
    value: "zh-cn",
  },
  {
    label: "繁体香港",
    value: "zh-hk",
  },
];
// 发音人
const voicePeopleList = [
  {
    label: "讯飞小燕",
    value: "xiaoyan",
  },
  {
    label: "讯飞许久",
    value: "aisjiuxu",
  },
  {
    label: "讯飞小萍",
    value: "aisxping",
  },
  {
    label: "讯飞小婧",
    value: "aisjinger",
  },
  {
    label: "讯飞许小宝",
    value: "aisbabyxu",
  },
  {
    label: "魔音工坊-魔小昭",
    value: "moxiaozhao_meet_24k@warm",
  },
  {
    label: "魔音工坊-采采",
    value: "caicai_meet_24k@assistant",
  },
  {
    label: "魔音工坊-魔依梦",
    value: "moyimeng_meet_24k",
  },
]
// 音频编码
const audioFrequencyEncodingList = [
  {
    label: "原生音频（支持单声道的pcm）",
    value: "raw",
  },
  {
    label: "speex压缩后的音频（8k）",
    value: "speex",
  },
  {
    label: "speex压缩后的音频（16k）",
    value: "speex-wb",
  },
  {
    label: "mp3格式（仅中文普通话和英文支持，方言及小语种暂不支持）",
    value: "lame",
  },
]
// 音频采样率
const audioSampleRateList = [
  {
    label: "audio/L16;rate=16000",
    value: "16k音频",
  },
  {
    label: "audio/L16;rate=8000",
    value: "8k音频",
  },
]
// 文本编码格式
const textEncodingFormatList = [
    {
    label: "GB2312",
    value: "GB2312",
  },
  {
    label: "GBK",
    value: "GBK",
  },
  {
    label: "BIG5",
    value: "BIG5",
  },
  {
    label: "UNICODE(小语种必须使用UNICODE编码，合成的文本需使用utf16小端的编码方式，详见java示例demo)",
    value: "UNICODE(小语种必须使用UNICODE编码，合成的文本需使用utf16小端的编码方式，详见java示例demo)",
  },
  {
    label: "GB18030",
    value: "GB18030",
  },
  {
    label: "UTF8（小语种）",
    value: "UTF8（小语种）",
  },
]
// 数字发音方式
const numberPronunciationTypeList = [
    {
    label: "自动判断（默认值）",
    value: "0",
  },
  {
    label: "完全数值",
    value: "1",
  },
  {
    label: "完全字符串",
    value: "2",
  },
  {
    label: "字符串优先",
    value: "3",
  },
]
// 英文发音方式
const EnglishPronunciationTypeList = [
    {
    label: "自动判断处理，如果不确定将按照英文词语拼写处理（缺省）",
    value: "0",
  },
  {
    label: "所有英文按字母发音",
    value: "1",
  },
  {
    label: "自动判断处理，如果不确定将按照字母朗读",
    value: "2",
  },
]
export default {
    props: {
        // type: {
        //     type: Number, // 1 - 语音转文本 语音识别 2-文本转语音 语音输出
        // },
        isChange: {
            type: Boolean,
        },
        sourceData: {
            type: Object,
            default: () => {
              return {}
            },
        },
        info: {
            type: Object,
            default: () => {
              return {}
            }
        }
  },
  data() {
    return {
      languagesList,
      domainList,
      accentList,
      dialectList: [],
      rlangList,
      voicePeopleList,
      audioFrequencyEncodingList,
      audioSampleRateList,
      textEncodingFormatList,
      numberPronunciationTypeList,
      EnglishPronunciationTypeList,
      baseInfoForm: {
        componentName: "", // 名称
        introduce: "", // 描述
        headPortraitUrl: "", // 头像
        appId: "", // API Id
        apiSecret: "", // API Secret
        apiKey: "", // API Key
      },
      // 语音转文本表单
      speechToTextForm: {
        language: "", // 语种
        domain: "", // 应用领域
        accent: "", // 方言
        // vinfo: 0, // 开启动态修正 0：关闭（默认值）1：开启
        vadEos: "", // 静默时间
        rlang: "", // 字体
        nbest: "", // nbest 取值范围[1,5]
        wbest: "", // wbest 取值范围[1,5]
        ptt: 1, // 开启标点符号添加 仅中文支持 1：开启（默认值）0：关闭
        vinfo: false, // 开启端点帧偏移 默认值：false
        nunum: 1, // 阿拉伯数字格式 中文普通话和日语支持 0：关闭  1：开启
        // status: false
      },
      // 文本转语音表单
      textToSpeechForm: {
        voicePeople: "", // 发音人
        voiceSpeed: "", // 语速
        volume: "", // 音量
        pitch: "", // 音高
        audioFrequencyEncoding: "", // 音频编码
        audioSampleRate: "", // 音频采样率
        textEncodingFormat: "", // 文本编码格式
        numberPronunciationType: "", // 数字发音方式
        streamFlag: false, // 开启流式返回
        bgm: false, // 开启背景音
      }, 
      // 配置项 语音输出
      rulesText: {
        language: [
          { required: true, message: "请选择语种", trigger: "change" },
        ],
        domain: [
          { required: true, message: "请选择应用领域", trigger: "change" },
        ],
        accent: [
          { required: true, message: "请选择方言", trigger: "change" },
        ],
        domainPersonal: [
          { required: false, message: "请选择个性化参数", trigger: "change" },
        ],
      },
      // 配置项 语音识别
      rulesVoice: {
        audioFrequencyEncoding: [
          { required: true, message: "请选择", trigger: "change" },
        ],
        
      },
      textValue: "",
      effectFlag:  false
    };
  },
  watch: {
    isChange: {
      handler(n) {
        for (let key in this.sourceData) {
          this.baseInfoForm[key] = this.sourceData[key];
        }

        console.log("baseInfoForm", this.baseInfoForm);
      },
      deep: true,
      immediate: true,
    },
    // sourceData: {
    //   handler(n) {
    //     for (let key in n) {
    //       this.baseInfoForm[key] = n[key];
    //     }

    //     console.log("baseInfoForm", this.baseInfoForm);
    //   },
    //   deep: true,
    //   immediate: true,
    // },
  },
  methods: {
    // 返回列表页
    back() {
      this.$emit("back");
    },
    handleChange() {},
    // 编辑基本西悉尼
    editBaseInfo() {
      this.$emit("openDialog");
    },
    editPop(item) {
        this.$emit("edit",{type:'edit',item})
    },
    saveBtn(formName) {
         this.$refs[formName].validate((valid) => {
          if (valid) {
            // 语音输出;
            if(formName == 'textToSpeechForm'){
                let params = {
                    ...this.textToSpeechForm
                }

                apiAddInfo(params).then(res=> {
                    if(res.code == '000000'){
                        
                    }
                })
            }else {
                let params = {
                        category: this.info.category, // 类型
                        componentName: this.info.componentName, // 名称
                        introduce: this.info.introduce, // 描述
                        appId: this.info.appId, // APP ID
                        apiSecret: this.info.apiSecret, // API Secret
                        apiKey: this.info.apiKey, // API Key   
                    ...this.speechToTextForm,
                    ptt: this.speechToTextForm.ptt ? 1 : 0,
                    vinfo: this.speechToTextForm.vinfo ? 1 : 0,
                    nunum: this.speechToTextForm.nunum ? 1 : 0,
                }

            apiAddInfo(params).then(res=> {
                if(res.code == '000000'){
                    
                }
            })
            }
          } else {
            // console.log('error submit!!');
            return false;
          }
        });
    },
    convertToText() {
        
    }
  },
  mounted(){
    console.log(this.$props,'数据')
    console.log(this.$attrs,'剩余数据')
  }
};
</script>

<style lang="scss" scoped>
main {
    background: #F2F4F7;
}
.flex {
  display: flex;
}
.w-full {
  width: 100%;
}
::v-deep .el-upload {
        width: 100%;
    }
    ::v-deep .el-upload-dragger {
        width: 100%;
        height: 120px;
    }
.container {
    display: flex;
    height: 100%;
    // background: #fff;
    .create-voice {
        padding: 24px 12px;
        width: 60%;
        height: 100%;
        background: #fff;

    ::v-deep .el-form-item {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        margin-bottom: 20px;

        .el-form-item__label {
        height: 20px;
        line-height: 20px;
        margin-bottom: 8px;
        }
        .el-form-item__content {
        width: 100%;
        }
        .el-select {
        width: 100%;
        }
    }

    ::v-deep .el-input-number {
        width: 128px;
    }

    ::v-deep .el-slider {
        flex: 1;
        padding: 0 12px;
    }

    ::v-deep .el-switch__core {
        width: 32px !important;
        height: 20px;
        border-radius: 12px;
        background: #ced4e0;
        border: none;
        &::after {
        width: 14px;
        height: 14px;
        top: 3px;
        }
    }

    ::v-deep .el-switch.is-checked .el-switch__core {
        background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
        &::after {
        width: 14px;
        height: 14px;
        top: 3px;
        }
    }

    
    ::v-deep .el-upload-dragger {
        width: 100%;
        height: 120px;
    }
    ::v-deep .el-slider__bar {
        background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    }
    ::v-deep .el-slider__button {
        background: #ffffff;
        box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.12);
        border: 1px solid #f2f5fa;
    }

    iconpark-icon {
        cursor: pointer;
    }

    &-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: -8px;
        .backBtn {
            margin-right: 16px;
            cursor: pointer;
            width: 20px;
            height: 20px;
            
            img {
                width: 100%;
                height: 100%;
            }
        }
        &-left {
        display: flex;
        align-items: center;
        .voiceImg {
            height: 40px;
            width: 40px;
            img {
                width: 100%;
                height: 100%;
            }
        }
        .text {
            display: flex;
            flex-direction: column;
            align-items: start;

        }
        .descript {
            font-size: 14px;
            color: #828894;
            line-height: 20px;
            margin: 0 8px 0 16px;
        }
        .title {
            margin: 0 8px 0 16px;
            font-weight: 800;
            font-size: 18px;
            color: #494E57;
            line-height: 24px;
            text-align: left;
        }
        }
        &-button {
        display: flex;
        align-items: center;
        iconpark-icon {
            margin-right: 6px;
        }
        }
    }
    &-content {
        display: flex;
        margin-top: 16px;
        width: 100%;
        height: calc(100% - 24px);
        background: #fff;
        border-radius: 4px;
        // border: 1px solid #e1e4eb;
        .info {
        width: 287px;
        height: 100%;
        border-radius: 8px 0px 0px 8px;
        padding: 24px;
        .name {
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 18px;
            color: #383d47;
            img {
            width: 48px;
            height: 48px;
            margin-right: 16px;
            border-radius: 50%;
            }
        }
        .introduct {
            margin-top: 16px;
            height: 38px;
            font-family: MiSans, MiSans;
            font-size: 14px;
            line-height: 19px;
            color: #828894;
        }
        .title {
            margin: 26px 0 16px;
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #383d47;
        }
        .label {
            height: 18px;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #828894;
            line-height: 18px;
        }
        .value {
            margin-top: 2px;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
            line-height: 20px;
        }
        .edit-btn {
            margin-top: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 40px;
            border-radius: 4px;
            border: 1px solid #1c50fd;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #1c50fd;
            cursor: pointer;
            iconpark-icon {
            margin-right: 6px;
            }
        }
        li {
            margin-bottom: 16px;
        }
        }
        .speechToText {
        background: url("~@/assets/images/speechToText.png") no-repeat 140px
            bottom / 170px auto,
            linear-gradient(
            180deg,
            rgba(43, 88, 213, 0.1) 0%,
            rgba(43, 88, 213, 0) 100%
            );
        }
        .textToSpeech {
        background: url("~@/assets/images/textToSpeech.png") no-repeat 140px
            bottom / 170px auto,
            linear-gradient(
            180deg,
            rgba(43, 88, 213, 0.1) 0%,
            rgba(43, 88, 213, 0) 100%
            );
        }
        .config {
        width: 544px;
        height: 750px;
        padding: 24px;
        overflow-y: scroll;
        //   border-right: 1px solid rgba(0, 0, 0, 0.12);
        .title {
            display: flex;
            align-items: center;
            margin-bottom: 26px;
            height: 28px;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 20px;
            color: #383d47;
            img {
            width: 24px;
            height: 24px;
            margin-right: 8px;
            }
        }
        }
        
    }
    .form-box {
        width: 100%;
        height: calc(100% - 54px);
        overflow-y: auto;
        &::-webkit-scrollbar {
        display: none;
        }
    }
    }
    .effect {
        flex: 1;
        // height: 100%;
        padding: 34px 32px;
        background: #F2F4F7;
        .title {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            height: 28px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 20px;
            color: #494E57;
            img {
            width: 24px;
            height: 24px;
            margin-right: 8px;
            }
        }
        .subtitle {
            margin-top: 32px;
            margin-bottom: 16px;
            height: 28px;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 18px;
            color: #383d47;
            line-height: 28px;
            display: flex;
            align-items: center;
            &::before {
            content: "";
            display: block;
            width: 3px;
            height: 18px;
            background: #1c50fd;
            margin-right: 8px;
            }
        }
        &-btn {
            margin-top: 20px;
            width: 100%;
            height: 40px;
            line-height: 40px;
            text-align: center;
            border-radius: 4px;
            border: 1px solid #1747E5;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #1747E5;
            cursor: pointer;
        }
        &-result {
            width: 100%;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #828894;
            line-height: 26px;
        }
        .upload-outer {
            width: 100%;
            height: 100%;
            background: #f9fafc;
            padding-top: 28px;
            align-items: flex-start;
            justify-content: center;
            &-right {
            text-align: left;
            margin-left: 18px;
            }
            .row1 {
            height: 20px;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
            line-height: 20px;
            margin-bottom: 4px;
            span {
                color: #1c50fd;
            }
            }
            .row2 {
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #b4bccc;
            line-height: 20px;
            }
        }
    }
}
</style>