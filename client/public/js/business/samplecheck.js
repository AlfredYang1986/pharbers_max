var sampleCheckFun = function sampleCheck(company, filename) {
	var dataMap = JSON.stringify({
			"company" : company,
			"filename" : filename
	});
	$.ajax({
		type: "POST",
		url: "/samplecheck/check",
		dataType: "json",
		data: dataMap,
		contentType: 'application/json,charset=utf-8',
		success: function(r){
			if(r.result.FinalResult == "is null"){
				$("#hospitalList").empty();
			}else{
				$("#chospNum").text(r.result.FinalResult.hospNum);
				$("#cproductNum").text(r.result.FinalResult.miniProNum);
				$("#csales").text(r.result.FinalResult.sales);
				$("#hospitalList").empty();
				$.each(r.result.FinalResult.hospList,function(i, v){
					$("#hospitalList").append("<tr><td>"+(i+1)+"</td><td>"+v+"</td><td>2016</td><td>1</td><td>2016-12-12</td><td><a href=\"#\"><i class=\"fa fa-times text-danger text\"></i></a></td></tr>")
				});
			}
			dataTableAjax();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			console.info("Error")
		}
	});
}
window.onload = function() {
	sampleCheckFun($.cookie("token"), $.cookie("filename"))
}
$(document).ready(
		function() {
			var sparklineCharts = function() {
				$("#sparkline1").sparkline(
						[ 3400, 4300, 4300, 3500, 4400, 3200, 4400, 5200, 3400,
								4200, 5400, 3200 ], {
							type : 'line',
							width : '100%',
							height : '50',
							lineColor : '#1ab394',
							fillColor : "transparent"
						});

				$("#sparkline2").sparkline(
						[ 1260100, 1160100, 1560100, 1760100, 1160100, 2260100,
								2460100, 3260100, 1360100, 2360100, 3460100,
								4260100 ], {
							type : 'line',
							width : '100%',
							height : '50',
							lineColor : '#1ab394',
							fillColor : "transparent"
						});

				$("#sparkline3").sparkline(
						[ 35608100, 11608100, 25608100, 37608100, 41608100,
								32608100, 34608100, 48608100, 31608100,
								42608100, 44608100, 58608100 ], {
							type : 'line',
							width : '100%',
							height : '50',
							lineColor : '#1ab394',
							fillColor : "transparent"
						});
			};

			var sparkResize;

			$(window).resize(function(e) {
				clearTimeout(sparkResize);
				sparkResize = setTimeout(sparklineCharts, 500);
			});

			sparklineCharts();

			var data1 = [ [ 0, 4 ], [ 1, 8 ], [ 2, 5 ], [ 3, 10 ], [ 4, 4 ],
					[ 5, 16 ], [ 6, 5 ], [ 7, 11 ], [ 8, 6 ], [ 9, 11 ],
					[ 10, 20 ], [ 11, 10 ], [ 12, 13 ], [ 13, 4 ], [ 14, 7 ],
					[ 15, 8 ], [ 16, 12 ] ];
			var data2 = [ [ 0, 0 ], [ 1, 2 ], [ 2, 7 ], [ 3, 4 ], [ 4, 11 ],
					[ 5, 4 ], [ 6, 2 ], [ 7, 5 ], [ 8, 11 ], [ 9, 5 ],
					[ 10, 4 ], [ 11, 1 ], [ 12, 5 ], [ 13, 2 ], [ 14, 5 ],
					[ 15, 2 ], [ 16, 0 ] ];
			$("#flot-dashboard5-chart").length
					&& $.plot($("#flot-dashboard5-chart"), [ data1, data2 ], {
						series : {
							lines : {
								show : false,
								fill : true
							},
							splines : {
								show : true,
								tension : 0.4,
								lineWidth : 1,
								fill : 0.4
							},
							points : {
								radius : 0,
								show : true
							},
							shadowSize : 2
						},
						grid : {
							hoverable : true,
							clickable : true,

							borderWidth : 2,
							color : 'transparent'
						},
						colors : [ "#23c6c8", "#1ab394" ],
						xaxis : {},
						yaxis : {},
						tooltip : false
					});

		});