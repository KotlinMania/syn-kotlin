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
