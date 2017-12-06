/**
 * Created by yym on 11/22/17.
 */

(function ($, w) {
    layui.use('laypage', function () {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'hosPage'
            , count: 50 //数据总数，从服务端得到
        });
    });
    var loadMainChart = function (v, id, title) {
        var mainChart = echarts.init(document.getElementById(id));
        var option = {
            "title": {
                "text": title,
                "top": '85%',
                "left": 'center',
                "textStyle": {
                    "fontSize": 14,
                    "fontWeight": "normal",
                    "color": "#bcbfff"
                }
            },
            "tooltip": {
                "trigger": 'item',
                "formatter": "{a} : ({d}%)"
            },
            "series": [
                {
                    "name": "内圈",
                    "center": [
                        "50%",
                        "40%"
                    ],
                    "radius": [
                        "49%",
                        "50%"
                    ],
                    "clockWise": false,
                    "hoverAnimation": false,
                    "type": "pie",
                    "data": [{
                        "value": v,
                        "name": "",
                        "label": {
                            "normal": {
                                "show": true,
                                "formatter": '{d} %',
                                "textStyle": {
                                    "fontSize": 20,
                                    "fontWeight": "normal"
                                },
                                "position": "center"
                            }
                        },
                        "labelLine": {
                            "show": false
                        },
                        "itemStyle": {
                            "normal": {
                                "color": "#5886f0",
                                "borderColor": new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#00a2ff'
                                }, {
                                    offset: 1,
                                    color: '#70ffac'
                                }]),
                                "borderWidth": 10
                            },
                            "emphasis": {
                                "color": "#5886f0",
                                "borderColor": new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#85b6b2'
                                }, {
                                    offset: 1,
                                    color: '#6d4f8d'
                                }]),
                                "borderWidth": 14
                            }
                        },
                    }, {
                        "name": " ",
                        "value": 100 - v,
                        "itemStyle": {
                            "normal": {
                                "label": {
                                    "show": false
                                },
                                "labelLine": {
                                    "show": false
                                },
                                "color": 'rgba(0,0,0,0)',
                                "borderColor": 'rgba(0,0,0,0)',
                                "borderWidth": 0
                            },
                            "emphasis": {
                                "color": 'rgba(0,0,0,0)',
                                "borderColor": 'rgba(0,0,0,0)',
                                "borderWidth": 0
                            }
                        }
                    }]
                },
                {
                    "name": "外圈",
                    "center": [
                        "50%",
                        "40%"
                    ],
                    "radius": [
                        "59%",
                        "60%"
                    ],
                    "clockWise": false,
                    "hoverAnimation": false,
                    "type": "pie",
                    "data": [{
                        "value": 100,
                        "name": "",
                        "label": {
                            "normal": {
                                "show": false,
                                "formatter": '{d} %',
                                "textStyle": {
//                                "fontSize": 28,
                                    "fontWeight": "normal"
                                },
                                "position": "center"
                            }
                        },
                        "labelLine": {
                            "show": false
                        },
                        "itemStyle": {
                            "normal": {
                                "color": "#5886f0",
                                "borderColor": new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#00a2ff'
                                }, {
                                    offset: 1,
                                    color: '#70ffac'
                                }]),
                                "borderWidth": 1
                            },
                            "emphasis": {
                                "color": "#5886f0",
                                "borderColor": new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#85b6b2'
                                }, {
                                    offset: 1,
                                    color: '#6d4f8d'
                                }]),
                                "borderWidth": 1
                            }
                        },
                    }, {
                        "name": " ",
                        "value": 0,
                        "itemStyle": {
                            "normal": {
                                "label": {
                                    "show": false
                                },
                                "labelLine": {
                                    "show": false
                                },
                                "color": 'rgba(0,0,0,0)',
                                "borderColor": 'rgba(0,0,0,0)',
                                "borderWidth": 0
                            },
                            "emphasis": {
                                "color": 'rgba(0,0,0,0)',
                                "borderColor": 'rgba(0,0,0,0)',
                                "borderWidth": 0
                            }
                        }
                    }]
                }]
        };
        mainChart.setOption(option);
    }

    var sample_bar = function (id) {
        var bar = echarts.init(document.getElementById(id));
        var option = {
            color: ['#3398DB'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis: [
                {
                    // type : 'category',
                    // data : ['10','20','30','40'],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            series: [
                {
                    name: '直接访问',
                    type: 'bar',
                    barWidth: '60%',
                    data: [1, 3, 2, 3, 4, 2, 1, 3, 3, 2, 3, 2]
                },

            ],
            label: {
                normal: {
                    show: true,
                    position: 'top',
                    formatter: '{c}'
                }
            },
            itemStyle: {
                normal: {

                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: 'rgba(17, 168,171, 1)'
                    }, {
                        offset: 1,
                        color: 'rgba(17, 168,171, 0.1)'
                    }]),
                    shadowColor: 'rgba(0, 0, 0, 0.1)',
                    shadowBlur: 10
                }
            }
        };
        bar.setOption(option);
        $(window).resize(function() {
            bar.resize();
        });
    }
}(jQuery, window))