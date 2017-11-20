function logout() {
    var f = new Facade();
    f.cookieModule.cleanAllCookie();
	location = "/login"
}