import Ember from 'ember';
const { computed } = Ember;

export default Ember.Controller.extend({
    auth: computed(function(){
        let auth_temp = Ember.$.cookie('auth');
        if(auth_temp==1){
            return true;
        }
        return false;
    }),
    markets: computed(function(){
        let str = Ember.$.cookie('rq_data_markets');
        str = str.replace('[', '').replace(']', '').replace(/\"/g, "");
        let arr = [];
        if(str!=""){
            arr = str.split(',');
        }
        return arr;
    })
});
