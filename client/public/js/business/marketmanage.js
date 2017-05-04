$(function(){
    query();

    $('#addEntry').click(function(){
        $('#m_form')[0].reset();
        $('#title').text("新增");
        $("#au").val("add");
        $("#modal-form").modal('show');
    });

    $('#selectAll').click(function(){
        if(this.checked){
            $("#list input[type='checkbox']").prop("checked", true);
        }else{
            $("#list input[type='checkbox']").prop("checked", false);
        }
    });

    $('#batchDel').click(function(){
        var ids = [];
        $("#list input[type='checkbox']").each(function(i){
            if($(this).prop("checked")==true){
                ids.push($(this).val());
            }
        });
        remove_func(ids);
    });
});

var market_lst = function(data) {
    var lst = data.result.result
    var temp = []
    var data = []
    var nobj = lst[0]
    if(nobj!=null){
        $.each(lst, function (i, v) {
            temp.push("<input type=\"checkbox\" class=\"i-checks\" name=\"input[]\" value="+v.Market_Id+">");
            temp.push((i + 1));
            temp.push(v.Market_Name);
            temp.push(v.Date);
            temp.push("<a href=\"javascript:update_func('"+v.Market_Id+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:singledel_func('"+v.Market_Id+"');\"><i class=\"fa fa-trash-o\"></i></a>");
            data.push(temp)
            temp = []
        })
    }
    return data;
}

var update_func = function(id){
    var obj = new Object();
    obj['Market_Id'] = id;
    $.ajax({
        url: "/marketmanage/findOne",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.status == "ok"){
                var result = data.result.result
                if(result.status == "success"){
                    $("#au").val("update");
                    $('#title').text("编辑");
                    $("#market_id").val(result.result.Market_Id);
                    $("#market_name").val(result.result.Market_Name);
                    $("#modal-form").modal('show');
                }else{
                    $.tooltip('My God, '+result.result+'！！！');
                }
            }else{
                $.tooltip('My God, 出错啦！！！');
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}

var singledel_func = function(id){
    remove_func([id]);
}

var remove_func = function(ids){
    var query_object = new Object();
    query_object['Market_Id'] = ids;
    $.dialog('confirm','提示','您确认要删除么？',0,function(){
        $.ajax({
            url: "/marketmanage/delete",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                var result = data.result.result
                if(data.status == "ok" && result.status == "success"){
                    $.tooltip('OK, '+result.result+'！', 2500, true);
                    query();
                }else{
                    $.tooltip('My God, '+result.result+'！！！');
                }
                $.closeDialog(function(){});
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $.tooltip('My God, 出错啦！！！');
            }
        });
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
            if(data.status == "ok"){
                var result = data.result.result
                if(result.status == "success"){
                    query();
                    $("#modal-form").modal('hide');
                }else{
                    $.tooltip('My God, '+result.result+'！！！');
                }
            }else{
                $.tooltip('My God, 出错啦！！！');
            }
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
