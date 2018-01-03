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

alertHandlers.prototype.content = function(domobj, skin, area, title, btns, yesFun, btn2Fun, btn3Fun ,cancelFun) {
    skin = skin || 'layer-ext-errLay';
    area = area || ['50%'];
    title = title || '信息';
    layui.use('layer', function() {
        layer.open({
            type: 1,
            title: title,
            btn: btns,
            yes: yesFun,
            btn2: btn2Fun,
            btn3: btn3Fun,
            offset: ['10%'],
            area: area,
            zIndex: 1000,
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

alertHandlers.prototype.open = function(obj) {
    layui.use('layer', function() {
        this.title = obj.title || '信息';
        this.content = obj.content || '';
        this.area = obj.area || '35%';
        this.offset = obj.offset || 'auto';
        this.btns = obj.btns || [];
        this.cancel = obj.cancel || null;
        this.btn1 = obj.btn1 || null;
        this.btn2 = obj.btn2 || null;
        this.btn3 = obj.btn3 || null;
        this.btn4 = obj.btn4 || null;

        var layer = layui.layer;
        layer.open({
            title: this.title,
            type: 1,
            offset: this.offset,
            resize: false,
            closeBtn: 0,
            moveOut: false,
            area: this.area,
            btn: this.btns,
            yes: this.btn1,
            btn2: this.btn2,
            btn3: this.btn3,
            btn4: this.btn4,
            cancel: this.cancel,
            content: this.content
        });
    });
};