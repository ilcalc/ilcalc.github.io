release:
	yarn shadow-cljs release app --config-merge '{:output-dir "public/js"}'
	NODE_ENV=production yarn postcss style.css -o public/css/style.css
