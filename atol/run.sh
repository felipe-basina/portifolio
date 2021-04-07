############################################################
######### Run this script with . ./run.sh command ##########
############################################################

# Running docker-compose
docker-compose down
docker-compose up -d --remove-orphans

# Setting environment variables
export REDIS_PASSWORD=Acesso01!
export ENV=dev
export DATABASE_URL=postgresql://postgres@localhost:5432/atol_dev

echo 'Waiting containers to be up running...'
sleep 10s

# Running scripts into database
echo 'Running migrations'
cat ./migrations/*.sql | docker exec -i atol_thedb_1 psql -U postgres -d atol_dev

lein run