/**
 * Created by qianpeng on 2017/7/12.
 */

var anow_uploader = function (filePicker, queueList, index, filetype) {
    return WebUploader.create({
        pick: {                                 // TODO 参数说明: {Selector, Object} [可选] [默认值：undefined] 指定选择文件的按钮容器，不指定则不创建按钮.
            id: filePicker,                     // TODO 参数说明: {Seletor|dom} 指定选择文件的按钮容器，不指定则不创建按钮。注意 这里虽然写的是 id, 但是不是只支持 id, 还支持 class, 或者 dom 节点.
            innerHTML: '点击选择文件'                // TODO 参数说明: {String}
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
        fileNumLimit: 1,                       // TODO 参数说明: {int} [可选] [默认值：undefined] 验证文件总数量, 超出则不允许加入队列.
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
}

// jQuery(function() {
//
//     var uploader = new Array();                         // TODO 参数说明: 创建uploader实例数组
//     var fileCount = new Array();                        // TODO 参数说明: 创建实例文件数量数组
//     var fileSize = new Array();                         // TODO 参数说明: 创建实例文件大小数组
//
//     var percentages = new Array();                      // TODO 参数说明: 实例内所有文件进度信息数组
//     var state = new Array();                            // TODO 参数说明: uploder状态数组
//     // TODO : 可行性判断
//     if ( !WebUploader.Uploader.support() ) {
//         alert( 'Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
//         throw new Error( 'WebUploader does not support the browser you are using.' );
//     }
//
//     //循环页面中每个上传域
//     $('.uploder-container').each(function(index){
//         var filetype = "";
//         switch(index) {
//             case 0:
//                 filetype = "CPA";
//                 break;
//             case 1:
//                 filetype = "GYCX";
//                 break;
//             case 2:
//                 filetype = "Manager";
//                 break;
//             default:
//                 filetype = "Hospital";
//                 break;
//         }
//
//
//         var ratio = window.devicePixelRatio || 1,                           // TODO 参数说明: 物理像素/独立像素 默认设置为1
//         thumbnailWidth = 100 * ratio,                                       // TODO 参数说明: 缩略图大小(宽)
//         thumbnailHeight = 100 * ratio,                                      // TODO 参数说明: 缩略图大小(高)
//
//         supportTransition = (function(){
//             var s = document.createElement('p').style,
//             r = 'transition' in s || 'WebkitTransition' in s || 'MozTransition' in s || 'msTransition' in s || 'OTransition' in s;
//             s = null;
//             return r;
//         })();
//         var filePicker=$(this).find('.filePicker');                         // TODO : 上传按钮实例
//         var queueList=$(this).find('.queueList');                           // TODO : 拖拽容器实例
//         var jxfilePicker=$(this).find('.jxfilePicker');                     // TODO : 继续添加按钮实例
//         var placeholder=$(this).find('.placeholder');                       // TODO : 按钮与虚线框实例
//         var statusBar=$(this).find('.statusBar');                           // TODO : 再次添加按钮容器实例
//         var info = statusBar.find('.info');                                  // TODO : 提示信息容器实例
//         var upload = $(this).find('.uploadBtn');                            // TODO : 上传按钮容器实例
//
//         var now_uploader = anow_uploader(filePicker, queueList, index, filetype)
//
//         var queue = $('<ul class="filelist"></ul>').appendTo( queueList);   // TODO : 文件容器实例
//         percentages[index] = {};                                            // TODO 参数说明: 所有文件的进度信息，key为file id
//         state[index] = 'pedding';                                           // TODO 参数说明: 状态
//         var progress = statusBar.find( '.progress' ).hide();                // TODO 参数说明: 进度条实例
//
//         fileCount[index] = 0;                                               // TODO : 添加的文件数量
//         fileSize[index] = 0;                                                // TODO : 添加的文件总大小
//         // TODO : 初始化上传实例
//
//
//         // TODO : 添加“添加文件”的按钮
//         now_uploader.addButton({
//             id: jxfilePicker,
//             innerHTML: '添加文件'
//         });
//
//         // TODO : 当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列
//         now_uploader.on('beforeFileQueued', function(file){
//             //console.log(file);
//             //console.log(file.name);
//         });
//
//         // TODO : 当一批文件添加进队列以后触发。
//         now_uploader.on('fileQueued', function( file ) {
//             fileCount[index] = fileCount[index]+1;
//             fileSize[index] += file.size;
//             //console.info(now_uploader)
//             //console.info(index)
//             if ( fileCount[index] === 1 ) {
//                 placeholder.addClass( 'element-invisible' );
//                 statusBar.show();
//             }
//             addFile(file);
//             setState('ready');
//             updateTotalProgress();
//         });
//
//         // TODO : 当一批文件添加进队列以后触发
//         now_uploader.on('filesQueued', function(files) {
//             //filesQueued;
//         });
//
//         // TODO : 当文件被移除队列后触发。
//         now_uploader.on('fileDequeued', function( file ) {
//             fileCount[index] = fileCount[index]-1;
//             fileSize[index] -= file.size;
//             //console.info(fileCount[index])
//             //console.info(index)
//             if ( !fileCount[index] ) {
//                 setState('pedding');
//             }
//             removeFile( file );
//             updateTotalProgress();
//         });
//
//         // TODO : 当 uploader 被重置的时候触发
//         now_uploader.on('reset', function(){
//             //reset;
//         });
//
//         // TODO : 当开始上传流程时触发
//         now_uploader.on('startUpload', function(){
//             //startUpload;
//         });
//
//         // TODO : 当开始上传流程暂停时触发
//         now_uploader.on('stopUpload', function(){
//             //stopUpload;
//         });
//
//         // TODO : 当所有文件上传结束时触发
//         now_uploader.on('uploadFinished', function(){
//             //uploadFinished;
//         });
//
//         // TODO : 某个文件开始上传前触发，一个文件只会触发一次
//         now_uploader.on('uploadStart', function(file){
//             //uploadStart;
//         });
//
//         // TODO : 当某个文件的分块在发送前触发，主要用来询问是否要添加附带参数，大文件在开起分片上传的前提下此事件可能会触发多次
//         now_uploader.on('uploadBeforeSend', function(object,data,headers){
//             //uploadBeforeSend
//             //console.info(object,data,headers)
//         });
//
//         // TODO : 当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效。如果此事件handler返回值为false, 则此文件将派送server类型的uploadError事件
//         now_uploader.on('uploadAccept', function(object,ret){
//             //uploadAccept;
//         });
//
//         // TODO : 上传过程中触发，携带上传进度
//         now_uploader.on('uploadProgress', function( file, percentage ) {
//             //uploadProgress
//             var $li = $('#'+file.id), $percent = $li.find('.progress span');
//             $percent.css( 'width', percentage * 100 + '%' );
//             percentages[index][ file.id ][ 1 ] = percentage;
//             updateTotalProgress();
//         });
//
//         // TODO : 当文件上传出错时触发
//         now_uploader.on('uploadError', function(file,reason){
//             //uploadError;
//         });
//
//         // TODO : 当文件上传成功时触发。
//         now_uploader.on('uploadSuccess',function(file,reponse){
//             var filetype = now_uploader.options.formData.filetype
//             if(filetype == "Hospital"){
//                 sendSCPFile(file.name);
//             }else{
//                 //alert("文件："+file.name+"上传完成.");
//             }
//         });
//
//         // TODO : 不管成功或者失败，文件上传完成时触发。
//         now_uploader.on('uploadComplete', function(file){
//             //uploadComplete;
//         });
//
//         // TODO : 捕捉uploader事件类型，并赋值状态
//         now_uploader.on( 'all', function( type ) {
//             switch( type ) {
//                 case 'uploadFinished':
//                     setState( 'confirm');
//                     break;
//
//                 case 'startUpload':
//                     setState( 'uploading');
//                     break;
//
//                 case 'stopUpload':
//                     setState( 'paused');
//                     break;
//
//             }
//         });
//
//         // TODO : 当validate不通过时，会以派送错误事件的形式通知调用者。通过upload.on('error', handler)可以捕获到此类错误，目前有以下错误会在特定的情况下派送错来
//         now_uploader.on('error', function( handler ) {
//             switch(handler) {
//                 case 'Q_EXCEED_NUM_LIMIT':
//                     alert('添加的文件数量超出文件数量限制。');
//                     break;
//                 case 'Q_EXCEED_SIZE_LIMIT':
//                     alert('添加的文件总大小超出文件大小限制。');
//                     break;
//                 case 'Q_TYPE_DENIED':
//                     alert('文件类型有误。');
//                     break;
//             }
//         });
//
//         //*********************************************************************
//         //功能: 上传
//         //时间：20170410
//         //说明：点击上传按钮后触发。
//         //*********************************************************************
//         upload.on('click', function() {
//             now_uploader.options.formData.company = $.cookie("token");
//             now_uploader.options.formData.date = $('select[name="timestamp"]').val();
//             now_uploader.options.formData.market = $('select[data-name="search-result-market"]').val();
//             if ($(this).hasClass('disabled')) {
//                 return false;
//             }
//             if ( state[index] === 'ready' ) {
//                 now_uploader.upload();
//             } else if ( state[index] === 'paused' ) {
//                 now_uploader.upload();
//             } else if ( state[index] === 'uploading' ) {
//                 now_uploader.stop();
//             }
//         });
//
//         //*********************************************************************
//         //功能: 文件元素框实例创建
//         //时间：20170410
//         //说明：当有文件添加进来时执行，负责view的创建。
//         //*********************************************************************
//         function addFile(file) {
//             var $li = $( '<li id="' + file.id + '">' +
//                     '<p class="title">' + file.name + '</p>' +
//                     '<p class="imgWrap"></p>'+
//                     '<p class="progress"><span></span></p>' +
//                     '</li>' ),
//
//                 $btns = $('<div class="file-panel">' +
//                     '<span class="cancel">删除</span>' +
//                     '<span class="rotateRight">向右旋转</span>' +
//                     '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
//                 $prgress = $li.find('p.progress span'),
//                 $wrap = $li.find( 'p.imgWrap' ),
//                 $info = $('<p class="error"></p>');
//
//             showError = function( code ) {
//                 switch( code ) {
//                     case 'exceed_size':
//                         text = '文件大小超出';
//                         break;
//
//                     case 'interrupt':
//                         text = '上传暂停';
//                         break;
//
//                     default:
//                         text = '上传失败，请重试';
//                         break;
//                 }
//
//                 $info.text( text ).appendTo( $li );
//             };
//
//             if ( file.getStatus() === 'invalid' ) {
//                 showError(file.statusText);
//             } else {
//                 now_uploader.makeThumb( file, function( error, src ) {
//                     $wrap.text(WebUploader.formatSize(file.size));
//                     return;
//                 }, thumbnailWidth, thumbnailHeight );
//                 percentages[index][ file.id ] = [ file.size, 0 ];
//                 file.rotation = 0;
//             }
//
//             file.on('statuschange', function( cur, prev ) {
//                 if ( prev === 'progress' ) {
//                     $prgress.hide().width(0);
//                 } else if ( prev === 'queued' ) {
//                     $li.off( 'mouseenter mouseleave' );
//                     $btns.remove();
//                 }
//
//                 // 成功
//                 if ( cur === 'error' || cur === 'invalid' ) {
//                     //console.log( file.statusText );
//                     showError(file.statusText);
//                     percentages[index][ file.id ][ 1 ] = 1;
//                 } else if ( cur === 'interrupt' ) {
//                     showError( 'interrupt' );
//                 } else if ( cur === 'queued' ) {
//                     percentages[index][ file.id ][ 1 ] = 0;
//                 } else if ( cur === 'progress' ) {
//                     $info.remove();
//                     $prgress.css('display', 'block');
//                 } else if ( cur === 'complete' ) {
//                     $li.append( '<span class="success"></span>' );
//                 }
//
//                 $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
//             });
//
//             $li.on( 'mouseenter', function() {
//                 $btns.stop().animate({height: 30});
//             });
//
//             $li.on( 'mouseleave', function() {
//                 $btns.stop().animate({height: 0});
//             });
//
//             $btns.on( 'click', 'span', function() {
//                 var index = $(this).index(), deg;
//                 switch ( index ) {
//                     case 0:
//                         now_uploader.removeFile( file );
//                         return;
//                     case 1:
//                         file.rotation += 90;
//                         break;
//                     case 2:
//                         file.rotation -= 90;
//                         break;
//                 }
//
//                 if ( supportTransition ) {
//                     deg = 'rotate(' + file.rotation + 'deg)';
//                     $wrap.css({
//                         '-webkit-transform': deg,
//                         '-mos-transform': deg,
//                         '-o-transform': deg,
//                         'transform': deg
//                     });
//                 } else {
//                     $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
//                 }
//             });
//             $li.appendTo(queue);
//         }
//
//         //*********************************************************************
//         //功能: 移除文件元素框实例
//         //时间：20170410
//         //说明：负责view的销毁。
//         //*********************************************************************
//         function removeFile( file ) {
//             var $li = $('#'+file.id);
//             delete percentages[index][ file.id ];
//             updateTotalProgress();
//             $li.off().find('.file-panel').off().end().remove();
//         }
//
//         //*********************************************************************
//         //功能: 赋值上传状态
//         //时间：20170410
//         //说明：负责赋值上传状态。
//         //*********************************************************************
//         function setState(val) {
//             var file, stats;
//             if ( val === state[index] ) {
//                 return;
//             }
//             upload.removeClass( 'state-' + state[index] );
//             upload.addClass( 'state-' + val );
//             state[index] = val;
//             switch (state[index]) {
//                 case 'pedding':
//                     placeholder.removeClass( 'element-invisible' );
//                     queue.hide();
//                     statusBar.addClass( 'element-invisible' );
//                     now_uploader.refresh();
//                     break;
//                 case 'ready':
//                     placeholder.addClass( 'element-invisible' );
//                     jxfilePicker.removeClass( 'element-invisible');
//                     queue.show();
//                     statusBar.removeClass('element-invisible');
//                     now_uploader.refresh();
//                     break;
//                 case 'uploading':
//                     jxfilePicker.addClass( 'element-invisible' );
//                     progress.show();
//                     upload.text( '暂停上传' );
//                     break;
//                 case 'paused':
//                     progress.show();
//                     upload.text( '继续上传' );
//                     break;
//                 case 'confirm':
//                     progress.hide();
//                     jxfilePicker.removeClass( 'element-invisible' );
//                     upload.text( '开始上传' );
//                     stats = now_uploader.getStats();
//                     if ( stats.successNum && !stats.uploadFailNum ) {
//                         setState( 'finish' );
//                         return;
//                     }
//                     break;
//                 case 'finish':
//                     stats = now_uploader.getStats();
//                     if ( stats.successNum ) {
//                         $.tooltip('OK, 上传成功！', 2500, true);
//                     } else {
//                         state[index] = 'done';
//                         location.reload();
//                     }
//                     break;
//             }
//             updateStatus(state[index]);
//         }
//
//         //*********************************************************************
//         //功能: 更新总进度
//         //时间：20170410
//         //说明：负责更新总上传进度。
//         //*********************************************************************
//         function updateTotalProgress() {
//             var loaded = 0,total = 0,spans = progress.children(),percent;
//             $.each( percentages[index], function( k, v ) {
//                 total += v[ 0 ];
//                 loaded += v[ 0 ] * v[ 1 ];
//             } );
//             percent = total ? loaded / total : 0;
//             spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
//             spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
//             updateStatus();
//         }
//
//         //*********************************************************************
//         //功能: 更新上传状态
//         //时间：20170410
//         //说明：负责更新上传状态。
//         //*********************************************************************
//         function updateStatus(val) {
//             var text = '', stats;
//             if ( state[index] === 'ready' ) {
//                 text = '选中' + fileCount[index] + '个文件，共' + WebUploader.formatSize( fileSize[index] ) + '。';
//             } else if ( state[index] === 'confirm' ) {
//                 stats = now_uploader.getStats();
//                 if ( stats.uploadFailNum ) {
//                     text = '已成功上传' + stats.successNum+ '个文件，'+ stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件或<a class="ignore" href="#">忽略</a>'
//                 }
//             } else {
//                 stats = now_uploader.getStats();
//                 text = '共' + fileCount[index] + '个（' + WebUploader.formatSize( fileSize[index] )  + '），已上传' + stats.successNum + '个';
//                 if (stats.uploadFailNum) {
//                     text += '，失败' + stats.uploadFailNum + '个';
//                 }
//             }
//             info.html( text );
//         }
//
//         //*********************************************************************
//         //功能: 重试
//         //时间：20170410
//         //说明：重试上传失败的文件。
//         //*********************************************************************
//         info.on( 'click', '.retry', function() {
//             uploader.retry();
//         } );
//
//         //*********************************************************************
//         //功能: 忽略
//         //时间：20170410
//         //说明：忽略上传失败的文件。
//         //*********************************************************************
//         info.on( 'click', '.ignore', function() {
//             alert( 'todo' );
//         } );
//
//         upload.addClass( 'state-' + state[index] );
//         updateTotalProgress();
//
//         //*********************************************************************
//         //功能: SCP高速文件传输
//         //时间：20170410
//         //说明：两万家医院文件上传成功后，就会SCP远程传输到Aliyun服务器。
//         //*********************************************************************
//         function sendSCPFile(filename){
//             var company = $.cookie("token");
//             var timestamp = $('select[name="timestamp"]').val();
//             var market = $('select[data-name="search-result-market"]').val();
//             var query_object = new Object();
//             query_object['filename'] = md5(company+''+timestamp+''+market.replace(/\s/g, ""));
//             query_object['company'] = $.cookie("token");
//             $.ajax({
//                 type : "post",
//                 data : JSON.stringify(query_object),
//                 contentType: "application/json,charset=utf-8",
//                 url :"/filesUpload/scpCopyFiles",
//                 cache : false,
//                 dataType : "json",
//                 success : function(json){
//                     //alert("文件："+filename+"上传完成。");
//                     //console.info("文件："+filename+"上传完成。");
//                 },
//                 error:function(e){
//                     $.tooltip('My God, SCP传输失败！！！');
//                 }
//             });
//         }
//
//     });
//
// });


var createUpload = function(uploadid, index, flag) {
    var uploader = new Array();                         // TODO 参数说明: 创建uploader实例数组
    var fileCount = new Array();                        // TODO 参数说明: 创建实例文件数量数组
    var fileSize = new Array();                         // TODO 参数说明: 创建实例文件大小数组

    var percentages = new Array();                      // TODO 参数说明: 实例内所有文件进度信息数组
    var state = new Array();                            // TODO 参数说明: uploder状态数组
    // TODO : 可行性判断
    if ( !WebUploader.Uploader.support() ) {
        alert( 'Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
        throw new Error( 'WebUploader does not support the browser you are using.' );
    }



    var ratio = window.devicePixelRatio || 1,                           // TODO 参数说明: 物理像素/独立像素 默认设置为1
        thumbnailWidth = 100 * ratio,                                       // TODO 参数说明: 缩略图大小(宽)
        thumbnailHeight = 100 * ratio,                                      // TODO 参数说明: 缩略图大小(高)

        supportTransition = (function(){
            var s = document.createElement('p').style,
                r = 'transition' in s || 'WebkitTransition' in s || 'MozTransition' in s || 'msTransition' in s || 'OTransition' in s;
            s = null;
            return r;
        })();
    var filePicker=$("#"+uploadid).find('.filePicker');                         // TODO : 上传按钮实例
    var queueList=$("#"+uploadid).find('.queueList');                           // TODO : 拖拽容器实例
    var jxfilePicker=$("#"+uploadid).find('.jxfilePicker');                     // TODO : 继续添加按钮实例
    var placeholder=$("#"+uploadid).find('.placeholder');                       // TODO : 按钮与虚线框实例
    var statusBar=$("#"+uploadid).find('.statusBar');                           // TODO : 再次添加按钮容器实例
    var info = statusBar.find('.info');                                  // TODO : 提示信息容器实例
    var upload = $("#"+uploadid).find('.uploadBtn');                            // TODO : 上传按钮容器实例

    var now_uploader = anow_uploader(filePicker, queueList, index, flag)

    var queue = $('<ul class="filelist"></ul>').appendTo( queueList);   // TODO : 文件容器实例
    percentages[index] = {};                                            // TODO 参数说明: 所有文件的进度信息，key为file id
    state[index] = 'pedding';                                           // TODO 参数说明: 状态
    var progress = statusBar.find( '.progress' ).hide();                // TODO 参数说明: 进度条实例

    fileCount[index] = 0;                                               // TODO : 添加的文件数量
    fileSize[index] = 0;                                                // TODO : 添加的文件总大小
    // TODO : 初始化上传实例


    // TODO : 添加“添加文件”的按钮
    now_uploader.addButton({
        id: jxfilePicker,
        innerHTML: '添加文件'
    });

    // TODO : 当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列
    now_uploader.on('beforeFileQueued', function(file){
        //console.log(file);
        //console.log(file.name);
    });

    // TODO : 当一批文件添加进队列以后触发。
    now_uploader.on('fileQueued', function( file ) {
        fileCount[index] = fileCount[index]+1;
        fileSize[index] += file.size;
        if ( fileCount[index] === 1 ) {
            placeholder.addClass( 'element-invisible' );
            statusBar.show();
        }
        addFile(file);
        setState('ready');
        updateTotalProgress();
    });

    // TODO : 当一批文件添加进队列以后触发
    now_uploader.on('filesQueued', function(files) {
        //filesQueued;
    });

    // TODO : 当文件被移除队列后触发。
    now_uploader.on('fileDequeued', function( file ) {
        fileCount[index] = fileCount[index]-1;
        fileSize[index] -= file.size;
        //console.info(fileCount[index])
        //console.info(index)
        if ( !fileCount[index] ) {
            setState('pedding');
        }
        removeFile( file );
        updateTotalProgress();
    });

    // TODO : 当 uploader 被重置的时候触发
    now_uploader.on('reset', function(){
        //reset;
    });

    // TODO : 当开始上传流程时触发
    now_uploader.on('startUpload', function(){
        //startUpload;
    });

    // TODO : 当开始上传流程暂停时触发
    now_uploader.on('stopUpload', function(){
        //stopUpload;
    });

    // TODO : 当所有文件上传结束时触发
    now_uploader.on('uploadFinished', function(){
        //uploadFinished;
    });

    // TODO : 某个文件开始上传前触发，一个文件只会触发一次
    now_uploader.on('uploadStart', function(file){
        //uploadStart;
    });

    // TODO : 当某个文件的分块在发送前触发，主要用来询问是否要添加附带参数，大文件在开起分片上传的前提下此事件可能会触发多次
    now_uploader.on('uploadBeforeSend', function(object,data,headers){
        //uploadBeforeSend
        //console.info(object,data,headers)
    });

    // TODO : 当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效。如果此事件handler返回值为false, 则此文件将派送server类型的uploadError事件
    now_uploader.on('uploadAccept', function(object,ret){
        //uploadAccept;
    });

    // TODO : 上传过程中触发，携带上传进度
    now_uploader.on('uploadProgress', function( file, percentage ) {
        //uploadProgress
        var $li = $('#'+file.id), $percent = $li.find('.progress span');
        $percent.css( 'width', percentage * 100 + '%' );
        percentages[index][ file.id ][ 1 ] = percentage;
        updateTotalProgress();
    });

    // TODO : 当文件上传出错时触发
    now_uploader.on('uploadError', function(file,reason){
        //uploadError;
    });

    // TODO : 当文件上传成功时触发。
    now_uploader.on('uploadSuccess',function(file,reponse){
        var filetype = now_uploader.options.formData.filetype
        if(filetype == "Hospital"){
            sendSCPFile(file.name);
        }else{
            //alert("文件："+file.name+"上传完成.");
        }
    });

    // TODO : 不管成功或者失败，文件上传完成时触发。
    now_uploader.on('uploadComplete', function(file){
        //uploadComplete;
    });

    // TODO : 捕捉uploader事件类型，并赋值状态
    now_uploader.on( 'all', function( type ) {
        switch( type ) {
            case 'uploadFinished':
                setState( 'confirm');
                break;

            case 'startUpload':
                setState( 'uploading');
                break;

            case 'stopUpload':
                setState( 'paused');
                break;

        }
    });

    // TODO : 当validate不通过时，会以派送错误事件的形式通知调用者。通过upload.on('error', handler)可以捕获到此类错误，目前有以下错误会在特定的情况下派送错来
    now_uploader.on('error', function( handler ) {
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

    //*********************************************************************
    //功能: 上传
    //时间：20170410
    //说明：点击上传按钮后触发。
    //*********************************************************************
    upload.on('click', function() {
        now_uploader.options.formData.company = $.cookie("token");
        now_uploader.options.formData.date = $('select[name="timestamp"]').val();
        now_uploader.options.formData.market = $('select[data-name="search-result-market"]').val();
        if ($(this).hasClass('disabled')) {
            return false;
        }
        if ( state[index] === 'ready' ) {
            now_uploader.upload();
        } else if ( state[index] === 'paused' ) {
            now_uploader.upload();
        } else if ( state[index] === 'uploading' ) {
            now_uploader.stop();
        }
    });

    //*********************************************************************
    //功能: 文件元素框实例创建
    //时间：20170410
    //说明：当有文件添加进来时执行，负责view的创建。
    //*********************************************************************
    function addFile(file) {
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

        showError = function( code ) {
            switch( code ) {
                case 'exceed_size':
                    text = '文件大小超出';
                    break;

                case 'interrupt':
                    text = '上传暂停';
                    break;

                default:
                    text = '上传失败，请重试';
                    break;
            }

            $info.text( text ).appendTo( $li );
        };

        if ( file.getStatus() === 'invalid' ) {
            showError(file.statusText);
        } else {
            now_uploader.makeThumb( file, function( error, src ) {
                $wrap.text(WebUploader.formatSize(file.size));
                return;
            }, thumbnailWidth, thumbnailHeight );
            percentages[index][ file.id ] = [ file.size, 0 ];
            file.rotation = 0;
        }

        file.on('statuschange', function( cur, prev ) {
            if ( prev === 'progress' ) {
                $prgress.hide().width(0);
            } else if ( prev === 'queued' ) {
                $li.off( 'mouseenter mouseleave' );
                $btns.remove();
            }

            // 成功
            if ( cur === 'error' || cur === 'invalid' ) {
                //console.log( file.statusText );
                showError(file.statusText);
                percentages[index][ file.id ][ 1 ] = 1;
            } else if ( cur === 'interrupt' ) {
                showError( 'interrupt' );
            } else if ( cur === 'queued' ) {
                percentages[index][ file.id ][ 1 ] = 0;
            } else if ( cur === 'progress' ) {
                $info.remove();
                $prgress.css('display', 'block');
            } else if ( cur === 'complete' ) {
                $li.append( '<span class="success"></span>' );
            }

            $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
        });

        $li.on( 'mouseenter', function() {
            $btns.stop().animate({height: 30});
        });

        $li.on( 'mouseleave', function() {
            $btns.stop().animate({height: 0});
        });

        $btns.on( 'click', 'span', function() {
            var index = $(this).index(), deg;
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

    //*********************************************************************
    //功能: 移除文件元素框实例
    //时间：20170410
    //说明：负责view的销毁。
    //*********************************************************************
    function removeFile( file ) {
        var $li = $('#'+file.id);
        delete percentages[index][ file.id ];
        updateTotalProgress();
        $li.off().find('.file-panel').off().end().remove();
    }

    //*********************************************************************
    //功能: 赋值上传状态
    //时间：20170410
    //说明：负责赋值上传状态。
    //*********************************************************************
    function setState(val) {
        var file, stats;
        if ( val === state[index] ) {
            return;
        }
        upload.removeClass( 'state-' + state[index] );
        upload.addClass( 'state-' + val );
        state[index] = val;
        switch (state[index]) {
            case 'pedding':
                placeholder.removeClass( 'element-invisible' );
                queue.hide();
                statusBar.addClass( 'element-invisible' );
                now_uploader.refresh();
                break;
            case 'ready':
                placeholder.addClass( 'element-invisible' );
                jxfilePicker.removeClass( 'element-invisible');
                queue.show();
                statusBar.removeClass('element-invisible');
                now_uploader.refresh();
                break;
            case 'uploading':
                jxfilePicker.addClass( 'element-invisible' );
                progress.show();
                upload.text( '暂停上传' );
                break;
            case 'paused':
                progress.show();
                upload.text( '继续上传' );
                break;
            case 'confirm':
                progress.hide();
                jxfilePicker.removeClass( 'element-invisible' );
                upload.text( '开始上传' );
                stats = now_uploader.getStats();
                if ( stats.successNum && !stats.uploadFailNum ) {
                    setState( 'finish' );
                    return;
                }
                break;
            case 'finish':
                stats = now_uploader.getStats();
                if ( stats.successNum ) {
                    $.tooltip('OK, 上传成功！', 2500, true);
                } else {
                    state[index] = 'done';
                    location.reload();
                }
                break;
        }
        updateStatus(state[index]);
    }

    //*********************************************************************
    //功能: 更新总进度
    //时间：20170410
    //说明：负责更新总上传进度。
    //*********************************************************************
    function updateTotalProgress() {
        var loaded = 0,total = 0,spans = progress.children(),percent;
        $.each( percentages[index], function( k, v ) {
            total += v[ 0 ];
            loaded += v[ 0 ] * v[ 1 ];
        } );
        percent = total ? loaded / total : 0;
        spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
        spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
        updateStatus();
    }

    //*********************************************************************
    //功能: 更新上传状态
    //时间：20170410
    //说明：负责更新上传状态。
    //*********************************************************************
    function updateStatus(val) {
        var text = '', stats;
        if ( state[index] === 'ready' ) {
            text = '选中' + fileCount[index] + '个文件，共' + WebUploader.formatSize( fileSize[index] ) + '。';
        } else if ( state[index] === 'confirm' ) {
            stats = now_uploader.getStats();
            if ( stats.uploadFailNum ) {
                text = '已成功上传' + stats.successNum+ '个文件，'+ stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件或<a class="ignore" href="#">忽略</a>'
            }
        } else {
            stats = now_uploader.getStats();
            text = '共' + fileCount[index] + '个（' + WebUploader.formatSize( fileSize[index] )  + '），已上传' + stats.successNum + '个';
            if (stats.uploadFailNum) {
                text += '，失败' + stats.uploadFailNum + '个';
            }
        }
        info.html( text );
    }

    //*********************************************************************
    //功能: 重试
    //时间：20170410
    //说明：重试上传失败的文件。
    //*********************************************************************
    info.on( 'click', '.retry', function() {
        uploader.retry();
    } );

    //*********************************************************************
    //功能: 忽略
    //时间：20170410
    //说明：忽略上传失败的文件。
    //*********************************************************************
    info.on( 'click', '.ignore', function() {
        alert( 'todo' );
    } );

    upload.addClass( 'state-' + state[index] );
    updateTotalProgress();

    //*********************************************************************
    //功能: SCP高速文件传输
    //时间：20170410
    //说明：两万家医院文件上传成功后，就会SCP远程传输到Aliyun服务器。
    //*********************************************************************
    function sendSCPFile(filename){
        var company = $.cookie("token");
        var timestamp = $('select[name="timestamp"]').val();
        var market = $('select[data-name="search-result-market"]').val();
        var query_object = new Object();
        query_object['filename'] = md5(company+''+timestamp+''+market.replace(/\s/g, ""));
        query_object['company'] = $.cookie("token");
        $.ajax({
            type : "post",
            data : JSON.stringify(query_object),
            contentType: "application/json,charset=utf-8",
            url :"/filesUpload/scpCopyFiles",
            cache : false,
            dataType : "json",
            success : function(json){
                //alert("文件："+filename+"上传完成。");
                //console.info("文件："+filename+"上传完成。");
            },
            error:function(e){
                $.tooltip('My God, SCP传输失败！！！');
            }
        });
    }
}