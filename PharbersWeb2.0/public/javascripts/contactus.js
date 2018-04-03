$(function () {
    var json = JSON.stringify({"userName": "Alex"});

    $.ajax({
        type: 'POST',
        url: 'contactus',
        dataType: "json",
        cache: false,
        data: json,
        contentType: "application/json,charset=utf-8",
        Accept: "application/json,charset=utf-8",
        success: function (data) {

        }
    });
});