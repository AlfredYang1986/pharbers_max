var conn;
var load;
$(function(){
    conn = load_Web_IM();
    login("test", "1")
    $("#login").click(function(){

    });
    $("#register").click(function(){
        register("BMS", "1")
    });
    callback()
    load = new loading()
})

var load_Web_IM = function() {
    return new WebIM.connection({
        https: WebIM.config.https,
        url: WebIM.config.xmppURL,
        isAutoLogin: WebIM.config.isAutoLogin,
        isMultiLoginSessions: WebIM.config.isMultiLoginSessions
    });
}

var login = function(u, p) {
    var options = {
        apiUrl: WebIM.config.apiURL,
        user: u,
        pwd: p,
        appKey: WebIM.config.appkey
    };
    conn.open(options);
}

var register = function(u, p) {
    // TODO : 目前通过js开放注册
    var options = {
        username: u,
        password: p,
        nickname: '',
        appKey: WebIM.config.appkey,
        success: function () { console.info("is success") },
        error: function () { console.error("is error")},
        apiUrl: WebIM.config.apiURL
    };
    conn.registerUser(options);
}

var callback = function() {
    conn.listen({
        onOpened: function ( message ) {          //连接成功回调
            // 如果isAutoLogin设置为false，那么必须手动设置上线，否则无法收消息
            // 手动上线指的是调用conn.setPresence(); 如果conn初始化时已将isAutoLogin设置为true
            // 则无需调用conn.setPresence();
        },
        onClosed: function ( message ) {},         //连接关闭回调
        onTextMessage: function ( message ) {
            //$("#msg").append("<p> msg：" + message.data + "，uuid：" + message.ext.uuid + "，company：" + message.ext.gs + "</p>")
            load.setPercent(message.data)
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

var loading = function() {
    var w = 300, h = 320;
    var outerRadius = (w / 2) - 10;
    var innerRadius = outerRadius - 8;

    var color = ['#ec1561', '#2a3a46', '#202b33'];
    var arc = d3.svg.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(0)
        .endAngle(2 * Math.PI);

//The circle is following this
    var arcDummy = d3.svg.arc()
        .innerRadius((outerRadius - innerRadius) / 2 + innerRadius)
        .outerRadius((outerRadius - innerRadius) / 2 + innerRadius)
        .startAngle(0);

    var arcLine = d3.svg.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(0);

    var svg = d3.select("#chart").append("svg").attr({
        width: w,
        height: h,
        class: 'shadow'
    }).append('g').attr({
        transform: 'translate(' + w / 2 + ',' + h / 2 + ')'
    });

//background
    var path = svg.append('path').attr({d: arc}).style({
        fill: color[1]
    });

    var pathForeground = svg.append('path').datum({
        endAngle: 0
    }).attr({
        d: arcLine
    }).style({
        fill: color[0]
    });

    var endCircle = svg.append('circle').attr({
        r: 12,
        transform: 'translate(0,' + (-outerRadius + 15) + ')'
    }).style({
        stroke: color[0],
        'stroke-width': 8,
        fill: color[2]
    });

    var middleTextCount = svg.append('text').datum(0).text(function(d) {
        return d + '%';
    }).attr({
        class: 'middleText',
        'text-anchor': 'middle',
        dy: 25,
        dx: 0
    }).style({
        fill: '#ec1561',
        'font-size': '80px'
    });

    var arcTweenOld = function(transition, percent, oldValue) {
        transition.attrTween("d", function(d) {

            var newAngle = (percent / 100) * (2 * Math.PI);

            var interpolate = d3.interpolate(d.endAngle, newAngle);

            var interpolateCount = d3.interpolate(oldValue, percent);

            return function(t) {
                d.endAngle = interpolate(t);
                var pathForegroundCircle = arcLine(d);
                middleTextCount.text(Math.floor(interpolateCount(t)) + '%');
                var pathDummyCircle = arcDummy(d);
                var coordinate = pathDummyCircle.split("L")[1].split("A")[0];
                endCircle.attr('transform', 'translate(' + coordinate + ')');
                return pathForegroundCircle;
            };
        });
    };

    var oldValue = 0;

    this.setPercent = function(num) {
        pathForeground.transition()
            .duration(750)
            .ease('cubic')
            .call(arcTweenOld, num, oldValue);
        oldValue = num;
    }
}