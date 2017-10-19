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

    layui.use('element', function () {
        var element = layui.element;
        element.on('tab(step)', function (data) {
            if (data.index === 0) {
                current_li = 0;
            } else if (data.index === 1) {
                current_li = 1;
            } else if (data.index === 2) {
                current_li = 2;
                load_result_check_tab()
            } else {
            }
        });
        load_panel_tab('#panel-lst');
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
                unbinding('#next-btn.operation', 'click');
            }
        }
        setProgress();
    };

    var callback = function() {
        var conn = window.im_object.conns();
        var temp = 0;
        conn.listen({
            onOpened: function ( message ) {},
            onClosed: function ( message ) {},         //连接关闭回调
            onTextMessage: function ( message ) {
                var num = $('#panel-lst').children().length;
                var ext = message.ext;
                if(ext !== null) {
                    var reVal = window.im_object.searchExtJson(ext)('type');
                    var fileName = window.im_object.searchExtJson(ext)('file');
                    var step = window.im_object.searchExtJson(ext)('step');
                    var lay_filter = 'calc-progress-' + fileName;
                    var span = $('div[lay-filter= '+ lay_filter +']').parent().prev().children('span');
                    span.text(step);
                    if(reVal === 'progress') {
                        setProgress(lay_filter, message.data);
                    } else if(reVal === 'progress_calc') {
                        if(message.data === "100") {
                            temp = temp + 1;
                            if(num === temp){
                                $('.mask-layer').hide();
                                $('.loading').hide();
                                isCalcDone = true
                            }
                            setProgress(lay_filter, message.data);
                        }
                    } else if(reVal === 'txt') {
                        console.info(data.data);
                    } else {
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

                        var json = JSON.stringify({"businessType": "/modelcalc",
                                                    "company": company,
                                                    "filename": res.result[0],
                                                    "uname": $.cookie('webim_user')
                                                  });
                        // f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
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
    }

    var load_result_check_tab = function () {
        //TODO 通过js获得
        var result_check_marketlst = ["market1", "market2", "market3"];
        var result_check_datelst = ["date1", "date2", "date3"];

        layui.use('form', function () {
            var form = layui.form;

            load_select_box('#sample_market', '#result_check_market', '#sample_date', 'result_check_date');
            load_bar1_chart();
            load_bar2_chart();
            load_bar3_chart();

            form.render();
        });

        function load_select_box(sample_market, result_check_market, sample_date, result_check_date) {
            $(sample_market).append(new Option());
            $.each(result_check_marketlst, function (i) {
                $(result_check_market).append(new Option(result_check_marketlst[i]));
            });
            $(sample_date).append(new Option());
            $.each(result_check_datelst, function (i) {
                $(result_check_date).append(new Option(result_check_datelst[i]));
            });
        }

        function load_bar1_chart() {
            var bar1_chart = echarts.init(document.getElementById('bar1'));
            // 指定图表的配置项和数据
            var option = {
                xAxis: {
                    name: '日期',
                    data: ['201512', '201601', '201602', '201603', '201604', '201605', '201606',
                        '201607', '201608', '201609', '201610', '201611']
                },
                yAxis: {
                    name: '销售额(万)',
                    type: 'value'
                },
                series: [{
                    type: 'bar',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8555]
                }]
            };
            // 使用刚指定的配置项和数据显示图表。
            bar1_chart.setOption(option);
        }

        function load_bar2_chart() {
            var bar2_chart = echarts.init(document.getElementById('bar2'));
            // 指定图表的配置项和数据
            var option = {
                xAxis: {
                    name: '城市',
                    data: ['北京市', '上海市', '成都市', '广州市', '南京市', '合肥市',
                        '北京市', '上海市', '成都市', '广州市', '南京市', '合肥市']
                },
                yAxis: {
                    name: '销售额(万)',
                    type: 'value'
                },
                series: [{
                    type: 'bar',
                    data: [1350, 750, 400, 390, 380, 250, 0, 0, 0, 0, 0, 0, 0]
                }]
            };
            bar2_chart.setOption(option);
        }

        function load_bar3_chart() {
            var bar3_chart = echarts.init(document.getElementById('bar3'));
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '今年Vs去年(近12月销售额)',
                    left: 'center'
                },
                xAxis: {
                    name: '日期',
                    type: 'category',
                    boundaryGap: false,
                    data: ['201512', '201601', '201602', '201603', '201604', '201605', '201606',
                        '201607', '201608', '201609', '201610', '201611']
                },
                yAxis: {
                    name: '销售额(万)',
                    type: 'value'
                },
                series: [{
                    type: 'bar',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5555]
                }]
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
    }

    var unbinding = function(id, operation) {
        $(id).unbind(operation)
    }

    binding('#previous-btn', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
    binding('#next-btn', 'next', '#next-btn', '#previous-btn', 'layui-btn-disabled');

}(jQuery))

