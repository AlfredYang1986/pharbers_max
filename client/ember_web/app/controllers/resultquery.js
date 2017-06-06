import Ember from 'ember';
const { computed } = Ember;

export default Ember.Controller.extend({
    markets: computed(function(){
        let str = Ember.$.cookie('rq_data_markets');
        str = str.replace('[', '').replace(']', '').replace(/\"/g, "");
        let arr = str.split(',');
        return arr;
    })
});
