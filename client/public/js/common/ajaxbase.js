/**
 * Created by qianpeng on 2017/7/13.
 */

//后续还会增加

/**
 * 公用ajax调用，初步版本
 */
var ajaxData = function(url, data, type, successfun, errorfun) {
    $.ajax({
        type: type,
        url: url,
        dataType: "json",
        cache: false,
        data: data,
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            successfun(data)
        },
        error: function (e) {
            errorfun(e)
        }
    });
}
