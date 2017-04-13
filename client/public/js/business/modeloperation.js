/**
 * Created by Wli on 2017/1/5.
 */
$(function(){
    //*********************************************************************
    //功能: 运算
    //时间：20170413
    //创建：Faiz2
    //说明：根据公司对应的Panel文件和25000家医院进行模型运算。
    //*********************************************************************
    $('#operationBtn').click(function(){
        $.showLoading('模型运算中...',140,40);
        if ($.cookie("calc_panel_file") != null) {
            var dataMap = JSON.stringify({
                "company": $.cookie("token"),
                "filename": $.cookie("calc_panel_file")
            })
            $.ajax({
                type: "post",
                data: dataMap,
                async: false,
                url: "/callrunmodel",
                contentType: 'application/json, charset=utf-8',
                cache: false,
                dataType: "json",
                success: function (json) {
                    $.hideLoading();
                    $.tooltip("本次运算可能会耗时半小时以上，稍后我们会以邮件的形式发送给您，请您点击确定按钮安全退出。", 12500, true);
                },
                error: function (e) {
                    $.hideLoading();
                    $.tooltip('My God, 出错啦！！！');
                }
            });
        } else {
            $.hideLoading();
            $.tooltip('您生成的panel文件无效，请核对后重新生成！！！');
        }
    });
    //*********************************************************************
    //功能: 确认->下一步
    //时间：20170413
    //创建：Arthas
    //修订：
    //说明：确认模型运算后的结果，完成后跳转结果查询页面。
    //*********************************************************************
    $('#nextstepBtm').click(function(){
        $.showLoading('运算结果确认中...',140,40);
        var dataMap = JSON.stringify({
            "company": $.cookie("token")
        });
        $.ajax({
            type: "post",
            data: dataMap,
            async: false,
            url: "/commitrunresult",
            contentType: 'application/json, charset=utf-8',
            cache: false,
            dataType: "json",
            success: function (json) {
               $.hideLoading();
               $.tooltip('OK, 操作成功！', 2500, true);
               document.getElementById("jgcx").click()
            },
            error: function (e) {
                $.hideLoading();
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });
});