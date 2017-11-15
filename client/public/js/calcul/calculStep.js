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

    var show_loading = function() {
        $('.mask-layer').show();
        $('.loading').show();
    };
    var hide_loading = function() {
        $('.mask-layer').hide();
        $('.loading').hide();
    };

    $("#check-btn").click(function(){check_file()});
    $("#generat-panel-btn").click(function(){generat_panel_action()});
    $("#to-third-btn").click(function(){toThirdStep()});
    $("#calc-btn").click(function(){calc_action()});

    query_company();
    load_cpa_source();
    load_gycx_source();

    //函数
    function query_company() {
        layui.use('layer', function () {});
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

    var toSecondStep = function () {
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
        $('#thirdStep').hide();
        $('.fth-img')[0].src = "/assets/images/calculStep/step4.png";
    };

    var check_file = function(){
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            var info = $("#loadInof");
            info.empty();
            info.text("MAX正在解析您的文件...");
            prograssBar(10, 2000, 0);
            var json = JSON.stringify({
                "businessType": "/calcYM",
                "company": company,
                "user": $.cookie('uid'),
                "cpa": sourceMap.cpa,
                "gycx": sourceMap.gycx
            });
            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
        }else{
            layer.msg('上传数据不全');
        }
    };

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
                layer.msg(obj.error);
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
            "user": $.cookie('uid'),
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

    function toSampleResult() {
        $('#secondStep').hide();
        $('#sampleResult').show();
    }

    var calc_action = function() {
        var json = JSON.stringify({
            "businessType": "/modelcalc",
            "company": company,
            "filename": fileNames,
            "uid": $.cookie('uid'),
            "imuname": ""//遗留症，必须传，重构时清理
        });
        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){
            layer.msg("开始计算");
            prograssBar(98, 210000, 0);
        }, function(e){console.error(e)});
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

    loadMainChart(82, 'mainChart', '文档总体可信度');
    loadMainChart(18, 't-char1', '文档总体可信度');
    loadMainChart(18, 't-char2', '文档总体可信度');
    loadMainChart(18, 't-char3', '文档总体可信度');
    loadLineChart('t1');
    loadLineChart('t2');
    loadLineChart('t3');
    sample_bar('bar1');

}(jQuery, window));
