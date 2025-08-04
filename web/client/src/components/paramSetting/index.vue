<template>
	<div>
		<template v-if="options.type === 'LONG'">
			<w-row class="grid-demo" align="center">
				<w-col :span="isMobile ? 13 : 14">
					<w-slider :min="Number(range[0])" :max="Number(range[1])" @change="changeFun" v-model="defaultValue" :style="{ width: '90%' }" />
				</w-col>
				<w-col :span="isMobile ? 11 : 10">
					<w-input-number v-model="defaultValue" @change="changeFun" :min="Number(range[0])" :max="Number(range[1])" />
				</w-col>
			</w-row>
		</template>
		<template v-else-if="options.type === 'DOUBLE'">
			<w-row class="grid-demo" align="center">
				<w-col :span="isMobile ? 13 : 14">
					<w-slider
						:show-arrow="false"
						:min="Number(range[0])"
						:max="Number(range[1])"
						:step="0.1"
						@change="changeFun"
						v-model="defaultValue"
						:style="{ width: '90%' }"
					/>
				</w-col>
				<w-col :span="isMobile ? 11 : 10">
					<w-input-number v-model="defaultValue" @change="changeFun" :step="0.1" :precision="1" :min="Number(range[0])" :max="Number(range[1])" />
				</w-col>
			</w-row>
		</template>
		<template v-else-if="options.type === 'ENUM'">
			<template v-if="isIcon">
				<w-select
					:trigger-props="{ position: 'top' }"
					v-model="defaultValue"
					@change="changeFun"
					placeholder="请选择"
					style="width: auto; min-width: 140px"
				>
					<template #label="{ data }">
						<span>
							<SvgIcon style="margin-right: 8px" :size="16" :name="data.value == 'baidu' ? 'cool-bing' : `cool-${data?.value}`" />
						</span>
						{{ data?.label }}
					</template>
					<w-option v-for="(item, index) in range" :class="item.value" :label="item.label" :value="item.value" :key="index">
						<SvgIcon class="isIconSelect" :size="16" :name="item.value == 'baidu' ? 'cool-bing' : `cool-${item?.value}`" />{{ item.label }}
					</w-option>
				</w-select>
			</template>
			<template v-else>
				<w-select v-if="range.length > 5" v-model="defaultValue" @change="changeFun" placeholder="请选择">
					<w-option v-for="(item, index) in range" :label="item.label" :value="item.value" :key="index">{{ item.label }}</w-option>
				</w-select>
				<w-radio-group v-else size="medium" class="radio-group-param" type="button" v-model="defaultValue" @change="changeFun">
					<w-radio v-for="(item, index) in range" :value="item.value" :key="index">
						<template v-if="item.label == '严谨学者'">
							<w-tooltip :content="item.label">
								<CoolYanjin size="14" />
							</w-tooltip>
						</template>
						<template v-else-if="item.label == '中规中矩'"
							><w-tooltip :content="item.label"><CoolZhonggui size="14" /></w-tooltip
						></template>
						<template v-else-if="item.label == '天马行空'"
							><w-tooltip :content="item.label"><CoolTianmahangkong size="14" /></w-tooltip
						></template>
						<template v-else>{{ item.label }}</template>
					</w-radio>
				</w-radio-group>
			</template>
		</template>
		<template v-else-if="options.type === 'ARRAY'">
			<w-select v-model="defaultValue" placeholder="请选择" multiple :max-tag-count="2">
				<w-option v-for="(item, index) in range" :key="index">{{ item }}</w-option>
			</w-select>
		</template>
		<template v-else-if="options.type === 'STRING'">
			<w-input v-if="range" v-model="defaultValue" :max-length="Number(range[1])" show-word-limit @change="changeFun" />
			<w-input v-else v-model="defaultValue" @change="changeFun" />
		</template>
		<template v-else-if="options.type === 'DATE'">
			<w-date-picker
				v-model="defaultValue"
				style="width: 100%"
				@change="changeFun"
				:disabledDate="
					(current) => {
						return disabledDate(current, options.range);
					}
				"
				type="date"
				:clearable="false"
				placeholder="请选择"
			>
			</w-date-picker>
		</template>
	</div>
</template>

<script lang="ts" setup>
import { toRefs, computed, watch, ref, onMounted } from 'vue';
import { useChatStore } from '/@/stores/chat';
import { formatDate } from '/@/utils/formatTime';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
const { isMobile } = useBasicLayout();
const chatStore = useChatStore();
const emit = defineEmits(['changeParam', 'refresh']);
const props = defineProps({
	options: {
		type: Object,
		default() {
			return {};
		},
	},
});
const { options } = toRefs(props);

const isIcon = ref(false);
const range = computed(() => {
	let value = options.value.range;
	if (options.value.type === 'ENUM') {
		value = value.map((item) => {
			if (item.indexOf('|') > -1) {
				if (options.value.key == 'web_source_list') {
					isIcon.value = true;
				}
				let [value, label] = item.split('|').map((s) => s.trim());
				return { label, value };
			}
			let [value, label] = [item, item];
			return { label, value };
		});
	}
	return value;
});
const webSourceList = computed(() => {
	let value = options.value.range;
	if (options.value.type === 'ENUM' && options.value.key === 'web_source_list') {
		value = value.map((item) => {
			if (item.indexOf('|') > -1) {
				isIcon.value = true;
				let [value, label] = item.split('|').map((s) => s.trim());
				return { label, value };
			}
			let [value, label] = [item, item];
			return { label, value };
		});
	}
	return value;
});

onMounted(() => {
	if (options.value.type === 'ENUM' && options.value.key === 'web_source_list') {
		chatStore.webSourceList = webSourceList.value;
	}
});

const defaultValue = computed({
	get() {
		if (options.value.type === 'DATE') {
			const val = options.value.defaultValue == '$now_date' ? formatDate(new Date(), 'YYYY-mm-dd') : options.value.defaultValue;
			let data = {
				key: options.value.key,
				defaultValue: val,
			};
			emit('changeParam', data);
			return val;
		}
		if (options.value.type === 'LONG' || options.value.type === 'DOUBLE') {
			return Number(options.value.defaultValue);
		}
		return options.value.defaultValue;
	},
	set(newVal) {
		options.value.defaultValue = newVal;
	},
});

const changeFun = () => {
	let data = {
		key: options.value.key,
		defaultValue: defaultValue.value,
	};
	emit('changeParam', data);
};

const isOffline = (v) => {
	let arr = chatStore.dialogueParamsList.filter((i) => {
		return i.key == 'web_source_list';
	});

	// if (v == 'offline' || (arr.length && arr[0].defaultValue == 'offline')) {
	// 	chatStore.dialogueParamsList
	// 		.filter((i) => {
	// 			return i.key != 'web_source_list';
	// 		})
	// 		.forEach((i) => {
	// 			i.isSystem = true;
	// 		});
	// } else {
	// 	chatStore.dialogueParamsListOld.forEach((i) => {
	// 		chatStore.dialogueParamsList.forEach((k) => {
	// 			if (i.key == k.key) {
	// 				k.isSystem = i.isSystem;
	// 			}
	// 		});
	// 	});
	// }
	emit('refresh');
};
const disabledDate = (time: any, dateString: any) => {
	const variable = new Date(time);
	if (Object.prototype.toString.call(variable) === '[object Date]' && !isNaN(variable.getTime())) {
		return variable.getTime() > new Date(dateString[1]).getTime() || variable.getTime() < new Date(dateString[0]).getTime();
	}
};

watch(
	defaultValue,
	() => {
		isOffline(defaultValue.value);
	},
	{
		immediate: true,
		deep: true,
	}
);
</script>

<style scoped lang="scss">
.radio-group-param {
	--color-bg-1: transparent;
	width: 100%;
	border-radius: 4px;
	overflow: hidden;
	border: 1px solid #c8cbd4;
	.w-radio-button {
		flex: 1;
		border-radius: 4px;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		--color-text-2: #181b49;
		:deep(.w-radio-button-content) {
			padding: 0 3px;
			text-align: center;
			border: none;
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
			border-right: 1px solid #e4e8ee;
			line-height: 16px;
			margin: 6px 0;
			// color: #181B49;
		}
		&:last-child {
			:deep(.w-radio-button-content) {
				border: none;
			}
		}
	}

	.w-radio-checked {
		--color-bg-5: #fff;
		background: var(--w-color-primary);
		border-radius: 4px;

		:deep(.w-radio-button-content) {
			border-right: none;
		}
	}
}
</style>
<style lang="scss">
.isIconSelect {
	margin-right: 10px;
	margin-left: 5px;
}
.offline {
	position: relative;

	// &::before {
	// 	content: '';
	// 	position: absolute;
	// 	width: 90%;
	// 	height: 1px;
	// 	background: #d0d5dc;
	// 	left: 5%;
	// 	bottom: 0;
	// 	text-align: center;
	// }
}
</style>
