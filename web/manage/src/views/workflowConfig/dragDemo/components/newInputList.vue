<template>
  <div class="container">
    <div class="input-ctn">
      <div class="title-flex" v-if="isLabel">
        <span>{{ toolTipLabel }}</span>
        <div class="icon-ctn" v-if="isTooltip"> 
          <el-tooltip popper-class="workflow-tooltip" :content="toolTipContent" placement="top" effect="light">
            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
          </el-tooltip>
        </div>
      </div>
      <!-- <p class="input-label">
        {{ "变量名" }}
      </p> -->
      <div class="single" v-if="!isBoth">
        <el-select v-model="value" :placeholder="tip" size="small" v-if="type=='select'" :disabled="isSelectDisabled" @change="changeOptions">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-upload v-else-if="type=='upload'"
            ref="upload"
            class="upload-demo"
            :action="actionUrl"
            :show-file-list="false"
            :file-list="fileList"
            :data="{ filePath: 'agent_source' }"
            :before-upload="beforeUpload"
            :accept="'image/*'"
            :on-success="handleLogoSuccess"
            :on-exceed="handleExceed"
            :on-change="handleChange"
            >
            <el-button size="small" style="width: 100%;">
              <div class="upload-word" v-if="!modelValue.urlPath">
                <iconpark-icon name="upload-line" color="#1d2129"></iconpark-icon> <span>点击上传</span>
              </div>
              <div class="upload-already" v-else>
                <img :src="modelValue.urlPath" alt="">
                <p class="file-name" style="width:220px ;">{{ modelValue.fileName }}</p>
                <div class="close" @click.stop="removeFile">
                  <iconpark-icon name="close-large-line" size="16" color="#1d2129"></iconpark-icon>
                </div>
              </div>
              
            </el-button>
            <!-- <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div> -->
          </el-upload>
        <el-input v-model="input" placeholder="请输入内容" size="small" v-else></el-input>
      </div>
      
      <div class="both-ctn" v-else>
        <div class="left-select">
          <el-select v-model="selectType" placeholder="请选择" size="small">
            <el-option label="自定义" value="0"></el-option>
            <el-option label="引用" value="1"></el-option>
          </el-select>
        </div>
        <div class="right-select">
          <div v-if="type=='select-select'" style="position: relative;">
            <el-select v-model="value" placeholder="选择变量" size="small" >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <div class="select-icon">
              <iconpark-icon name="settings-line" color="#1d2129"></iconpark-icon>
            </div>
          </div>
         
          <el-input-number v-model="num"  :min="1" :max="10" label="描述文字" size="small" v-else-if="type=='select-count'"></el-input-number>
          <el-upload v-else-if="type=='select-upload'"
            ref="upload"
            class="upload-demo"
            :action="actionUrl"
            :show-file-list="false"
            :file-list="fileList"
            :data="{ filePath: 'agent_source' }"
            :before-upload="beforeUpload"
            :accept="'image/*'"
            :on-success="handleLogoSuccess"
            :on-exceed="handleExceed"
            :on-change="handleChange"
            >
            <el-button size="small" style="width: 100%;">
              <div class="upload-word" v-if="!modelValue.urlPath">
                <iconpark-icon name="upload-line" color="#1d2129"></iconpark-icon> <span>点击上传</span>
              </div>
              <div class="upload-already" v-else>
                <img :src="modelValue.urlPath" alt="">
                <p class="file-name">{{ modelValue.fileName }}</p>
                <div class="close" @click.stop="removeFile">
                  <iconpark-icon name="close-large-line" size="16" color="#1d2129"></iconpark-icon>
                </div>
              </div>
              
            </el-button>
            <!-- <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div> -->
          </el-upload>
        </div>
        
      </div>
    </div>

    <!-- <div class="right-ctn">
      <div class="title-flex">
        <span>{{ "变量值" }}</span>
        <div class="icon-ctn">
          <el-tooltip popper-class="workflow-tooltip" :content="'提示词'" placement="top" effect="light">
            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
          </el-tooltip>
        </div>
      </div>
      <p class="input-label">
        {{ "变量值" }}
      </p>
      <el-input placeholder="请输入内容" v-model="input3" class="input-with-select" size="small">
        <el-select v-model="selectType" slot="prepend" placeholder="选择变量">
          <el-option label="引用" value="0"></el-option>
          <el-option label="自定义" value="1"></el-option>
        </el-select>
        <i slot="suffix" class="el-icon-s-tools" style="line-height: 30px;"></i>
      </el-input>
    </div> -->

    <!-- <div class="right-icon">
      <i class="el-icon-remove-outline"></i>
    </div> -->
  </div>
</template>

<script>

export default {
  name: "CustomDrawer",
  props: {
    type:{
      type:String,
      default:"normal"
    },
    isBoth:{
      type:Boolean,
      default:false
    },
    isTooltip:{
      type:Boolean,
      default:false
    },
    toolTipContent:{
      type:String,
      default:""
    },
    toolTipLabel:{
      type:String,
      default:""
    },
    isLabel:{
      type:Boolean,
      default:true
    },
    isSelectDisabled:{
      type:Boolean,
      default:false
    },
    modelValue:{
      type:Object,
      default:()=>({})
    },
    options:{
      type:Array,
      default:()=>[]
    },
    optionsValue:{
      type:String,
      default:""
    },
    tip:{
      type:String,
      default:""
    }
  },
  data() {
      return {
        selectType:"0",
        actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
        num:1,
        input:"",
        fileList:[],
        value:"",
        treeList:[
          {
            label:"用户输入",
            img:"",
            children:[]
          },
          {
            label:"安全拦截",
            img:"",
            children:[
              {
                label:"风险拦截",
                img:""
              }
            ]
          },
          {
            label:"问题理解",
            img:"",
            children:[
              {
                label:"问题改写",
                img:""
              },
              {
                label:"问题拆解",
                img:""
              }
            ]
          }
        ]
      }
  },
  mounted() {
      this.value=this.optionsValue
  },
  watch: {
      
  },
  methods:{
    beforeUpload(file,fileList){
      console.log(file,fileList);
      const isJPG = file.type === 'image/jpg'
      const isPNG = file.type === 'image/png'
      const isJPEG = file.type === 'image/jpeg'
      if(!(isJPG || isPNG || isJPEG)){
        this.$message({
            message: '图片格式只能为jpg,png,jpeg',
            center: true,
            type:"info"
        })
        return false;
      }
      if(file.size>10*1024*1024){
        this.$message({
            message: '文件大小不能超过10MB，请重新上传',
            center: true,
            type:"warning"
        })
        return false;
      }
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        // this.appForm.facadeImageUrl =
        //   response.data && response.data[0] ? response.data[0].url : "";
        // this.$refs.upload.clearFiles(); 
        console.log(response.data[0].urlPath);
        
        this.$emit("update:modelValue",response.data[0])
      }
    },
    handleExceed(file,fileList){
      console.log("超出",file,fileList);
      const uploadRef = this.$refs.upload;
      
      // 清空文件列表
      uploadRef.clearFiles();
      
      // 将当前文件添加到文件列表
      this.fileList.push(file)
      // 立即上传
      uploadRef.submit();
    },
    handleChange(file,fileList){
      console.log(file);
      const isJPG = file.type === 'image/jpg'
      const isPNG = file.type === 'image/png'
      const isJPEG = file.type === 'image/jpeg'
      if(fileList.length>1&&(isJPG || isPNG || isJPEG)){
        this.fileList = [fileList[fileList.length - 1]];
      }
    },
    removeFile(){
      this.$emit("update:modelValue",{})
    },
    changeOptions(data){
      this.$emit("update:optionsValue",data)
    }
  }
};
</script>

<style scoped lang="scss">
.container{
  width: 100%;
  position: relative;
  display: flex;
  gap: 8px;
  align-items: flex-end;

  .single{
    width: 100% !important;
    ::v-deep .el-select{
      .is-focus .el-input__inner {
        border-color: #d3d9e6;
      }

      .el-input__inner{
        background-color: #fff;
      }
      .el-input.is-disabled .el-input__inner {
        background-color: #F5F7FA !important;
      }
    }

    ::v-deep .el-upload{
          
      .el-button{
        border-color: #d3d9e6;
        color: #1D2129;
        height: 32px;
      }
      .el-button:focus, .el-button:hover {
        color: #1D2129;
        border-color: #d3d9e6;
        background-color: #fff;
      }
    }
  }


  .input-ctn{
    width: 100%;

    .both-ctn{
      width: 100%;
      display: flex;

      .left-select{
        width: 85px;
        ::v-deep .el-select{
          .is-focus .el-input__inner {
            border-color: #d3d9e6;
          }

          .el-input__inner{
            background-color: #F7F8FA;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #494C4F;
            border-right: none;
            border-top-right-radius: 0;
            border-bottom-right-radius: 0;
          }
        }
        
      }

      .right-select{
        flex: 1;
        ::v-deep .el-select{
          .is-focus .el-input__inner {
            border-color: #d3d9e6;
          }

          .el-input__inner{
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #494C4F;
            border-left: none;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;

            &:hover{
              background: #fff;
            }
          }
        }

        ::v-deep .el-input-number{
          
          .el-input-number__decrease{
            background: #fff;
            border-left: none;
            border-right: none;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
          }
          .el-input-number__increase{
            background: #fff;
            border-left: none;
          }
          

          .el-input__inner{
            border-left: none;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
            border-color: #d3d9e6 !important;
          }
        }

        ::v-deep .el-upload{
          
          .el-button{
            border-left: 0;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
            border-color: #d3d9e6;
            color: #1D2129;
            height: 32px;
          }
          .el-button:focus, .el-button:hover {
            color: #1D2129;
            border-color: #d3d9e6;
            background-color: #fff;
          }
        }

        .select-icon{
          width: 25px;
          height: 30px;
          background: #fff;
          position: absolute;
          top: 1px;
          right: 5px;
          display: flex;
          justify-content: center;
          align-items: center;
        }
        
      }
    }

  }



  .right-icon{
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1d2129;
  }

}

.upload-word{
  height: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  span{
    display: inline-block;
    height: 12px;
    line-height: 14px;
  }
}
.upload-already{
  width: 100%;
  height: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;

  img{
    width: 20px;
    height: 20px;
    border-radius: 2px;
  }

  .file-name{
    width: 120px;
    height: 20px;
    line-height: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494C4F;
    text-align: left;
  }

  .close{
    width: 20px;
    height: 20px;
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    right: -10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}


.title-flex{
display: flex;
align-items: center;
margin-bottom: 8px;

span{
  display: inline-block;
  height: 16px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #86909C;
    line-height: 16px;
}

.icon-ctn{
  height: 16px;
  line-height: 16px;
  position: relative;

}
}
</style>
