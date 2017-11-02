/**
 * Created by clock on 17-9-28.
 */

(function ($) {
    'use strict';
    var total = $('ul[pharbers-ul="calc-tab"]').find('li').length;
    var current_li = 0;
    var tab_arr = ['panel-li', 'calc-li', 'result-li'];
    $('li[pharbers-filter="calc"]').addClass("layui-this");
    var company = "";
    var files;
    var isCalcDone = false;
    var f = new Facade();
    var tables = [];
    var uuids = [];
    var sourceMap = {"cpa":"","gycx":""};

    layui.use('element', function () {
        var element = layui.element;
        element.on('tab(step)', function (data) {
            if (data.index === 0) {
                current_li = 0;
            } else if (data.index === 1) {
                current_li = 1;
            } else if (data.index === 2) {
                current_li = 2;
            } else {
            }
        });
        load_source_tab('#panel-lst');
        // load_panel_tab('#panel-lst');
        callback();
        setProgress();
    });

    var change_tab = function (btn, nextBtn, preBtn, disableCss) {
        if (btn === "pre" && current_li > 0) {
            current_li = current_li - 1;
            $(nextBtn).removeClass(disableCss);
            binding('#next-btn.operation', 'next', '#next-btn', '#previous-btn', 'layui-btn-disabled');
            if (current_li === 0) {
                $(preBtn).addClass(disableCss);
                unbinding('#previous-btn.operation', 'click');
            }
        } else if (btn === "next" && current_li < 2) {
            var panel_lst = $('#panel-lst').children();
            if(panel_lst.length === 0)
                return;
            current_li = current_li + 1;
            binding('#previous-btn.operation', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
            $(preBtn).removeClass(disableCss);
            if (current_li === 2) {
                $(nextBtn).addClass(disableCss);
                load_result_check_tab();
                unbinding('#next-btn.operation', 'click');
            }
        }
        setProgress();
    };

    var callback = function() {
        var conn = window.im_object.conns();
        var temp = 0;
        conn.listen({
            onOpened: function ( message ) {console.info("im 连接成功")},
            onClosed: function ( message ) {},         //连接关闭回调
            onTextMessage: function ( message ) {
                var num = $('#panel-lst').children().length;
                var ext = message.ext;
                if(ext !== null) {
                    var reVal = window.im_object.searchExtJson(ext)('type') !== 'Null' ? window.im_object.searchExtJson(ext)('type') : window.im_object.searchExtJsonForElement(ext.elems)('type');
                    if(reVal === 'progress') {
                        console.info(message.data);
                    } else if(reVal === 'progress_calc') {
                        var fileName = window.im_object.searchExtJson(ext)('file') !== 'Null' ? window.im_object.searchExtJson(ext)('file') : window.im_object.searchExtJsonForElement(ext.elems)('file');
                        var step = window.im_object.searchExtJson(ext)('step') !== 'Null' ? window.im_object.searchExtJson(ext)('step') : window.im_object.searchExtJsonForElement(ext.elems)('step');
                        var lay_filter = 'calc-progress-' + fileName;
                        var span = $('#panel-calc-lst').find('div[lay-filter=' + lay_filter + ']').parent().prev().children('span');
                        span.text(step);
                        if(message.data === "100") {
                            temp = temp + 1;
                            uuids.push({"fileName": fileName, "uuid" : window.im_object.searchExtJsonForElement(ext.elems)('uuid')});
                            tables.push(window.im_object.searchExtJsonForElement(ext.elems)('table'));
                            if(num === temp){
                                $('.mask-layer').hide();
                                $('.loading').hide();
                                isCalcDone = true;temp = 0;
                            }
                        }
                        setProgress(lay_filter, message.data);
                    } else if(reVal === 'progress_calc_result') {
                        var uuid = window.im_object.searchExtJson(ext)('uuid');
                        var lay_uuid = 'calc-progress-' + uuid;
                        var step_result = window.im_object.searchExtJson(ext)('step');
                        var span_result = $('.confrim-calc-lst').eq(1).find('div[lay-filter=' + lay_uuid + ']').parent().prev().children('span');
                        span_result.text(step_result);
                        if(message.data === "100") {
                            temp = temp + 1;
                            if(num === temp){
                                $('.mask-layer').hide();
                                $('.loading').hide();
                                temp = 0;uuids = [];tables = [];
                                $('li[pharbers-filter="history"]').click();
                            }
                        }
                        setProgress(lay_uuid, message.data);
                    } else if(reVal === 'txt') {
                        console.info(message.data);
                        if(message.data === "201705"){
                            var json = JSON.stringify({
                                "businessType": "/genternPanel",
                                "company": "fea9f203d4f593a96f0d6faa91ba24ba",
                                "user": $.cookie('webim_user'),
                                "cpa": sourceMap.cpa,
                                "gycx": sourceMap.gycx,
                                "ym": [message.data]
                            });
                            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
                        }else{
                            var panelList = message.data.split(',');
                            for(var i=0 ; i<panelList.length ; i++){
                                var json = JSON.stringify({"businessType": "/modelcalc",
                                    "company": "fea9f203d4f593a96f0d6faa91ba24ba",
                                    "filename": panelList[i],
                                    "uname": $.cookie('webim_user')
                                });
                                f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
                            }
                        }
                    } else {
                        console.warn(message.ext);
                        console.warn("No Type");
                        console.warn(message.data);
                    }
                }
            },    //收到文本消息
            onOnline: function () {},                  //本机网络连接成功
            onOffline: function () {},                 //本机网络掉线
            onError: function ( message ) { console.error(message) }          //失败回调
        });
    }

    var setProgress = function (flag, num) {
        layui.use("element", function () {
            var element = layui.element;
            var progress = (((current_li + 1) / total) * 100) + "%";
            element.tabChange('step', tab_arr[current_li]);
            element.progress('calc-progress-step', progress);
            element.progress(flag, num + '%');
        });
    }

    var load_panel_tab = function (uploadid) {
        layui.use('upload', function () {
            var upload = layui.upload;
            var panel_lst = $(uploadid);
            var panel_calc_lst = $('#panel-calc-lst');
            var confrim_calc_lst = $('.confrim-calc-lst');

            upload.render({
                elem: '#select-panel-btn',
                url: '/panel/upload',
                drag: false,
                data: {"company": company} ,
                auto: false, //选择文件后不自动上传
                multiple: true ,
                accept: 'file',
                exts: 'xlsx',
                bindAction: '#next-btn' ,//#upload-panel-btn
                before: function () {
                    query_company();
                    if(!isCalcDone) {
                        $('.mask-layer').show();
                        $('.loading').show();
                    }
                },
                choose: function (obj) {
                    files = obj.pushFile();
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">'
                            , '<td>' + file.name + '</td>'
                            , '<td>' + (file.size / 1024 / 1024).toFixed(1) + 'MB</td>'
                            , '<td>等待上传</td>'
                            , '<td class="opretion">'
                            , '<button class="layui-btn layui-btn-mini demo-reload layui-hide">重传</button>'
                            , '<button class="layui-btn layui-btn-mini layui-btn-danger demo-delete">删除</button>'
                            , '</td>'
                            , '</tr>'].join(''));

                        tr.find('.demo-reload').on('click', function () {
                            obj.upload(index, file);
                        });

                        tr.find('.demo-delete').on('click', function () {
                            delete files[index];
                            tr.remove();
                        });
                        panel_calc_lst.empty();
                        panel_lst.append(tr);
                        panel_calc_lst.append(panel_lst.children().clone());
                    });
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        var fileName = res.result[0];
                        var tr = panel_lst.find('tr#upload-' + index);
                        var tds = tr.children();
                        tds.eq(2).html('<span style="color: #008B7D;">上传完成</span>');
                        tds.eq(3).html('<i class="layui-icon" style="font-size: 30px; color: #008B7D;">&#xe618;</i> ');

                        var calc_tr = panel_calc_lst.find('tr#upload-' + index);
                        var calc_tds = calc_tr.children();
                        calc_tds.eq(2).html('<span style="color: #008B7D;">等待计算</span>');
                        var lay_filter = 'calc-progress-' + fileName;

                        var p = '<div class="layui-progress" lay-filter= '+ lay_filter +'>\n' +
                            '    <div class="layui-progress-bar layui-bg-green" lay-percent="0%"></div>\n' +
                            '</div>';
                        calc_tds.eq(3).html(p);
                        delete files[index];

                        confrim_calc_lst.empty();
                        confrim_calc_lst.append(panel_calc_lst.children().clone());

                        var json = JSON.stringify({"businessType": "/modelcalc",
                                                    "company": company,
                                                    "filename": res.result[0],
                                                    "uname": $.cookie('webim_user')
                                                  });
                        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
                        return;
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                    var tr = panel_lst.find('tr#upload-' + index);
                    var tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        });
    };

    var load_source_tab = function (uploadid) {
        layui.use('upload', function () {
            var upload = layui.upload;
            var source_lst = $(uploadid);

            upload.render({
                elem: '#select-panel-btn',
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                auto: false, //选择文件后不自动上传
                multiple: true ,
                accept: 'file',
                exts: 'xlsx',
                bindAction: '#next-btn' ,//#upload-panel-btn
                before: function () {
                    query_company();
                    if(!isCalcDone) {
                        $('.mask-layer').show();
                        $('.loading').show();
                    }
                },
                choose: function (obj) {
                    files = obj.pushFile();
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">'
                            , '<td>' + file.name + '</td>'
                            , '<td>' + (file.size / 1024 / 1024).toFixed(1) + 'MB</td>'
                            , '<td>等待上传</td>'
                            , '<td class="opretion">'
                            , '<button class="layui-btn layui-btn-mini demo-reload layui-hide">重传</button>'
                            , '<button class="layui-btn layui-btn-mini layui-btn-danger demo-delete">删除</button>'
                            , '</td>'
                            , '</tr>'].join(''));

                        tr.find('.demo-reload').on('click', function () {
                            obj.upload(index, file);
                        });

                        tr.find('.demo-delete').on('click', function () {
                            delete files[index];
                            tr.remove();
                        });
                        source_lst.append(tr);
                    });
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        var tr = source_lst.find('tr#upload-' + index);
                        var tds = tr.children();
                        tds.eq(2).html('<span style="color: #008B7D;">上传完成</span>');
                        tds.eq(3).html('<i class="layui-icon" style="font-size: 30px; color: #008B7D;">&#xe618;</i> ');

                        if(sourceMap.gycx === "")
                            sourceMap.gycx = res.result[0];
                        else if(sourceMap.cpa === "")
                            sourceMap.cpa = res.result[0];

                        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
                            var json = JSON.stringify({
                                "businessType": "/calcYM",
                                "company": "fea9f203d4f593a96f0d6faa91ba24ba",
                                "user": $.cookie('webim_user'),
                                "cpa": sourceMap.cpa,
                                "gycx": sourceMap.gycx
                            });
                            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
                        }

                        return;
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                    var tr = source_lst.find('tr#upload-' + index);
                    var tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        });
    };

    var load_result_check_tab = function () {

        $('#goinghistory').click(function(){
            $('.mask-layer').show();
            $('.loading').show();
            f.alertModule.content($('#confrims').html(), null, null, '确认列表', function(){$('#confrims').hide();});
            var confrimLst = $('.confrim-calc-lst');
            $.each(uuids, function(i, v){
                var div = confrimLst.eq(1).find('div[lay-filter="calc-progress-' + v.fileName + '"]');
                div.attr('lay-filter', 'calc-progress-' + v.uuid);
            });

            $.each(uuids, function(i, v){
                var json = JSON.stringify({"businessType": "/datacommit",
                    "company": company,
                    "uuid": v.uuid,
                    "uname": $.cookie('webim_user')
                });
                f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
            });
        });

        layui.use('form', function () {
            var form = layui.form;
            var json = JSON.stringify(f.parameterPrefix.conditions({
                "user_token": $.cookie("user_token"),
                // "tables": ["fea9f203d4f593a96f0d6faa91ba24ba0645320d-8987-41b9-851c-095988f0bd68","fea9f203d4f593a96f0d6faa91ba24bad25c431e-9993-4dfa-99af-163157aecb2a"],
                "tables": tables
            }));

            load_select_box(form, json);

            form.render();

            form.on('select(result-check-year-and-market)', function(data){
                var json = JSON.stringify(f.parameterPrefix.conditions({
                    "user_token": $.cookie("user_token"),
                    // "tables": ["fea9f203d4f593a96f0d6faa91ba24ba0645320d-8987-41b9-851c-095988f0bd68","fea9f203d4f593a96f0d6faa91ba24bad25c431e-9993-4dfa-99af-163157aecb2a"],
                    "tables": tables,
                    "marketWithYear": data.value
                }));
                $('.mask-layer').show();
                $('.loading').show();
                var selectobj = data.value.split("-");
                f.ajaxModule.baseCall("calc/querySalesVsShare", json, "POST", function(r){
                    $('.mask-layer').hide();
                    $('.loading').hide();
                    load_bar1_chart(r.result.condition, selectobj);
                }, function(e){console.error(e)});

                f.ajaxModule.baseCall("calc/queryCurVsPreWithCity", json, "POST", function(r){
                    load_bar2_chart(r.result.condition);
                }, function(e){console.error(e)});


                f.ajaxModule.baseCall("calc/queryWithYearForCurVsPre", json, "POST", function(r){
                    load_bar3_chart(r.result.condition);
                }, function(e){console.error(e)});
            });
        });

        function load_select_box(form, json) {
            $('.mask-layer').show();
            $('.loading').show();

            f.ajaxModule.baseCall("calc/querySalesVsShare", json, "POST", function(r){
                $('.mask-layer').hide();
                $('.loading').hide();
                $('#result-check-year-and-market').empty();
                $.each(r.result.condition.select_values, function (i, v) {
                    var values = v.Date + '-' + v.Market;
                    if(i === 0) {
                        $('#result-check-year-and-market').append('<option value=' + values + ' selected >' + values + '</option>');
                    } else {
                        $('#result-check-year-and-market').append('<option value=' + values + '>' + values + '</option>');
                    }
                });
                form.render();
                load_bar1_chart(r.result.condition, $('#result-check-year-and-market').val().split("-"));
            }, function(e){console.error(e)});

            f.ajaxModule.baseCall("calc/queryCurVsPreWithCity", json, "POST", function(r){
                load_bar2_chart(r.result.condition);
            }, function(e){console.error(e)});

            f.ajaxModule.baseCall("calc/queryWithYearForCurVsPre", json, "POST", function(r){
                load_bar3_chart(r.result.condition);
            }, function(e){console.error(e)});
        }

        function load_bar1_chart(d, selectobj) {
            var xAxisDateArray = [];
            var seriesDataArray = [];
            var shareDataArray = [];
            var sales_vs_share_chart = echarts.init(document.getElementById('sales-vs-share'));
            var key = md5(selectobj[0] + selectobj[1]);
            $.each(d.sales_vs_share.history[key], function(i, v){
                xAxisDateArray.push(v.Date);
                seriesDataArray.push(v.Sales);
                shareDataArray.push(v.Share);
            });

            $.each(d.sales_vs_share.cur[key], function(i, v) {
                xAxisDateArray.push(v.Date);
                seriesDataArray.push(v.Sales);
                shareDataArray.push(v.Share);
            });
            console.info(shareDataArray)
            var itemStyleColor = ['#1ab394', '#cacaca'];
            var option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer : {
                        type : 'shadow'
                    }
                },
                xAxis: {
                    name: '日期',
                    data: xAxisDateArray
                },
                yAxis: [
                    {
                        name: '销售额(万)',
                        type: 'value'
                    },
                    {
                        name: 'Mono Unit Share',
                        type: 'value',
                        show: false,
                        spliteLine: {show: false}
                    }
                ],
                series: [{
                    name:'销售额',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: itemStyleColor[0]
                        }
                    },
                    data: seriesDataArray
                },{
                    name:'MAX Mono Share',
                    type:'line',
                    itemStyle: {
                        normal: {
                            color: itemStyleColor[1]
                        }
                    },
                    yAxisIndex: 1,
                    data: shareDataArray
                }]
            };
            sales_vs_share_chart.setOption(option);

        }

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

        function load_bar2_chart(d) {
            var xAxisDataArray = [];
            var curDataArray = [];
            var preDataArray = [];
            $.each(d.curLst, function(i, v){
                xAxisDataArray.push(v.City);
                curDataArray.push(v.Sales);
            });

            $.each(d.preLst, function(i, v){
                preDataArray.push(v.Sales);
            });
            var cur_vs_pre_city_chart = echarts.init(document.getElementById('cur-vs-pre-city'));
            // 指定图表的配置项和数据
            var option = {
                color: colors,
                tooltip: {
                    trigger: 'axis',
                    axisPointer : {
                        type : 'shadow'
                    }
                },
                legend: {
                    data:['当月','上月']
                },
                xAxis: {
                    type: 'category',
                    name: '城市',
                    data: xAxisDataArray,
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                yAxis: {
                    name: '销售额(万)',
                    type: 'value'
                },
                series: [
                    {
                        name: "当月",
                        type: 'bar',
                        data: curDataArray
                    },{
                        name: "上月",
                        type: 'bar',
                        data: preDataArray
                    }
                ]
            };
            cur_vs_pre_city_chart.setOption(option);
        }

        function load_bar3_chart(d) {
            var bar3_chart = echarts.init(document.getElementById('bar3'));
            var xAxisDataArray = [];
            var curDataArray = [];
            var preDataArray = [];
            $.each(d.curLst, function(i, v){
                xAxisDataArray.push(v.City);
                curDataArray.push(v.Sales);
            });

            $.each(d.preLst, function(i, v){
                preDataArray.push(v.Sales);
            });

            // 指定图表的配置项和数据
            var option = {
                color: colors,
                tooltip: {
                    trigger: 'axis',
                    axisPointer : {
                        type : 'shadow'
                    }
                },
                legend: {
                    data:['本次计算','去年同期']
                },
                xAxis: {
                    name: '日期',
                    data: xAxisDataArray
                },
                yAxis: {
                    name: '销售额(万)',
                    type: 'value'
                },
                series: [
                    {
                        name: "本次计算",
                        type: 'bar',
                        data: curDataArray
                    },{
                        name: "去年同期",
                        type: 'bar',
                        data: preDataArray
                    }
                ]
            };
            bar3_chart.setOption(option);
        }
    }

    var query_company = function() {
        layui.use('layer', function () {});
        var json = JSON.stringify(f.parameterPrefix.conditions({"user_token": $.cookie("user_token")}));
        f.ajaxModule.baseCall('/upload/queryUserCompnay', json, 'POST', function(r){
            if(r.status === 'ok') {
                company = r.result.user.company;
            } else if (r.status === 'error') {
                layer.msg(r.error.message);
            } else {
                layer.msg('服务出错请联系管理员！');
            }
        }, function(e){console.error(e)})
    }

    var binding = function(opera, btn, preBtn, postBtn, cssClass) {
        unbinding(opera, 'click');
        $(opera).bind('click', function() {change_tab(btn, preBtn, postBtn, cssClass)});
    };

    var unbinding = function(id, operation) {
        $(id).unbind(operation)
    };

    binding('#previous-btn', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
    binding('#next-btn', 'next', '#next-btn', '#previous-btn', 'layui-btn-disabled');

}(jQuery))

