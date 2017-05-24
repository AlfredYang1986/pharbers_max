import Ember from 'ember';

export default Ember.Component.extend({
    liTab:[{
        id:0,
        title:"首页",
        to:"index"
    },{
        id:1,
        title:"1.文件上传",
        to:"filesupload"
    },{
        id:2,
        title:"2.样本检查",
        to:"samplecheck"
    },{
        id:3,
        title:"3.样本报告",
        to:"samplereport"
    },{
        id:4,
        title:"4.结果检查",
        to:"modeloperation"
    },{
        id:5,
        title:"5.结果查询",
        to:"resultquery"
    }
    ]
});
