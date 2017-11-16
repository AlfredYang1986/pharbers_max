var Web_Socket = {};
Web_Socket.config = {
    socketURL: "ws://192.168.100.19:9000/ws",
    register: JSON.stringify({"uid": $.cookie("uid")})
};