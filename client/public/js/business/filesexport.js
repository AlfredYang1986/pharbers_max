var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );

function fileExport(type) {
    loader.show();
    var datatype = $('select[data-name="search-result-datatype"]').val();
    var market = $('select[data-name="search-result-market"]').val();
    var startdate = $('input[name="startdate"]').val();
    var enddate = $('input[name="enddate"]').val();

    var query_object = new Object();
    query_object['datatype'] = datatype;
    if(market==null){
        query_object['market'] = [];
    }else{
        query_object['market'] = market;
    }
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
                location.href = "/resultquery/files/"+data.result.result.result.filename;
            }else{
                alert(data.result.result.result.message)
            }
            loader.hide();
        },
        error : function(e){
            alert("Error")
        }
    });
}