/**
 * Created by yym on 10/11/17.
 */

var AjaxCall = function () {

};
//异步post
AjaxCall.prototype.asyncPost = function (url, data, successFun, errorFun, beforeFun, completeFun) {
    var errorFun = errorFun || function (e) {
        console.log(e)
    };
    var beforeFun = beforeFun || function () {};
    var completeFun = completeFun || function () {};

    $.ajax({
        url : url,
        type : "POST",
        data : data,
        dataType : "json",
        contentType : "application/json,charset=utf-8",
        Accept : "application/json,charset=utf-8",
        cache : false,
        async : true,
        success : function (data) {
            successFun(data);
        },
        error : function (e) {
            errorFun(e);
        },
        beforeSend : function () {
            beforeFun();
        },
        complete : function () {
            completeFun();
        }

    })
};

AjaxCall.prototype.baseCall = function (url, data, type, successFun, errorFun, beforeFun, completeFun, g) {
    var errorFunction = errorFun || function (e) {console.error(e)};
    var beforeFunction = beforeFun || function () {};
    var completeFunction = completeFun || function (e) {};
    var gg;
    if ( g !== undefined) { gg = g } else { gg = true }
    $.ajax({
        global: gg,
        type: type,
        url: url,
        dataType: "json",
        cache: false,
        data: data,
        contentType: "application/json,charset=utf-8",
        Accept: "application/json,charset=utf-8",
        success: function (data) {successFun(data)},
        error: errorFunction,
        beforeSend : beforeFunction,
        complete : completeFunction
    });

};