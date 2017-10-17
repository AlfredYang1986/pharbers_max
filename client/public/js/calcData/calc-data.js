/**
 * Created by clock on 17-9-28.
 */

(function ($) {
    'use strict';
    var total = $('ul[pharbers-ul="calc-tab"]').find('li').length
    var current_li = 0;
    var tab_arr = ['panel-li', 'sample-li', 'result-li'];
    $('li[pharbers-filter="calc"]').addClass("layui-this");
    var f = new Facade();
    var company = "";

    layui.use('element', function () {
        var element = layui.element;
        var sample_market = ["market1", "market2", "market3"];
        var sample_date = ["date1", "date2", "date3"];
        element.on('tab(step)', function (data) {
            if (data.index === 0) {
                current_li = 0;
                load_panel_tab('#panel-lst');
            } else if (data.index === 1) {
                current_li = 1;
                load_sample_check_tab(sample_market, sample_date)
            } else if (data.index === 2) {
                current_li = 2;
                load_result_check_tab()
            } else {
            }
        });
        setProgress();
    });

    var change_tab = function (btn, nextBtn, preBtn, disableCss) {
        if (btn === "pre" && current_li > 0) {
            current_li = current_li - 1;
            $(nextBtn).removeClass(disableCss);
            binding('#next-btn', 'next', '#next-btn', '#previous-btn', 'layui-btn-disabled');
            if (current_li === 0) {
                $(preBtn).addClass(disableCss);
                unbinding('#previous-btn', 'click');
            }
        } else if (btn === "next" && current_li < 2) {
            current_li = current_li + 1;
            binding('#previous-btn', 'pre', '#next-btn', '#previous-btn', 'layui-btn-disabled');
            $(preBtn).removeClass(disableCss);
            if (current_li === 2) {
                $(nextBtn).addClass(disableCss);
                unbinding('#next-btn', 'click');
            }
        }
        setProgress();
    };

    var setProgress = function (num) {
        layui.use("element", function () {
            var element = layui.element;
            var progress = (((current_li + 1) / total) * 100) + "%";
            element.tabChange('step', tab_arr[current_li]);
            element.progress('calc-progress-step', progress);
            element.progress('calc-progress', num + '%');
        });
    }

    var load_panel_tab = function (uploadid) {
        layui.use('upload', function () {
            var upload = layui.upload;
            var panel_lst = $(uploadid);

            upload.render({
                elem: '#select-panel-btn',
                url: '/panel/upload',
                drag: false,
                data: {"company": company},
                auto: false, //选择文件后不自动上传
                multiple: true,
                accept: 'file',
                exts: 'xlsx',
                bindAction: '#upload-panel-btn',
                before: function () {
                    query_company();
                },
                choose: function (obj) {
                    var files = obj.pushFile();
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">'
                            , '<td>' + file.name + '</td>'
                            , '<td>' + (file.size / 1024 / 1024).toFixed(1) + 'MB</td>'
                            , '<td>等待上传</td>'
                            , '<td>'
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

                        panel_lst.append(tr);
                    });
                },
                done: function (res, index, upload) {
                    if (res.status === 'ok') { //上传成功
                        var tr = panel_lst.find('tr#upload-' + index);
                        var tds = tr.children();
                        tds.eq(2).html('<span style="color: #008B7D;">上传成功</span>');
                        var p = '<div class="layui-progress" lay-filter="calc-progress">\n' +
                                '    <div class="layui-progress-bar layui-bg-green" lay-percent="50%"></div>\n' +
                                '</div>';
                        tds.eq(3).html(p); //清空操作
                        // setProgress(50); // 设置计算进度条
                        var json = JSON.stringify({"businessType": "/modelcalc",
                                                    "company": company,
                                                    "filename": res.result[0],
                                                    "uname": "fuck"
                                                  });
                        f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)})
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

    var load_sample_check_tab = function () {
        //TODO 通过js获得
        var sample_market = ["market1", "market2", "market3"];
        var sample_date = ["date1", "date2", "date3"];
        $('#Current_Month_HospitalNum').text("912");
        $('#Early_Month_HospitalNum').text("0");
        $('#Last_Year_HospitalNum').text("0");
        $('#Current_Month_ProductNum').text("2331");
        $('#Early_Month_ProductNum').text("0");
        $('#Last_Year_ProductNum').text("0");
        $('#Current_Month_MarketNum').text("1");
        $('#Early_Month_MarketNum').text("0");
        $('#Last_Year_MarketNum').text("0");

        var hosp_line_data = [[0, 0], [5, 0], [10, 0], [15, 0], [20, 0], [25, 0], [30, 160000]];
        var prod_line_data = [[0, 0], [5, 0], [10, 0], [15, 0], [20, 0], [25, 0], [30, 6]];
        var mark_line_data = [[0, 0], [5, 0], [10, 0], [15, 0], [20, 0], [25, 0], [30, 6]];
        var sales_data = [];
        var units_data = [];
        var semple_table_data = [];

        layui.use('form', function () {
            var form = layui.form;

            load_select_box('#sample_market','#sample_date');
            load_hosp_line();
            load_prodline_chart();
            load_markline_chart();
            load_sales_chart();
            load_units_chart();
            load_semple_table();

            form.render();
        });

        function load_select_box(market, date) {
            $(market).append(new Option());
            $.each(sample_market, function (i) {
                $(market).append(new Option(sample_market[i]));
            });
            $(date).append(new Option());
            $.each(sample_date, function (i) {
                $(date).append(new Option(sample_date[i]));
            });
        }

        function load_hosp_line() {
            var hospline_chart = echarts.init(document.getElementById('hospline'));
            var option = {
                xAxis: {
                    show: false
                },
                yAxis: {
                    show: false
                },
                series: [{
                    type: 'line',
                    data: hosp_line_data
                }]
            };
            hospline_chart.setOption(option);
        }

        function load_prodline_chart() {
            var prodline_chart = echarts.init(document.getElementById('prodline'));
            var option = {
                xAxis: {
                    show: false
                },
                yAxis: {
                    show: false
                },
                series: [{
                    type: 'line',
                    data: prod_line_data
                }]
            };
            prodline_chart.setOption(option);
        }

        function load_markline_chart() {
            var markline_chart = echarts.init(document.getElementById('markline'));
            var option = {
                xAxis: {
                    show: false
                },
                yAxis: {
                    show: false
                },
                series: [{
                    type: 'line',
                    data: mark_line_data
                }]
            };
            markline_chart.setOption(option);
        }

        function load_sales_chart() {
            var sales_chart = echarts.init(document.getElementById('Sales'));
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
                    type: 'line',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5555]
                }]
            };
            sales_chart.setOption(option);
        }

        function load_units_chart() {
            var units_chart = echarts.init(document.getElementById('Units'));
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '今年Vs去年(近12月销售量)',
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
                    name: '销售量(万)',
                    type: 'value'
                },
                series: [{
                    type: 'line',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2555]
                }]
            };
            units_chart.setOption(option);
        }

        function load_semple_table() {
            layui.use('table', function () {
                var table = layui.table;

                table.render({
                    elem: '#semple_table',
                    cols: [[
                        {align: 'center', title: '样本医院检查结果', colspan: 6}
                    ], [
                        {field: 'id', title: '序号', align: 'center', sort: true, rowspan: 1, width: 100},
                        {field: 'hosp_name', title: '医院名称', align: 'center', sort: true, rowspan: 2, width: 200},
                        {field: 'provinces', title: '省份', align: 'center', sort: true, rowspan: 1, width: 200},
                        {field: 'city', title: '城市', align: 'center', sort: true, rowspan: 1, width: 200},
                        {field: 'level', title: '城市级别', align: 'center', sort: true, rowspan: 1, width: 200}
                    ]],
                    data: [
                        {"id": "a1", "hosp_name": "a2", "provinces": "a3", "city": "a4", "level": "a5"},
                        {"id": "b1", "hosp_name": "b2", "provinces": "b3", "city": "b4", "level": "b5"},
                        {"id": "c1", "hosp_name": "c2", "provinces": "c3", "city": "e4", "level": "c5"},
                        {"id": "d1", "hosp_name": "d2", "provinces": "d3", "city": "d4", "level": "d5"},
                        {'id': "e1", 'hosp_name': "e2", 'provinces': "e3", 'city': "e4", 'level': "e5"},
                        {'id': "a2", "hosp_name": "b2", "provinces": "a3", "city": "a4", "level": "a5"},
                        {'id': "a3", "hosp_name": "a2", "provinces": "a3", "city": "a4", "level": "a5"},
                        {'id': "a4", "hosp_name": "a2", "provinces": "a3", "city": "a4", "level": "a5"},
                        {"id": "a5", "hosp_name": "a2", "provinces": "a3", "city": "a4", "level": "a5"}
                    ],
                    page: true, //是否显示分页
                    limits: [5, 7, 10],
                    limit: 5 //每页默认显示的数量
                });
            });
        }
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

