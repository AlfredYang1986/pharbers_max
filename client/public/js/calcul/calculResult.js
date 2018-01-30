/**
 * Created by yym on 11/27/17.
 */
var step_chart = (function ($, w) {
    "use strict";
    let f = new Facade();
    let itemStyleColor = ['#3AD1C2', '#60C6CF', '#FFFFFF', '#009992'];
    let barLineChart, mapChart, provinces_barChart, city_barChart;
    let table_num = 0;
    let form, layer;

    $(function(){
        bar_line_chart("market_trend");
        map_chart("market_map");
        provinces_bar_chart("provinces_bar");
        city_bar_chart("city_bar");
        layui.use(['form', 'layer'], function() {
            form = layui.form;
            layer = layui.layer;
            form.on('submit(cancel)', function () {
                layer.closeAll();
                return false;
            });
            form.on('submit(save)', function (data) {
                let show_lst = [];
                $.each(data.field, function(i, v){show_lst.push(v)});
                table_num = show_lst.length;
                if (show_lst.length === 0) {
                    layer.msg("您尚未选择要保存的数据！");
                } else {
                    show_loading();
                    let json = JSON.stringify({
                        "businessType": "/datacommit",
                        "uid": $.cookie('uid'),
                        "showLst": show_lst
                    });
                    f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function (r) {
                        if (r.status === 'ok') {
                            layer.closeAll();
                        } else {
                            console.error("Error");
                        }
                    }, null, null, null, false);
                }
                return false;
            });
            form.on('checkbox(calc-result-choose-all)', function(data){
                var inputs = $('div[name="calc-result-choose-lst"] input');
                if (data.elem.checked) {
                    $.each(inputs, function(i, v){$(v).prop("checked", true)});
                } else {
                    $.each(inputs, function(i, v){$(v).prop("checked", false)});
                }
                form.render('checkbox');
            });
        });

        $('div[name="btn-query-calcresult"]').click(function(){
            let json = JSON.stringify(f.parameterPrefix.conditions({
                "user_token": $.cookie("user_token"),
                "date": $('select[name="calc-result-month"]').val(),
                "market": $('select[name="calc-result-market"]').val(),
                "uid": $.cookie("uid")
            }));
            query_data(json);
        });
        $('#submit-data').click(function(){

            layer.confirm('请确认本次结果无误，保存入数据库中数据将无法删除！<br/>并将已有月份的数据，进行覆盖。', {
                btn: ['确认', '取消'], //按钮
                resize: false,
                maxWidth: 'auto',
                closeBtn: 0
            }, function(index){
                let op = {
                    "content": $('#calc-result-selectbox').html(),
                    "title": '请确认本次要保存的数据：'
                    // "offset": '160px'
                };
                f.alertModule.open(op);
                form.render('checkbox');
                // layui有点恶心，尤其是render，不得不删除
                $('div p[name="calc-result-choose-all"]').next('div').remove()
            }, function(){});
        });


        $('#provinces').click(function(){
            $(this).attr('class', 'btn-result btn-active');
            $('#city').attr('class', 'btn-result btn-result-info');
            $('#provinces_bar').show();
            $('#city_bar').hide();
            provinces_barChart.resize();
        });
        $('#city').click(function(){
            $(this).attr('class', 'btn-result btn-active');
            $('#provinces').attr('class', 'btn-result btn-result-info');
            $('#provinces_bar').hide();
            $('#city_bar').show();
            city_barChart.resize();
        });
    });

    let query_select = function() {
        f.ajaxModule.baseCall('/calc/querySelectBox', JSON.stringify(f.parameterPrefix.conditions({"user_token": $.cookie("user_token"), "uid": $.cookie("uid")})), 'POST', function (r) {
            if(r.status === 'ok') {
                var $select_month =  $('select[name="calc-result-month"]').empty();
                var $select_market = $('select[name="calc-result-market"]').empty();
                var $select_checkbox = $('div[name="calc-result-choose-lst"]').empty();
                $('div p[name="calc-result-choose-all"]').empty().append('<input lay-filter="calc-result-choose-all" type="checkbox" name="" title="全部" lay-skin="primary">');
                var market_lst = [];
                var time_lst = [];

                $.each(r.result.result_condition.select_values, function(i, v){
                    time_lst.push(v.Date);
                    market_lst.push(v.Market);
                    var value = v.Market + '-' + v.Date;
                    var title = v.Market + ' ' + v.Date;
                    $select_checkbox.append('<input type="checkbox" name="' + value + '" title="' + title + '" value="' + value + '" lay-skin="primary">')
                });
                $.each($.unique(market_lst), function(i, v){$select_market.append('<option  value="'+ v +'">' + v + '</option>');});
                $.each($.unique(time_lst).sort(), function(i, v){$select_month.append('<option  value="'+ v +'">' + v + '</option>');});
            }
        });
    };

    let query_data = function(json) {
        $(document).ajaxStop(function(){
            hide_loading();
        });
        show_loading();

        let j = json || JSON.stringify(f.parameterPrefix.conditions({"user_token": $.cookie("user_token"), "uid": $.cookie("uid")}));

        f.ajaxModule.baseCall('/calc/querySalesVsShare', j, 'POST', function(r) {
            if(r.status === 'ok') {

                let $select_month =  $('select[name="calc-result-month"]');
                let $select_market = $('select[name="calc-result-market"]');
                $.each($select_market.find('option'), function(i, v){
                    if(r.result.selectMarket === $(v).val()) {
                        $(v).attr("selected", true)
                    }
                });
                $.each($select_month.find('option'), function(i, v){
                    if(r.result.selectDate === $(v).val()) {
                        $(v).attr("selected", true)
                    }
                });

                $('span[name="sumsales"]').empty().text(r.result.cursales );
                $('span[name="productsales"]').empty().text(r.result.curproductsales );
                $('span[name="share"]').empty().text((parseFloat(r.result.curproductsales) / parseFloat(r.result.cursales) * 100).toFixed(2));


                if (parseInt(r.result.data_last.sales_year) < 0) {
                    $("market-sum-sales").attr("current-lastyear down");
                    $('span[name="current-sales-sign"]').empty().text("-");
                } else {
                    $("market-sum-sales").attr("current-lastyear up");
                    $('span[name="current-sales-sign"]').empty().text("+");
                }
                if (parseInt(r.result.data_last.productSales_year) < 0) {
                    $("product-sales").attr("current-lastyear down");
                    $('span[name="current-product-sales-sign"]').empty().text("-");
                } else {
                    $("product-sales").attr("current-lastyear up");
                    $('span[name="current-product-sales-sign"]').empty().text("+");
                }
                $('span[name="current-sales-value"]').empty().text(r.result.data_last.sales_year);
                $('span[name="current-product-sales-value"]').empty().text(r.result.data_last.productSales_year);



                let xAxisData = [];
                let seriesBarData = [];
                let seriesLineData = [];
                $.each(r.result.condition, function(i, v) {
                    xAxisData.push(v.Date);
                    seriesBarData.push(v.Sales);
                    seriesLineData.push(v.Share);
                });

                barLineChart.setOption({
                    xAxis: [
                        {
                            data: xAxisData
                        }
                    ],
                    series: [
                        {
                            name:'市场销量',
                            type:'bar',
                            yAxisIndex: 0,
                            data: seriesBarData
                        },
                        {
                            name:'份额占比',
                            type:'line',
                            yAxisIndex: 1,
                            data: seriesLineData
                        }
                    ]
                });
            } else {
                console.error("error");
            }
        });

        f.ajaxModule.baseCall('/calc/queryAreaData', j, 'POST', function(r) {
            if(r.status === 'ok') {
                // 先完善功能，这个重复问题与Echarts版本问题后续重构的时候需要解决
                let seriesMapData = [];

                let areaData = [];
                let lastData = [];
                let curData = [];

                let city_areaData = [];
                let city_lastData = [];
                let city_curData = [];

                $.each(r.result.condition, function(i, v) {
                    seriesMapData.push({name: v.Provinces, value: v.Sales, productSales: v.ProductSales, share: v.Share});
                });


                $.each(r.result.provinces_bar.history_provinces, function(i, v){
                    lastData.push(v.Sales);
                });

                $.each(r.result.provinces_bar.cur_provinces, function(i, v){
                    areaData.push(v.Provinces);
                    curData.push(v.Sales);
                });

                $.each(r.result.city_bar.history_city, function(i, v){
                    city_lastData.push(v.Sales);
                });

                $.each(r.result.city_bar.cur_city, function(i, v){
                    city_areaData.push(v.City);
                    city_curData.push(v.Sales);
                });

                mapChart.setOption({
                    visualMap: {
                        max: seriesMapData[0].value
                    },
                    series: [{
                        data: seriesMapData
                    }]
                });

                provinces_barChart.setOption({
                    baseOption: {
                        yAxis: [
                            {
                                type: 'category',
                                inverse: true,
                                data: areaData
                            },
                            {
                                gridIndex: 1,
                                type: 'category',
                                data: areaData
                            },
                            {
                                gridIndex: 2,
                                type: 'category',
                                data: areaData
                            }
                        ]
                    },
                    options: [
                        {
                            series: [
                                {
                                    name: '去年同期',
                                    type: 'bar',
                                    data: lastData,
                                }, {
                                    name: '本期',
                                    type: 'bar',
                                    data: curData,
                                }
                            ]
                        }
                    ]
                });

                city_barChart.setOption({
                    baseOption: {
                        yAxis: [
                            {
                                type: 'category',
                                inverse: true,
                                data: city_areaData
                            },
                            {
                                gridIndex: 1,
                                type: 'category',
                                data: city_areaData
                            },
                            {
                                gridIndex: 2,
                                type: 'category',
                                data: city_areaData
                            }
                        ]
                    },
                    options: [
                        {
                            series: [
                                {
                                    name: '去年同期',
                                    type: 'bar',
                                    data: city_lastData,
                                }, {
                                    name: '本期',
                                    type: 'bar',
                                    data: city_curData,
                                }
                            ]
                        }
                    ]
                });
            } else {
                console.error("error");
            }
        });
    };

    function bar_line_chart(id) {
        barLineChart = echarts.init(document.getElementById(id));
        let option = {
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
                    name:'市场销量',
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
                    name:'份额占比',
                    type:'line',
                    yAxisIndex: 1,
                    data: [], //[1, 13, 37, 35, 15, 13, 25, 21, 6, 45, 32, 2, 4, 13, 6, 4, 10],
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
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
        let option = {
            title: {
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
                    let tip_content = '省份：'+ v.data.name +'<br/>';
                    tip_content += '市场销量：'+ (f.thousandsModule.formatNum(v.data.value)) + '(Mil)' +'<br/>';
                    tip_content += '产品销量：'+ (f.thousandsModule.formatNum(v.data.productSales)) + '(Mil)'  +'<br/>';
                    tip_content += '份额：'+ (parseFloat(v.data.share) < 0 ? 0 : v.data.share) +'%';
                    return tip_content;
                }
            },
            visualMap: {
                min: 0,
                max: 9999999999,
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

    function provinces_bar_chart(id) {
        provinces_barChart = echarts.init(document.getElementById(id));
        let provinces_option = {
            baseOption: {
                timeline: {show: false},//这个不能删除
                title: {
                    text: "市场规模排名",
                    left: '200'
                },
                tooltip: {
                    show: true,
                    trigger: 'axis',
                    formatter: '{b}<br/>{a}: {c}（Mil）',
                    axisPointer: {
                        type: 'shadow',
                    }
                },
                grid: [
                    {
                        show: false,
                        left: '10%',
                        top: 60,
                        bottom: 60,
                        containLabel: true,
                        width: '40%',
                    },
                    {
                        show: false,
                        left: '50.5%',
                        top: 80,
                        bottom: 60,
                        width: '0%',
                    },
                    {
                        show: false,
                        right: '10%',
                        top: 60,
                        bottom: 60,
                        containLabel: true,
                        width: '40%',
                    }
                ],
                xAxis: [
                    {
                        type: 'value',
                        inverse: true,
                        axisLine: {
                            show: false,
                        },
                        axisTick: {
                            show: false,
                        },
                        position: 'top',
                        axisLabel: {
                            show: false,
                            textStyle: {
                                color: '#000000',
                                fontSize: 12,
                            },
                        },
                        splitLine: {
                            show: false,
                            lineStyle: {
                                color: '#1F2022',
                                width: 1,
                                type: 'solid',
                            },
                        },
                    },
                    {
                        gridIndex: 1,
                        show: false,
                    },
                    {
                        gridIndex: 2,
                        type: 'value',
                        axisLine: {
                            show: false,
                        },
                        axisTick: {
                            show: false,
                        },
                        position: 'top',
                        axisLabel: {
                            show: false,
                            textStyle: {
                                color: '#B2B2B2',
                                fontSize: 12,
                            },
                        },
                        splitLine: {
                            show: false,
                            lineStyle: {
                                color: '#1F2022',
                                width: 1,
                                type: 'solid',
                            },
                        },
                    }
                ],
                yAxis: [
                    {
                        type: 'category',
                        inverse: true,
                        position: 'right',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            show: false,
                            margin: 8,
                            textStyle: {
                                color: '#9D9EA0',
                                fontSize: 12,
                            },

                        },
                        data: [],//areaData,
                    },
                    {
                        gridIndex: 1,
                        type: 'category',
                        inverse: true,
                        position: 'left',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            show: true,
                            textStyle: {
                                align: 'center',
                                color: '#9D9EA0',
                                fontSize: 12,
                            },

                        },
                        data: [],//areaData//
                    },
                    {
                        gridIndex: 2,
                        type: 'category',
                        inverse: true,
                        position: 'left',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            show: false,
                            textStyle: {
                                color: '#9D9EA0',
                                fontSize: 12,
                            },

                        },
                        data: []//areaData,
                    }
                ]
            },
            options: [
                {
                    series: [
                        {
                            name: '去年同期',
                            type: 'bar',
                            barGap: 20,
                            barWidth: 20,
                            label: {
                                normal: {
                                    show: false,
                                },
                                emphasis: {
                                    show: true,
                                    position: 'left',
                                    offset: [0, 0],
                                    textStyle: {
                                        color: '#08C7AE',
                                        fontSize: 14,
                                    },
                                },
                            },
                            itemStyle: {
                                normal: {
                                    color: '#659F83',
                                },
                                emphasis: {
                                    color: '#08C7AE',
                                },
                            },
                            data: []//lastData,
                        },
                        {
                            name: '本期',
                            type: 'bar',
                            barGap: 20,
                            barWidth: 20,
                            xAxisIndex: 2,
                            yAxisIndex: 2,
                            label: {
                                normal: {
                                    show: false,
                                },
                                emphasis: {
                                    show: true,
                                    position: 'right',
                                    offset: [0, 0],
                                    textStyle: {
                                        color: '#08C7AE',
                                        fontSize: 14,
                                    },
                                },
                            },
                            itemStyle: {
                                normal: {
                                    color: '#08C7AE',
                                },
                                emphasis: {
                                    color: '#08C7AE',
                                },
                            },
                            data: []//curData,
                        }
                    ]
                }
            ]
        };
        provinces_barChart.setOption(provinces_option);
    }

    function city_bar_chart(id) {
        city_barChart = echarts.init(document.getElementById(id));
        let city_option = {
            baseOption: {
                timeline: {show: false},//这个不能删除
                title: {
                    text: "市场规模排名",
                    left: '200'
                },
                tooltip: {
                    show: true,
                    trigger: 'axis',
                    formatter: '{b}<br/>{a}: {c}（Mil）',
                    axisPointer: {
                        type: 'shadow',
                    }
                },
                grid: [
                    {
                        show: false,
                        left: '10%',
                        top: 60,
                        bottom: 60,
                        containLabel: true,
                        width: '40%',
                    },
                    {
                        show: false,
                        left: '50.5%',
                        top: 80,
                        bottom: 60,
                        width: '0%',
                    },
                    {
                        show: false,
                        right: '10%',
                        top: 60,
                        bottom: 60,
                        containLabel: true,
                        width: '40%',
                    }
                ],
                xAxis: [
                    {
                        type: 'value',
                        inverse: true,
                        axisLine: {
                            show: false,
                        },
                        axisTick: {
                            show: false,
                        },
                        position: 'top',
                        axisLabel: {
                            show: false,
                            textStyle: {
                                color: '#000000',
                                fontSize: 12,
                            },
                        },
                        splitLine: {
                            show: false,
                            lineStyle: {
                                color: '#1F2022',
                                width: 1,
                                type: 'solid',
                            },
                        },
                    },
                    {
                        gridIndex: 1,
                        show: false,
                    },
                    {
                        gridIndex: 2,
                        type: 'value',
                        axisLine: {
                            show: false,
                        },
                        axisTick: {
                            show: false,
                        },
                        position: 'top',
                        axisLabel: {
                            show: false,
                            textStyle: {
                                color: '#B2B2B2',
                                fontSize: 12,
                            },
                        },
                        splitLine: {
                            show: false,
                            lineStyle: {
                                color: '#1F2022',
                                width: 1,
                                type: 'solid',
                            },
                        },
                    }
                ],
                yAxis: [
                    {
                        type: 'category',
                        inverse: true,
                        position: 'right',
                        axisLine: {
                                      show: false
                                      },
                        axisTick: {
                                      show: false
                                      },
                        axisLabel: {
                                       show: false,
                                       margin: 8,
                                       textStyle: {
                                       color: '#9D9EA0',
                                       fontSize: 12,
                                       },

                                       },
                        data: [],
                    },
                    {
                        gridIndex: 1,
                        type: 'category',
                        inverse: true,
                        position: 'left',
                        axisLine: {
                                      show: false
                                      },
                        axisTick: {
                                      show: false
                                      },
                        axisLabel: {
                                       show: true,
                                       textStyle: {
                                       align: 'center',
                                       color: '#9D9EA0',
                                       fontSize: 12,
                                       },
                                       },
                        data: []
                    },
                    {
                        gridIndex: 2,
                        type: 'category',
                        inverse: true,
                        position: 'left',
                        axisLine: {
                                      show: false
                                      },
                        axisTick: {
                                      show: false
                                      },
                        axisLabel: {
                                       show: false,
                                       textStyle: {
                                       color: '#9D9EA0',
                                       fontSize: 12,
                                       },

                                       },
                        data: [],
                    }
                ]
            },
            options: [
                {
                    series: [
                        {
                            name: '去年同期',
                            type: 'bar',
                            barGap: 20,
                            barWidth: 20,
                            label: {
                                normal: {
                                    show: false,
                                },
                                emphasis: {
                                    show: true,
                                    position: 'left',
                                    offset: [0, 0],
                                    textStyle: {
                                        color: '#08C7AE',
                                        fontSize: 14,
                                    },
                                },
                            },
                            itemStyle: {
                                normal: {
                                    color: '#659F83',
                                },
                                emphasis: {
                                    color: '#08C7AE',
                                },
                            },
                            data: [],
                        },
                        {
                            name: '本期',
                            type: 'bar',
                            barGap: 20,
                            barWidth: 20,
                            xAxisIndex: 2,
                            yAxisIndex: 2,
                            label: {
                                normal: {
                                    show: false,
                                },
                                emphasis: {
                                    show: true,
                                    position: 'right',
                                    offset: [0, 0],
                                    textStyle: {
                                        color: '#08C7AE',
                                        fontSize: 14,
                                    },
                                },
                            },
                            itemStyle: {
                                normal: {
                                    color: '#08C7AE',
                                },
                                emphasis: {
                                    color: '#08C7AE',
                                },
                            },
                            data: [],
                        }
                    ]
                }
            ]
        };
        city_barChart.setOption(city_option);
    }

    $(w).resize(function () {
        barLineChart.resize();
        mapChart.resize();
        provinces_barChart.resize();
        city_barChart.resize();
    });

    return {
        "barLineChart": function() {return barLineChart;},
        "mapChart": function() {return mapChart;},
        "barChart": function() {return provinces_barChart;},
        "query_data": query_data,
        "query_select": query_select,
        "table_num": function(){return table_num;}
    }
}(jQuery, window));