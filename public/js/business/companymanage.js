/**
 * Created by liwei on 2017/5/22.
 */
$(function(){
    query_companys();

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
    $('#batchDelCompany').click(function () {
        var ids = [];
        $("#comp_list input[type='checkbox']").each(function(i){
            if($(this).prop("checked")==true){
                ids.push($(this).val());
            }
        });
        remove_company_func(ids);
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
                var company = data.result.result.result;
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
                $.tooltip(data.result.result.result, 2500, true);
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
                if(data.result.status == "success"){
                    query_companys();
                    $.tooltip(data.result.result.result, 2500, true);
                }else{
                    $.tooltip(data.result.message);
                }
                $.closeDialog(function(){});
            }
        });
    });
}
