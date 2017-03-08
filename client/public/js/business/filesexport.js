var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );

function fileExport(type) {
    loader.show();
    var datatype = $('select[data-name="search-result-datatype"]').val();
    var market = $('select[data-name="search-result-market"]').val();
    var startdate = $('input[name="startdate"]').val();
    var enddate = $('input[name="enddate"]').val();
    var query_object = new Object();
    query_object['datatype'] = datatype;
    query_object['market'] = market;
    query_object['staend'] = [startdate, enddate];
    query_object['company'] = $.cookie("token");
    query_object['filetype'] = type;

    $.ajax({
        //url :"/callfileexport",
        url :"/resultquery/tempexport",
        type : "POST",
        dataType : "json",
        contentType: "application/json,charset=utf-8",
        data : JSON.stringify(query_object),
        cache : false,
        success : function(data){
            if (data.status == "ok") {
                //alert("导出成功");
                location.href = "/resultquery/files/"+data.result.finalResult;
            }
            loader.hide();
        },
        error : function(e){
            alert("Error")
        }
    });
}