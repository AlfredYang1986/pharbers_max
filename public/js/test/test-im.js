(function($, w){
    var f = new Facade();
    // 加入到login当中去 暂时用全局变量，后续封装后再议
    var web_url = "ws://127.0.0.1:9000/ws";
    // f.cookieModule.setCookie("uid", "qp")
    var ws = new WebSocket(web_url);
    var obj = JSON.stringify({
        "uid": $.cookie("uid") // 就这样穿参数
    });

    $(function(){
        $('#test-websocket').click(function(){
            var json = JSON.stringify({
                "businessType": "/test",
                "str": $.cookie("uid"),
                "lst": ['f', 'u', 'c', 'k']
            });
            f.ajaxModule.baseCall('/calc/callhttp', json, 'POST', function(r){}, function(e){console.error(e)});
        })
    })

    ws.onopen = function(evt) {
        ws.send(obj); // 一定要在打开后send
        web_socket_open(evt);
    };
    ws.onclose = function(evt) {
        web_socket_close(evt)
    };
    ws.onmessage = function(evt) {
        web_socket_message(evt)
    };
    ws.onerror = function(evt) {
        web_socket_error(evt)
    };

    var web_socket_open = function(e) {
        console.info("CONNECTED");
        console.info(e);
    }

    var web_socket_close = function(e) {
        console.info("CLOSE");
        console.info(e);
    }

    var web_socket_message = function(e) {
        console.info("MESSAGE");
        var ext = JSON.parse(e.data);
        console.info(ext);
        var aa = window.im_object.searchExtJson(ext)('aaa')
        console.info(aa)
        $('body').append('<p>' + e.data + '</p>')
    }

    var web_socket_error = function(e) {
        console.info("ERROR");
        console.info(e);
    }

}(jQuery, window));

