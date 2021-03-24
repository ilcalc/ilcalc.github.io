js:
	yarn shadow-cljs watch app test

css:
	yarn postcss style.css -w -o docs/dev/css/style.css

release:
	yarn shadow-cljs release app --config-merge '{:output-dir "docs/js"}'
	NODE_ENV=production yarn postcss style.css -o docs/css/style.css
	git add .
	git commit -am "Release"
