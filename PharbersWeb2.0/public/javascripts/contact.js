$(function() {
    // 验证 非空 函数
    function isBlank(object) {
        var company = $('input[name="user-company"]').val();
        var username = $('input[name="user-name"]').val();
        var email = $('input[name="user-email"]').val();
        var phoneNumber = $('input[name="user-phonenumber"]').val();
        if(company == "" && username == "" && email == "" && phoneNumber == "" ){
            $('span[name="hint-text-company"]').show();
            $('span[name="hint-text-username"]').show();
            $('span[name="hint-text-email"]').show();
            $('span[name="hint-text-phonenumber"]').show();
            return false;
        } else if (company == "") {
            console.log("company is blank")
            $('span[name="hint-text-company"]').show();
            return false;
        } else if (username == "") {
            $('span[name="hint-text-username"]').show();
            return false;
        } else if (email == "") {
            $('span[name="hint-text-email"]').show();
            return false;
        } else if (phoneNumber == "") {
            $('span[name="hint-text-phonenumber"]').show();
            return false;
        } else {
            $('span[name="hint-text-company"]').hide();
            $('span[name="hint-text-username"]').hide();
            $('span[name="hint-text-email"]').hide();
            $('span[name="hint-text-phonenumber"]').hide();
            return true;
        }

        // var hasValue = object.val();
        // if(hasValue == "") {
        //     // object.next("span").show();
        //     return false;
        // } else {
        //     // object.next("span").hide();
        //     return true;
        // }
    }
    // 验证email 函数
    function isEmail(object) {
        var x = object.val();
        var atpos = x.indexOf("@");
        var dotpos = x.lastIndexOf(".");
        if (atpos < 1 || dotpos < atpos + 2 || dotpos + 2 >= x.length) {
            // alert("不是一个有效的 e-mail 地址");
            $('span[name="hint-text-email"]').show();
            return false;
        }
    };
    // 验证phone number 函数
    function isPhoneNumber(object) {
        var phone = object.val();
        if(!(/^1[34578]\d{9}$/.test(phone))){
            $('span[name="hint-text-phonenumber"]').show();
            return false;
        }
    };
    // 显示用户填写信息页面
    $('button[name="show-message-wrapper"]').click(function(e) {
        e.preventDefault();
        $('body').css("overflow", "hidden")
        $('.user-message-wrapper').show();
    });
    // 隐藏用户填写信息页面
    $('button[name="close-message-wrapper"]').click(function(e) {
        e.preventDefault();
        $('body').css("overflow", "auto");
        $('.user-message-wrapper').hide();
    });
    // 验证 公司名称 的动作
    $('input[name="user-company"]').focusout(function() {
        if($(this).val() == "") {
            $('span[name="hint-text-company"]').show();
        } else {
            $('span[name="hint-text-company"]').hide();
        }
    });
    $('input[name="user-company"]').focus(function() {
        $('span[name="hint-text-company"]').hide();
    });
    // 验证 公司名称 的动作结束
    // 验证 联系人姓名 的动作
    $('input[name="user-name"]').focusout(function() {
        if($(this).val() == "") {
            $('span[name="hint-text-username"]').show();
        }else {
            $('span[name="hint-text-username"]').hide();
        }
    });
    $('input[name="user-name"]').focus(function() {
        $('span[name="hint-text-username"]').hide();
    });
    // 验证 联系人姓名 的动作结束
    // 验证email 的动作
    $('input[name="user-email"]').focusout(function() {
        isEmail($(this));
    });
    $('input[name="user-email"]').focus(function() {
        $('span[name="hint-text-email"]').hide();
    });
    // 验证email 的动作结束
    // 验证 phone number 的动作
    $('input[name="user-phonenumber"]').focusout(function() {
        isPhoneNumber($(this));
    });
    $('input[name="user-phonenumber"]').focus(function() {
        $('span[name="hint-text-phonenumber"]').hide();
    });
    // 验证 phone number 的动作结束

    // 提交按钮验证
    $('button[name="submit-contact-message"]').click(function(e){
        e.preventDefault();
        if(isBlank() == false ) {
            console.log("fail")
        } else {
            $('.submit-success').show();
            setTimeout(function(){
                $('.user-message-wrapper').hide();
                $('.submit-success').hide();
            },1500)
        }

    })
})
