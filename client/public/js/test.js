$(function(){
    $("#test1").click(function(){
        console.info("in")
        $.ajax({
            type: "get",
            url: "http://127.0.0.1:5000/Test",
            cache : false,
            dataType : "jsonp",
            jsonp: "callbackparam",
            jsonpCallback:"jsonpCallback1",
            success: function(r){
                console.info(r)
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                console.info("Error")
            }
        });
    });

    $("#test2").click(function(){
        var dataMap = JSON.stringify({
            "filename" : "123456.txt",
            "company" : $.cookie("token"),
            "filetype": "0"
        })
        $.ajax({
            type: "post",
            url: "/callrunmodel",
            cache : false,
            data : dataMap,
            contentType: "application/json,charset=utf-8",
            dataType : "json",
            success: function(r){
                console.info(r)
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                console.info("Error")
            }
        });
    });
})