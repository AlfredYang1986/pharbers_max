import Ember from 'ember';

const { computed } = Ember;

export default Ember.Controller.extend({

    accessToken: computed(function(){
        return Ember.$.cookie('user_token');
    }),

    validated:Ember.computed.bool('accessToken'),

    isAdmin: computed(function(){

        let binaryAdmin = Ember.$.cookie('is_administrator');
        if(binaryAdmin==1){
            return true;
        }
        return false;
    }),

    actions:{
        cleanCookie(){
            $.cookie("user_token","");
            $.cookie("is_administrator","");
            // cleanAllCookie();//未能成功清除所有cookie
        }
    }

});
