/**
 * Created by Wli on 2017/1/5.
 */
$(function(){
    $('#operation').click(function(){
        if($.cookie("filename") != null) {
            var dataMap = {
                "filename" : $.cookie("filename"),
                "company" : $.cookie("token"),
                "filetype":  $.cookie("filetype")
            }
            $.ajax({
                type : "get",
                data : dataMap,
                async : false,
                url :"http://59.110.31.215:5000/calc",
                cache : false,
                dataType : "jsonp",
                jsonp: "callbackparam",
                jsonpCallback:"jsonpCallback1",
                success : function(json){
                    alert("正在运算,大约5-6分左右")
                    console.info(json);
                },
                error:function(e){
                    alert("Error")
                }
            });
        }else{
            alert("您还没上传文件")
        }
        // var query_object = new Object();
        // query_object['company'] = $.cookie("token");
        // $.ajax({
        //     url: "/modeloperation/operation",
        //     type: 'POST',
        //     dataType: 'json',
        //     contentType: 'application/json, charset=utf-8',
        //     data: JSON.stringify(query_object),
        //     cache: false,
        //     success: function(data) {
        //         if (data.status == "ok") {
        //             console.info("测试成功");
        //         }
        //     }
        // });
    });
});