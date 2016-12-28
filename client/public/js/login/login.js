$(function(){
	$("#loginBtn").click(function(){
		
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
			contentType: 'application/json,charset=utf-8',
			success: function(r){
				if(r.result.FinalResult == "input is null") {
					$("#loginSub").click();
				}else if(r.result.FinalResult == "is null") {
					alert("该用户不存在！！！")
				}else {
					$.cookie('token','098f6bcd4621d373cade4e832627b4f6',{expires: 7})
					location = "index"
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				console.info("Error")
			}
		});
	});
	
	JsCookie();
})

function JsCookie() {
	console.info($.cookie('the_cookie'))
}
