import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');
  this.route('index', { path: '/' });
  this.route('index', { path: '/index' });
  this.route('filesupload');
  this.route('samplecheck');
  this.route('samplereport');
  this.route('modeloperation');
  this.route('resultquery');
  this.route('home');
});

export default Router;
