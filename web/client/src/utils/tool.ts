//补位，小于10时，前补0
export const padStart = (val) => {
	if (!val) {
		return "00";
	}
	return val.toString().padStart(2, "0");
};

/**
 * 格式化日期时间
 */
export const FormatDateYear = (val) => {
	if (!val) {
		return "";
	}
	let date = new Date(val);
	let y = date.getFullYear();
	let m = date.getMonth() + 1;
	let d = date.getDate();
	// let h = date.getHours();
	// let mm = date.getMinutes();
	// let s = date.getSeconds();
	let str =
		[y, m, d].map((item) => padStart(item)).join("-")
	return str;
};

export const FormatDateYYMM = (val) => {
	if (!val) {
		return "";
	}
	let date = new Date(val);
	let y = date.getFullYear();
	let m = date.getMonth() + 1;
	// let d = date.getDate();
	// let h = date.getHours();
	// let mm = date.getMinutes();
	// let s = date.getSeconds();
	let str =
		[y, m].map((item) => padStart(item)).join("-")
	return str;
};

/**
 * 格式化日期时间
 */
export const FormatDate = (val) => {
	if (!val) {
		return "";
	}
	let date = new Date(val);
	let y = date.getFullYear();
	let m = date.getMonth() + 1;
	let d = date.getDate();
	let h = date.getHours();
	let mm = date.getMinutes();
	let s = date.getSeconds();
	let str =
		[y, m, d].map((item) => padStart(item)).join("-") +
		" " +
		[h, mm, s].map((item) => padStart(item)).join(":");
	return str;
};

// 从00：00：00开始
export const FormatDateNew = (val) => {
	if (!val) {
		return "";
	}
	let date = new Date(val);
	let y = date.getFullYear();
	let m = date.getMonth() + 1;
	let d = date.getDate();
	date.setHours(0);
	// 将分钟数设置为59
	date.setMinutes(0);
	// 将秒数设置为59
	date.setSeconds(0);
	let h = date.getHours();
	let mm = date.getMinutes();
	let s = date.getSeconds();
	let str =
		[y, m, d].map((item) => padStart(item)).join("-") +
		" " +
		[h, mm, s].map((item) => padStart(item)).join(":");
	return str;
};

/**
 * 格式化日期时间
 */
export const FormatDateLast = (val) => {
	if (!val) {
		return "";
	}
	let date = new Date(val);
	let y = date.getFullYear();
	let m = date.getMonth() + 1;
	let d = date.getDate();
	date.setHours(23);
	// 将分钟数设置为59
	date.setMinutes(59);
	// 将秒数设置为59
	date.setSeconds(59);
	let h = date.getHours();
	let mm = date.getMinutes();
	let s = date.getSeconds();
	let str =
		[y, m, d].map((item) => padStart(item)).join("-") +
		" " +
		[h, mm, s].map((item) => padStart(item)).join(":");
	return str;
};

export const getDateTime = (data) => {
	data && data.forEach((item) => {
		if (item.hasHour > 24) {
			item.hourDay = `剩余${parseInt(item.hasHour / 24)}天${
				item.hasHour % 24
			}小时`;
		} else if (item.hasHour == 24) {
			item.hourDay = `剩余${item.hasHour}小时`;
		} else if (item.hasHour < 24 && item.hasHour > 0) {
			item.hourDay = `剩余${item.hasHour}小时`;
		} else {
			item.hourDay = "超期";
		}
	});
	return data;
};

export const singleGetDateTime = (data) => {
	let hourDay = ''
	if (data.hasHour > 24) {
		hourDay = `剩余${parseInt(data.hasHour / 24)}天${
			data.hasHour % 24
		}小时`;
	} else if (data.hasHour == 24) {
		hourDay = `剩余${data.hasHour}小时`;
	} else if (data.hasHour < 24 && data.hasHour > 0) {
		hourDay = `剩余${data.hasHour}小时`;
	} else {
		hourDay = "超期";
	}
	return hourDay
};

export const dateToArray = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // getMonth() 返回的月份是从 0 开始的
  const day = date.getDate();
  return [year.toString(), month.toString().padStart(2, '0'), day.toString().padStart(2, '0')];
}


export const filterChildrenByLabel = (arr, label) =>{
  let result = [];
  function recursiveFilter(nodes) {
    nodes && nodes.forEach(node => {
      if (node.label === label) {
        if (node.children) {
          result = result.concat(node.children);
        }
      } else if (node.children) {
        recursiveFilter(node.children);
      }
    });
  }
  recursiveFilter(arr);
  return result;
}
/**
 * 判断字符串是否可以解析为 JSON 对象
 * @param {string} text - 需要判断的字符串
 * @returns {boolean} - 是否可以解析为对象
 */
export const isJsonString = (text) =>{
  if (typeof text !== 'string') {
    return false; // 如果不是字符串，直接返回 false
  }

  try {
    const result = JSON.parse(text); // 尝试解析
    return typeof result === 'object' && result !== null; // 确保解析结果是对象且不为 null
  } catch (error) {
    return false; // 如果解析失败，返回 false
  }
}
export const getUrlParamValue = (key:string) => {
	// 获取参数
	const url = window.location.href;
	const index = url.indexOf("?");
	const params = url.substring(index + 1);
	const paramsArr = params.split("&");
	let paramsObj = {};
	paramsArr.forEach(item => {
	// item 存在多个 = 号 , 只截取第一
	if(item.indexOf(key) > -1 ){
		const arr = item.split(`${key}=`)
		paramsObj[key] = arr[1]
	}
	});
	return paramsObj[key] ? paramsObj[key] : null;
}
export function isWechatBrowser() {
  const ua = navigator.userAgent.toLowerCase()
  return /micromessenger/i.test(ua)
}