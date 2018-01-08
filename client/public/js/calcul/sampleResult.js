/**
 * Created by yym on 11/22/17.
 */

var sample = (function ($, w) {

    var lineChart1, lineChart2, barChart;
    var $select_market = $('div[name="select-box"] select[name="sample-market"]');
    var $select_date = $('div[name="select-box"] select[name="sample-date"]');
    var f = new Facade();

    $(function(){
        sample_hospital_number('lineChart1');
        sample_product_number('lineChart2');
        sample_sales("barChart1");

        $('#reset-upload').click(function(){
            layui.use('layer', function(){
                var layer = layui.layer;
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

    var query_selectBox = function(){
        var json = JSON.stringify(f.parameterPrefix.conditions({
            "user_token": $.cookie("user_token"),
            "uid": $.cookie("uid"),
            "company": $.cookie("company")
        }));
        f.ajaxModule.baseCall('/sample/querySelectBox', json, 'POST', function(r) {
            var market= [];
            var date = [];
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
            var json = JSON.stringify(f.parameterPrefix.conditions({
                "user_token": $.cookie("user_token"),
                "uid": $.cookie("uid"),
                "company": $.cookie("company"),
                "market": $select_market.val(),
                "date": $select_date.val()
            }));
            query_data(json);
        });
    };

    var common = function(leftName) {
        return {
            tooltip : {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            xAxis: {
                splitNumber : 12,
                data: []
            },
            yAxis: {
                type: 'value',
                name: leftName,
                position: "left"
            },
            series: [{
                name: leftName,
                type: 'line',
                data : [],
                lineStyle : {
                    normal : {color : '#60B3AD'}
                },
                itemStyle : {
                    normal : {color : '#60B3AD'}
                }
            }]
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
        var option = {
            color: ['#3398DB'],
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : [],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis : [{axisTick: {alignWithLabel: true}}],
            series : [
                {
                    name:'样本销售额',
                    type:'bar',
                    barWidth: '40%',
                    data:[]
                }

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
        barChart.setOption(option);
    }

    var query_data = function(json) {
        $(document).ajaxStop(function(){
            hide_loading();
        });
        show_loading();
        f.ajaxModule.baseCall('/sample/queryHospitalNumber', json, 'POST', function(r){
            if(r.status === 'ok') {
                $('div[name="cur-hospital-number"]').empty().text(r.result.data.curHospitalNumber);
                $('div[name="pre-hospital-number"]').empty().text(r.result.data.preHospitalNumber);
                $('div[name="last-hospital-number"]').empty().text(r.result.data.lastHospitalNumber);
                var date = [];
                var value = [];
                var $echart_option = lineChart1.getOption();
                $.each(r.result.data.hospitalList, function(i, v){
                    date.push(v.date);
                    value.push(v.hospitalNumber);
                });
                $echart_option.xAxis[0].data = date;
                $echart_option.series[0].data = value;
            }
            lineChart1.setOption($echart_option);
        });
        f.ajaxModule.baseCall('/sample/queryProductNumber', json, 'POST', function(r){
            if(r.status === 'ok') {
                $('div[name="cur-product-number"]').empty().text(r.result.data.curProductNumber);
                $('div[name="pre-product-number"]').empty().text(r.result.data.preProductNumber);
                $('div[name="last-product-number"]').empty().text(r.result.data.lastProductNumber);
                var date = [];
                var value = [];
                var $echart_option = lineChart2.getOption();
                $.each(r.result.data.productList, function(i, v){
                    date.push(v.date);
                    value.push(v.productNumber);
                });
                $echart_option.xAxis[0].data = date;
                $echart_option.series[0].data = value;
            }
            lineChart2.setOption($echart_option);
        });
        f.ajaxModule.baseCall('/sqmple/querySampleSales', json, 'POST', function(r){
            if(r.status === 'ok') {
                var date = [];
                var value = [];
                var $echart_option = barChart.getOption();
                $.each(r.result.data.sampleSalesList, function(i, v){
                    date.push(v.date);
                    value.push(v.sampleSales);
                });
                $echart_option.xAxis[0].data = date;
                $echart_option.series[0].data = value;
            }
            barChart.setOption($echart_option);
        });

        f.ajaxModule.baseCall('/sample/queryHospitalList', json, 'POST', function(r){
            if(r.status === 'ok') {
                var $table = $('#sample-tbody');
                $table.empty();
                $.each(r.result.data, function(i, v){
                    $table.append('<tr><td>' + v["PHA医院名称"] + '</td><td>' + v["Province"] + '</td><td>' +  v["Prefecture"] + '</td><td>' + v["City Tier 2010"] + '</td></tr>');
                });
                layui.use('table', function(){
                    var table = layui.table;
                    table.init('sample-hospital', {
                        height: 315,
                        page: true //开启分页
                        // id: 'sample-hospital'
                    });

                });
            }
        });

    };

    $('#query-sample').click(function(){
        var json = JSON.stringify(f.parameterPrefix.conditions({
            "user_token": $.cookie("user_token"),
            "uid": $.cookie("uid"),
            "company": $.cookie("company"),
            "market": $select_market.val(),
            "date": $select_date.val()}));
        query_data(json);
    });

    return {
        "query_selectBox" : query_selectBox,
        "lineChart1": function() {return lineChart1},
        "lineChart2": function() {return lineChart2},
        "barChart": function() {return barChart},
        "query_data": query_data
    }

}(jQuery, window));