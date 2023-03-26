# Patient Servis - Servis za Pacijente

## O servisu:

Servis za manipulaciju pacijentima

### Pre pokretanja servisa, skinuti i instalirati docker i pokrenuti sledeću komandu:

```bash
docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d postgres
```

```
docker network create local_network
docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d --network local_network postgres
docker run -d -p 8082:8082 --network local_network patient
```

