/**
 * Created by yym on 11/10/17.
 */
(function ($) {
    $("#stepBack").bind("click",function () {
        back_calcul_model();
    });
    $("#back_check_again").click(function () {
        back_check_again();
    });

    var back_calcul_model = function () {
        $("#backCheck").modal("show");
    };
    var back_check_again = function () {
        $("#backCheck").modal("hide");
        window.location.href = "/calcul/home";
    }

}(jQuery));