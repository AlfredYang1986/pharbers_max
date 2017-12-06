var Web_Socket = {};
Web_Socket.config = {
    socketURL: "ws://59.110.31.106:80/ws",
    register: JSON.stringify({"uid": $.cookie("uid")})
};