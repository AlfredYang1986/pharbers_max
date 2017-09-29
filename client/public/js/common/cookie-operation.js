(function ($) {
    $.setCookie = function(key, value) {
        $.cookie(key, value, {path: '/'})
    }

    $.cleanAllCookie = function() {
        var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
        if(keys) {
            $.each(keys, function(i, v) {
                $.cookie(v, "", {"path": "/", "expires": -1 });
            })
        }
    }
})(jQuery);