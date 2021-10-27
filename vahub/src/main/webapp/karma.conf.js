'use strict';

module.exports = function (config) {
    config.set({
        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',
        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine', '@angular/cli'],
        // web server port
        port: 9898,
        //------
        //Some timeouts so that chrome would wait for the app to compile and catch up
        captureTimeout: 210000,
        browserDisconnectTolerance: 3,
        browserDisconnectTimeout : 210000,
        browserNoActivityTimeout : 210000,
        //-------
        // enable / disable colors in the output (reporters and logs)
        colors: true,
        client:{
            clearContext: false, // leave Jasmine Spec Runner output visible in browser
            jasmine: {
                random: false
            }
        },
        files: [
            { pattern: 'node_modules/jquery/dist/jquery.min.js' },
            { pattern: './src/test.ts', watched: false },
            { pattern: 'node_modules/rxjs/**/*.js', included: false, watched: false }
        ],
        preprocessors: {
            './src/test.ts': ['@angular/cli']
        },
        mime: {
            'text/x-typescript': ['ts','tsx']
        },
        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_ERROR,
        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,
        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['ChromeHeadless', 'Chrome'],
        customLaunchers: {
            ChromeHeadless: {
                base: 'Chrome',
                flags: [
                    '--headless',
                    //'--disable-gpu',
                    // Without a remote debugging port, Google Chrome exits immediately.
                    '--remote-debugging-port=9222',
                ],
            }
        },

        plugins: [
            require('karma-jasmine'),
            require('karma-chrome-launcher'),
            // require('karma-selenium-launcher'),
            require('karma-coverage-istanbul-reporter'),
            require('karma-junit-reporter'),
            require('karma-mocha-reporter'),
            require('karma-sourcemap-loader'),
            require('@angular/cli/plugins/karma')
        ],
        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: true,
        // singleRun: false,
        reporters: ['progress', 'junit'],
        junitReporter: {
            outputDir: 'reports',
            outputFile: 'test-results.xml'
        },
        coverageIstanbulReporter: {
            reports: [ 'html', 'lcovonly' ],
            fixWebpackSourcePaths: true
        },
        failOnEmptyTestSuite: false
    });
};
