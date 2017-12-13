/**
 * Created by yym on 11/27/17.
 */
var step_chart = (function ($, w) {

    "use strict";
    var f = new Facade();
    var itemStyleColor = ['#3AD1C2', '#60C6CF', '#FFFFFF', '#009992'];
    var barLineChart;
    var mapChart;
    var barChart;

    $(function(){
        bar_line_chart("market_trend");
        map_chart("market_map");
        bar_chart("market_bar");

        var json = JSON.stringify(f.parameterPrefix.conditions({"tables": ["fea9f203d4f593a96f0d6faa91ba24ba"], "user_token": "bearer47ee6f05c8994e9ddbe12c2971600766", "marketWithYear": "201502-INF"}));

        f.ajaxModule.baseCall('/calc/querySalesVsShare', json, 'POST', function(r) {
            if(r.status === 'ok') {
                var $select_month =  $('select[name="calc-result-month"]');
                var $select_market = $('select[name="calc-result-market"]');
                $select_month.empty();
                $select_market.empty();
                $.each(r.result.result_condition, function(i, v){
                    $select_month.append('<option value="'+ v.Date +'">' + v.Date + '</option>');
                    $select_market.append('<option  value="'+ v.Market +'">' + v.Market + '</option>');
                });

                $('span[name="sumsales"]').empty().text(parseFloat(r.result.cursales / 1000000).toFixed(2));
                $('span[name="productsales"]').empty().text(parseFloat(r.result.curproductsales / 1000000).toFixed(2));
                $('span[name="share"]').empty().text((parseFloat(r.result.curproductsales) / parseFloat(r.result.cursales) * 100).toFixed(2));


                var $echart_option = barLineChart.getOption();
                var xAxisData = [];
                var seriesBarData = [];
                var seriesLineData = [];
                $.each(r.result.condition, function(i, v) {
                    xAxisData.push(v.Date);
                    seriesBarData.push((v.Sales / 1000000).toFixed(2));
                    seriesLineData.push(v.Share);
                });
                $echart_option.xAxis[0].data = xAxisData;
                $echart_option.series[0].data = seriesBarData;
                $echart_option.series[1].data = seriesLineData;
                barLineChart.setOption($echart_option);
            } else {
                console.error("error");
            }
        });

        f.ajaxModule.baseCall('/calc/queryCurVsPreWithCity', json, 'POST', function(r) {
            if(r.status === 'ok') {
                var $echart_map_option = mapChart.getOption();
                var $echart_bar_option = barChart.getOption();

                var seriesMapData = [];
                var yAxisBarData = [];
                var seriesBarMarketData = [];
                var seriesBarProductData = [];

                $.each(r.result.condition, function(i, v) {
                    seriesMapData.push({name: v.Province, value: v.Sales, productSales: v.ProductSales, share: v.Share});
                });

                $.each(r.result.bar, function(i, v) {
                    yAxisBarData.push(v.Province);
                    seriesBarMarketData.push(v.Sales);
                    seriesBarProductData.push(v.ProductSales);
                });
                $echart_map_option.series[0].data = seriesMapData;
                $echart_map_option.visualMap[0].max = seriesMapData[0].value;

                $echart_bar_option.yAxis[0].data = yAxisBarData;
                $echart_bar_option.series[0].data = seriesBarMarketData;
                $echart_bar_option.series[1].data = seriesBarProductData;

                mapChart.setOption($echart_map_option);
                barChart.setOption($echart_bar_option);

            } else {
                console.error("error");
            }
        });

        $(w).resize(function () {
            barLineChart.resize();
            mapChart.resize();
            barChart.resize();
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
                    data: [], //['2000', '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016'],
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
                    data: [], //[209, 236, 325, 439, 507, 576, 722, 879, 938, 1364, 1806, 1851, 1931, 2198, 2349, 2460, 2735],
                    label: {
                        normal: {
                            show: false,
                            color: "#FFFFFF",
                            position: 'minddle'
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
                    data: [], //[1, 13, 37, 35, 15, 13, 25, 21, 6, 45, 32, 2, 4, 13, 6, 4, 10],
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
        mapChart = echarts.init(document.getElementById(id));

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
                trigger: 'item',
                textStyle: {align: 'left'},
                formatter: function (v) {
                    var tip_content = '省份：'+ v.data.name +'<br/>';
                    tip_content += '市场销量：'+ (f.thousandsModule.formatNum(v.data.value)) +'<br/>';
                    tip_content += '产品销量：'+ (f.thousandsModule.formatNum(v.data.productSales)) +'<br/>';
                    tip_content += '份额：'+ (parseFloat(v.data.share) < 0 ? 0 : v.data.share) +'%';
                    return tip_content;
                }
            },
            visualMap: {
                min: 0,
                max: 56173792,
                left: 'left',
                top: 'bottom',
                text: ['高','低'],
                inRange: {
                    color: ['#EBF0EF', '#37D1C1']
                },
                calculable : true
            },
            series: [{
                name: '中国',
                type: 'map',
                zoom: 1.2,
                mapType: 'china',
                roam: false,
                label: {normal: {show: true}},
                data: []
            }]
        };


        mapChart.setOption(option);

    }

    function bar_chart(id) {
        barChart = echarts.init(document.getElementById(id));
        var option = {
            tooltip: {
                trigger: 'axis',
                textStyle: {align: 'left'},
                axisPointer: {type: 'shadow'}
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
                data: [],
                axisLabel: {show: true, interval: 'auto', formatter: '{value}'}
            },
            series: [
                {
                    name: '市场总销售额',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color : '#ADADAD',//柱状图颜色
                            label: {
                                // textStyle: {color: '#000000', fontSize: 3},
                                show: false,
                                position: 'right'
                                // formatter: '{c}%'
                            }
                        }
                    },
                    data: []
                },
                {
                    name: '产品销售额',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: '#60B3AD',
                            label: {
                                // textStyle: {color: '#000000', fontSize: 3},
                                show: false,
                                position: 'right'
                                // formatter: '{c}%'
                            }
                        }
                    },
                    data: []
                }
            ]
        };

        barChart.setOption(option);
    }

    return {
        "barLineChart": function() {return barLineChart;},
        "mapChart": function() {return mapChart;},
        "barChart": function() {return barChart;}
    }
}(jQuery, window));