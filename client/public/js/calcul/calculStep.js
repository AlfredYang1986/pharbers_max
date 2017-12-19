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
    var mkt_lst = [];
    var ym_mkt_num = 1;
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
    /// $("#snd-btn").click(function () {toSecondStep()});
    /// $("#sample-btn").click(function () {toSampleResult()});
    /// $("#thd-btn").click(function () {toThirdStep()});
    /// $("#calculInof").click(function(){toFourthStep()});
    /// $("#test-show").click(function(){toFourthStep()});


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

        ym_mkt_num = ym_lst.length * mkt_lst.length;
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
            prograssBar(99, 60000, 0);
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
        w.step_chart.query_select();
        w.step_chart.query_data();
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
    };

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
    };

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
            case 'generate_panel_result':
                toSecondStep();
                generate_panel_result(obj);
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
                console.info(obj);
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
        if(obj.ym === "0"){
            alert("无法解析月份，请刷新重试")
        }else{
            $.each(obj.ym.split(","), function( index, ym ) {
                $ym_div.append('<div class="col-sm-3"><div class="checkbox"> <label> <input type="checkbox" value="'+ ym +'">'+ym+'</label> </div> </div>');
                sample_month.append(ym +"&nbsp;");
            });
            $.each(obj.mkt.split(","), function( index, mkt ) {
                mkt_lst.push(mkt)
            });
            $('#chooseMonth').modal('show');
        }
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

    var generate_panel_result = function (obj) {
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
        console.info(obj);
        var progress = window.socket.getValue(obj)('progress');
        if(progress === "100"){
            calc_base_progress += 1;
            if(calc_base_progress === ym_mkt_num){
                prograssBar(100, 300, 99);
                alert("计算完成");
                isCalcDone = true;
            }
        }
    };

    var result_base_progress = 0;
    var progress_calc_result = function(obj) {
        console.info(obj);
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
