FROM ghcr.io/maplibre/martin:1.4.0
WORKDIR /app
COPY ./map.mbtiles /app/map.mbtiles
COPY ./martin.config.yml /app/martin.config.yml
CMD ["--config", "/app/martin.config.yml"]