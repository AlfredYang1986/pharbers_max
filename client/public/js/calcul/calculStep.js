/**
 * Created by yym on 11/7/17.
 */
(function ($) {
    //变量
    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    var f = new Facade();

    $('#secondStep').hide();
    $('#sampleResult').hide();
    $('#thirdStep').hide();
    loadMainChart(82, 'mainChart', '文档总体可信度');
    loadMainChart(18, 't-char1', '文档总体可信度');
    loadMainChart(18, 't-char2', '文档总体可信度');
    loadMainChart(18, 't-char3', '文档总体可信度');
    loadMainChart(18, 't-char4', '文档总体可信度');
    loadLineChart('t1');
    loadLineChart('t2');
    loadLineChart('t3');
    loadLineChart('t4');

    var loading = function() {
        $('.mask-layer').show();
        $('.loading').show();
    };

    $("#toSecondBtn").click(function(){toSecondStep()});
    $("#check-btn").click(function(){check_file()});

    load_cpa_source();
    load_gycx_source();
    callback();

    //函数
    function load_cpa_source () {
        var name = 'cpa';
        var txt = '#txt-'+name;
        var sel = '#select-'+name;
        layui.use('upload', function () {
            var upload = layui.upload;
            upload.render({
                elem: sel,
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                // multiple: true , // 多文件上传
                accept: 'file',
                exts: 'xlsx',
                before: function (obj) {
                    obj.preview(function (index, file, result) {
                        $(txt).val(file.name);
                        $(txt).addClass('disabled');
                    });
                    query_company();
                    if(!isCalcDone) {
                        $('.mask-layer').show();
                        $('.loading').show();
                    }
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        $('.mask-layer').hide();
                        $('.loading').hide();
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
    };

    function load_gycx_source () {
        var name = 'gycx';
        var txt = '#txt-'+name;
        var sel = '#select-'+name;
        layui.use('upload', function () {
            var upload = layui.upload;
            upload.render({
                elem: sel,
                url: '/source/upload',
                drag: false,
                data: {"company": company} ,
                // multiple: true , // 多文件上传
                accept: 'file',
                exts: 'xlsx',
                before: function (obj) {
                    obj.preview(function (index, file, result) {
                        $(txt).val(file.name);
                        $(txt).addClass('disabled');
                    });
                    query_company();
                    if(!isCalcDone) {
                        $('.mask-layer').show();
                        $('.loading').show();
                    }
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        $('.mask-layer').hide();
                        $('.loading').hide();
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
    };

    var toSecondStep = function () {
       if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
           $('#firstStep').hide();
           $('#secondStep').show();
           $('.scd-img')[0].src = "/assets/images/calculStep/step11.png";
       }
    };

    var check_file = function(){
        if(sourceMap.cpa !== "" && sourceMap.gycx !== ""){
            var json = JSON.stringify({
                "businessType": "/calcYM",
                "company": company,
                "user": $.cookie('webim_user'),
                "cpa": sourceMap.cpa,
                "gycx": sourceMap.gycx
            });
            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){loading()}, function(e){console.error(e)});
        }
    };

    // 环信回调函数
    function callback() {
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
                            loading();
                            calc_ym_result(message);
                            break;
                        case 'progress_generat_panel':
                            loading();
                            progress_generat_panel(message);
                            break;
                        case 'generat_panel_result':
                            loading();
                            generat_panel_result(message);
                            break;
                        case 'progress_calc':
                            loading();
                            progress_calc(message);
                            break;
                        case 'progress_calc_result':
                            loading();
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
        var msg = '{"ym":"201705","mkt":"INF,SPE"}';
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
        console.info(msg);
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
        console.info(msg);
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
        console.info(msg);
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
        console.info(msg);
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



    var toSampleResult = function () {
        $('#secondStep').hide();
        $('#sampleResult').show();
    };

    var prograssBar = function (tips) {
        var rotate = echarts.init(document.getElementById('rotate'));

        function loading() {
            return [{
                value: tips,
                itemStyle: {
                    normal: {
                        color: '#fb358a',
                        shadowBlur: 10,
                        shadowColor: '#fb358a'
                    }
                }
            }, {
                value: 100 - tips,
            }];
        }

        var option = {
            title: {
                text: (tips * 1) + '%',
                x: 'center',
                y: 'center',
                textStyle: {
                    color: '#fb358a',
                    fontSize: 30,
                }
            },
            series: [{
                name: 'loading',
                type: 'pie',
                radius: ['30%', '31%'],
                hoverAnimation: false,
                label: {
                    normal: {
                        show: false,
                    }
                },
                data: loading()
            }]
        };
        var interval = setInterval(function () {
            if (tips == 10) {
                $('#chooseMonth').modal('show');
                ++tips;
                clearInterval(interval);
            } else if (tips == 100) {
                clearInterval(interval);
                toSampleResult();
            } else {
                ++tips;
            }

//                    if (tips == 100) {
//                        tips = 0;
//                    } else {
//                        ++tips;
//                    }
            rotate.setOption({
                title: {
                    text: tips + '%'
                },
                series: [{
                    name: 'loading',
                    data: loading()
                }]
            })

        }, 100);
        rotate.setOption(option);
    };

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

    var setProgress = function (flag, num) {
        layui.use("element", function () {
            var element = layui.element;
            var progress = (((current_li + 1) / total) * 100) + "%";
            element.tabChange('step', tab_arr[current_li]);
            element.progress('calc-progress-step', progress);
            element.progress(flag, num + '%');
        });
    };


}(jQuery));