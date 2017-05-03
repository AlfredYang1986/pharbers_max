$(function(){
    query();
});

var market_lst = function(data) {
    var lst = data.result.result
    var temp = []
    var data = []
    var nobj = lst[0]
    if(nobj!=null){
        $.each(lst, function (i, v) {
            temp.push((i + 1));
            temp.push(v.Market_Name);
            temp.push(v.Date);
            temp.push("<a href=\"javascript:update_func('"+v.Market_Id+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:delete_func('"+v.Market_Id+"');\"><i class=\"fa fa-trash-o\"></i></a>");
            data.push(temp)
            temp = []
        })
    }
    return data;
}

var add_func = function(){
    $("#au").val("a");
    $("#modal-form").modal('show');
}

var update_func = function(id){
    var obj = new Object();
    obj['Market_Id'] = id;
    $.ajax({
        url: "/marketmanage/querybyid",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            var obj = data.result.result
            $("#au").val("u");
            $("#market_id").val(obj.Market_Id);
            $("#market_name").val(obj.Market_Name);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
    $("#modal-form").modal('show');
}

var delete_func = function(id){
    var query_object = new Object();
    query_object['Market_Id'] = id;
    $.ajax({
        url: "/marketmanage/delete",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(query_object),
        cache: false,
        success: function(data) {
            var lst = data.result.result
            if(data.status == "ok" && lst.length != 0){
                $.tooltip('OK, 删除成功！', 2500, true);
                var data = market_lst(data);
                dataTableAjax(data);
            }else{
                $.tooltip('My God, 出错啦！！！');
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}

var save_func = function(){
    var obj = new Object();
    obj['Market_Id'] = $("#market_id").val();
    obj['Market_Name'] = $("#market_name").val();
    obj['au'] = $("#au").val();
    $.ajax({
        url: "/marketmanage/save",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            query();
            $("#modal-form").modal('hide');
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}

var query = function(){
    $.ajax({
        url: "/marketmanage/query",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(new Object()),
        cache: false,
        success: function(data) {
            var data = market_lst(data);
            dataTableAjax(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}
