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
function downloadfile(){
	location.href = "/pharbers/files/3c1bf01f-b158-4ffc-a205-c084bb9d2629";
}