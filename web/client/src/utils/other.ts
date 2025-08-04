import { nextTick } from 'vue';
import router from '/@/router/index';
import pinia from '/@/stores/index';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { useChatStore } from '/@/stores/chat';
import { i18n } from '/@/i18n/index';
import { verifyUrl } from '/@/utils/toolsValidate';

/**
 * 设置对话框滚动位置
 * @param el dom 目标元素
 * @param top 滚动高度
 * @description 用于控制滚动条高度
 */
export function setScrollPosition(el: string, type?: string, top?: number) {
	const dom = document.querySelector(el);
	const chatStore = useChatStore();
	// 滚动条已经到达底部 
	// if (dom.scrollTop + dom.clientHeight == dom.scrollHeight) { 
	nextTick(() => {
		// const clientHeight = dom.clientHeight;
		// const scrollTop = dom.scrollTop;
		// const scrollHeight = dom.scrollHeight;
		if (chatStore.scrollbarBottom) {
			dom.style.scrollBehavior = type || 'smooth';
			dom.scrollTop = top || dom.scrollHeight;
		}

		// if (clientHeight + scrollTop === scrollHeight) {
		// 	console.log(11111111111);
		// }

	});
	// } 
}

/**
 * 设置浏览器标题国际化
 * @method const title = useTitle(); ==> title()
 */
export function useTitle() {
	const stores = useThemeConfig(pinia);
	const { themeConfig } = storeToRefs(stores);
	nextTick(() => {
		let webTitle = '';
		let globalTitle: string = themeConfig.value.globalTitle;
		webTitle = setTagsViewNameI18n(router.currentRoute.value);
		document.title = `${webTitle} - ${globalTitle}` || globalTitle;
	});
}


/**
 * 设置 自定义 tagsView 名称、 自定义 tagsView 名称国际化
 * @param params 路由 query、params 中的 tagsViewName
 * @returns 返回当前 tagsViewName 名称
 */
export function setTagsViewNameI18n(item: any) {
	let tagsViewName: string = '';
	const { query, params, meta } = item;
	if (query?.tagsViewName || params?.tagsViewName) {
		if (/\/zh-cn|en|zh-tw\//.test(query?.tagsViewName) || /\/zh-cn|en|zh-tw\//.test(params?.tagsViewName)) {
			// 国际化
			const urlTagsParams = (query?.tagsViewName && JSON.parse(query?.tagsViewName)) || (params?.tagsViewName && JSON.parse(params?.tagsViewName));
			tagsViewName = urlTagsParams[i18n.global.locale.value];
		} else {
			// 非国际化
			tagsViewName = query?.tagsViewName || params?.tagsViewName;
		}
	} else {
		// 非自定义 tagsView 名称
		tagsViewName = i18n.global.t(meta.title);
	}
	return tagsViewName;
}

/**
 * 图片懒加载
 * @param el dom 目标元素
 * @param arr 列表数据
 * @description data-xxx 属性用于存储页面或应用程序的私有自定义数据
 */
export const lazyImg = (el: string, arr: EmptyArrayType) => {
	const io = new IntersectionObserver((res) => {
		res.forEach((v: any) => {
			if (v.isIntersecting) {
				const { img, key } = v.target.dataset;
				v.target.src = img;
				v.target.onload = () => {
					io.unobserve(v.target);
					arr[key]['loading'] = false;
				};
			}
		});
	});
	nextTick(() => {
		document.querySelectorAll(el).forEach((img) => io.observe(img));
	});
};

/**
 * 对象深克隆
 * @param obj 源对象
 * @returns 克隆后的对象
 */
export function deepClone(obj: EmptyObjectType) {
	let newObj: EmptyObjectType;
	try {
		newObj = obj.push ? [] : {};
	} catch (error) {
		newObj = {};
	}
	for (let attr in obj) {
		if (obj[attr] && typeof obj[attr] === 'object') {
			newObj[attr] = deepClone(obj[attr]);
		} else {
			newObj[attr] = obj[attr];
		}
	}
	return newObj;
}

/**
 * 判断是否是移动端
 */
export function isMobile() {
	if (
		navigator.userAgent.match(
			/('phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone')/i
		)
	) {
		return true;
	} else {
		return false;
	}
}

/**
 * 判断数组对象中所有属性是否为空，为空则删除当前行对象
 * @description @感谢大黄
 * @param list 数组对象
 * @returns 删除空值后的数组对象
 */
export function handleEmpty(list: EmptyArrayType) {
	const arr = [];
	for (const i in list) {
		const d = [];
		for (const j in list[i]) {
			d.push(list[i][j]);
		}
		const leng = d.filter((item) => item === '').length;
		if (leng !== d.length) {
			arr.push(list[i]);
		}
	}
	return arr;
}

/**
 * 打开外部链接
 * @param val 当前点击项菜单
 */
export function handleOpenLink(val: RouteItem) {
	const { origin, pathname } = window.location;
	router.push(val.path);
	if (verifyUrl(<string>val.meta?.isLink)) window.open(val.meta?.isLink);
	else window.open(`${origin}${pathname}#${val.meta?.isLink}`);
}

/**
 * 随机生成字符串
 * @param len 指定生成字符串长度
 */
export function randomString(e: number) {
	e = e || 32
	var t = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678',
		a = t.length,
		n = ''
	for (var i = 0; i < e; i++) n += t.charAt(Math.floor(Math.random() * a))
	return n
}
//是否在iframe中
export function isInIframe() {
  try {
    return window.self !== window.top;
  } catch (e) {
    return true;
  }
}
export function filterPhoneNumber(code: any) {
	let reg = /^1[3456789]{1}\d{9}$/;   //必须是以1开头，第二位必须是3-9中的任意一个数，后面9位必须是数字
	//校验手机号是否正确
	if (reg.test(code)) {
		code = code.toString();  //先强制转换成字符串类型
		return code.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');
	} else {
		return '';
	}
}
/**
 * 统一批量导出
 * @method useTitle 设置浏览器标题国际化
 * @method setTagsViewNameI18n 设置 自定义 tagsView 名称、 自定义 tagsView 名称国际化
 * @method lazyImg 图片懒加载
 * @method deepClone 对象深克隆
 * @method isMobile 判断是否是移动端
 * @method handleEmpty 判断数组对象中所有属性是否为空，为空则删除当前行对象
 * @method handleOpenLink 打开外部链接
 */
const other = {
	useTitle: () => {
		useTitle();
	},
	setTagsViewNameI18n(route: RouteToFrom) {
		return setTagsViewNameI18n(route);
	},
	lazyImg: (el: string, arr: EmptyArrayType) => {
		lazyImg(el, arr);
	},
	deepClone: (obj: EmptyObjectType) => {
		return deepClone(obj);
	},
	isMobile: () => {
		return isMobile();
	},
	handleEmpty: (list: EmptyArrayType) => {
		return handleEmpty(list);
	},
	handleOpenLink: (val: RouteItem) => {
		handleOpenLink(val);
	},
	randomString: (val: number) => {
		randomString(val);
	},
	filterPhoneNumber: (val: any) => {
		filterPhoneNumber(val);
	},

};

// 统一批量导出
export default other;
