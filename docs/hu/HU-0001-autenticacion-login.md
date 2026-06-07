# HU-0001 — Autenticación y login con JWT multi-tenant

| Campo | Valor |
|---|---|
| **Código** | HU-0001 |
| **Módulo** | Administración (seguridad) |
| **Estado** | ✅ Hecha (probada contra Postgres real, 2026-06-06) |
| **Prioridad** | Alta |
| **Dependencias** | Migración V1 (usuario/rol/permiso); agente jwt-multitenant |

## Historia
> **Como** usuario del ERP
> **quiero** autenticarme con usuario y contraseña y recibir un JWT
> **para** operar de forma segura dentro del contexto de mi empresa y sede.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Login exitoso
  Dado un usuario activo con credenciales válidas
  Cuando hace POST /api/auth/login con username y password
  Entonces recibe 200 y un JWT con claims empresa_id, usuario_id, sede_id y permisos

Escenario: Credenciales inválidas
  Dado un usuario inexistente o contraseña incorrecta
  Cuando hace POST /api/auth/login
  Entonces recibe 401 con mensaje genérico "Credenciales inválidas"

Escenario: Acceso sin token
  Dado un endpoint protegido
  Cuando se invoca sin Authorization: Bearer
  Entonces recibe 401
```

## Reglas de negocio
- RN1: La contraseña se valida contra `usuario.hash_password` (bcrypt/argon2); jamás texto plano.
- RN2: El JWT incluye `empresa_id`, `usuario_id`, `sede_id`, `super_admin` y permisos efectivos.
- RN3: Mensaje de error genérico (no revelar si el usuario existe).
- RN4: El tenant viaja en el Reactor Context (no ThreadLocal).

## Datos (consultar agente base-datos)
- **Entidad(es) dueña(s):** `usuario`, `rol`, `permiso`, `rol_permiso`, `usuario_rol`.
- **Migración nueva requerida:** no (existe en V1).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST | `/api/auth/login` | `LoginRequestDto {username, password}` | `ApiResponse<TokenResponseDto>` |
| POST | `/api/auth/refresh` | `RefreshRequestDto {refreshToken}` | `ApiResponse<TokenResponseDto>` |

## Seguridad
- Rutas públicas: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`. Resto autenticado.
- Implementar `JwtAuthenticationWebFilter` que escribe `TenantContext.write(tenantInfo)`.

## Preguntas abiertas
- ¿Algoritmo de firma (HS256 con secreto vs RS256 con par de llaves)?
- ¿Duración del access token y del refresh token?
- ¿Multi-sede: el usuario elige sede al login o se infiere?
