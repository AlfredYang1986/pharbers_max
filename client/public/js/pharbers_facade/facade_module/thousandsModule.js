function Thousands() {}

(function() {
    // TODO: 逻辑有问题，未增加符号与小数点的处理
    // this.formatNum = function(input) {
    //     var num = (input || 0).toString(), result = '';
    //     while (num.length > 3) {
    //         result = ',' + num.slice(-3) + result;
    //         num = num.slice(0, num.length - 3);
    //     }
    //     if (num) { result = num + result; }
    //     return result;
    // };


    //TODO：~瞄~的IE9以下不支持
    this.formatNum = function (input) {
        if (isNaN(input)) {
            // throw new TypeError("num is not a number");
            console.warn("input is not a number");
            return input;
        } else {
            var groups = (/([\-\+]?)(\d*)(\.\d+)?/g).exec("" + input),
                mask = groups[1],            //符号位
                integers = (groups[2] || "").split(""), //整数部分
                decimal = groups[3] || "",       //小数部分
                remain = integers.length % 3;

            var temp = integers.reduce(function(previousValue, currentValue, index) {
                if (index + 1 === remain || (index + 1 - remain) % 3 === 0) {
                    return previousValue + currentValue + ",";
                } else {
                    return previousValue + currentValue;
                }
            }, "").replace(/\,$/g, "");
            return mask + temp + decimal;
        }
    }
}).call(Thousands.prototype);
