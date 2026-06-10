# Pruebas Sprint 2 — Terceros (JSON)

> Base `http://localhost:8081`. Header `Authorization: Bearer <accessToken>` + `Content-Type: application/json`.
> `tipoIdentificacionId` y `tipoTerceroIds` son ids de catálogos (sembrados en V24). Para verlos:
> ```sql
> SELECT id, codigo, nombre FROM tipo_identificacion;   -- CC, NIT, CE...
> SELECT id, codigo, nombre FROM tipo_tercero;          -- CLIENTE, PROVEEDOR, EMPLEADO...
> ```

## Crear tercero — persona natural (cliente)
`POST /api/terceros`
```json
{
  "tipoIdentificacionId": 1,
  "numeroDocumento": "1098765432",
  "tipoPersona": "natural",
  "primerNombre": "Carlos",
  "segundoNombre": "Andrés",
  "primerApellido": "Gómez",
  "segundoApellido": "Ruiz",
  "municipioId": 1,
  "direccion": "Cra 50 # 10-20",
  "responsableIva": false,
  "tipoTerceroIds": [1]
}
```
> `nombre` se calcula solo (nombres + apellidos). Devuelve el tercero con su `id` y `tipoTerceroIds`.

## Crear tercero — persona jurídica (proveedor)
`POST /api/terceros`
```json
{
  "tipoIdentificacionId": 2,
  "numeroDocumento": "900555444",
  "digitoVerificacion": 1,
  "tipoPersona": "juridica",
  "razonSocial": "Distribuciones XYZ S.A.S.",
  "nombreComercial": "XYZ",
  "nombreRepresentanteLegal": "Ana Pérez",
  "actividadEconomicaId": null,
  "tipoContribuyenteId": 1,
  "responsableIva": true,
  "esAutoretenedorIva": false,
  "declarante": true,
  "condicionPagoProveedorId": 3,
  "tipoTerceroIds": [2]
}
```

## Crear tercero — JSON COMPLETO (todos los campos, persona jurídica)
`POST /api/terceros`
```json
{
  "tipoIdentificacionId": 2,
  "numeroDocumento": "900555444",
  "digitoVerificacion": 1,
  "tipoPersona": "juridica",
  "primerNombre": null, "segundoNombre": null, "primerApellido": null, "segundoApellido": null,
  "razonSocial": "Distribuciones XYZ S.A.S.",
  "nombreComercial": "XYZ",
  "nombreRepresentanteLegal": "Ana Pérez",
  "documentoRepresentanteLegal": "43111222",
  "generoId": null, "estadoCivilId": null, "fechaNacimiento": null,
  "municipioId": 1, "barrioId": null, "direccion": "Cra 50 # 10-20", "sitioWeb": "https://xyz.com",
  "fechaExpedicionDocumento": "2015-03-10", "municipioExpedicionId": 1, "fechaVencimientoDocumento": null,
  "actividadEconomicaId": null, "tipoContribuyenteId": 1,
  "responsableIva": true, "esAutoretenedorIva": false, "esAutoretenedorIca": false,
  "esAutoretenedorFuente": false, "declarante": true, "aplicaArt383": false, "tieneRut": true,
  "condicionPagoClienteId": 3, "condicionPagoProveedorId": 4,
  "formaPagoClienteId": 2, "formaPagoProveedorId": 2, "interesEfectivoMensual": 2.5,
  "cuentaContableProveedorId": null, "recursoId": null,
  "esReciproco": false, "codigoReciproco": null,
  "observaciones": "Proveedor mayorista zona norte",
  "tipoTerceroIds": [2, 1]
}
```
> `nombre` NO se envía (lo calcula el backend). FKs sin datos sembrados → null. Fechas `yyyy-MM-dd`.

## Resto de operaciones
`GET /api/terceros/{id}`   (incluye `tipoTerceroIds`)
`PUT /api/terceros`  (parcial: solo envía lo que cambia; obligatorios id, tipoIdentificacionId, numeroDocumento, tipoPersona)
```json
{ "id": 2, "tipoIdentificacionId": 1, "numeroDocumento": "1098765432", "tipoPersona": "natural", "direccion": "Nueva dirección 123", "tipoTerceroIds": [1, 3] }
```
`DELETE /api/terceros/{id}`
`POST /api/terceros/list`
```json
{ "page": 0, "rows": 10, "search": "gomez" }
```

## Encadenado con Sprint 1
Ahora que puedes crear terceros por API, ya puedes crear su usuario sin SQL manual:
```json
POST /api/usuarios
{ "terceroId": 2, "username": "cgomez", "email": "cgomez@demo.com", "password": "clave123" }
```

## Satélites del tercero  (sub-recursos bajo `/api/terceros/{terceroId}`)
> Todos validan que el tercero sea de tu empresa. `principal=true` desmarca el principal anterior.

### Contactos
`GET  /api/terceros/2/contactos`
`POST /api/terceros/2/contactos`
```json
{ "nombre": "María López", "cargo": "Compras", "telefono": "6041112233", "celular": "3009998877", "email": "maria@xyz.com", "principal": true }
```
`PUT  /api/terceros/2/contactos`
```json
{ "id": 1, "nombre": "María López", "cargo": "Gerente de Compras", "principal": true }
```
`DELETE /api/terceros/2/contactos/1`

### Direcciones
`GET  /api/terceros/2/direcciones`
`POST /api/terceros/2/direcciones`
```json
{ "tipo": "facturacion", "direccion": "Cra 50 # 10-20", "municipioId": 1, "codigoPostal": "050001", "telefono": "6041112233", "principal": true }
```
`PUT  /api/terceros/2/direcciones`
```json
{ "id": 1, "tipo": "envio", "direccion": "Bodega Km 5 vía norte", "municipioId": 1 }
```
`DELETE /api/terceros/2/direcciones/1`

### Cuentas bancarias
`GET  /api/terceros/2/cuentas-bancarias`
`POST /api/terceros/2/cuentas-bancarias`
```json
{ "bancoId": 1, "tipoCuentaBancariaId": 1, "numeroCuenta": "1234567890", "principal": true }
```
`PUT  /api/terceros/2/cuentas-bancarias`
```json
{ "id": 1, "bancoId": 1, "tipoCuentaBancariaId": 2, "numeroCuenta": "9876543210" }
```
`DELETE /api/terceros/2/cuentas-bancarias/1`
> `bancoId` y `tipoCuentaBancariaId` salen de los catálogos sembrados: `SELECT id,codigo,nombre FROM banco;` / `tipo_cuenta_bancaria;`
