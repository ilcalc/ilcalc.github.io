module.exports = {
  purge: {
    content: ["./src/**/*.cljs"],
    defaultExtractor: (content) => content.match(/[\.#][\w-_]+/) || [],
  },
  theme: {
    screens: {
      sm: "640px",
    },
  },
  plugins: [require("@tailwindcss/forms")],
};
