var alertHandlers = function(){}

alertHandlers.prototype.success = function(message) {
    layui.use('layer', function() {
        layer.alert(message, {
            title: ['提示', 'font-size:14px;height:28px;line-height:28px;'],
            icon: 1,
            btn: false,
            closeBtn: 2,
            move: false,
            offset: ['50%','40%'],
            area: ['30px', '100px']
        });
    });
}

alertHandlers.prototype.error = function(message) {
    layui.use('layer', function() {
        layer.alert(message, {
            title: ['提示', 'font-size:14px;height:28px;line-height:28px;'],
            icon: 2,
            btn: false,
            closeBtn: 2,
            move: false,
            offset: ['50%','40%'],
            area: ['30px', '100px']
        });
    });
}

alertHandlers.prototype.content = function(domobj, skin, area, title, cancelFun) {
    skin = skin || 'layer-ext-errLay';
    area = area || ['50%'];
    title = title || '信息';
    layui.use('layer', function() {
        layer.open({
            type: 1,
            title: title,
            offset: ['10%'],
            area: area,
            skin: skin,
            moveOut: false,
            // scrollbar: false,
            resize: false,
            content: domobj,
            cancel: cancelFun
        });
    });
}

alertHandlers.prototype.contentIFrame = function(domobj, skin, area, title, cancelFun) {
    skin = skin || 'layer-ext-errLay';
    area = area || ['50%', '50%'];
    title = title || '信息';
    layui.use('layer', function() {
        layer.open({
            type: 2,
            offset: ['10%'],
            title: title,
            moveOut: false,
            area: area,
            skin: skin,
            content: domobj,
            cancel: cancelFun
        });
    });
}
