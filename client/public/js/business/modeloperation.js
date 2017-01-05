/**
 * Created by Wli on 2017/1/5.
 */
$(function(){
    $('#operation').click(function(){
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        $.ajax({
            url: "/modeloperation/operation",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                if (data.status == "ok") {
                    console.info("测试成功");
                }
            }
        });
    });
});