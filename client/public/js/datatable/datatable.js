var dataTableAjax = function() {
	$('#dataTables-example').DataTable({
		dom : '<"html5buttons"B>lTfgitp',
		bFilter : false,
        destroy: true,
		bLengthChange : false,
		buttons : []
	});
}