// 从 storage 模块导入 Session 工具
import { Session } from '/@/utils/storage';
import { Modal } from 'winbox-ui-next';
import md5 from 'js-md5';
// 从 Microsoft 库导入 fetchEventSource 函数
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useUserInfo } from '/@/stores/userInfo';
import { ElMessage } from 'element-plus';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
const { isMobile } = useBasicLayout();
export default async function useEventSource(url: string, params = {}, abortController: any, onmessage?, onclose?, onerror?, headerParams={}) {
	let timer = new Date().getTime();
	// 使用给定的 URL 和选项创建一个 EventSource 对
	let now = new Date();
  let hasMessage = false
	params.clientType = isMobile.value ? 'H5' : 'PC';
	let clientType = isMobile.value ? 'H5' : 'PC';
	const source = await fetchEventSource(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			timestamp: timer,
			cipher: md5(timer + JSON.stringify(params) + '/dialogue/v3/dialogueByStreamxxxxxxx='),
			// Authorization: sessionStorage.getItem('wxAccessToken') ? `Bearer ${sessionStorage.getItem('wxAccessToken')}` : '', 
			Authorization: sessionStorage.getItem('manageAccessToken') ? `Bearer ${sessionStorage.getItem('manageAccessToken')}` : '',// 使用 Session 存储中的 token 进行授权
			'login-plat': `${sessionStorage.getItem('loginPlat')}`, // 使用 Session 存储中的 token 进行授权
			'clientType': clientType, 
			...headerParams
		},
		body: JSON.stringify(params),
		// 使用 abort signal 在需要时取消请求
		signal: abortController.signal,
		// 即使页面隐藏也打开连接
		openWhenHidden: true,
		// 定义不同事件的回调函数
		onopen(event) {
			const stores = useUserInfo();
			if (event.status == 401) {
				stores.userInfos.isLoginExpired = true;
			}
			// 创建回调
		},
		onmessage(event) {
			const data = JSON.parse(event.data);
      hasMessage = true
			if (data?.code == '000024') {
				Modal.open({
					title: '登录超时',
					content: '请重新登录',
					closable: false,
					okText: '确定',
					cancelText: '取消',
					onOk: () => {
						sessionStorage.removeItem('manageAccessToken');
						localStorage.removeItem('userInfo');
						// window.location.href = sessionStorage.getItem('clientLink');
					},
					onCancel: () => {
						sessionStorage.removeItem('ModalFlag');
					},
				});
				return;
			}
			// 消息回调
      try {
        onmessage?.(event.data);
      } catch (err) {
        console.error('数据处理失败:', err);
        source?.close();
      }
		},
		onclose() {
			const oncloseDate = new Date();
			if (oncloseDate.getTime() - now.getTime() < 1000 && !hasMessage) {
				ElMessage({
					message: '系统繁忙，请稍等',
					type: 'warning',
					duration: 2000,
				});
			}
			// 结束回调
			onclose?.();
			// 当发生错误时，中止请求
      source?.close();
			abortController.abort();
		},
		onerror(e) {
			// 失败回调
			onerror?.();
			// 当发生错误时，中止请求
      source?.close();
			abortController.abort();
		},
	});

	return {
		close: () => {
			abortController.abort();
			source?.close();
		},
	};
}
