$(function() {
    var i = 0
	$('#data_5 .input-daterange').datepicker({
		minViewMode: 1,
		keyboardNavigation: false,
		forceParse: false,
		autoclose: true,
		todayHighlight: true
	}).on('changeDate',function(){
	    if(i==0){
            sampleCheckFun()
	        i++
	    }else{
	        i=0
	    }
	});
});

function sampleCheckFun() {
    var market = $('select[data-name="search-result-market"]').val();
    var date = $('input[name="date_5"]').val();
    var dataMap = JSON.stringify({
        "company": $.cookie("token"),
        "market": market,
        "date": date
    });
    $.ajax({
        type: "POST",
        url: "/samplecheck/check",
        dataType: "json",
        data: dataMap,
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            Top_Current(r.result.top_mismatch);
            Top_Early(r.result.top_early[0]);
            Top_Last(r.result.top_last[0]);
            Top_Mismatch(r.result.top_mismatch);
            salesChartsPlot(r.result.top_early12,r.result.top_last12);
            dataTableAjax();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.info("Error")
        }
    });
}

function Top_Current(obj){
    var nobj = obj[0]
    if(nobj != null){
        $("#Current_Month_HospitalNum").text(nobj.HospNum);
        $("#Current_Month_ProductNum").text(nobj.ProductNum);
        $("#Current_Month_MarketNum").text(nobj.MarketNum);
    }else{
        $("#Current_Month_HospitalNum").text(0);
        $("#Current_Month_ProductNum").text(0);
        $("#Current_Month_MarketNum").text(0);
    }
}

function Top_Early(obj){
    if(obj != null){
        $("#Early_Month_HospitalNum").text(obj.HospNum);
        $("#Early_Month_ProductNum").text(obj.ProductNum);
        $("#Early_Month_marketNum").text(obj.MarketNum);
    }else{
        $("#Early_Month_HospitalNum").text(0);
        $("#Early_Month_ProductNum").text(0);
        $("#Early_Month_marketNum").text(0);
    }
}

function Top_Last(obj){
    if(obj != null){
        $("#Last_Year_HospitalNum").text(obj.HospNum);
        $("#Last_Year_ProductNum").text(obj.ProductNum);
        $("#Last_Year_MarketNum").text(obj.MarketNum);
    }else{
        $("#Last_Year_HospitalNum").text(0);
        $("#Last_Year_ProductNum").text(0);
        $("#Last_Year_MarketNum").text(0);
    }
}

function Top_Mismatch(obj) {
    var nobj = obj[0]
    if(nobj!=null){
        var mismatch_lst = nobj.MisMatch;
        $.each(mismatch_lst, function (i, v) {
            $("#hospitalList").append("<tr><td>" + (i + 1) + "</td><td>" + v.Hosp_name + "</td><td>" + v.Province + "</td><td>" + v.City + "</td><td>" + v.City_level + "</td><td><a href=\"javascript:;\"><i class=\"fa fa-times text-danger text\"></i></a></td></tr>")
        })
    }
}

function salesChartsPlot(early12,last12) {

    //var x_sales12_data = ['201511','201512','201601','201602','201603','201604','201605','201606','201607','201608','201609',"201610"]
    //var y_curr12_sales = ['1200', '1400', '1008', '1411', '1026', '1288', '1300', '800', '1100', '1000', '1118', '1322']
    //var y_last12_sales = ['1200', '1400', '808', '811', '626', '488', '1600', '1100', '500', '300', '1998', '822']

    var x_data = [];
    var y_curr12_sales = [];
    var y_last12_sales = [];
    var y_curr12_utils = [];
    var y_last12_utils = [];

    for(var item in early12){
        var obj = early12[item];
        x_data.push(obj.Date);
        y_curr12_sales.push((obj.Sales).toFixed(2));
        y_curr12_utils.push((obj.Units).toFixed(2));
    }

    //var x_utils12_data = ['201511','201512','201601','201602','201603','201604','201605','201606','201607','201608','201609',"201610"]
    //var y_curr12_utils = ['1200', '1400', '1008', '1411', '1026', '1288', '1300', '800', '1100', '1000', '1118', '1322']
    //var y_last12_utils = ['1200', '1400', '808', '811', '626', '488', '1600', '1100', '500', '300', '1998', '822']

    for(var item in last12){
        var obj = early12[item];
        x_data.push(obj.Date);
        y_last12_sales.push((obj.Sales).toFixed(2));
        y_last12_utils.push((obj.Units).toFixed(2));
    }

    var itemStyleColor = ['#23c6c8', '#1ab394'];
    Sales_Opt = {
        title: {text: '今年Vs去年(近12月销售额)',left: '50%',textAlign: 'center'},
        tooltip: {
            trigger: 'asix',
            axisPointer: {lineStyle: {color: '#ddd'}},
            backgroundColor: 'rgba(255,255,255,1)',
            padding: [5, 10],
            textStyle: {color: '#7588E4'},
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
        },
        legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
        xAxis: {
            type: 'category',
            data: x_data,
            boundaryGap: false,
            splitLine: {show: true,interval: 'auto',lineStyle: {color: [itemStyleColor[0]]}},
            axisTick: {show: false},
            axisLine: {lineStyle: {color: itemStyleColor[0]}},
            axisLabel: {margin: 10,textStyle: {fontSize: 14}}
        },
        yAxis: {
            type: 'value',
            splitLine: {lineStyle: {color: [itemStyleColor[1]]}},
            axisTick: {show: false},
            axisLine: {lineStyle: {color: itemStyleColor[1]}},
            axisLabel: {margin: 10,textStyle: {fontSize: 14}}
        },
        series: [{
            name: '去年',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: y_last12_sales,
            areaStyle: {normal: {color: '#23c6c8'}},
            itemStyle: {normal: {color: '#23c6c8'}},
            lineStyle: {normal: {width: 3}}
        }, {
            name: '今年',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: y_curr12_sales,
            areaStyle: {normal: {color: '#1ab394'}},
            itemStyle: {normal: {color: '#1ab394'}},
            lineStyle: {normal: {width: 3}}
        }]
    };

    Units_Opt = {
        title: {text: '今年Vs去年(近12月销售量)',left: '50%',textAlign: 'center'},
        tooltip: {
            trigger: 'asix',
            axisPointer: {lineStyle: {color: '#ddd'}},
            backgroundColor: 'rgba(255,255,255,1)',
            padding: [5, 10],
            textStyle: {color: '#7588E4'},
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
        },
        legend: {right: 20,orient: 'vertical',data: ['今年前12月','去年前12月']},
        xAxis: {
            type: 'category',
            data: x_data,
            boundaryGap: false,
            splitLine: {show: true,interval: 'auto',lineStyle: {color: ['#23c6c8']}},
            axisTick: {show: false},
            axisLine: {lineStyle: {color: '#23c6c8'}},
            axisLabel: {margin: 10,textStyle: {fontSize: 14}}
        },
        yAxis: {
            type: 'value',
            splitLine: {lineStyle: {color: ['#1ab394']}},
            axisTick: {show: false},
            axisLine: {lineStyle: {color: '#1ab394'}},
            axisLabel: {margin: 10,textStyle: {fontSize: 14}}
        },
        series: [{
            name: '去年',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: y_last12_utils,
            areaStyle: {normal: {color: '#23c6c8'}},
            itemStyle: {normal: {color: '#23c6c8'}},
            lineStyle: {normal: {width: 3}}
        }, {
            name: '今年',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: y_curr12_utils,
            areaStyle: {normal: {color: '#1ab394'}},
            itemStyle: {normal: {color: '#1ab394'}},
            lineStyle: {normal: {width: 3}}
        }]
    };

    var Sales = echarts.init(document.getElementById('Sales'));
    var Units = echarts.init(document.getElementById('Units'));
    Sales.setOption(Sales_Opt);
    Units.setOption(Units_Opt);
    window.addEventListener("resize", function() {
        Sales.resize();
        Units.resize();
    });
}

window.onload = function () {
    sampleCheckFun()
}