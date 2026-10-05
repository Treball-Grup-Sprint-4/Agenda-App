# Agenda App

Aplicación de gestión de agenda desarrollada en Java como proyecto grupal.

La aplicación permitirá gestionar tareas, notas y eventos desde consola, aplicando una arquitectura organizada por funcionalidades y diferentes patrones de diseño.

## Tecnologías utilizadas

- Java 25
- Maven
- MySQL 8
- JDBC
- Docker
- Docker Compose
- JUnit 6
- Git
- GitHub

## Requisitos previos

Para ejecutar el proyecto es necesario disponer de:

- Java 25
- Maven
- Docker y Docker Compose
- Git

## Configuración

El proyecto utiliza variables de entorno para configurar la conexión con MySQL.

Crear el archivo `.env` a partir del archivo de ejemplo:

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

### Terminal Mac
```terminal
cp .env.example .env
```

## Inicializar la base de datos

Una vez levantados los contenedores con Docker, es necesario ejecutar el script `agenda-app-db.sql` para crear las tablas de la aplicación dentro de MySQL.

Los siguientes comandos funcionan tanto en Windows PowerShell como en Terminal de macOS.

Desde la raíz del proyecto, copiar el archivo SQL al contenedor de MySQL:

```bash
docker cp agenda-app-db.sql mysql-agenda-app-container:/tmp/agenda-app-db.sql
```

Ejecutar el script dentro del contenedor:

```bash
docker exec mysql-agenda-app-container sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < /tmp/agenda-app-db.sql'
```

Para comprobar que las tablas se han creado correctamente:

```bash
docker exec mysql-agenda-app-container sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "USE \`agenda-app-database\`; SHOW TABLES;"'
```

El resultado debe mostrar las siguientes tablas:

```text
event
note
task
```

> [!NOTE]
> MySQL puede mostrar el aviso `Using a password on the command line interface can be insecure.` al ejecutar estos comandos. Es únicamente un warning y no impide la ejecución del script.

### IntelliJ IDEA

- Abrir el proyecto como proyecto Maven utilizando `pom.xml`.
- Usar JDK 25 y Language Level 25.
- Si Maven no se detecta automáticamente, hacer clic derecho sobre `pom.xml` → `Add as Maven Project`.

#### Ejecutar la aplicación

Para ejecutar `Main` desde IntelliJ mientras MySQL se ejecuta en Docker, configurar estas variables de entorno en la Run Configuration de `Main`:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=agenda-app-database
DB_USER=root
DB_PASSWORD=<password definido en .env>
```

El archivo `.env` es utilizado por Docker Compose, pero IntelliJ no carga automáticamente estas variables al ejecutar la aplicación.

#### Ejecutar la aplicación con Docker

La aplicación también se puede ejecutar completamente desde Docker, sin necesidad de iniciar `Main` desde IntelliJ.

Primero, levantar la base de datos:

```bash
docker compose up -d --build agenda-app-database
```

Después, ejecutar la aplicación Java de forma interactiva:

```bash
docker compose run --rm -it my-java-app
```

La opción `-it` permite utilizar el menú de consola mediante `Scanner`.

La opción `--rm` elimina automáticamente el contenedor de la aplicación Java cuando se cierra el programa.

La base de datos permanece activa en su propio contenedor y los datos se conservan mediante el volumen configurado en Docker Compose.

#### Ejecutar tests con JUnit

Los tests que acceden a MySQL también necesitan estas variables de entorno.

En IntelliJ:

`Run → Edit Configurations → JUnit`

Añadir en `Environment variables`:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=agenda-app-database
DB_USER=root
DB_PASSWORD=<password definido en .env>
```

Añadir cada variable correctamente en la configuración de IntelliJ y comprobar que los tests utilizan JDK 25 y las dependencias Maven del proyecto.

Los tests unitarios que no acceden a la base de datos no necesitan esta configuración.

## UML
![Agenda-App-UML.svg](docs/Agenda-App-UML.svg)