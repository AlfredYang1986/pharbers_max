(function($, w) {
    "use strict";
    var f = new Facade();
    var itemStyleColor = ['#3AD1C2', '#60C6CF', '#FFFFFF', '#009992'];
    var barLineChart;
    var map;
    $(function() {
        bar_line_chart('market-trend');
        map_chart("market-map");
        // f.ajaxModule.baseCall('', {}, 'POST', function(r) {
        //     console.info(r)
        // });

        $(w).resize(function () {
            barLineChart.resize();
            map.resize();
        })
    });

    function bar_line_chart(id) {
        barLineChart = echarts.init(document.getElementById(id));
        var option = {
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            tooltip: {
                trigger: 'axis',
                textStyle: {
                    align: 'left'
                },
                axisPointer: {
                    type: 'shadow'
                }
            },
            xAxis: [
                {
                    name: '日期',
                    nameGap: 40,
                    type: 'category',
                    data: ['2000', '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016'],
                    splitLine: {
                        show:false
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '市场销量(百万)',
                    // max: 10000,
                    position: "left"
                },
                {
                    type: 'value',
                    name: '份额变化趋势',
                    show: true,
                    position: 'right',
                    axisLabel: {
                        formatter: '{value} %'
                    },
                    splitLine: {
                        show:false
                    }
                }
            ],
            series: [
                {
                    name:'Market Sales',
                    type:'bar',
                    barWidth: '80%',
                    yAxisIndex: 0,
                    data: [209, 236, 325, 439, 507, 576, 722, 879, 938, 1364, 1806, 1851, 1931, 2198, 2349, 2460, 2735],
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: itemStyleColor[0]
                        }
                    }
                },
                {
                    name:'MAX Mono Share',
                    type:'line',
                    yAxisIndex: 1,
                    data: [1, 13, 37, 35, 15, 13, 25, 21, 6, 45, 32, 2, 4, 13, 6, 4, 10],
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    lineStyle: {
                        normal: {
                            color: itemStyleColor[1],
                            width: 3,
                            shadowBlur: 10,
                            shadowOffsetY: 10
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: itemStyleColor[3]
                        }
                    }
                }
            ]
        };
        barLineChart.setOption(option);
    }

    function map_chart(id) {
        map = echarts.init(document.getElementById(id));
        function randomData() {
            return Math.round(Math.random()*2500);
        }

        var option = {
            title: {
                // text: '中国大区分布图',
                // subtext: '中国的八大区分布',
                // sublink: '#',

                itemGap: 30,

                left: 'center',
                textStyle: {
                    color: '#1a1b4e',
                    fontStyle: 'normal',
                    fontWeight: 'bold',
                    fontSize: 30

                },
                subtextStyle: {
                    color: '#58d9df',
                    fontStyle: 'normal',
                    fontWeight: 'bold',
                    fontSize: 16
                }
            },
            tooltip: {
                trigger: 'item'
            },
            visualMap: {
                type : "piecewise",
                pieces : [
                    {gt: 2000, color : '#60B3AD'},            // (1500, Infinity]
                    {gt: 1500, lte: 2000, color : '#80CDC8'},  // (900, 1500]
                    {gt: 1000, lte: 1500, color : '#9DE0DC'},  // (310, 1000]
                    {gt: 500, lte: 1000, color : '#D2F5F2'},
                    {lt :500 ,color : '#D7D7D7'}
                ],
                splitNumber : 5,
                seriesIndex: 0,
                min: 0,
                max: 3500,
                left: 'left',
                top: 'bottom',
                text: ['高', '低'],
                calculable: true
            },
            series: [{
                name: '中国',
                type: 'map',
                zoom: 1.2,
                mapType: 'china',
                roam: false,//不进行缩放
                // left: '0',
                // right: '0',
                label: {
                    normal: {
                        show: true
                    }
                },
                data: [
                    {name: '北京',value: randomData() },
                    {name: '天津',value: randomData() },
                    {name: '上海',value: randomData() },
                    {name: '重庆',value: randomData() },
                    {name: '河北',value: randomData() },
                    {name: '河南',value: randomData() },
                    {name: '云南',value: randomData() },
                    {name: '辽宁',value: randomData() },
                    {name: '黑龙江',value: randomData() },
                    {name: '湖南',value: randomData() },
                    {name: '安徽',value: randomData() },
                    {name: '山东',value: randomData() },
                    {name: '新疆',value: randomData() },
                    {name: '江苏',value: randomData() },
                    {name: '浙江',value: randomData() },
                    {name: '江西',value: randomData() },
                    {name: '湖北',value: randomData() },
                    {name: '广西',value: randomData() },
                    {name: '甘肃',value: randomData() },
                    {name: '山西',value: randomData() },
                    {name: '内蒙古',value: randomData() },
                    {name: '陕西',value: randomData() },
                    {name: '吉林',value: randomData() },
                    {name: '福建',value: randomData() },
                    {name: '贵州',value: randomData() },
                    {name: '广东',value: randomData() },
                    {name: '青海',value: randomData() },
                    {name: '西藏',value: randomData() },
                    {name: '四川',value: randomData() },
                    {name: '宁夏',value: randomData() },
                    {name: '海南',value: randomData() },
                    {name: '台湾',value: randomData() },
                    {name: '香港',value: randomData() },
                    {name: '澳门',value: randomData() }
                ]
            }]
        };


        map.setOption(option);

    }


})(jQuery, window);