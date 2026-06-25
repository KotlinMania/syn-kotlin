const client = config.client || {};

config.set({
  browserNoActivityTimeout: 1200000,
  browserDisconnectTimeout: 1200000,
  browserDisconnectTolerance: 1,
  client: {
    ...client,
    mocha: {
      ...(client.mocha || {}),
      timeout: 1200000
    }
  }
});

const kotlinReporterPlugin = require('kotlin-web-helpers/dist/karma-kotlin-reporter.js');
const KotlinReporter = kotlinReporterPlugin['reporter:karma-kotlin-reporter'][1];

function TolerantKotlinReporter(baseReporterDecorator, config, emitter) {
  KotlinReporter.call(this, baseReporterDecorator, config, emitter);

  const specSuccess = this.specSuccess;
  const specFailure = this.specFailure;

  const ensureConsoleResult = (browser, result) => {
    this.checkBrowserResult(browser);
    const browserResult = this.browserResults[browser.id];
    const key = `${result.suite.join(".")}.${result.description}`;
    browserResult.consoleResultCollector[key] =
      browserResult.consoleResultCollector[key] || [];
  };

  this.specSuccess = function(browser, result) {
    ensureConsoleResult(browser, result);
    specSuccess.call(this, browser, result);
  };

  this.specFailure = function(browser, result) {
    ensureConsoleResult(browser, result);
    specFailure.call(this, browser, result);
  };
}

TolerantKotlinReporter.$inject = KotlinReporter.$inject;

config.plugins = config.plugins || [];
config.plugins.push({
  'reporter:tolerant-kotlin-reporter': ['type', TolerantKotlinReporter]
});
config.set({
  reporters: ['tolerant-kotlin-reporter']
});
