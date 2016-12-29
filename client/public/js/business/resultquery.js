$(function(){
    var mySelect = $('#first-disabled2');
    $('#special').on('click', function () {
      mySelect.find('option:selected').prop('disabled', true);
      mySelect.selectpicker('refresh');
    });

    $('#special2').on('click', function () {
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
    searchResult();
});

var config = {
        '.chosen-select'           : {},
        '.chosen-select-deselect'  : {allow_single_deselect:true},
        '.chosen-select-no-single' : {disable_search_threshold:10},
        '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
        '.chosen-select-width'     : {width:"95%"}
        }
for (var selector in config) {
    $(selector).chosen(config[selector]);
}

function searchResult() {
	var datatype = $('select[data-name="search-result-datatype"]').val();
	var market = $('select[data-name="search-result-market"]').val();
	var startdate = $('input[name="startdate"]').val();
	var enddate = $('input[name="enddate"]').val();

	//alert("数据类型："+datatype+"市场："+market+"开始日期："+startdate+"结束日期："+enddate+"条目数："+count);

	var query_object = new Object();
	query_object['datatype'] = datatype;
	query_object['market'] = market;
	query_object['Timestamp'] = [startdate,enddate];
	
    $.ajax({
        url: "/resultquery/search",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function (data) {
        	if (data.status == "ok") {
            var result = data.result.finalResult;
            var thead = "<tr><th>年</th><th>月</th><th>区域</th><th>省份</th>";
								if(datatype == "城市数据"){ 
									thead += "<th>城市</th><th>城市级别</th>";
								} else if (datatype == "医院数据"){ 
									thead += "<th>城市</th><th>城市级别</th><th>医院</th><th>医院级别</th>";
								}
								thead += "<th>最小产品单位（标准_中文）</th><th>最小产品单位（标准_英文）</th><th>生产厂家（标准_中文）</th><th>生产厂家（标准_英文）</th><th>通用名（标准_中文）</th><th>通用名（标准_英文）</th>";
								thead += "<th>商品名（标准_中文）</th><th>商品名（标准_英文）</th><th>剂型（标准_中文）</th><th>剂型（标准_英文）</th><th>药品规格（标准_中文）</th><th>药品规格（标准_英文）</th><th>包装数量（标准_中文）</th><th>包装数量（标准_英文）</th><th>SKU（标准_中文）</th>";
								thead += "<th>SKU（标准_英文）</th><th>市场I（标准_中文）</th><th>市场I（标准_英文）</th><th>市场II（标准_中文）</th><th>市场II（标准_英文）</th><th>市场III（标准_中文）</th><th>市场III（标准_英文）</th><th>Value（金额）</th><th>Volume (Unit)</th></tr>";
            $('thead[id="thead"]').html(thead);
            var tbody = "";
            if(result.length != 0){
            		for(var i in result){
                    tbody += "<tr class='gradeX'><td>"+result[i].Year+"</td><td>"+result[i].Month+"</td><td>"+result[i].Region_Name+"</td><td>"+result[i].Province_Name+"</td>";
											var fbody ="";
                    if(datatype == "城市数据"){ 
                    	fbody = "<td>"+result[i].City_Name+"</td><td>"+result[i].City_Level+"</td>";
											} else if (datatype == "医院数据"){ 
												fbody = "<td>"+result[i].City_Name+"</td><td>"+result[i].City_Level+"</td><td>"+result[i].Hosp_Name+"</td><td>"+result[i].Hosp_Level+"</td>";
											}
                    tbody += fbody;
                   	tbody += "<td>"+result[i].MiniProd_Name_Ch+"</td><td>"+result[i].MiniProd_Name_En+"</td><td>"+result[i].Manufacturer_Ch+"</td><td>"+result[i].Manufacturer_En+"</td><td>"+result[i].Drug_Ch+"</td><td>"+result[i].Drug_En+"</td>";
                   	tbody += "<td>"+result[i].Products_Ch+"</td><td>"+result[i].Products_En+"</td><td>"+result[i].DosageForm_Ch+"</td><td>"+result[i].DosageForm_En+"</td><td>"+result[i].DrugSpecification_Ch+"</td><td>"+result[i].DrugSpecification_En+"</td>";
                   	tbody += "<td>"+result[i].Package_Quantity_Ch+"</td><td>"+result[i].Package_Quantity_En+"</td><td>"+result[i].sku_Ch+"</td><td>"+result[i].sku_En+"</td><td>"+result[i].Market_Code1_Ch+"</td><td>"+result[i].Market_Code1_En+"</td>";
                   	tbody += "<td>"+result[i].Market_Code2_Ch+"</td><td>"+result[i].Market_Code2_En+"</td><td>"+result[i].Market_Code3_Ch+"</td><td>"+result[i].Market_Code3_En+"</td><td>"+result[i].Sales+"</td><td>"+result[i].Units+"</td></tr>";
                }
            }else{
            	tbody += "<tr class='gradeX'><td valign='top' colspan='32'>没有匹配的记录</td></tr>";
            }
            $('tbody[id="tbody"]').html(tbody);
            $('div[id="DataTables_Table_0_info"]').html("显示第 1 至 10 项记录，共 "+data.result.total+" 项")
            $('div[id="DataTables_Table_0_paginate"]').html("<ul class='pagination'><li class='paginate_button previous disabled' id='DataTables_Table_0_previous' onclick='page(this)'><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='0' tabindex='0'>上一页</a></li><li class='paginate_button active'><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='1' tabindex='0'>1</a></li><li class='paginate_button '><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='2' tabindex='0'>2</a></li><li class='paginate_button '><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='3' tabindex='0'>3</a></li><li class='paginate_button '><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='4' tabindex='0'>4</a></li><li class='paginate_button '><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='5' tabindex='0'>5</a></li><li class='paginate_button '><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='6' tabindex='0'>6</a></li><li class='paginate_button next' id='DataTables_Table_0_next'><a href='#' aria-controls='DataTables_Table_0' data-dt-idx='7' tabindex='0'>下一页</a></li></ul>");
          } else {
              alert("请求超时。");
          }
        },
        error: function (xhr, status, error) {
            alert("请检查您的输入");
        }
    })
}

function page(index) {
	alert(index);
}