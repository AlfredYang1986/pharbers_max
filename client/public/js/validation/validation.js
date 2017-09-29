function ValidateHandler() {
    this.ele_arr = [];
}

ValidateHandler.prototype.validateEmail = function (idName, errMes) {
    var regexp =/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    var email = $('#'+idName).val();
    var res = regexp.test(email);
    if (res) return[true, "ok"]
    else return [false, errMes];
}

ValidateHandler.prototype.validateMobilePhone = function (idName, errMes) {
    var regexp = /^1(3|4|5|7|8)[0-9]\d{8}$/;
    var phone =  $('#'+idName).val();
    var res = regexp.test(phone);
    if(res) return [true, "ok"];
    else return [false, errMes];
}

ValidateHandler.prototype.vaildateCompanyName = function (idName, errMes) {
    var regexp = /^[\u4e00-\u9fa5]{6,30}$/;
    var name =  $('#'+idName).val();
    var res = regexp.test(name);
    if (res) return [true, "ok"]
    else return [false, errMes];
}

ValidateHandler.prototype.vaildateName = function (idName, errMes) {
    var regexp = /^[\u4e00-\u9fa5]{2,4}$/;
    var name =  $('#'+idName).val();
    var res = regexp.test(name);
    if (res) return [true, "ok"];
    else return [false, errMes];
}

ValidateHandler.prototype.formIsEmpty = function (idName) {
    var elem = $('#' + idName);
    if(elem.val() == "")
        return [false, "输入不能为空"];
    else return[true, "ok"]
}

ValidateHandler.prototype.validatePassword = function (idName, errMes) {
    var regexp = /^[a-zA-Z0-9]{6,20}$/;
    var pwd = $('#'+idName).val();
    var res = regexp.test(pwd);
    if (res) return [true, "ok"];
    else return [false, errMes];
}

ValidateHandler.prototype.changeClass = function (old, newOne, idName) {
    var elem = $('#'+idName)
    if(elem.hasClass(old)){
        elem.removeClass(old);
    }
}

ValidateHandler.prototype.dealInfo = function (idName, res, succClass,errClass) {
    var elem = $('#' + idName);
    if(res[0] == false){
        this.changeClass(succClass, errClass, idName);
        elem.next(".form_tip_bottom").html('<i class="layui-icon">&#xe69c;</i>'+'<span>&nbsp'+res[1]+'</span>');
    }else{
        this.changeClass(errClass, succClass, idName);
        elem.next(".form_tip_bottom").empty();
    }
}

//需要验证的 input id,  验证类型， 正确样式，错误样式
ValidateHandler.prototype.postValidation = function (idName, validateType, succ, err) {
    var res;
    if(validateType === "phone"){
        res = this.validateMobilePhone(idName, "手机号码输入错误");
    }else if(validateType === "email"){
        res = this.validateEmail(idName, "邮箱输入格式错误");
    }else if(validateType === "pwd"){
        res = this.validatePassword(idName, "请输入6~20位字母或数字");
    } else if(validateType === "cName"){
        res = this.vaildateCompanyName(idName, "请输入公司全称（中文6~30字）");
    } else if(validateType === "name"){
        res = this.vaildateName(idName, "请输入自己正确的名字（中文2~4字）");
    }else{
        res = this.formIsEmpty(idName);
    }

    this.dealInfo(idName, res, succ, err);
    return res[0];
}

ValidateHandler.prototype.input_blur = function (idName, validateType) {
    var elem = $('#' + idName);
    var that = this;
    elem.blur(function () {
        that.postValidation(idName, validateType, 'input_success', 'input_alert');
    })
    var obj = new Object()
    obj['idName'] = idName;
    obj['validateType'] = validateType;
    this.ele_arr.push(obj);
}

ValidateHandler.prototype.finalResult = function () {
    var result = 1;
    var that = this;
    $.each(this.ele_arr, function(index, iter) {
        result &= that.postValidation(iter['idName'], iter['validateType'], 'input_success', 'input_alert');
    });
    return result;
}

ValidateHandler.prototype.finalResultWithExt = function (ext) {
    var result = 1;
    var that = this;
    $.each(this.ele_arr, function(index, iter) {
        if (ext.indexOf(iter['idName']) == -1)
            result &= that.postValidation(iter['idName'], iter['validateType'], 'input_success', 'input_alert');
    });
    return result;
}