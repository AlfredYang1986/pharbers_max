/*!
 * @copyright Copyright &copy; pharbers, www.pharbers.com, 2008 - 2017
 * @version 0.0.1
 *
 * File input styled for Baidu(FEX) that utilizes HTML5 File Input's advanced
 * features including the WebUploader API.
 *
 * Author: liwei
 * Copyright: 2017, pharbers, www.pharbers.com
 * Demo                 http://fex.baidu.com/webuploader/demo.html
 * WebUploader API      http://fex.baidu.com/webuploader/doc/index.html
 * Webuploader git仓库   git clone https://github.com/fex-team/webuploader.git
 *
 */
jQuery(function() {

    uploader = new Array();                         // TODO 参数说明: 创建uploader实例数组
    var ratio = window.devicePixelRatio || 1,       // TODO 参数说明: 物理像素/独立像素 默认设置为1
    thumbnailWidth = 100 * ratio,                   // TODO 参数说明: 缩略图大小(宽)
    thumbnailHeight = 100 * ratio,                  // TODO 参数说明: 缩略图大小(高)

    supportTransition = (function(){
        var s = document.createElement('p').style,
        r = 'transition' in s || 'WebkitTransition' in s || 'MozTransition' in s || 'msTransition' in s || 'OTransition' in s;
        s = null;
        return r;
    })();

    var percentages = {};                           // TODO 参数说明: 所有文件的进度信息，key为file id
    var state = 'pedding';                          // TODO 参数说明: 状态

    // TODO : 可行性判断
    if ( !WebUploader.Uploader.support() ) {
        alert( 'Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
        throw new Error( 'WebUploader does not support the browser you are using.' );
    }

    //循环页面中每个上传域
    $('.uploder-container').each(function(index){
        var filetype = "";
        switch(index) {
            case 0:
                filetype = "CPA";
                break;
            case 1:
                filetype = "GYCX";
                break;
            case 2:
                filetype = "Manager";
                break;
            default:
                filetype = "Hospital";
                break;
        }

        var filePicker=$(this).find('.filePicker');                         // TODO : 上传按钮实例
        var queueList=$(this).find('.queueList');                           // TODO : 拖拽容器实例
        var jxfilePicker=$(this).find('.jxfilePicker');                     // TODO : 继续添加按钮实例
        var placeholder=$(this).find('.placeholder');                       // TODO : 按钮与虚线框实例
        var statusBar=$(this).find('.statusBar');                           // TODO : 再次添加按钮容器实例
        var info =statusBar.find('.info');                                  // TODO : 提示信息容器实例
        var upload = $(this).find('.uploadBtn');                            // TODO : 上传按钮容器实例
        var fileCount = 0;                                                  // TODO : 添加的文件数量
        var fileSize = 0;                                                   // TODO : 添加的文件总大小
        var queue = $('<ul class="filelist"></ul>').appendTo( queueList);   // TODO : 文件容器实例

        // TODO : 初始化上传实例
        uploader[index] = WebUploader.create({
            pick: {                                 // TODO 参数说明: {Selector, Object} [可选] [默认值：undefined] 指定选择文件的按钮容器，不指定则不创建按钮.
                id: filePicker,                     // TODO 参数说明: {Seletor|dom} 指定选择文件的按钮容器，不指定则不创建按钮。注意 这里虽然写的是 id, 但是不是只支持 id, 还支持 class, 或者 dom 节点.
                innerHTML: '选择文件'                // TODO 参数说明: {String}
            },
            dnd: queueList,                         // TODO 参数说明: {Selector} [可选] [默认值：undefined] 指定Drag And Drop拖拽的容器，如果不指定，则不启动.
            accept: {                               // TODO 参数说明: {Arroy} [可选] [默认值：null] 指定接受哪些类型的文件.由于目前还有ext转mimeType表，所以这里需要分开指定.
                title: 'intoTypes',                 // TODO 参数说明: {String} 文字描述.
                extensions: 'xlsx,xls',             // TODO 参数说明: {String} 允许的文件后缀，不带点，多个用逗号分割.
                mimeTypes: '.xlsx,.xls'             // TODO 参数说明: {String} 多个用逗号分割.
            },
            disableGlobalDnd: true,                 // TODO 参数说明: {Selector} [可选] [默认值：false] 是否禁掉整个页面的拖拽功能，如果不禁用，图片拖进来的时候会默认被浏览器打开.
            chunked: true,                          // TODO 参数说明: {Boolean} [可选] [默认值：false] 是否要分片处理大文件上传.
            chunkSize: 5242880,                     // TODO 参数说明: {Boolean} [可选] [默认值：5242880] 如果要分片，分多大一片？ 默认大小为5M.
            chunkRetry: 3,                          // TODO 参数说明: {Boolean} [可选] [默认值：2] 如果某个分片由于网络问题出错，允许自动重传多少次？
            threads: 1,                             // TODO 参数说明: {Boolean} [可选] [默认值：3] 上传并发数。允许同时最大上传进程数.
            server: 'pharbers/files/upload',        // TODO 参数说明: {String} [必选] 文件接收服务端.
            fileVal: 'file',                        // TODO 参数说明: {Object} [可选] [默认值：'file'] 设置文件上传域的name.
            method: 'POST',                         // TODO 参数说明: {Object} [可选] [默认值：'POST'] 文件上传方式，POST或者GET.
            fileNumLimit: 10,                       // TODO 参数说明: {int} [可选] [默认值：undefined] 验证文件总数量, 超出则不允许加入队列.
            fileSizeLimit: 5242880000,              // TODO 参数说明: {int} [可选] [默认值：undefined] 验证文件总大小是否超出限制, 超出则不允许加入队列.
            fileSingleSizeLimit: 524288000,         // TODO 参数说明: {int} [可选] [默认值：undefined] 验证单个文件大小是否超出限制, 超出则不允许加入队列.
            auto : false,                           // TODO 参数说明: {Boolean} [可选] [默认值：false] 设置为 true 后，不需要手动调用上传，有文件选择即开始上传.
            formData: {                             // TODO 参数说明: {Object} [可选] [默认值：{}] 文件上传请求的参数表，每次发送都会发送此对象中的参数.
                token: index,                       // TODO: 唯一标示
                filetype: filetype,                 // TODO: 文件类型
                company: '',                        // TODO: 公司名称
                date: '',                           // TODO: 文件日期
                market: ''                          // TODO: 所属市场
            },
            compress: false                         // TODO 参数说明: {Object} [可选] 配置压缩的图片的选项。如果此选项为false, 则图片在上传前不进行压缩
        });

        // TODO : 添加“添加文件”的按钮
        uploader[index].addButton({
            id: jxfilePicker,
            innerHTML: '添加文件'
        });

        // TODO : 当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列
        uploader[index].on('beforeFileQueued', function(file){
            //console.log(file);
        });

        // TODO : 当一批文件添加进队列以后触发。
        uploader[index].on('fileQueued', function( file ) {
            fileCount++;
            fileSize += file.size;
            if ( fileCount === 1 ) {
                placeholder.addClass( 'element-invisible' );
                statusBar.show();
            }
            addFile( file,uploader[index],queue);
            setState( 'ready' ,uploader[index],placeholder,queue,statusBar,jxfilePicker);
            updateStatus('ready',info,fileCount,fileSize);
        });

        // TODO : 当一批文件添加进队列以后触发
        uploader[index].on('filesQueued', function(files) {
            //filesQueued;
        });

        // TODO : 当文件被移除队列后触发。
        uploader[index].on('fileDequeued', function( file ) {
            fileCount--;
            fileSize -= file.size;
            if ( !fileCount ) {
                setState( 'pedding',uploader[index],placeholder,queue,statusBar,jxfilePicker);
                updateStatus('pedding',info,fileCount,fileSize);
            }
            removeFile( file );
        });

        // TODO : 当 uploader 被重置的时候触发
        uploader[index].on('reset', function(){
            //reset;
        });

        // TODO : 当开始上传流程时触发
        uploader[index].on('startUpload', function(){
            //startUpload;
        });

        // TODO : 当开始上传流程暂停时触发
        uploader[index].on('stopUpload', function(){
            //stopUpload;
        });

        // TODO : 当所有文件上传结束时触发
        uploader[index].on('uploadFinished', function(){
            //uploadFinished;
        });

        // TODO : 某个文件开始上传前触发，一个文件只会触发一次
        uploader[index].on('uploadStart', function(file){
            //uploadStart;
        });

        // TODO : 当某个文件的分块在发送前触发，主要用来询问是否要添加附带参数，大文件在开起分片上传的前提下此事件可能会触发多次
        uploader[index].on('uploadBeforeSend', function(object,data,headers){

        });

        // TODO : 当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效。如果此事件handler返回值为false, 则此文件将派送server类型的uploadError事件
        uploader[index].on('uploadAccept', function(object,ret){
            //uploadAccept;
        });

        // TODO : 上传过程中触发，携带上传进度
        uploader[index].on('uploadProgress', function( file, percentage ) {
            //uploadProgress
        });

        // TODO : 当文件上传出错时触发
        uploader[index].on('uploadError', function(file,reason){
            //uploadError;
        });

        // TODO : 当文件上传成功时触发。
        uploader[index].on('uploadSuccess',function(file,reponse){
            var filetype = uploader[index].options.formData.filetype
            if(filetype == "Hospital"){
                ajaxScpCopyFile(file.name);
            }else{
                alert("文件："+file.name+"上传完成.");
            }
        });

        // TODO : 不管成功或者失败，文件上传完成时触发。
        uploader[index].on('uploadComplete', function(file){
            //uploadComplete;
        });

        // TODO : 当validate不通过时，会以派送错误事件的形式通知调用者。通过upload.on('error', handler)可以捕获到此类错误，目前有以下错误会在特定的情况下派送错来
        uploader[index].on('error', function( handler ) {
            switch(handler) {
                case 'Q_EXCEED_NUM_LIMIT':
                    alert('添加的文件数量超出文件数量限制。');
                    break;
                case 'Q_EXCEED_SIZE_LIMIT':
                    alert('添加的文件总大小超出文件大小限制。');
                    break;
                case 'Q_TYPE_DENIED':
                    alert('文件类型有误。');
                    break;
            }
        });

        // TODO : 点击上传按钮后触发。
        upload.on('click', function() {
            uploader[index].options.formData.company = $.cookie("token");
            uploader[index].options.formData.date = $('select[name="timestamp"]').val();
            uploader[index].options.formData.market = $('select[data-name="search-result-market"]').val();
            //console.info(uploader[index])
            uploader[index].upload();
        });

    });

    // TODO : 当有文件添加进来时执行，负责view的创建
    function addFile( file,now_uploader,queue) {
        var $li = $( '<li id="' + file.id + '">' +
                '<p class="title">' + file.name + '</p>' +
                '<p class="imgWrap"></p>'+
                '<p class="progress"><span></span></p>' +
                '</li>' ),

            $btns = $('<div class="file-panel">' +
                '<span class="cancel">删除</span>' +
                '<span class="rotateRight">向右旋转</span>' +
                '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
            $prgress = $li.find('p.progress span'),
            $wrap = $li.find( 'p.imgWrap' ),
            $info = $('<p class="error"></p>');

        $wrap.text( '预览中' );
        now_uploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $wrap.text(WebUploader.formatSize(file.size));
                //$wrap.text( '不能预览' );
                return;
            }
            var img = $('<img src="'+src+'">');
            $wrap.empty().append( img );
        }, thumbnailWidth, thumbnailHeight );

        percentages[ file.id ] = [ file.size, 0 ];
        file.rotation = 0;

        $li.on( 'mouseenter', function() {
            $btns.stop().animate({height: 30});
        });

        $li.on( 'mouseleave', function() {
            $btns.stop().animate({height: 0});
        });

        $btns.on( 'click', 'span', function() {
            var index = $(this).index(),
                deg;

            switch ( index ) {
                case 0:
                    now_uploader.removeFile( file );
                    return;

                case 1:
                    file.rotation += 90;
                    break;

                case 2:
                    file.rotation -= 90;
                    break;
            }

            if ( supportTransition ) {
                deg = 'rotate(' + file.rotation + 'deg)';
                $wrap.css({
                    '-webkit-transform': deg,
                    '-mos-transform': deg,
                    '-o-transform': deg,
                    'transform': deg
                });
            } else {
                $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
            }
        });

        $li.appendTo(queue);
    }

    // TODO : 负责view的销毁
    function removeFile( file ) {
        var $li = $('#'+file.id);
        delete percentages[ file.id ];
        $li.off().find('.file-panel').off().end().remove();
    }

    // TODO : 负责set状态
    function setState( val, now_uploader,placeHolder,queue,statusBar,jxfilePicker) {
        switch ( val ) {
            case 'pedding':
                placeHolder.removeClass( 'element-invisible' );
                queue.parent().removeClass('filled');
                queue.hide();
                statusBar.addClass( 'element-invisible' );
                now_uploader.refresh();
                break;
            case 'ready':
                placeHolder.addClass( 'element-invisible' );
                jxfilePicker.removeClass( 'element-invisible');
                queue.parent().addClass('filled');
                queue.show();
                statusBar.removeClass('element-invisible');
                now_uploader.refresh();
                break;
        }
    }

    // TODO : 负责更新状态
    function updateStatus(val,info,f_count,f_size) {
        var text = '';
        if ( val === 'ready' ) {
            text = '选中' + f_count + '个文件，共' + WebUploader.formatSize(f_size ) + '。';
        }
        info.html( text );
    }

    // TODO : SCP高速文件传输
    function ajaxScpCopyFile(filename){
        var query_object = new Object();
        query_object['filename'] = filename;
        query_object['company'] = $.cookie("token");
        $.ajax({
            type : "post",
            data : JSON.stringify(query_object),
            contentType: "application/json,charset=utf-8",
            url :"pharbers/files/scp",
            cache : false,
            dataType : "json",
            success : function(json){
                alert("文件："+filename+"上传完成。");
            },
            error:function(e){
                alert("SCP传输失败。")
            }
        });
    }

});