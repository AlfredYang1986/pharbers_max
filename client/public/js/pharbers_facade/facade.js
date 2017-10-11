/**
 * Created by yym on 10/9/17.
 */
var Facade = function () {
    this.validationModule = new ValidateHandler();
    this.ajaxModule =  new AjaxCall();
    this.URLModule = new URLHandler();
    this.cookieModule = new CookieHandler();
};


Facade.prototype.validationModule = function () {
    return this.validationModule;
};

Facade.prototype.ajaxModule = function () {
    return this.ajaxModule;
};

Facade.prototype.URLModule = function () {
    return this.URLModule;
};

Facade.prototype.cookieModule = function () {
    return this.cookieModule;
};
