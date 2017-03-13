/**
 * Created by Wli on 2017/1/5.
 */
$(function(){
    $('#operation').click(function(){
        if($.cookie("filename") != null) {
            var dataMap = JSON.stringify({
                "filename" : $.cookie("filename"),
                "company" : $.cookie("token"),
                "filetype":  $.cookie("filetype")
            })
            //098f6bcd4621d373cade4e832627b4f6
            $.ajax({
                type : "post",
                data : dataMap,
                async : false,
                url :"/callrunmodel",
                contentType: 'application/json, charset=utf-8',
                cache : false,
                dataType : "json",
                success : function(json){
                    alert("正在运算")
                },
                error:function(e){
                    alert("Error")
                }
            });
        }else{
            alert("您还没上传文件")
        }
    });

    $('#commitresult').click(function(){
        alert("操作成功")
    });
});