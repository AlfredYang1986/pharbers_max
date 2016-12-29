$("#file-1").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1000,
    maxFilesNum: 10,
    //allowedFileTypes: ['image', 'video', 'flash'],
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
});

$("#file-2").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1000,
    maxFilesNum: 10,
    //allowedFileTypes: ['image', 'video', 'flash'],
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
});

$("#file-3").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1000,
    maxFilesNum: 10,
    //allowedFileTypes: ['image', 'video', 'flash'],
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
});

$("#file-4").fileinput({
    //uploadUrl: 'http://127.0.0.1:9001/pharbers/files/upload', // you must set a valid URL here else you will get an error
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1000,
    maxFilesNum: 10,
    //allowedFileTypes: ['image', 'video', 'flash'],
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
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