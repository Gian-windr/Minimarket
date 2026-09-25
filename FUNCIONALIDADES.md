# Funcionalidades del Proyecto

Este documento detalla las funcionalidades del sistema MiniMarket Don Manuelito. Para información técnica e instrucciones de instalación, ver [README.md](README.md).

## Módulo de Inventario
- **Gestión de productos**: alta, edición, baja y consulta del catálogo de productos.
- **Categorías y unidades de medida**: organización de productos por categoría (abarrotes, bebidas, limpieza, etc.) y unidad (unidad, caja, kg, litro).
- **Control de stock**: registro de entradas y salidas de inventario, con historial de movimientos.
- **Alertas de stock mínimo**: notificación cuando un producto llega a su umbral de reposición.
- **Código de barras**: búsqueda y registro rápido de productos mediante escaneo.
- **Precio de compra vs. precio de venta**: cálculo de margen por producto.

## Módulo de Proveedores y Compras
- **Gestión de proveedores**: alta, edición y baja de proveedores.
- **Registro de compras**: ingreso de mercadería asociada a un proveedor, actualizando el stock automáticamente.
- **Historial de compras**: consulta de compras realizadas por proveedor y por fecha.

## Módulo de Ventas
- **Punto de venta (POS)**: registro de ventas con búsqueda de productos por código de barras o nombre.
- **Apertura y cierre de caja**: registro de monto inicial, arqueo de caja y diferencias al cerrar turno.
- **Comprobantes de pago**: emisión de boleta o factura, con numeración/correlativo.
- **Cálculo de impuestos (IGV)**: aplicación automática de impuestos sobre el total de venta.
- **Métodos de pago**: efectivo, tarjeta, transferencia u otros.
- **Devoluciones y anulación de ventas**: reversión de una venta con ajuste automático de stock.
- **Historial de ventas**: consulta de ventas por fecha, cliente o producto.

## Módulo de Clientes
- **Gestión de clientes**: alta, edición, baja y consulta de clientes registrados.
- **Historial de compras por cliente**.
- **Consulta de DNI/RUC**: autocompletado de los datos del cliente contra la API de Factiliza.

## Módulo de Usuarios y Seguridad
- **Gestión de usuarios**: alta, edición y baja de usuarios del sistema.
- **Roles y permisos**: control de acceso a funcionalidades según el rol (administrador, cajero, etc.).
- **Autenticación y autorización**: acceso al sistema mediante credenciales, restringido según rol.

## Módulo de Reportes
- **Reporte de ventas**: por día, semana, mes o rango de fechas.
- **Productos más vendidos**.
- **Clientes frecuentes**.
- **Reporte de stock**: productos con bajo inventario o próximos a vencer.
- **Reporte de caja**: resumen de ingresos y egresos por turno.

## Estado de las funcionalidades

| Funcionalidad | Backend | Frontend |
|---|---|---|
| Gestión de productos | Listo | Pendiente |
| Categorías, tipos y unidades de medida | Listo | Pendiente |
| Control de stock (kardex de movimientos) | Listo | Pendiente |
| Alertas de stock mínimo | Listo | Pendiente |
| Código de barras (campo + búsqueda) | Listo | Pendiente |
| Promociones y descuentos | Listo | Pendiente |
| Gestión de proveedores | Listo | Pendiente |
| Registro de compras (actualiza stock) | Listo | Pendiente |
| Punto de venta (registro de ventas) | Listo | Pendiente |
| Apertura y cierre de caja (arqueo) | Listo | Pendiente |
| Comprobantes (boleta/factura con correlativo) | Listo | Pendiente |
| Cálculo de IGV (desglose del precio) | Listo | Pendiente |
| Devoluciones (con nota de crédito automática) | Listo | Pendiente |
| Anulación de ventas (restituye stock) | Listo | Pendiente |
| Gestión de clientes | Listo | Pendiente |
| Gestión de usuarios y roles (JWT) | Listo | Pendiente |
| Auditoría (login, anulaciones) | Listo | Pendiente |
| Registro de backups | Listo | Pendiente |
| Reportes (ventas, top productos, clientes frecuentes, stock bajo) | Listo | Pendiente |
| Dashboard operativo (resumen del día y del mes) | Listo | Pendiente |
| PDF del comprobante (ticket 80 mm) | Listo | Pendiente |
| Integración Factiliza (validar DNI/RUC) | Listo | Pendiente |
| Paginación en productos y ventas | Listo | Pendiente |
| XML firmado para SUNAT (UBL 2.1) | Pendiente | — |

> "Listo" en backend = implementado y cubierto por la suite de pruebas (34 pruebas, todas en verde), incluyendo pruebas de integración que levantan la aplicación real contra una base H2 en memoria.
