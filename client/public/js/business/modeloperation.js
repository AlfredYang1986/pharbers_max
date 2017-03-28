/**
 * Created by Wli on 2017/1/5.
 */

 function operation(){
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
                 console.info(json)
                 alert("正在运算")
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
    var dataMap = JSON.stringify({"company" : $.cookie("token")})
     $.ajax({
         type : "post",
         data : dataMap,
         async : false,
         url :"/commitrunresult",
         contentType: 'application/json, charset=utf-8',
         cache : false,
         dataType : "json",
         success : function(json){
             alert("操作成功")
         },
         error:function(e){
             alert("Error")
         }
     });
 }