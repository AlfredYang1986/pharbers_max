$(function(){
	$("body").keydown(function(event) {
        if(event.which == 13){
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
		"Account" : userName,
		"Password" : userPass
	})
	$.ajax({
		type: "POST",
		url: "/login/start",
		dataType: "json",
        cache: false,
		data: d,
		contentType: "application/json,charset=utf-8",
		success: function(r){
			if(r.result.status == "success"){
				var user = r.result.result.result
				login_im(userName, userPass);
				$.cookie("user_token",user.User_Token);
				$.cookie("user_name",user.UserName);
				$.cookie("user_auth",user.UserAuth);
				$.cookie("auth",user.Auth);
				$.cookie("token",user.Token);
				$.cookie("company_name_ch",user.CompanyNameCh);
				$.cookie("company_name_en",user.CompanyNameEn);
				$.cookie("email",user.E_Mail)
				$.cookie("ip",user.ip);
				$.tooltip('OK, 登录成功！', 2500, true);
				setTimeout(function () {
					location = "index"
				}, 1000 * 3)
			}else{
				$.tooltip(r.result.message);
			}
		}
	});
}

function logout() {
    conn.close();
    cleanAllCookie();
	location = "login"
}


var cleanAllCookie = function() {
	var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if(keys) {
		$.each(keys, function(i, v) {
            $.cookie(v, "", {"path": "/", "expires": -1 });
		})
    }
}

function loginInfo() {
	if($.cookie("user_name") == undefined) {
        $("#Name").text("未知")
        $("#company").text("未知")
	}else {
		$("#Name").text($.cookie("user_name"))
        $("#company").text($.cookie("company_name_ch"))
	}
}
