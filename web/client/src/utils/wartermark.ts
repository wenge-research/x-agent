const fillTextWithWrap = (context, text, x, y, maxWidth) => {
	// 获取每个字符的宽度
	var charWidths = [];
	for (var i = 0; i < text.length; i++) {
		charWidths[i] = context.measureText(text[i]).width;
	}

	// 初始化当前行的文本和宽度
	var line = '';
	var lineWidth = 0;

	// 遍历每个字符
	for (var i = 0; i < text.length; i++) {
		// 如果加上当前字符后超过最大宽度，就先绘制当前行，然后换行
		if (lineWidth + charWidths[i] > maxWidth) {
			context.fillText(line, x, y);
			y += parseInt(context.font) + 4; // 根据字体大小增加y坐标
			line = ''; // 清空当前行
			lineWidth = 0; // 清空当前行宽度
		}
		// 将当前字符添加到当前行，并增加当前行宽度
		line += text[i];
		lineWidth += charWidths[i];
	}
	// 绘制最后一行
	context.fillText(line, x, y);
};

// 页面添加水印效果
const setWatermark = (str: string) => {
	const id = '1.23452384164.123412416';
	if (document.getElementById(id) !== null) document.getElementById('bg-main').removeChild(<HTMLElement>document.getElementById(id));
	const can = document.createElement('canvas');
	can.width = 300;
	can.height = 160;
	const cans = <CanvasRenderingContext2D>can.getContext('2d');
	cans.rotate((-20 * Math.PI) / 180);
	cans.font = '12px Misans-Regular,Microsoft YaHei';
	cans.fillStyle = 'rgba(24,27,73,.1)';
	cans.textBaseline = 'middle';
	fillTextWithWrap(cans, str, can.width / 10, can.height / 2, 140);
	// 自定义的函数，根据最大宽度来分割文本并换行绘制

	// cans.fillText(str, can.width / 10, can.height / 2);
	const div = document.createElement('div');
	div.id = id;
	div.style.pointerEvents = 'none';
	div.style.top = '0px';
	div.style.left = '0px';
	div.style.position = 'absolute';
	div.style.zIndex = '2';
	div.style.width = `${document.getElementById('bg-main').clientWidth}px`;
	div.style.height = `${document.getElementById('bg-main').clientHeight}px`;
	div.style.background = `url(${can.toDataURL('image/png')}) left top repeat`;
	document.getElementById('bg-main').appendChild(div);

	return id;
};

/**
 * 页面添加水印效果
 * @method set 设置水印
 * @method del 删除水印
 */
const watermark = {
	// 设置水印
	set: (str: string) => {
		let id = setWatermark(str);
		if (document.getElementById(id) === null) id = setWatermark(str);
	},
	// 删除水印
	del: () => {
		let id = '1.23452384164.123412416';
		if (document.getElementById(id) !== null) document.getElementById('bg-main').removeChild(<HTMLElement>document.getElementById(id));
	},
};

// 导出方法
export default watermark;
