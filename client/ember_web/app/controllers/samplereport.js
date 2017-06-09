import Ember from 'ember';

export default Ember.Controller.extend({
    mytime:new Date(),

    onload:Ember.computed('mytime',function () {

        Ember.$('#date')[0].innerText = this.getNowFormatDate();
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
                    if(result.length != 0){
                        var html = "";
                        html += "<li>"
                        Ember.$.each(result,function(i,v){
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v.Market+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，您需要进行放大月份为</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+this.array2str(v.date_lst_sb)+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                            html += "</li>"
                            html += "<p class=MsoNormal style='vertical-align: middle'><span lang=EN-US style='font-family: 'verdena', 'serif'; color: black'>&nbsp;</span></p>"
                            html += "<li>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v.Market+"</span>"
                            html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>：</span><br><ul>"

                            var bodys = v.dhp_lst_sb
                            if(bodys!=null && bodys.length!=0){

                                Ember.$.each(bodys,function (i,v2) {
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red; text-indent:2em;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>产品数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.c_ProductNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应上期产品数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.e_ProductNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应去年同期产品数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.l_ProductNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>。</span><br><br>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>医院数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.c_HospNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应上期医院数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.e_HospNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>，</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.Date+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>对应去年同期医院数</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: red'>"+v2.l_HospNum+"</span>"
                                    html += "<span style='font-family: 幼圆; font-size: 12pt; color: black'>。</span><br><br>"
                                })

                                /*for(var y in bodys){
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
                                }*/
                            }
                        })

                        /*for(var x in result){
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
                        }*/
                        html += "</li>";
                        html += "<p class=MsoNormal style='vertical-align: middle'><span lang=EN-US style='font-family: 'verdena', 'serif'; color: black'>&nbsp;</span></p>"
                        Ember.$('#tbody')[0].innerHTML = html;
                    }

                }else{
                    Ember.$.tooltip(data.result.message);
                }
            }
        });
    }),

    array2str(lst){
        var str = ""
        for(var x in lst){
            str = str + lst[x] + "、"
        }
        return str.substring(0,str.length-1)
    },

    //获取当前时间，格式YYYY年MM月DD日
    getNowFormatDate() {
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

});
