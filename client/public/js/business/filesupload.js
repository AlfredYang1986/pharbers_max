var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );
$(function(){

})

function excelCheck() {
//	loader.show();
	var query_object = new Object();
    query_object['company'] = $.cookie("token");
    $.cookie("calc_panel_file","CPA_GYCX_Others_panel_2016_INF.xlsx");
    query_object['filename'] = $.cookie("calc_panel_file");
    $.ajax({
        type : "post",
        data : JSON.stringify(query_object),
        contentType: "application/json,charset=utf-8",
        url :"/callcheckexcel",
        cache : false,
        dataType : "json",
        success : function(json){
        alert("操作成功")
            loader.hide();
            document.getElementById("ybjc").click()
        },
        error:function(e){
            alert("Error")
        }
    });

//    loader.hide();
//    document.getElementById("ybjc").click()
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


$("#cpa").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1024000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("CPA",data)
});

$("#gycx").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1024000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("GYCX",data)
});
/*
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
});*/

$("#manager").fileinput({
    uploadUrl: 'pharbers/files/upload', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 1024000,
    maxFilesNum: 10,
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    classifyFiles("Manage",data)
});



function classifyFiles(filetype,data){
    var query_object = new Object();
    query_object['filename'] = data.response.result[0];
    query_object['company'] = $.cookie("token");
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
    $.ajax({
        url: "/cleaningdata",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            console.info(data)
            if (data.status == "ok") {
                if(data.result.result.result.head.status==0){
                    $.cookie("calc_panel_file",data.result.result.result.head.filename)
                    alert("Python Code调用成功。")
                }else{
                    $.cookie("calc_panel_file",null)
                    alert("Python Code调用失败，文件内部出现错误。")
                }
                loader.hide();
            }
            loader.hide();
        }
    });
}

function downloadfile(filename){
	var query_object = new Object();
    query_object['filename'] = filename;
    query_object['company'] = $.cookie("token");
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