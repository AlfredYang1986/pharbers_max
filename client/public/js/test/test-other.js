(function($, w){
    // Array.prototype.distinct = function() {
    //     var res = [], hash = {};
    //     for(var i=0, elem; (elem = this[i]) != null; i++)  {
    //         if (!hash[elem])
    //         {
    //             res.push(elem);
    //             hash[elem] = true;
    //         }
    //     }
    //     return res;
    // };

    // Array.prototype.distinct = function(){
    //     var arr = [], obj = {}, i = 0, len = this.length, result;
    //     for( ; i < len; i++ ) {
    //         result = this[i];
    //         if( obj[result] !== result ){
    //             arr.push( result );
    //             obj[result] = result;
    //         }
    //     }
    //     return arr;
    // };


    // Array.prototype.distinct = function() {
    //     var ret = [];
    //     this.reduce(function(prev, next) {
    //             prev[next] = (prev[next] + 1) || 1;
    //             //console.log(next);
    //             if (prev[next] === 1) ret.push(next);
    //             return prev;
    //         },
    //         {});
    //     return ret;
    // };


    Array.prototype.distinct = function() {
        return this.filter(function(value, index, array) {
            return array.indexOf(value) === index;
        });
    };

    var array = ['fuck', 1, '1', 'a', 'a', 1];
    var array2 = [{"aa": "bb"}, {"aa": "bb"}, {"aa": "cc"}];
    w.console.info(array.distinct());
    w.console.info(array2.distinct());// 不支持深度判断
    w.console.info($.unique(array2));// 哎jQuery3.0过期了，并且不能深度判断
})(jQuery, window);



