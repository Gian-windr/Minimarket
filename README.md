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
- Spring Security + JWT (jjwt)
- Bean Validation
- Lombok
- springdoc-openapi (Swagger UI)
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

Al primer arranque se crean datos iniciales: roles `ADMINISTRADOR` y `CAJERO`, unidades de medida básicas y el usuario administrador **admin / admin123** (cambiar la password en producción).

La documentación interactiva de la API está en `http://localhost:8080/swagger-ui.html` — autenticarse en `POST /api/auth/login` y usar el botón **Authorize** con el token JWT.

### Frontend

```bash
cd frontend
pnpm install
pnpm start
```

> Ajusta la ruta anterior según dónde se ubique el proyecto de frontend dentro del repositorio.

## API

Todos los endpoints cuelgan de `/api` y requieren token JWT, salvo `/api/auth/login` y la documentación.

| Grupo | Ruta base | Qué hace |
|---|---|---|
| Autenticación | `/api/auth` | Login y emisión de token |
| Dashboard | `/api/dashboard` | Resumen del día, mes, alertas y caja |
| Productos | `/api/productos` | CRUD, búsqueda por código de barras, stock bajo |
| Catálogos | `/api/categorias`, `/api/tipos-producto`, `/api/unidades-medida` | Maestros del catálogo |
| Inventario | `/api/inventario` | Kardex y ajustes manuales de stock |
| Ventas | `/api/ventas` | Registro y anulación de ventas |
| Comprobantes | `/api/comprobantes` | Emisión de boleta/factura y PDF |
| Devoluciones | `/api/devoluciones` | Devoluciones con nota de crédito |
| Caja | `/api/cajas` | Apertura, cierre y arqueo |
| Compras | `/api/compras`, `/api/proveedores` | Ingreso de mercadería |
| Clientes | `/api/clientes`, `/api/consultas` | Clientes y consulta DNI/RUC |
| Promociones | `/api/promociones` | Descuentos por producto |
| Reportes | `/api/reportes` | Ventas, top productos, clientes, stock |
| Administración | `/api/usuarios`, `/api/roles`, `/api/empleados`, `/api/auditoria`, `/api/backups` | Solo rol ADMINISTRADOR |

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

La suite tiene 34 pruebas y no requiere PostgreSQL: las pruebas de integración levantan la aplicación contra una base H2 en memoria (configurada en `src/test/resources/application.properties`).

- **Pruebas unitarias** de la lógica de negocio: ventas e IGV, kardex de inventario, arqueo de caja, devoluciones, promociones y correlativos de comprobantes.
- **Pruebas de integración**: flujo completo de venta contra base de datos, generación real del PDF, y circuito HTTP con Tomcat (login JWT, endpoints protegidos, Swagger público).

## Solución de problemas

**`java.io.IOException: Unable to establish loopback connection` al compilar o ejecutar**

En esta máquina los sockets AF_UNIX están bloqueados (interferencia de antivirus/LSP de Winsock), lo que rompe `Selector.open()` del JDK 21 y con ello Gradle y Tomcat. Workaround — definir esta variable de entorno antes de compilar o ejecutar:

```powershell
$env:JDK_JAVA_OPTIONS = "-Djdk.net.unixdomain.tmpdir=C:/__uds_disabled__"
```

Apuntar a una ruta inexistente fuerza al JDK a usar TCP loopback en lugar de sockets Unix. Para hacerlo permanente, agregarla como variable de entorno de usuario en Windows. El fix definitivo es reparar Winsock (`netsh winsock reset` como administrador y reiniciar).

## Licencia

Proyecto personal con fines de aprendizaje. Pendiente de definir licencia.

## Autor

Proyecto desarrollado por camposgian05@gmail.com.
