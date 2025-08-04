<template>
  <div class="agent-pattern-wrapper" :class="{hideLabel:hideLabel}" >
    <el-dropdown class="agent-pattern-dropdown"  trigger="click" :hide-on-click="true" placement="bottom-start" @visible-change="handleVisibleChange" :class="{hideLabel:hideLabel}" @command="(value) => handleCommand(value)">
      <div class="el-dropdown-link" :class="{ 'active': visibleChange}">
        <iconpark-icon v-if="actvieModel?.icon || hideLabel" class="model-icon" :class="{hideLabel:hideLabel}" size="20" :name="actvieModel.icon" :color="actvieModel?.disabled ? '#BCC1CC' : '#494E57'"></iconpark-icon><span v-if="!hideLabel">{{ actvieModel?.label }}</span><i class="el-icon-arrow-down el-icon--right" ></i>
      </div>
      <el-dropdown-menu slot="dropdown" class="agent-pattern-dropdown-menu">
        <div class="agent-pattern">
          <div class="agent-pattern-title">选择模式</div>
          <div class="agent-pattern-list">
            <el-dropdown-item v-for="(item, index) in modelList" :key="index"  :disbled="item.disabled" :command="item.value">
              <div class="agent-pattern-item" :class="{ 'disabled': item.disabled, 'actvie': item.value === actvieValue }">
                <div class="agent-pattern-item-icon" v-if="item.icon">
                  <iconpark-icon class="icon" :name="item.icon" :color="item.disabled ? '#BCC1CC' : '#494E57'"></iconpark-icon>
                </div>
                <div class="agent-pattern-item-content">
                  <div class="agent-pattern-item-label">
                    <p>{{ item.label }}</p>
                    <span v-if="item.disabled">敬请期待</span>
                  </div>
                  <div class="agent-pattern-item-desc">{{ item.desc }}</div>
                </div>
              </div>
            </el-dropdown-item>
            
          </div>
        </div>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>
<script>
export default {
  name: "agentPattern",
  props: {
    model: {
      type: String,
      default: 'qa'
    },
    hideLabel: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    actvieModel() {
      return this.modelList.find(item => item.value === this.actvieValue);
    },
  },
  data() {
    return {
      actvieValue: 'qa',
      visibleChange: false,
      modelList: [
        {
          label: 'LLM模式(单Agent)',
          value: 'qa',
          icon: 'LLM',
          desc: '项目中只有一个Agent，用户与大模型进行对话，适用于逻辑比较简单的项目'
        },
        {
          label: '对话流模式(单Agent)',
          value: 'dialogue',
          icon: 'Dialogue',
          desc: '以工作流的形式编排应用，提供高度的自定义。适合有经验的用户。'
        },
        {
          label: '文本生成模式(单Agent)',
          value: 'text-agent',
          icon: 'Text-generation',
          desc: '构建文本生成类任务的应用，支持单次同时输入多个参数，使用例如撰写报告、翻译等场景'
        },
        {
          label: '工作流模式(单Agent)',
          value: 'workflow',
          icon: 'Workflow',
          desc: '用于处理功能类请求，通过按顺序执行一系列节点实现特定功能。适用于数据自动化处理场景，比如内容创作、数据处理、设计工作等'
        },
        {
          label: '多Agent',
          value: 'multi_agent',
          icon: 'Workflow',
          desc: '在项目中设置多个Agent，以处理逻辑比较复杂的需求',
        }
      ]
    }
    
  },
  methods: {
    handleCommand(val) {
      const item = this.modelList.find(item => item.value === val);
      if (item.disabled || this.actvieValue === item.value ) {
        return;
      }
      this.actvieValue = item.value;
      this.$emit('updateModel', item.value);
    },
    handleVisibleChange(val) {
      this.visibleChange = val;
    }
  },
  watch: {
    model: {
      handler(val) {
        this.actvieValue = val;
        this.$store.commit('setmodelType', val);  //储存所选模板类型
      },
      immediate: true
    }
  }
  
}
</script>
<style lang="scss" scoped>
.agent-pattern-wrapper  {
  cursor: pointer;
  &.hideLabel{
    border-bottom: 2px solid #dedede;
    .agent-pattern-dropdown .el-dropdown-link{
      padding: 0;
      justify-content: center;
      width: 56px;
      .el-icon-arrow-down{
        margin-top: 5px;
        font-weight: 600;
      }
    }
  }
  .agent-pattern-dropdown {
    .el-dropdown{
      width: 100%;
    }
    .el-dropdown-link {
      display: flex;
      align-items: center;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #494C4F;
      height: 40px;
      
      border-radius: 2px;
      padding: 0 16px;
      .model-icon {
        margin-right: 8px;
        &.hideLabel{
          margin: 0;
          position: relative;
          top: 4px;
          left: 3px;
        }
      }
      .el-icon-arrow-down {
        margin-left: 8px;
      }
      &.active {
        background: #F0F2F5;
      }
    }
    &.hideLabel {
      .el-dropdown-link {
        height: 50px;
      }
    }
  }

}

</style>
<style lang="scss">
.agent-pattern-dropdown-menu {
  width: 480px;
  box-sizing: border-box;
  padding: 24px;
  .agent-pattern {
    width: 100%;
    .agent-pattern-title {
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 18px;
      color: #494E57;
      line-height: 24px;
      margin-bottom: 16px;
    }
    .agent-pattern-list {
      width: 100%;
      .el-dropdown-menu__item {
        padding: 0px;
      }
      .agent-pattern-item {
        padding: 16px;
        cursor: pointer;
        background: #FFFFFF;
        border-radius: 4px;
        border: 1px solid #D5D8DE;
        margin-bottom: 16px;
        display: flex;
        .agent-pattern-item-icon {
          display: inline-block;
          margin-right: 12px;
          width: 32px;
          font-size: 32px;
        }
        .agent-pattern-item-content {
          flex: 1;
          overflow: hidden;
          .agent-pattern-item-label {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 18px;
            color: #494E57;
            line-height: 32px;
            width: 100%;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            p {
              max-width: calc(100% - 80px);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            span {
              display: inline-block;
              height: 20px;
              background: linear-gradient( 270deg, rgba(142, 101, 255, .2) 0%, rgba(23, 71, 229, .2) 100%);
              border-radius: 10px;
              font-size: 12px;
              color: #494E57;
              line-height: 20px;
              padding: 0 8px;
              margin-left: 8px;
            }
            
          }
          .agent-pattern-item-desc {
            width: 100%;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 3;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 14px;
            color: #828894;
            line-height: 20px;
          }
        }
        &.disabled {
          background: #FFFFFF !important;
          border-radius: 4px;
          border: 1px solid #D5D8DE !important;
          .agent-pattern-item-content {
            .agent-pattern-item-label {
              color: #BCC1CC;
            }
            .agent-pattern-item-desc {
              color: #BCC1CC;
            }
          }
        }
        &:hover, &.actvie {
          background: rgba(28,80,253,0.05);
          border-radius: 4px;
          border: 1px solid #1747E5;
        }
        
      }
    }
  }
  .popper__arrow {
    display: none;
  }
}
</style>