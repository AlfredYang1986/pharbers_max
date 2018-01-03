/**
 * Created by yym on 11/7/17.
 */
var calc_step = (function ($, w) {
    "use strict";

    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    var f = new Facade();
    var fileNames = [];
    var rotate_name = "";
    var aggregation_num = 0;
    var form, layer;

    $('#secondStep').hide();
    $('#sampleResult').hide();
    $('#thirdStep').hide();
    $('#calculResult').hide();

    $(function(){
        $('button[name="upload-next"]').click(function(){
            toSecondStep();
        });

        layui.use(['form', 'layer'], function(){
            form = layui.form; layer = layui.layer;
            form.on('submit(*)', function (data) {
                generat_panel_action(data, layer);
                return false;
            })
        });

        // $('#test-calc-result').click(function(){
        //     toFourthStep()
        // });
    });

    var toSecondStep = function () {
        rotate_name = "panel-rotate";
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
        $('#firstStep').hide();
        $('#calculResult').hide();
        $('#secondStep').hide();
        $('#thirdStep').show();
        $('.thd-img')[0].src = "/assets/images/calculStep/step3.png";
    };

    var toFourthStep = function () {
        $('#firstStep').hide();
        $('#sampleResult').hide();
        $('#thirdStep').hide();
        $('.fth-img')[0].src = "/assets/images/calculStep/step4.png";
        toCalculResult();
    };

    $("#check-btn").click(function(){check_file()});
    // 无用了
    $("#generat-panel-btn").click(function(){generat_panel_action()});

    $("#to-third-btn").click(function(){toThirdStep()});
    $("#calc-btn").click(function(){calc_action()});

    var check_file = function(){
        var info = $("#loadInof");
        info.empty();
        info.text("MAX正在解析您的文件...");
        prograssBar(10, 2, 0);
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            info.empty();
            info.text("MAX正在解析您的文件...");
            prograssBar(10, 2, 0);
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

    var generat_panel_action = function(data, layer) {
        var ym_lst =[];

        $.each(data.field, function(i, v){ym_lst.push(v);});

        if (ym_lst.length < 1){
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
            prograssBar(20, 6, 10);
            layer.closeAll();
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
            prograssBar(99, 60, 0);
        }, function(e){console.error(e)});
    };

    function toSampleResult() {
        $('#firstStep').hide();
        $('#secondStep').hide();
        $('#calculResult').hide();
        $('#thirdStep').hide();
        $('#sampleResult').show();

        w.sample.query_selectBox();
        w.sample.lineChart1().resize();
        w.sample.lineChart2().resize();
        w.sample.barChart().resize();
    }

    function toCalculResult() {
        $('#firstStep').hide();
        $('#secondStep').hide();
        $('#sampleResult').hide();
        $('#thirdStep').hide();
        $('#calculResult').show();

        w.step_chart.barLineChart().resize();
        w.step_chart.mapChart().resize();
        w.step_chart.barChart().resize();
        w.step_chart.query_select();
        w.step_chart.query_data();
    }

    query_company();
    load_cpa_source();
    load_gycx_source();

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
                        if(sourceMap.cpa !== '' && sourceMap.gycx !== '') {
                            var $btn = $('button[name="upload-next"]');
                            $btn.attr({'class': 'layui-btn layui-btn-radius', 'disabled': false})
                        }
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
                        if(sourceMap.cpa !== '' && sourceMap.gycx !== '') {
                            var $btn = $('button[name="upload-next"]');
                            $btn.attr({'class': 'layui-btn layui-btn-radius', 'disabled': false})
                        }
                        // toSecondStep();
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
            case 'progress_calc_result_done':
                progress_calc_result_done(obj);
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

    //未显示要计算的月份
    var show_panel_none_month = function() {
        var option = {
            title: '未显示要计算的月份？',
            offset: '160px',
            area: ['35%', '333px'],
            content: $('#none-show-time').html(),
            btns: ['返回选择月份', '重新上传'],
            btn1: function(index) {
                layer.close(index);
            },
            btn2: function() {
                w.location = '/calcul/step'
            }
        };

        f.alertModule.open ( option );
    };

    var calc_ym_result = function (obj) {
        console.info(obj);

        var $time_lst = $('#panel-show-time').find('div[name="time-lst"]').empty();

        if (obj.ym === "0") {
            alert("无法解析月份，请刷新重试")
        } else {
            $.each(obj.ym.split(","), function( index, ym ) {
                $time_lst.append('<input type="checkbox" name="'+ ym +'" title="' + ym + '" value="' + ym + '" lay-skin="primary">')
            });
            var option = {title: '选择月份', offset: '160px', content: $('#panel-show-time').html()};
            f.alertModule.open(option);
            form.render('checkbox');
        }
    };

    var progress_generat_panel = function (obj) {
        console.info(obj);
        var progress = window.socket.getValue(obj)('progress');
        prograssBar(progress);
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
        setTimeout(function(){toSampleResult()}, 1000);
    };

    var progress_calc = function(obj) {
        console.info(obj);
        window.socket.getValue(obj)('progress');
    };

    var progress_calc_result = function(obj){
        console.info(obj);
        layer.msg("计算完成");
        toFourthStep()
    };

    var progress_calc_result_done = function(obj){

        if (parseInt(obj.progress) === 100) {
            aggregation_num = aggregation_num + 1
        } else {}

        if (parseInt(aggregation_num) === parseInt(w.step_chart.table_num()) && parseInt(obj.progress) === 100) {
            layer.msg("保存结束");
            hide_loading();
            layer.msg("1秒后将自动跳转，历史查询界面");
            setTimeout(function(){w.location = '/calcul/home'}, 1500);
        } else {
            //TODO: 后续接入进度条
        }
    };

    var txt = function(msg) {
        console.info(msg.data);
    };

    var prograssBar = function (e, t, b) {
        var end = parseInt(e);
        var time = (typeof t !== 'undefined') ?  parseInt(t)*1000 : 1;
        var begin = (typeof b !== 'undefined') ?  parseInt(b) : end-1;

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

    return {
        "show_panel_none_month": show_panel_none_month
    }

})(jQuery, window);