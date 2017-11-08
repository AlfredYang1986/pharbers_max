/**
 * Created by yym on 11/7/17.
 */
(function ($) {

    //变量
    var company = "";
    var isCalcDone = false;
    var sourceMap = {"cpa":"","gycx":""};
    //函数
    var load_cpa_source = function () {
        var aFile;
        var name = 'cpa';
        var txt = '#txt'+name;
        var sel = '#select'+name;
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
                choose: function (obj) {
                    console.log("aaaa")
                    aFile = obj.pushFile();
                    obj.preview(function (index, file, result) {
                        $(txt).val(file.name);
                        $(txt).addClass('disabled');
                    });
                },//文件选择
                before: function () {
                    query_company();
                    if(!isCalcDone) {
                        $('.mask-layer').show();
                        $('.loading').show();
                    }
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        $('.cpa-file').addClass('succColor');
                        sourceMap.cpa = res.result[0];
                        delete aFile[index];
                        // $( "#upload-gycx-btn" ).click();

                        return;
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                }
            });
        });
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

    var load_gycx_source = function (uploadid) {
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
}(jQuery))