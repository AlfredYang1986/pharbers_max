
$(function() {
    $('#data_5 .input-daterange').datepicker({
        minViewMode: 1,
        keyboardNavigation: false,
        forceParse: false,
        autoclose: true,
        todayHighlight: true
    });
});

$("#hospital-data").fileinput({
    uploadUrl: '/manage/uploadHospitalFile', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    uploadExtraData : function() {   //额外参数的关键点
        var obj = {};
        obj.filetype = "hd";
        obj.company = $.cookie("token");
        obj.timestamp = $('input[name="timestamp"]').val();
        obj.market = $('select[data-name="search-result-market"]').val();
        return obj;
    },
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
        var query_object = new Object();
        query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
        query_object['Datasource_Type'] = "Manage";
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
                }
            }
        });
    }
});

$("#hospital-match").fileinput({
    uploadUrl: '/manage/uploadHospitalFile', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    uploadExtraData : function() {   //额外参数的关键点
        var obj = {};
        obj.filetype = "hmd";
        obj.company = $.cookie("token");
        return obj;
    },
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
}).on("fileuploaded", function(event, data) {
    if(data.response){
        var query_object = new Object();
        query_object['uuid'] = data.response.result[0];
        query_object['company'] = $.cookie("token");
        query_object['Datasource_Type'] = "Manage";
        $.ajax({
            url: "/uploadfiles",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                if (data.status == "ok") { console.info("操作成功") }
            }
        });
    }
});