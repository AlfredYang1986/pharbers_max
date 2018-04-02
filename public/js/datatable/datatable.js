//*********************************************************************
//功能: 分页查询（单实例）
//时间：20170301
//创建：qianpeng
//说明：纯前端分页。
//*********************************************************************
var dataTableAjax = function(d) {
	$('#dataTables-example').DataTable({
		dom : '<"html5buttons"B>lTfgitp',
		bFilter : false,
        data: d,
        destroy : true,
		bLengthChange : false,
		buttons : []
	});
}
//*********************************************************************
//功能: 分页查询（多实例）
//时间：20170509
//创建：liwei
//说明：纯前端分页。
//*********************************************************************
var dataTableMultiAjax = function(d,id) {
	$(id).DataTable({
		dom : '<"html5buttons"B>lTfgitp',
		bFilter : false,
        data: d,
        destroy : true,
		bLengthChange : false,
		buttons : []
	});
}