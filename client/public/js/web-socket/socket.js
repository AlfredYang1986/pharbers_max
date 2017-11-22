var socket = (function($){
    // 加入到login当中去 暂时用全局变量，后续封装后再议
    var ws = new WebSocket(Web_Socket.config.socketURL);

    ws.onopen = function(evt) {
        ws.send(Web_Socket.config.register);
        console.info("WEB SOCKET IS ONOPEN");
    };

    ws.onclose = function(evt) {
        console.info("WEB SOCKET IS CLOSE");
    };

    ws.onerror = function(evt) {
        console.info("WEB SOCKET IS ERROR");
        console.info(evt);
    };

    var getValue = function(json){
        return function(key) {
            return json[key] === undefined ? "Null" : json[key]
        }
    };

    return {
        "callback2obj": function(callback) {
            ws.onmessage = function(evt) {
                callback( JSON.parse(evt.data) )
            }
        },
        "getValue": getValue
    };
}(jQuery));