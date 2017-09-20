(function($, w, d){
    "use strict";
    $(function(){
        $("#userinfo").on("click", function(){
            var data = JSON.stringify({
                "account": $.cookie("webim_user"),
                "cid": $.cookie("token")
            });
            ajaxData("usermanage/user/findOne", data, "POST", function(d){
                console.info(d)
                $.each(d.result.result.result, function(i, v){
                    $("#account").text("").text(v.Account);
                    $("#name").text("").text(v.Name);
                    $("#company_ch").text("").text(v.Company_CH);
                    $("#company_en").text("").text(v.Company_EN);
                    $("#isadmin").text("").text(v.isadministrator == 0 ? "否" : "是");
                    $("#createtime").text("").text(v.Timestamp);
                })
                $("#user-info").modal('show');
            }, function(e){console.error(e)})
        });
    });



    var userinfo = function() {
        console.info("fuck");
    }
}(jQuery, this, document))