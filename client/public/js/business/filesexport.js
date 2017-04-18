var p;

$(function(){
    p = new progress2();
    conn = load_Web_IM();
    login_im("test", "1");
    setProgress();

    $("#csvExportBtn").click(function(){
        fileExport('.csv');
    });

    $("#txtExportBtn").click(function(){
        fileExport('.txt');
    });

    //*********************************************************************
    //功能: 文件导出
    //时间：20170413
    //创建：Arthas
    //说明：文件导出，支持csv、txt文件格式。
    //*********************************************************************
    function fileExport(type) {
        $(".progresstier").css("display", "block");
        p.setPercent(0);
        var datatype = $('select[data-name="search-result-datatype"]').val();
        var market = $('select[data-name="search-result-market"]').val();
        var startdate = $('input[name="startdate"]').val();
        var enddate = $('input[name="enddate"]').val();

        var query_object = new Object();
        query_object['datatype'] = datatype;
        query_object['market'] = market==null?[]:market;
        query_object['staend'] = [startdate, enddate];
        query_object['company'] = $.cookie("token");
        query_object['filetype'] = type;

        $.ajax({
            url :"/callfileexport",
            type : "POST",
            dataType : "json",
            contentType: "application/json,charset=utf-8",
            data : JSON.stringify(query_object),
            cache : false,
            success : function(data){
                if (data.result.result.result.status==0) {
                    $(".progresstier").css("display", "none");
                    location.href = "/resultquery/files/"+data.result.result.result.filename;
                }else{
                    $(".progresstier").css("display", "none");
                    $.tooltip(data.result.result.result.message);
                }
            },
            error : function(e){
                $(".progresstier").css("display", "none");
                $.tooltip('My God, 出错啦！！！');
            }
        });
    }
});


var setProgress = function() {
    conn.listen({
        onTextMessage: function ( message ) {
            var msg = eval("("+message.data+")")
            if(msg.progress == 100){
                p.setPercent(0)
                $(".progresstier").css("display", "none");
            }else{
                p.setPercent(msg.progress)
            }
        }
    });
}
