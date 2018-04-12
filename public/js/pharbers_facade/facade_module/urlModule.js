/**
 * Created by yym on 10/11/17.
 */
var URLHandler = function () {

};

URLHandler.prototype.getURLParam = function (name, flag) {
    var uri = location.href;
    if(flag) {
        return uri.substring(uri.lastIndexOf('/') + 1)
    }else {
        return uri.substring(uri.indexOf(name) + name.length, uri.lastIndexOf('/'))
    }
};

URLHandler.prototype.toHref = function (otherHref) {
    window.location.href(otherHref);
}