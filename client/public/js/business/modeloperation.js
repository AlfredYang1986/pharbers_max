/**
 * Created by Wli on 2017/1/5.
 */
var p = null;

// var test = function() {
//     callback.call(this);
//     conn.listen({
//         onTextMessage: function ( message ) {
//             console.info(message)
//             // var msg = eval("("+message.data+")")
//             // textMsg = eval("("+message.data+")")
//             // if(msg.progress == 100){
//             //     p.setPercent(0)
//             //     $(".progresstier").css("display", "none");
//             // }else{
//             //     p.setPercent(msg.progress)
//             // }
//         }
//     });
// }

$(function(){
    p = new progress2();
    conn = load_Web_IM();
    login_im("test", "1");
    // var proto = beget(callback.prototype);
    // proto.constructor = test;
    // test.prototype = proto;
    //
    // test()


    //*********************************************************************
    //功能: 运算
    //时间：20170413
    //创建：Faiz2
    //说明：根据公司对应的Panel文件和25000家医院进行模型运算。
    //*********************************************************************
    $('#operationBtn').click(function(){
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
                    $(".progresstier").css("display", "block");
                    p.setPercent(4);
                },
                error: function (e) {
                    $.tooltip('My God, 出错啦！！！');
                }
            });
        } else {
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
    $('#nextstepBtn').click(function(){
        var dataMap = JSON.stringify({
            "company": $.cookie("token")
        })
        $.ajax({
            type: "post",
            data: dataMap,
            async: false,
            url: "/commitrunresult",
            contentType: 'application/json, charset=utf-8',
            cache: false,
            dataType: "json",
            success: function (json) {
                $(".progresstier").css("display", "block");
                nextStep()
            },
            error: function (e) {
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });
})

var nextStep = function() {
    conn.listen({
        onTextMessage: function ( message ) {
            var msg = eval("("+message.data+")")
            msgIdentifying = msg.progress
            var r = p.setPercent(msg.progress)
            if(msg.progress >= 100 || r >= 100) {
                p.setPercent(0)
                $(".progresstier").css("display", "none");
                setTimeout(function(){document.getElementById("jgcx").click()}, 1000 * 1)
            }
        }
    });
}