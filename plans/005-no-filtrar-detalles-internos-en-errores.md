# Plan 005: Dejar de exponer detalles internos en respuestas de error

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandler.java`
> On a mismatch with the excerpts below, treat it as a STOP condition.

## Status

- **Priority**: P2
- **Effort**: S
- **Risk**: MED (si algún flujo de negocio lanza `RuntimeException` cruda y el frontend depende de su mensaje/409, cambiará a 500 genérico — eso es deseable, pero hay que verificarlo con la suite)
- **Depends on**: plans/001-baseline-pruebas-integracion.md (tests)
- **Category**: security
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

`GlobalExceptionHandler` devuelve `ex.getMessage()` al cliente para CUALQUIER `RuntimeException` (además con estado 409) y para cualquier `Exception` (500). Los mensajes de excepciones no controladas en este stack incluyen detalles internos: SQL y nombres de columnas (R2DBC `BadSqlGrammarException`, `DataIntegrityViolationException` con el nombre del constraint), nulls de mapeo, rutas de clases. Eso le regala a un atacante el esquema de la BD y contradice la regla del equipo (mensajes de error al usuario en español, vía `GlobalException`). Además, mapear toda `RuntimeException` a 409 CONFLICT confunde la semántica del API: un NPE hoy responde 409.

## Current state

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandler.java:75-91`:

  ```java
  // Cualquier RuntimeException no específica
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
      logger.error("RuntimeException no controlada", ex);
      ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage(), true, null);
      return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  // Cualquier otra excepción
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
      logger.error("Excepción no controlada", ex);
      ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
              true, null);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  ```

- Los errores esperables del negocio NO pasan por ahí: servicios lanzan `GlobalException(HttpStatus, "mensaje")` o `BusinessException`, con handlers propios (líneas 30-40) que se conservan tal cual.
- Contrato de respuesta: `ApiResponse<T>(status, message, error, data)` — `util/ApiResponse.java`.
- Convención (regla 00): mensajes al usuario en español.

## Commands you will need

| Purpose   | Command                            | Expected on success |
|-----------|------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`           | exit 0              |
| Tests     | `mvnw.cmd -B test`                 | BUILD SUCCESS       |

## Scope

**In scope**:
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandler.java`
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandlerTest.java` (create — unitario)

**Out of scope**:
- `GlobalException`, `BusinessException`, `ApiResponse` — el contrato no cambia.
- Handlers existentes de `GlobalException`, `BusinessException`, `WebExchangeBindException`, `ResponseStatusException`, `NumberFormatException`, `JsonMappingException` — no tocarlos.
- Cualquier servicio o controller (si un flujo de negocio dependía de `RuntimeException` cruda, eso se REPORTA, no se arregla aquí).

## Git workflow

- Branch: `advisor/005-errores-sin-detalles-internos`
- Commit: `fix: respuestas de error genericas para excepciones no controladas`
- No push/PR sin instrucción del operador.

## Steps

### Step 1: Añadir handler específico para violaciones de integridad

Antes del handler de `RuntimeException`, añadir (import `org.springframework.dao.DataIntegrityViolationException`):

```java
// Violaciones de integridad (FK, UNIQUE...) → 409 con mensaje genérico, sin nombre de constraint
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
    logger.error("Violación de integridad de datos", ex);
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(),
            "El registro entra en conflicto con datos existentes", true, null);
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
}
```

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Generalizar los handlers residuales

Reemplazar los cuerpos de `handleRuntimeException` y `handleGenericException` para responder **500** con mensaje fijo en español, conservando el log completo:

```java
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
    logger.error("RuntimeException no controlada", ex);
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor", true, null);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    logger.error("Excepción no controlada", ex);
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor", true, null);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Test unitario del handler

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandlerTest.java` (unitario puro: `new GlobalExceptionHandler()` y llamar los métodos directamente). Casos:

1. `handleRuntimeException(new NullPointerException("tabla usuario columna x"))` → status 500 y `message` NO contiene `"tabla usuario"` (es `"Error interno del servidor"`).
2. `handleDataIntegrity(...)` → 409 y mensaje genérico.
3. `handleGlobalException(new GlobalException(HttpStatus.NOT_FOUND, "Tercero no encontrado"))` → 404 con el mensaje intacto (regresión: los errores de negocio conservan su texto).

**Verify**: `mvnw.cmd -B test -Dtest=GlobalExceptionHandlerTest` → 3 tests pasan.

### Step 4: Suite completa

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS. Si algún test de integración existente esperaba un 409 con mensaje crudo, eso es un STOP (ver abajo).

## Test plan

Casos en el paso 3. No hay test previo de este handler; este archivo se convierte en el patrón. Verificación: `mvnw.cmd -B test` → todo pasa.

## Done criteria

- [ ] `grep -n "ex.getMessage()" src/main/java/com/cloud_tecnoligical/nyxora_erp/util/GlobalExceptionHandler.java` → solo apariciones en handlers de excepciones CONTROLADAS (`GlobalException`, `BusinessException`); ninguna en los de `RuntimeException`/`Exception`
- [ ] `handleRuntimeException` responde 500, no 409
- [ ] Existe handler de `DataIntegrityViolationException` → 409 genérico
- [ ] `mvnw.cmd -B test` exit 0 con los 3 tests nuevos
- [ ] Solo archivos in-scope modificados; fila actualizada en `plans/README.md`

## STOP conditions

- La suite revela que un flujo de negocio lanza `RuntimeException` cruda esperando 409 con su mensaje (búsqueda de apoyo: `grep -rn "throw new RuntimeException" src/main/java/` — si hay resultados en services/controllers, listarlos en el reporte). El arreglo correcto es migrar esos puntos a `GlobalException`, pero está fuera de alcance: reportar.
- El frontend (si el operador lo indica) depende del comportamiento 409-con-mensaje para errores no controlados.

## Maintenance notes

- Regla para el equipo: todo error esperable debe lanzarse como `GlobalException(HttpStatus, msg)`; con este cambio, lo que no la use mostrará "Error interno del servidor" — incentivo correcto.
- Revisor: vigilar que nadie reintroduzca `ex.getMessage()` en handlers genéricos en PRs futuros.
- Futuro relacionado (no en este plan): logging estructurado con correlation-id para rastrear los 500 genéricos hasta el stack trace del log.
