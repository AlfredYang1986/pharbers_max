$("#file-1").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
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
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
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
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
	if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
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
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
	if(data.response){
		var query_object = new Object();
		query_object['uuid'] = data.response.result[0];
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
		    		console.info("PharmaTrust市场上传成功");
				}
			}
		});
	}
});
function downloadfile(type){
	var filename;
	switch (type) {
		case "cpaproduct":
			filename = "2359310c-0598-4c18-a361-75252e1be9b8";
			break;
		case "cpamarket":
			filename = "8b997b55-e6a6-45fd-a58b-25fd2f94f45e";
			break;
		case "ptrustproduct":
			filename = "0e68d410-f94c-4e5f-951e-92b71916fa63";
			break;
		case "ptrustmarket":
			filename = "50cd6b75-529c-4fb0-b1e0-d407b7960ef2";
			break;
	}
	location.href = "/pharbers/files/"+filename;
}