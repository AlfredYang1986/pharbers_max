window.onload = function(){
    $('#date')[0].innerText = getNowFormatDate();
    $('#company_ch')[0].innerText = $.cookie("company_name_ch");
    $.ajax({
        type: "POST",
        url: "/samplereport/report",
        dataType: "json",
        data: JSON.stringify({ "company": $.cookie("token")}),
        contentType: 'application/json,charset=utf-8',
        success: function (r) {
            if(r.status=="ok"){
                var data = r.result.result
                $('#markets')[0].innerText = data.length+'个市场'
                var html = "";
                html += "<li>"
                for(var x in data){
                    var obj = data[x]
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj.Market+"</span>"
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，您需要进行放大月份为</span>"
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+array2str(obj.date_lst_sb)+"</span>"
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                    html += "</li>"
                    html += "<p class=MsoNormal style='vertical-align: middle'><span lang=EN-US style='font-family: 'verdena', 'serif'; color: black'>&nbsp;</span></p>"
                    html += "<li>"
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj.Market+"</span>"
                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>：</span><br><ul>"

                    var bodys = obj.dhp_lst_sb
                    if(bodys!=null && bodys.length!=0){
                        for(var y in bodys){
                            var obj2 = bodys[y]
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red; text-indent:2em;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>产品数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.c_ProductNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应上期产品数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.e_ProductNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应去年同期产品数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.l_ProductNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>。</span><br><br>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>医院数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.c_HospNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应上期医院数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.e_HospNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.Date+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应去年同期医院数</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+obj2.l_HospNum+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>。</span><br><br>"
                        }
                    }
                }
                html += "</li>";
                html += "<p class=MsoNormal style='vertical-align: middle'><span lang=EN-US style='font-family: 'verdena', 'serif'; color: black'>&nbsp;</span></p>"
                $('#tbody')[0].innerHTML = html;
            }else{
                $.tooltip('My God, 出错啦！！！');
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.tooltip('My God, 出错啦！！！');
        }
    });
}

function array2str(lst){
    var str = ""
    for(var x in lst){
        str = str + lst[x] + "、"
    }
    return str.substring(0,str.length-1)
}

//获取当前时间，格式YYYY年MM月DD日
function getNowFormatDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + '年' + month + '月' + strDate + '日';
    return currentdate;
}

$(function(){
    //*********************************************************************
    //功能: 下一步
    //时间：20170413
    //创建：Arthas
    //说明：跳转至模型运算页面。
    //*********************************************************************
    $('#nextstepBtm').click(function(){
        document.getElementById("mxys").click()
    });
});