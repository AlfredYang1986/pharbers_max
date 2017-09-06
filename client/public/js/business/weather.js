/**
 * Created by qianpeng on 2017/5/4.
 */

$(function() {

})

// window.onload = function() {
//     // weather($.cookie("ip"))
// }

var weather = function(ip) {
    var showapi_appid = "36787"
    var showapi_sign = "faf8ccd9975c4c8a8604a6abbd482a2f"
    var url = "http://route.showapi.com/9-4?showapi_appid="+
                showapi_appid+ "&showapi_sign=" +
                showapi_sign + "&ip=" + ip + "&needMoreDay=0&needIndex=0&needHourData=0&"
    $.getJSON(url, function(data) {
        var showapi_res_body = data.showapi_res_body
        var city = showapi_res_body.cityInfo.c3
        var temperature = showapi_res_body.now.temperature
        var weather = showapi_res_body.now.weather
        var wind_direction = showapi_res_body.now.wind_direction
        var wind_power = showapi_res_body.now.wind_power
        var weather_pic = showapi_res_body.now.weather_pic
        var t = "城市：" + city + "，温度：" + temperature + "，天气：" + weather + "，风向：" + wind_direction + "，风力：" + wind_power
        $("#weather").empty();
        $("#weather").html('<img src="'+weather_pic+'" width="94px" height="94px" data-toggle="tooltip" data-placement="right" title='+t+'>');
    })
}
