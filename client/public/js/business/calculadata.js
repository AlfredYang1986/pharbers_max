/**
 * Created by liwei on 2017/7/5.
 */
$(function(){

    stepBar.init("stepBar", { step : 1, change : true, animation : true });

    $("#gycx_div").hide()
    $("#sc_div").hide()
    $("#calc_div").hide()
    $("#rc_div").hide()

    $("#btn_cpa_next").click(function () {
        $("#cpa_div").hide()
        stepBar.init("stepBar", { step : 2, change : true, animation : true });
        $("#gycx_div").show()
    })

    $("#btn_gycx_pr").click(function () {
        $("#cpa_div").show()
        stepBar.init("stepBar", { step : 1, change : true, animation : true });
        $("#gycx_div").hide()
    })

    $("#btn_gycx_next").click(function () {
        $("#gycx_div").hide()
        stepBar.init("stepBar", { step : 3, change : true, animation : true });
        $("#sc_div").show()
    })

    $("#btn_sc_pr").click(function () {
        $("#gycx_div").show()
        stepBar.init("stepBar", { step : 2, change : true, animation : true });
        $("#sc_div").hide()
    })

    $("#btn_sc_next").click(function () {
        $("#calc_div").show()
        stepBar.init("stepBar", { step : 4, change : true, animation : true });
        $("#sc_div").hide()
    })

    $("#btn_calc_pr").click(function () {
        $("#sc_div").show()
        stepBar.init("stepBar", { step : 3, change : true, animation : true });
        $("#calc_div").hide()
    })

    $("#btn_calc_next").click(function () {
        $("#rc_div").show()
        stepBar.init("stepBar", { step : 5, change : true, animation : true });
        $("#calc_div").hide()
    })

    $("#btn_rc_pr").click(function () {
        $("#calc_div").show()
        stepBar.init("stepBar", { step : 4, change : true, animation : true });
        $("#rc_div").hide()
    })

    $("#btn_rc_next").click(function () {
        alert("计算完成！");
//                $("#rc_div").show()
//                stepBar.init("stepBar", { step : 5, change : true, animation : true });
//                $("#calc_div").hide()
    })
});