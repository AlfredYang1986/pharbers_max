var alertHandlers = function(){
    this.layerobj = null;
    layui.use('layer');
}

alertHandlers.prototype.success = function() {
    this.layerobj.open({
        type: 2,
        title: 'iframe父子操作',
        maxmin: true,
        shadeClose: false, //点击遮罩关闭层
        area : ['800px' , '520px'],
        content: 'http://www.baidu.com'
    });
}
//
// alertHandler.prototype.error = function() {
//
// }
//
// alertHandler().prototype.content = function() {
//
// }
//
// alertHandler().prototype.contentIframe = function() {
//
// }
