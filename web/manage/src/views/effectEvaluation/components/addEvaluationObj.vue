<template>
  <div class="app-container">
    <el-form :model="ruleForm" label-position="top" :hide-required-asterisk="true" :rules="rules" ref="ruleForm"
      class="form-box">
      <el-form-item prop="taskName">
        <template v-slot:label>
          <div class="label-box">
            任务名称
            <span>*</span>
          </div>
        </template>
        <el-input v-model="ruleForm.taskName"></el-input>
      </el-form-item>
      <el-form-item prop="applicationId">
        <template v-slot:label>
          <div class="label-box">
            任务对象
            <span>*</span>
          </div>
        </template>
        <el-radio-group style="margin-left: 12px;" v-model="resource">
          <el-radio label="智能体"></el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div class="list-wrap">
      <div class="list-header">
        <div class="title">选择要测评的智能体</div>
        <div class="search-box">
          <el-input size="small" v-model="applicationName" placeholder="搜索智能体" class="input-with-select"
            @keydown.native.enter="getAppList" @input="getAppList">
            <template slot="prepend"><i class="el-icon-search"></i></template>
          </el-input>
        </div>
      </div>
      <div class="list-box">
        <div class="left-list" v-loading="workflowLoading">
          <el-radio-group class="list-checkbox-group" v-model="ruleForm.applicationId" @change="changeApplicationId">
            <el-radio class="checkbox-wrap" v-for="(item, index) in appList" :label="item.applicationId" :key="index">
              <div class="list-item">
                <div class="list-item-left">
                  <img :src="item.facadeImageUrl" alt="" />
                </div>
                <div class="list-item-right">
                  <div class="title">{{ item.applicationName }}</div>
                  <div class="info">{{ item.introduce }}</div>
                  <div class="time">最近发布：{{ item.updateTime }}</div>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <div v-if="isRequired"  class="el-form-item__error">请选择要测评的智能体</div>
    </div>

    <div class="btn-wrap">
      <el-button type="primary" size="small" @click="nextPage">下一步</el-button>
      <!-- <el-button size="small" @click="cancelAdd">取消</el-button> -->
    </div>

  </div>
</template>

<script>
import { debounce } from 'lodash';
import {
  appList
} from "@/api/app";
export default {
  name: 'addEvaluationObj',
  props: {
    params: {
      type: Object
    }
  },
  data() {
    return {
      ruleForm: {
        taskName: "",
        applicationId: "",
        applicationName: "",
        applicationcode: ""
      },
      isRequired: false,
      resource: "智能体",
      workflowLoading: false,
      appList: [],
      applicationName: "",
      rules: {
        taskName: [
          { required: true, message: '请输入任务名称', trigger: 'blur' },
        ],
        // applicationId: [
        //   { required: true, message: '请选择要测评的智能体', trigger: 'change' },
        // ],
      }
    }
  },
  mounted() {
    this.getAppList()
    this.ruleForm = {
      taskName: this.params.taskName ? this.params.taskName : null,
      applicationId: this.params.applicationId ? this.params.applicationId : null,
    }
  },
  methods: {
    changeApplicationId(val){
      this.appList.forEach(item=>{
        if(item.applicationId == val){
          this.ruleForm.applicationName = item.applicationName
          this.ruleForm.applicationCode = item.applicationCode
        }
      })
    },
    getAppList: debounce(function() {
      this.workflowLoading = true;
	  this.appList = [];
      appList({
        pageNo: 1,
        pageSize: 9999,
        applicationName: this.applicationName,
      }).then((res) => {
        this.workflowLoading = false;
        if (res.code == "000000") {
          this.appList = res.data?.records;
        } else {
          this.appList = [];
        }
      });
    }, 1000),
    nextPage() {
      this.$refs['ruleForm'].validate((val)=>{
        console.log(this.ruleForm.applicationId)
        if(!this.ruleForm.applicationId){
          this.isRequired = true
        }
        if(val && !this.isRequired){
          this.$emit('update', this.ruleForm)
          this.$emit('next', 'center')
        }
      })
      
    },
    cancelAdd() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  background: #fff;
  width: 80%;
  height: 100%;
  margin: 0 auto;
  padding: 24px 12%;
  border-radius: 8px;
  display: flex;
  flex-direction: column;

  .form-box {
    width: 100%;
    margin: 0 auto;
    margin-bottom: 28px;

    ::v-deep .el-form-item__label {
      padding: 0;

      .label-box {
        color: #1D2129;
        font-weight: 500;
        line-height: 20px;
        margin-bottom: 8px;

        span {
          color: #FF0000;
        }
      }
    }


  }

  .list-wrap {
    // margin-bottom: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;

    .el-form-item__error{
      bottom: 0;
      top: auto;
    }

    .list-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;

      .title {
        font-size: 16px;
        font-weight: 500;
      }

      ::v-deep .el-input-group__prepend {
        padding: 0;
        background: transparent;
        width: 32px;
        text-align: center;
        border: 0;
        margin-left: -32px;
        right: -33px;
        // line-height: 32px;
      }

      ::v-deep .el-input-group--prepend .el-input__inner {
        border-radius: 4px;
        padding-left: 38px;
      }
    }
  }

  .list-box {
    flex: 1;
    border: 1px solid #E7E7E7;
    border-radius: 3px;
    display: flex;
    padding: 16px;
    width: 100%;
    overflow: auto;
    margin-bottom: 20px;
    // height: 532px;

    .left-list {
      width: 100%;
      height: 100%;
      // border-right: 1px solid #ccc;


      .list-checkbox-group {
        width: 100%;
      }

      .checkbox-wrap {
        height: 76px;
        display: flex;
        width: 100%;
        align-items: center;
        margin-right: 0;
        margin-bottom: 8px;
        border-radius: 4px;

        ::v-deep .el-radio__input {
          padding: 0 10px;
          padding-right: 6px;
        }

        ::v-deep .el-radio__label {
          flex: 1;
          overflow: hidden;
          border-radius: 8px;
          padding-left: 6px;
        }

        .list-item {
          padding-right: 10px;
          display: flex;
          align-items: center;
          overflow: hidden;
          height: 80px;

          .list-item-left {
            margin-right: 10px;

            img {
              width: 40px;
              height: 40px;
              border-radius: 4px;
            }
          }

          .list-item-right {
            overflow: hidden;
            flex: 1;

            .title,
            .info,
            .time {
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }

            .title {
              font-size: 14px;
              font-weight: 500;
              color: #1D2129;
              margin-bottom: 10px;
            }

            .info {
              font-size: 12px;
              color: #86909C;
              margin-bottom: 6px;
            }

            .time {
              font-size: 12px;
              color: #86909C;
            }

          }

        }
      }

      .checkbox-wrap:hover {
        background: #F2F3F5;
      }

      .is-checked {

        background: #F2F3F5;

      }

    }

    .right-list {
      border-right: 0;
    }
  }

  .btn-wrap {
    display: flex;
    justify-content: flex-end;
    // margin-right: 10%;
  }
}
</style>