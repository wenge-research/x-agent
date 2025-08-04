// 通用函数
import mammoth from 'mammoth';
import FileSaver from 'file-saver';
import md5 from 'js-md5';
import { asBlob } from 'html-docx-js-typescript';
import { useAIWriteApi } from '@/api/aiWrite/index.ts';

// console.log(22222,htmlDocx)
export const parseString2 = (str: any) => {
	let right: any = null;
	let parent: any = null;
	let carry: any = 0;

	let key = '';
	let value = '';

	for (let i = 0; i < str.length; i++) {
		const letter = str[i];

		if (letter === '[') {
			if (right) {
				// 按照下面的模式，你可以自行补齐这里
			} else {
				right = ']';
				parent = new Array();
			}
		} else if (letter === '{') {
			if (right) {
				value = '';
				for (let j = i; j < str.length; j++) {
					value += str[j];
					if (str[j] === '}') {
						i = j;
						// 这里的进位是为了防止在遇到逗号时将空白字符计算进去
						carry = 1;
						break;
					}
				}
				if (right === ']') {
					parent.push(parseString2(value));
					value = '';
				} else if (right === '}') {
					parent[key] = parseString2(value);
					key = '';
					value = '';
				}
			} else {
				right = '}';
				parent = new Object();
			}
		} else if (letter === ']') {
			if (right === letter && value) {
				parent.push(value);
				value = '';
				right = null;
			}
		} else if (letter === '}') {
			if (right === letter && key && value) {
				parent[key] = value;
				key = '';
				value = '';
				right = null;
			}
		} else if (letter === ',') {
			if (carry === 0) {
				if (right === ']') {
					parent.push(value);
					value = '';
				} else if (right === '}') {
					parent[key] = value;
					key = '';
					value = '';
				}
			} else {
				carry--;
			}
		} else if (letter === ':') {
			key = value;
			value = '';
		} else {
			// non empty
			if (letter && letter !== ' ') {
				value += letter;
			}
		}
	}
	return parent;
};

export const getBase64 = (url: string, callback: Function) => {
	let Img = new Image(),
		dataURL = '';
	Img.src = url + '?v=' + Math.random();
	Img.setAttribute('crossOrigin', 'Anonymous');
	Img.onload = function () {
		let canvas = document.createElement('canvas'),
			width = Img.width,
			height = Img.height;
		canvas.width = width;
		canvas.height = height;
		canvas.getContext('2d')?.drawImage(Img, 0, 0, width, height);
		dataURL = canvas.toDataURL('image/jpeg', 1); //可选取多种模式
		return callback ? callback(dataURL) : null; //回掉函数获取Base64编码
	};
};
const imgList: any = ['jpg', 'jpeg', 'png', 'svg', 'SVG', 'JPG', 'JPEG', 'PNG'];
export const changeImgType = (url: any) => {
	let suffix: any = url.substring(url.lastIndexOf('.') + 1);
	return imgList.includes(suffix);
};
// 调用
// let imgUrl=‘https://www.baidu.com/img/bd_logo1.png’
//  this.getBase64(imgUrl, dataURL => {
//  console.log(dataURL:就是base64了)
// });
// 得到文件后缀
export const getSuffix = (fileName: string) => {
	let suffix = fileName.split('.')[fileName.split('.').length - 1];
	return suffix;
};
//file 单位转换
export const formatbytes  = (val:any) => {
	if (val == "0") return "0B";
	var k = 1024;
	var sizes = ["B", "KB", "MB", "GB", "TB"];
	let i = Math.floor(Math.log(val) / Math.log(k));//得出该数字的单位应该是kB?MB
	return (val / Math.pow(k, i)).toPrecision(3) + "" + sizes[i];
}


/** 请求加密 */
export const requestEncryption = (config, timer) => {
	
  // 网关列表
  const gatewayList = [`${import.meta.env.VITE_BASE_API_URL}`, '/industrial-base-service', '/industrial-data-service', '/sz-industrial-chain', '/industrial-show-service',
   '/sz-industrial-base', '/tbase-iam-manage','/tbase-iam-service'];
  let params = '';
  if (config.params) {
    for (const key in config.params) {
      if (config.params[key]) params += `${key}=${config.params[key]}&`;
    }
  }
  const data = config.data ? JSON.stringify(config.data) : '';
  let url = config.url ? config.url : '';
  // 加密时需要去除网关
  gatewayList?.forEach(item => {
    if (url.indexOf(item) !== -1 && item !== '/') {
      url = url.replace(item, '');
    }
  });

  if (url.indexOf('?') !== -1) {
    const index = url.indexOf('?');
    const result = url.substring(index + 1, url.length);
    url = url.substring(0, index);
    params = result;
    /** 加密地址 */
    let gatewayUrl = `${data + params + url}xxxxxxx=`;
    // 如果为导入接口，需要去除文件标识
    if (gatewayUrl.indexOf('{}') !== -1 && config?.headers.fileFlag) gatewayUrl = gatewayUrl.replace('{}', '');
		// config.headers.cipher2 = timer + gatewayUrl;
    config.headers.cipher = md5(timer + gatewayUrl);
  } else {
    let gatewayUrl = `${data + params.slice(0, params.length - 1) + url}xxxxxxx=`;
    // 如果为导入接口，需要去除文件标识
    if (gatewayUrl.indexOf('{}') !== -1 && config?.headers.fileFlag) gatewayUrl = gatewayUrl.replace('{}', '');

    // config.headers.cipher2 = timer + gatewayUrl;
    config.headers.cipher = md5(timer + gatewayUrl);
  }
  return config;
}
/**
 * 获取指定名称的Cookie值
 * @param {string} name - 要获取的Cookie名称
 * @returns {string|null} - 返回Cookie值，如果不存在则返回null
 */
  export const getCookie = (name) => {
  // 将cookie字符串分割成数组
  const cookies = document.cookie.split(';');
  
  // 遍历所有cookie
  for (let i = 0; i < cookies.length; i++) {
    let cookie = cookies[i];
    // 去除前后空格
    cookie = cookie.trim();
    
    // 检查cookie是否以目标名称开头
    if (cookie.startsWith(name + '=')) {
      // 返回cookie值
      return cookie.substring(name.length + 1);
    }
  }
  
  // 如果没找到，返回null
  return null;
}