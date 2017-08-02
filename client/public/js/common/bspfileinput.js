var createUpload = function(id, url, uploadType, maxFileSize, extData) {
    // var extData = {};
    // //{filetype: 'CPA', company: "fea9f203d4f593a96f0d6faa91ba24ba"}
    // this.addExtData = function(ext){
    //     extData = ext
    // }
    // this.console_info = function () {
    //     console.info(extData)
    // }

    $("#"+id).on('fileuploaderror', function(event, data, previewId, index) {
        // var form = data.form, files = data.files, extra = data.extra, response = data.response, reader = data.reader;
        // console.log(data);
        // console.log('文件上传失败');
        $.cookie("next",false);
    });

    $("#"+id).on('fileerror', function(event, data) {
        // console.log(data.id);
        // console.log(data.index);
        // console.log(data.file);
        // console.log(data.reader);
        // console.log(data.files);
        $.cookie("next",false);
    });

    $("#"+id).on('fileuploaded', function(event, data, previewId, index) {
        // var form = data.form, files = data.files, extra = data.extra, response = data.response, reader = data.reader;
        $.cookie("next",true);
    });

    return $("#"+id).fileinput({
        language: 'zh',
        uploadUrl: url,
        maxFileCount: 1,
        uploadExtraData: extData,
        allowedFileExtensions : uploadType,
        maxFileSize : maxFileSize,
    });
}