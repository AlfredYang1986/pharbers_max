(function($, w) {
    "use strict";
    var f = new Facade();
    var itemStyleColor = ['#3AD1C2', '#60C6CF', '#FFFFFF', '#009992'];
    var barLineChart;
    $(function() {
        bar_line_chart('market-trend');

        // f.ajaxModule.baseCall('', {}, 'POST', function(r) {
        //     console.info(r)
        // });

        $(w).resize(function () {
            barLineChart.resize();
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


})(jQuery, window);