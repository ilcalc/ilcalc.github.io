module.exports = {
  // purge: [
  //   './src/**/*.cljs'
  // ],
  // purge: {
  //   content: ["./src/**/*.cljs"],
  //   // defaultExtractor: (content) => content.match(/[\.#][\w-_]+/) || [],
  // },
  plugins: [
    require("postcss-import"),
    require("tailwindcss"),
    require("autoprefixer"),
  ],
};
