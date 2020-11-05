# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME io.vertx.starter.MainVerticle

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 5432
