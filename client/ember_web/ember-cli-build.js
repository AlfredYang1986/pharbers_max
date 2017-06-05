/* eslint-env node */
const EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    storeConfigInMeta: false,
    SRI: {
      enabled: false
    },
    fingerprint: {
      enabled: false
    },
    outputPaths: {
      app: {
        css: {
          'app': '/assets/max-ember-web.css'
        },
        js: '/assets/max-ember-web.js'

      },
    }
  });
  app.import(app.bowerDirectory + '/bootstrap/dist/css/bootstrap.css');
  // app.import(app.bowerDirectory + '/assets/css/bootstrap-select.css');
  app.import(app.bowerDirectory + '/bootstrap/dist/js/bootstrap.js');
  // app.import(app.bowerDirectory + '/bootstrap/dist/js/bootstrap.min.js');
  app.import(app.bowerDirectory + '/jquery/dist/jquery.cookie.js');

  app.import(app.bowerDirectory + '/assets/js/web-im/load-im.js');
  app.import(app.bowerDirectory + '/assets/js/web-im/strophe-1.2.8.js');
  app.import(app.bowerDirectory + '/assets/js/web-im/webim.config.js');
  app.import(app.bowerDirectory + '/assets/js/web-im/websdk-1.4.10.js');

  // app.import(app.bowerDirectory + '/assets/js/bootstrap-select.js');
  //   app.import(app.bowerDirectory + '/assets/js/chosen.jquery.js');

  // app.import(app.bowerDirectory + '/assets/dist/css/bootstrap-select.min.css');
  // app.import(app.bowerDirectory + '/assets/dist/js/defaults-zh_CN.js');
  // app.import(app.bowerDirectory + '/assets/dist/js/i18n/defaults-zh_CN.min.js');
  // app.import(app.bowerDirectory + '/assets/dist/js/bootstrap-select.min.js');
  app.import(app.bowerDirectory + '/assets/js/business/login.js');
  // app.import(app.bowerDirectory + '/assets/js/business/resultquery.js');


  app.import(app.bowerDirectory + '/bootstrap/dist/fonts/glyphicons-halflings-regular.woff', {
    destDir: 'fonts'
  });

  return app.toTree();
};
