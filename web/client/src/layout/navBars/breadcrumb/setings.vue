<template>
	<div class="layout-breadcrumb-seting">
		<w-drawer
			:title="$t('message.layout.configTitle')"
			:visible ="getThemeConfig.isDrawer"
			placement="right"
			unmount-on-close
			width="260px"
			:footer="false"
			@cancel="onDrawerClose"
			@close="onDrawerClose"
		>
			<div class="layout-breadcrumb-seting-bar" >
				<!-- 全局主题 -->
				<w-divider content-position="left">{{ $t('message.layout.oneTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex" style="overflow: hidden;">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.themeColor') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value" style="overflow: hidden;">
						<w-color-picker
							size="small"
							:modes="['hex']"
							v-model:value="getThemeConfig.primary"
							:show-alpha="false"
							@complete="onColorPickerChange"
						/>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsDark') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isIsDark" size="small" @change="onAddDarkChange"></w-switch>
					</div>
				</div>

				<!-- 顶栏设置 -->
				<w-divider content-position="left">{{ $t('message.layout.twoTopTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.twoTopBar') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-color-picker
							size="small"
							v-model:value="getThemeConfig.topBar"
							@complete="onBgColorPickerChange('topBar')"
						/>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.twoTopBarColor') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-color-picker
							size="small"
							v-model:value="getThemeConfig.topBarColor"
							@complete="onBgColorPickerChange('topBarColor')"
						/>
					</div>
				</div>

				<!-- 菜单设置 -->
				<w-divider content-position="left">{{ $t('message.layout.twoMenuTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.twoMenuBar') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-color-picker
							size="small"
							v-model:value="getThemeConfig.menuBar"
							@complete="onBgColorPickerChange('menuBar')"
						/>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.twoMenuBarColor') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-color-picker
							size="small"
							v-model:value="getThemeConfig.menuBarColor"
							@complete="onBgColorPickerChange('menuBarColor')"
						/>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.twoMenuBarActiveColor') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-color-picker
							size="small"
							v-model:value="getThemeConfig.menuBarActiveColor"
							@complete="onBgColorPickerChange('menuBarActiveColor')"
						/>
					</div>
				</div>

				<!-- 界面设置 -->
				<w-divider content-position="left">{{ $t('message.layout.threeTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex" :style="{ opacity: getThemeConfig.layout === 'transverse' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.threeIsCollapse') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch
							v-model="getThemeConfig.isCollapse"
							:disabled="getThemeConfig.layout === 'transverse'"
							size="small"
							@change="onThemeConfigChange"
						></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15" :style="{ opacity: getThemeConfig.layout === 'transverse' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.threeIsUniqueOpened') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch
							v-model="getThemeConfig.isUniqueOpened"
							:disabled="getThemeConfig.layout === 'transverse'"
							size="small"
							@change="setLocalThemeConfig"
						></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.threeIsFixedHeader') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isFixedHeader" size="small" @change="onIsFixedHeaderChange"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15" :style="{ opacity: getThemeConfig.layout !== 'classic' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.threeIsClassicSplitMenu') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch
							v-model="getThemeConfig.isClassicSplitMenu"
							:disabled="getThemeConfig.layout !== 'classic'"
							size="small"
							@change="onClassicSplitMenuChange"
						>
						</w-switch>
					</div>
				</div>

				<!-- 界面显示 -->
				<w-divider content-position="left">{{ $t('message.layout.fourTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsShowLogo') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isShowLogo" size="small" @change="onIsShowLogoChange"></w-switch>
					</div>
				</div>
				<div
					class="layout-breadcrumb-seting-bar-flex mt15"
					:style="{ opacity: getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse' ? 0.5 : 1 }"
				>
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsBreadcrumb') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch
							v-model="getThemeConfig.isBreadcrumb"
							:disabled="getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse'"
							size="small"
							@change="onIsBreadcrumbChange"
						></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsBreadcrumbIcon') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isBreadcrumbIcon" size="small" @change="setLocalThemeConfig"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsTagsview') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isTagsview" size="small" @change="setLocalThemeConfig"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsTagsviewIcon') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isTagsviewIcon" size="small" @change="setLocalThemeConfig"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsCacheTagsView') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isCacheTagsView" size="small" @change="setLocalThemeConfig"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15" :style="{ opacity: state.isMobile ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsSortableTagsView') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch
							v-model="getThemeConfig.isSortableTagsView"
							:disabled="state.isMobile ? true : false"
							size="small"
							@change="onSortableTagsViewChange"
						></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsShareTagsView') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isShareTagsView" size="small" @change="onShareTagsViewChange"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsFooter') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isFooter" size="small" @change="setLocalThemeConfig"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsGrayscale') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isGrayscale" size="small" @change="onAddFilterChange()"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourIsWartermark') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-switch v-model="getThemeConfig.isWartermark" size="small" @change="onWartermarkChange"></w-switch>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt14">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fourWartermarkText') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-input v-model="getThemeConfig.wartermarkText"  style="width: 90px" @input="onWartermarkTextInput"></w-input>
					</div>
				</div>

				<!-- 其它设置 -->
				<w-divider content-position="left">{{ $t('message.layout.fiveTitle') }}</w-divider>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fiveTagsStyle') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-select v-model="getThemeConfig.tagsStyle" placeholder="请选择" style="width: 90px" @change="setLocalThemeConfig">
							<w-option label="风格1" value="tags-style-one"></w-option>
							<w-option label="风格4" value="tags-style-four"></w-option>
							<w-option label="风格5" value="tags-style-five"></w-option>
						</w-select>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fiveAnimation') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<w-select v-model="getThemeConfig.animation" placeholder="请选择" style="width: 90px" @change="setLocalThemeConfig">
							<w-option label="slide-right" value="slide-right"></w-option>
							<w-option label="slide-left" value="slide-left"></w-option>
							<w-option label="opacitys" value="opacitys"></w-option>
						</w-select>
					</div>
				</div>

				<!-- 布局切换 -->
				<w-divider content-position="left">{{ $t('message.layout.sixTitle') }}</w-divider>
				<div class="layout-drawer-content-flex">
					<!-- defaults 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('defaults')">
						<w-layout :class="{ 'drawer-layout-active': getThemeConfig.layout === 'defaults' }">
							<w-layout-sider class="w-aside" style="width: 20px"></w-layout-sider>
							<w-layout>
								<w-layout-header class="w-header" style="height: 10px"></w-layout-header>
								<w-layout-content class="w-main"></w-layout-content>
							</w-layout>
						</w-layout>
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'defaults' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixDefaults') }}</p>
							</div>
						</div>
					</div>
					<!-- classic 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('classic')">
						<w-layout :class="{ 'drawer-layout-active': getThemeConfig.layout === 'classic' }">
							<w-layout-header class="w-header" style="height: 10px"></w-layout-header>
							<w-layout>
								<w-layout-sider class="w-aside" style="width: 20px"></w-layout-sider>
								<w-layout-content class="w-main"></w-layout-content>
							</w-layout>
						</w-layout>	
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'classic' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixClassic') }}</p>
							</div>
						</div>
					</div>
					<!-- transverse 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('transverse')">
						<w-layout :class="{ 'drawer-layout-active': getThemeConfig.layout === 'transverse' }">
							<w-layout-header class="w-header" style="height: 10px"></w-layout-header>
							<w-layout>
								<w-layout>
									<w-layout-content class="w-main"></w-layout-content>
								</w-layout>
							</w-layout>
						</w-layout>	
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'transverse' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixTransverse') }}</p>
							</div>
						</div>
					</div>
				</div>
				<div class="copy-config">
					<w-alert type="warning" :closable="false">{{$t('message.layout.tipText')}} </w-alert>
					<w-button size="default" class="copy-config-btn" type="primary" ref="copyConfigBtnRef" @click="onCopyConfigClick">
						<IconCopy/>
						{{ $t('message.layout.copyText') }}
					</w-button>
					<w-button size="default" class="copy-config-btn-reset" type="secondary" @click="onResetConfigClick">
						<IconRefresh/>
						{{ $t('message.layout.resetText') }}
					</w-button>
				</div>
			</div>
		</w-drawer>
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbSeting">
import { nextTick, onUnmounted, onMounted, computed, reactive } from 'vue';
import { Message } from 'winbox-ui-next'
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { useChangeColor } from '/@/utils/theme';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { Local } from '/@/utils/storage';
import Watermark from '/@/utils/wartermark';
import commonFunction from '/@/utils/commonFunction';
import other from '/@/utils/other';
import mittBus from '/@/utils/mitt';
import { IconCopy,IconRefresh} from 'winbox-ui-next/es/icon';

// 定义变量内容
const { locale } = useI18n();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const { copyText } = commonFunction();
const { getLightColor, getDarkColor, hexToRgb } = useChangeColor();
const state = reactive({
	isMobile: false,
});


// 获取布局配置信息
const getThemeConfig = computed(() => {
	return themeConfig.value;
});

// 1、全局主题
const onColorPickerChange = () => {

	if (!getThemeConfig.value.primary) return Message.warning('全局主题 primary 颜色值不能为空');

	document.body.style.setProperty('--primary-6', hexToRgb(getThemeConfig.value.primary).join());
	
	// 颜色变浅
	// for (let i = 5; i >= 1; i--) {
	// 	document.body.style.setProperty(`--primary-${i}`, hexToRgb(getLightColor(getThemeConfig.value.primary, (6 - i) / 6)).join());
	// }
	// for (let i = 7; i <= 9; i++) {
	// 	document.body.style.setProperty(`--primary-${i}`, hexToRgb(getDarkColor(getThemeConfig.value.primary, i / 12)).join());
	// }
	setDispatchThemeConfig();
};
// 2、菜单 / 顶栏
const onBgColorPickerChange = (bg: string) => {
	document.documentElement.style.setProperty(`--next-bg-${bg}`, themeConfig.value[bg]);
	setDispatchThemeConfig();
};
// 3、界面设置 --> 菜单水平折叠
const onThemeConfigChange = () => {
	setDispatchThemeConfig();
};
// 3、界面设置 --> 固定 Header
const onIsFixedHeaderChange = () => {
	getThemeConfig.value.isFixedHeaderChange = getThemeConfig.value.isFixedHeader ? false : true;
	setLocalThemeConfig();
};
// 3、界面设置 --> 经典布局分割菜单
const onClassicSplitMenuChange = () => {
	getThemeConfig.value.isBreadcrumb = false;
	setLocalThemeConfig();
	mittBus.emit('getBreadcrumbIndexSetFilterRoutes');
};
// 4、界面显示 --> 侧边栏 Logo
const onIsShowLogoChange = () => {
	getThemeConfig.value.isShowLogoChange = getThemeConfig.value.isShowLogo ? false : true;
	setLocalThemeConfig();
};
// 4、界面显示 --> 面包屑 Breadcrumb
const onIsBreadcrumbChange = () => {
	if (getThemeConfig.value.layout === 'classic') {
		getThemeConfig.value.isClassicSplitMenu = false;
	}
	setLocalThemeConfig();
};
// 4、界面显示 --> 开启 TagsView 拖拽
const onSortableTagsViewChange = () => {
	mittBus.emit('openOrCloseSortable');
	setLocalThemeConfig();
};
// 4、界面显示 --> 开启 TagsView 共用
const onShareTagsViewChange = () => {
	mittBus.emit('openShareTagsView');
	setLocalThemeConfig();
};
// 4、界面显示 --> 灰色模式
const onAddFilterChange = () => {
	const cssAttr =`grayscale(${getThemeConfig.value.isGrayscale ? 1 : 0})`
	const appEle = document.body;
	appEle.setAttribute('style', `filter: ${cssAttr}`);
	setLocalThemeConfig();
};
// 4、界面显示 --> 深色模式
const onAddDarkChange = () => {
	const body = document.body as HTMLElement;
	if (getThemeConfig.value.isIsDark) body.setAttribute('winbox-theme', 'dark');
	else body.setAttribute('winbox-theme', '');
};
// 4、界面显示 --> 开启水印
const onWartermarkChange = () => {
	getThemeConfig.value.isWartermark ? Watermark.set(getThemeConfig.value.wartermarkText) : Watermark.del();
	setLocalThemeConfig();
};
// 4、界面显示 --> 水印文案
const onWartermarkTextInput = (val: string) => {
	getThemeConfig.value.wartermarkText = verifyAndSpace(val);
	if (getThemeConfig.value.wartermarkText === '') return false;
	if (getThemeConfig.value.isWartermark) Watermark.set(getThemeConfig.value.wartermarkText);
	setLocalThemeConfig();
};
// 5、布局切换
const onSetLayout = (layout: string) => {
	Local.set('oldLayout', layout);
	if (getThemeConfig.value.layout === layout) return false;
	if (layout === 'transverse') getThemeConfig.value.isCollapse = false;
	getThemeConfig.value.layout = layout;
	getThemeConfig.value.isDrawer = false;
	initLayoutChangeFun();
};
// 设置布局切换函数
const initLayoutChangeFun = () => {
	onBgColorPickerChange('menuBar');
	onBgColorPickerChange('menuBarColor');
	onBgColorPickerChange('menuBarActiveColor');
	onBgColorPickerChange('topBar');
	onBgColorPickerChange('topBarColor');
};
// 关闭弹窗时，初始化变量。变量用于处理 layoutScrollbarRef.value.handleResize() 更新滚动条高度
const onDrawerClose = () => {
	getThemeConfig.value.isFixedHeaderChange = false;
	getThemeConfig.value.isShowLogoChange = false;
	getThemeConfig.value.isDrawer = false;
	setLocalThemeConfig();
};
// 布局配置弹窗打开
const openDrawer = () => {
	getThemeConfig.value.isDrawer = true;
};
// 触发 store 布局配置更新
const setDispatchThemeConfig = () => {
	setLocalThemeConfig();
	setLocalThemeConfigStyle();
};
// 存储布局配置
const setLocalThemeConfig = () => {
	Local.remove('themeConfig');
	Local.set('themeConfig', getThemeConfig.value);
};
// 存储布局配置全局主题样式（html根标签）
const setLocalThemeConfigStyle = () => {
	Local.set('themeConfigStyle', document.documentElement.style.cssText);
};
// 一键复制配置
const onCopyConfigClick = () => {
	let copyThemeConfig = Local.get('themeConfig');
	copyThemeConfig.isDrawer = false;
	copyText(JSON.stringify(copyThemeConfig)).then(() => {
		getThemeConfig.value.isDrawer = false;
	});
};
// 一键恢复默认
const onResetConfigClick = () => {
	Local.clear();
	window.location.reload();
	// @ts-ignore
	Local.set('version', __VERSION__);
};
onMounted(() => {
	nextTick(() => {
		// 判断当前布局是否不相同，不相同则初始化当前布局的样式，防止监听窗口大小改变时，布局配置logo、菜单背景等部分布局失效问题
		if (!Local.get('frequency')) initLayoutChangeFun();
		Local.set('frequency', 1);
		// 监听窗口大小改变，非默认布局，设置成默认布局（适配移动端）
		mittBus.on('layoutMobileResize', (res: LayoutMobileResize) => {
			getThemeConfig.value.layout = res.layout;
			getThemeConfig.value.isDrawer = false;
			initLayoutChangeFun();
			state.isMobile = other.isMobile();
		});
		setTimeout(() => {
			// 默认样式
			onColorPickerChange();
			// 灰色模式
			if (getThemeConfig.value.isGrayscale) onAddFilterChange();
			// 深色模式
			if (getThemeConfig.value.isIsDark) onAddDarkChange();
			// 开启水印
			//onWartermarkChange();
			// 语言国际化
			if (Local.get('themeConfig')) locale.value = Local.get('themeConfig').globalI18n;
		}, 100);
	});
});
onUnmounted(() => {
	mittBus.off('layoutMobileResize', () => {});
});

// 暴露变量
defineExpose({
	openDrawer,
});
</script>

<style scoped lang="scss">

.layout-breadcrumb-seting-bar {
	.layout-breadcrumb-seting-bar-flex {
		display: flex;
		align-items: center;
		margin-bottom: 5px;
		&-label {
			flex: 1;
			color: var(--color-text-1);
		}
	}
	.layout-drawer-content-flex {
		overflow: hidden;
		display: flex;
		flex-wrap: wrap;
		align-content: flex-start;
		margin: 0 -5px;
		.layout-drawer-content-item {
			width: 50%;
			height: 70px;
			cursor: pointer;
			border: 1px solid transparent;
			position: relative;
			padding: 5px;
			.w-layout {
				height: 100%;
				.w-aside-dark {
					background-color: rgb(var(--gray-6));
				}
				.w-aside {
					background-color: rgb(var(--gray-5));
				}
				.w-header {
					background-color: rgb(var(--gray-6));
				}
				.w-main {
					background-color: rgb(var(--gray-4));
				}
			}
			.w-circular {
				border-radius: 2px;
				overflow: hidden;
				border: 1px solid transparent;
				transition: all 0.3s ease-in-out;
			}
			.drawer-layout-active {
				border: 1px solid;
				border-color: var(--w-color-primary);
			}
			.layout-tips-warp,
			.layout-tips-warp-active {
				transition: all 0.3s ease-in-out;
				position: absolute;
				left: 50%;
				top: 50%;
				transform: translate(-50%, -50%);
				border: 1px solid;
				border-color: var(--color-primary-light-4);
				border-radius: 100%;
				padding: 4px;
				.layout-tips-box {
					transition: inherit;
					width: 30px;
					height: 30px;
					z-index: 9;
					border: 1px solid;
					border-color: var(--color-primary-light-4);
					border-radius: 100%;
					.layout-tips-txt {
						transition: inherit;
						position: relative;
						top: 5px;
						font-size: var(--font12);
						line-height: 1;
						letter-spacing: 2px;
						white-space: nowrap;
						color: var(--color-primary-light-4);
						text-align: center;
						transform: rotate(30deg);
						left: -1px;
						background-color: rgb(var(--gray-4));
						width: 32px;
						height: 17px;
						line-height: 17px;
					}
				}
			}
			.layout-tips-warp-active {
				border: 1px solid;
				border-color: var(--w-color-primary);
				.layout-tips-box {
					border: 1px solid;
					border-color: var(--w-color-primary);
					.layout-tips-txt {
						color: var(--w-color-primary) !important;
						background-color: rgb(var(--gray-4)) !important;
					}
				}
			}
			&:hover {
				.w-circular {
					transition: all 0.3s ease-in-out;
					border: 1px solid;
					border-color: var(--w-color-primary);
				}
				.layout-tips-warp {
					transition: all 0.3s ease-in-out;
					border-color: var(--w-color-primary);
					.layout-tips-box {
						transition: inherit;
						border-color: var(--w-color-primary);
						.layout-tips-txt {
							transition: inherit;
							color: var(--w-color-primary) !important;
							background-color: rgb(var(--gray-4)) !important;
						}
					}
				}
			}
		}
	}
	.copy-config {
		margin: 10px 0;
		.copy-config-btn {
			width: 100%;
			margin-top: 15px;
		}
		.copy-config-btn-reset {
			width: 100%;
			margin: 10px 0 0;
		}
	}

}
</style>

