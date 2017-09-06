import Ember from 'ember';
const { computed } = Ember;

export default Ember.Controller.extend({
    markets: computed(function(){
        let str = Ember.$.cookie('sc_data_markets');
        str = str.replace('[', '').replace(']', '').replace(/\"/g, "");
        let arr = str.split(',');
        return arr;
    }),
    dates: computed(function(){
        let str = Ember.$.cookie('sc_data_dates');
        str = str.replace('[', '').replace(']', '').replace(/\"/g, "");
        let arr = str.split(',');
        return arr;
    })
});
