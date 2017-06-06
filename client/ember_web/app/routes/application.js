import Ember from 'ember';

const Data = Ember.Object.extend({
});

Data.reopen({
   ms:null,
   ds:null
});


export default Ember.Route.extend({
    model(){
        Ember.$.ajax({
            type: "POST",
            url: "/loadPageData",
            dataType: "json",
            data: JSON.stringify({
                "dttype": "fileupload"
            }),
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                Ember.$.cookie('fu_data_markets',JSON.stringify(data.result.result.result.markets));
            }
        });
        Ember.$.ajax({
            type: "POST",
            url: "/loadPageData",
            dataType: "json",
            data: JSON.stringify({
                "dttype": "samplecheck"
            }),
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                Ember.$.cookie('sc_data_markets',JSON.stringify(data.result.result.result.markets));
                Ember.$.cookie('sc_data_dates',JSON.stringify(data.result.result.result.dates));
            }
        });
        Ember.$.ajax({
            type: "POST",
            url: "/loadPageData",
            dataType: "json",
            data: JSON.stringify({
                "dttype": "resultcheck"
            }),
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                Ember.$.cookie('rc_data_markets',JSON.stringify(data.result.result.result.markets));
                Ember.$.cookie('rc_data_dates',JSON.stringify(data.result.result.result.dates));
            }
        });
        Ember.$.ajax({
            type: "POST",
            url: "/loadPageData",
            dataType: "json",
            data: JSON.stringify({
                "dttype": "resultquery"
            }),
            contentType: 'application/json,charset=utf-8',
            success: function (data) {
                Ember.$.cookie('rq_data_markets',JSON.stringify(data.result.result.result.markets));
            }
        });
    }
});
