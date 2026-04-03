# Deployment - Salón de Belleza

![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=flat-square)
![Production](https://img.shields.io/badge/Environment-Production%2FDev-brightgreen?style=flat-square)

**Última actualización:** Abril 2026

---

## 📋 Tabla de Contenidos

1. [Desarrollo Local](#desarrollo-local)
2. [Docker](#docker)
3. [Producción](#producción)
4. [Variables de Entorno](#variables-de-entorno)
5. [Monitoreo](#monitoreo)
6. [Troubleshooting](#troubleshooting)

---

## Desarrollo Local

### Requisitos

```bash
# Verificar versiones
java -version          # Debe ser 21+
docker --version       # Cualquier versión reciente
gradle --version       # Cualquier versión (incluida en proyecto)
git --version          # Para versionado
```

### Setup Inicial

```bash
# 1. Clonar repositorio
git clone https://github.com/sena/SalonDeBelleza.git
cd SalonDeBelleza

# 2. Crear archivo .env (copiar de .env.example si existe)
cp .env.example .env

# 3. Levantar dependencias (BD, cache)
docker-compose up -d

# 4. Verificar que PostgreSQL está listo
docker-compose logs -f postgres  # Esperar mensaje de ready

# 5. Compilar y ejecutar tests
./gradlew clean build

# 6. Ejecutar aplicación
./gradlew bootRun

# 7. Verificar
curl http://localhost:8080/swagger-ui.html
```

### Estructura de Carpetas

```
SalonDeBelleza/
├── src/main/java/       # Código fuente
├── src/main/resources/  # Propiedades, migraciones
├── src/test/            # Tests
├── .env                 # Variables locales (gitignore)
├── docker-compose.yml   # Servicios locales (BD, cache)
└── Dockerfile           # Imagen de producción
```

### Ejecutar en Desarrollo

```bash
# Terminal 1: BD
docker-compose up

# Terminal 2: Aplicación
./gradlew bootRun

# Terminal 3: Tests (opcional)
./gradlew test --continue
```

### Puertos

| Servicio | Puerto | URL |
|----------|--------|-----|
| Aplicación | 8080 | http://localhost:8080 |
| Swagger | 8080 | http://localhost:8080/swagger-ui.html |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/salon_de_belleza_db |
| Redis (opcional) | 6379 | redis://localhost:6379 |
| pgAdmin (opcional) | 5050 | http://localhost:5050 |

---

## Docker

### Build Local

```bash
# Construir imagen
docker build -t salon-belleza:local .

# Ejecutar contenedor (conectado a red local)
docker run -d \
  --name salon-belleza \
  -p 8080:8080 \
  --env-file .env \
  --network salon-network \
  salon-belleza:local

# Ver logs
docker logs -f salon-belleza

# Detener
docker stop salon-belleza
```

### Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar build
COPY build/libs/*.jar app.jar

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
      -Dspring.profiles.active=prod \
      || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:17-alpine
    container_name: salon_postgres
    environment:
      POSTGRES_DB: salon_de_belleza_db
      POSTGRES_USER: salon_admin
      POSTGRES_PASSWORD: salon_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U salon_admin"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: salon_redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

---

## Producción

### Checklist Pre-Deployment

- [ ] Todos los tests pasan (`./gradlew test`)
- [ ] Cobertura >= 80%
- [ ] Código sin warnings de compilación
- [ ] Migraciones de BD listas
- [ ] Variables de entorno configuradas
- [ ] Certificados SSL/TLS listos
- [ ] Backups configurados
- [ ] Monitoring/logging listo
- [ ] Plan de rollback documentado

### Deployment en AWS (Ejemplo)

#### 1. Crear Instancia EC2

```bash
# Instancia Ubuntu 22.04 LTS
# Security Group: permitir 80, 443, 5432 (si BD local)

# SSH a instancia
ssh -i key.pem ubuntu@instance-ip

# Actualizar
sudo apt update && sudo apt upgrade -y

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

#### 2. Preparar Aplicación

```bash
# Clonar repo
git clone https://github.com/sena/SalonDeBelleza.git
cd SalonDeBelleza

# Crear .env con valores de producción
nano .env
```

#### 3. Build y Deploy

```bash
# Build
./gradlew clean build

# Construir imagen
docker build -t salon-belleza:prod .

# Ejecutar contenedor
docker run -d \
  --name salon-belleza \
  -p 80:8080 \
  --restart always \
  --env-file .env \
  salon-belleza:prod

# Verificar
curl http://localhost/swagger-ui.html
```

#### 4. Setup Nginx (Reverse Proxy)

```bash
sudo apt install nginx

# Crear config
sudo nano /etc/nginx/sites-available/salon-belleza
```

```nginx
upstream salon_app {
    server localhost:8080;
}

server {
    listen 80;
    server_name api.salon-belleza.com;

    # Redirigir HTTP a HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.salon-belleza.com;

    ssl_certificate /etc/ssl/certs/cert.pem;
    ssl_certificate_key /etc/ssl/private/key.pem;

    # Headers de seguridad
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;

    location / {
        proxy_pass http://salon_app;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Habilitar
sudo ln -s /etc/nginx/sites-available/salon-belleza \
    /etc/nginx/sites-enabled/

# Validar config
sudo nginx -t

# Restart
sudo systemctl restart nginx
```

#### 5. SSL/TLS con Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx

sudo certbot certonly --standalone \
  -d api.salon-belleza.com \
  -m admin@salon-belleza.com

# Auto-renew
sudo systemctl enable certbot.timer
```

### Deployment en Cloud (GCP/Azure/Digital Ocean)

Similar a AWS, pasos generales:

1. Crear instancia/app
2. Clonar código
3. Configurar variables de entorno
4. Build con Docker
5. Deploy contenedor
6. Setup reverse proxy (Nginx/LoadBalancer)
7. Configure SSL/TLS
8. Setup monitoring

---

## Variables de Entorno

### Desarrollo

```properties
# application-dev.properties
spring.profiles.active=dev
spring.datasource.url=jdbc:postgresql://localhost:5432/salon_de_belleza_db
spring.datasource.username=salon_admin
spring.datasource.password=salon_password
logging.level.com.salondebelleza=DEBUG
```

### Producción

```bash
# .env.prod (no commitear)
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://db.prod.com:5432/salon_db
export SPRING_DATASOURCE_USERNAME=salon_prod_user
export SPRING_DATASOURCE_PASSWORD=<super-secure-password>
export SECURITY_JWT_SECRET_KEY=<256-bit-key-generado-aleatoriamente>
export SERVER_SSL_ENABLED=true
export SERVER_SSL_KEYSTORE_PATH=/app/keystore.jks
export SERVER_SSL_KEYSTORE_PASSWORD=<keystore-password>
export LOGGING_LEVEL_COM_SALONDEBELLEZA=INFO
export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics
```

### Cargar Variables en Contenedor

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar

# Cargar .env (si está disponible)
ARG ENV_FILE=.env.prod
COPY ${ENV_FILE} .env
RUN chmod 600 .env

ENTRYPOINT ["sh", "-c", "source .env && java -jar app.jar"]
```

---

## Monitoreo

### Logs

```bash
# Ver logs en vivo
docker logs -f salon-belleza

# Logs persisten en
/var/lib/docker/containers/<container-id>/<container-id>-json.log

# Rotar logs
docker inspect --format='{{.LogPath}}' salon-belleza
```

### Health Check

```bash
# Endpoint de salud
curl http://localhost:8080/actuator/health

# Respuesta
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

### Métricas (Micrometer)

```bash
# Métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# En Grafana:
# Data source: http://prometheus:9090
# Dashboard: JVM, Spring Boot, etc.
```

### Logs Agregados (ELK Stack)

```yaml
# docker-compose.yml - agregar servicios
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.13.0
    environment:
      - discovery.type=single-node
  
  kibana:
    image: docker.elastic.co/kibana/kibana:7.13.0
    ports:
      - "5601:5601"
```

Logback configuración:

```xml
<!-- logback-spring.xml -->
<appender name="ELASTIC" class="com.example.ElasticsearchAppender">
  <host>elasticsearch</host>
  <port>9200</port>
</appender>
```

---

## Troubleshooting

### Problema: Aplicación no inicia

```bash
# Ver logs detallados
docker logs salon-belleza

# Verificar BD está corriendo
docker-compose ps

# Si BD no está ready:
docker-compose restart postgres
sleep 10
docker-compose up -d
```

### Problema: BD no conecta

```bash
# Verificar conexión
docker-compose exec postgres psql -U salon_admin -d salon_de_belleza_db

# Si falla: revisar variables en docker-compose.yml
docker-compose down
docker volume rm SalonDeBelleza_postgres_data
docker-compose up -d
```

### Problema: Migraciones Flyway fallan

```bash
# Ver estado de migraciones
SELECT * FROM flyway_schema_history;

# Si hay error, puede ser:
# 1. Validación de suma de verificación falló
# 2. Archivo de migración corrupido
# 3. Base de datos en estado inconsistente

# Limpiar Flyway (CUIDADO - borra todo)
DELETE FROM flyway_schema_history;
TRUNCATE TABLE clientes CASCADE;
```

### Problema: Conexión timeout

```bash
# Aumentar timeout en application.properties
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=20

# Reiniciar
./gradlew bootRun
```

### Problema: Memory leak / Aplicación lenta

```bash
# Ver memory heap
curl http://localhost:8080/actuator/heapdump > heap.hprof

# Analizar con JProfiler o YourKit

# Aumentar memory en Docker
docker run -e JAVA_OPTS="-Xmx2g -Xms1g" ...
```

---

## Rollback

### Rollback con Docker

```bash
# Si deployment fallió, revertir a imagen anterior
docker stop salon-belleza
docker rm salon-belleza

# Re-ejecutar con imagen anterior
docker run -d \
  --name salon-belleza \
  -p 8080:8080 \
  salon-belleza:v0.0.0  # Versión anterior
```

### Rollback de BD

```bash
# Si migración Flyway falla, marcar como exitosa (si es seguro)
UPDATE flyway_schema_history 
SET success = false 
WHERE version = '7';

# Revertir cambios manuales
docker-compose exec postgres psql -U salon_admin -d salon_de_belleza_db < rollback.sql

# O usar backup
psql -U salon_admin -d salon_de_belleza_db < backup_anterior.sql
```

---

## Checklist de Deployment

### Pre-Deploy
- [ ] Código compilable (`./gradlew clean build`)
- [ ] Tests pasan (`./gradlew test`)
- [ ] Migraciones validadas
- [ ] Variables de entorno configuradas
- [ ] SSL/TLS listo
- [ ] Backups listos

### Deploy
- [ ] Construir imagen Docker
- [ ] Ejecutar contenedor
- [ ] Verificar health checks
- [ ] Verificar logs
- [ ] Verificar endpoints principales

### Post-Deploy
- [ ] Monitoreo activo
- [ ] Alertas configuradas
- [ ] Logs siendo agregados
- [ ] Métricas siendo recolectadas
- [ ] Plan de rollback documentado

---

## Recursos

- 🔗 [Docker Docs](https://docs.docker.com/)
- 🔗 [Docker Compose](https://docs.docker.com/compose/)
- 🔗 [Spring Boot Deployment](https://spring.io/guides/gs/deploying-spring-boot-app-to-azure/)
- 🔗 [Let's Encrypt](https://letsencrypt.org/)
- 🔗 [Nginx Docs](https://nginx.org/en/docs/)

---

**Última actualización:** Abril 2026 | **Versión:** 0.0.1-SNAPSHOT


