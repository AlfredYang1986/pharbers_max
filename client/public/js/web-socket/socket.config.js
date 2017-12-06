var Web_Socket = {};
Web_Socket.config = {
    // socketURL: "ws://192.168.100.18:9000/ws",
    socketURL: "ws://192.168.100.174:9000/ws",
    // socketURL: "ws://127.0.0.1:9000/ws",
    register: JSON.stringify({"uid": $.cookie("uid")})
};