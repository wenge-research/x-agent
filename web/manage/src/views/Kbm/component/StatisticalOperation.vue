<template>
	<div v-loading="loading">
	  <div class="card-box" v-if="type=='1'">
		<div class="card-title">
		  数据总量
		  <iconpark-icon  name="question-line"></iconpark-icon>
		</div>
		<div class="card-total">
		  <span>{{knowledgeCountInfoObj.allCount}}</span>
		  <span>条</span>
		</div>
		<div class="card-info-list">
		  <div class="card-info-item">
			<span>QA对</span>
			<span>{{knowledgeCountInfoObj.wddCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>文档数据</span>
			<span>{{knowledgeCountInfoObj.wjCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>URL</span>
			<span>{{knowledgeCountInfoObj.urlCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>结构化数据</span>
			<span>{{knowledgeCountInfoObj.jghCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>多媒体</span>
			<span class="number" @mouseenter="showTooltip = true" 
			@mouseleave="showTooltip = false">{{knowledgeCountInfoObj.jghCount}}</span>
			<div 
				v-if="showTooltip" 
				class="tooltip"
				>
				<div class="tooltip-tit">视频: {{ knowledgeCountInfoObj.videoCount }}</div>
				<div class="tooltip-tit">音频: {{ knowledgeCountInfoObj.audioCount }}</div>
				<div class="tooltip-tit">图片: {{ knowledgeCountInfoObj.imageCount }}</div>
				</div>
		  </div>
		</div>
	  </div>
	  <div class="card-box" v-if="type=='2'">
		<div class="card-title">
		  关联应用数
		  <iconpark-icon  name="question-line"></iconpark-icon>
		</div>
		<div class="card-total">
		  <span>{{applicationCountInfoObj.totalCount}}</span>
		  <span>条</span>
		</div>
		<div class="card-info-list">
		  <div class="card-info-item">
			<span>LLM</span>
			<span>{{applicationCountInfoObj.llmCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>对话流</span>
			<span>{{applicationCountInfoObj.dialogueCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>文本生成</span>
			<span>{{applicationCountInfoObj.textAgentCount}}</span>
		  </div>
		  <div class="card-info-item">
			<span>工作流</span>
			<span>{{applicationCountInfoObj.workflowCount}}</span>
		  </div>
		</div>
	  </div>
	</div>
</template>
<script>
export default {
  name: "StatisticalOperation",
  props: {
	knowledgeCountInfoObj: {
	  type: Object,
	  default: () => {
	    return {}
	  }
	},
	applicationCountInfoObj: {
	  type: Object,
	  default: () => {
	    return {}
	  }
	},
    type: {
      type: String,
      default: ''
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
		showTooltip: false,
	};
  },
  methods: {
	
  },
};
</script>
<style lang="scss" scoped>
.card-box {
  width: 100%;
  height: 204px;
  border-radius: 4px;
  padding: 8px;
  padding-top: 20px;
  font-size: 16px;
  color: #1d2129;
  .card-title {
    display: flex;
    align-items: center;
    justify-content: start;
    margin-bottom: 20px;
    margin-left: 20px;
  }
  .card-total {
    margin-left: 20px;
    margin-bottom: 20px;
    :first-child {
      font-size: 40px;
      font-weight: bold;
      margin-right: 7px;
    }
  }
  .card-info-list {
    width: 100%;
    background-color: rgba(255, 255, 255, 0.6);
    height: 80px;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 18px;
    .card-info-item {
      flex: 1;
      display: flex;
      flex-direction: column;
	  position: relative;
      :first-child {
        color: #86909c;
        margin-bottom: 8px;
      }
      :last-child {
        font-weight: 600;
        font-size: 24px;
      }
	  .number {
		font-size: 20px;
		font-weight: bold;
		}
		
		
    }
	.tooltip {
		position: absolute;
		left: 26px;
		width: 86px;
		flex-wrap: wrap;
		top: 24px;
		display: flex;
		background: #ccc;
		// border: 1px solid #ccc;
		margin-left: 10px;
		padding: 5px;
		// box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
		z-index: 10;
		.tooltip-tit {
			font-size: 16px;
			font-weight: bold;
			margin-bottom: 8px;
			color: #000;
		}
		}
  }
}
</style>
