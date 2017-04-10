//var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );

function excelCheck() {
//	loader.show();
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
            alert("操作成功")
//            loader.hide();
            document.getElementById("ybjc").click()
        },
        error:function(e){
            alert("Error")
        }
    });
}

function commitUp(){
//    loader.show();
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
                if(data.result.result.result.head.status==0){
                    $.cookie("calc_panel_file",data.result.result.result.head.filename)
                    alert("操作成功。")
                }else{
                    $.cookie("calc_panel_file",null)
                    alert("操作失败，Python Code调用失败，文件内部出现错误。")
                }
//                loader.hide();
            }
//            loader.hide();
        }
    });
}