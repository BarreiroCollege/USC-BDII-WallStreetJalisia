insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, activo, baja) 
	values ('manuel', 'sha1:64000:18:gQh8DgIBEwFWNANA16hiluLqCTYsMIwG:p+2PGzw91P3g/ysi6XBLc6NJ', 'Calle del olmo, 27', '28012', 'Madrid', 617483290, 529.0, 0.0, true, false);
insert into inversor (usuario, dni, nombre, apellidos) 
	values ('manuel', '14782689F', 'Manuel', 'Iglesias Suárez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, activo, baja)
	values ('marcos', 'sha1:64000:18:xN6xzXXuxSzqbrpnHJIqv1FZWdTLYRCc:5ngoCRSKBJwbQ8nSm4jVwZip', 'Calle Abedul, 12', '28036', 'Madrid', 628714222, 1067.0, 0.0, true, false);
insert into inversor (usuario, dni, nombre, apellidos) 
	values ('marcos', '47157847C', 'Marcos', 'Vázquez García');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, activo, baja)
	values ('teceric', 'sha1:64000:18:0WoocFp8pqjWuMP/72KFmGE62xjlBsP6:MEB75nM5A3WfLS10Bj5DTu6A', 'Calle 10 de Marzo', '36210', 'Vigo', 986331724, 7012.0, 0.0, true, false);
insert into empresa (usuario, cif, nombre) 
	values ('teceric', 'B-76365789', 'Teclados Eric');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, activo, baja)
	values ('garcables', 'sha1:64000:18:0cUa4PNwAG+HJvz3JD5Zg4Ru08Bo0J/Y:djo94B5Vc1r5gPasgWM6B88K', 'Avenida Ramón y Cajal, 79', '28016', 'Madrid', 677100888, 0.0, 0.0, true, false);
insert into empresa (usuario, cif, nombre) 
	values ('garcables', 'A-70012249', 'Garaje de cables');


insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada) 
	values ('teceric', 'teceric', 8000, 0);
insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
	values ('garcables', 'garcables', 5000, 0);
insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
	values ('marcos', 'teceric', 420, 0);
insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
	values ('marcos', 'garcables', 500, 60);
insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
	values ('manuel', 'teceric', 320, 60);


insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision)
	values ('2021-01-30 19:52:33', 'teceric', 'marcos', 60, 15, true, 0.05);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision)
	values ('2021-02-16 16:48:02', 'teceric', 'marcos', 100, 20, true, 0.05);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision)
	values ('2021-02-18 18:55:54', 'garcables', 'garcables', 50, 10, true, 0.05);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision)
	values ('2021-03-14 14:16:10', 'teceric', 'manuel', 70, 15, true, 0.05);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision)
	values ('2021-03-20 12:16:31', 'garcables', 'marcos', 80, 25, true, 0.1);


insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
	values ('2021-02-02 17:32:41', '2021-01-30 19:52:33', 'marcos', 'manuel', 60);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
	values ('2021-02-17 11:15:03', '2021-02-16 16:48:02', 'marcos', 'manuel', 100);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
	values ('2021-02-18 19:10:44', '2021-02-18 18:55:54', 'garcables', 'manuel', 50);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
	values ('2021-03-24 12:01:50', '2021-03-14 14:16:10', 'manuel', 'marcos', 10);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
	values ('2021-03-25 12:16:29', '2021-03-20 12:16:31', 'marcos', 'manuel', 20);


insert into pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)
	values ('2021-01-15 10:00:00', 'teceric', 5, 0, null, 1, 0);
insert into pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)
	values ('2021-01-04 13:29:32', 'teceric', 3, 0, null, 1, 0);
insert into pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)
	values ('2021-05-29 18:00:00', 'garcables', 3, 4, '2021-04-18 11:14:21', 0.75, 0.25);
insert into pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)
	values ('2021-06-29 18:00:00', 'garcables', 6, 0, '2021-04-18 11:13:20', 1, 0);
insert into pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)
	values ('2021-06-04 16:00:00', 'teceric', 2, 2, '2021-04-16 09:32:47', 0.5, 0.5);

insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
	values ('marcos', '2021-01-15 10:00:00', 'teceric', 570);
insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
	values ('manuel', '2021-01-15 10:00:00', 'teceric', 230);
insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
	values ('manuel', '2021-01-04 13:29:32', 'teceric', 380);
insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
	values ('marcos', '2021-01-04 13:29:32', 'teceric', 420);