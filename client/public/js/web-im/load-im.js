var conn, msgIdentifying = 0;

var beget = function(obj) {
    var F = function(){};
    F.prototype = obj;
    return new F();
}

$(function(){
    conn = load_Web_IM();
})

var load_Web_IM = function() {
    return new WebIM.connection({
        https: WebIM.config.https,
        url: WebIM.config.xmppURL,
        isAutoLogin: WebIM.config.isAutoLogin,
        isMultiLoginSessions: WebIM.config.isMultiLoginSessions
    });
}

/**
 * 只针对Max的Login的登录与获取WEB-IM
 * token
 * @param u
 * @param p
 */
var login_im = function(u, p) {
    conn = load_Web_IM();
    var options = {
        apiUrl: WebIM.config.apiURL,
        appKey: WebIM.config.appkey,
        user: u,
        pwd: p,
        success: function(token) {
            $.cookie('webim_token', token.access_token);
            $.cookie('webim_user', token.user.username);
        },
        error: function(m){
            console.info("Error = " + m)
        }
    };
    conn.open(options);
}

/***
 * 每个页面都需要重新加载这个方法
 * 该方法是通过WEB-IM的token登录
 */
var load_im = function() {
    conn = load_Web_IM();
    if($.cookie('webim_user') != undefined) {
        var options = {
            apiUrl: WebIM.config.apiURL,
            appKey: WebIM.config.appkey,
            user: $.cookie('webim_user'),
            accessToken: $.cookie('webim_token')
        };
        conn.open(options);
        callback();
    }
}

var callback = function() {
     conn.listen({
        onOpened: function ( message ) {          //连接成功回调
            // 如果isAutoLogin设置为false，那么必须手动设置上线，否则无法收消息
            // 手动上线指的是调用conn.setPresence(); 如果conn初始化时已将isAutoLogin设置为true
            // 则无需调用conn.setPresence();
            console.info(message)
        },
        onClosed: function ( message ) {},         //连接关闭回调
        onTextMessage: function ( message ) {
            var ext = message.ext
            if (ext != null) {
                var result = searchExtJson(ext)("type")
                if(result == "progress") {
                    var r = p.setPercent(parseInt(message.data))
                    msgIdentifying = parseInt(message.data)
                    if(parseInt(message.data) >= 100 || r >= 100) {
                        p.setPercent(0)
                        $(".progresstier").css("display", "none");
                    }
                }else if(result == "txt") {
                    console.info(message.data);
                }else {
                    console.info("No Type");
                    console.info(message.data);
                }
            }
        },    //收到文本消息
        onEmojiMessage: function ( message ) {},   //收到表情消息
        onPictureMessage: function ( message ) {}, //收到图片消息
        onCmdMessage: function ( message ) {},     //收到命令消息
        onAudioMessage: function ( message ) {},   //收到音频消息
        onLocationMessage: function ( message ) {},//收到位置消息
        onFileMessage: function ( message ) {},    //收到文件消息
        onVideoMessage: function (message) {
            var node = document.getElementById('privateVideo');
            var option = {
                url: message.url,
                headers: {
                    'Accept': 'audio/mp4'
                },
                onFileDownloadComplete: function (response) {
                    var objectURL = WebIM.utils.parseDownloadResponse.call(conn, response);
                    node.src = objectURL;
                },
                onFileDownloadError: function () {
                    console.log('File down load error.')
                }
            };
            WebIM.utils.download.call(conn, option);
        },   //收到视频消息
        onPresence: function ( message ) {},       //收到联系人订阅请求、处理群组、聊天室被踢解散等消息
        onRoster: function ( message ) {},         //处理好友申请
        onInviteMessage: function ( message ) {},  //处理群组邀请
        onOnline: function () {},                  //本机网络连接成功
        onOffline: function () {},                 //本机网络掉线
        onError: function ( message ) { console.error(message) },          //失败回调
        onBlacklistUpdate: function (list) {       //黑名单变动
            // 查询黑名单，将好友拉黑，将好友从黑名单移除都会回调这个函数，list则是黑名单现有的所有好友信息
            console.log(list);
        }
    });
}

var searchExtJson = function(json) {
    return function(key) {
        var key2 = "Null";
        $.each(json, function(i, v) {
            if(v == key && i.indexOf("key") > -1) {
                key2 = "value"+i.substring(3)
                return false
            }
        })
        return json[key2] == undefined ? key2 : json[key2]
    }
}
