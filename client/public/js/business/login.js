function logout() {
    var f = new Facade();
    window.im_object.im_close();
    f.cookieModule.cleanAllCookie();
	location = "/login"
}