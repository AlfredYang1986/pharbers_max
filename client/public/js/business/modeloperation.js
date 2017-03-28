/**
 * Created by Wli on 2017/1/5.
 */

 var loader = new SVGLoader( document.getElementById( 'loader' ), { speedIn : 0, easingIn : mina.easeinout } );

 function operation(){
    $.cookie("filename","CPA_GYCX_Others_panel_2016_INF.xlsx")
    if($.cookie("filename") != null) {
         var dataMap = JSON.stringify({
          "company" : $.cookie("token"),
          "filename" : $.cookie("filename")
          })
         $.ajax({
             type : "post",
             data : dataMap,
             async : false,
             url :"/callrunmodel",
             contentType: 'application/json, charset=utf-8',
             cache : false,
             dataType : "json",
             success : function(json){
                 alert("本次运算可能会耗时半小时以上，稍后我们会以邮件的形式发送给您，请您点击确定按钮安全退出。")
             },
             error:function(e){
                 alert("Error")
             }
         });
     }else{
         alert("您生成的panel文件无效，请核对后重新生成。")
     }
 }

 function commitresult(){
    loader.show();
    var dataMap = JSON.stringify({
        "company" : $.cookie("token")
    })
     $.ajax({
         type : "post",
         data : dataMap,
         async : false,
         url :"/commitrunresult",
         contentType: 'application/json, charset=utf-8',
         cache : false,
         dataType : "json",
         success : function(json){
             alert("操作成功");
             loader.hide();
         },
         error:function(e){
             alert("Error");
             loader.hide();
         }
     });
 }