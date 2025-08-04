<template>
	<div class="left-side">
		<div class="information">
			<div class="title">
				<CoolFanhuiA89n18hc class="back" size="30" color="var(--w-color-primary)" @click="back" />
				<span class="name" :title="currentLibrary.name">{{ currentLibrary.name }}</span>
				<w-popover placement="bottom" trigger="hover" content-class="nav-select-popover nav-handle-popover">
					<div class="ability" v-if="isPublic">
						<CoolMore_2LineWe size="16" />
					</div>
					<template #content>
						<contextmenu :item="currentLibrary" @currentContextmenuClick="libraryContextmenuClick" />
					</template>
				</w-popover>
			</div>
			<div class="des" v-if="currentLibrary.descr">{{ currentLibrary.descr }}</div>

			<div class="search">
				<w-input
					v-model="searchText"
					class="searchInput"
					:class="!isPublic ? 'searchWidth' : ''"
					placeholder="关键词"
					clearable
					@input="changeText"
					@clear="changeText('')"
				>
					<template #prefix><cool-sousuo size="1em" color="currentColor"></cool-sousuo></template>
				</w-input>
				<w-popover placement="bottom" trigger="hover" content-class="nav-select-popover nav-handle-popover">
					<w-button type="primary" v-if="isPublic">
						<template #icon>
							<CoolXinzeng size="18" color="#fff" />
						</template>
					</w-button>
					<template #content>
						<contextmenu :item="{}" :type="3" @currentContextmenuClick="currentContextmenuClick" />
					</template>
				</w-popover>
			</div>
		</div>
		<div class="list">
			<w-skeleton v-if="initSkeleton" :animation="true">
				<w-space direction="vertical" :style="{ width: '100%' }" size="default">
					<w-skeleton-line :rows="18" />
				</w-space>
			</w-skeleton>
			<div class="library-tree" v-else>
				<input type="file" ref="inputFile" @change="handleChange" v-show="false" :accept="fileUpdate.accept" multiple />
				<div class="init" v-show="!currentLibrary.fileCount && !treeList.length">
					<img :src="init_centent" alt="" />
					<!-- <w-upload
						:accept="fileUpdate.accept"
						action
						multiple
						:custom-request="handleChange"
						:show-file-list="false"
					>
						<template #upload-button>
							<p>暂无数据<span>立即上传</span></p>
						</template>
					</w-upload> -->
					<p>暂无数据 <span @click="inputFileClick" v-if="isPublic">立即上传</span></p>
				</div>
				<w-tree
					v-if="treeList.length"
					ref="libraryTree"
					:data="treeList"
					:fieldNames="treeFieldNames"
					:accordion="true"
					:indent="20"
					:highlight-current="true"
					:draggable="isPublic && !isEdit"
					blockNode
					:allow-drop="isAllowDrop"
					@drop="onDrop"
					@select="treeNodeSelect"
				>
					<template #title="nodeData">
						<p
							class="tree-title"
							:title="nodeData[treeFieldNames.title]"
							:class="{ flex: nodeParams.key == nodeData.id && nodeParams.status }"
							@dblclick="nodePreview(nodeData)"
						>
							<CoolWenjianjia class="fileIcon CoolWenjianjia" v-if="nodeData.type == 1" size="20" />
							<CoolWenjianjiakai class="fileIcon CoolWenjianjiakai" v-if="nodeData.type == 1" size="20" />
							<CoolDocx class="fileIcon" v-if="['.doc', '.docx'].includes(nodeData.fileType)" size="20" />
							<CoolPdf class="fileIcon" v-if="nodeData.fileType == '.pdf'" size="20" />
							<CoolTxt class="fileIcon" v-if="nodeData.fileType == '.txt'" size="20" />
							<w-input
								v-if="nodeParams.key == nodeData.id && nodeParams.status"
								v-model="nodeParams.value"
								ref="treeNodeInput"
								@press-enter="nodeLabelUpdate(nodeData)"
								@blur="nodeLabelUpdate(nodeData)"
								@focus="getInputFocus($event)"
							/>
							<template v-else>
								<template v-if="((index = getMatchIndex(nodeData[treeFieldNames.title])), index < 0)"
									><span class="tree-title-span">{{ nodeData[treeFieldNames.title] }}</span>
								</template>
								<span class="tree-title-span" v-else>
									{{ nodeData[treeFieldNames.title]?.substr(0, index) }}
									<span style="color: var(--color-primary-light-4)"> {{ nodeData[treeFieldNames.title]?.substr(index, searchText.length) }} </span
									>{{ nodeData[treeFieldNames.title]?.substr(index + searchText.length) }}
								</span>
							</template>
						</p>
						<w-popover placement="bottom" trigger="hover" content-class="nav-select-popover nav-handle-popover">
							<div class="ability" v-if="isPublic">
								<CoolMore_2LineWe size="16" />
							</div>
							<template #content>
								<contextmenu :item="nodeData" :type="2" @currentContextmenuClick="currentContextmenuClick" />
							</template>
						</w-popover>
					</template>
				</w-tree>
			</div>
		</div>
		<div class="footer" v-if="currentLibrary.creator">
			<w-progress v-if="isPublic" :percent="percent" :show-text="false" />
			<p :style="{ marginTop: isPublic ? '12px' : '28px' }">
				<span class="size">
					{{ ThousandWithNumber(knowledgesSize.charCount) }} / {{ isPublic ? ThousandWithNumber(knowledgesSize.capacity) + '字' : '不限制' }}
				</span>
				<span class="upgradation" v-if="isPublic"> 升级扩容</span>
				<!-- @click="expandOpen" -->
			</p>
			<div class="expand" v-if="expand.show">
				<p class="expand-title">知识库容量</p>
				<p>当前知识库总容量{{ ThousandWithNumber(currentLibrary.capacity) }}字</p>
				<p>剩余可用{{ ThousandWithNumber(residue) }}字</p>
				<hr />
				<div class="detail" v-for="item in wordCounts" :key="item.id">
					<span> {{ item.fileName }} </span>
					<span> {{ ThousandWithNumber(item.wordCount) }} </span>
					<hr />
				</div>
			</div>
		</div>
	</div>
	<w-modal width="520px" modal-class="knowledgeModal knowledgeDelModalBG" :mask-closable="false" v-model:visible="visible">
		<template #title>
			{{ selectNode.type === 1 ? '确认删除此文件夹吗？删除后无法恢复' : '确认删除此文件吗？' }}
		</template>
		<div class="tips">{{ selectNode.type === 1 ? '删除文件夹后，文件夹内所有文件将被同步删除，此操作不可撤销。' : '删除后无法恢复' }}</div>
		<template #footer>
			<w-button @click="visible = false">取消</w-button>
			<w-button type="primary" @click="handleok">删除</w-button>
		</template>
	</w-modal>
	<delModal ref="delModalRef" />
</template>

<script lang="ts" name="navMenuVertical1" setup>
import {
	defineAsyncComponent,
	computed,
	nextTick,
	onBeforeMount,
	onMounted,
	reactive,
	ref,
	watch,
	onUnmounted,
	getCurrentInstance,
	onBeforeUnmount,
} from 'vue';
import { IconPlus } from 'winbox-ui-next/es/icon';
import { useRoute, useRouter } from 'vue-router';
import { getAppList } from '/@/api/chat';
import { useChatStore } from '/@/stores/chat';
import {
	knowledgeDetail,
	fileTree,
	fileUploadBatch,
	createDir,
	fileTreeDrag,
	fileUpdateName,
	fileDelete,
	dirUpdateName,
	dirDelete,
	listKnowledgeBaseFile,
	userKnowledgesSize,
} from '/@/api/knowledge';
import { useKnowledgeState } from '/@/stores/knowledge';
import { useRobotStore } from '/@/stores/robot';
import { Message } from 'winbox-ui-next';
import { getSuffix, formatbytes } from '/@/utils/utils.ts';
import { ThousandWithNumber } from '/@/utils/format.ts';
import init_centent from '/@/assets/knowledge/init_centent.png';
import { v4 as uuidv4 } from 'uuid';
import { Session } from '/@/utils/storage';
import mittBus from '/@/utils/mitt';

const instance: any = getCurrentInstance();
const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const isPublic: any = computed(() => {
	// if (currentLibrary.value.creator == Session.get('userId')) {
	// 	return true;
	// }
	let status = currentLibrary.value.authority != 2;
	return !status;
});
const initSkeleton = ref(true);
const isEdit = ref(false);
const knowledgeFileList: any = computed(() => knowledgeState.fileList);
const previewData: any = computed(() => knowledgeState.previewData);
const residue = computed(() => Number(currentLibrary.value.capacity) - Number(currentLibrary.value.charCount));
const knowledgesSize: any = computed(() => knowledgeState.knowledgesSize);
const percent = computed(() => Number(knowledgesSize.value.charCount) / Number(knowledgesSize.value.capacity));
const contextmenu = defineAsyncComponent(() => import('./contextmenu.vue'));
const delModal = defineAsyncComponent(() => import('./delModal.vue'));
const delModalRef = ref();
const treeFieldNames = {
	key: 'id',
	title: 'name',
	children: 'child',
};
const visible = ref(false);
// watch(
// 	() => knowledgeFileList.value,
// 	(val: any) => {
// 		if (val) {
// 			// treeList.value = val.child
// 		}
// 	},
// 	{ immediate: true, deep: true }
// );
const nodeParams = reactive({
	key: '',
	value: '',
	status: false,
});
const inputFile = ref();
const searchText = ref('');
let originTreeData: any = [];
const treeList: any = ref(originTreeData);
const wordCounts: any = ref([]);
const selectNode: any = ref({});
const currentAppId = ref();
const clickedExpendKey = ref();
const state = reactive({
	treeListMap: {},
});
const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const robotStore = useRobotStore();
const { appId } = route.params as { appId: string };
const expand = reactive({
	show: false,
});
const libraryTree = ref();
const back = () => {
	// try {
	// 	router.back();
	// } catch (err) {
	router.push({
		path: '/knowledge/0',
	});
	// }
};
const libraryContextmenuClick = (item: any) => {
	switch (item.value) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			let type = 2;
			knowledgeState.modelOpen(type, item);
			break;
		case 4:
			delModalRef.value.handleClick(item, router.back);
			break;
	}
};
// 关闭页签
// todo: 关闭之后需要调取删除已打开的画布页签的接口（批量删除）
const currentContextmenuClick = (item: any) => {
	selectNode.value = item;
	switch (item.value) {
		case 1:
			if (item.id) {
				createDirFn();
			} else {
				createDirFn('init');
			}
			break;
		case 2:
			inputFileClick();
			break;
		case 3:
			let typeIndex = item?.fileType ? item?.fileType.length : 0;
			let value = item.type === 0 ? item[treeFieldNames.title].slice(0, -typeIndex) : item[treeFieldNames.title];
			nodeParams.key = item[treeFieldNames.key];
			nodeParams.value = value;
			nodeParams.status = true;
			isEdit.value = true;
			// treeNodeInput
			setTimeout(() => {
				instance?.refs.treeNodeInput.focus();
			});
			break;
		case 4:
			visible.value = true;
			// nodeDelete(item)
			break;
	}
};
const nodeLabelUpdate = async (item: any) => {
	if (nodeParams.value && nodeParams.key == item[treeFieldNames.key]) {
		item[treeFieldNames.title] = selectNode.value.type === 0 ? nodeParams.value + selectNode.value.fileType : nodeParams.value;
		nodeParams.status = false;
		let api = item.type === 0 ? fileUpdateName : dirUpdateName;
		let params = {};
		if (item.type === 0) {
			params = {
				id: item[treeFieldNames.key],
				fileName: nodeParams.value,
			};
		} else {
			params = {
				folderId: item[treeFieldNames.key],
				name: nodeParams.value,
			};
		}
		let res = await api(params);
		// getTree()
	}
	isEdit.value = false;
	nodeParams.key = '';
	nodeParams.value = '';
};
const getInputFocus = (event) => {
	console.log(event);
	event.currentTarget.select();
};
const handleok = () => {
	visible.value = false;
	nodeDelete(selectNode.value);
};
const refreshKnowledgesSize = async () => {
	let resSize = await userKnowledgesSize();
	if (resSize?.code === 200 && resSize?.data) {
		knowledgeState.setKnowledgesSize(resSize.data);
	}
};

const nodeDelete = async (item: any) => {
	let api = item.type === 0 ? fileDelete : dirDelete;
	let id = item[treeFieldNames.key];
	let res = await api(id);
	if (res.code == 200) {
		getTree();
		if (item.type === 0) {
			let currItem: any = {};
			if (previewData.value.active == id) {
				currItem = {};
			}
			let params: any = {
				currItem,
				active: '',
				params: {},
			};
			knowledgeState.setPreviewData(params);
		}
	}
};
//树状数组指定位置增加子节点（必须标识为key）
const addChildren = ({ arr, value, item }) => {
	let newArr: any = [];
	if (!arr.length || item.level == 1) {
		newArr = arr;
		newArr.unshift(item);
		return newArr;
	}
	arr.forEach((ele) => {
		if (ele[treeFieldNames.key] === value) {
			ele[treeFieldNames.children].unshift(item);
			newArr.push(ele);
		} else {
			if (ele[treeFieldNames.children]?.length > 0) {
				ele[treeFieldNames.children] = addChildren({
					arr: ele[treeFieldNames.children],
					value,
					item,
				});
				newArr.push(ele);
			} else {
				newArr.push(ele);
			}
		}
	});
	return newArr;
};
const createDirFn = async (type: any | null = null) => {
	let status = type == 'init';
	let params = {
		knowledgeBaseId: appId,
		name: '新建文件夹',
		parentId: selectNode.value?.id || knowledgeFileList.value.id,
		parentLevel: selectNode.value?.level || 0,
	};
	if (status) {
		params.parentId = knowledgeFileList.value.id;
		params.parentLevel = 0;
	}
	let res = await createDir(params);
	if (res?.code === 200 && res?.data) {
		let list = await addChildren({
			arr: treeList.value,
			value: params.parentId,
			item: res.data,
		});
		treeList.value = list;
		// console.log(libraryTree.value)
		// if(!status){
		// 	libraryTree.value.expandedKeys([params.parentId])
		// }
	} else {
		Message.warning(res.msg);
	}
};
const getInfo = async () => {
	if (!appId) return;
	let res = await knowledgeDetail(appId);
	if (res?.code === 200 && res?.data) {
		let data = res.data;
		data.fileCount = Number(data.fileCount);
		knowledgeState.setCurrentLibrary(res?.data);
	} else {
		// back();
	}
};
const getTree = async () => {
	if (!appId) return;
	let res = await fileTree(appId);
	if (res?.code === 200 && res?.data) {
		initSkeleton.value = false;
		let list = res.data.child || [];
		if (!list.length) {
			getInfo();
		}
		knowledgeState.setFileList(res.data);
		originTreeData = list;
		treeList.value = originTreeData;
		refreshKnowledgesSize();
	} else {
		Message.warning(res.msg);
	}
};
const getWordCounts = async () => {
	if (!appId) return;
	let params = { knowledgeBaseId: appId, keyword: '' };
	let res = await listKnowledgeBaseFile(params);
	if (res?.code === 200 && res?.data) {
		wordCounts.value = res.data;
	}
};

const expandOpen = () => {
	expand.show = true;
};

const searchData = (keyword: string) => {
	const loop = (data: any) => {
		const result: any = [];
		data.forEach((item: any) => {
			if (item[treeFieldNames.title].toLowerCase().indexOf(keyword.toLowerCase()) > -1) {
				result.push({ ...item });
			} else if (item[treeFieldNames.children]) {
				const filterData = loop(item[treeFieldNames.children]);
				if (filterData.length) {
					result.push({
						...item,
						[treeFieldNames.children]: filterData,
					});
				}
			}
		});
		return result;
	};
	return loop(originTreeData);
};

const getMatchIndex = (title: string) => {
	if (!searchText.value) return -1;
	return title.toLowerCase().indexOf(searchText.value.toLowerCase());
};
const changeText = async (val: string) => {
	// originTreeData = api.getlist()
	if (!val) treeList.value = originTreeData;
	treeList.value = await searchData(val);
};
const onDrop = ({ dragNode, dropNode, dropPosition }) => {
	if (dropNode.type === 0 && dropPosition === 0) {
		return;
	}
	if (dragNode.type === 1 && dropNode.level >= 6) {
		return;
	}
	let newNodes = [];
	const data = treeList.value;
	const loop = (data, id, callback, parentId) => {
		data.some((item, index, arr) => {
			if (item[treeFieldNames.key] === id) {
				callback(item, index, arr, parentId);
				return true;
			}
			if (item[treeFieldNames.children]) {
				return loop(item[treeFieldNames.children], id, callback, item[treeFieldNames.key]);
			}
			return false;
		});
	};

	loop(
		data,
		dragNode.id,
		(_, index, arr) => {
			arr.splice(index, 1);
			if (dropPosition === 0) {
				loop(
					data,
					dropNode.id,
					(item, index, arr1, parentId) => {
						item[treeFieldNames.children] = item[treeFieldNames.children] || [];
						item[treeFieldNames.children].unshift(dragNode);
						newNodes = item;
						TreeDragSave(dropNode, dragNode, newNodes, item.id);
					},
					''
				);
			} else {
				loop(
					data,
					dropNode.id,
					(item, index, arr1, parentId) => {
						arr1.splice(dropPosition < 0 ? index : index + 1, 0, dragNode);
						newNodes = arr1;
						TreeDragSave(dropNode, dragNode, newNodes, parentId);
					},
					''
				);
			}
		},
		''
	);
};
const TreeDragSave = async (dropNode: any, dragNode: any, newNodes: any, parentId: any) => {
	let order = [];
	if (newNodes[treeFieldNames.children]) {
		// parentId = newNodes[treeFieldNames.key]
		order = newNodes[treeFieldNames.children].filter((i) => dragNode.type === i.type).map((i) => i[treeFieldNames.key]);
	} else if (newNodes.length) {
		// parentId = knowledgeFileList.value.id
		order = newNodes.filter((i) => dragNode.type === i.type).map((i) => i[treeFieldNames.key]);
	}
	let params = {
		id: dragNode[treeFieldNames.key],
		knowledgeId: currentLibrary.value.id,
		parentId: parentId || knowledgeFileList.value.id,
		type: dragNode.type,
		order,
	};
	let res = await fileTreeDrag(params);
};
const nodePreview = (item: any) => {
	if (window.innerWidth < 1920) useChatStore().isOpen = false;
	if (nodeParams.key == selectNode.value.id) return;
	if (item.type === 0) {
		let currItem: any = item;
		let active = item[treeFieldNames.key];
		let params = {
			currItem,
			active,
			params: {},
		};
		knowledgeState.setPreviewData(params);
		// knowledgeState.setDblclickName(item[treeFieldNames.title])
		let p = {
			value: '@' + item[treeFieldNames.title],
			id: item.id,
			dblclick: true,
		};
		mittBus.emit('InsertHtml', p);
	}
};
const treeNodeSelect = (selected: boolean, selectedNodes: any) => {
	console.log(selected, selectedNodes);
};

const inputFileClick = () => {
	inputFile.value.click();
};
const fileUpdate: any = computed(() => knowledgeState.fileUpdate);
const CHUNK_SIZE = 10 * 1024 * 1024; // 10MB
const fileList: any = ref([]);
const handleChange = (e: any) => {
	const uploadFile = e.target.files;
	const uploadList = [];
	for (let i = 0; i < uploadFile.length; i++) {
		let file = uploadFile[i];
		let suffixArr = fileUpdate.value.suffixArr;
		let name = file.name;
		let size = file.size;
		let suffix = getSuffix(name);

		if (!suffixArr.includes(suffix)) {
			Message.warning('只允许导入' + suffixArr.join('、') + '格式的文件');
			break;
		}
		let fileParms = {
			name,
			format: suffix,
			size: formatbytes(size),
			file: file,
			id: uuidv4(),
			type: 0,
			url: '',
			isState: 2,
		};
		// const MAN_SIZE = CHUNK_SIZE * 5; // 50MB
		// if (size > MAN_SIZE * 2 * 2) {
		// 	Message.warning('文件大小超出200M');
		// 	break;
		// }
		// fileList.value.push(fileParms);
		uploadList.push(fileParms);
		if (i == uploadFile.length - 1) {
			FileUploads(uploadList);
		}
	}
};
const handleDelete = (item: any) => {
	let index = fileList.value.findIndex((f: any) => f.id == item.id);
	fileList.value.splice(index, 1);
};
const FileUploads = async (files: any) => {
	// 循环上传
	let filesFilter = files;
	// let filesFilter = await files.filter((f: any) => {
	// 	return f.isState;
	// });
	if (!filesFilter.length) {
		return;
	}
	// return
	const fd = new FormData();
	filesFilter.forEach((f: any) => {
		fd.append('files', f.file);
	});
	fd.append('folderId', selectNode.value?.id || knowledgeFileList.value.id);
	fd.append('knowledgeId', currentLibrary.value.id);
	let res: any = fileUploadBatch(fd);
	knowledgeState.setFileUpdate({
		list: files,
		status: true,
	});
	return;
};
onBeforeMount(async () => {
	try {
		//存储知识库详情
		getInfo();
		knowledgeState.setModeCallback(getInfo);
	} catch (err) {
		throw new Error();
	}
});
if (!appId) {
	router.push({ name: 'knowledge', params: { appId: 0 } });
}
onMounted(() => {
	getTree();
	getWordCounts();
	document.addEventListener('click', (e: any) => {
		if (!['input', 'span'].includes(e.target.localName)) {
			nodeParams.status = false;
		}
	});
});
onUnmounted(() => {
	knowledgeState.restoreData();
	setTimeout(() => {
		knowledgeState.setCurrentLibrary({});
	});
});
watch(
	() => fileUpdate.value.isGetTree,
	(val: any) => {
		if (val) {
			getInfo();
			getTree();
			setTimeout(() => {
				knowledgeState.setFileUpdate({
					isGetTree: false,
				});
			});
		}
	},
	{ immediate: true, deep: true }
);
// 展开项的key值
let expendKey = computed(() => {
	let currentKey;
	for (const key in state.treeListMap) {
		if (state.treeListMap[key].includes(currentAppId.value)) {
			currentKey = key;
			break;
		}
	}
	return clickedExpendKey.value || currentKey;
});

const isInViewport = (element) => {
	var rect = element.getBoundingClientRect();
	return (
		rect.top >= 0 &&
		rect.left >= 0 &&
		rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
		rect.right <= (window.innerWidth || document.documentElement.clientWidth)
	);
};

watch(
	() => route.params.appId,
	(newVal: any) => {
		currentAppId.value = newVal;
		clickedExpendKey.value = undefined;
		nextTick(() => {
			// setTimeout(() => {
			// 	knowledgeState.setPreviewData({active:'',list:[],params:{}})
			// }, 300);
		});
	},
	{ immediate: true, deep: true }
);

// const isAllowDrop = ({ dropNode }) => !dropNode.fileType;
</script>
<style lang="scss" scoped>
.tree-demo :deep(.tree-node-dropover) > :deep(.w-tree-node-title),
.tree-demo :deep(.tree-node-dropover) > :deep(.w-tree-node-title):hover {
	animation: blinkBg 0.4s 2;
}

:deep(.w-tree-node-title-draggable) {
	&:hover {
		background: transparent !important;
	}
}

@keyframes blinkBg {
	0% {
		background-color: transparent;
	}

	100% {
		background-color: var(--color-primary-light-1);
	}
}
.left-side {
	// position: relative;
	width: 100%;
	padding: 0 16px 86px 16px;
	min-height: 100%;
	background: rgba(255, 255, 255, 0.5);
	box-sizing: border-box;
	.information {
		position: sticky;
		z-index: 10;
		top: 0;
		background: #fff;
		padding: 20px 0;
		.title {
			margin: 0;
			display: flex;
			align-items: center;
			justify-content: space-between;
			width: 100%;
			background: linear-gradient(180deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.6) 100%);
			border-radius: 8px;
			// border: 1px solid #ffffff;
			color: #181b49;
			cursor: pointer;
			.back {
				border-radius: 8px;
				margin-right: 8px;
			}
			.name {
				display: inline-block;
				width: calc(100% - 40px);
				font-size: var(--font18);
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #181b49;
				line-height: 28px;
				white-space: pre;
				overflow: hidden;
				text-overflow: ellipsis;
			}
		}
		.des {
			font-size: var(--font14);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #9a99aa;
			line-height: 22px;
			margin-top: 6px;
		}
	}
	.search {
		margin-top: 24px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		button {
			border-radius: 8px;
			background: linear-gradient(90deg, #7e9dff 0%, #355eff 100%);
			border: none;
		}
		.searchInput {
			&.searchWidth {
				width: 100% !important;
			}
			width: calc(100% - 45px);
			border-radius: 8px;
			background: transparent;
		}
	}

	.ability {
		display: inline-block;
	}
	.list {
		width: 100%;
		.library {
			margin-top: 12px;
			display: flex;
			align-items: center;
			width: 100%;
			height: 56px;
			background: linear-gradient(180deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.6) 100%);
			border-radius: 8px;
			border: 1px solid #ffffff;
			color: #181b49;
			cursor: pointer;
			.icon {
				width: 48px;
				height: 48px;
				background: #ff6200;
				border-radius: 8px;
				margin: 0 12px 0 8px;
			}
			.info {
				margin-right: 10px;
				width: 140px;
				.name {
					font-size: var(--font16);
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					line-height: var(--font22);
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
				.number {
					font-size: var(--font12);
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #9a99aa;
					line-height: var(--font18);
				}
			}
			&:hover {
				.name {
					color: #355eff;
				}
			}
		}
		.library:nth-of-type(1) {
			margin-top: 0;
		}

		.list-title {
			margin-top: 24px;
			font-size: var(--font20);
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			line-height: var(--font28);
		}
		.library-tree {
			// :deep(.w-tree > .w-tree-node) {
			// 	display: inline-block;
			// 	width: 100%;
			// }
			.title {
				margin-top: 24px;
				font-size: var(--font14);
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #9a99aa;
			}
			.init {
				margin-top: 102px;
				text-align: center;
				img {
					margin: 0 auto;
					height: 150px;
				}
				// :deep(.w-upload) {
				text-align: center;
				p {
					margin-bottom: var(--font16);
					font-size: var(--font14);
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #646479;
					span {
						color: #355eff;
						cursor: pointer;
					}
				}
				// }
			}
			:deep(.w-tree) {
				padding-top: 2px;
				.w-tree-node-drag-icon {
					display: none !important;
				}
				.w-tree-node {
					width: 100%;
					box-sizing: border-box;
					height: 40px;
					border-radius: 8px;
					.ability {
						opacity: 0;
					}
					&:hover {
						.ability {
							opacity: 1;
						}
					}
				}
				.w-tree-node-switcher {
					display: inline-flex;
					align-items: center;
				}
				.w-tree-node-title {
					width: 100%;
					display: inline-block;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
					.w-tree-node-title-text {
						display: inline-block;
						width: 100%;
						position: relative;
						.tree-title {
							transform: translateY(7%);
							width: calc(100% - 20px);
							display: inline-block;
							.w-input-wrapper,
							.tree-title-span {
								width: 100%;
								display: inline-block;
								user-select: none;
								-webkit-user-select: none;
								-moz-user-select: none;
								-ms-user-select: none;
							}
							.tree-title-span {
								width: calc(100% - 20px);
								display: inline-block;
								white-space: pre;
								overflow: hidden;
								text-overflow: ellipsis;
							}
							.cool-icon {
								transform: translateY(-13%);
								margin-right: 8px;
							}
							&.flex {
								display: flex;
								align-items: center;
								.w-input-wrapper {
									transform: translateY(-5%);
								}
								.cool-icon {
									transform: translateY(-5%);
									margin-right: 8px;
								}
							}
						}
						.ability {
							position: absolute;
							right: 0;
							top: 50%;
							transform: translateY(-50%);
						}
					}
				}
				.w-tree-node-title-highlight {
					border: 1px solid rgba(53, 94, 255, 1);
				}
				.w-tree-node {
					&[type='0'] {
						.w-tree-node-title-highlight {
							border: none;
						}
					}
				}
				.w-tree-node-title-gap-bottom::before {
					height: 2px;
					bottom: 1px;
					background-color: rgba(53, 94, 255, 1);
				}
				.w-tree-node:hover {
					background: #ebefff;
					// .w-input-wrapper,.tree-title-span{
					// 	width: calc(100% - 20px) !important;
					// }
				}
			}
		}
	}
	.footer {
		position: absolute;
		bottom: 0;
		left: 0;
		width: 100%;
		padding: 20px 24px;
		height: 86px;
		box-sizing: border-box;
		background: #fff;
		p {
			margin-top: 12px;
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: var(--font14);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #9a99aa;
			.upgradation {
				cursor: pointer;
				// color: #355eff;
				color: #666;
				cursor: no-drop;
			}
		}
		.expand {
			position: absolute;
			top: 12px;
			transform: translateY(-100%);
			width: calc(100% - 48px);
			padding: 20px;
			box-sizing: border-box;
			background: #ffffff linear-gradient(180deg, rgba(172, 193, 255, 0.2) 0%, rgba(235, 244, 252, 0) 100%),
				linear-gradient(180deg, rgba(126, 242, 255, 0.2) 0%, rgba(235, 244, 252, 0) 100%);

			.expand-title {
				font-size: var(--font16);
				font-family: MiSans-Regular, MiSans;
				font-weight: 400;
				color: #181b49;
				line-height: var(--font20);
				margin-bottom: 6px;
			}
			p {
				margin-top: 0;
				font-size: var(--font12);
				line-height: var(--font20);
			}
			p:nth-child(3) {
				margin-bottom: 15px;
			}
			.detail {
				width: 100%;
				height: 39px;
				display: flex;
				align-items: center;
				justify-content: space-between;
				font-size: var(--font12);
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #9a99aa;
				span:nth-child(1) {
					width: 80%;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
				span:nth-child(2) {
					text-align: right;
				}
			}
			.expand-button {
				margin-top: 9px;
				font-size: var(--font14);
				font-family: MiSans-Regular, MiSans;
				font-weight: 400;
				color: #355eff;
				line-height: 19px;
				text-align: center;
				span {
					cursor: pointer;
				}
			}
		}
	}
}
</style>
<style lang="scss">
.CoolWenjianjiakai {
	display: none;
}
.w-tree-node-expanded {
	.CoolWenjianjiakai {
		display: inline-block;
	}
	.CoolWenjianjia {
		display: none;
	}
}
</style>
