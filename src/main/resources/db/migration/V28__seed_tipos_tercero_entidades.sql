-- Tipos de tercero adicionales (entidades). Se diferencian en el formulario; persisten en el maestro tercero.
INSERT INTO tipo_tercero (codigo, nombre) VALUES
 ('ASEGURADORA', 'Aseguradora'),
 ('BANCO', 'Banco'),
 ('ICBF', 'ICBF')
ON CONFLICT (codigo) DO NOTHING;
