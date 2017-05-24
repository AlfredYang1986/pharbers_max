'use strict';

define('em/tests/app.lint-test', ['exports'], function (exports) {
  'use strict';

  QUnit.module('ESLint | app');

  QUnit.test('app.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'app.js should pass ESLint\n\n');
  });

  QUnit.test('components/file-upload.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'components/file-upload.js should pass ESLint\n\n');
  });

  QUnit.test('resolver.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'resolver.js should pass ESLint\n\n');
  });

  QUnit.test('router.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'router.js should pass ESLint\n\n');
  });

  QUnit.test('routes/application.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/application.js should pass ESLint\n\n');
  });

  QUnit.test('routes/filesupload.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/filesupload.js should pass ESLint\n\n');
  });

  QUnit.test('routes/home.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/home.js should pass ESLint\n\n');
  });

  QUnit.test('routes/index.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/index.js should pass ESLint\n\n');
  });

  QUnit.test('routes/login.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/login.js should pass ESLint\n\n');
  });

  QUnit.test('routes/modeloperation.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/modeloperation.js should pass ESLint\n\n');
  });

  QUnit.test('routes/resultquery.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/resultquery.js should pass ESLint\n\n');
  });

  QUnit.test('routes/samplecheck.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/samplecheck.js should pass ESLint\n\n');
  });

  QUnit.test('routes/samplereport.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'routes/samplereport.js should pass ESLint\n\n');
  });
});
define('em/tests/helpers/destroy-app', ['exports', 'ember'], function (exports, _ember) {
  exports['default'] = destroyApp;

  function destroyApp(application) {
    _ember['default'].run(application, 'destroy');
  }
});
define('em/tests/helpers/module-for-acceptance', ['exports', 'qunit', 'ember', 'em/tests/helpers/start-app', 'em/tests/helpers/destroy-app'], function (exports, _qunit, _ember, _emTestsHelpersStartApp, _emTestsHelpersDestroyApp) {
  var Promise = _ember['default'].RSVP.Promise;

  exports['default'] = function (name) {
    var options = arguments.length <= 1 || arguments[1] === undefined ? {} : arguments[1];

    (0, _qunit.module)(name, {
      beforeEach: function beforeEach() {
        this.application = (0, _emTestsHelpersStartApp['default'])();

        if (options.beforeEach) {
          return options.beforeEach.apply(this, arguments);
        }
      },

      afterEach: function afterEach() {
        var _this = this;

        var afterEach = options.afterEach && options.afterEach.apply(this, arguments);
        return Promise.resolve(afterEach).then(function () {
          return (0, _emTestsHelpersDestroyApp['default'])(_this.application);
        });
      }
    });
  };
});
define('em/tests/helpers/resolver', ['exports', 'em/resolver', 'em/config/environment'], function (exports, _emResolver, _emConfigEnvironment) {

  var resolver = _emResolver['default'].create();

  resolver.namespace = {
    modulePrefix: _emConfigEnvironment['default'].modulePrefix,
    podModulePrefix: _emConfigEnvironment['default'].podModulePrefix
  };

  exports['default'] = resolver;
});
define('em/tests/helpers/start-app', ['exports', 'ember', 'em/app', 'em/config/environment'], function (exports, _ember, _emApp, _emConfigEnvironment) {
  exports['default'] = startApp;

  function startApp(attrs) {
    var attributes = _ember['default'].merge({}, _emConfigEnvironment['default'].APP);
    attributes = _ember['default'].merge(attributes, attrs); // use defaults, but you can override;

    return _ember['default'].run(function () {
      var application = _emApp['default'].create(attributes);
      application.setupForTesting();
      application.injectTestHelpers();
      return application;
    });
  }
});
define('em/tests/test-helper', ['exports', 'em/tests/helpers/resolver', 'ember-qunit'], function (exports, _emTestsHelpersResolver, _emberQunit) {

  (0, _emberQunit.setResolver)(_emTestsHelpersResolver['default']);
});
define('em/tests/tests.lint-test', ['exports'], function (exports) {
  'use strict';

  QUnit.module('ESLint | tests');

  QUnit.test('helpers/destroy-app.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'helpers/destroy-app.js should pass ESLint\n\n');
  });

  QUnit.test('helpers/module-for-acceptance.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'helpers/module-for-acceptance.js should pass ESLint\n\n');
  });

  QUnit.test('helpers/resolver.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'helpers/resolver.js should pass ESLint\n\n');
  });

  QUnit.test('helpers/start-app.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'helpers/start-app.js should pass ESLint\n\n');
  });

  QUnit.test('test-helper.js', function (assert) {
    assert.expect(1);
    assert.ok(true, 'test-helper.js should pass ESLint\n\n');
  });
});
require('em/tests/test-helper');
EmberENV.TESTS_FILE_LOADED = true;
//# sourceMappingURL=tests.map
