(function(w){

   var f = new Facade();
   // f.alertModule.success('通过');
   // f.alertModule.error('失败');
   // f.alertModule.content($('.container'), null, null, '测试', function(index, layero) {
   //     // console.info(123)
   //     // layer.close(index)
   // });
   // f.alertModule.contentIFrame('http://www.baidu.com', null, null, '测试');

    var createIMTempUser = function() {
        function login() {
            var map = {"email": 'pqian@pharbers.com', "password": md5('pqian@pharbers.com' + 'aaaaaa')};
            var json  = JSON.stringify(f.parameterPrefix.conditions(map))
            f.ajaxModule.baseCall('auth/authWithPassword', json, 'POST', function(r) {
                if(r.status === 'ok') {
                    w.im_object.login_im(r.result.uuid, r.result.uuid);
                }
            }, function(e) {console.error(e)})
        }
        w.im_object.login_im("49fce7d6228ad90ec4865248cce4735e", "49fce7d6228ad90ec4865248cce4735e");
        // login();
    }

    var callback = function() {
        var conn = window.im_object.conns();
        conn.listen({
            onOpened: function ( message ) {console.info("im 连接成功")},
            onClosed: function ( message ) {},         //连接关闭回调
            onTextMessage: function ( message ) {
                console.info(message);
                var ext = message.ext;
                if(ext !== null) {

                }
            },    //收到文本消息
            onOnline: function () {},                  //本机网络连接成功
            onOffline: function () {},                 //本机网络掉线
            onError: function ( message ) { console.error(message) }          //失败回调
        });
    }

    var handlePresence = function(e) {
        console.info(e)
    }
    createIMTempUser();
    callback();
}(window));

