function Page(data) {
    var options, element = $('#pageview');
	try {
        element.show()
        var page = data.result.result.page[0];
        $('div[id="pageinfo"]').html("显示第 " + page.ROW_START + " 至 " + page.ROW_END + " 条记录，共 " + page.TOTLE_RECORD + " 条记录");
        options = {
            size: "small",
            bootstrapMajorVersion: 3,
            currentPage: page.PAGE_CURRE,
            numberOfPages: 5,
            totalPages: page.TOTLE_PAGE
        };
        element.bootstrapPaginator(options);
	}catch(ex) {
        element.hide()
	}
}
