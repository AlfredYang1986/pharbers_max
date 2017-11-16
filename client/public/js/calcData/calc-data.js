/**
 * Created by clock on 17-9-28.
 */

(function ($) {
    'use strict';
    $('li[pharbers-filter="calc"]').addClass("layui-this");
    var total = $('ul[pharbers-ul="calc-tab"]').find('li').length;
    $('#result-check-year-and-market').empty();
    var current_li = 0;
    var tab_arr = ['source-li', 'panel-li', 'calc-li', 'result-li'];
    var isCalcDone = false;
    var f = new Facade();

    var cpaFile;
    var gycFile;
    var company = "";
    var tables = [];
    var uuids = [];

    var sourceMap = {"cpa":"","gycx":""};
    var isSelectYm = false;

    var temp = 0;
    var num = 0;

    layui.use('element', function () {
        var element = layui.element;
        element.on('tab(step)', function (data) {
            if (data.index === 0) {
                current_li = 0;
                load_cpa_source_tab('#cpa-file');
                load_gycx_source_tab('#gycx-file');
            } else if (data.index === 1) {
                current_li = 1;
            } else if (data.index === 2) {
                current_li = 2;
            } else if (data.index === 3) {
                current_li = 3;
            } else {
            }
        });
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
        } else if (btn === "next" && current_li < total -1) {
            if(current_li === 0 && isSelectYm === false){
                    return;
            }else if(current_li === 1) {
                var panel_lst = $('#panel-lst').children();
                if(panel_lst.length === 0)
                    return;
            }

            current_li = current_li + 1;
            binding('#previous-btn.operation', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
            $(preBtn).removeClass(disableCss);
            if (current_li === total -1) {
                $(nextBtn).addClass(disableCss);
                load_result_check_tab();
                unbinding('#next-btn.operation', 'click');
            }
        }
        setProgress();
    };

    var setProgress = function (flag, num) {
        layui.use("element", function () {
            var element = layui.element;
            var progress = (((current_li + 1) / total) * 100) + "%";
            element.tabChange('step', tab_arr[current_li]);
            element.progress('calc-progress-step', progress);
            element.progress(flag, num + '%');
        });
    };

    var callback = function() {
        var conn = window.im_object.conns();
        conn.listen({
            onOpened: function ( message ) {console.info("im 连接成功")},
            onClosed: function ( message ) {},         //连接关闭回调
            onTextMessage: function ( message ) {
                var ext = message.ext;
                if(ext !== null) {
                    var reVal = window.im_object.searchExtJson(ext)('type') !== 'Null' ? window.im_object.searchExtJson(ext)('type') : window.im_object.searchExtJsonForElement(ext.elems)('type');
                    switch (reVal) {
                        case 'progress':
                            progress(message);
                            break;
                        case 'calc_ym_result':
                            $('.mask-layer').show();
                            $('.loading').show();
                            calc_ym_result(message);
                            break;
                        case 'progress_generat_panel':
                            $('.mask-layer').show();
                            $('.loading').show();
                            progress_generat_panel(message);
                            break;
                        case 'generat_panel_result':
                            $('.mask-layer').show();
                            $('.loading').show();
                            generat_panel_result(message);
                            break;
                        case 'progress_calc':
                            $('.mask-layer').show();
                            $('.loading').show();
                            progress_calc(message);
                            break;
                        case 'progress_calc_result':
                            $('.mask-layer').show();
                            $('.loading').show();
                            progress_calc_result(message);
                            break;
                        case 'txt':
                            txt(message);
                            break;
                        default:
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
    };

    var progress = function(msg) {
        console.info(msg);
    };

    var calc_ym_result = function (msg) {
        var obj = JSON.parse(msg.data);
        console.info(msg.data);
        $('.mask-layer').hide();
        $('.loading').hide();

        var $ym_div = $('#ym-div');
        $ym_div.empty();
        $.each(obj.ym.split(","), function( index, ym ) {
            $ym_div.append("<input type='checkbox' value='"+ ym +"' lay-skin='primary'>" + ym);
        });

        f.alertModule.content($('#selectYM').html(), null, null, "请选择需要Max的月份", ['MAX'], function(index, layero){
            write_panel_table(obj.mkt.split(','));
            generat_panel_action();
            layer.close(index);
        });
    };

    var write_panel_table = function(mkt_lst){
        var ym_lst = [];
        var panel_lst = $('#panel-lst');

        panel_lst.empty();

        $('#ym-div input[type=checkbox]:checked').each(function(){
            ym_lst.push($(this).val());
        });

        function write_row(ym, mkt, str){
            var s = "<tr><td>"+ ym  +"</td>";
            s = s + "<td>"+ mkt +"</td>";
            s = s + "<td><span style='color: #1AB394;'>"+ str +"</span></td>";
            var lay_filter = 'generat_panel-progress-' + ym + '-' + mkt;
            s = s + "<td><div class='layui-progress' lay-filter='" + lay_filter + "'>";
            s = s + "<div class='layui-progress-bar layui-bg-green' lay-percent='0%'></div>";
            s = s + "</div></td></tr>";
            return s;
        }

        $.each(ym_lst, function(index1, ym) {
            $.each(mkt_lst, function(index2, mkt) {
                panel_lst.append(write_row(ym, mkt, "正在生成"));
            });
        });
    };

    var generat_panel_action = function() {
        var ym_lst = [];
        $('#ym-div input[type=checkbox]:checked').each(function(){
            ym_lst.push($(this).val());
        });

        if(ym_lst.length < 1){
            return;
        }

        var json = JSON.stringify({
            "businessType": "/genternPanel",
            "company": company,
            "user": $.cookie('webim_user'),
            "cpa": sourceMap.cpa,
            "gycx": sourceMap.gycx,
            "ym": ym_lst
        });
        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});

        isSelectYm = true;
        $( "#next-btn" ).click();
    };

    var progress_generat_panel = function (msg) {
        var ext = msg.ext;
        var ym = window.im_object.searchExtJson(ext)('ym') !== 'Null' ? window.im_object.searchExtJson(ext)('ym') : window.im_object.searchExtJsonForElement(ext.elems)('ym');
        var mkt = window.im_object.searchExtJson(ext)('mkt') !== 'Null' ? window.im_object.searchExtJson(ext)('mkt') : window.im_object.searchExtJsonForElement(ext.elems)('mkt');
        var step = window.im_object.searchExtJson(ext)('step') !== 'Null' ? window.im_object.searchExtJson(ext)('step') : window.im_object.searchExtJsonForElement(ext.elems)('step');
        var lay_filter = 'generat_panel-progress-' + ym + '-' + mkt;
        var span = $('#panel-lst').find('div[lay-filter=' + lay_filter + ']').parent().prev().children('span');
        span.text(step);
        setProgress(lay_filter, msg.data);
    };

    var generat_panel_result = function (msg) {
        var obj = JSON.parse(msg.data);
        var panel_calc_lst = $('#panel-calc-lst');
        var confrim_calc_lst = $('.confrim-calc-lst');
        var fileNames = [];
        panel_calc_lst.empty();
        confrim_calc_lst.empty();

        function write_row(ym, mkt, fileName, str){
            var s = "<tr><td>"+ ym  +"</td>";
            s = s + "<td>"+ mkt +"</td>";
            s = s + "<td><span style='color: #1AB394;'>"+ str +"</span></td>";
            var lay_filter = 'calc-progress-' + fileName;
            s = s + "<td><div class='layui-progress' lay-filter='" + lay_filter + "'>";
            s = s + "<div class='layui-progress-bar layui-bg-green' lay-percent='0%'></div>";
            s = s + "</div></td></tr>";
            return s;
        }

        $.each(obj, function(ym, v1) {
            $.each(v1, function(mkt, panel_lst) {
                $.each(panel_lst, function(i, fname){
                    panel_calc_lst.append(write_row(ym, mkt, fname, "正在启动"));
                    confrim_calc_lst.append(write_row(ym, mkt, fname, "正在启动"));
                    fileNames.push(fname);
                });
            });
        });
        num = confrim_calc_lst.children().length;
        var json = JSON.stringify({
            "businessType": "/modelcalc",
            "company": company,
            "filename": fileNames,
            "uid": $.cookie('uid'),
            "imuname": $.cookie('webim_user')
        });
        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){$( "#next-btn" ).trigger( "click" );}, function(e){console.error(e)});
    };

    var progress_calc = function(msg) {
        var ext = msg.ext;
        var fileName = window.im_object.searchExtJson(ext)('file') !== 'Null' ? window.im_object.searchExtJson(ext)('file') : window.im_object.searchExtJsonForElement(ext.elems)('file');
        var step = window.im_object.searchExtJson(ext)('step') !== 'Null' ? window.im_object.searchExtJson(ext)('step') : window.im_object.searchExtJsonForElement(ext.elems)('step');
        var lay_filter = 'calc-progress-' + fileName;
        var span = $('#panel-calc-lst').find('div[lay-filter=' + lay_filter + ']').parent().prev().children('span');
        span.text(step);
        if(msg.data === "100") {
            temp = temp + 1;
            uuids.push({"fileName": fileName, "uuid" : window.im_object.searchExtJsonForElement(ext.elems)('uuid')});
            tables.push(window.im_object.searchExtJsonForElement(ext.elems)('table'));
            if(num === temp){
                $('.mask-layer').hide();
                $('.loading').hide();
                isCalcDone = true;
                temp = 0;
            }
        }
        setProgress(lay_filter, msg.data);
    };

    var progress_calc_result = function(msg) {
        var ext = msg.ext;
        var uuid = window.im_object.searchExtJsonForElement(ext.elems)('uuid');
        var lay_uuid = 'calc-progress-' + uuid;
        var step_result = window.im_object.searchExtJsonForElement(ext.elems)('step');
        var span_result = $('.confrim-calc-lst').eq(1).find('div[lay-filter=' + lay_uuid + ']').parent().prev().children('span');
        span_result.text(step_result);
        if(msg.data === "100") {
            temp = temp + 1;
            if(num === temp){
                $('.mask-layer').hide();
                $('.loading').hide();
                temp = 0;uuids = [];tables = [];
                $('li[pharbers-filter="history"]').click();
            }
        }
        setProgress(lay_uuid, msg.data);
    };

    var txt = function(msg) {
        console.info(msg.data);
    };

    var load_cpa_source_tab = function (uploadid) {
        layui.use('upload', function () {
            var upload = layui.upload;
            var source_lst = $(uploadid);

            upload.render({
                elem: '#select-cpa-btn',
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                auto: false, //选择文件后不自动上传
                // multiple: true , // 多文件上传
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
                    cpaFile = obj.pushFile();
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

                        sourceMap.cpa = res.result[0];
                        delete cpaFile[index];


                        $( "#upload-gycx-btn" ).click();

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

    var load_gycx_source_tab = function (uploadid) {
        layui.use('upload', function () {
            var upload = layui.upload;
            var source_lst = $(uploadid);

            upload.render({
                elem: '#select-gycx-btn',
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                auto: false, //选择文件后不自动上传
                // multiple: true ,
                accept: 'file',
                exts: 'xlsx',
                bindAction: '#upload-gycx-btn' ,//#upload-panel-btn
                choose: function (obj) {
                    gycFile = obj.pushFile();
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

                        sourceMap.gycx = res.result[0];
                        delete gycFile[index];

                        var json = JSON.stringify(
                                f.parameterPrefix.conditions({
                                    "company": company,
                                    "uid": $.cookie('uid')
                                })
                        );
                        f.ajaxModule.baseCall('/imroom/create', json, 'POST', function(r){
                            call_calcYM();
                        }, function(e){console.error(e)});

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

    var call_calcYM = function() {
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            var json = JSON.stringify({
                "businessType": "/calcYM",
                "company": company,
                "user": $.cookie('webim_user'),
                "cpa": sourceMap.cpa,
                "gycx": sourceMap.gycx
            });
            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
        }
    };

    var load_result_check_tab = function () {
        $('#goinghistory').click(function(){
            $('.mask-layer').show();
            $('.loading').show();
            f.alertModule.content($('#confrims').html(), null, null, '确认列表', function(){$('#confrims').hide();});
            var confrim_calc_lst = $('.confrim-calc-lst');
            $.each(uuids, function(i, v){
                var div = confrim_calc_lst.eq(1).find('div[lay-filter="calc-progress-' + v.fileName + '"]');
                div.attr('lay-filter', 'calc-progress-' + v.uuid);
            });

            $.each(uuids, function(i, v){
                var json = JSON.stringify({"businessType": "/datacommit",
                    "company": company,
                    "uuid": v.uuid,
                    "uid": $.cookie('uid'),
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
    };

    var query_company = function() {
        layui.use('layer', function () {});
        var json = JSON.stringify(f.parameterPrefix.conditions({"user_token": $.cookie("user_token")}));
        f.ajaxModule.baseCall('/upload/queryUserCompnay', json, 'POST', function(r){
            if(r.status === 'ok') {
                //company = r.result.user.company;
                company = "fea9f203d4f593a96f0d6faa91ba24ba";
            } else if (r.status === 'error') {
                layer.msg(r.error.message);
            } else {
                layer.msg('服务出错请联系管理员！');
            }
        }, function(e){console.error(e)})
    };

    var binding = function(opera, btn, preBtn, postBtn, cssClass) {
        unbinding(opera, 'click');
        $(opera).bind('click', function() {change_tab(btn, preBtn, postBtn, cssClass)});
    };

    var unbinding = function(id, operation) {
        $(id).unbind(operation)
    };

    binding('#previous-btn', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
    binding('#next-btn', 'next', '#next-btn', '#previous-btn', 'layui-btn-disabled');

}(jQuery));

