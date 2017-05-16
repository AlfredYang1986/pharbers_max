var p;
$(function(){
    p = new progress2();
    load_im()
    setProgress();

    $("#generate_panel_file").hide();
    $("#generate_sample_data").hide();

    $("#f_nextstepBtn").click(function () {
        $("#upload_file").hide();
        $("#generate_panel_file").show();
        setProgressStart(1000 * 3);
        $(".progresstier").css("display", "block");
        p.setPercent(10);
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        query_object["uname"] = $.cookie('webim_user');
        $("#s_nextstepBtn").removeAttr("disabled");
        $.ajax({
            url: "/uploadbefore",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                if (data.status == "ok") {
                    if(data.result.result.result.head.status==0){
                        var result = data.result.result.result.head.result;
                        var arr = result.split("#");
                        var html = "<div class='col-lg-6 text-left'>"
                        for(var i in arr){
                            var obj = arr[i];
                            if(obj!=null && obj!=""){
                                html += "<label class='checkbox-inline'><input type='checkbox' value='"+obj+"'> "+obj+"</label>";
                            }
                        }
                        html += "</div>";
                        $('#resultDiv')[0].innerHTML = html;
                        $('label[id="generate_panel_content"]').text("文件检查通过，请勾选月份，继续点击下一步。");
                        //$.tooltip('OK, 操作成功！', 2500, true);
                    }else{
                        $.cookie("calc_panel_file",null)
                        $("#s_nextstepBtn").attr({"disabled":"disabled"});
                        $('label[id="generate_panel_content"]').text("文件检查失败，请点击上一步，返回上传页面，重新上传文件。");
                        //$.tooltip('My God, 出错啦！！！');
                    }
                }else{
                    $.tooltip('My God, 出错啦！！！');
                }
            },
            error:function(e){
                $.tooltip('My God, 出错啦！！！');
            }
        });
    });

    $("#s_prevstepBtn").click(function () {
        $("#upload_file").show();
        $("#generate_panel_file").hide();
        $("#generate_sample_data").hide();
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        $.ajax({
            url: "/uploadfiles/removefiles",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                // if (data.status == "ok") {
                //     $("#upload_file").show();
                //     $("#generate_panel_file").hide();
                //     $("#generate_sample_data").hide();
                // }
                document.getElementById("wjsc").click();
            }
        });
    });

    $("#s_nextstepBtn").click(function () {
        $("#upload_file").hide();
        $("#generate_panel_file").hide();
        $("#generate_sample_data").show();
        setProgressStart(1000 * 5);
        var ck = $(':input[type=checkbox]');
        var checked = ""
        ck.each(function(){
            if($(this).is(':checked')){
                checked += $(this).val() + "#"
            }
        })
        $("#t_nextstepBtn").removeAttr("disabled");
        if(checked!=""){
            $(".progresstier").css("display", "block");
            var query_object = new Object();
            query_object['company'] = $.cookie("token");
            query_object['yms'] = checked;
            query_object['uname'] = $.cookie('webim_user');
            $.ajax({
                url: "/cleaningdata",
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json, charset=utf-8',
                data: JSON.stringify(query_object),
                cache: false,
                success: function(data) {
                    if (data.status == "ok") {
                        if(data.result.result.result.head.status==0){
                            $.cookie("calc_panel_file",data.result.result.result.head.result)
                            //$.tooltip('OK, 操作成功！', 2500, true);
                            $('label[id="generate_sample_content"]').text("生成panel文件成功，请继续点击下一步。");
                        }else{
                            $.cookie("calc_panel_file",null)
                            //$.tooltip('My God, 出错啦！！！');
                            $("#t_nextstepBtn").attr({"disabled":"disabled"});
                            $('label[id="generate_sample_content"]').text("生成panel文件失败，请点击上一步，返回文件上传页面或文件检查页面重新操作。");
                        }
                    }else{
                        //$.tooltip('My God, 出错啦！！！');
                        $('label[id="generate_sample_content"]').text("生成panel文件失败，请点击上一步，返回文件上传页面或文件检查页面重新操作。");
                    }
                },
                error:function(e){
                    $.tooltip('My God, 出错啦！！！');
                }
            });
        }else{
            $.tooltip('抱歉，在您进行确认之前，需要先点击文件解析，并且勾选年月！！！');
        }
    });

    $("#t_prevstepBtn").click(function () {
        $("#upload_file").hide();
        $("#generate_panel_file").show();
        $("#generate_sample_data").hide();
        $("#t_nextstepBtn").removeAttr("disabled");
        $("#s_nextstepBtn").removeAttr("disabled");
        $('#resultDiv')[0].innerHTML = "";
    });

    $("#t_nextstepBtn").click(function () {
        setProgressStart(1000 * 10);
        var calc_panel_file = $.cookie("calc_panel_file")
        if(calc_panel_file!=null && calc_panel_file!=""){
            $(".progresstier").css("display", "block");
            p.setPercent(10);
            var query_object = new Object();
            query_object['company'] = $.cookie("token");
            query_object['filename'] = $.cookie("calc_panel_file");
            query_object["uname"] = $.cookie('webim_user');
            $.ajax({
                type : "post",
                data : JSON.stringify(query_object),
                contentType: "application/json,charset=utf-8",
                url :"/callcheckexcel",
                cache : false,
                dataType : "json",
                success : function(json){
                    $.tooltip('OK, 操作成功！', 2500, true);
                    document.getElementById("ybjc").click();
                },
                error:function(e){
                    $.tooltip('My God, 出错啦！！！');
                }
            });
        }else{
            $.tooltip('Panel文件不存在，无法进行下一步！！！');
        }
        //document.getElementById("ybjc").click();
    });



    //*********************************************************************
    //功能: 文件解析
    //时间：20170417
    //创建：Arthas
    //说明：文件上传前，通过对文件年月占比，进行判断分析，将大于50%得月份返回。
    //*********************************************************************
    // $('#upBeforeBtn').click(function(){
    //     setProgressStart(1000 * 3);
    //     $(".progresstier").css("display", "block");
    //     p.setPercent(10);
    //     var query_object = new Object();
    //     query_object['company'] = $.cookie("token");
    //     query_object["uname"] = $.cookie('webim_user');
    //     $.ajax({
    //         url: "/uploadbefore",
    //         type: 'POST',
    //         dataType: 'json',
    //         contentType: 'application/json, charset=utf-8',
    //         data: JSON.stringify(query_object),
    //         cache: false,
    //         success: function(data) {
    //             if (data.status == "ok") {
    //                 if(data.result.result.result.head.status==0){
    //                     var result = data.result.result.result.head.result;
    //                     var arr = result.split("#");
    //                     var html = "<div class='col-lg-6 text-left'>"
    //                     for(var i in arr){
    //                         var obj = arr[i];
    //                         if(obj!=null && obj!=""){
    //                             html += "<label class='checkbox-inline'><input type='checkbox' value='"+obj+"'> "+obj+"</label>";
    //                         }
    //                     }
    //                     html += "</div>";
    //                     $('#resultDiv')[0].innerHTML = html;
    //                     $.tooltip('OK, 操作成功！', 2500, true);
    //                 }else{
    //                     $.cookie("calc_panel_file",null)
    //                     $.tooltip('My God, 出错啦！！！');
    //                 }
    //             }else{
    //                 $.tooltip('My God, 出错啦！！！');
    //             }
    //         },
    //         error:function(e){
    //             $.tooltip('My God, 出错啦！！！');
    //         }
    //     });
    // });
    //*********************************************************************
    //功能: 确认
    //时间：20170413
    //创建：Arthas
    //说明：文件上传后，后台Python调用这些文件生成Panel文件。
    //*********************************************************************
    // $('#commitBtn').click(function(){
    //     setProgressStart(1000 * 5);
    //     var ck = $(':input[type=checkbox]');
    //     var checked = ""
    //     ck.each(function(){
    //         if($(this).is(':checked')){
    //             checked += $(this).val() + "#"
    //         }
    //     })
    //     if(checked!=""){
    //         $(".progresstier").css("display", "block");
    //         var query_object = new Object();
    //         query_object['company'] = $.cookie("token");
    //         query_object['yms'] = checked;
    //         query_object['uname'] = $.cookie('webim_user');
    //         $.ajax({
    //             url: "/cleaningdata",
    //             type: 'POST',
    //             dataType: 'json',
    //             contentType: 'application/json, charset=utf-8',
    //             data: JSON.stringify(query_object),
    //             cache: false,
    //             success: function(data) {
    //                 if (data.status == "ok") {
    //                     if(data.result.result.result.head.status==0){
    //                         $.cookie("calc_panel_file",data.result.result.result.head.result)
    //                         $.tooltip('OK, 操作成功！', 2500, true);
    //
    //                     }else{
    //                         $.cookie("calc_panel_file",null)
    //                         $.tooltip('My God, 出错啦！！！');
    //                     }
    //                 }else{
    //                     $.tooltip('My God, 出错啦！！！');
    //                 }
    //             },
    //             error:function(e){
    //                 $.tooltip('My God, 出错啦！！！');
    //             }
    //         });
    //     }else{
    //         $.tooltip('抱歉，在您进行确认之前，需要先点击文件解析，并且勾选年月！！！');
    //     }
    // });
    //*********************************************************************
    //功能: 下一步
    //时间：20170413
    //创建：Arthas
    //说明：利用Python生成得Panel文件，及其包含的所有去重市场所对应得两万家医院做
    //将未匹配数据、医院数量、市场数量、产品数量等入MongoDB库。
    //*********************************************************************
    // $('#nextstepBtm').click(function(){
    //     setProgressStart(1000 * 10);
    //     var calc_panel_file = $.cookie("calc_panel_file")
    //     if(calc_panel_file!=null && calc_panel_file!=""){
    //         $(".progresstier").css("display", "block");
    //         p.setPercent(10);
    //         var query_object = new Object();
    //         query_object['company'] = $.cookie("token");
    //         query_object['filename'] = $.cookie("calc_panel_file");
    //         query_object["uname"] = $.cookie('webim_user');
    //         $.ajax({
    //             type : "post",
    //             data : JSON.stringify(query_object),
    //             contentType: "application/json,charset=utf-8",
    //             url :"/callcheckexcel",
    //             cache : false,
    //             dataType : "json",
    //             success : function(json){
    //                 $.tooltip('OK, 操作成功！', 2500, true);
    //                 document.getElementById("ybjc").click();
    //             },
    //             error:function(e){
    //                 $.tooltip('My God, 出错啦！！！');
    //             }
    //         });
    //     }else{
    //         $.tooltip('抱歉，在您进行下一步之前，需要先点击确认！！！');
    //     }
    // });
});

var setProgress = function() {
    conn.listen({
        onTextMessage: function ( message ) {
            var ext = message.ext
            if (ext != null) {
                var result = searchExtJson(ext)("type")
                if(result == "progress") {
                    var r = p.setPercent(parseInt(message.data))
                    msgIdentifying = parseInt(message.data)
                    if(parseInt(message.data) >= 100 || r >= 100) {
                        setCloseInterval()
                        p.setPercent(0)
                        $(".progresstier").css("display", "none");
                    }
                }else if(result == "txt") {
                    console.info(message.data);
                }else {
                    console.info("No Type");
                    console.info(message.data);
                }
            }

            // var msg = eval("("+message.data+")")
            // msgIdentifying = msg.progress
            // var r = p.setPercent(msg.progress)
            // if(msg.progress >= 100 || r >= 100) {
            //     setCloseInterval()
            //     p.setPercent(0)
            //     $(".progresstier").css("display", "none");
            // }
        }
    });
}