$(function(){
    query_companys();
    query_user();

    if($.cookie("token") != "788d4ff5836bcee2ebf4940fec882ac8"){
        $('#addCompany').hide();
        $('#batchDelCompany').hide();
        $("#isadmin option:last").remove();
    }
    $("#div_u_company").hide()
    //*********************************************************************
    //功能: 新增公司
    //时间：20170504
    //创建：liwei
    //说明：新增公司弹框。
    //*********************************************************************
    $('#addCompany').click(function(){
        $('#c_form')[0].reset()
        $('#c_title').text("新增");
        $("#c_au").val("add");
        $("#company-form").modal('show');
    });

    //*********************************************************************
    //功能: 全选/全不选
    //时间：20170504
    //创建：liwei
    //说明：服务于批量删除。
    //*********************************************************************
    $('#selectAllCompany').click(function(){
        if(this.checked){
            $("#comp_list input[type='checkbox']").prop("checked", true);
        }else{
            $("#comp_list input[type='checkbox']").prop("checked", false);
        }
    });
    //*********************************************************************
    //功能: 批量删除
    //时间：20170510
    //创建：liwei
    //说明：批量删除公司数据。
    //*********************************************************************
    $('#batchDelUser').click(function () {
        var ids = [];
        $("#comp_list input[type='checkbox']").each(function(i){
            if($(this).prop("checked")==true){
                ids.push($(this).val());
            }
        });
        remove_company_func(ids);
    });

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
//说明：AJAX请求公司数据。
//*********************************************************************
var query_companys = function () {
    var obj = new Object();
    obj['Company_Id'] = $.cookie("token");
    $.ajax({
        url: "/usermanage/company/query",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                dataTableMultiAjax(company_lst(data),'#dataTables-company');
            }else{
                $.tooltip(data.result.message);
            }
        }
    });
}

//*********************************************************************
//功能: 公司分页
//时间：20170504
//创建：liwei
//说明：组装公司分页数据。
//*********************************************************************
var company_lst = function(data) {
    var lst = data.result.result.result
    var temp = []
    var fina = []
    var nobj = lst[0]
    if(nobj!=null){
        $("#u_company").empty();
        $.each(lst, function (i, v) {
            temp.push("<input type=\"checkbox\" class=\"i-checks\" name=\"input[]\" value="+v.Company_Id+">");
            temp.push((i + 1));
            temp.push(v.Ch);
            temp.push(v.En);
            temp.push(v.E_Mail);
            temp.push(v.Timestamp);
            if($.cookie("token") == "788d4ff5836bcee2ebf4940fec882ac8"){
                $("#u_company").append("<option value='"+v.Company_Id+"'>"+v.Ch+"</option>");
                $('#div_u_company').show();
                temp.push("<a href=\"javascript:update_company_func('"+v.Company_Id+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>&nbsp;&nbsp;<a href=\"javascript:sing_company_func('"+v.Company_Id+"');\"><i class=\"fa fa-trash-o\"></i></a>");
            }else{
                temp.push("<a href=\"javascript:update_company_func('"+v.Company_Id+"');\"><i class=\"fa fa-pencil-square-o\"></i></a>");
            }
            fina.push(temp);
            temp = [];
        })
    }
    return fina;
}

//*********************************************************************
//功能: 修改
//时间：201705011
//创建：liwei
//说明：修改公司数据。
//*********************************************************************
var update_company_func = function(id){
    var obj = new Object();
    obj['Company_Id'] = id;
    $.ajax({
        url: "/usermanage/company/findOne",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                var company = data.result.result.result.result;
                $("#c_au").val("update");
                $('#c_title').text("编辑");
                $("#Company_Id").val(company.Company_Id);
                $("#Company_Name_Ch").val(company.Ch);
                $("#Company_Name_En").val(company.En);
                $("#E_Mail").val(company.E_Mail);
                $("#company-form").modal('show');
            }else{
                $.tooltip(data.result.message);
            }
        }
    });
}
//*********************************************************************
//功能: 保存
//时间：20170511
//创建：liwei
//说明：保存公司数据。
//*********************************************************************
var save_company_func = function(){
    var Company_Name_Ch = $("#Company_Name_Ch").val();
    var Company_Name_En = $("#Company_Name_En").val();
    if(Company_Name_Ch == "" && Company_Name_En == ""){
        $.tooltip('公司名称输入为空！！！');
        return false;
    }
    var email_reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    var E_Mail = $("#E_Mail").val();
    if(E_Mail == ""){
        $.tooltip('邮箱输入为空！！！');
        return false;
    }else{
        if(!email_reg.test(E_Mail)){
            $.tooltip('请输入有效的E_mail！！！');
            return false;
        }
    }
    var obj = new Object();
    obj['au'] = $("#c_au").val();
    obj['Company_Id'] = $("#Company_Id").val();
    obj['Company_Name_Ch'] = Company_Name_Ch;
    obj['Company_Name_En'] = Company_Name_En;
    obj['E_Mail'] = E_Mail;
    $.ajax({
        url: "/usermanage/company/save",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                query_companys();
                $("#company-form").modal('hide');
                $.tooltip('OK, 操作成功！', 2500, true);
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
//说明：删除单个公司数据。
//*********************************************************************
var sing_company_func = function(id){
    remove_company_func([id]);
}

//*********************************************************************
//功能: 移除
//时间：20170511
//创建：liwei
//说明：移除公司数据。
//*********************************************************************
var remove_company_func = function (ids) {
    var query_object = new Object();
    query_object['Company_Id'] = ids;
    $.dialog('confirm','提示','您确认要删除么？',0,function(){
        $.ajax({
            url: "/usermanage/company/delete",
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json, charset=utf-8',
            data: JSON.stringify(query_object),
            cache: false,
            success: function(data) {
                $.tooltip(data.result.message);
                $.closeDialog(function(){});
            }
        });
    });
}
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
                $.tooltip(data.result.message);
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
    var obj = new Object();
    obj['ID'] = id;
    $.ajax({
        url: "/usermanage/user/findOne",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json, charset=utf-8',
        data: JSON.stringify(obj),
        cache: false,
        success: function(data) {
            if(data.result.status == "success"){
                var user = data.result.result.result.result;
                $("#u_au").val("update");
                $('#u_title').text("编辑");
                $("#ID").val(user.ID);
                $("#Account").val(user.Account);
                $("#Name").val(user.Name);
                $("#Password").val(user.Password);
                $("#isadmin").val(user.isadministrator);
                if(user.isadministrator == 0){
                    $('#isadmin').get(0).selectedIndex=0;
                }else{
                    $('#isadmin').get(0).selectedIndex=1;
                }
                $("#u_Company_Id").val(user.Company_Id);
                $("#u_Ch").val(user.Ch);
                $("#u_En").val(user.En);
                $("#u_E_Mail").val(user.E_Mail);
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
            query_user();
            $.tooltip(data.result.message, 2500, true);
        }
    });
}