function pageshow(data) {
	var page = data.result.page[0];
	$('div[id="pageinfo"]').html("显示第 " + page.startrow + " 至 " + page.endrow + " 条记录，共 " + page.total + " 条记录");
	var element = $('#pageview');
	options = {
		size: "small",
		bootstrapMajorVersion: 3,
		currentPage: page.currentpage,
		numberOfPages: 5,
		totalPages: page.totlepage
	};
	element.bootstrapPaginator(options);
}
