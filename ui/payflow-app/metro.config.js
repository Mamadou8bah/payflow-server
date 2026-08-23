const { getDefaultConfig } = require("expo/metro-config");

const config = getDefaultConfig(__dirname);

// pdf-lib's default ESM entry imports @pdf-lib/* sub-packages that Metro can miss.
config.resolver.resolveRequest = (context, moduleName, platform) => {
  if (moduleName === "pdf-lib") {
    return {
      filePath: require.resolve("pdf-lib/dist/pdf-lib.esm.min.js"),
      type: "sourceFile",
    };
  }
  return context.resolveRequest(context, moduleName, platform);
};

config.resolver.extraNodeModules = {
  ...config.resolver.extraNodeModules,
  "@pdf-lib/standard-fonts": require.resolve("@pdf-lib/standard-fonts"),
  "@pdf-lib/upng": require.resolve("@pdf-lib/upng"),
};

module.exports = config;
