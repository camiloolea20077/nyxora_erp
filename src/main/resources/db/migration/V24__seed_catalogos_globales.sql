-- ============================================================================
-- V24 — Semillas de catálogos GLOBALES (estándar Colombia/DIAN, sin empresa_id)
-- Nyxora · PostgreSQL
--
-- Solo catálogos globales. Los datos por-empresa (plan de cuentas, impuestos, tipos de
-- documento, consecutivos) se siembran al crear cada empresa (seed por tenant).
-- La geografía DANE completa y el CIIU completo se importan del real (starter mínimo aquí).
-- ============================================================================

-- tipo_identificacion (códigos DIAN)
INSERT INTO tipo_identificacion (codigo, nombre) VALUES
 ('13','Cédula de ciudadanía'), ('31','NIT'), ('22','Cédula de extranjería'),
 ('12','Tarjeta de identidad'), ('41','Pasaporte'), ('11','Registro civil'),
 ('21','Tarjeta de extranjería'), ('42','Documento extranjero'), ('47','PEP'),
 ('48','PPT'), ('50','NIT otro país'), ('91','NUIP');

-- genero
INSERT INTO genero (codigo, nombre) VALUES ('M','Masculino'), ('F','Femenino'), ('O','Otro');

-- estado_civil
INSERT INTO estado_civil (codigo, nombre) VALUES
 ('SOL','Soltero(a)'), ('CAS','Casado(a)'), ('UNL','Unión libre'),
 ('DIV','Divorciado(a)'), ('VIU','Viudo(a)'), ('SEP','Separado(a)');

-- tipo_tercero
INSERT INTO tipo_tercero (codigo, nombre) VALUES
 ('CLIENTE','Cliente'), ('PROVEEDOR','Proveedor'), ('EMPLEADO','Empleado'),
 ('ACREEDOR','Acreedor'), ('SOCIO','Socio'), ('OTRO','Otro');

-- tipo_contribuyente
INSERT INTO tipo_contribuyente (codigo, nombre) VALUES
 ('COMUN','Responsable de IVA'), ('NO_RESP','No responsable de IVA'),
 ('GRAN','Gran contribuyente'), ('SIMPLE','Régimen simple'), ('AUTORET','Autorretenedor');

-- condicion_pago
INSERT INTO condicion_pago (codigo, nombre, dias) VALUES
 ('CONTADO','Contado',0), ('C15','Crédito 15 días',15), ('C30','Crédito 30 días',30),
 ('C60','Crédito 60 días',60), ('C90','Crédito 90 días',90);

-- forma_pago
INSERT INTO forma_pago (codigo, nombre) VALUES
 ('EFECTIVO','Efectivo'), ('TRANSFER','Transferencia'), ('CHEQUE','Cheque'),
 ('TARJETA','Tarjeta'), ('CONSIGNA','Consignación'), ('OTRO','Otro');

-- tipo_cuenta_bancaria
INSERT INTO tipo_cuenta_bancaria (codigo, nombre) VALUES
 ('AHORROS','Ahorros'), ('CORRIENTE','Corriente');

-- banco (principales de Colombia, código ACH)
INSERT INTO banco (codigo, nombre) VALUES
 ('1007','Bancolombia'), ('1051','Davivienda'), ('1001','Banco de Bogotá'),
 ('1013','BBVA Colombia'), ('1023','Banco de Occidente'), ('1009','Citibank'),
 ('1012','Banco GNB Sudameris'), ('1006','Banco Itaú'), ('1002','Banco Popular'),
 ('1062','Banco Falabella'), ('1283','Banco Cooperativo Coopcentral'), ('1551','Daviplata'),
 ('1801','Nequi'), ('1052','Banco AV Villas'), ('1019','Scotiabank Colpatria');

-- nivel_estudio
INSERT INTO nivel_estudio (codigo, nombre) VALUES
 ('PRIM','Primaria'), ('SEC','Secundaria'), ('TEC','Técnico'), ('TECN','Tecnólogo'),
 ('UNI','Universitario'), ('ESP','Especialización'), ('MAES','Maestría'), ('DOC','Doctorado');

-- pais (starter; ISO)
INSERT INTO pais (codigo, nombre) VALUES ('CO','Colombia'), ('US','Estados Unidos'), ('VE','Venezuela'), ('EC','Ecuador');

-- departamento (starter DANE; FK a pais Colombia)
INSERT INTO departamento (pais_id, codigo, nombre)
 SELECT id, d.codigo, d.nombre FROM pais,
   (VALUES ('05','Antioquia'), ('08','Atlántico'), ('11','Bogotá D.C.'),
           ('76','Valle del Cauca'), ('68','Santander'), ('66','Risaralda'),
           ('25','Cundinamarca'), ('13','Bolívar')) AS d(codigo, nombre)
 WHERE pais.codigo = 'CO';

-- municipio (starter DANE; FK a departamento)
INSERT INTO municipio (departamento_id, codigo, nombre)
 SELECT dep.id, m.codigo, m.nombre FROM departamento dep,
   (VALUES ('05','05001','Medellín'), ('08','08001','Barranquilla'), ('11','11001','Bogotá D.C.'),
           ('76','76001','Cali'), ('68','68001','Bucaramanga'), ('66','66001','Pereira'),
           ('13','13001','Cartagena')) AS m(dep_codigo, codigo, nombre)
 WHERE dep.codigo = m.dep_codigo;
