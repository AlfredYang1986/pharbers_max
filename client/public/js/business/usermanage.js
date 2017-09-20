$(function(){
    query_user();

    //*********************************************************************
    //功能: 新增用户
    //时间：20170504
    //创建：liwei
    //说明：新增用户弹框。
    //*********************************************************************
    $('#addUser').click(function(){
        $('#u_form')[0].reset();
        $('#u_title').text("新增");
        $("#u_au").val("add");
        $("#Account").prop('readonly', false);
        $("#user-form").modal('show');
    });

    //*********************************************************************
    //功能: 全选/全不选
    //时间：20170504
    //创建：liwei
    //说明：服务于批量删除。
    //*********************************************************************
    $('#selectAllUser').click(function(){
        if(this.checked){
            $("#user_list input[type='checkbox']").prop("checked", true);
        }else{
            $("#user_list input[type='checkbox']").prop("checked", false);
        }
    });
    //*********************************************************************
    //功能: 批量删除
    //时间：20170511
    //创建：liwei
    //说明：批量删除用户。
    //*********************************************************************
    $('#batchDelUser').click(function () {
        var ids = [];
        $("#user_list input[type='checkbox']").each(function(i){
            if($(this).prop("checked")==true){
                ids.push($(this).val());
            }
        });
        sing_user_func(ids);
    });
});

//*********************************************************************
//功能: 查询
//时间：20170504
//创建：liwei
//说明：检索全部用户数据。
//*********************************************************************
var query_user = function(){
    var obj = new Object();
    obj['Company_Id'] = $.cookie("token");
    $.ajax({
        url: "/usermanage/user/query",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                dataTableMultiAjax(user_lst(data),'#dataTables-user');
            }else{
                $.tooltip(data.result.message);
            }
        }
    });
}

//*********************************************************************
//功能: 用户分页
//时间：20170504
//创建：liwei
//说明：组装用户分页数据。
//*********************************************************************
var user_lst = function(data) {
    var lst = data.result.result.result;
    var temp = []
    var fina = []
    var nobj = lst[0]
    if(nobj!=null){
        var row = 0
        $.each(lst, function (i, v) {
            $.each(v, function (j, user) {
                // temp.push("<input type=\"checkbox\" class=\"i-checks\" name=\"input[]\" value="+user.ID+">");
                row = row+1
                temp.push(row);
                temp.push(user.Account);
                temp.push(user.Name);
                temp.push(user.isadministrator);
                temp.push(user.Timestamp);
                if($.cookie("token") == "788d4ff5836bcee2ebf4940fec882ac8"){
                    temp.push("<a href=\"javascript:update_user_func('"+user.ID+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:sing_user_func('"+user.ID+"','"+user.Company_Id+"');\"><i class=\"fa fa-trash-o\"></i></a>");
                }else{
                    if(user.isadministrator == '管理员'){
                        temp.push("<a href=\"javascript:update_user_func('"+user.ID+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>");
                    }else{
                        temp.push("<a href=\"javascript:update_user_func('"+user.ID+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:sing_user_func('"+user.ID+"','"+user.Company_Id+"');\"><i class=\"fa fa-trash-o\"></i></a>");
                    }
                }
                fina.push(temp);
                temp = [];
            });
        })
    }
    return fina;
}

//*********************************************************************
//功能: 单次删除
//时间：20170510
//创建：liwei
//说明：删除单个公司数据。
//*********************************************************************
var sing_user_func = function(id,Company_Id){
    remove_user_func([id],Company_Id);
}

//*********************************************************************
//功能: 移除
//时间：20170511
//创建：liwei
//说明：移除用户数据。
//*********************************************************************
var remove_user_func = function (ids,Company_Id) {
    var query_object = new Object();
    if($.cookie("token") == '788d4ff5836bcee2ebf4940fec882ac8'){
        query_object['Company_Id'] = Company_Id;
    }else{
        query_object['Company_Id'] = $.cookie("token");
    }
    query_object['IDs'] = ids;
    $.dialog('confirm','提示','您确认要删除么？',0,function(){
        $.ajax({
            url: "/usermanage/user/delete",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                if(data.result.status == "success"){
                    query_user();
                    $.tooltip(data.result.result.result, 2500, true);
                }else{
                    $.tooltip(data.result.message);
                }
                $.closeDialog(function(){});
            }
        });
    });
}

//*********************************************************************
//功能: 修改
//时间：20170511
//创建：liwei
//说明：修改用户数据。
//*********************************************************************
var update_user_func = function (id) {
    // var obj = new Object();
    // obj['ID'] = id;
    var obj = JSON.stringify({
        "uid" : id,
        "cid" : $.cookie("token")
    })
    $.ajax({
        url: "/usermanage/user/findOne",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: obj,
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                var user = data.result.result.result;
                $.each(user, function(i, v){
                    $("#u_au").val("update");
                    $('#u_title').text("编辑");
                    $("#ID").val(v.ID);
                    $("#Account").val(v.Account);
                    $("#Name").val(v.Name);
                    $("#Password").val(v.Password);
                    $("#isadmin").val(v.isadministrator);
                    if(v.isadministrator == 0){
                        $('#isadmin').get(0).selectedIndex=0;
                    }else{
                        $('#isadmin').get(0).selectedIndex=1;
                    }
                    // $("#u_Company_Id").val(v.Company_Id);
                    // $("#u_Ch").val(v.Ch);
                    // $("#u_En").val(v.En);
                    // $("#u_E_Mail").val(v.E_Mail);
                });
                $("#Account").prop('readonly', true);
                $("#user-form").modal('show');
            }else{
                $.tooltip(data.result.message);
            }
        }
    });
}

//*********************************************************************
//功能: 保存
//时间：20170504
//创建：liwei
//说明：保存用户数据。
//*********************************************************************
var save_user_func = function(){
    var Account = $("#Account").val();
    if(Account == ""){
        $.tooltip('账号输入为空！！！');
        return false;
    }
    var User_Name = $("#Name").val();
    if(User_Name == ""){
        $.tooltip('用户名输入为空！！！');
        return false;
    }
    var Password = $("#Password").val();
    if(Password == ""){
        $.tooltip('密码输入为空！！！');
        return false;
    }
    var obj = new Object();
    obj['au'] = $("#u_au").val();
    obj['ID'] = $("#ID").val();
    obj['Account'] = $("#Account").val();
    obj['Name'] = $("#Name").val();
    obj['Password'] = $("#Password").val();
    obj['isadmin'] = $("#isadmin").get(0).selectedIndex;
    if($("#u_company").val()!=null){
        obj['Company_Id'] = $("#u_company").val();
    }else{
        obj['Company_Id'] = $.cookie("token");
    }
    $.ajax({
        url: "/usermanage/user/save",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                query_user();
                $("#user-form").modal('hide');
                $.tooltip(data.result.result.result, 2500, true);
            }else{
                $.tooltip(data.result.message);
            }

        }
    });
}