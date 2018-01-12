/**
 * Created by yym on 11/22/17.
 */

var sample = (function ($, w) {

    let lineChart1, lineChart2, barChart;
    let $select_market = $('div[name="select-box"] select[name="sample-market"]');
    let $select_date = $('div[name="select-box"] select[name="sample-date"]');
    let f = new Facade();
    let baseLine;

    $(function(){
        sample_hospital_number('lineChart1');
        sample_product_number('lineChart2');
        sample_sales("barChart1");

        $('#query-sample').click(function(){
            let json = JSON.stringify(f.parameterPrefix.conditions({
                "user_token": $.cookie("user_token"),
                "uid": $.cookie("uid"),
                "company": $.cookie("company"),
                "market": $select_market.val(),
                "date": $select_date.val()}));
            query_base_line(json);
        });

        $('#reset-upload').click(function(){
            layui.use('layer', function(){
                let layer = layui.layer;
                layer.confirm('是否重新上传源文件？', {
                    btn: ['重新上传', '取消'], //按钮
                    resize: false,
                    maxWidth: 'auto',
                    closeBtn: 0
                }, function(){
                    w.location = '/calcul/step';
                }, function(){});
            });
        });

        $(w).resize(function() {
            lineChart1.resize();
            lineChart2.resize();
            barChart.resize();
        });
    });

    const query_selectBox = function() {
        let json = JSON.stringify(f.parameterPrefix.conditions({
            "user_token": $.cookie("user_token"),
            "uid": $.cookie("uid"),
            "company": $.cookie("company")
        }));
        f.ajaxModule.baseCall('/sample/querySelectBox', json, 'POST', function(r) {
            let market= [];
            let date = [];
            $select_market.empty();
            $select_date.empty();
            if(r.status === 'ok') {
                $.each(r.result.data.selectBox, function(i, v){
                    market.push(v.market);
                    date.push(v.date);
                });
                $.each($.unique(market), function (i, v) {
                    $select_market.append('<option value="' + v + '">' + v + '</option>');
                });
                $.each($.unique(date), function (i, v) {
                    $select_date.append('<option value="' + v + '">' + v + '</option>');
                });
            }
            let json = JSON.stringify(f.parameterPrefix.conditions({
                "user_token": $.cookie("user_token"),
                "uid": $.cookie("uid"),
                "company": $.cookie("company"),
                "market": $select_market.val(),
                "date": $select_date.val()
            }));
            set_sample_market_name($select_market.val());
            query_base_line(json)
        });









        //Test
        // let a = JSON.stringify(f.parameterPrefix.conditions({
        //     "user_token": $.cookie("user_token"),
        //     "uid": $.cookie("uid"),
        //     "company": $.cookie("company"),
        //     "market": "麻醉市场",
        //     "date": "201604"
        // }));
        // f.ajaxModule.baseCall('/sample/queryDataBaseLine', a, 'POST', function(r){
        //     if(r.status === 'ok') {
        //         baseLine = r.result.data.baseLine;
        //         let $echart_option1 = lineChart1.getOption();
        //         let $echart_option2 = lineChart2.getOption();
        //         let $echart_option3 = barChart.getOption();
        //
        //         let hospital_array = [];
        //         let sales_array = [];
        //         let product_array = [];
        //         $.each(r.result.data.baseLine, function (i, v) {
        //             hospital_array.push(v.Hospital);
        //             sales_array.push(v.Sales);
        //             product_array.push(v.Product);
        //         });
        //         $echart_option1.series[1].data = hospital_array;
        //         $echart_option2.series[1].data = product_array;
        //         $echart_option3.series[1].data = sales_array;
        //         lineChart1.setOption($echart_option1);
        //         lineChart2.setOption($echart_option2);
        //         barChart.setOption($echart_option3);
        //         query_data(a)
        //     }
        // });


    };

    const query_base_line = function(json) {
        f.ajaxModule.baseCall('/sample/queryDataBaseLine', json, 'POST', function(r){
            if(r.status === 'ok') {
                baseLine = r.result.data.baseLine;
                let $echart_option1 = lineChart1.getOption();
                let $echart_option2 = lineChart2.getOption();
                let $echart_option3 = barChart.getOption();

                let hospital_array = [];
                let sales_array = [];
                let product_array = [];
                $.each(r.result.data.baseLine, function (i, v) {
                    hospital_array.push(v.Hospital);
                    sales_array.push(v.Sales);
                    product_array.push(v.Product);
                });
                $echart_option1.series[1].data = hospital_array;
                $echart_option2.series[1].data = product_array;
                $echart_option3.series[1].data = sales_array;
                lineChart1.setOption($echart_option1);
                lineChart2.setOption($echart_option2);
                barChart.setOption($echart_option3);
                query_data(json)
            }
        });
    };


    // 不封装了
    const query_data = function(json) {
        $(document).ajaxStop(function(){
            hide_loading();
        });
        show_loading();
        const set_sample_tip = function(obj) {
            $.each(baseLine, function(i, v) {
               if (obj.month === v.Month) {
                   let value = parseFloat(v[obj.read_key]);
                   let pct = ((obj.cur_hospital_num - value) / obj.cur_hospital_num) * 100;
                   let $pct_tip =  $('#' + obj.pct_tip_id);
                   if (obj.read_key === 'Hospital' || obj.read_key === 'Product') {
                       pct >= 5 || pct <= -5 ? $pct_tip.attr('class', 'sample-badge sample-badge-error') : $pct_tip.attr('class', 'sample-badge');
                       $('#' + obj.base_tip_id).text(value + '个');
                   } else {
                       pct >= 10 || pct <= -10 ? $pct_tip.attr('class', 'sample-badge sample-badge-error') : $pct_tip.attr('class', 'sample-badge');
                       $('#' + obj.base_tip_id).text(f.thousandsModule.formatNum(value));
                   }
                   $pct_tip.html(pct.toFixed(2) + '<b>%</b>');
               }
            });
        };
        f.ajaxModule.baseCall('/sample/queryHospitalNumber', json, 'POST', function(r){
            if(r.status === 'ok') {
                let $echart_option = lineChart1.getOption();
                let hospital_array = [];
                $.each(r.result.data, function (i, v) {
                    hospital_array.push(v);
                    if(v !== '0') {
                        $('#cur-sample-hospital-num').text(v + '个');
                        let obj = {
                            month: parseInt(i),
                            cur_hospital_num: v,
                            base_tip_id: 'base-sample-hospital-num',
                            pct_tip_id: 'pct-sample-hospital',
                            read_key: 'Hospital'
                        };
                        set_sample_tip(obj);
                    }
                });
                $echart_option.series[0].data = hospital_array;
                lineChart1.setOption($echart_option);
            }
        });
        f.ajaxModule.baseCall('/sample/queryProductNumber', json, 'POST', function(r){
            if(r.status === 'ok') {
                let $echart_option = lineChart2.getOption();
                let product_array = [];
                $.each(r.result.data, function (i, v) {
                    product_array.push(v);
                    if(v !== '0') {
                        $('#cur-sample-product-num').text(v + '个');
                        let obj = {
                            month: parseInt(i),
                            cur_hospital_num: v,
                            base_tip_id: 'base-sample-product-num',
                            pct_tip_id: 'pct-sample-product',
                            read_key: 'Product'
                        };
                        set_sample_tip(obj);
                    }
                });
                $echart_option.series[0].data = product_array;
                lineChart2.setOption($echart_option);
            }
        });
        f.ajaxModule.baseCall('/sample/querySampleSales', json, 'POST', function(r){
            if(r.status === 'ok') {
                let $echart_option = barChart.getOption();
                let sales_array = [];
                $.each(r.result.data, function (i, v) {
                    sales_array.push(v);
                    if(v !== '0') {
                        $('#cur-sample-sales-num').text(f.thousandsModule.formatNum(parseFloat(v)));
                        let obj = {
                            month: parseInt(i),
                            cur_hospital_num: v,
                            base_tip_id: 'base-sample-sales-num',
                            pct_tip_id: 'pct-sample-sales',
                            read_key: 'Sales'
                        };
                        set_sample_tip(obj);
                    }
                });
                $echart_option.series[0].data = sales_array;
                barChart.setOption($echart_option);
            }
        });
        f.ajaxModule.baseCall('/sample/queryHospitalList', json, 'POST', function(r){
            if(r.status === 'ok') {
                let $table = $('#sample-tbody');
                $table.empty();
                $.each(r.result.data, function(i, v){
                    $table.append('<tr><td>' + v["PHA医院名称"] + '</td><td>' + v["Province"] + '</td><td>' +  v["Prefecture"] + '</td><td>' + v["City Tier 2010"] + '</td></tr>');
                });
                layui.use('table', function(){
                    let table = layui.table;
                    table.init('sample-hospital', {
                        height: 315,
                        page: true //开启分页
                        // id: 'sample-hospital'
                    });

                });
            }
        });
    };

    const set_sample_market_name = function(name) {
        $('span[name="sample-market-tip"]').empty().text(name);
    };

    let common = function(leftName) {
        return {
            tooltip : {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            grid: {
                left: '8%',
                right: '5%',
                bottom: '6%',
                top: '5%'
            },
            xAxis: {
                splitNumber : 12,
                data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
            },
            yAxis: [
                {
                    show: true,
                    type: 'value',
                    name: leftName
                },
                {
                    show: false,
                    type: 'value'
                }
            ],
            series: [
                {
                    name: leftName,
                    type: 'bar',
                    data : [],
                    lineStyle : {normal : {color : '#60B3AD'}},
                    itemStyle : {normal: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                            offset: 0,
                            color: 'rgba(17, 168,171, 1)'
                        }, {
                            offset: 1,
                            color: 'rgba(17, 168,171, 0.5)'
                        }]),
                        barBorderRadius: 5,
                        shadowColor: 'rgba(0, 0, 0, 0.1)',
                        shadowBlur: 10
                    }}
                },
                {
                    name: '去年同期',
                    type: 'line',
                    data : [],
                    yAxisIndex: 0,
                    lineStyle : {normal : {color : '#b3312f', type: 'dotted'}},
                    itemStyle : {normal : {color : '#b3312f'}}
                }
            ]
        };
    };

    function sample_hospital_number (id) {
        lineChart1 = echarts.init(document.getElementById(id));
        lineChart1.setOption(common('医院数量'));
    }

    function sample_product_number(id) {
        lineChart2 = echarts.init(document.getElementById(id));
        lineChart2.setOption(common('产品数量'));
}

    function sample_sales (id) {
        barChart = echarts.init(document.getElementById(id));
        barChart.setOption(common('样本销售额'));
    }


    return {
        "query_selectBox" : query_selectBox,
        "lineChart1": function() {return lineChart1},
        "lineChart2": function() {return lineChart2},
        "barChart": function() {return barChart},
        "query_data": query_data
    }

}(jQuery, window));