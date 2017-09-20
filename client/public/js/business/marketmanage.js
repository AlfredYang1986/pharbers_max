$(function(){
    query();

    //*********************************************************************
    //功能: 新增
    //时间：20170504
    //创建：liwei
    //说明：新增弹框。
    //*********************************************************************
    $('#addEntry').click(function(){
        $('#m_form')[0].reset();
        $('#title').text("新增");
        $("#au").val("add");
        $("#modal-form").modal('show');
    });

    //*********************************************************************
    //功能: 全选/全不选
    //时间：20170504
    //创建：liwei
    //说明：服务于批量删除。
    //*********************************************************************
    $('#selectAll').click(function(){
        if(this.checked){
            $("#list input[type='checkbox']").prop("checked", true);
        }else{
            $("#list input[type='checkbox']").prop("checked", false);
        }
    });

    //*********************************************************************
    //功能: 批量删除
    //时间：20170504
    //创建：liwei
    //说明：批量删除市场数据。
    //*********************************************************************
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

//*********************************************************************
//功能: 分页
//时间：20170504
//创建：liwei
//说明：组装分页数据。
//*********************************************************************
var market_lst = function(data) {
    var lst = data.result.result.result
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

//*********************************************************************
//功能: 修改
//时间：20170504
//创建：liwei
//说明：修改市场数据。
//*********************************************************************
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
            var result = data.result.result.result
            if(result.status == "success"){
                $("#au").val("update");
                $('#title').text("编辑");
                $("#market_id").val(result.result.Market_Id);
                $("#market_name").val(result.result.Market_Name);
                $("#modal-form").modal('show');
            }else{
                $.tooltip(data.result.message);
            }
        }
    });
}

//*********************************************************************
//功能: 单次删除
//时间：20170504
//创建：liwei
//说明：删除单个市场数据。
//*********************************************************************
var singledel_func = function(id){
    remove_func([id]);
}

//*********************************************************************
//功能: 移除
//时间：20170504
//创建：liwei
//说明：移除市场数据。
//*********************************************************************
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
                var result = data.result
                if(result.status == "success"){
                    query();
                    $.tooltip(result.result.result, 2500, true);
                }else{
                    $.tooltip(data.result.message);
                }
                $.closeDialog(function(){});
            }
        });
    });
}

//*********************************************************************
//功能: 保存
//时间：20170504
//创建：liwei
//说明：保存新增或修改后的市场数据。
//*********************************************************************
var save_func = function(){
    var market_name = $("#market_name").val();
    if(market_name != ""){
        var obj = new Object();
        obj['Market_Id'] = $("#market_id").val();
        obj['Market_Name'] = market_name;
        obj['au'] = $("#au").val();
        $.ajax({
            url: "/marketmanage/save",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(obj),
            cache: false,
            success: function(data) {
                var result = data.result
                if(result.status == "success"){
                    query();
                    $("#modal-form").modal('hide');
                    $.tooltip(result.result.result, 2500, true);
                }else{
                    $.tooltip(result.message);
                }
            }
        });
    } else {
        $.tooltip('市场名称输入为空！！！');
    }
}

//*********************************************************************
//功能: 查询
//时间：20170504
//创建：liwei
//说明：检索全部市场数据。
//*********************************************************************
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
        }
    });
}