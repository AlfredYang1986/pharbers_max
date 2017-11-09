(function($, w){
    var f = new Facade()
    var web_url = "ws://127.0.0.1:9000/ws"
    var ws = new WebSocket(web_url)
    var obj = JSON.stringify({
        "name": "qp"
    });

    $(function(){
       $('#test-websocket').click(function(){
            f.ajaxModule.baseCall('/test2', obj, 'POST',function(r){}, function(e){console.error(e)})
       });
    });

    ws.onopen = function(evt) {
        ws.send(obj);
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
        $('body').append('<p>' + e.data + '</p>')
    }

    var web_socket_error = function(e) {
        console.info("ERROR");
        console.info(e);
    }

}(jQuery, window));

