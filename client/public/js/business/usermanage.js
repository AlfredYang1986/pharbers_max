$(function(){
    query();

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
});

//*********************************************************************
//功能: 分页
//时间：20170504
//创建：liwei
//说明：组装分页数据。
//*********************************************************************
var user_lst = function(data) {
    var lst = data.result.result
    var temp = []
    var fina = []
    var nobj = lst[0]
    if(nobj!=null){
        $.each(lst, function (i, v) {
            temp.push("<input type=\"checkbox\" class=\"i-checks\" name=\"input[]\" value="+v.User_ID+">");
            temp.push((i + 1));
            temp.push(v.Account);
            temp.push(v.Name);
            temp.push(v.isadministrator);
            temp.push(v.Company_Name_Ch);
            temp.push(v.Company_Name_En);
            temp.push(v.E_Mail);
            temp.push(v.Timestamp);
            temp.push("<a href=\"javascript:update_func('"+v.User_ID+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:singledel_func('"+v.User_ID+"');\"><i class=\"fa fa-trash-o\"></i></a>");
            fina.push(temp);
            temp = [];
        })
    }
    return fina;
}

//*********************************************************************
//功能: 查询
//时间：20170504
//创建：liwei
//说明：检索全部市场数据。
//*********************************************************************
var query = function(){
    var obj = new Object();
    obj['company'] = $.cookie("token");
    $.ajax({
        url: "/usermanage/query",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            var data = user_lst(data);
            dataTableAjax(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}