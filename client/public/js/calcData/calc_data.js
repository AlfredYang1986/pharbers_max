/**
 * Created by clock on 17-9-28.
 */
'use strict';
var current_li = 0;
var tab_arr = ['cpa_li', 'gycx_li', 'sample_li', 'result_li'];
var progress_arr = ['0%', '33%', '66%', '100%'];

layui.use('element', function () {
    var element = layui.element;
    element.on('tab(step)', function (data) {
        if (data.index === 0) {
            current_li = 0;
            load_cpa_tab()
        } else if (data.index === 1) {
            current_li = 1;
            load_gycx_tab()
        } else if (data.index === 2) {
            current_li = 2;
            load_sample_check_tab()
        } else if (data.index === 3) {
            current_li = 3;
            load_result_check_tab()
        } else {
        }
    });
});

var change_tab = function (btn) {
    if (btn === "pre" && current_li > 0) {
        $("#next_btn").removeClass("layui-btn-disabled");
        current_li = current_li - 1;
        if (current_li === 0)
            $("#previous_btn").addClass('layui-btn-disabled');
    } else if (btn === "next" && current_li < 3) {
        $("#previous_btn").removeClass("layui-btn-disabled");
        current_li = current_li + 1;
        if (current_li === 3)
            $("#next_btn").addClass('layui-btn-disabled');
    }
    layui.use("element", function () {
        var element = layui.element;
        element.tabChange('step', tab_arr[current_li]);
        element.progress('calc_progress', progress_arr[current_li]);
    });
};

var load_cpa_tab = function () {
    layui.use('upload', function () {
        var upload = layui.upload;
        var cpa_lst = $('#cpa_lst');
        upload.render({
            elem: '#select_cpa_btn',
            url: '/api/upload/',
            auto: false, //选择文件后不自动上传
//                multiple: true,
            accept: 'file',
            exts: 'xlsx',
            bindAction: '#upload_cap_btn',
            choose: function (obj) {
                if (cpa_lst.html() !== "") {
                    layer.open({
                        title: '提示',
                        content: '只能上传一个CPA数据源'
                    });
                } else {
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

                        cpa_lst.append(tr);
                    });
                }
            },
            done: function (res, index, upload) {
                if (res.code === 0) { //上传成功
                    var tr = cpa_lst.find('tr#upload-' + index);
                    var tds = tr.children();
                    tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                    tds.eq(3).html(''); //清空操作
                    delete files[index]; //删除文件队列已经上传成功的文件
                    return;
                }
                this.error(index, upload);
            },
            error: function (index, upload) {
                var tr = cpa_lst.find('tr#upload-' + index);
                var tds = tr.children();
                tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
            }
        });
    });
};

var load_gycx_tab = function () {
    layui.use('upload', function () {
        var upload = layui.upload;
        var gycx_lst = $('#gycx_lst');
        upload.render({
            elem: '#select_gycx_btn',
            url: '/api/upload/',
            auto: false, //选择文件后不自动上传
//                multiple: true,
            accept: 'file',
            exts: 'xlsx',
            bindAction: '#upload_gycx_btn',
            choose: function (obj) {
                if (gycx_lst.html() !== "") {
                    layer.open({
                        title: '提示',
                        content: '只能上传一个CPA数据源'
                    });
                } else {
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

                        gycx_lst.append(tr);
                    });
                }
            },
            done: function (res, index, upload) {
                if (res.code == 0) { //上传成功
                    var tr = gycx_lst.find('tr#upload-' + index);
                    var tds = tr.children();
                    tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                    tds.eq(3).html(''); //清空操作
                    delete files[index]; //删除文件队列已经上传成功的文件
                    return;
                }
                this.error(index, upload);
            },
            error: function (index, upload) {
                var tr = gycx_lst.find('tr#upload-' + index);
                var tds = tr.children();
                tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
            }
        });
    });
};

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

        load_select_box();
        load_hosp_line();
        load_prodline_chart();
        load_markline_chart();
        load_sales_chart();
        load_units_chart();
        load_semple_table();

        form.render();
    });

    function load_select_box() {
        $('#sample_market').append(new Option());
        $.each(sample_market, function (i) {
            $('#sample_market').append(new Option(sample_market[i]));
        });
        $('#sample_date').append(new Option());
        $.each(sample_date, function (i) {
            $('#sample_date').append(new Option(sample_date[i]));
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
};

var load_result_check_tab = function () {
    //TODO 通过js获得
    var result_check_market = ["market1", "market2", "market3"];
    var result_check_date = ["date1", "date2", "date3"];
    var bar1_data = [];
    var bar2_data = [];
    var bar3_data = [];

    layui.use('form', function () {
        var form = layui.form;

        load_select_box();
        load_bar1_chart();
        load_bar2_chart();
        load_bar3_chart();

        form.render();
    });

    function load_select_box() {
        $('#sample_market').append(new Option());
        $.each(result_check_market, function (i) {
            $('#result_check_market').append(new Option(result_check_market[i]));
        });
        $('#sample_date').append(new Option());
        $.each(result_check_date, function (i) {
            $('#result_check_date').append(new Option(result_check_date[i]));
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
};

