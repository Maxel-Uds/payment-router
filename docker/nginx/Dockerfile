FROM openresty/openresty:1.21.4.1-1-bullseye

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

RUN apt-get update && apt-get install -y luarocks

RUN luarocks install lua-resty-http
RUN luarocks install lua-resty-redis
RUN luarocks install lua-cjson
