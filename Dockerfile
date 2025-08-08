# Custom Postgres image with both pgvector and pgml extensions
#
# pgvector is already provided by the base image.
# This Dockerfile compiles and installs the pgml extension.
#
# https://github.com/postgresml/postgresml

FROM pgvector/pgvector:pg17

RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        build-essential \
        python3 \
        python3-pip3 \
        pipx; \
    pipx install pgml-extension

