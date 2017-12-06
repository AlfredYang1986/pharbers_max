/**
 * Created by yym on 11/27/17.
 */
"use strict";
(function ($, w) {

    bar_line_chart("market_trend");
    map_chart("market_map");
    bar_chart("market_bar");
    function bar_line_chart(id) {
        var barLineChart = echarts.init(document.getElementById(id));
        var option = {
            title: {
            },
            tooltip: {
                trigger: 'axis'
            },
            // toolbox: {
            //     feature: {
            //         dataView: {
            //             show: true,
            //             readOnly: false
            //         },
            //         restore: {
            //             show: true
            //         },
            //         saveAsImage: {
            //             show: true
            //         }
            //     }
            // },
            grid: {
                containLabel: true
            },
            legend: {
                data: ['市场销量', '份额变化趋势']
            },
            xAxis: [{
                type: 'category',
                axisTick: {
                    alignWithLabel: true
                },
                data: ['2000', '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016']
            }],
            yAxis: [{
                type: 'value',
                name: '增速',
                min: 0,
                max: 50,
                position: 'right',
                axisLabel: {
                    formatter: '{value} %'
                }
            }, {
                type: 'value',
                name: '销量',
                min: 0,
                max: 3000,
                position: 'left'
            }],
            series: [{
                name: '增速',
                type: 'line',
                stack: '总量',
                label: {
                    normal: {
                        show: true,
                        position: 'top',
                    }
                },
                lineStyle: {
                    normal: {
                        width: 3,
                        shadowColor: 'rgba(0,0,0,0.4)',
                        shadowBlur: 10,
                        shadowOffsetY: 10
                    }
                },
                data: [1, 13, 37, 35, 15, 13, 25, 21, 6, 45, 32, 2, 4, 13, 6, 4, 11]
            }, {
                name: '销量',
                type: 'bar',
                yAxisIndex: 1,
                stack: '总量',
                label: {
                    normal: {
                        show: true,
                        position: 'top'
                    }
                },
                data: [209, 236, 325, 439, 507, 576, 722, 879, 938, 1364, 1806, 1851, 1931, 2198, 2349, 2460, 2735]
            }]
        };
        barLineChart.setOption(option);
        $(window).resize(function () {
            barLineChart.resize();
        })
    }

    function map_chart(id) {
        var map = echarts.init(document.getElementById(id));
        function randomData() {
            return Math.round(Math.random()*2500);
        }

        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}'
            },
            visualMap: {//视觉映射组件
                type : "piecewise",
                splitNumber : 5,
                seriesIndex: 0,
                pieces : [
                    {gt: 2000, color : '#60B3AD'},            // (1500, Infinity]
                    {gt: 1500, lte: 2000, color : '#80CDC8'},  // (900, 1500]
                    {gt: 1000, lte: 1500, color : '#9DE0DC'},  // (310, 1000]
                    {gt: 500, lte: 1000, color : '#D2F5F2'},
                    {lt :500 ,color : '#D7D7D7'}
                ],
                min: 0,
                max: 2500,
                left: 'left',
                top: 'bottom',
                text: ['高','低'],           // 文本，默认为数值文本
                calculable: true
            },
            xAxis: {
                type: 'category',
                data: [],
                splitNumber: 1,
                show: false
            },
            yAxis: {
                position: 'right',
                min: 0,
                max: 20,
                splitNumber: 20,
                inverse: true,
                axisLabel: {
                    show: true
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                data: []
            },
            series: [
                {
                    zlevel: 1,
                    name: '中国',
                    type: 'map',
                    mapType: 'china',
                    // selectedMode : 'multiple',
                    roam: false,//不进行缩放
                    left: 0,
                    right: '15%',
                    label: {
                        normal: {
                            show: true
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    data:[
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
                }
            ]
        };
        map.setOption(option);
        $(window).resize(function () {
            map.resize();
        });

        /**
         * 根据值获取线性渐变颜色
         * @param  {String} start 起始颜色
         * @param  {String} end   结束颜色
         * @param  {Number} max   最多分成多少分
         * @param  {Number} val   渐变取值
         * @return {String}       颜色
         */
        function getGradientColor (start, end, max, val) {
            var rgb = /#((?:[0-9]|[a-fA-F]){2})((?:[0-9]|[a-fA-F]){2})((?:[0-9]|[a-fA-F]){2})/;
            var sM = start.match(rgb);
            var eM = end.match(rgb);
            var err = '';
            max = max || 1
            val = val || 0
            if (sM === null) {
                err = 'start';
            }
            if (eM === null) {
                err = 'end';
            }
            if (err.length > 0) {
                throw new Error('Invalid ' + err + ' color format, required hex color');
            }
            var sR = parseInt(sM[1], 16),
                sG = parseInt(sM[2], 16),
                sB = parseInt(sM[3], 16);
            var eR = parseInt(eM[1], 16),
                eG = parseInt(eM[2], 16),
                eB = parseInt(eM[3], 16);
            var p = val / max;
            var gR = Math.round(sR + (eR - sR) * p).toString(16),
                gG = Math.round(sG + (eG - sG) * p).toString(16),
                gB = Math.round(sB + (eB - sB) * p).toString(16);
            return '#' + gR + gG + gB;
        }

        /*setTimeout(function() {
            var TOPN = 25

            var option = map.getOption()
            // 修改top
            option.grid[0].height = TOPN * 20
            option.yAxis[0].max = TOPN
            option.yAxis[0].splitNumber = TOPN
            option.series[1].data[0] = TOPN
            // 排序
            var data = option.series[0].data.sort(function(a, b) {
                return b.value - a.value
            })

            var maxValue = data[0].value,
                minValue = data.length > TOPN ? data[TOPN - 1].value : data[data.length - 1].value

            var s = option.visualMap[0].controller.inRange.color[0],
                e = option.visualMap[0].controller.inRange.color.slice(-1)[0]
            var sColor = getGradientColor(s, e, maxValue, minValue)
            var eColor = getGradientColor(s, e, maxValue, maxValue)

            option.series[1].itemStyle.normal.color = new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 1,
                color: sColor
            }, {
                offset: 0,
                color: eColor
            }])

            // yAxis
            var newYAxisArr = []
            echarts.util.each(data, function(item, i) {
                if (i >= TOPN) {
                    return false
                }
                var c = getGradientColor(sColor, eColor, maxValue, item.value)
                newYAxisArr.push({
                    value: item.name,
                    textStyle: {
                        color: c
                    }
                })
            })
            option.yAxis[0].data = newYAxisArr
            option.yAxis[0].axisLabel.formatter = (function(data) {
                return function(value, i) {
                    if (!value) return ''
                    return value + ' ' + data[i].value
                }
            })(data)
            map.setOption(option)
        }, 0);*/

    }

    function bar_chart(id) {
        var barChart = echarts.init(document.getElementById(id));
        var option = {
            // title: {
            //     text: '需求驳回率排名',
            // },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['市场总销售额', '产品销售额'],
                bottom:'10px',
                right:'10px',
                orient:'vertical'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                boundaryGap: [0, 0.01]
            },
            yAxis: {
                type: 'category',
                data: ['01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15', '01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15'],
                axisLabel: {
                    show: true,
                    interval: 'auto',
                    formatter: '{value}%',
                },
            },
            series: [
                {
                    name: '市场总销售额',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color : '#ADADAD',//柱状图颜色
                            label: {
                                textStyle: {
                                    color: '#000000',
                                    fontSize: 3
                                },
                                show: true,
                                position: 'right',
                                formatter: '{c}%',


                            }
                        }
                    },
                    data: [8, 13, 15, 17, 22, 24, 28, 31, 35, 37],

                },
                {
                    name: '产品销售额',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: '#60B3AD',
                            label: {
                                textStyle: {

                                    color: '#000000',
                                    fontSize: 3
                                },
                                show: true,
                                position: 'right',
                                formatter: '{c}%',


                            }
                        }
                    },
                    data: [9, 21, 13, 22, 13, 16, 13, 15, 17, 22]
                }
            ]
        }
        barChart.setOption(option);
        $(window).resize(function () {
            barChart.resize();
        });

    }

}(jQuery))