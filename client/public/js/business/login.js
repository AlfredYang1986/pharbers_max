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
			if(r.status == "ok") {
                if(r.result.FinalResult == "input is null") {
                    $("#loginSub").click();
                }else if(r.result.FinalResult == "is null") {
                    alert("用户名或密码错误！！！")
                }else if(r != null  && r != ""){
                    login_im(userName, userPass);
                    $.cookie("user_token",r.result.FinalResult.User_Token);
                    $.cookie("user_name",r.result.FinalResult.UserName);
                    $.cookie("user_auth",r.result.FinalResult.UserAuth);
                    $.cookie("is_administrator",r.result.FinalResult.IsAdministrator);
                    $.cookie("token",r.result.FinalResult.Token);
                    $.cookie("company_name_ch",r.result.FinalResult.CompanyNameCh);
                    $.cookie("company_name_en",r.result.FinalResult.CompanyNameEn);
                    $.cookie("email",r.result.FinalResult.E_Mail)
                    $.cookie("ip",r.result.FinalResult.ip);
                    $.tooltip('OK, 登录成功！', 2500, true);
                    setTimeout(function () {
                        location = "index"
                    }, 1000 * 3)
                }
			}else {
                $.tooltip('清理浏览器Cookie');
			}

		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			console.info("Error")
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
