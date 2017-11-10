/**
 * Created by yym on 11/7/17.
 */
(function ($) {
    //变量
    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    var f = new Facade();
    var fileNames = [];

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
                    if(!isCalcDone)
                        show_loading();
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        hide_loading();
                        $('.gycx-file').css("color", "#009688");
                        sourceMap.gycx = res.result[0];

                        //创建环信聊天室
                        var json = JSON.stringify(
                            f.parameterPrefix.conditions({
                                "company": company,
                                "uid": $.cookie('uid')
                            })
                        );
                        f.ajaxModule.baseCall('/imroom/create', json, 'POST', function(r){
                            toSecondStep();
                        }, function(e){console.error(e)});

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
           $('#firstStep').hide();
           $('#secondStep').show();
           $('.scd-img')[0].src = "@routes.Assets.versioned(\"images/calculStep/step11.png\")";
       }
    };

    var toThirdStep = function () {
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
            prograssBar(10);
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
                            show_loading();
                            calc_ym_result(message);
                            break;
                        case 'progress_generat_panel':
                            show_loading();
                            progress_generat_panel(message);

                            break;
                        case 'generat_panel_result':
                            show_loading();
                            generat_panel_result(message);
                            break;
                        case 'progress_calc':
                            show_loading();
                            progress_calc(message);
                            break;
                        case 'progress_calc_result':
                            show_loading();
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
        console.info(msg.data);
        var obj = JSON.parse(msg.data);
        hide_loading();

        var $ym_div = $('#month_choose');
        $ym_div.empty();

        $.each(obj.ym.split(","), function( index, ym ) {
            $ym_div.append('<div class="col-sm-3"> <div class="checkbox"> <label> <input type="checkbox" value="'+ ym +'">'+ym+'</label> </div> </div>');
        });

        $('#chooseMonth').modal('show');
    };

    load_cpa_source();
    load_gycx_source();
}(jQuery))