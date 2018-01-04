var alertHandlers = function(){}

// 暂时不要用这个
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

alertHandlers.prototype.error = function(obj) {
    layui.use('layer', function(){
        this.icon = obj.icon || undefined;
        this.title = obj.title || '信息';
        this.content = obj.content || '';
        this.area = obj.area || '35%';
        this.offset = obj.offset || 'auto';
        this.btns = obj.btns || [];
        this.btn1 = obj.btn1 || null;
        this.btn2 = obj.btn2 || null;
        this.btn3 = obj.btn3 || null;
        this.btn4 = obj.btn4 || null;

        var layer = layui.layer;
        layer.confirm(this.content, {
            icon: this.icon,
            title: '<b style="color: red; font: 16px bold Monaco;"> ' + this.title + ' </b>',
            offset: this.offset,
            area: this.area,
            resize: false,
            move: false,
            closeBtn: 0,
            btn: this.btns,
            yes: this.btn1,
            btn2: this.btn2,
            btn3: this.btn3,
            btn4: this.btn4
        });
    });
};

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
            move: false,
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
