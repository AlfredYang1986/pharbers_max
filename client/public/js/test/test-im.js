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
        function login() {//891366402@qq.com  pqian@pharbers.com
            var map = {"email": 'pqian@pharbers.com', "password": md5('pqian@pharbers.com' + 'aaaaaa')};
            var json  = JSON.stringify(f.parameterPrefix.conditions(map))
            f.ajaxModule.baseCall('auth/authWithPassword', json, 'POST', function(r) {
                if(r.status === 'ok') {
                    f.cookieModule.setCookie("uid", r.result.uid);
                    w.im_object.login_im(r.result.imuid, r.result.imuid);
                }
            }, function(e) {console.error(e)})
        }
        // login();
    }

    var callback = function() {
        var conn = window.im_object.conns();
        conn.listen({
            onOpened: function ( message ) {w.console.info("im 连接成功");},
            onClosed: function ( message ) {},         //连接关闭回调
            onTextMessage: function ( message ) {
                var ext = message.ext;
                if(ext !== null) {
                    var reVal = w.im_object.searchExtJson(ext)('type') !== 'Null' ? w.im_object.searchExtJson(ext)('type') : w.im_object.searchExtJsonForElement(ext.elems)('type');
                    switch (reVal) {
                        case 'progress':
                            progress(message);
                            break;
                        case 'progress_calc':
                            progress_calc(message);
                            break;
                        case 'progress_calc_result':
                            progress_calc_result(message);
                            break;
                        case 'txt':
                            txt(message);
                            break;
                        default:
                            console.warn(message.ext);
                            console.warn("No Type");
                            console.warn(message.data);
                    }
                }
            },    //收到文本消息
            onOnline: function () {},                  //本机网络连接成功
            onOffline: function () {},                 //本机网络掉线
            onError: function ( message ) { console.error(message) }          //失败回调
        });
    }

    var progress = function(msg) {
        console.info(msg);
    }

    var progress_calc = function(msg) {
        console.info(msg);
    }

    var progress_calc_result = function(msg) {
        console.info(msg);
    }

    var txt = function(msg) {
        console.info(msg)
    }
    createIMTempUser();
    callback();
}(window));

