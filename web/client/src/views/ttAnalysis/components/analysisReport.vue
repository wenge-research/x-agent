<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
	<div class="analysis-report">
		<div class="analysis-report-header flex justify-between items-center">
			<ul class="analysis-report-header-tabs flex">
				<li :class="['cursor-pointer', tabsVal == '译文' && 'active']" @click="tabsClick('译文')">译文</li>
				<li :class="['cursor-pointer', tabsVal == '原文' && 'active']" @click="tabsClick('原文')">原文</li>
			</ul>
			<div class="analysis-report-header-operation flex items-center">
				<!-- 先隐藏 此功能先不开发 -->
				<!-- <div class="add flex items-center justify-center cursor-pointer">
					<iconpark-icon name="add-circle-line" size="16" color="#1D2129"></iconpark-icon>添加到知识库
				</div> -->
				<div class="close flex items-center justify-center cursor-pointer" @click="closeReportHandler">
					<iconpark-icon name="close-large-fill" size="18" color="#1D2129"></iconpark-icon>
				</div>
			</div>
		</div>
		<div class="analysis-report-content">
			<div v-if="tabsUrl" class="analysis-report-content-text" ref="docxContainer"></div>
			<div v-else class="analysis-report-content-text">暂无数据</div>
			<div class="analysis-report-content-divider flex items-center"><span>正文完</span></div>
			<div v-if="pictureList?.length" class="analysis-report-content-chart">
				<div class="analysis-report-content-chart-title flex items-center">相关图片</div>
				<ul class="analysis-report-content-chart-picture flex items-center">
					<li v-for="(item, index) in pictureList" :key="index" @mouseleave="mouseleaveHandler" @mouseenter="mouseenterHandler(item)">
						<img :src="item" alt="" />
						<div
							v-if="item == currentChat"
							@click="viewDetailsHandler('图表分析', index, item)"
							class="chat-btn flex items-center justify-center cursor-pointer"
						>
							<img src="/@/assets/TTAnalysis/chat.png" alt="" />图表分析
						</div>
					</li>
				</ul>
			</div>
		</div>
		<div class="analysis-report-footer">
			<ul class="analysis-report-footer-inner flex items-center">
				<li class="flex items-center justify-center cursor-pointer" @click="viewDetailsHandler('全文概况')">
					<iconpark-icon name="draft-line" size="18" color="#1D2129"></iconpark-icon>全文概况
				</li>
				<li class="flex items-center justify-center cursor-pointer" @click="viewDetailsHandler('大纲提取')">
					<iconpark-icon name="node-tree" size="18" color="#1D2129"></iconpark-icon>大纲提取
				</li>
				<li class="flex items-center justify-center cursor-pointer" @click="viewDetailsHandler('知识提取')">
					<iconpark-icon name="book-2-line" size="18" color="#1D2129"></iconpark-icon>知识提取
				</li>
			</ul>
		</div>

		<transition name="el-zoom-in-bottom">
			<div v-if="drawerVisible" class="analysis-report-drawer" :style="{ height: drawerHeight }">
				<div class="analysis-report-drawer-header flex items-center justify-between">
					<!-- 图表分析 -->
					<div v-if="currentTab == '图表分析'" class="analysis-report-drawer-header-chat flex items-center">
						<img src="/@/assets/TTAnalysis/chat2.png" alt="" />图表分析
					</div>
					<ul v-else class="analysis-report-footer-inner flex items-center">
						<li
							class="flex items-center justify-center cursor-pointer"
							:class="[currentTab == '全文概况' && 'active']"
							@click="viewDetailsHandler('全文概况')"
						>
							<iconpark-icon name="draft-line" size="18" :fill="currentTab == '全文概况' ? '#fff' : '#1D2129'"></iconpark-icon>全文概况
						</li>
						<li
							class="flex items-center justify-center cursor-pointer"
							:class="[currentTab == '大纲提取' && 'active']"
							@click="viewDetailsHandler('大纲提取')"
						>
							<iconpark-icon name="node-tree" size="18" :fill="currentTab == '大纲提取' ? '#fff' : '#1D2129'"></iconpark-icon>大纲提取
						</li>
						<li
							class="flex items-center justify-center cursor-pointer"
							:class="[currentTab == '知识提取' && 'active']"
							@click="viewDetailsHandler('知识提取')"
						>
							<iconpark-icon name="book-2-line" size="18" :fill="currentTab == '知识提取' ? '#fff' : '#1D2129'"></iconpark-icon>知识提取
						</li>
					</ul>
					<div class="analysis-report-drawer-header-right flex items-center">
						<div v-if="currentTab != '图表分析'" class="flex items-center justify-center" @click="heightAdjustment">
							<iconpark-icon name="expand-up-down-line" size="16" color="#1D2129"></iconpark-icon>
						</div>
						<div class="flex items-center justify-center" @click="handleClose">
							<iconpark-icon name="arrow-down-s-line" size="16" color="#1D2129"></iconpark-icon>
						</div>
					</div>
				</div>
				<div v-if="currentTab == '图表分析'" class="analysis-report-drawer-tabsContent">
					<el-carousel
						:autoplay="false"
						arrow="always"
						height="232"
						trigger="click"
						indicator-position="none"
						:initial-index="initialIndex"
						@change="changeCarousel"
					>
						<el-carousel-item v-for="(item, index) in pictureList" :key="index">
							<h3 text="2xl" justify="center">
								<img :src="item" alt="" style="width: 80%; margin: 0 auto" />
							</h3>
						</el-carousel-item>
					</el-carousel>
					<div class="analysis-report-drawer-tabsContent-analysis" v-loading="loading">
						<div v-if="!parsedMarkdown && !loading" class="nodata flex items-center justify-center">
							<img src="/@/assets/nodataImg.png" alt="" class="nodata-img" />
							<div>未分析出有效信息</div>
						</div>
						<div v-else style="overflow: auto; width: 100%; height: 100%" v-html="parsedMarkdown"></div>
					</div>
				</div>
				<div v-else class="analysis-report-drawer-tabsContent" v-loading="loading2">
					<div v-if="!tabsContentMarkdown && !loading2" class="nodata flex items-center justify-center">
						<img src="/@/assets/nodataImg.png" alt="" class="nodata-img" />
						<div>暂无数据</div>
					</div>
					<div v-else class="text" style="width: 100%; height: 100%; overflow: auto" v-html="tabsContentMarkdown">
					</div>
				</div>
			</div>
		</transition>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted, reactive } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import mittBus from '/@/utils/mitt';
import { useChatStore } from '/@/stores/chat';
import { useRoute } from 'vue-router';
import { renderAsync } from 'docx-preview';
import { apiMathModel, apiSummary } from '/@/api/knowledge';
import { marked } from 'marked';

const route = useRoute();
// 移动端自适应相关
const { isMobile } = useBasicLayout();
interface Emit {
	(ev: 'closeReport'): void;
}
const emit = defineEmits<Emit>();
interface Props {
	sourceData: object;
}
const props = defineProps<Props>();

const tabsVal = ref('译文');
const currentChat = ref('');
const drawerVisible = ref(false);
const currentTab = ref('');
const drawerHeight = ref('45%');
const tabsClick = (val: string) => {
	tabsVal.value = val;
	previewDocx();
};
const mouseenterHandler = (val: string) => {
	currentChat.value = val;
};
const mouseleaveHandler = () => {
	currentChat.value = '';
};
const viewDetailsHandler = (type: string, idx: number, data: any) => {
	currentTab.value = type;
	drawerVisible.value = true;
	if (currentTab.value == '图表分析') {
		drawerHeight.value = '85%';
		initialIndex.value = idx;
		pictureUrl.value = data;
		mathModelHandler();
	} else {
		drawerHeight.value = '45%';
		tabsChange(type);
	}
};
// 参数
const content = computed(() => {
	return props?.sourceData?.content || null;
});
const tabsContent = ref('');
const loading2 = ref(false);
// 全文概况/大纲提取/知识提取
const tabsChange = async () => {
	tabsContent.value = '';
	loading2.value = true;
	const params = {
		param: {
			clientId: Math.round(Math.random() * 1000000000000000000).toString(36),
			content: content.value,
			type: currentTab.value
		},
		fileLanguage: {
			id: fileLanguage.value?.id
		}
	};
	const res = await apiSummary(params);
	if (res.code == '000000') {
		tabsContent.value = res.data && res.data.includes('markdown') ? extractMarkdownContent(res.data) : res.data;
	} else {
		loading2.value = false;
	}
	loading2.value = false;
};
const extractMarkdownContent = (rawData) => {
  // 去掉开头的```markdown和结尾的```
  const startMarker = "```markdown\n";
  const endMarker = "\n```";
  
  const startIndex = rawData.indexOf(startMarker) + startMarker.length;
  const endIndex = rawData.lastIndexOf(endMarker);
  
  return rawData.slice(startIndex, endIndex).trim();
}

const tabsContentMarkdown = computed(() => {
	return tabsContent.value ? marked.parse(tabsContent.value, { breaks: true, sanitize: true }) : '';
})

const handleClose = () => {
	drawerVisible.value = false;
	currentTab.value = '';
	drawerHeight.value = '45%';
	initialIndex.value = 0;
};
const heightAdjustment = () => {
	drawerHeight.value = drawerHeight.value == '45%' ? '74%' : '45%';
};
const translationUrlMap = computed(() => {
	return props?.sourceData?.translationUrlMap || null;
});
// 原文key
const theOriginal = computed(() => {
	let key = '英语';
	if (translationUrlMap.value) {
		key = Object.keys(translationUrlMap.value)?.find((item) => item != '中文、汉语');
	}
	return key;
});
const tabsKey = computed(() => {
	return tabsVal.value == '译文' ? '中文、汉语' : theOriginal.value;
});
const tabsUrl = computed(() => {
	return translationUrlMap.value ? translationUrlMap.value[tabsKey.value] : '';
});
const pictureList = computed(() => {
	return props?.sourceData?.fileLanguageList?.length ? props.sourceData.fileLanguageList[0]?.pictures?.split('#') : [];
});
const fileLanguage = computed(() => {
	let valObj = {}
	if(props?.sourceData?.fileLanguageList?.length) {
		valObj = props.sourceData.fileLanguageList.find(item => item.language == tabsKey.value)
	}
	return valObj
});

// const tabsContent = computed(() => {
// 	if (!props?.sourceData?.fileLanguageList?.length) return '暂无数据';
// 	let val = '';
// 	if (currentTab.value == '全文概况') {
// 		val = props.sourceData.fileLanguageList[0]?.fullTextOverview;
// 	} else if (currentTab.value == '大纲提取') {
// 		val = props.sourceData.fileLanguageList[0]?.outline;
// 	} else if (currentTab.value == '知识提取') {
// 		val = props.sourceData.fileLanguageList[0]?.knowledgePoint;
// 	}
// 	return val;
// });
const initialIndex = ref(0);
const pictureUrl = ref('');
const analysisContent = ref('');
const loading = ref(false);
const parsedMarkdown = computed(() => {
	return analysisContent.value ? marked(analysisContent.value) : '';
});
// 图文分析
const mathModelHandler = async () => {
	loading.value = true;
	const params = {
		url: pictureUrl.value,
		max_try_steps: 3,
	};
	const res = await apiMathModel(params);
	if (res?.code == '000000') {
		const response = res.data || {};
		analysisContent.value = response?.choices?.[0]?.message?.content ?? '暂无分析内容';
	}
	loading.value = false;
};
// 幻灯片切换
const changeCarousel = (idx: any) => {
	analysisContent.value = '';
	pictureUrl.value = pictureList.value[idx];
	mathModelHandler();
};
const docxContainer = ref(null);
// const fileUrl = 'https://dibrain.localhost/smart-agent-api-uat/wos/file/download/document/1747829400703.docx';
const docxOptions = {
	className: 'docx', // 默认和文档根元素的class名称/命名空间
	inWrapper: true, // 启用围绕文档内容的包装器
	ignoreWidth: false, // 禁止页面渲染宽度
	ignoreHeight: false, // 禁止页面渲染高度
	ignoreFonts: false, // 禁止字体渲染
	breakPages: true, // 启用分页
	ignoreLastRenderedPageBreak: true, // 禁用最后一页的分页符
	experimental: false, // 启用实验性功能（制表符停止测量）
	trimXmlDeclaration: true, // 如果为true，xml声明将从xml文档中删除
	debug: false, // 启用额外的日志记录
	useBase64URL: false, // boolean：如果为true，图片、字体等会转为base 64 URL，否则使用URL.createObjectURL
	useMathMLPolyfill: false, // boolean：包括用于 chrome、edge 等的 MathML polyfill。
	showChanges: false, // boolean：启用文档更改的实验性渲染（插入/删除）
};
const previewDocx = async () => {
	try {
		if (!tabsUrl.value) return;
		const response = await fetch(tabsUrl.value);
		const blob = await response.blob();
		await renderAsync(blob, docxContainer.value, null, docxOptions);
	} catch (error) {
		console.error('文档预览失败:', error);
	}
};
// 关闭报告
const closeReportHandler = () => {
	emit('closeReport');
};
onMounted(() => {
	previewDocx();
});
</script>

<style scoped lang="scss">
.analysis-report {
	width: 100%;
	height: 100%;
	position: relative;
	transform: translateX(0);
	transition: transform 0.5s ease-in-out;
	::-webkit-scrollbar {
		display: none;
	}
	&-header {
		width: 100%;
		height: 80px;
		padding: 26px 24px 16px;
		border-bottom: 1px solid #e7e7e7;
		&-tabs {
			width: 160px;
			height: 36px;
			border-radius: 18px;
			background: #f7f8fa;
			font-family: MiSans, MiSans;
			font-size: 16px;
			color: #86909c;
			li {
				width: 50%;
				height: 100%;
				line-height: 36px;
				text-align: center;
			}
			li.active {
				background: #ffffff;
				box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
				color: #1d2129;
				font-weight: 600;
				border-radius: 18px;
			}
		}
		&-operation {
			height: 40px;
			.add {
				width: 156px;
				height: 100%;
				border-radius: 8px;
				font-family: MiSans, MiSans;
				font-size: 16px;
				color: #3f4247;
				iconpark-icon {
					margin-right: 8px;
				}
			}
			.close {
				width: 40px;
				height: 100%;
				margin-left: 32px;
			}
		}
	}
	&-content {
		width: 100%;
		height: calc(100% - 80px - 64px);
		padding: 0 24px;
		overflow: auto;
		&-text {
			padding: 16px 0;
			min-height: 358px;
		}
		&-divider {
			width: 100%;
			height: 16px;
			&::before {
				content: '';
				display: inline-block;
				flex: 1;
				height: 1px;
				background: #e7e7e7;
			}
			&::after {
				content: '';
				display: inline-block;
				flex: 1;
				height: 1px;
				background: #e7e7e7;
			}
			& > span {
				width: 56px;
				text-align: center;
				font-family: MiSans, MiSans;
				font-size: 12px;
				color: #c9cdd4;
			}
		}
		&-chart {
			padding: 16px 0 36px;
			&-title {
				width: 80px;
				height: 24px;
				font-family: MiSans, MiSans;
				font-weight: 600;
				font-size: 16px;
				color: #434649;
				margin-bottom: 12px;
				&::before {
					content: '';
					display: inline-block;
					width: 2px;
					height: 16px;
					background: #1747e5;
					margin-right: 8px;
				}
			}
			&-picture {
				flex-wrap: wrap;
				gap: 12px;
				li {
					position: relative;
					flex: 0 0 calc(33.33% - 12px); /* 每项占 1/3 宽度 - 间隙补偿 */
					box-sizing: border-box; /* 防止 padding 影响宽度 */
					height: 144px;
					border-radius: 4px;
					// border: 1px solid #eee;
					img {
						width: 100%;
						height: 100%;
						border-radius: 4px;
						object-fit: cover;
					}
					&:hover {
						margin-top: -4px;
						box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
						border-radius: 4px;
						border: 1px solid #4d77ef;
					}
					.chat-btn {
						position: absolute;
						left: 30%;
						bottom: 12px;
						width: 104px;
						height: 32px;
						background: linear-gradient(270deg, #1747e5 0%, #7e56eb 100%);
						box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
						border-radius: 16px;
						font-family: MiSans, MiSans;
						font-size: 14px;
						color: #ffffff;
						img {
							width: 16px;
							height: 16px;
							margin-right: 4px;
						}
					}
				}
			}
		}
	}
	&-footer {
		width: 100%;
		height: 64px;
		background: rgba(255, 255, 255, 0.5);
		border-radius: 8px 8px 8px 0px;
		&-inner {
			width: 100%;
			height: 100%;
			padding: 0 24px;
			li {
				margin-right: 12px;
				width: 106px;
				height: 32px;
				border-radius: 8px;
				border: 1px solid #c4c6cc;
				&.active {
					background: #2065d6;
					border-radius: 8px;
					border: none;
					color: #ffffff;
				}
				iconpark-icon {
					margin-right: 4px;
				}
			}
		}
	}
	&-drawer {
		width: 100%;
		position: absolute;
		left: 0;
		bottom: 0;
		background: #ffffff;
		border-radius: 8px 8px 8px 0px;
		border: 1px solid #ffffff;
		z-index: 1;
		&-header {
			width: 100%;
			height: 72px;
			background: linear-gradient(180deg, rgba(30, 148, 255, 0.1) 0%, rgba(179, 223, 255, 0) 100%), #ffffff;
			border-radius: 8px 8px 8px 0px;
			border: 1px solid #ffffff;
			&-chat {
				padding-left: 24px;
				font-family: MiSans, MiSans;
				font-weight: 600;
				font-size: 18px;
				color: #434649;
				img {
					width: 20px;
					height: 20px;
					margin-right: 8px;
				}
			}
			&-right {
				margin-right: 24px;
				& > div {
					width: 32px;
					height: 32px;
					margin-left: 8px;
					cursor: pointer;
				}
			}
		}
		&-tabsContent {
			padding: 0 24px 16px;
			height: calc(100% - 72px);
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #434649;
			line-height: 24px;
			&-analysis {
				width: 100%;
				height: calc(100% - 232px);
				padding-top: 16px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #434649;
				line-height: 24px;
			}
			:deep(.el-carousel__arrow) {
				width: 32px;
				height: 32px;
				background-color: #707275;
			}
			:deep(.el-carousel__container) {
				height: 232px;
				h3 {
					background-color: #8c8f92;
				}
			}
		}
	}
	.nodata {
		width: 100%;
		height: 100%;
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 14px;
		color: #797f8a;
		margin-bottom: 16px;
		flex-direction: column;
		&-img {
			width: 88px;
			height: 88px;
		}
	}
	.flex {
		display: flex;
	}
	.items-center {
		align-items: center;
	}
	.justify-center {
		justify-content: center;
	}
	.justify-between {
		justify-content: space-between;
	}
	.cursor-pointer {
		cursor: pointer;
	}
}
/* 修改文档整体样式 */
.docx-wrapper {
	background-color: #f5f5f5 !important;
	padding: 20px !important;
}

/* 修改段落样式 */
.docx p {
	margin-bottom: 15px !important;
	line-height: 1.6 !important;
}

/* 修改标题样式 */
.docx h1,
.docx h2,
.docx h3 {
	color: #333 !important;
	margin: 20px 0 10px 0 !important;
}

/* 修改表格样式 */
.docx table {
	border-collapse: collapse !important;
	width: 100% !important;
}

.docx table td,
.docx table th {
	border: 1px solid #ddd !important;
	padding: 8px !important;
}
</style>
<style>
/* 修改文档整体样式 */
.docx-wrapper {
	background: transparent !important;
	padding: 0 !important;
}

.docx-wrapper > section.docx {
	margin-bottom: 0 !important;
	box-shadow: none !important;
	background: transparent !important;
}

/* 修改段落样式 */
.docx p {
	font-family: MiSans, MiSans !important;
	font-size: 14px !important;
	color: #3f4247 !important;
	margin-bottom: 16px !important;
	line-height: 26px !important;
}

/* 修改标题样式 */
.docx h1,
.docx h2,
.docx h3 {
	color: #333 !important;
	margin: 20px 0 10px 0 !important;
}

/* 修改表格样式 */
.docx table {
	border-collapse: collapse !important;
	width: 100% !important;
}

.docx table td,
.docx table th {
	border: 1px solid #ddd !important;
	padding: 8px !important;
}
</style>
