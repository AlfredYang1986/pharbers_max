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