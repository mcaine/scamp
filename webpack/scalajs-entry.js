if (process.env.NODE_ENV === "production") {
    const opt = require("./scamp-opt.js");
    opt.main();
    module.exports = opt;
} else {
    var exports = window;
    exports.require = require("./scamp-fastopt-entrypoint.js").require;
    window.global = window;

    const fastOpt = require("./scamp-fastopt.js");
    fastOpt.main()
    module.exports = fastOpt;

    if (module.hot) {
        module.hot.accept();
    }
}
