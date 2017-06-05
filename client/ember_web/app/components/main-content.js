import Ember from 'ember';

export default Ember.Component.extend({
    liTab:[{
        id:0,
        title:"首页",
        to:"index",
        icon:"fa fa-th-large"
    },{
        id:1,
        title:"1.文件上传",
        to:"filesupload",
        icon:"fa fa-cloud-upload"
    },{
        id:2,
        title:"2.样本检查",
        to:"samplecheck",
        icon:"fa fa-pie-chart"
    },{
        id:3,
        title:"3.样本报告",
        to:"samplereport",
        icon:"fa fa-file-text"
    },{
        id:4,
        title:"4.结果检查",
        to:"modeloperation",
        icon:"fa fa-bar-chart-o"
    },{
        id:5,
        title:"5.结果查询",
        to:"resultquery",
        icon:"fa fa-table"
    }
    ],
    actions:{
        cleanCookie(){
            Ember.$.cookie("user_token","");
            Ember.$.cookie("is_administrator","");
            // cleanAllCookie();//未能成功清除所有cookie
        }
    }
});
