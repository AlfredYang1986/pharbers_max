$(function(){
    query();

    //*********************************************************************
    //功能: 新增
    //时间：20170504
    //创建：liwei
    //说明：新增弹框。
    //*********************************************************************
    $('#addEntry').click(function(){
        $('#u_form')[0].reset();
        $('#title').text("新增");
        $("#au").val("add");
        $("#Company_Name_Ch").val($.cookie("company_name_ch"));
        $("#Company_Name_En").val($.cookie("company_name_en"));
        $("#E_Mail").val($.cookie("email"));
        $("#Account").prop('readonly', false);
        $("#Company_Name_Ch").prop('readonly', true);
        $("#Company_Name_En").prop('readonly', true);
        $("#E_Mail").prop('readonly', true);
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
    //说明：批量删除用户数据。
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
//功能: 修改
//时间：20170504
//创建：liwei
//说明：修改用户数据。
//*********************************************************************
var update_func = function(id){
    var obj = new Object();
    obj['User_Id'] = id;
    obj['Company_Id'] = $.cookie("token");
    $.ajax({
        url: "/usermanage/findOne",
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
                    $("#User_ID").val(result.result.User_ID);
                    $("#Company_Id").val(result.result.Company_Id);
                    $("#Account").prop('readonly', true);
                    $("#Account").val(result.result.Account);
                    $("#Name").val(result.result.Name);
                    $("#Password").val(result.result.Password);
                    $("#isadministrator").val(result.result.isadministrator);
                    if(result.result.isadministrator == "普通用户"){
                        $('#isadministrator').get(0).selectedIndex=0;
                    }else{
                        $('#isadministrator').get(0).selectedIndex=1;
                    }
                    $("#Company_Name_Ch").val(result.result.Company_Name_Ch);
                    $("#Company_Name_En").val(result.result.Company_Name_En);
                    $("#E_Mail").val(result.result.E_Mail);
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

//*********************************************************************
//功能: 单次删除
//时间：20170504
//创建：liwei
//说明：删除单个用户数据。
//*********************************************************************
var singledel_func = function(id){
    remove_func([id]);
}

//*********************************************************************
//功能: 移除
//时间：20170504
//创建：liwei
//说明：移除用户数据。
//*********************************************************************
var remove_func = function(ids){
    $.tooltip('玩命开发中，尽请期待！！！');
}

//*********************************************************************
//功能: 保存
//时间：20170504
//创建：liwei
//说明：保存新增或修改后的用户数据。
//*********************************************************************
var save_func = function(){
    //var user = $('#u_form').serialize();
    var obj = new Object();
    obj['au'] = $("#au").val();
    obj['User_ID'] = $("#User_ID").val();
    obj['Company_Id'] = $.cookie("token");
    obj['Account'] = $("#Account").val();
    obj['User_Name'] = $("#Name").val();
    obj['Password'] = $("#Password").val();
    obj['auth'] = 0;
    if($("#isadministrator").find("option:selected").text()=="管理员"){
        obj['isadministrator'] = 1;
    }else{
        obj['isadministrator'] = 0;
    }
    obj['Company_Name_Ch'] = $("#Company_Name_Ch").val();
    obj['Company_Name_En'] = $("#Company_Name_En").val();
    obj['E_Mail'] = $("#E_Mail").val();
    console.info(obj)
    $.ajax({
        url: "/usermanage/save",
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
                    $.tooltip('OK, 操作成功！', 2500, true);
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