/**
 * Created by Wli on 2017/1/5.
 */
var p = null;

// var test = function() {
//     callback.call(this);
//     conn.listen({
//         onTextMessage: function ( message ) {
//             console.info(message)
//             // var msg = eval("("+message.data+")")
//             // textMsg = eval("("+message.data+")")
//             // if(msg.progress == 100){
//             //     p.setPercent(0)
//             //     $(".progresstier").css("display", "none");
//             // }else{
//             //     p.setPercent(msg.progress)
//             // }
//         }
//     });
// }

$(function(){
    p = new progress2();
    load_im()
    //conn = load_Web_IM();
    //login_im("test", "1");


    // var proto = beget(callback.prototype);
    // proto.constructor = test;
    // test.prototype = proto;
    //
    // test()

    //*********************************************************************
    //功能: 确认->下一步
    //时间：20170413
    //创建：Arthas
    //修订：
    //说明：确认模型运算后的结果，完成后跳转结果查询页面。
    //*********************************************************************
    $('#nextstepBtn').click(function(){
        var dataMap = JSON.stringify({
            "company": $.cookie("token"),
            "uname": $.cookie('webim_user'),
            "businessType": "/datacommit"
        })
        $.ajax({
            type: "post",
            data: dataMap,
            url: "/callhttpServer",
            contentType: 'application/json, charset=utf-8',
            cache: false,
            dataType: "json",
            success: function (json) {
                $(".progresstier").css("display", "block");
                nextStep()
            },
            error: function (e) {
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });

})

$(function(){

    var i = 0
    $('#data_5 .input-daterange').datepicker({
        minViewMode: 1,
        keyboardNavigation: false,
        forceParse: false,
        autoclose: true,
        todayHighlight: true
    }).on('changeDate',function(){
        if(i==0){
          ModelOperationEchartFun();
          i++
        }else{
          i=0
      }
    });

    ModelOperationEchartFun();
});

//*********************************************************************
//功能: Echarts图初始化加载
//时间：20170420
//创建：Arthas
//修订：
//说明：Echarts图初始化加载，Ajax请求后台数据。
//*********************************************************************
var ModelOperationEchartFun = function() {
    var bar = echarts.init(document.getElementById('bar1'));
    var bar2 = echarts.init(document.getElementById('bar2'));
    var bar3 = echarts.init(document.getElementById('bar3'));

    bar.showLoading({
        text : '数据获取中',
        effect: 'whirling'
    });
    bar2.showLoading({
        text : '数据获取中',
        effect: 'whirling'
    });
    bar3.showLoading({
        text : '数据获取中',
        effect: 'whirling'
    });
    var market = $('select[data-name="search-result-market"]').val();
    var date = $('input[name="date_5"]').val();
    var dataMap = JSON.stringify({
        "company": $.cookie("token"),
        "market": market.replace(/\s/g, ""),
        "date": date
    });
    $.ajax({
        type: "POST",
        url: "/modeloperation/operationbar11",
        dataType: "json",
        data: dataMap,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            echarts_bar1(r,bar);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
    $.ajax({
        type: "POST",
        url: "/modeloperation/operationbar23",
        dataType: "json",
        data: dataMap,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            echarts_bar23(r,bar2,bar3);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}

//*********************************************************************
//功能: Echarts图1
//时间：20170420
//创建：Arthas
//说明：根据日期维度、以日期和市场为筛选条件进行分析，将Market和Market Share数据进行比较。
//*********************************************************************
var echarts_bar1 = function(r){
    var data = r.result.result
    var x_data = [];
    var s_data1 = [];
    var s_data2 = [];

    for(var item in data){
        var obj = data[item];
        x_data.push(obj.Date);
        s_data1.push((obj.f_sales/10000).toFixed(4));
        s_data2.push((obj.f_sales/10000).toFixed(4));
    }

    var itemStyleColor = ['#1ab394', '#cacaca'];
    var option = {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: [
            {
                name: '日期',
                type: 'category',
                data: x_data
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '销售额(万)',
            },
            {
                type: 'value',
                name: 'Mono Unit Share',
                show: false,
                spliteLine: {show: false}
            }
        ],
        series: [
            {
                name:'MAX',
                type:'bar',
                barWidth: 35,
                data: s_data1,
                itemStyle: {
                    normal: {
                        color: itemStyleColor[0]
                    }
                }
            },
            {
                name:'MAX Mono Share',
                type:'line',
                yAxisIndex: 1,
                data: s_data2,
                itemStyle: {
                    normal: {
                        color: itemStyleColor[1]
                    }
                }
            }
        ]
    };
    bar = echarts.init(document.getElementById('bar1'));
    bar.hideLoading();
    bar.setOption(option);
    window.addEventListener("resize", function() {
        bar.resize();
    });
}

//*********************************************************************
//功能: Echart图2-图3
//时间：20170420
//创建：Arthas
//说明：根据城市维度、以日期和市场为筛选条件进行分析，将当期和上期数据进行比较，将当期和去年同期数据进行比较。
//*********************************************************************
var echarts_bar23 = function(r){
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

    var cur_data = r.result.result.cur_top6
    var ear_data = r.result.result.ear_top6
    var las_data = r.result.result.las_top6

    var x_data = [];
    var echarts2_s_data1 = [];
    var echarts2_s_data2 = [];
    var echarts3_s_data2 = [];

    for(var cur in cur_data){
        var obj = cur_data[cur];
        x_data.push(obj.City);
        echarts2_s_data1.push((obj.f_sales/10000).toFixed(4));
    }

    for(var ear in ear_data){
        var obj = ear_data[ear];
        x_data.push(obj.City);
        echarts2_s_data2.push((obj.f_sales/10000).toFixed(4));
    }

    for(var las in las_data){
        var obj = las_data[las];
        x_data.push(obj.City);
        echarts3_s_data2.push((obj.f_sales/10000).toFixed(4));
    }

    var option2 = {
        color: colors,

        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',name: '城市',
            data: x_data
        },
        yAxis: {type: 'value',name: '销售额(万)'},
        series: [{
            name: '当期',
            type: 'bar',
            data: echarts2_s_data1
        }, {
            name: '上期',
            type: 'bar',
            data: echarts2_s_data2
        }]
    };
    var option3 = {
        color: colors,
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',name: '城市',
            data: x_data
        },
        yAxis: {type: 'value',name: '销售额(万)'},
        series: [{
            name: '当期',
            type: 'bar',
            data: echarts2_s_data1
        }, {
            name: '去年同期',
            type: 'bar',
            data: echarts3_s_data2
        }]
    };
    bar2 = echarts.init(document.getElementById('bar2'));
    bar3 = echarts.init(document.getElementById('bar3'));
    bar2.hideLoading();
    bar2.setOption(option2);
    bar3.hideLoading();
    bar3.setOption(option3);
    window.addEventListener("resize", function() {
        bar2.resize();
        bar3.resize();
    });
}

var nextStep = function() {
    conn.listen({
        onTextMessage: function ( message ) {
            var ext = message.ext
            if (ext != null) {
                var result = searchExtJson(ext)("type")
                if(result == "progress") {
                    var r = p.setPercent(parseInt(message.data))
                    msgIdentifying = parseInt(message.data)
                    if(parseInt(message.data) >= 100 || r >= 100) {
                        setCloseInterval()
                        p.setPercent(0)
                        $(".progresstier").css("display", "none");
                        setTimeout(function(){document.getElementById("jgcx").click()}, 1000 * 1)
                    }
                }else if(result == "txt") {
                    console.info(message.data);
                }else {
                    console.info("No Type");
                    console.info(message.data);
                }
            }
            // var msg = eval("("+message.data+")")
            // msgIdentifying = msg.progress
            // var r = p.setPercent(msg.progress)
            // if(msg.progress >= 100 || r >= 100) {
            //     setCloseInterval()
            //     p.setPercent(0)
            //     $(".progresstier").css("display", "none");
            //     setTimeout(function(){document.getElementById("jgcx").click()}, 1000 * 1)
            // }
        }
    });
}