/**
 * Created by yym on 9/22/17.
 */
if(typeof validateType  == "undefined"){
    var validateType = {};
    validateType.isEmp = 0;
    validateType.email = 1;
    validateType.phone = 2;
}
var validateEmail = function (idName, errMes) {
    var regexp =/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    var email = $('#'+idName).val();
    var res = regexp.test(email);
    if(res) return [true, "ok"];
    else return [false, errMes];
}

var validateMobilePhone = function (idName, errMes) {
    var regexp = /^1(3|4|5|7|8)[0-9]\d{8}$/;
    var res = regexp.test(phone);
    if(res) return [true, "ok"];
    else return [false, errMes];
}

var formIsEmpty = function (idName) {
    var elem = $('#' + idName);
    if(elem.val() == "")
        return [false, "输入不能为空"];
    else return[true, "ok"]
}

var warning = function (idName, res) {
    var elem = $('#' + idName)
    if(res[0] == false){
        elem.addClass('input_alert');
        elem.next(".form_tip_bottom").text(res[1]);
    }
}

var postValidation = function (idName, validateType) {
    if(validateType == "phone"){
        var res = validateMobilePhone(idName, "手机号码输入错误");
        warning(res);
    }else if(validateType == "email"){
        var res =validateEmail(idName, "邮箱输入格式错误");
        warning(res);
    }else {
        var res = formIsEmpty(idName);
        warning(res);
    }
}

