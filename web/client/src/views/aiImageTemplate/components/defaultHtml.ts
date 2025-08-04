export const htnmJs = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>腾讯控股近十年财务数据可视化</title>
    <!-- 引入 ECharts 主库 -->
    <script src="https://cdn.staticfile.org/echarts/5.4.3/echarts.min.js"></script>
    <style>
        .chart-container {
            width: 100%;
            height: 600px;
            margin: 20px auto;
        }
        body {
            font-family: Arial, sans-serif;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
    </style>
</head>
<body>
    <h2 style="text-align: center;">腾讯控股2015-2024年核心财务数据</h2>

    <!-- 折线图容器 -->
    <div id="lineChart" class="chart-container"></div>

    <!-- 柱状图容器 -->
    <div id="barChart" class="chart-container"></div>

    <script>
        // 初始化折线图
        const lineChart = echarts.init(document.getElementById('lineChart'));
        lineChart.setOption({
            title: { text: '腾讯控股2015-2024年核心财务指标' },
            tooltip: { trigger: 'axis' },
            legend: { data: ['总收入', '归母净利润', '调整后净利润'] },
            xAxis: { 
                type: 'category',
                data: ['2015','2016','2017','2018','2019','2020','2021','2022','2023','2024'],
                axisLabel: { rotate: 45 }
            },
            yAxis: [{ type: 'value', name: '亿元' }],
            series: [
                { 
                    name: '总收入', 
                    type: 'line', 
                    data: [1028.63,1519.38,2377.60,3126.94,3772.89,4820.64,5545.50,5546.00,6090.15,6602.57],
                    itemStyle: { color: '#5470c6' }
                },
                { 
                    name: '归母净利润', 
                    type: 'line', 
                    data: [288,414.47,715,787.19,933.1,1598.5,1882.4,1156.5,1152.2,1941],
                    itemStyle: { color: '#91cc75' }
                },
                { 
                    name: '调整后净利润', 
                    type: 'line', 
                    data: [null,null,null,null,null,1227.5,1237.9,1576.9,1576.9,2227],
                    itemStyle: { color: '#fac858' }
                }
            ]
        });

        // 初始化柱状图
        const barChart = echarts.init(document.getElementById('barChart'));
        barChart.setOption({
            title: { text: '2024年收入结构（单位：亿元）' },
            tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
            legend: { data: ['金额', '占比'] },
            grid: { left: '3%', right: '10%', top: '15%', bottom: '10%' },
            xAxis: [{ 
                type: 'category',
                data: ['增值服务', '广告', '金融科技及企业服务'],
                axisTick: { alignWithLabel: true }
            }],
            yAxis: [
                { type: 'value', name: '金额', position: 'left' },
                { type: 'value', name: '占比', position: 'right', min: 0, max: 50, axisLabel: { formatter: '{value}%' } }
            ],
            series: [
                { 
                    name: '金额', 
                    type: 'bar', 
                    data: [3192.57,1214.0,2120.0],
                    itemStyle: { color: '#5470c6' }
                },
                { 
                    name: '占比', 
                    type: 'bar', 
                    data: [48.3,18.4,32.1],
                    yAxisIndex: 1,
                    itemStyle: { color: '#91cc75' }
                }
            ]
        });

        // 响应式布局
        window.addEventListener('resize', () => {
            lineChart.resize();
            barChart.resize();
        });
    </script>
</body>
</html>`