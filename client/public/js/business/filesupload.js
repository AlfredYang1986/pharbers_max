var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );
$(function(){

})

function excelCheck(file) {
	var dataMap = JSON.stringify({
        "filename" : file,
		"company" : $.cookie("token")
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
            loader.hide();
            document.getElementById("ybjc").click()
        },
        error:function(e){
            alert("Error")
        }
    });
}

$("#file-0").fileinput({
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
        query_object['Datasource_Type'] = "Client";
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
                    setTimeout(excelCheck(query_object.uuid), 1000)
                    $.cookie("filename", query_object.uuid)
                    alert("上传完成！")
                    console.info("Panel文件上传");
                }
            }
        });
    }
});


$("#file-1").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("CPAP",data)
});

$("#file-2").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("CPAM",data)
});

$("#file-3").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
	classifyFiles("PTP",data)
});

$("#file-4").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("PTM",data)
});

function classifyFiles(filetype,data){
    var query_object = new Object();
    query_object['filename'] = data.response.result[0];
    query_object['company'] = $.cookie("token");
    query_object['year'] = $('select[name="year"]').val();
    query_object['filetype'] = filetype;
    $.ajax({
        url: "/classifyFiles",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            if (data.status == "ok") {
                console.info("上传成功")
            }
        }
    });
}

function commitUp(){
    loader.show();
    var query_object = new Object();
    query_object['company'] = $.cookie("token");
    query_object['year'] = $('select[name="year"]').val();
    $.ajax({
        url: "/cleaningdata",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            if (data.status == "ok") {
                loader.hide();
                alert("操作成功")
                //loader.show();
                //excelCheck(query_object.uuid, "3");
            }
        }
    });
}

function downloadfile(filename){
	var query_object = new Object();
    query_object['filename'] = filename;
    $.ajax({
        url: "/filesexists",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            if (data.status == "ok") {
                if(data.result.result){
                    location.href = "/pharbers/files/"+filename;
                }else{
                    alert("template file does not exist.")
                }
            }
        }
    });
}