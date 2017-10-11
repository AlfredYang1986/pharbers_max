/**
 * Created by yym on 10/9/17.
 */
var Facade = function () {
}


Facade.prototype.validationModule = function () {
    return new ValidateHandler();
}

Facade.prototype.ajaxModule = function () {
    return new Ajax();
}