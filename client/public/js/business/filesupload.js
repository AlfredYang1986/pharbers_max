var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );
$(function(){

})

function excelCheck(file, type) {
	var dataMap = JSON.stringify({
        "filename" : file,
		"company" : $.cookie("token"),
		"filetype": type
    })

    $.ajax({
        type : "post",
        data : dataMap,
        async : false,
        contentType: "application/json,charset=utf-8",
        url :"/callcheckexcel",
        cache : false,
        dataType : "json",
        success : function(json){
            $.cookie("filename", file)
            $.cookie("filetype", type)
            loader.hide();
            document.getElementById("ybjc").click()
        },
        error:function(e){
            alert("Error")
        }
    });
}

$("#file-1").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
		query_object['Datasource_Type'] = "CPA产品";
		$.ajax({
			url: "/uploadfiles",
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json, charset=utf-8',
			data: JSON.stringify(query_object),
			cache: false,
			success: function(data) {
				if (data.status == "ok") {
                    loader.show();
                    setTimeout(excelCheck(query_object.uuid, "0"), 1000)
		    		console.info("CPA产品上传成功");
				}
			}
		});
	}
});

$("#file-2").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
		query_object['Datasource_Type'] = "CPA市场";
		$.ajax({
			url: "/uploadfiles",
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json, charset=utf-8',
			data: JSON.stringify(query_object),
			cache: false,
			success: function(data) {
				if (data.status == "ok") {
                    loader.show();
                    excelCheck(query_object.uuid, "1")
		    		console.info("CPA市场上传成功");
				}
			}
		});
	}
});

$("#file-3").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
	if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
		query_object['Datasource_Type'] = "PharmaTrust产品";
		$.ajax({
			url: "/uploadfiles",
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json, charset=utf-8',
			data: JSON.stringify(query_object),
			cache: false,
			success: function(data) {
				if (data.status == "ok") {
                    loader.show();
                    excelCheck(query_object.uuid, "2")
		    		console.info("PharmaTrust产品上传成功");
				}
			}
		});
	}
});

$("#file-4").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
	if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
		query_object['Datasource_Type'] = "PharmaTrust市场";
		$.ajax({
			url: "/uploadfiles",
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json, charset=utf-8',
			data: JSON.stringify(query_object),
			cache: false,
			success: function(data) {
				if (data.status == "ok") {
                    loader.show();
                    excelCheck(query_object.uuid, "3")
		    		console.info("PharmaTrust市场上传成功");
				}
			}
		});
	}
});
function downloadfile(type){
	var filename;
	switch (type) {
		case "CPA产品":
			filename = "CPA产品.xlsx";
			break;
		case "CPA市场":
			filename = "CPA市场数据.xlsx";
			break;
		case "PharmaTrust产品":
			filename = "PharmaTrust产品.xlsx";
			break;
		case "PharmaTrust市场":
			filename = "PharmaTrust市场数据.xlsx";
			break;
	}
	location.href = "/pharbers/files/"+filename;
}