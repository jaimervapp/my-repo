# E-commerce API

Esta es una API REST para un servicio de e-commerce, diseñada para gestionar una cesta de compras y la carga de inventarios desde un archivo CSV. El objetivo principal de este proyecto es proporcionar una solución eficiente, escalable y segura para la gestión de artículos y su cálculo con IVA.

## Tabla de Contenidos
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Uso de la API](#uso-de-la-api)
  - [Cálculo de Cesta de Compras](#cálculo-de-cesta-de-compras)
  - [Importación de Artículos desde CSV](#importación-de-artículos-desde-csv)
- [Escalabilidad](#escalabilidad)
- [Decisiones Arquitectónicas](#decisiones-arquitectónicas)
- [Seguridad](#seguridad)


## Requisitos
- Java 17
- Maven 3.8+
- Git
- Base de datos H2 (embebida)
- Cuenta de GitHub con token de acceso personal
- [Postman](https://www.postman.com/) para pruebas de API

## Instalación
1. Clonar el repositorio:
```bash
  git clone https://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@github.com/jaimervapp/ecommerce-api.git
  cd my-repo
```
Si hay problemas con el token, descargar el repo en zip en local, descomprimir y abrir terminal en esa carpeta. 

2. Compilar el proyecto con Maven:
```bash
mvn clean install
```
## Configuración
1. ### Base de Datos H2:

- La base de datos H2 está configurada para ejecutar en memoria y es adecuada para pruebas y desarrollo.
- Las propiedades de configuración de la base de datos se encuentran en src/main/resources/application.properties.

2. ### Token de Acceso Personal de GitHub:

Ingresa tu token la primera vez que realices un git push para que Git lo almacene.

## Ejecución

1. Iniciar la aplicación:
```bash
mvn spring-boot:run
```
2. La API estará disponible en http://localhost:8080.

## Uso de la API
### Cálculo de Cesta de Compras
- **Endpoint**: POST /cart/calculate?ivaType={tipo}
- **Descripción**: Calcula el total de una lista de artículos y aplica el tipo de IVA especificado (normal, reducido, superreducido, o un porcentaje personalizado).
- **Ejemplo con Postman**:
1. Abre Postman y crea una nueva solicitud POST.
2. Configura la URL de la solicitud:

```bash
    http://localhost:8080/cart/calculate?ivaType=normal
```

3. En la pestaña Body, selecciona la opción raw y elige JSON.
4. Introduce el siguiente JSON en el cuerpo de la solicitud:
```json
[
  { "id": 1, "name": "Item A", "value": 100.0 },
  { "id": 2, "name": "Item B", "value": 50.0 }
]
```
5. Haz clic en Send.
6. La respuesta debería ser algo similar a:
```json
{
  "totalValue": 150.0,
  "iva": 31.5,
  "totalWithIva": 181.5
}
```
### Importación de Artículos desde CSV
- **Endpoint**: POST /items/import
- **Descripción**: Permite subir un archivo CSV con artículos para ser procesados en lotes, calculando el valor total.
- **Ejemplo con Postman**:
1. Abre Postman y crea una nueva solicitud POST.
2. Configura la URL de la solicitud:
```bash
http://localhost:8080/items/import
```
3. En la pestaña Body, selecciona la opción form-data.
4. Añade una clave llamada file y en el campo de valor, selecciona el archivo CSV desde tu sistema.
5. Haz clic en Send.
6. La respuesta debería ser algo similar a:
```json
{
  "message": "Import successful: 200 items imported. Total value: 5000.0"
}
```
## Escalabilidad
La API está diseñada para ser escalable mediante las siguientes prácticas:

1. ### Lectura por Lotes de Archivos CSV:

Los archivos CSV deberían ser leídos en fragmentos para evitar cargar grandes volúmenes de datos en memoria.
Esto permite manejar archivos de gran tamaño de manera eficiente, evitando problemas de memoria y tiempos de respuesta largos.

2. ### Uso de Atomic para Concurrencia:

El uso de AtomicInteger y AtomicReference asegura que las operaciones de suma y conteo de elementos sean seguras en entornos multihilo, lo cual es importante si se despliega en servidores con múltiples hilos de procesamiento.

3. ### Despliegue en Contenedores:

La aplicación puede ser empaquetada en un contenedor Docker para su despliegue y escalado en entornos de Kubernetes o Docker Swarm, facilitando la escalabilidad horizontal.

4. ### Balanceo de carga
uso de balabceadores de carga para distribuir las solicitudes entre las diferentes instancias de la API. Esto asegura que ninguna instancia esté sobrecargada y mejora la disponibilidad del sistema. Dependiendo de la carga y la criticidad, se puede optar por balanceador de software o de hardware. 

5. ### Integración con Apache Kafka (Opcional):
   - Para mejorar la escalabilidad en escenarios de alto volumen de datos, como la carga masiva de archivos CSV, la API puede integrarse con **Apache Kafka**.
   - Kafka actúa como un sistema de mensajería distribuido, permitiendo que la API envíe mensajes a un **topic** de Kafka cada vez que se procesa un lote de datos desde el CSV.
   - Esto facilita el procesamiento asíncrono de datos: una vez que un lote se ha enviado a Kafka, los consumidores pueden procesarlo de forma independiente, desacoplando la carga de procesamiento del servicio principal.
   - Esta arquitectura permite que la API maneje grandes volúmenes de datos sin verse afectada por el tiempo de procesamiento de cada mensaje, distribuyendo la carga de trabajo entre múltiples consumidores.


## Decisiones Arquitectónicas
1. ### Separación de Capas:

El proyecto sigue una arquitectura en capas (Controller, Service, Repository), lo que facilita la mantenibilidad y pruebas unitarias.
CsvProcessor se encarga de la lectura y validación de archivos CSV, lo que permite mantener una lógica de procesamiento separada.

2. ### API RESTful:

La elección de una API RESTful permite una fácil integración con otros servicios, siguiendo las mejores prácticas de HTTP y facilitando la extensibilidad.
Los endpoints están diseñados para ser intuitivos y seguir un estándar de uso de recursos.

3. ### Procesamiento Asíncrono con Lotes:

Para manejar archivos de gran tamaño, se utiliza la lectura por lotes, lo que evita problemas de memoria y mejora la experiencia de usuario al permitir operaciones continuas sin bloqueos.

4. ### Base de Datos H2 para Desarrollo:

La base de datos H2 se usa para desarrollo y pruebas, lo cual facilita la configuración rápida y el desarrollo local.
En entornos de producción, se recomienda cambiar a una base de datos SQL más robusta como PostgreSQL o MySQL.

5. ### Uso de microservicios
El uso de microservicios supone beneficios tan importantes como:
- **Escalabilidad**: se pueden escalar servicios independientes.
- **Optimización de recursos**: uso de recursos conforme a las necesidades expresas del servicio.
- **Tolerancia a fallos**: el uso de microservicios independiza la gestión de los servicios, descentralizando la ejecución de aplicaciones en el mismo nodo, evitando fallos masivos.

## Seguridad

1. ### Autenticación
Se recomienda el uso del servicio de Oauth de Github (por ejemplo) para poder dotar de una capa de seguridad extra a nuestro acceso a la plataforma. Además, nos abstraemos del proceso de autenticación y podemos valorar incluir autenticación multifactor (2FA).

2. ### Uso de HTTPS en los API Rest
Comunicación segura, datos encriptados y protección contra interceptaciones de datos.

3. ### POST mejor que GET
En caso de tener que dar de alta servicios GET en servicios con datos sensibles, considerar el uso de POST para evitar exposición de estos datos en la URL.

4. ### Almacenamiento seguro de credenciales
En entornos de producción, se recomienda el uso de herramientas de gestión de secretos como Vault. Esta herramienta proporciona almacenamiento seguro para credenciales y permiten la rotación automática de contraseñas.


## Seguridad

1. ### Autenticación
Se recomienda el uso del servicio de Oauth de Github (por ejemplo) para poder dotar de una capa de seguridad extra a nuestro acceso a la plataforma. Además, nos abstraemos del proceso de autenticación y podemos valorar incluir autenticación multifactor (2FA).

2. ### Uso de HTTPS en los API Rest
Comunicación segura, datos encriptados y protección contra interceptaciones de datos.

3. ### POST mejor que GET
En caso de tener que dar de alta servicios GET en servicios con datos sensibles, considerar el uso de POST para evitar exposición de estos datos en la URL.

4. ### Almacenamiento seguro de credenciales
En entornos de producción, se recomienda el uso de herramientas de gestión de secretos como Vault. Esta herramienta proporciona almacenamiento seguro para credenciales y permiten la rotación automática de contraseñas.
