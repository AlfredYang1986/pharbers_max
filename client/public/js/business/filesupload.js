$(function(){
    //*********************************************************************
    //功能: 确认
    //时间：20170413
    //创建：Arthas
    //说明：文件上传后，后台Python调用这些文件生成Panel文件。
    //*********************************************************************
    $('#commitBtn').click(function(){
        $.showLoading('文件确认中...',140,40);
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        $.ajax({
            url: "/cleaningdata",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                if (data.status == "ok") {
                    $.hideLoading();
                    if(data.result.result.result.head.status==0){
                        $.cookie("calc_panel_file",data.result.result.result.head.filename)
                        $.tooltip('OK, 操作成功！', 2500, true);

                    }else{
                        $.cookie("calc_panel_file",null)
                        $.tooltip('My God, 出错啦！！！');
                    }
                }else{
                    $.hideLoading();
                    $.tooltip('My God, 出错啦！！！');
                }
            },
            error:function(e){
                $.hideLoading();
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });
    //*********************************************************************
    //功能: 下一步
    //时间：20170413
    //创建：Arthas
    //说明：利用Python生成得Panel文件，及其包含的所有去重市场所对应得两万家医院做
    //将未匹配数据、医院数量、市场数量、产品数量等入MongoDB库。
    //*********************************************************************
    $('#nextstepBtm').click(function(){
        $.showLoading('数据匹配中...',140,40);
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        query_object['filename'] = $.cookie("calc_panel_file");
        $.ajax({
            type : "post",
            data : JSON.stringify(query_object),
            contentType: "application/json,charset=utf-8",
            url :"/callcheckexcel",
            cache : false,
            dataType : "json",
            success : function(json){
                $.hideLoading();
                $.tooltip('OK, 操作成功！', 2500, true);
                document.getElementById("ybjc").click();
            },
            error:function(e){
                $.hideLoading();
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });
});