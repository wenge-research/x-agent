<template>
  <el-drawer
    :title="row ? $t('editSensitiveWords') : $t('addSensitiveWords')"
    :visible.sync="addQaVisible"
    :modal-append-to-body="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :wrapperClosable="false"
    @close="cancelSubmit"
  >
    <div slot class="qa-box">
      <div class="content">
        <el-form :model="setForm" ref="setForm" :rules="rules">
          <el-form-item :label="$t('classification')" prop="type">
            <el-select
              v-model="setForm.type"
              :placeholder="$t('selectPlaceholder')"
              clearable
            >
              <el-option
                v-for="item in verifyStatusColumns"
                :key="item.id"
                :label="item.type_name"
                :value="item.type_name"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('keywords')" prop="content">
            <el-input
              v-model="setForm.content"
              :placeholder="$t('pleaseEnter')"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('remarks')">
            <el-input
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 6 }"
              v-model="setForm.remark"
              :placeholder="$t('pleaseEnter')"
            ></el-input>
          </el-form-item>
        </el-form>
        <div class="flex">
          <div class="box"></div>
          <div class="name">
            {{ $t("handlingMethod") }}
          </div>
        </div>
        <div class="handle-list">
          <div class="handle-item">
            <el-select
              v-model="processList.way"
              :placeholder="$t('selectPlaceholder')"
              clearable
            >
              <el-option
                v-for="item in handlingList"
                :key="item.id"
                :label="item.method_name"
                :value="item.method_name"
              ></el-option>
            </el-select>
            <!-- <img
              :src="require('@/assets/images/delete-bin-4-line1.svg')"
              style="width: 17px; height: 17px"
            /> -->
          </div>
          <div
            v-for="(itemData, index) in handledingListData"
            :key="index"
            class="handle-item"
          >
            <el-select
              v-model="itemData.name"
              :placeholder="$t('selectPlaceholder')"
              clearable
              style="width: 140px; margin-right: 8px"
              @change="handleSelect"
            >
              <el-option
                v-for="item in handlingFourList"
                :key="item.name"
                :label="item.name"
                :value="item.name"
                :disabled="item.disabled"
              ></el-option>
            </el-select>
            <el-input
              v-model="itemData.content"
              :placeholder="placrholdername[itemData.name]"
              class="input"
            ></el-input>
            <img
              :src="require('@/assets/images/delete-bin-4-line1.svg')"
              style="width: 17px; height: 17px"
              @click="deleteClick(itemData.name, index)"
            />
          </div>
          <div style="margin-top: 12px" v-if="handledingListData.length < 4">
            <el-button plain class="addbtn" @click="addNewHandleing">
              <div class="flex flex-just">
                <img
                  src="@/assets/images/add-line2.svg"
                  style="width: 17px; height: 17px; margin-right: 4px"
                />
                <span class="add-name">{{ $t("appendTo") }}</span>
              </div>
            </el-button>
          </div>
        </div>
      </div>
      <div class="dialog-footer">
        <el-button :loading="submitLoading" @click="cancelSubmit">{{
          $t("cancel")
        }}</el-button>
        <el-button :loading="submitLoading" type="primary" @click="handleAddQaDialog">{{
          $t("confirm")
        }}</el-button>
      </div>
    </div>
  </el-drawer>
</template>
<script>
import { getInterceptWordHandlingMethodList, addInterceptWord, updateInterceptWord } from "@/api/toolManager";
export default {
  props: {
    addQaVisible: {
      type: Boolean,
      default: false,
    },
    row: {
      type: Boolean,
      default: false,
    },
    verifyStatusColumns: {
      type: Array,
      default: () => [],
    },
    sensitiveRow: {
      type: Object,
      default: () => {},
    },
    sensitiveWordList: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      uploadForm: new FormData(),
      uploadFile: [],
      downLoading: false,
      setForm: {},
      submitLoading: false,
      rules: {
        content: [{ required: true, message: this.$t("pleaseEnter"), trigger: "blur" }],
        type: [{ required: true, message: this.$t("pleaseSelect"), trigger: "change" }],
      },
      handledingListData: [],
      handlingList: [],
      handlingForm: {},
      handlingFourList: [
        {
          name: this.$t("limitedAnswer"),
        },
        {
          name: this.$t("addPrefix"),
        },
        {
          name: this.$t("addSuffix"),
        },
        {
          name: this.$t("replacementIssues"),
        },
      ],
      handleWordList: {
        answer: this.$t("limitedAnswer"),
        preQuestion: this.$t("addPrefix"),
        extendQuestion: this.$t("addSuffix"),
        replaceQuestion: this.$t("replacementIssues"),
      },
      placrholdername: {
        "": this.$t("pleaseEnter"),
        限定答案: this.$t("enterALimitedAnswer"),
        添加前缀: this.$t("enterPrefix"),
        添加后缀: this.$t("enterSuffix"),
        替换问题: this.$t("enterTheReplacementContent"),
      },
      processList: {
        way: ""
      },
    };
  },
  watch: {
    sensitiveWordList(val) {
      if (this.row) {
        this.setForm = JSON.parse(JSON.stringify(val));
        let process = JSON.parse(this.setForm.processing);
        this.processList.way = process && process.way;
        for (var key in process) {
          if (key != "way") {
            this.handledingListData.push({
              name: this.handleWordList[key],
              content: process[key],
            });
          }
        }
      }
    },
	'setForm.type'(val) {
	  if (val) {
	      console.log('val',val)
		  if(val=='禁用词'){
			  this.handlingList = [{
									id: 1,
									method_name: "拦截"
								}]
			  this.handlingFourList= [
								{
								  name: this.$t("limitedAnswer"),
								},								
								{
								  name: this.$t("replacementIssues"),
								}
							  ]
		  }else if(val=='非学区话题'||val=='学区话题'||val=='讨论话题'){
			  this.handlingList = [{
			  									id: 1,
			  									method_name: "拦截"
			  								},{
			  									id: 2,
			  									method_name: "不处理"
			  								}]
			  this.handlingFourList= [{
          name: this.$t("limitedAnswer"),
        },
        {
          name: this.$t("addPrefix"),
        },
        {
          name: this.$t("addSuffix"),
        },
        {
          name: this.$t("replacementIssues"),
        },]
		  }else{
			  this.handlingList = [{
										id: 2,
										method_name: "不处理"
									}]
			  	  this.handlingFourList= [
			  {
			    name: this.$t("addPrefix"),
			  },
			  {
			    name: this.$t("addSuffix"),
			  }]
		  }
	  }
	},
  },
  mounted() {
    if (this.row) {
      this.setForm = JSON.parse(JSON.stringify(this.sensitiveWordList));
      let process = JSON.parse(this.setForm.processing);
      this.processList.way = process && process.way;
      for (var key in process) {
        if (key != "way") {
          this.handledingListData.push({
            name: this.handleWordList[key],
            content: process[key],
          });
        }
      }
    }
	console.log('verifyStatusColumns',this.verifyStatusColumns)
	this.verifyStatusColumns = [{
		id: 6,
		type_name: "禁用词"
	},{
		id: 4,
		type_name: "非学区话题"
	},{
		id: 2,
		type_name: "学区话题"
	},{
		id: 9,
		type_name: "讨论话题"
	},{
		id: 5,
		type_name: "单词白名单"
	},]
    this.handlingMethodList()
  },
  methods: {
    cancelSubmit() {
      this.$emit("closeDialog");
    },
    handleDownloadKnowledgeDataTemp() {},
    async handleAddQaDialog() {
      this.$refs.setForm.validate(async (valid) => {
        if (valid) {
          this.handledingListData.forEach((item) => {
            if (item.name == this.$t("limitedAnswer")) {
              this.processList.answer = item.content;
            }
            if (item.name == this.$t("addPrefix")) {
              this.processList.preQuestion = item.content;
            }
            if (item.name == this.$t("addSuffix")) {
              this.processList.extendQuestion = item.content;
            }
            if (item.name == this.$t("replacementIssues")) {
              this.processList.replaceQuestion = item.content;
            }
          });
          let res;
          if (!this.row) {
            res = await addInterceptWord({
              ...this.setForm,
              interceptWordHouseId: this.sensitiveRow.id,
              processing: JSON.stringify(this.processList),
            });
          } else {
            res = await updateInterceptWord({
              content: this.setForm.content,
              remark: this.setForm.remark,
              type: this.setForm.type,
              interceptWordHouseId: this.sensitiveRow.id,
              processing: JSON.stringify(this.processList),
              id: this.sensitiveWordList.id
            });
          }

          if (res.code == "000000") {
            this.$message.success("保存成功");
            this.$emit("closeWordDialog");
          } else {
            this.$message.warning(res.msg);
          }
        }
      });
    },
    async handlingMethodList() {
      let res = await getInterceptWordHandlingMethodList();
      if (res.code == "000000") {
        this.handlingList = res.data || [];
      }
    },
    addNewHandleing() {
      this.handledingListData.push({
        name: "",
      });
    },
    handleSelect() {
      this.setDisable();
    },
    setDisable() {
      this.handlingFourList.forEach((item) => {
        let isdisable = this.handledingListData.findIndex((val) => val.name == item.name);
        if (isdisable != -1) {
          item.disabled = true;
        } else {
          item.disabled = false;
        }
      });
    },
    deleteClick(name, index) {
      this.handledingListData.splice(index, 1);
      this.handlingFourList.forEach((item) => {
        if (item.name == name) {
          item.disabled = false;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-drawer__header {
//   background: linear-gradient(
//     180deg,
//     rgba(43, 88, 213, 0.1) 0%,
//     rgba(43, 88, 213, 0) 100%
//   );
  border-radius: 8px 8px 0px 0px;
  color: #494E57;
}
.qa-box {
  position: relative;
  padding-bottom: 60px;
  width: 100%;
  display: flex;
  flex-direction: column;
  height: 100%;

  .content {
    padding: 0 32px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }

  .dialog-footer {
    text-align: right;
    padding: 0 32px;
    .el-button {
      border-radius: 2px;
    }
    .el-button--primary {
      background: #1747E5;
      border-color: #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
    .el-button--text {
        color: #1747E5;
    }
  }

  .el-select {
    width: 100%;
  }

  .el-date-editor {
    width: 100%;
  }

  .btns {
    display: flex;
    justify-content: flex-end;
  }
}

.flex {
  display: flex;
  align-items: center;
  .box {
    width: 3px;
    height: 18px;
    background: #1c50fd;
  }
  .name {
    margin-left: 8px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
  }
}

.handle-list {
  margin: 20px 0;
  .handle-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    img {
      margin-left: 12px;
    }
    .input {
      flex: 1;
    }
  }
}
.flex-just {
  justify-content: center;
}
.addbtn {
  border: 1px solid #C9CCD1;
  background: #fff;
  color: #494E57;
  width: 92px;
}

</style>
