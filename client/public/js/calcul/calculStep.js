/**
 * Created by yym on 11/7/17.
 */
(function ($, w) {
    //变量
    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    var f = new Facade();
    var fileNames = [];
    var ym_mkt_num = 0;
    var rotate_name = "";

    $('#secondStep').hide();
    $('#sampleResult').hide();
    $('#thirdStep').hide();
    $('#calculResult').hide();

    var toSecondStep = function () {
        rotate_name = "panel-rotate";
        $('#firstStep').hide();
        $('#secondStep').show();
        $('.scd-img')[0].src = "/assets/images/calculStep/step2.png";
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            rotate_name = "panel-rotate";
            $('#firstStep').hide();
            $('#secondStep').show();
            $('.scd-img')[0].src = "/assets/images/calculStep/step2.png";
        }
    };

    var toThirdStep = function () {
        rotate_name = "calc-rotate";
        $('#sampleResult').hide();
        $('#thirdStep').show();
        $('.thd-img')[0].src = "/assets/images/calculStep/step3.png";
    };

    var toFourthStep = function () {
        $('#firstStep').hide();
        $('#thirdStep').hide();
        $('.fth-img')[0].src = "/assets/images/calculStep/step4.png";
        toCalculResult()
    };

    $("#check-btn").click(function(){check_file()});
    $("#generat-panel-btn").click(function(){generat_panel_action()});
    $("#to-third-btn").click(function(){toThirdStep()});
    $("#calc-btn").click(function(){calc_action()});
    //测试
    // $("#snd-btn").click(function () {toSecondStep()});
    // $("#sample-btn").click(function () {toSampleResult()});
    // $("#thd-btn").click(function () {toThirdStep()});
    $("#calculInof").click(function(){toFourthStep()});
    // $("#test-show").click(function(){toFourthStep()});


    var check_file = function(){
        var info = $("#loadInof");
        info.empty();
        info.text("MAX正在解析您的文件...");
        prograssBar(10, 2000, 0);
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            var info = $("#loadInof");
            info.empty();
            info.text("MAX正在解析您的文件...");
            prograssBar(10, 2000, 0);
            var json = JSON.stringify({
                "businessType": "/calcYM",
                "company": company,
                "uid": $.cookie('uid'),
                "cpa": sourceMap.cpa,
                "gycx": sourceMap.gycx
            });
            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
        }else{
            layer.msg('上传数据不全');
        }
    };

    var generat_panel_action = function() {
        var ym_lst = [];
        $("#month_choose input[type=checkbox]:checked").each(function(){
            ym_lst.push($(this).val());
        });

        if(ym_lst.length < 1){
            layer.msg("请选择月份");
            return;
        }

        var json = JSON.stringify({
            "businessType": "/genternPanel",
            "company": company,
            "uid": $.cookie('uid'),
            "cpa": sourceMap.cpa,
            "gycx": sourceMap.gycx,
            "ym": ym_lst
        });
        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){
            layer.msg("开始生成panel");
            prograssBar(20, 6000, 10);
            $('#chooseMonth').modal('hide');
            var info = $("#loadInof");
            info.empty();
            info.text("MAX正在解析您的样本...");
        }, function(e){console.error(e)});
    };

    var calc_action = function() {
        var json = JSON.stringify({
            "businessType": "/modelcalc",
            "uid": $.cookie('uid')
        });
        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){
            layer.msg("开始计算");
            prograssBar(98, 20000, 0);
        }, function(e){console.error(e)});
    };

    function toSampleResult() {
        $('#secondStep').hide();
        $('#sampleResult').show();
        loadLineChart("lineChart1");
        loadLineChart("lineChart2");
        loadBarChart("barChart1");
        window.onload = function () {
            loadLineChart("lineChart1");
            loadLineChart("lineChart2");
            loadBarChart("barChart1");
        }
    }

    function toCalculResult() {
        $('#thirdStep').hide();
        $('#calculResult').show();
        w.step_chart.barLineChart().resize();
        w.step_chart.mapChart().resize();
        w.step_chart.barChart().resize();
        w.step_chart.query_data();
        // w.step_chart.barLineChart.resize();
        // bar_line_chart('market_trend');
        // map_chart('market_map');
        // bar_chart('market_bar');
    }

    var loadLineChart = function (id) {
        var lineChart = echarts.init(document.getElementById(id));
        var data = [220, 182, 191, 234, 190, 330, 310,50,200];
        var markLineData = [];
        for (var i = 1; i < data.length; i++) {
            markLineData.push([{
                xAxis: i - 1,
                yAxis: data[i - 1],
                value: (data[i] + data[i-1]).toFixed(2)
            }, {
                xAxis: i,
                yAxis: data[i]
            }]);
        }
        var option = {
            tooltip : {
                trigger: 'axis'
            },
            xAxis: {
                splitNumber : 12,
                data: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
            },
            yAxis: {},
            series: [{
                type: 'line',
                data : data,
                // markPoint: {
                //     data: [
                //         {type: 'max', name: '最大值'},
                //         {type: 'min', name: '最小值'}
                //     ]
                // },
                lineStyle : {
                    normal : {color : '#60B3AD'}
                },
                itemStyle : {
                    normal : {color : '#60B3AD'}
                },
                markLine: {
                    smooth: true,
                    effect: {
                        show: true
                    },
                    distance: 10,
                    label: {
                        normal: {
                            position: 'middle'
                        }
                    },
                    symbol: ['none', 'none'],
                    data: markLineData
                }
            }]
        };
        lineChart.setOption(option);
        $(window).resize(function() {
            lineChart.resize();
        });
    }
    var loadBarChart = function (id) {
        var chart = echarts.init(document.getElementById(id));
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
                    data : ['13:00', '13:05', '13:10', '13:15', '13:20', '13:25', '13:30','13:35','13:40','13:45','13:50','13:55'],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis : [
                {
                    // type : 'category',
                    // data : ['10','20','30','40'],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            series : [
                {
                    name:'直接访问',
                    type:'bar',
                    barWidth: '40%',
                    data:[1, 3, 2, 3, 4, 2, 1,3,3,2,3,2]
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
        chart.setOption(option);
        $(window).resize(function() {
            chart.resize();
        });
    }

    // var bar_line_chart = function(id) {
    //     var barLineChart = echarts.init(document.getElementById(id));
    //     var option = {
    //         title: {
    //         },
    //         tooltip: {
    //             trigger: 'axis'
    //         },
    //         // toolbox: {
    //         //     feature: {
    //         //         dataView: {
    //         //             show: true,
    //         //             readOnly: false
    //         //         },
    //         //         restore: {
    //         //             show: true
    //         //         },
    //         //         saveAsImage: {
    //         //             show: true
    //         //         }
    //         //     }
    //         // },
    //         grid: {
    //             containLabel: true
    //         },
    //         legend: {
    //             data: ['市场销量', '份额变化趋势']
    //         },
    //         xAxis: [{
    //             type: 'category',
    //             axisTick: {
    //                 alignWithLabel: true
    //             },
    //             data: ['2000', '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016']
    //         }],
    //         yAxis: [{
    //             type: 'value',
    //             name: '增速',
    //             min: 0,
    //             max: 50,
    //             position: 'right',
    //             axisLabel: {
    //                 formatter: '{value} %'
    //             }
    //         }, {
    //             type: 'value',
    //             name: '销量',
    //             min: 0,
    //             max: 3000,
    //             position: 'left'
    //         }],
    //         series: [{
    //             name: '增速',
    //             type: 'line',
    //             stack: '总量',
    //             label: {
    //                 normal: {
    //                     show: true,
    //                     position: 'top',
    //                 }
    //             },
    //             lineStyle: {
    //                 normal: {
    //                     width: 3,
    //                     shadowColor: 'rgba(0,0,0,0.4)',
    //                     shadowBlur: 10,
    //                     shadowOffsetY: 10
    //                 }
    //             },
    //             data: [1, 13, 37, 35, 15, 13, 25, 21, 6, 45, 32, 2, 4, 13, 6, 4, 11]
    //         }, {
    //             name: '销量',
    //             type: 'bar',
    //             yAxisIndex: 1,
    //             stack: '总量',
    //             label: {
    //                 normal: {
    //                     show: true,
    //                     position: 'top'
    //                 }
    //             },
    //             data: [209, 236, 325, 439, 507, 576, 722, 879, 938, 1364, 1806, 1851, 1931, 2198, 2349, 2460, 2735]
    //         }]
    //     };
    //     barLineChart.setOption(option);
    //     $(window).resize(function () {
    //         barLineChart.resize();
    //     })
    // }

    // var map_chart = function(id) {
    //     var map = echarts.init(document.getElementById(id));
    //     function randomData() {
    //         return Math.round(Math.random()*2500);
    //     }
    //
    //     var option = {
    //         tooltip: {
    //             trigger: 'item',
    //             formatter: '{b}'
    //         },
    //         visualMap: {//视觉映射组件
    //             type : "piecewise",
    //             splitNumber : 5,
    //             seriesIndex: 0,
    //             pieces : [
    //                 {gt: 2000, color : '#60B3AD'},            // (1500, Infinity]
    //                 {gt: 1500, lte: 2000, color : '#80CDC8'},  // (900, 1500]
    //                 {gt: 1000, lte: 1500, color : '#9DE0DC'},  // (310, 1000]
    //                 {gt: 500, lte: 1000, color : '#D2F5F2'},
    //                 {lt :500 ,color : '#D7D7D7'}
    //             ],
    //             min: 0,
    //             max: 2500,
    //             left: 'left',
    //             top: 'bottom',
    //             text: ['高','低'],           // 文本，默认为数值文本
    //             calculable: true
    //         },
    //         xAxis: {
    //             type: 'category',
    //             data: [],
    //             splitNumber: 1,
    //             show: false
    //         },
    //         yAxis: {
    //             position: 'right',
    //             min: 0,
    //             max: 20,
    //             splitNumber: 20,
    //             inverse: true,
    //             axisLabel: {
    //                 show: true
    //             },
    //             axisLine: {
    //                 show: false
    //             },
    //             splitLine: {
    //                 show: false
    //             },
    //             axisTick: {
    //                 show: false
    //             },
    //             data: []
    //         },
    //         series: [
    //             {
    //                 zlevel: 1,
    //                 name: '中国',
    //                 type: 'map',
    //                 mapType: 'china',
    //                 // selectedMode : 'multiple',
    //                 roam: false,//不进行缩放
    //                 left: 0,
    //                 right: '15%',
    //                 label: {
    //                     normal: {
    //                         show: true
    //                     },
    //                     emphasis: {
    //                         show: true
    //                     }
    //                 },
    //                 data:[
    //                     {name: '北京',value: randomData() },
    //                     {name: '天津',value: randomData() },
    //                     {name: '上海',value: randomData() },
    //                     {name: '重庆',value: randomData() },
    //                     {name: '河北',value: randomData() },
    //                     {name: '河南',value: randomData() },
    //                     {name: '云南',value: randomData() },
    //                     {name: '辽宁',value: randomData() },
    //                     {name: '黑龙江',value: randomData() },
    //                     {name: '湖南',value: randomData() },
    //                     {name: '安徽',value: randomData() },
    //                     {name: '山东',value: randomData() },
    //                     {name: '新疆',value: randomData() },
    //                     {name: '江苏',value: randomData() },
    //                     {name: '浙江',value: randomData() },
    //                     {name: '江西',value: randomData() },
    //                     {name: '湖北',value: randomData() },
    //                     {name: '广西',value: randomData() },
    //                     {name: '甘肃',value: randomData() },
    //                     {name: '山西',value: randomData() },
    //                     {name: '内蒙古',value: randomData() },
    //                     {name: '陕西',value: randomData() },
    //                     {name: '吉林',value: randomData() },
    //                     {name: '福建',value: randomData() },
    //                     {name: '贵州',value: randomData() },
    //                     {name: '广东',value: randomData() },
    //                     {name: '青海',value: randomData() },
    //                     {name: '西藏',value: randomData() },
    //                     {name: '四川',value: randomData() },
    //                     {name: '宁夏',value: randomData() },
    //                     {name: '海南',value: randomData() },
    //                     {name: '台湾',value: randomData() },
    //                     {name: '香港',value: randomData() },
    //                     {name: '澳门',value: randomData() }
    //                 ]
    //             }
    //         ]
    //     };
    //     map.setOption(option);
    //     $(window).resize(function () {
    //         map.resize();
    //     });
    //
    //     /**
    //      * 根据值获取线性渐变颜色
    //      * @param  {String} start 起始颜色
    //      * @param  {String} end   结束颜色
    //      * @param  {Number} max   最多分成多少分
    //      * @param  {Number} val   渐变取值
    //      * @return {String}       颜色
    //      */
    //     function getGradientColor (start, end, max, val) {
    //         var rgb = /#((?:[0-9]|[a-fA-F]){2})((?:[0-9]|[a-fA-F]){2})((?:[0-9]|[a-fA-F]){2})/;
    //         var sM = start.match(rgb);
    //         var eM = end.match(rgb);
    //         var err = '';
    //         max = max || 1
    //         val = val || 0
    //         if (sM === null) {
    //             err = 'start';
    //         }
    //         if (eM === null) {
    //             err = 'end';
    //         }
    //         if (err.length > 0) {
    //             throw new Error('Invalid ' + err + ' color format, required hex color');
    //         }
    //         var sR = parseInt(sM[1], 16),
    //             sG = parseInt(sM[2], 16),
    //             sB = parseInt(sM[3], 16);
    //         var eR = parseInt(eM[1], 16),
    //             eG = parseInt(eM[2], 16),
    //             eB = parseInt(eM[3], 16);
    //         var p = val / max;
    //         var gR = Math.round(sR + (eR - sR) * p).toString(16),
    //             gG = Math.round(sG + (eG - sG) * p).toString(16),
    //             gB = Math.round(sB + (eB - sB) * p).toString(16);
    //         return '#' + gR + gG + gB;
    //     }
    //
    //     /*setTimeout(function() {
    //      var TOPN = 25
    //
    //      var option = map.getOption()
    //      // 修改top
    //      option.grid[0].height = TOPN * 20
    //      option.yAxis[0].max = TOPN
    //      option.yAxis[0].splitNumber = TOPN
    //      option.series[1].data[0] = TOPN
    //      // 排序
    //      var data = option.series[0].data.sort(function(a, b) {
    //      return b.value - a.value
    //      })
    //
    //      var maxValue = data[0].value,
    //      minValue = data.length > TOPN ? data[TOPN - 1].value : data[data.length - 1].value
    //
    //      var s = option.visualMap[0].controller.inRange.color[0],
    //      e = option.visualMap[0].controller.inRange.color.slice(-1)[0]
    //      var sColor = getGradientColor(s, e, maxValue, minValue)
    //      var eColor = getGradientColor(s, e, maxValue, maxValue)
    //
    //      option.series[1].itemStyle.normal.color = new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
    //      offset: 1,
    //      color: sColor
    //      }, {
    //      offset: 0,
    //      color: eColor
    //      }])
    //
    //      // yAxis
    //      var newYAxisArr = []
    //      echarts.util.each(data, function(item, i) {
    //      if (i >= TOPN) {
    //      return false
    //      }
    //      var c = getGradientColor(sColor, eColor, maxValue, item.value)
    //      newYAxisArr.push({
    //      value: item.name,
    //      textStyle: {
    //      color: c
    //      }
    //      })
    //      })
    //      option.yAxis[0].data = newYAxisArr
    //      option.yAxis[0].axisLabel.formatter = (function(data) {
    //      return function(value, i) {
    //      if (!value) return ''
    //      return value + ' ' + data[i].value
    //      }
    //      })(data)
    //      map.setOption(option)
    //      }, 0);*/
    //
    // }

    // var bar_chart = function(id) {
    //     var barChart = echarts.init(document.getElementById(id));
    //     var option = {
    //         // title: {
    //         //     text: '需求驳回率排名',
    //         // },
    //         tooltip: {
    //             trigger: 'axis',
    //             axisPointer: {
    //                 type: 'shadow'
    //             }
    //         },
    //         legend: {
    //             data: ['市场总销售额', '产品销售额'],
    //             bottom:'10px',
    //             right:'10px',
    //             orient:'vertical'
    //         },
    //         grid: {
    //             left: '3%',
    //             right: '4%',
    //             bottom: '3%',
    //             containLabel: true
    //         },
    //         xAxis: {
    //             type: 'value',
    //             boundaryGap: [0, 0.01]
    //         },
    //         yAxis: {
    //             type: 'category',
    //             data: ['01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15', '01 江苏 15','01 江苏 15','01 江苏 15','01 江苏 15'],
    //             axisLabel: {
    //                 show: true,
    //                 interval: 'auto',
    //                 formatter: '{value}%',
    //             },
    //         },
    //         series: [
    //             {
    //                 name: '市场总销售额',
    //                 type: 'bar',
    //                 itemStyle: {
    //                     normal: {
    //                         color : '#ADADAD',//柱状图颜色
    //                         label: {
    //                             textStyle: {
    //                                 color: '#000000',
    //                                 fontSize: 3
    //                             },
    //                             show: true,
    //                             position: 'right',
    //                             formatter: '{c}%',
    //
    //
    //                         }
    //                     }
    //                 },
    //                 data: [8, 13, 15, 17, 22, 24, 28, 31, 35, 37],
    //
    //             },
    //             {
    //                 name: '产品销售额',
    //                 type: 'bar',
    //                 itemStyle: {
    //                     normal: {
    //                         color: '#60B3AD',
    //                         label: {
    //                             textStyle: {
    //
    //                                 color: '#000000',
    //                                 fontSize: 3
    //                             },
    //                             show: true,
    //                             position: 'right',
    //                             formatter: '{c}%',
    //
    //
    //                         }
    //                     }
    //                 },
    //                 data: [9, 21, 13, 22, 13, 16, 13, 15, 17, 22]
    //             }
    //         ]
    //     }
    //     barChart.setOption(option);
    //     $(window).resize(function () {
    //         barChart.resize();
    //     });
    //
    // }


    var show_loading = function() {
        $('.mask-layer').show();
        $('.loading').show();
    };
    var hide_loading = function() {
        $('.mask-layer').hide();
        $('.loading').hide();
    };



    query_company();
    load_cpa_source();
    load_gycx_source();

    //函数
    function query_company() {
        layui.use('layer', function () {
            var json = JSON.stringify(f.parameterPrefix.conditions({"user_token": $.cookie("user_token")}));
            f.ajaxModule.baseCall('/upload/queryUserCompnay', json, 'POST', function(r){
                if(r.status === 'ok') {
                    company = "fea9f203d4f593a96f0d6faa91ba24ba";//r.result.user.company;
                } else if (r.status === 'error') {
                    layer.msg(r.error.message);
                } else {
                    layer.msg('服务出错请联系管理员！');
                }
            }, function(e){console.error(e)})
        });
    }

    function load_cpa_source () {
        var name = 'cpa';
        var txt = '#txt-'+name;
        var sel = '#select-'+name;
        var fileName = '.snd-'+name;
        layui.use('upload', function () {
            var upload = layui.upload;
            upload.render({
                elem: sel,
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                multiple: false , // 多文件上传
                accept: 'file',
                exts: 'xlsx',
                before: function (obj) {
                    obj.preview(function (index, file, result) {
                        $(txt).val(file.name);
                        $(txt).addClass('disabled');
                        $(fileName).text(file.name)
                    });
                    if(!isCalcDone)
                        show_loading();
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        hide_loading();
                        $('.cpa-file').css("color", "#009688");
                        sourceMap.cpa = res.result[0];
                        return;
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {

                }
            });
        });
    }

    function load_gycx_source () {
        var name = 'gycx';
        var txt = '#txt-'+name;
        var sel = '#select-'+name;
        var fileName = '.snd-'+name;
        layui.use('upload', function () {
            var upload = layui.upload;
            upload.render({
                elem: sel,
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                multiple: false , // 多文件上传
                accept: 'file',
                exts: 'xlsx',
                before: function (obj) {
                    obj.preview(function (index, file, result) {
                        $(txt).val(file.name);
                        $(txt).addClass('disabled');
                        $(fileName).text(file.name);
                    });
                    if(!isCalcDone)
                        show_loading();
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        hide_loading();
                        $('.gycx-file').css("color", "#009688");
                        sourceMap.gycx = res.result[0];
                        toSecondStep();
                        return;
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {

                }
            });
        });
    }





    //web socket 回调
    w.socket.callback2obj(function(obj){
        var type = window.socket.getValue(obj)('type') !== 'Null' ? window.socket.getValue(obj)('type') : layer.msg("ws信息回调出错");
        switch (type) {
            case 'progress':
                progress(obj);
                break;
            case 'calc_ym_result':
                toSecondStep();
                calc_ym_result(obj);
                break;
            case 'progress_generat_panel':
                toSecondStep();
                progress_generat_panel(obj);
                break;
            case 'generat_panel_result':
                toSecondStep();
                generat_panel_result(obj);
                break;
            case 'progress_calc':
                toThirdStep();
                progress_calc(obj);
                break;
            case 'progress_calc_result':
                progress_calc_result(obj);
                break;
            case 'txt':
                txt(obj);
                break;
            case 'error':
                layui.use('layer', function () {
                    layer.msg(obj.error);
                });
                break;
            default:
                console.warn(obj.type);
                console.warn("No Type");
        }
    });

    var progress = function(obj) {
        console.info(obj);
    };

    var calc_ym_result = function (obj) {
        console.info(obj);

        var $ym_div = $('#month_choose');
        var sample_month = $('#sample_month');

        $ym_div.empty();
        sample_month.empty();
        $.each(obj.ym.split(","), function( index, ym ) {
            $ym_div.append('<div class="col-sm-3"><div class="checkbox"> <label> <input type="checkbox" value="'+ ym +'">'+ym+'</label> </div> </div>');
            sample_month.append(ym +"&nbsp;");
        });
        ym_mkt_num = obj.ym.split(",").length * obj.mkt.split(",").length;
        $('#chooseMonth').modal('show');
    };



    var panel_base_progress = 20;
    var progress_generat_panel = function (obj) {
        console.info(obj);
        var progress = window.socket.getValue(obj)('progress');

        prograssBar( Math.floor(panel_base_progress + progress / ym_mkt_num * 0.8 ) );

        if(progress === "100"){
            panel_base_progress = panel_base_progress + (100-20)/ym_mkt_num;
        }
    };

    var generat_panel_result = function (obj) {
        console.info(obj);
        layer.msg("panel生成完成");
        var result = JSON.parse(obj.result);
        $.each(result, function(ym, v1) {
            $.each(v1, function(mkt, panel_lst) {
                $.each(panel_lst, function(i, fname){
                    fileNames.push(fname);
                });
            });
        });
        toSampleResult();
    };



    var calc_base_progress = 0;
    var progress_calc = function(obj) {
        var progress = window.socket.getValue(obj)('progress');
        if(progress === "100"){
            calc_base_progress += 1;
            if(calc_base_progress === ym_mkt_num){
                prograssBar(100, 300, 98);
                alert("计算完成");
                isCalcDone = true;
            }
        }
    };

    var result_base_progress = 0;
    var progress_calc_result = function(obj) {
        var progress = window.socket.getValue(obj)('progress');
        if(progress === "100"){
            result_base_progress += 1;
            if(result_base_progress === ym_mkt_num){
                layer.msg("还原数据库完成");
                toFourthStep()
            }
        }
    };

    var txt = function(msg) {
        console.info(msg.data);
    };

    var prograssBar = function (end, time, begin) {
        time = (typeof time !== 'undefined') ?  time : 1;
        begin = (typeof begin !== 'undefined') ?  begin : end-1;

        var rotate = echarts.init(document.getElementById(rotate_name));
        var option = {
            animation: false,
            title: {
                text: (begin * 1) + '%',
                x: 'center',
                y: 'center',
                textStyle: {
                    color: '#fb358a',
                    fontSize: 30
                }
            },
            series: [{
                name: 'loading',
                type: 'pie',
                radius: ['30%', '32%'],
                hoverAnimation: false,
                label: {
                    normal: {
                        show: false
                    }
                },
                data: [
                    {
                        value: begin,
                        itemStyle: {
                            normal: {
                                color: '#fb358a'
                            }
                        }
                    }, {
                        value: 100 - begin,
                        itemStyle : {
                            normal : {
                                color: '#D5D8DC '
                            }
                        }
                    }
                ]
            }]
        };

        function increase() {
            return [{
                value: begin,
                itemStyle: {
                    normal: {
                        color: '#fb358a'
                    }
                }
            }, {
                value: 100 - begin,
                itemStyle : {
                    normal : {
                        color: '#D5D8DC '
                    }
                }
            }];
        }

        var interval = setInterval(function () {
            if (begin === end) {
                clearInterval(interval);
            } else if (begin === 100){
                clearInterval(interval);
            } else {
                ++begin;
            }

            rotate.setOption({
                title: {
                    text: begin + '%'
                },
                series: [{
                    name: 'loading',
                    data: increase()
                }]
            })
        }, time);

        rotate.setOption(option);
    };


}(jQuery, window));
