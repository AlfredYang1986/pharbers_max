var sampleCheckFun = function(company, filename) {
    var dataMap = JSON.stringify({
        "company": company,
        "filename": filename
    });
    $.ajax({
        type: "POST",
        url: "/samplecheck/check",
        dataType: "json",
        data: dataMap,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            topThreeCurrent(r.result.FinalResult);
            noMatchHosp(r.result.FinalResult.CurResult.hospList);
            dataTableAjax();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.info("Error")
        }
    });
}

var sampleCheckChartsLineFun = function(company, filename) {
    var dataMap = JSON.stringify({
        "company": company,
        "filename": filename
    });
    $.ajax({
        type: "POST",
        url: "/samplecheck/line",
        dataType: "json",
        data: dataMap,
        cache: true,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            topThreeCharts(r.result.TopChartResult)
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.info("Error")
        }
    });
}

var sampleCheckChartsPlotFun = function(company, filename) {
    var dataMap = JSON.stringify({
        "company": company,
        "filename": filename
    });
    $.ajax({
        type: "POST",
        url: "/samplecheck/plot",
        dataType: "json",
        data: dataMap,
        cache: true,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            salesChartsPlot(r.result.TopChartResult)
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.info("Error")
        }
    });
}

var topThreeCurrent = function (topdata) {
    var yesterYearFlag = topdata.YesterYear
    var agoMonth = topdata.AgoMonth
    var cur = topdata.CurResult
    if(yesterYearFlag != "not data") {
        $("#yesterYearHospNum").text(yesterYearFlag.yesterYearHospNum);
        $("#yesterYearProdNum").text(yesterYearFlag.yesterYearMiniProNum);
        $("#yesterYearMarkNum").text(yesterYearFlag.yesterYearMarketNum);
    }
    if(agoMonth != "not data") {
        $("#agoHospNum").text(agoMonth.agoHospNum);
        $("#agoProductNum").text(agoMonth.agoMiniProNum);
        $("#agoMarketNum").text(agoMonth.agoMarketNum);
    }
    if(cur != "not data") {
        $("#curHospNum").text(cur.hospNum);
        $("#curProductNum").text(cur.miniProNum);
        $("#curMarketNum").text(cur.marketNum);
    }
}

var noMatchHosp = function (hosplist) {
    $.each(hosplist, function (i, v) {
        $("#hospitalList").append("<tr><td>" + (i + 1) + "</td><td>" + v + "</td><td>undefined</td><td>undefined</td><td>undefined</td><td><a href=\"javascript:;\"><i class=\"fa fa-times text-danger text\"></i></a></td></tr>")
    })
}

var topThreeCharts = function(data) {
    var linechart = function(id, d) {
        $(id).sparkline(d, {type: 'line', width: '100%', height: '50', lineColor: '#1ab394', fillColor: "transparent"})
    }
    var hospArray = [], productArray = [], marketArray = []
    $.each(data, function(i, v){hospArray.push(v.hospNum);productArray.push(v.miniProNum);marketArray.push(v.marketNum);});
    linechart("#sparkline1", hospArray)
    linechart("#sparkline2", productArray)
    linechart("#sparkline3", marketArray)
}

var salesChartsPlot = function(data) {
    var plotchart = function(id, d1, d2) {
        $(id).length && $.plot($(id), [d1, d2], {
            series: {
                lines: {show: false, fill: true},
                splines: {show: true, tension: 0.4, lineWidth: 1, fill: 0.4},
                points: {radius: 1, show: true},
                shadowSize: 2
            },
            grid: {hoverable: true, clickable: true, borderWidth: 2, color: 'transparent'},
            colors: ["#23c6c8", "#1ab394"],
            yaxis: {
                // axisLabel: "支付金额",
                // axisLabelUseCanvas: true,
                // axisLabelFontSizePixels: 12,
                // axisLabelFontFamily: 'Verdana, Arial',
                // axisLabelPadding: 3
            },
            tooltip: true
        });
    }
    // [1,2]1.X轴 2.Y轴
    var cur = [] , ago =[]
    var t;
    var tmp = []
    $.each(data, function(i, v) {
        if(v.time.split("-")[0] != t){tmp.push(v.time.split("-")[0])}
        t = v.time.split("-")[0]
    })

    $.each(data, function(i, v) {
        if(v.time.split("-")[0] == tmp[0]){
            ago.push([v.time.split("-")[1], v.sales / 100000000])
        }else{
            cur.push([v.time.split("-")[1], v.sales / 100000000])
        }
    })

    plotchart("#flot-dashboard5-chart", ago, cur)

}

window.onload = function () {
    sampleCheckFun($.cookie("token"), $.cookie("filename"))
    sampleCheckChartsLineFun($.cookie("token"), $.cookie("filename"))
    sampleCheckChartsPlotFun($.cookie("token"), $.cookie("filename"))

}

$(document).ready(function () {
    // var sparkResize;
    // $(window).resize(function (e) {
    //     clearTimeout(sparkResize);
    //     sparkResize = setTimeout(sampleCheckChartsLineFun($.cookie("token"), $.cookie("filename")), 500);
    // });
});