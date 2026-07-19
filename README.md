# MiniMarket Don Manuelito

Sistema de gestión de ventas e inventarios para un minimarket, desarrollado con Java + Spring Boot en el backend y React en el frontend, usando PostgreSQL como base de datos.

Para el detalle completo de funcionalidades (implementadas y planeadas), ver [FUNCIONALIDADES.md](FUNCIONALIDADES.md).

## Arquitectura

Aplicación desacoplada en dos proyectos independientes:

- **Backend**: API REST con Spring Boot (`spring-boot-starter-web` + Spring Data JPA), sin renderizado de vistas del lado del servidor.
- **Frontend**: SPA en React + React Router DOM, consume la API del backend vía HTTP.

Ambos proyectos se ejecutan y despliegan por separado.

## Tecnologías

**Backend**
- Java 21
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Web
- Gradle

**Base de datos**
- PostgreSQL

**Frontend**
- React
- React Router DOM

**Servicios externos**
- [Factiliza](https://factiliza.com/) — validación de documentos (DNI/RUC)

## Requisitos previos

- JDK 21
- PostgreSQL 14+
- Node.js 18+ y pnpm (para el frontend)
- Gradle Wrapper (incluido en el repositorio, no requiere instalación aparte)

## Configuración

1. Crea una base de datos en PostgreSQL para el proyecto.
2. Crea un archivo `.env` en la raíz del proyecto (no se versiona) con las siguientes variables:

   ```
   API_KEY=tu_api_key_de_factiliza
   ```

3. Configura la conexión a la base de datos en `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/minimarket
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
   spring.jpa.hibernate.ddl-auto=update
   ```

## Instalación y ejecución

### Backend

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd minimarket

# Ejecutar la aplicación
./gradlew bootRun
```

El backend quedará disponible en `http://localhost:8080`.

### Frontend

```bash
cd frontend
pnpm install
pnpm start
```

> Ajusta la ruta anterior según dónde se ubique el proyecto de frontend dentro del repositorio.

## Estructura del proyecto

```
minimarket/
├── src/
│   ├── main/
│   │   ├── java/com/donmanuelito/minimarket/
│   │   │   ├── config/       # Configuración de la aplicación
│   │   │   ├── controller/   # Controladores REST
│   │   │   ├── dto/          # Objetos de transferencia de datos
│   │   │   ├── exception/    # Manejo de excepciones
│   │   │   ├── model/        # Entidades JPA
│   │   │   ├── repository/   # Repositorios de datos
│   │   │   └── service/      # Lógica de negocio
│   │   └── resources/
│   │       └── application.properties
│   └── test/                 # Pruebas unitarias e integración
├── build.gradle
├── settings.gradle
└── frontend/                 # SPA en React (proyecto independiente)
```

## Pruebas

```bash
./gradlew test
```

## Licencia

Proyecto personal con fines de aprendizaje. Pendiente de definir licencia.

## Autor

Proyecto desarrollado por camposgian05@gmail.com.
