/**
 * Created by Wli on 2017/1/5.
 */
$(function(){

    sampleCheckFun();

    //*********************************************************************
    //功能: 样本检查
    //时间：20170413
    //创建：Arthas
    //说明：加载样本检查数据。
    //*********************************************************************
    function sampleCheckFun() {
        var markets = $('select[data-name="search-result-market"]').val();
        var dates = $('select[data-name="search-result-date"]').val();
        var dataMap = JSON.stringify({
            "company": $.cookie("token"),
            "market": markets,
            "date": dates
        });
        $.ajax({
            type: "POST",
            url: "/samplecheck/check",
            dataType: "json",
            data: dataMap,
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                console.info(data)
                if(data.result.status){
                    var result = data.result.result.result
                    cel_data(result);
                    cur12_data_HPM(result);
                    cur12_las12_data(result);
                    var data = Top_Mismatch(result);
                    dataTableAjax(data);
                }else{
                    $.tooltip('生成样本检查数据失败');
                }
            }
        });
    }

    //*********************************************************************
    //功能: 当期|上期|去年同期
    //创建时间：20170413
    //修改时间：20170421
    //创建人：Arthas
    //说明：医院数量、产品数量、市场数量。
    //*********************************************************************
    var cel_data = function(r){
        var cur_data = r.cur_data
        var ear_data = r.ear_data
        var las_data = r.las_data
        $("#Current_Month_HospitalNum").text(cur_data.HospNum);
        $("#Current_Month_ProductNum").text(cur_data.ProductNum);
        $("#Current_Month_MarketNum").text(cur_data.MarketNum);
        $("#Early_Month_HospitalNum").text(ear_data.HospNum);
        $("#Early_Month_ProductNum").text(ear_data.ProductNum);
        $("#Early_Month_marketNum").text(ear_data.MarketNum);
        $("#Last_Year_HospitalNum").text(las_data.HospNum);
        $("#Last_Year_ProductNum").text(las_data.ProductNum);
        $("#Last_Year_MarketNum").text(las_data.MarketNum);
    }

    var cur12_data_HPM = function(r){

        var symbolSize = 4;
        var x_data = [];
        var s_hosp_data = [];
        var s_prod_data = [];
        var s_mark_data = [];

        var cur12_date = r.cur12_date

        for(var item in cur12_date){
            var obj = cur12_date[item];
            x_data.push(obj.Date);
            s_hosp_data.push(obj.HospNum);
            s_prod_data.push(obj.ProductNum);
            s_mark_data.push(obj.MarketNum);
        }

        hosp_option = {
            title: {text: ''},
            tooltip: {trigger: 'axis', axisPointer: {type: 'none'}},
            legend: {x: 'left', y: 'middle', orient: 'vertical', data: ['数量'], show: false},
            grid: {bottom: '3%', left: '-35px', containLabel: true},
            xAxis: {type: 'category', show: false, boundaryGap: false, data: x_data},
            yAxis: {type: 'value', show: false},
            series: [{name: '数量', type: 'line', smooth: true, symbolSize: symbolSize,
                itemStyle : {
                    normal : {
                        color:'#2BB89B',
                        lineStyle:{
                            color:'#2BB89B'
                        }
                    }
                },
                data: s_hosp_data
            }]
        };

        prod_option = {
            title: {text: ''},
            tooltip: {trigger: 'axis', axisPointer: {type: 'none'}},
            legend: {x: 'left', y: 'middle', orient: 'vertical', data: ['数量'], show: false},
            grid: {bottom: '3%', left: '-35px', containLabel: true},
            xAxis: {type: 'category', show: false, boundaryGap: false, data: x_data},
            yAxis: {type: 'value', show: false},
            series: [{name: '数量', type: 'line', smooth: true, symbolSize: symbolSize, itemStyle : {normal : {color:'#2BB89B', lineStyle:{color:'#2BB89B'}}}, data: s_prod_data}]
        };

        mark_option = {
            title: {text: ''},
            tooltip: {trigger: 'axis', axisPointer: {type: 'none'}},
            legend: {x: 'left', y: 'middle', orient: 'vertical', data: ['数量'], show: false},
            grid: {bottom: '3%', left: '-35px', containLabel: true},
            xAxis: {type: 'category', show: false, boundaryGap: false, data: x_data},
            yAxis: {type: 'value', show: false},
            series: [{name: '数量', type: 'line', smooth: true, symbolSize: symbolSize, itemStyle : {normal : {color:'#2BB89B', lineStyle:{color:'#2BB89B'}}}, data: s_mark_data}]
        };

        var sparkline1 = echarts.init(document.getElementById('hospline'));
        var sparkline2 = echarts.init(document.getElementById('prodline'));
        var sparkline3 = echarts.init(document.getElementById('markline'));
        sparkline1.setOption(hosp_option);
        sparkline2.setOption(prod_option);
        sparkline3.setOption(mark_option);
        window.addEventListener("resize", function() {
            sparkline1.resize();
            sparkline2.resize();
            sparkline3.resize();
        });
    }


    //*********************************************************************
    //功能: Echarts图
    //创建时间：20170413
    //修改日期：20170421
    //创建人：Arthas
    //说明：去年vs今年近12月的销售额、销售数量。
    //*********************************************************************
    function cur12_las12_data(r) {

        var cur12_date = r.cur12_date
        var las12_date = r.las12_date

        var x_data = [];
        var y_curr12_sales = [];
        var y_last12_sales = [];
        var y_curr12_utils = [];
        var y_last12_utils = [];

        for(var item in cur12_date){
            var obj = cur12_date[item];
            x_data.push(obj.Date);
            if(obj.Sales != 0.0){
                y_curr12_sales.push((obj.Sales/10000).toFixed(4));
                y_curr12_utils.push((obj.Units/10000).toFixed(4));
            }else{
                y_curr12_sales.push(0.0000);
                y_curr12_utils.push(0.0000);

            }

        }

        for(var item in las12_date){
            var obj = las12_date[item];
            //x_data.push(obj.Date);
            if(obj.Units != 0.0){
                y_last12_sales.push((obj.Sales/10000).toFixed(4));
                y_last12_utils.push((obj.Units/10000).toFixed(4));
            }else{
                y_last12_sales.push(0.0000);
                y_last12_utils.push(0.0000);
            }
        }

        var itemStyleColor = ['#9CDACD', '#9DC7E1'];
        Sales_Opt = {
            title: {text: '今年Vs去年(近12月销售额)',left: '50%',textAlign: 'center'},
            tooltip: {
                trigger: 'asix',
                axisPointer: {lineStyle: {color: '#ddd'}},
                backgroundColor: 'rgba(255,255,255,1)',
                padding: [5, 10],
                textStyle: {color: '#7588E4'},
                extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
            },
            legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
            xAxis: {
                type: 'category',
                name: '日期',
                data: x_data,
                boundaryGap: false,
                splitLine: {show: false,interval: 'auto',lineStyle: {color: [itemStyleColor[0]]}},
                axisTick: {show: false},
                //axisLine: {lineStyle: {color: itemStyleColor[0]}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            yAxis: {
                type: 'value',
                name: '销售额(万)',
                //splitLine: {lineStyle: {color: [itemStyleColor[1]]}},
                axisTick: {show: false},
                //axisLine: {lineStyle: {color: itemStyleColor[1]}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            series: [{
                name: '去年',
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbol: 'circle',
                symbolSize: 6,
                data: y_last12_sales,
                areaStyle: {normal: {color: '#23c6c8'}},
                itemStyle: {normal: {color: '#23c6c8'}},
                lineStyle: {normal: {width: 3}}
            }, {
                name: '今年',
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbol: 'circle',
                symbolSize: 6,
                data: y_curr12_sales,
                areaStyle: {normal: {color: '#1ab394'}},
                itemStyle: {normal: {color: '#1ab394'}},
                lineStyle: {normal: {width: 3}}
            }]
        };

        Units_Opt = {
            title: {text: '今年Vs去年(近12月销售量)',left: '50%',textAlign: 'center'},
            tooltip: {
                trigger: 'asix',
                axisPointer: {lineStyle: {color: '#ddd'}},
                backgroundColor: 'rgba(255,255,255,1)',
                padding: [5, 10],
                textStyle: {color: '#7588E4'},
                extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
            },
            legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
            xAxis: {
                type: 'category',
                name: '日期',
                data: x_data,
                boundaryGap: false,
                splitLine: {show: false,interval: 'auto',lineStyle: {color: ['#23c6c8']}},
                axisTick: {show: false},
                //axisLine: {lineStyle: {color: '#23c6c8'}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            yAxis: {
                type: 'value',
                name: '销售量(万)',
                //splitLine: {lineStyle: {color: ['#1ab394']}},
                axisTick: {show: false},
                //axisLine: {lineStyle: {color: '#1ab394'}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            series: [{
                name: '去年',
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbol: 'circle',
                symbolSize: 6,
                data: y_last12_utils,
                areaStyle: {normal: {color: '#23c6c8'}},
                itemStyle: {normal: {color: '#23c6c8'}},
                lineStyle: {normal: {width: 3}}
            }, {
                name: '今年',
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbol: 'circle',
                symbolSize: 6,
                data: y_curr12_utils,
                areaStyle: {normal: {color: '#1ab394'}},
                itemStyle: {normal: {color: '#1ab394'}},
                lineStyle: {normal: {width: 3}}
            }]
        };

        var Sales = echarts.init(document.getElementById('Sales'));
        var Units = echarts.init(document.getElementById('Units'));
        Sales.setOption(Sales_Opt);
        Units.setOption(Units_Opt);
        window.addEventListener("resize", function() {
            Sales.resize();
            Units.resize();
        });
    }
    //*********************************************************************
    //功能: 不匹配医院列表
    //时间：20170413
    //创建：Arthas
    //说明：不匹配医院列表。
    //*********************************************************************
    function Top_Mismatch(r) {
        var mismatch_lst = r.misMatchHospital
        var temp = []
        var data = []
        var nobj = mismatch_lst[0]
        if(nobj!=null){
            $.each(mismatch_lst, function (i, v) {
                temp.push((i + 1));
                temp.push(v.Hosp_name);
                temp.push(v.Province);
                temp.push(v.City);
                temp.push(v.City_level);
                temp.push("<a href=\"javascript:;\"><i class=\"fa fa-times text-danger text\"></i></a>");
                data.push(temp)
                temp = []
            })
            return data;
        }
    }

    //*********************************************************************
    //功能: 下一步->小报告
    //时间：20170413
    //创建：Arthas
    //说明：根据样本检查结果生成一份可供用户参考确认得小报告。
    //*********************************************************************
    $('#nextstepBtm').click(function(){
        document.getElementById("ybbg").click()
    });
});