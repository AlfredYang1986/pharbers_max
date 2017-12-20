var show_loading = function() {
    var $h = $('.container-fluid').height();
    $('.mask-layer').css({
        "height": $h
    }).show();
    $('.loading').show();
};
var hide_loading = function() {
    $('.mask-layer').hide();
    $('.loading').hide();
};