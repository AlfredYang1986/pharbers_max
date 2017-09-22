var createUpload = function(id, url, uploadType, maxFileSize, extData) {
    // var extData = {};
    // //{filetype: 'CPA', company: "fea9f203d4f593a96f0d6faa91ba24ba"}
    // this.addExtData = function(ext){
    //     extData = ext
    // }
    // this.console_info = function () {
    //     console.info(extData)
    // }

    $("#"+id).fileinput({
        language: 'zh',
        uploadUrl: url,
        validateInitialCount: true,
        overwriteInitial: false,
        minFileCount: 1,
        maxFileCount: 1,
        maxFileCountFlag: false, //新增参数，控制原有上传逻辑，为了不破坏改插件的结构，采用参数配置方式（其实正确的做法是获取文件Array缓存然后进行重新注入，这里偷懒没做）源码已经加入注释
        uploadExtraData: extData,
        allowedFileExtensions : uploadType,
        maxFileSize : maxFileSize
    });

    $("#"+id).on('fileuploaderror', function(event, data, previewId, index) {});

    $("#"+id).on('fileerror', function(event, data) {
        $.cookie("next",false);
        $.tooltip('文件上传失败！');
    });


    $("#"+id).on('fileuploaded', function(event, data, previewId, index) {
        //尝试按照官方api里提到的override但是，先走的这里设置的，最后才走的内部，还是给清空stack里的东西，你们可以挑战下
        // $("#"+id).fileinput('updateStack', index, data.files);
        $.cookie("next",true);
        $.tooltip('文件上传完成！', 3000, true);
    });
}