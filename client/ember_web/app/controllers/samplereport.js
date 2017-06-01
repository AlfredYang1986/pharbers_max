import Ember from 'ember';

export default Ember.Controller.extend({
    onload:Ember.computed(function () {
        Ember.$('#date')[0].innerText = getNowFormatDate();
        Ember.$('#company_ch')[0].innerText = Ember.$.cookie("company_name_ch");
        Ember.$.ajax({
            type: "POST",
            url: "/samplereport/report",
            dataType: "json",
            data: JSON.stringify({ "company": Ember.$.cookie("token")}),
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                if(data.result.status == "success"){
                    var result = data.result.result.result
                    Ember.$('#markets')[0].innerText = result.length+'个市场'
                    console.log(result.length)
                    var html = "";
                    html += "<li>"
                    for(var x in result){
                        var obj = result[x]
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
                    Ember.$('#tbody')[0].innerHTML = html;
                }else{
                    Ember.$.tooltip(data.result.message);
                }
            }
        });
    })
});
