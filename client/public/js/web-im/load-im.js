var im_object = (function($, w){
    var conn;

    var f = new Facade()

    var load_web_im = function() {
        return new WebIM.connection({
            https: WebIM.config.https,
            url: WebIM.config.xmppURL,
            isAutoLogin: WebIM.config.isAutoLogin,
            isMultiLoginSessions: WebIM.config.isMultiLoginSessions
        });
    }

    var login_im = function(u, p) {
        conn = load_web_im();
        var options = {
            apiUrl: WebIM.config.apiURL,
            appKey: WebIM.config.appkey,
            user: u,
            pwd: p,
            success: function(token) {
                f.cookieModule.setCookie('webim_token', token.access_token);
                f.cookieModule.setCookie('webim_user', token.user.username);
            },
            error: function(m){console.error("Error = " + m)}
        };
        conn.open(options);
    }

    var load_im = function() {
        conn = load_web_im();
        if($.cookie('webim_user') !== undefined) {
            var options = {
                apiUrl: WebIM.config.apiURL,
                appKey: WebIM.config.appkey,
                user: $.cookie('webim_user'),
                accessToken: $.cookie('webim_token')
            };
            conn.open(options);
            // callback();
        }
    }

    // var callback = function() {
    //     conn.listen({
    //         onOpened: function ( message ) {console.log(message);},
    //         onClosed: function ( message ) {},         //连接关闭回调
    //         onTextMessage: function ( message ) {
    //             var ext = message.ext;
    //             if(ext !== null) {
    //                 var reVal = searchExtJson(ext)('type');
    //                 console.info(flag);
    //                 if(reVal === 'progress') {
    //
    //                 } else if(reVal === 'txt') {
    //
    //                 } else {
    //                     console.warn("No Type");
    //                     console.warn(message.data);
    //                 }
    //             }
    //         },    //收到文本消息
    //         onOnline: function () {},                  //本机网络连接成功
    //         onOffline: function () {},                 //本机网络掉线
    //         onError: function ( message ) { console.error(message) }          //失败回调
    //     });
    // }

    var im_close = function() {conn.close()}



    var searchExtJsonForElement = function(elems) {
        return function(key) {
            try {
                var value = "Null";
                $.each(elems, function (i, v) {
                    if(v.key === key) { value = v.value;}
                })
            } catch(ex) {
                console.error(ex);
            }
            return value;
        }
    }

    var searchExtJson = function(json) {
        return function(key) {
            try {
                var key2 = "Null";
                $.each(json, function(i, v) {
                    if(v === key && i.indexOf("key") > -1) {
                        key2 = "value"+i.substring(3);
                        return false
                    }
                });
            } catch(ex) {
                console.error(ex);
            }
            return json[key2] === undefined ? key2 : json[key2]
        }
    }

    var searchSocketJson = function(json) {
        return function(key) {
            return json[key] === undefined ? "Null" : json[key]
        }
    }

    load_im();

    return {
        "searchExtJsonForElement": searchExtJsonForElement,
        "searchExtJson": searchSocketJson,
        // "callback": function() {callback()},
        "load_im": load_im,
        "login_im": login_im,
        "load_web_im": load_web_im,
        "im_close": im_close,
        "conns": function() {return conn}
    }
}(jQuery, window));