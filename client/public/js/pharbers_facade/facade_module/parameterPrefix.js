var ParameterPrefix = function() {
    this.busi = {};
    this.cons = {};
}

// ParameterPrefix.prototype.condition = function(cond) {
//     return {"condition": cond }
// };

ParameterPrefix.prototype.conditions = function(cond) {
    return this.cons = {"condition": cond}
};

ParameterPrefix.prototype.business = function(key, bus) {
    var o = {};
    o[key] = bus;
    return this.busi = o
};

ParameterPrefix.prototype.merges = function() {return $.extend(this.cons, this.busi)};


// (function($) {
//     this.condition = function(cond) {
//         return {"condition": cond };
//     };
//     this.conditions = function(cond) {
//         this.cons = {"condition" : cond}
//     };
//     this.business = function(key, bus) {
//         var o = {}
//         o[key] = bus
//         this.busi = o
//     };
//     this.merges = function() {
//         return $.extend(this.cons, this.busi)
//     };
// }(jQuery)).call(ParameterPrefix.prototype);
