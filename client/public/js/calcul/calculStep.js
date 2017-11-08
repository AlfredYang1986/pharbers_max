/**
 * Created by yym on 11/7/17.
 */
(function ($) {
    //变量
    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    var f = new Facade();
    //函数
    var load_cpa_source = function () {
        var aFile;
        var name = 'cpa';
        var txt = '#txt-'+name;
        var sel = '#select-'+name;
        layui.use('upload', function () {
            console.log("aaa")
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
                        // $( "#upload-gycx-btn" ).click();
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

    var load_gycx_source = function () {
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
                        // $( "#upload-gycx-btn" ).click();
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
           $('.scd-img')[0].src = "@routes.Assets.versioned(\"images/calculStep/step11.png\")";
       }
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
    var setProgress = function (flag, num) {
        layui.use("element", function () {
            var element = layui.element;
            var progress = (((current_li + 1) / total) * 100) + "%";
            element.tabChange('step', tab_arr[current_li]);
            element.progress('calc-progress-step', progress);
            element.progress(flag, num + '%');
        });
    };

    load_cpa_source();
    load_gycx_source();
}(jQuery))