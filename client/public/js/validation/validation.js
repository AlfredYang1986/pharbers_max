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
    console.log(email)
    var res = regexp.test(email);
    console.log(res)
    if(res) {
        return [true, "ok"];
    }
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

var changeClass = function (old, newOne, idName) {
    var elem = $('#'+idName)
    if(elem.hasClass(old)){

        elem.removeClass(old);
        elem.addClass(newOne);
    }else {
        console.log(elem)
        elem.addClass(newOne)
    }
}

var dealInfo = function (idName, res, succClass,errClass) {
    var elem = $('#' + idName);
    console.log(res)
    if(res[0] == false){
        console.log("sad")
        changeClass(succClass, errClass, idName)
        elem.next(".form_tip_bottom").text(res[1]);
    }else{
        changeClass(errClass, succClass, idName)
        elem.next(".form_tip_bottom").text("");
    }
}

//需要验证的 input id,  验证类型， 正确样式，错误样式
var postValidation = function (idName, validateType,succ, err ) {
    if(validateType == "phone"){
        var res = validateMobilePhone(idName, "手机号码输入错误");
        dealInfo(idName, res, succ, err);
    }else if(validateType == "email"){
        var res =validateEmail(idName, "邮箱输入格式错误");
        dealInfo(idName, res, succ, err);
    }else {
        var res = formIsEmpty(idName);
        dealInfo(idName, res, succ, err);
    }
}

var input_blur = function (idName, validateType) {
    var elem = $('#' + idName);
    elem.blur(function () {
        postValidation(idName, validateType, 'input_success', 'input_alert');
    })
}

