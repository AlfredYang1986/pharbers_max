/**
 * Created by qianpeng on 2017/7/12.
 */

// $(function(){
//     loadFun()
// })
//
// /**
//  * 加载function
//  */
// var loadFun = function() {
//     createUpload("cpa_upload", 0)
//     bootstraptab()
// }
//
// /**
//  * 计算步骤TAB组件
//  */
// var bootstraptab = function() {
//     $('#progressWizard').bootstrapWizard({
//         'nextSelector': '.next',
//         'previousSelector': '.previous',
//         onNext: function(tab, navigation, index) {
//             if(index <= navigation.find('li').length - 1) {
//                 if(index == 1){
//                     //创建第二个上传组件
//                     createUpload("gycx_upload", 1)
//                 }
//                 var $total = navigation.find('li').length;
//                 var $current = index+1;
//                 var $percent = ($current/$total) * 100;
//                 $('#progressWizard').find('.progress-bar').css('width', $percent+'%');
//             }
//         },
//         onPrevious: function(tab, navigation, index) {
//             if(index >= 0) {
//                 var $total = navigation.find('li').length;
//                 var $current = index+1;
//                 var $percent = ($current/$total) * 100;
//                 $('#progressWizard').find('.progress-bar').css('width', $percent+'%');
//             }
//         }
//     });
//
//     $('#disabledTabWizard').bootstrapWizard({
//         tabClass: 'nav nav-pills nav-justified nav-disabled-click',
//         onTabClick: function(tab, navigation, index) {
//             return false;
//         }
//     });
// }
var p;
(function ($, g, d) {
    var flagnext = false;
    var setProgress = function() {

        conn.listen({
            onTextMessage: function ( message ) {
                var ext = message.ext;
                if (ext != null) {
                    var result = searchExtJson(ext)("type");
                    if(result == "progress") {
                        var r = p.setPercent(parseInt(message.data));
                        msgIdentifying = parseInt(message.data);
                        if(parseInt(message.data) >= 100 || r >= 100) {
                            setCloseInterval();
                            setTimeout(function(){$(".progresstier").css("display", "none");p.setPercent(0);}, 1000 * 1);
                        }
                    }else if(result == "txt") {
                        console.info(message.data);
                    }else if(result == "progress_calc"){
                        var r = p.setPercent(parseInt(message.data));
                        msgIdentifying = parseInt(message.data);
                        if(parseInt(message.data) >= 100 || r >= 100) {
                            setCloseInterval();
                            setTimeout(function(){$(".progresstier").css("display", "none");p.setPercent(0);}, 1000 * 1);
                            flagnext = true;
                            loadresultcheck();
                            $(".next a").click();
                        }
                    }else if(result == "progress_calc_result"){
                        var r = p.setPercent(parseInt(message.data));
                        msgIdentifying = parseInt(message.data);
                        if(parseInt(message.data) >= 100 || r >= 100) {
                            setCloseInterval();
                            setTimeout(function(){$(".progresstier").css("display", "none");p.setPercent(0);document.getElementById("jgcx").click();}, 1000 * 1);
                        }
                    }else {
                        console.info("No Type");
                        console.info(message.data);
                    }
                }
            }
        });
    }
     // $.cookie("calc_panel_file","CPA_GYCX_Others_panel.xlsx");

    /*加载function*/
    var loadFun = function() {
        createUpload("cpa_upload", 0, "CPA");
        bootstraptab();
        p = new progress2();
        load_im();
        setProgress();
    }

    /*生成panle文件的前一步*/
    var uploadbefore = function() {
        setProgressStart(1000 * 3);
        $(".progresstier").css("display", "block");
        $("#modal_content").empty()
        var query_object = JSON.stringify({
            "company": $.cookie("token"),
            "uname": $.cookie('webim_user'),
            "businessType": "/uploadbefore"
        })
        ajaxData("/callhttpServer", query_object, "POST", function(d){
            if(d.status == "ok" && d.result.status == "success") {
                var result = d.result.result.result.split("#");
                $.each(result, function(i, v){
                    if(v != "") {
                        $("#modal_content").append("<label class='checkbox-inline'><input class='pnale_time' type='checkbox' value='"+v+"'> "+v+"</label>")
                    }
                });
                //打开拟态框，选择日期
                openModel();
            }
        },function(e){console.error(e)})
    }

    /*生成panle文件*/
    var uploadfile = function() {
        setProgressStart(1000 * 8);
        $(".progresstier").css("display", "block");
        var checked = ""
        $.each($(".pnale_time:checked"), function(i, v){checked += $(v).val() + "#"})
        var query_object = JSON.stringify({
            "company": $.cookie("token"),
            "yms": checked,
            "uname": $.cookie('webim_user'),
            "businessType": "/uploadfile"
        })
        // setTimeout(function () {
        //     $.tooltip('生成Panle文件成功，正在进行样本检查...', 5000, true);
        //     insertsampledata();
        // }, 3000)

        //生成时间太长，注释
        ajaxData("/callhttpServer", query_object, "POST", function(d) {
            if(d.status == "ok" && d.result.status == "success") {
                $.cookie("calc_panel_file",d.result.result.result);
                $.tooltip('生成Panle文件成功，正在进行样本检查...', 3000, true);
                //插入生成样本数据
                insertsampledata()
            }
        },function(e){console.error(e)})
    }

    /*插入生成样本数据*/
    var insertsampledata = function () {
        if($.cookie("calc_panel_file") != undefined && $.cookie("calc_panel_file") != null && $.cookie("calc_panel_file") != "") {
            var query_object = JSON.stringify({
                "company": $.cookie("token"),
                "filename": $.cookie("calc_panel_file"),
                "uname": $.cookie('webim_user'),
                "businessType": "/samplecheck"
            })

            // setTimeout(function(){
            //     $.tooltip('样本检查结束，正在进行检查结果展示...', 3000, true);
            //     //加载样本检查数据
            //     loadsamplecheck();
            //     flagnext = true;
            //     $(".next a").click();
            // }, 5000)



            ajaxData("/callhttpServer", query_object, "POST", function(d) {
                if(d.status == "ok" && d.result.status == "success") {
                    $.tooltip('样本检查结束，正在进行检查结果展示...', 5000, true);
                    //加载样本检查数据
                    loadsamplecheck();
                    flagnext = true;
                    $(".next a").click();
                }
            },function(e){console.error(e)})
        }
    }

    /*********样本检查 开始**********/
    /**加载样本检查查询数据*/
    var loadsamplecheck = function() {
        var markets = $('select[data-name="search-result-market"]').val();
        var dates = $('select[data-name="search-result-date"]').val();
        if(markets != null) {
            var dataMap = JSON.stringify({
                "company": $.cookie("token"),
                "market": markets,
                "date": dates
            });
            ajaxData("/samplecheck/check",dataMap ,"POST", function(d){
                if(d.status == "ok" && d.result.status == "success") {
                    samplecharts(d.result.result.result);
                }else {
                    $.tooltip('生成样本检查数据失败');
                }
            }, function(e){console.info(e)})
        }
    }

    /*样本检查图标*/
    var samplecharts = function(data) {
        //上传文件的样本医院未与标准库匹配到
        var hospList = function() {
            var lst = [];
            $.each(data.misMatchHospital, function(i, v){
                lst.push([(i + 1), v.Hosp_name, v.Province, v.City, v.City_level, "<a href=\"javascript:;\"><i class=\"fa fa-times text-danger text\"></i></a>"])
            });
            dataTableAjax(lst);
        }

        //当期|上期|去年同期;医院数量、产品数量、市场数量。
        var cel_data = function(){
            var cur_data = data.cur_data
            var ear_data = data.ear_data
            var las_data = data.las_data
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

        //当期|上期|去年同期;医院数量、产品数量、市场数量。折线图
        var cur12_data_HPM = function() {
            var symbolSize = 4;
            var x_data = [];
            var s_hosp_data = [];
            var s_prod_data = [];
            var s_mark_data = [];

            $.each(data.cur12_date, function (i, v) {
                x_data.push(v.Date);
                s_hosp_data.push(v.HospNum);
                s_prod_data.push(v.ProductNum);
                s_mark_data.push(v.MarketNum);
            })

            hosp_option = {
                title: {text: ''},
                tooltip: {trigger: 'axis', axisPointer: {type: 'none'}},
                legend: {x: 'left', y: 'middle', orient: 'vertical', data: ['数量'], show: false},
                grid: {bottom: '3%', left: '-35px', containLabel: true},
                xAxis: {type: 'category', show: false, boundaryGap: false, data: x_data},
                yAxis: {type: 'value', show: false},
                series: [{name: '数量', type: 'line', smooth: true, symbolSize: symbolSize,
                    itemStyle : {normal : {color:'#2BB89B', lineStyle:{color:'#2BB89B'}}},
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

        //折线图
        var cur12_las12_data = function () {

            var cur12_date = data.cur12_date
            var las12_date = data.las12_date

            var x_data = [];
            var y_curr12_sales = [];
            var y_last12_sales = [];
            var y_curr12_utils = [];
            var y_last12_utils = [];

            $.each(cur12_date, function(i, v){
                x_data.push(v.Date);
                if(v.Sales != 0.0){
                    y_curr12_sales.push((v.Sales/10000).toFixed(4));
                    y_curr12_utils.push((v.Units/10000).toFixed(4));
                }else{
                    y_curr12_sales.push(0.0000);
                    y_curr12_utils.push(0.0000);

                }
            })

            $.each(las12_date, function(i, v){
                if(v.Units != 0.0){
                    y_last12_sales.push((v.Sales/10000).toFixed(4));
                    y_last12_utils.push((v.Units/10000).toFixed(4));
                }else{
                    y_last12_sales.push(0.0000);
                    y_last12_utils.push(0.0000);
                }
            })

            var itemStyleColor = ['#9CDACD', '#9DC7E1'];
            Sales_Opt = {
                title: {text: '今年Vs去年(近12月销售额)',left: '50%',textAlign: 'center'},
                tooltip: {trigger: 'asix', axisPointer: {lineStyle: {color: '#ddd'}}, backgroundColor: 'rgba(255,255,255,1)', padding: [5, 10], textStyle: {color: '#7588E4'}, extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'},
                legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
                xAxis: {type: 'category', name: '日期', data: x_data, boundaryGap: false, splitLine: {show: false,interval: 'auto',lineStyle: {color: [itemStyleColor[0]]}}, axisTick: {show: false}, axisLabel: {margin: 10,textStyle: {fontSize: 14}}},
                yAxis: {type: 'value', name: '销售额(万)', axisTick: {show: false}, axisLabel: {margin: 10,textStyle: {fontSize: 14}}},
                series: [{name: '去年', type: 'line', smooth: true, showSymbol: false, symbol: 'circle', symbolSize: 6, data: y_last12_sales, areaStyle: {normal: {color: '#23c6c8'}}, itemStyle: {normal: {color: '#23c6c8'}}, lineStyle: {normal: {width: 3}}},
                        {name: '今年', type: 'line', smooth: true, showSymbol: false, symbol: 'circle', symbolSize: 6, data: y_curr12_sales, areaStyle: {normal: {color: '#1ab394'}}, itemStyle: {normal: {color: '#1ab394'}}, lineStyle: {normal: {width: 3}}
                }]
            };

            Units_Opt = {
                title: {text: '今年Vs去年(近12月销售量)',left: '50%',textAlign: 'center'},
                tooltip: {trigger: 'asix', axisPointer: {lineStyle: {color: '#ddd'}}, backgroundColor: 'rgba(255,255,255,1)', padding: [5, 10], textStyle: {color: '#7588E4'}, extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'},
                legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
                xAxis: {type: 'category', name: '日期', data: x_data, boundaryGap: false, splitLine: {show: false,interval: 'auto',lineStyle: {color: ['#23c6c8']}}, axisTick: {show: false}, axisLabel: {margin: 10,textStyle: {fontSize: 14}}},
                yAxis: {type: 'value', name: '销售量(万)', axisTick: {show: false}, axisLabel: {margin: 10,textStyle: {fontSize: 14}}},
                series: [{name: '去年', type: 'line', smooth: true, showSymbol: false, symbol: 'circle', symbolSize: 6, data: y_last12_utils, areaStyle: {normal: {color: '#23c6c8'}}, itemStyle: {normal: {color: '#23c6c8'}}, lineStyle: {normal: {width: 3}}},
                        {name: '今年', type: 'line', smooth: true, showSymbol: false, symbol: 'circle', symbolSize: 6, data: y_curr12_utils, areaStyle: {normal: {color: '#1ab394'}}, itemStyle: {normal: {color: '#1ab394'}}, lineStyle: {normal: {width: 3}}
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

        hospList()
        cel_data()
        cur12_data_HPM()
        cur12_las12_data()

    }

    /*样本检查change事件*/
    $("#sample_market,#sample_date").change(function(){loadsamplecheck();})
    /*********样本检查 结束**********/

    /*********计算 开始********/
    var calc = function() {
        if($.cookie("calc_panel_file") != undefined && $.cookie("calc_panel_file") != null && $.cookie("calc_panel_file") != "") {
            var dataMap = JSON.stringify({
                "company": $.cookie("token"),
                "filename": $.cookie("calc_panel_file"),
                "uname": $.cookie('webim_user'),
                "businessType": "/modelcalc"
            });
            ajaxData("/callhttpServer", dataMap, "POST", function(d){
                if(d.status == "ok" && d.result.status == "success") {
                    $(".progresstier").css("display", "block");
                    setProgressStart(1000 * 60);
                    p.setPercent(4);
                }
            }, function(e){console.error(e)});
        }else {
            $.tooltip('您生成的panel文件无效，请核对后重新生成！！！');
        }
    }
    /*********计算 结束********/

    /*********结果检查 开始********/
    /*加载结果检查数据*/
    var loadresultcheck = function() {
        var bar = echarts.init(document.getElementById('bar1'));
        var bar2 = echarts.init(document.getElementById('bar2'));
        var bar3 = echarts.init(document.getElementById('bar3'));

        bar.showLoading({
            text : '数据获取中',
            effect: 'whirling'
        });
        bar2.showLoading({
            text : '数据获取中',
            effect: 'whirling'
        });
        bar3.showLoading({
            text : '数据获取中',
            effect: 'whirling'
        });
        var markets = $('select[data-name="search-result-market"]').val();
        var dates = $('select[data-name="search-result-date"]').val();
        var dataMap = JSON.stringify({
            "company": $.cookie("token"),
            "market": markets.replace(/\s/g, ""),
            "date": dates
        });

        ajaxData("/resultcheck/linechart", dataMap, "POST", function(d){
            if(d.result.status == "success" && d.status == "ok"){
                echarts_bar1(d.result.result.result,bar);
            }else{
                $.tooltip(d.result.message);
            }
        }, function(e){console.error(e)})

        ajaxData("/resultcheck/histogram", dataMap, "POST", function(d){
            if(d.result.status == "success" && d.status == "ok"){
                echarts_bar23(d.result.result.result,bar2,bar3);
            }else{
                $.tooltip(d.result.message);
            }
        }, function(e){console.error(e)})

        var echarts_bar1 = function(result,bar){
            var x_data = [];
            var s_data1 = [];
            var s_data2 = [];

            $.each(result, function(i, v){
                x_data.push(v.Date);
                s_data1.push((v.f_sales/10000).toFixed(4));
                s_data2.push((v.f_sales/10000).toFixed(4));
            });

            var itemStyleColor = ['#1ab394', '#cacaca'];
            var option = {
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: [
                    {
                        name: '日期',
                        type: 'category',
                        data: x_data
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        name: '销售额(万)',
                    },
                    {
                        type: 'value',
                        name: 'Mono Unit Share',
                        show: false,
                        spliteLine: {show: false}
                    }
                ],
                series: [
                    {
                        name:'MAX',
                        type:'bar',
                        barWidth: 35,
                        data: s_data1,
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
                        data: s_data2,
                        itemStyle: {
                            normal: {
                                color: itemStyleColor[1]
                            }
                        }
                    }
                ]
            };
            bar = echarts.init(document.getElementById('bar1'));
            bar.hideLoading();
            bar.setOption(option);
            window.addEventListener("resize", function() {
                bar.resize();
            });
        }

        var echarts_bar23 = function(result,bar2,bar3){
            var colors = [{
                type: 'linear',
                x: 0, x2: 1, y: 0, y2: 0,
                colorStops: [{
                    offset: 0,
                    color: '#1ab394'
                }, {
                    offset: 1,
                    color: '#1ab394'
                }]
            }, {
                type: 'linear',
                x: 0, x2: 1, y: 0, y2: 0,
                colorStops: [{
                    offset: 0,
                    color: '#cacaca'
                }, {
                    offset: 1,
                    color: '#cacaca'
                }]
            }];

            var x_data = [];
            var echarts2_s_data1 = [];
            var echarts2_s_data2 = [];
            var echarts3_s_data2 = [];

            $.each(result.cur_top6, function(i, v){
                x_data.push(v.City);
                echarts2_s_data1.push((v.f_sales/10000).toFixed(4));
            });

            $.each(result.ear_top6, function(i, v){
                x_data.push(v.City);
                echarts2_s_data1.push((v.f_sales/10000).toFixed(4));
            });

            $.each(result.las_top6, function(i, v){
                x_data.push(v.City);
                echarts3_s_data2.push((v.f_sales/10000).toFixed(4));
            });

            var option2 = {
                color: colors,

                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',name: '城市',
                    data: x_data
                },
                yAxis: {type: 'value',name: '销售额(万)'},
                series: [{
                    name: '当期',
                    type: 'bar',
                    data: echarts2_s_data1
                }, {
                    name: '上期',
                    type: 'bar',
                    data: echarts2_s_data2
                }]
            };
            var option3 = {
                color: colors,
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',name: '城市',
                    data: x_data
                },
                yAxis: {type: 'value',name: '销售额(万)'},
                series: [{
                    name: '当期',
                    type: 'bar',
                    data: echarts2_s_data1
                }, {
                    name: '去年同期',
                    type: 'bar',
                    data: echarts3_s_data2
                }]
            };
            bar2 = echarts.init(document.getElementById('bar2'));
            bar3 = echarts.init(document.getElementById('bar3'));
            bar2.hideLoading();
            bar2.setOption(option2);
            bar3.hideLoading();
            bar3.setOption(option3);
            window.addEventListener("resize", function() {
                bar2.resize();
                bar3.resize();
            });
        }

    }
    /*结果检查change事件*/
    $("#result_check_market,#result_check_date").change(function(){
        loadresultcheck();
    });
    //点击进入历史按钮
    $("#goinghistory").click(function() {
        var dataMap = JSON.stringify({
            "company": $.cookie("token"),
            "uname": $.cookie('webim_user'),
            "businessType": "/datacommit"
        });
        ajaxData("/callhttpServer", dataMap, "POST", function(d){
            if(d.status == "ok" && d.result.status == "success") {
                $(".progresstier").css("display", "block");
                setProgressStart(1000 * 60);
                p.setPercent(4);
            }
        }, function(e){console.info(e)})
    });
    /*********结果检查 结束********/

    /*计算步骤TAB组件*/
    var bootstraptab = function() {
        var step = function(tab, navigation, index) {
            var $total = navigation.find('li').length;
            var $current = index+1;
            var $percent = ($current/$total) * 100;
            $('#progressWizard').find('.progress-bar').css('width', $percent+'%');
        }

        $('#progressWizard').bootstrapWizard({
            'nextSelector': '.next',
            'previousSelector': '.previous',
            onNext: function(tab, navigation, index) {
                if(index <= navigation.find('li').length - 1) {
                    if(index == 1){
                        //创建第二个上传组件
                        createUpload("gycx_upload", 1, "GYCX");
                        step(tab, navigation, index);
                    } else if(index == 2) {
                        if(!flagnext) {
                            uploadbefore();
                            return false;
                        }else {
                            setCloseInterval();
                            step(tab, navigation, index);
                            flagnext = false;
                            return true;
                        }
                    } else if(index == 3) {
                        if(!flagnext) {
                            calc();
                            return false;
                        }else {
                            setCloseInterval();
                            step(tab, navigation, index);
                            flagnext = false;
                            return true;
                        }
                    }
                }
            },
            onPrevious: function(tab, navigation, index) {
                if(index >= 0) {
                    flagnext = false;
                    step(tab, navigation, index);
                }
            }
        });

        $('#disabledTabWizard').bootstrapWizard({
            tabClass: 'nav nav-pills nav-justified nav-disabled-click',
            onTabClick: function(tab, navigation, index) {
                return false;
            }
        });
    }

    /*操作拟态框*/
    var openModel = function() {
        $("#panel_modal").modal('show');
        $("#create_panel").click(function(){
            uploadfile()
            $("#panel_modal").modal('hide');
        });
    }

    loadFun();

}(jQuery, this, document))

