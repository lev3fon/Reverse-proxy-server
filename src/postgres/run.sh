docker run -itd --rm \
    -p 8765:5432 \
    --name proxy-pg \
    -e POSTGRES_PASSWORD=12345 \
    -e PGDATA=/var/lib/postgresql/data/pgdata \
    -v $PWD/../proxy-pgdata/pgdata:/var/lib/postgresql/data \
    postgres:11