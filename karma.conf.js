module.exports = function (config) {
  config.set({
    browsers: ["ChromeHeadless"],
    basePath: "docs/test",
    files: ["ci.js"],
    frameworks: ["cljs-test"],
    plugins: ["karma-cljs-test", "karma-chrome-launcher"],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ["shadow.test.karma.init"],
      singleRun: true,
    },
  });
};
