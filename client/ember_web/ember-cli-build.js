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
  app.import(app.bowerDirectory + '/bootstrap/dist/js/bootstrap.js');

  app.import(app.bowerDirectory + '/bootstrap/dist/fonts/glyphicons-halflings-regular.woff', {
    destDir: 'fonts'
  });

  return app.toTree();
};
