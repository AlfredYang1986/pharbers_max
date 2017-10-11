/**
 * Created by yym on 10/11/17.
 */
var Ajax = function () {

};

Ajax.prototype.asyncPost = function (url, data, successfun, errorfun) {
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
            successfun(data);
        },
        error : function (data) {
            errorfun(data);
        }


    })
};

Ajax.prototype.syncPost = function (url, data, successfun, errorfun) {
    $.ajax({
        url : url,
        type : "POST",
        data : data,
        dataType : "json",
        contentType : "application/json,charset=utf-8",
        Accept : "application/json,charset=utf-8",
        cache : false,
        async : false,
        success : function (data) {
            successfun(data);
        },
        error : function (data) {
            errorfun(data);
        }


    })
};

Ajax.prototype.baseCall = function (url, data, type, successfun, errorfun) {
    $.ajax({
        type: type,
        url: url,
        dataType: "json",
        cache: false,
        data: data,
        contentType: "application/json,charset=utf-8",
        Accept: "application/json,charset=utf-8",
        success: function (data) {
            successfun(data)
        },
        error: function (e) {
                errorfun(e)
            }
        });
};