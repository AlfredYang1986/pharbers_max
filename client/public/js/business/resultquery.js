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

    //*********************************************************************
    //功能: 查询
    //时间：20170413
    //创建：Arthas
    //说明：根据市场和日期筛选结果数据列表。
    //*********************************************************************
	$('#queryBtn').click(function(){
        pageResult(1);
	});

	pageResult(1);
});

function pageResult(page){
    $.showLoading('数据加载中...',140,40);
    var datatype = $('select[data-name="search-result-datatype"]').val();
    var market = $('select[data-name="search-result-market"]').val();
    var startdate = $('input[name="startdate"]').val();
    var enddate = $('input[name="enddate"]').val();

    var query_object = new Object();
    query_object['market'] = market;
    query_object['staend'] = [startdate, enddate];
    query_object['currentPage'] = page;
    query_object['company'] = $.cookie("token");

    $.ajax({
        url: "/resultquery/search",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            $.hideLoading();
            if(data.result.status == "success"){
                var result = data.result.result.result;
                var thead = "<tr>";
                    thead += "<th>序号</th>";
                    thead += "<th>Date</th>";
                    thead += "<th>Provice</th>";
                    if(datatype=="城市数据"){
                        thead += "<th>City</th>";
                    }
                    if(datatype=="医院数据"){
                        thead += "<th>City</th>";
                        thead += "<th>Panel_ID</th>";
                    }
                    thead += "<th>Market</th>";
                    thead += "<th>Product</th>";
                    thead += "<th>Sales</th>";
                    thead += "<th>Units</th>";
                    thead += "</tr>";
                $('thead[id="thead"]').html(thead);
                var page = data.result.result.page[0];
                var tbody = "";
                if (result.length != 0) {
                    for (var i in result) {
                        tbody += "<tr class='gradeX'>";
                            tbody += "<td>" + (page.SERIAL+parseInt(i)) + "</td>";
                            tbody += "<td>" + result[i].Date + "</td>";
                            tbody += "<td>" + result[i].Provice + "</td>";
                            if(datatype=="城市数据"){
                                tbody += "<td>" + result[i].City + "</td>";
                            }
                            if(datatype=="医院数据"){
                                tbody += "<td>" + result[i].City + "</td>";
                                tbody += "<td>" + result[i].Panel_ID + "</td>";
                            }
                            tbody += "<td>" + result[i].Market + "</td>";
                            tbody += "<td>" + result[i].Product + "</td>";
                            tbody += "<td>" + result[i].Sales + "</td>";
                            tbody += "<td>" + result[i].Units + "</td>";
                        tbody += "</tr>";
                    }
                } else {
                    tbody += "<tr class='gradeX'><td valign='top' colspan='32'>没有匹配的记录</td></tr>";
                }
                $('tbody[id="tbody"]').html(tbody);
                Page(data);
            } else {
                $.tooltip(data.result.message);
            }
        }
    });
}