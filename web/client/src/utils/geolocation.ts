

/**
 * 定义一些常量
 */
// eslint-disable-next-line @typescript-eslint/no-loss-of-precision
const PI = 3.1415926535897932384626;
const a = 6378245.0; // 长半轴
// eslint-disable-next-line @typescript-eslint/no-loss-of-precision
const ee = 0.00669342162296594323; // 扁率


/**
 * WGS84 转换为 GCJ02
 * @param lng
 * @param lat
 * @returns {*}
 */
export function wgs84togcj02(lng, lat) {
	if (out_of_china(lng, lat)) {
		return {
			lng,
			lat,
		};
	}
		let dlat = transformlat(lng - 105.0, lat - 35.0);
		let dlng = transformlng(lng - 105.0, lat - 35.0);
		const radlat = (lat / 180.0) * PI;
		let magic = Math.sin(radlat);
		magic = 1 - ee * magic * magic;
		const sqrtmagic = Math.sqrt(magic);
		dlat = (dlat * 180.0) / (((a * (1 - ee)) / (magic * sqrtmagic)) * PI);
		dlng = (dlng * 180.0) / ((a / sqrtmagic) * Math.cos(radlat) * PI);
		const mglat = lat + dlat;
		const mglng = lng + dlng;
		return {
			lng: mglng,
			lat: mglat,
		};

}

/**
 * 判断是否在国内，不在国内则不做偏移
 * @param lng
 * @param lat
 * @returns {boolean}
 */
function out_of_china(lng, lat) {
	// eslint-disable-next-line no-param-reassign
	lat = Number(lat);
	// eslint-disable-next-line no-param-reassign
	lng = Number(lng);
	// return !(lng > 73.66 && lng < 135.05 && lat > 3.86 && lat < 53.55)
	return (
		lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271 || false
	);
}

function transformlat(lng, lat) {
	// eslint-disable-next-line no-param-reassign
	lat = Number(lat);
	// eslint-disable-next-line no-param-reassign
	lng = Number(lng);
	let ret =
		-100.0 +
		2.0 * lng +
		3.0 * lat +
		0.2 * lat * lat +
		0.1 * lng * lat +
		0.2 * Math.sqrt(Math.abs(lng));
	ret +=
		((20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) *
			2.0) /
		3.0;
	ret +=
		((20.0 * Math.sin(lat * PI) + 40.0 * Math.sin((lat / 3.0) * PI)) * 2.0) /
		3.0;
	ret +=
		((160.0 * Math.sin((lat / 12.0) * PI) + 320 * Math.sin((lat * PI) / 30.0)) *
			2.0) /
		3.0;
	return ret;
}

function transformlng(lng, lat) {
	// eslint-disable-next-line no-param-reassign
	lat = Number(lat);
	// eslint-disable-next-line no-param-reassign
	lng = Number(lng);
	let ret =
		300.0 +
		lng +
		2.0 * lat +
		0.1 * lng * lng +
		0.1 * lng * lat +
		0.1 * Math.sqrt(Math.abs(lng));
	ret +=
		((20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) *
			2.0) /
		3.0;
	ret +=
		((20.0 * Math.sin(lng * PI) + 40.0 * Math.sin((lng / 3.0) * PI)) * 2.0) /
		3.0;
	ret +=
		((150.0 * Math.sin((lng / 12.0) * PI) +
			300.0 * Math.sin((lng / 30.0) * PI)) *
			2.0) /
		3.0;
	return ret;
}

/*
 *lng1,lat1 第一个经纬度
 *lng2，lat2 第二个经纬度
 */

 export function calculateDiscount(lng1, lat1, lng2, lat2) {
	const radLat1 = (lat1 * Math.PI) / 180.0;
	const radLat2 = (lat2 * Math.PI) / 180.0;
	const a = radLat1 - radLat2;
	const b = (lng1 * Math.PI) / 180.0 - (lng2 * Math.PI) / 180.0;
	let s =
		2 *
		Math.asin(
			Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) +
					Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
			)
		);
	s = s * 6378.137; // EARTH_RADIUS;
	s = Math.round(s * 10000) / 10000;
	s = s * 1000;
	if (isNaN(s)) {
		return 0 + "m";
	}
	// if (s > 100000) {
	// 	// 大于100Km时
	// 	s = Math.floor((s / 1000) * 100) / 100;
	// 	return s.toFixed() + "km";
	// }
	// if (s > 1000 && s < 100000) {
	// 	// 大于1000米时
	// 	s = Math.floor((s / 1000) * 100) / 100;
	// 	return s.toFixed(1) + "km";
	// }
	// 小于1000米直接返回
	return s.toFixed();
}