import Ember from 'ember';
const { computed } = Ember;

export default Ember.Controller.extend({
    isAdmin: computed(function(){
        let binaryAdmin = Ember.$.cookie('is_administrator');
        if(binaryAdmin==1){
            return true;
        }
        return false;
    })
});
