var p;
$(function(){
    p = new progress2();
    load_im()
    setProgress();

    $("#generate_panel_file").hide();
    $("#generate_sample_data").hide();
    $("#f_nextstepBtn").click(function () {
        setProgressStart(1000 * 3);
        $(".progresstier").css("display", "block");
        p.setPercent(10);
        var query_object = new Object();
        query_object['company'] = $.cookie("token");
        query_object["uname"] = $.cookie('webim_user');
        query_object["businessType"] = "/uploadbefore";
        $("#s_nextstepBtn").removeAttr("disabled");
            $.ajax({
                url: "/callhttpServer",
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json, charset=utf-8',
                data: JSON.stringify(query_object),
                timeout: 1000 * 60 * 10,
                error: function(xhr){
                    console.info(xhr);
                },
                success: function(data) {
                    $('#resultDiv').show();
                    $("#upload_file").hide();
                    $("#generate_panel_file").show();

                    console.info(data)
                    var result = data.result
                    if(result.status == "success"){
                        var arr = result.result.result.split("#");
                        var html = "<div class='col-lg-6 text-left'>"
                        for(var i in arr){
                            var obj = arr[i];
                            if(obj!=null && obj!=""){
                                html += "<label class='checkbox-inline'><input type='checkbox' value='"+obj+"'> "+obj+"</label>";
                            }
                        }
                        html += "</div>";
                        $('div[id="resultDiv"]')[0].innerHTML = html;
                        $('label[id="generate_panel_content"]').text("文件检查通过，请勾选月份，继续点击下一步。");
                    }else {
                        $.cookie("calc_panel_file",null)
                        $("#s_nextstepBtn").attr({"disabled":"disabled"});
                        $('label[id="generate_panel_content"]').text("文件检查失败，请点击上一步，返回上传页面，重新上传文件。");
                    }
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
            url: "/filesUpload/removeFiles",
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
            query_object["businessType"] = "/uploadfile";
            $.ajax({
                url: "/callhttpServer",
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json, charset=utf-8',
                data: JSON.stringify(query_object),
                cache: false,
                success: function(data) {
                    $("#upload_file").hide();
                    $("#generate_panel_file").hide();
                    $("#generate_sample_data").show();

                    console.info(data)
                    var result = data.result
                    if(result.status == "success"){
                        $.cookie("calc_panel_file",result.result.result)
                        $('label[id="generate_sample_content"]').text("生成panel文件成功，请继续点击下一步。");
                    }else{
                        $.cookie("calc_panel_file",null)
                        $("#t_nextstepBtn").attr({"disabled":"disabled"});
                        $('label[id="generate_sample_content"]').text("生成panel文件失败，请点击上一步，返回文件上传页面或文件检查页面重新操作。");
                    }
                }
            });
        }else{
            $.tooltip('抱歉，在点击下一步之前，您需要选择一个或多个年月！！！');
        }
    });

    $("#t_prevstepBtn").click(function () {
        $("#upload_file").hide();
        $("#generate_panel_file").show();
        $("#generate_sample_data").hide();
        $("#t_nextstepBtn").removeAttr("disabled");
        $("#s_nextstepBtn").removeAttr("disabled");
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
            query_object["businessType"] = "/samplecheck";
            $.ajax({
                type : "post",
                data : JSON.stringify(query_object),
                contentType: "application/json,charset=utf-8",
                url :"/callhttpServer",
                cache : false,
                dataType : "json",
                success : function(data){

                    console.info(data)
                    var result = data.result
                    if(result.status == "success"){
                        $.tooltip('操作成功', 2500, true);
                        document.getElementById("ybjc").click();
                    }else{
                        $.tooltip(result.message);
                    }
                }
            });
        }else{
            $.tooltip('Panel文件不存在，无法进行下一步！！！');
        }
    });
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
        }
    });
}