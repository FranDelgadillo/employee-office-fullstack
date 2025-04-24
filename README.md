Para construir y levantar los servicios
Desde el directorio employee-office-api, ejecutar:

bash
docker-compose up --build

Este comando construirá las imágenes del backend y frontend, y levantará los siguientes servicios:

- PostgreSQL: Base de datos para la aplicación.
- employee-office-api: Backend de la aplicación.
- frontend: Frontend de la aplicación.

Acceder a la aplicación

Una vez que los servicios estén en funcionamiento:

- Frontend: http://localhost:3000/register
- Backend API: http://localhost:8080/webjars/swagger-ui/index.html#/

🐳 Detalles del docker-compose.yaml

El archivo docker-compose.yaml se encuentra en el directorio employee-office-api y define los siguientes servicios:

- postgres: Utiliza la imagen oficial de PostgreSQL 15.5 y configura la base de datos con las variables de entorno proporcionadas.
- employee-office-api: Construye la imagen del backend desde el Dockerfile en el directorio actual y expone el puerto 8080.
- frontend: Construye la imagen del frontend desde el Dockerfile ubicado en ../employee-office-frontend y expone el puerto 3000. Además, establece la variable de entorno REACT_APP_API_URL para que el frontend pueda comunicarse con el backend.

📄 Consideraciones adicionales

Persistencia de datos: El servicio de PostgreSQL utiliza un volumen llamado postgres_data para persistir los datos de la base de datos.

Red interna: Todos los servicios están conectados a la red por defecto de Docker Compose, lo que permite la comunicación entre ellos mediante los nombres de servicio definidos.

Variables de entorno: Se recomienda no versionar archivos que contengan información sensible, como .env. Asegúrate de incluir estos archivos en tu .gitignore.

📌 Notas

Asegúrate de tener Docker y Docker Compose instalados en tu sistema antes de ejecutar los comandos mencionados.

Si realizas cambios en el código fuente del frontend o backend, deberás reconstruir las imágenes correspondientes para que los cambios se reflejen en los contenedores.
