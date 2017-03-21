$("#hospital-data").fileinput({
    uploadUrl: '/manage/uploadHospitalFile', // you must set a valid URL here else you will get an error
    allowedFileExtensions : ['xlsx', 'xls'],
    overwriteInitial: false,
    maxFileSize: 50000,
    maxFilesNum: 1,
    uploadExtraData : function() {   //额外参数的关键点
        var obj = {};
        obj.company = $.cookie("token");
        obj.timestamp = $('select[name="timestamp"]').val();
        obj.market = $('select[data-name="search-result-market"]').val();
        return obj;
    },
    slugCallback: function(filename) {
        return filename.replace('(', '_').replace(']', '_');
    }
});