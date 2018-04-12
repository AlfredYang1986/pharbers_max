/**
 * Created by yym on 10/9/17.
 */
var Facade = function () {
    this.validationModule = new ValidateHandler();
    this.ajaxModule =  new AjaxCall();
    this.URLModule = new URLHandler();
    this.cookieModule = new CookieHandler();
    this.parameterPrefix = new ParameterPrefix();
    this.thousandsModule = new Thousands();
    this.alertModule = new alertHandlers();
};
