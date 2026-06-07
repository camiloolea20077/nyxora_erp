# Pruebas Sprint 1 — Administración (JSON)

> Base URL local: `http://localhost:8081` (ajusta el puerto al de tu `.env`).
> **Todos los endpoints requieren** header `Authorization: Bearer <accessToken>` y
> `Content-Type: application/json`, **excepto** `/api/auth/login` y `/api/auth/refresh`.
> Usuario demo (seed): `admin` / `admin123`.

---

## 1. Login (obtener token)
`POST /api/auth/login`
```json
{ "username": "admin", "password": "admin123" }
```
Copia `data.accessToken` y úsalo como Bearer en todo lo demás.

`POST /api/auth/refresh`
```json
{ "refreshToken": "<refreshToken>" }
```

## 2. Permisos (catálogo global, para asignar a roles)
`GET /api/permisos`  *(sin body)*

## 3. Roles
`POST /api/roles`
```json
{ "name": "Cajero", "permisoIds": [4, 5] }
```
`PUT /api/roles`  (reemplaza permisos si envías `permisoIds`)
```json
{ "id": 2, "name": "Cajero Senior", "permisoIds": [4, 5, 6] }
```
`GET /api/roles/2`
`POST /api/roles/list`
```json
{ "page": 0, "rows": 10, "search": "" }
```
`DELETE /api/roles/2`

## 4. Usuarios
> ⚠️ Un usuario **debe pertenecer a un tercero (persona)**. Como el CRUD de terceros es del Sprint 2,
> crea un tercero de prueba con este SQL (devuelve el `id` que usarás como `terceroId`):
> ```sql
> INSERT INTO tercero (empresa_id, tipo_identificacion_id, numero_documento, tipo_persona, nombre)
> SELECT 1, ti.id, '79123456', 'natural', 'Juan Pérez'
> FROM tipo_identificacion ti ORDER BY ti.id LIMIT 1
> RETURNING id;
> ```

`POST /api/usuarios`  (terceroId obligatorio; un tercero solo puede tener un usuario)
```json
{ "terceroId": 2, "username": "jperez", "email": "jperez@nyxora.local", "password": "clave123" }
```
`PUT /api/usuarios`  (password opcional: si no lo envías, no se cambia)
```json
{ "id": 2, "email": "jperez2@nyxora.local", "active": true, "password": "nueva123" }
```
`GET /api/usuarios/2`
`POST /api/usuarios/list`
```json
{ "page": 0, "rows": 10, "search": "jperez" }
```
`DELETE /api/usuarios/2`

### Asignar / quitar rol a un usuario (en una sede)
`POST /api/usuarios/2/roles`
```json
{ "rolId": 2, "sedeId": 1 }
```
`DELETE /api/usuarios/2/roles`
```json
{ "rolId": 2, "sedeId": 1 }
```

## 4b. Empresa (enriquecida: NIT+DV, dirección, datos fiscales, contacto)
> El admin demo **no** es super-admin, así que puede **ver y actualizar SU empresa (id=1)**.
> `crear` y `listar todas` requieren super-admin (devuelven 403 con el admin demo).
> `municipioId`, `tipoContribuyenteId`, `actividadEconomicaId` son FKs opcionales (usa ids de los catálogos sembrados).

`GET /api/empresas/actual`  ← tu empresa (la del token, sin saber el id)
`GET /api/empresas/1`       ← por id
> ⚠️ NO existe `GET /api/empresas` (sin id): da 404 "No static resource". Usa `/actual`, `/{id}` o `POST /list`.

`PUT /api/empresas`  (rellena los datos de tu empresa)
```json
{
  "id": 1,
  "nit": "900123456",
  "digitoVerificacion": 7,
  "razonSocial": "Empresa Demo S.A.S.",
  "nombreComercial": "Nyxora Demo",
  "codigo": "EMP01",
  "tipoPersona": "juridica",
  "representanteLegal": "Juan Administrador",
  "regimenTributario": "responsable_iva",
  "responsabilidadFiscal": "O-13",
  "sector": "comercio",
  "email": "info@nyxorademo.com",
  "telefono": "6041234567",
  "celular": "3001234567",
  "sitioWeb": "https://nyxorademo.com",
  "municipioId": 1,
  "direccion": "Calle 10 # 20-30",
  "codigoPostal": "050001",
  "active": true
}
```
`POST /api/empresas` y `POST /api/empresas/list` → requieren super-admin.

## 4c. Vigencias (periodo fiscal + máquina de estados)
`POST /api/vigencias`  (nace en estado `planeada`)
```json
{ "year": 2026 }
```
`POST /api/vigencias/1/abrir`   ← planeada → abierta
`POST /api/vigencias/1/cerrar`  ← abierta → cerrada
`PUT /api/vigencias`
```json
{ "id": 1, "year": 2026 }
```
`GET /api/vigencias/1` · `DELETE /api/vigencias/1`
`POST /api/vigencias/list`
```json
{ "page": 0, "rows": 10 }
```

## 4d. Parámetros del sistema
`POST /api/parametros`
```json
{ "key": "moneda.base", "value": "COP", "dataType": "string" }
```
`PUT /api/parametros`
```json
{ "id": 1, "value": "USD", "dataType": "string" }
```
`GET /api/parametros/1` · `GET /api/parametros/by-clave/moneda.base` · `DELETE /api/parametros/1`
`POST /api/parametros/list`
```json
{ "page": 0, "rows": 10, "search": "moneda" }
```

## 5. Sedes (referencia, ya estaba)
`POST /api/sedes`
```json
{ "code": "SUC01", "name": "Sucursal Norte" }
```
`PUT /api/sedes`
```json
{ "id": 2, "code": "SUC01", "name": "Sucursal Norte Edit" }
```
`GET /api/sedes/1` · `DELETE /api/sedes/2`
`POST /api/sedes/list`
```json
{ "page": 0, "rows": 10, "search": "" }
```

---

### Flujo sugerido para validar RBAC completo
1. Login admin → token.
2. `GET /api/permisos` → mira los `id` de permisos.
3. `POST /api/roles` con esos `permisoIds` → crea un rol.
4. `POST /api/usuarios` → crea un usuario.
5. `POST /api/usuarios/{id}/roles` → asígnale el rol en la sede 1.
6. Login con el nuevo usuario → su token traerá los permisos heredados del rol.
