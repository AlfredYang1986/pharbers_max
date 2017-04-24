/**
 * Created by Wli on 2017/1/5.
 */
$(function(){
    var i = 0
    $('#data_5 .input-daterange').datepicker({
        minViewMode: 1,
        keyboardNavigation: false,
        forceParse: false,
        autoclose: true,
        todayHighlight: true
    }).on('changeDate',function(){
        if(i==0){
            sampleCheckFun()
            i++
        }else{
            i=0
        }
    });

    sampleCheckFun();

    //*********************************************************************
    //功能: 样本检查
    //时间：20170413
    //创建：Arthas
    //说明：加载样本检查数据。
    //*********************************************************************
    function sampleCheckFun() {
        var market = $('select[data-name="search-result-market"]').val();
        var date = $('input[name="date_5"]').val();
        var dataMap = JSON.stringify({
            "company": $.cookie("token"),
            "market": market,
            "date": date
        });
        $.ajax({
            type: "POST",
            url: "/samplecheck/check",
            dataType: "json",
            data: dataMap,
            contentType: 'application/json,charset=utf-8',
            success: function (r) {
                cel_data(r);
                cur12_data_HPM(r);
                cur12_las12_data(r);
                var data = Top_Mismatch(r);
                dataTableAjax(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $.tooltip('My God, 出错啦！！！');
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
        var cur_data = r.result.cur_data
        var ear_data = r.result.ear_data
        var las_data = r.result.las_data
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
        var hosp_x_data = [];
        var hosp_s_data = [];
        var hosp_xs_data = [];
        var prod_x_data = [];
        var prod_s_data = [];
        var mark_x_data = [];
        var mark_s_data = [];

        var cur12_data_H = r.result.cur12_data_H
        for(var item in cur12_data_H){
            var obj = cur12_data_H[item];
            hosp_x_data.push(obj.Date);
            hosp_s_data.push(obj.HospNum);
        }

        var cur12_data_P = r.result.cur12_data_P
        for(var item in cur12_data_P){
            var obj = cur12_data_P[item];
            prod_x_data.push(obj.Date);
            prod_s_data.push(obj.ProductNum);
        }

        var cur12_data_M = r.result.cur12_data_M
        for(var item in cur12_data_M){
            var obj = cur12_data_M[item];
            mark_x_data.push(obj.Date);
            mark_s_data.push(obj.MarketNum);
        }

//        $("#sparkline1").sparkline(hosp_s_data,{
//            type: 'line',
//            width: '100%',
//            height: '50',
//            lineColor: '#1ab394',
//            fillColor: "transparent",
//            tooltipFormat: '{{offset:offset}} : {{y:val}}'
//        }).bind('sparklineRegionChange', function(ev) {
//            var sparkline = ev.sparklines[0],
//            region = sparkline.getCurrentRegionFields(),
//            value = region.y;
//            var index = region.x+":"+region.y
//            console.info(hosp_x_data[region.x]+":"+region.y);
//            console.info(this.tooltip);
//        });

        hosp_option = {
            title: {
                text: ''
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                   type: 'none'
                }
            },
            legend: {
                x: 'left',
                y: 'middle',
                orient: 'vertical',
                data: ['数量'],
                show: false
            },
            grid: {
                bottom: '3%',
                left: '-11px',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                show: false,
                boundaryGap: false,
                data: hosp_x_data
            },
            yAxis: {
                type: 'value',
                show: false
            },
            series: [{
                name: '数量',
                type: 'line',
                smooth: true,
                symbolSize: symbolSize,
                itemStyle : {
                    normal : {
                        color:'#1068AF',
                        lineStyle:{
                            color:'#1068AF'
                        }
                    }
                },
                data: hosp_s_data
            }]
        };

        prod_option = {
            title: {
                text: ''
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'none'
                }
            },
            legend: {
                x: 'left',
                y: 'middle',
                orient: 'vertical',
                data: ['数量'],
                show: false
            },
            grid: {
                bottom: '3%',
                left: '-11px',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                show: false,
                boundaryGap: false,
                data: prod_x_data
            },
            yAxis: {
                type: 'value',
                show: false
            },
            series: [{
                name: '数量',
                type: 'line',
                smooth: true,
                symbolSize: symbolSize,
                itemStyle : {
                    normal : {
                        color:'#37892A',
                        lineStyle:{
                            color:'#37892A'
                        }
                    }
                },
                data: prod_s_data
            }]
        };

        mark_option = {
            title: {
                text: ''
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                   type: 'none'
                }
            },
            legend: {
                x: 'left',
                y: 'middle',
                orient: 'vertical',
                data: ['数量'],
                show: false
            },
            grid: {
                bottom: '3%',
                left: '-11px',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                show: false,
                boundaryGap: false,
                data: mark_x_data
            },
            yAxis: {
                type: 'value',
                show: false
            },
            series: [{
                name: '数量',
                type: 'line',
                smooth: true,
                symbolSize: symbolSize,
                itemStyle : {
                    normal : {
                        color:'#0098D9',
                        lineStyle:{
                            color:'#0098D9'
                        }
                    }
                },
                data: mark_s_data
            }]
        };

        var sparkline1 = echarts.init(document.getElementById('sparkline1'));
        var sparkline2 = echarts.init(document.getElementById('sparkline2'));
        var sparkline3 = echarts.init(document.getElementById('sparkline3'));
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
        var cur12_las12_Sales1 = r.result.cur12_las12_data.cur12_las12_Sales
        var cur12_las12_Units2 = r.result.cur12_las12_data.cur12_las12_Units

        var x_data = [];
        var y_curr12_sales = [];
        var y_last12_sales = [];
        var y_curr12_utils = [];
        var y_last12_utils = [];

        for(var item in cur12_las12_Sales1){
            var obj = cur12_las12_Sales1[item];
            x_data.push(obj.Date);
            y_curr12_sales.push((obj.cur_Sales/10000).toFixed(4));
            y_last12_sales.push((obj.las_Sales/10000).toFixed(4));
        }

        for(var item in cur12_las12_Units2){
            var obj = cur12_las12_Units2[item];
            x_data.push(obj.Date);
            y_curr12_utils.push((obj.cur_Units/10000).toFixed(4));
            y_last12_utils.push((obj.las_Units/10000).toFixed(4));
        }

        var itemStyleColor = ['#23c6c8', '#1ab394'];
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
                splitLine: {show: true,interval: 'auto',lineStyle: {color: [itemStyleColor[0]]}},
                axisTick: {show: false},
                axisLine: {lineStyle: {color: itemStyleColor[0]}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            yAxis: {
                type: 'value',
                name: '销售额(万)',
                splitLine: {lineStyle: {color: [itemStyleColor[1]]}},
                axisTick: {show: false},
                axisLine: {lineStyle: {color: itemStyleColor[1]}},
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
                splitLine: {show: true,interval: 'auto',lineStyle: {color: ['#23c6c8']}},
                axisTick: {show: false},
                axisLine: {lineStyle: {color: '#23c6c8'}},
                axisLabel: {margin: 10,textStyle: {fontSize: 14}}
            },
            yAxis: {
                type: 'value',
                name: '销售量(万)',
                splitLine: {lineStyle: {color: ['#1ab394']}},
                axisTick: {show: false},
                axisLine: {lineStyle: {color: '#1ab394'}},
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
        var mismatch_lst = r.result.misMatchHospital
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