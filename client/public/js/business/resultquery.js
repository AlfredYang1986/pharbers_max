var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );

$(function() {
	var mySelect = $('#first-disabled2');
	$('#special').on('click', function() {
		mySelect.find('option:selected').prop('disabled', true);
		mySelect.selectpicker('refresh');
	});

	$('#special2').on('click', function() {
		mySelect.find('option:disabled').prop('disabled', false);
		mySelect.selectpicker('refresh');
	});

	$('#basic2').selectpicker({
		liveSearch: true,
		maxOptions: 1
	});

	$('#data_5 .input-daterange').datepicker({
		minViewMode: 1,
		keyboardNavigation: false,
		forceParse: false,
		autoclose: true,
		todayHighlight: true
	});

	pageResult(3);
});

var config = {
	'.chosen-select': {},
	'.chosen-select-deselect': {
		allow_single_deselect: true
	},
	'.chosen-select-no-single': {
		disable_search_threshold: 10
	},
	'.chosen-select-no-results': {
		no_results_text: 'Oops, nothing found!'
	},
	'.chosen-select-width': {
		width: "95%"
	}
}
for (var selector in config) {
	$(selector).chosen(config[selector]);
}

function pageResult(page){
    loader.show();
	var datatype = $('select[data-name="search-result-datatype"]').val();
	var market = $('select[data-name="search-result-market"]').val();
	var startdate = $('input[name="startdate"]').val();
	var enddate = $('input[name="enddate"]').val();

	var query_object = new Object();
	query_object['datatype'] = datatype;
	query_object['market'] = market;
	query_object['Timestamp'] = [startdate, enddate];
	query_object['currentPage'] = page;
    query_object['company'] = $.cookie("token");
    console.info($.cookie("token"))

	$.ajax({
		url: "/resultquery/search",
		type: 'POST',
		dataType: 'json',
		contentType: 'application/json, charset=utf-8',
		data: JSON.stringify(query_object),
		cache: false,
		success: function(data) {
			if (data.status == "ok") {
				var result = data.result.finalResult;
				var thead = "<tr>";
						thead += "<th>序号</th>"
						thead += "<th>年</th>";
						thead += "<th>月</th>";
						thead += "<th>区域</th>";
						thead += "<th>省份</th>";
						if (datatype == "城市数据") {
							thead += "<th>城市</th>";
							thead += "<th>城市级别</th>";
						} else if (datatype == "医院数据") {
							thead += "<th>城市</th>";
							thead += "<th>城市级别</th>";
							thead += "<th>医院</th>";
							thead += "<th>医院级别</th>";
						}
						thead += "<th>最小产品单位（标准_中文）</th>";
						thead += "<th>最小产品单位（标准_英文）</th>";
						thead += "<th>生产厂家（标准_中文）</th>";
						thead += "<th>生产厂家（标准_英文）</th>";
						thead += "<th>通用名（标准_中文）</th>";
						thead += "<th>通用名（标准_英文）</th>";
						thead += "<th>商品名（标准_中文）</th>";
						thead += "<th>商品名（标准_英文）</th>";
						thead += "<th>剂型（标准_中文）</th>";
						thead += "<th>剂型（标准_英文）</th>";
						thead += "<th>药品规格（标准_中文）</th>";
						thead += "<th>药品规格（标准_英文）</th>";
						thead += "<th>包装数量（标准_中文）</th>";
						thead += "<th>包装数量（标准_英文）</th>";
						thead += "<th>SKU（标准_中文）</th>";
						thead += "<th>SKU（标准_英文）</th>";
						thead += "<th>市场I（标准_中文）</th>";
						thead += "<th>市场I（标准_英文）</th>";
						thead += "<th>市场II（标准_中文）</th>";
						thead += "<th>市场II（标准_英文）</th>";
						thead += "<th>市场III（标准_中文）</th>";
						thead += "<th>市场III（标准_英文）</th>";
						thead += "<th>Value（金额）</th>";
						thead += "<th>Volume（数量）</th>";
					thead += "</tr>";
				$('thead[id="thead"]').html(thead);

                var page = data.result.page[0];

				var tbody = "";
				if (result.length != 0) {
					for (var i in result) {
						tbody += "<tr class='gradeX'>";
                        	tbody += "<td>" + (page.serial+parseInt(i)) + "</td>";
							tbody += "<td>" + result[i].Year + "</td>";
							tbody += "<td>" + result[i].Month + "</td>";
							tbody += "<td>" + result[i].Region_Name + "</td>";
							tbody += "<td>" + result[i].Province_Name + "</td>";
							var fbody = "";
							if (datatype == "城市数据") {
								fbody += "<td>" + result[i].City_Name + "</td>";
								fbody += "<td>" + result[i].City_Level + "</td>";
							} else if (datatype == "医院数据") {
								fbody += "<td>" + result[i].City_Name + "</td>";
								fbody += "<td>" + result[i].City_Level + "</td>";
								fbody += "<td>" + result[i].Hosp_Name + "</td>";
								fbody += "<td>" + result[i].Hosp_Level + "</td>";
							}
							tbody += fbody;
							tbody += "<td>" + result[i].MiniProd_Name_Ch + "</td>";
							tbody += "<td>" + result[i].MiniProd_Name_En + "</td>";
							tbody += "<td>" + result[i].Manufacturer_Ch + "</td>";
							tbody += "<td>" + result[i].Manufacturer_En + "</td>";
							tbody += "<td>" + result[i].Drug_Ch + "</td>";
							tbody += "<td>" + result[i].Drug_En + "</td>";
							tbody += "<td>" + result[i].Products_Ch + "</td>";
							tbody += "<td>" + result[i].Products_En + "</td>";
							tbody += "<td>" + result[i].DosageForm_Ch + "</td>";
							tbody += "<td>" + result[i].DosageForm_En + "</td>";
							tbody += "<td>" + result[i].DrugSpecification_Ch + "</td>";
							tbody += "<td>" + result[i].DrugSpecification_En + "</td>";
							tbody += "<td>" + result[i].Package_Quantity_Ch + "</td>";
							tbody += "<td>" + result[i].Package_Quantity_En + "</td>";
							tbody += "<td>" + result[i].sku_Ch + "</td>";
							tbody += "<td>" + result[i].sku_En + "</td>";
							tbody += "<td>" + result[i].Market_Code1_Ch + "</td>";
							tbody += "<td>" + result[i].Market_Code1_En + "</td>";
							tbody += "<td>" + result[i].Market_Code2_Ch + "</td>";
							tbody += "<td>" + result[i].Market_Code2_En + "</td>";
							tbody += "<td>" + result[i].Market_Code3_Ch + "</td>";
							tbody += "<td>" + result[i].Market_Code3_En + "</td>";
							tbody += "<td>" + result[i].Sales + "</td>";
							tbody += "<td>" + result[i].Units + "</td>";
						tbody += "</tr>";
					}
				} else {
					tbody += "<tr class='gradeX'><td valign='top' colspan='32'>没有匹配的记录</td></tr>";
                    loader.hide();
				}
				$('tbody[id="tbody"]').html(tbody);
				pageshow(data);
			} else {
				alert("请求超时。");
			}
            loader.hide();
		},
		error: function(xhr, status, error) {
			alert("请检查您的输入");
            loader.hide();
		}
	});
}

function fileExport(type) {
	var filetype;
	switch (type){
		case "csv":
            filetype = "导出csv";
			break;
		case "excel":
            filetype = "导出excel";
			break;
		case "xlsb":
            filetype = "导出xlsb";
			break;
	}
    var datatype = $('select[data-name="search-result-datatype"]').val();
    var market = $('select[data-name="search-result-market"]').val();
    var startdate = $('input[name="startdate"]').val();
    var enddate = $('input[name="enddate"]').val();

    var query_object = new Object();
    query_object['datatype'] = datatype;
    query_object['market'] = market;
    query_object['Timestamp'] = [startdate, enddate];
    query_object['company'] = $.cookie("token");
    query_object['filetype'] = filetype;

    $.ajax({
        url: "/resultquery/fileexport",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            if (data.status == "ok") {
				alert("导出成功");
            }
        },
        error: function(xhr, status, error) {
            alert("请检查您的输入");
        }
    });
}