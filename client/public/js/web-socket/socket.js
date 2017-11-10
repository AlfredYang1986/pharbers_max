var socket = (function($){
    // 加入到login当中去 暂时用全局变量，后续封装后再议
    var web_url = "ws://127.0.0.1:9000/ws";
    var ws = new WebSocket(web_url);

    ws.onopen = function(evt) {
        var obj = JSON.stringify({
            "uid": $.cookie("uid")
        });
        ws.send(obj);
        console.info("WEB SOCKET IS ONOPEN");
    };

    ws.onclose = function(evt) {
        console.info("WEB SOCKET IS CLOSE");
    };

    ws.onerror = function(evt) {
        console.info("WEB SOCKET IS ERROR");
        console.info(evt);
    };

    var searchSocketJson = function(json){
        return function(key) {
            return json[key] === undefined ? "Null" : json[key]
        }
    };

    return {
        "ws": ws,
        "searchExtJson": searchSocketJson
    };
}(jQuery));