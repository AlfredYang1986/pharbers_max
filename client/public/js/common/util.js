// 获取地址栏后端参数
(function ($) {
    $.getUrlParam = function(name, flag) {
        var uri = location.href;
        if(flag) {
            return uri.substring(uri.lastIndexOf('/') + 1)
        }else {
            return uri.substring(uri.indexOf(name) + name.length, uri.lastIndexOf('/'))
        }

    }
})(jQuery);