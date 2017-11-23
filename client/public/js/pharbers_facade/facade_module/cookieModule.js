/**
 * Created by yym on 10/11/17.
 */
var CookieHandler = function () {

};

CookieHandler.prototype.setCookie = function (key, value) {
    $.cookie(key, value, {path: '/'})
};

CookieHandler.prototype.cleanAllCookie = function () {
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if(keys) {
        $.each(keys, function(i, v) {
            $.cookie(v, "", {"path": "/", "expires": -1 });
        });
    }
};