/**
 * Created by yym on 9/19/17.
 */
$(document).ready(function(){
    $('#popLayer').hide();
    $('#openTextValidate').hide();
})

var openTextValidate = function(){
    var index = layer.open({
        type: 1,
        skin: 'layui-layer-demo',
        area: '600px',
        shade: [0.8, '#393D49'],
        content: $('#popTextValidate'),
        cancel: function(index, layero){
            layer.close(index)
            $('#popTextValidate').hide();
        }
    });
    layer.title("身份验证", index)
    layer.style(index, {
        width: '600px',
        top: '10px',
        color: '#fff'
    });
}