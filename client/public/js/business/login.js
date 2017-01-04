$(function(){
	$("body").keydown(function() {
	    if (event.keyCode == "13") {
	    	login();
	    	return false; 
	    }
	});
	$("#loginBtn").click(function(){
		login();
	});
    loginInfo()
})

function login() {
	var userName = $("#loginForm [name='name']").val()
	var userPass = $("#loginForm [name='password']").val()
	var d = JSON.stringify({
		"ID" : userName,
		"Password" : userPass
	})
	$.ajax({
		type: "POST",
		url: "/login/start",
		dataType: "json",
		data: d,
		contentType: "application/json,charset=utf-8",
		success: function(r){
			if(r.result.FinalResult == "input is null") {
				$("#loginSub").click();
			}else if(r.result.FinalResult == "is null") {
				alert("该用户不存在！！！")
			}else {
				$.cookie("token",r.result.FinalResult.Token)
				$.cookie("loginName",r.result.FinalResult.Name)
                $.cookie("companyName",r.result.FinalResult.Name)
				location = "index"
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			console.info("Error")
		}
	});
}

function logout() {
	$.cookie("token", "", {"path": "/", "expires": -1 });
	location = "login"
}

function loginInfo() {
	if($.cookie("loginName") == undefined) {
        $("#Name").text("未知")
        $("#company").text("未知")
	}else {
		$("#Name").text($.cookie("loginName"))
        $("#company").text($.cookie("companyName"))
	}
}
