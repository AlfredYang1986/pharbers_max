import Ember from 'ember';

const { computed } = Ember;

export default Ember.Controller.extend({

    accessToken: computed(function(){
        return getCookie('user_token');
    }),

    validated:Ember.computed.bool('accessToken'),

    isAdmin: computed(function(){

        let binaryAdmin = getCookie('is_administrator');
        if(binaryAdmin==1){
            return true;
        }
        return false;
    }),

    actions:{
        cleanCookie(){
            delCookie("user_token");
            delCookie("is_administrator");
        }
    }

});
