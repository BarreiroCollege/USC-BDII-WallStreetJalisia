insert into superusuario(identificador) values('manuel');
insert into superusuario(identificador) values('marcos');
insert into superusuario(identificador) values('sara');
insert into superusuario(identificador) values('eva');
insert into superusuario(identificador) values('pepe');
insert into superusuario(identificador) values('alex');
insert into superusuario(identificador) values('sherlock');

insert into superusuario(identificador) values('teceric');
insert into superusuario(identificador) values('garcables');
insert into superusuario(identificador) values('fruluis');
insert into superusuario(identificador) values('libjaim');
insert into superusuario(identificador) values('zappaco');

insert into superusuario(identificador) values ('regulador');

insert into superusuario(identificador) values('aai');
insert into superusuario(identificador) values('hampa');
insert into superusuario(identificador) values('invemena');
insert into superusuario(identificador) values('ubicorp');
insert into superusuario(identificador) values('urrs');

insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('aai',0,2);
insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('hampa',0,2);
insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('invemena',0,2);
insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('ubicorp',0,2);
insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('urrs',0,2);

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('manuel', 'sha1:64000:18:SVe0FlPbMUEQY71rlvaYveyCUSgFXhLz:HOrb28zdabCt2xaWdl5fKyla', 'Calle del olmo, 27', '28012', 'Madrid', 617483290, 529.0, 0.0, null, null,'H3MSFZO7X7A3OKZOA4OYOJXBN3C4ED5Q','aai', true );
insert into inversor (usuario, dni, nombre, apellidos)
values ('manuel', '14782689F', 'Manuel', 'Iglesias Suárez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('marcos', 'sha1:64000:18:nt7r6x4PAJtz7hzGRYeWXNnMGyok+h++:d6zcs5/whc/aroAWWaSw1edA', 'Calle Abedul, 12', '28036', 'Madrid', 628714222, 1067.0, 0.0, null, null,'QZ3YM24E7KHVUIQTFNZIKZ2FIKNT6FTW','invemena', false);
insert into inversor (usuario, dni, nombre, apellidos)
values ('marcos', '47157847C', 'Marcos', 'Vázquez García');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('sara', 'sha1:64000:18:TyWsDXrMKgzwDIh+XC4YtvpeGJ94zgM7:ht05Ayvb+hpgwanuNJfaDK/O', 'Calle del Palomar, 31', '15701', 'Santiago de Compostela', 615237989, 513.15, 0.0, null, '2021-02-18 18:00:34','OJEAZFGLPKOAOHTM5ILNRTTQOL6DS6YP','urrs', false);
insert into inversor (usuario, dni, nombre, apellidos)
values ('sara', '20416592K', 'Sara', 'Sousa Álvarez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('eva', 'sha1:64000:18:3ncoaUfAAVLd+IG3Qv+j0PElWxtutDm4:qAGZjZUZM/AmmtK6jbD/kwhW', 'Calle del mar, 78', '15701', 'Santiago de Compostela', 678910344, 1485.30, 0.0, null, null,'SEBID4GBR4WBHL5TMS2KYQRZ735UECJS','hampa', true);
insert into inversor (usuario, dni, nombre, apellidos)
values ('eva', '10024875E', 'Eva', 'Doural Méndez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('pepe', 'sha1:64000:18:8W/C96qh9kWpqAwQExVLdhWvr7mJXJcO:z2BBAChRZJuSSryfMy1qtwA+', 'Calle del mar, 78', '15701', 'Santiago de Compostela', 678910344, 1485.30, 0.0, null, null,'U4NREHH3F4LJONAIBGOD3YT66CHVZFKR','urrs', true);
insert into inversor (usuario, dni, nombre, apellidos)
values ('pepe', '10024875E', 'Eva', 'Doural Méndez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('alex', 'sha1:64000:18:Aj0ahywTxknu45mSPx6WD2XojNkY7eox:0VDmaXTHXYzDIc8vCtO/qe4q', 'Gran Vía, 15', '15701', 'Santiago de Compostela', 648512326, 0.0, 0.0, '2021-01-01 12:51:42', null, null, null, false);
insert into inversor (usuario, dni, nombre, apellidos)
values ('alex', '95214724B', 'Álex', 'Castro Pérez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('sherlock', 'sha1:64000:18:DkxtFkqdJgthfzufgtXk2cUtgc0beHP7:gl6FXkP8ho9sIJkV19bVW2GU', 'Baker Street 221B', 'NW1', 'Londres', null, 0.0, 0.0, '2021-03-14 15:22:33', null, null, null, false);
insert into inversor (usuario, dni, nombre, apellidos)
values ('sherlock', '10011001S', 'Shelock', 'Holmes');

insert into usuario(identificador, clave)
values ('regulador', 'sha1:64000:18:fRyIy2KpkOhXIkFl9+OCu6BPq32xY2+Y:d5j3M7amOsYqisxLDk6VbSL3');
insert into regulador(usuario, comision, comision_sociedad)
values ('regulador', 0.05, 0.04);

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('teceric', 'sha1:64000:18:amY24vKwv1SJGpgjVpdLsWWx9qc69qMl:2AidJweG25zNE4orrhkVxkMF', 'Calle 10 de Marzo', '36210', 'Vigo', 986331724, 7012.0, 0.0, null, null,'GWI7QDYNDQZQ3BZU7I6WTGPCIK5S5Q75','aaI', false );
insert into empresa (usuario, cif, nombre)
values ('teceric', 'B-76365789', 'Teclados Eric');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('garcables', 'sha1:64000:18:66YcELkG9EpMakgQj8xzGHJANDIzW+Sm:yuMuFHuREUlSIyM6SlEg30tp', 'Avenida Ramón y Cajal, 79', '28016', 'Madrid', 677100888, 2410.0, 0.0, null, null,'UXIUSTUQRMMPN4UO2GNJIQWQPJENVLBM','invemena', true );
insert into empresa (usuario, cif, nombre)
values ('garcables', 'A-70012249', 'Garaje de cables');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('fruluis', 'sha1:64000:18:D1sx4K1DesoJlhcJdMmO6MSm8wGJfxsI:0p8rOhTfdBtyuah0bOF6II0Y', 'Camino sombrío, 14', '39212', 'Fontecha', 147025316, 1000.0, 0.0, null, null,'VTFQD7DQE2ZGTCIDKNBFZ3MNKWKE5NYO',null, false );
insert into empresa (usuario, cif, nombre)
values ('fruluis', 'A-48216592', 'Frutas Luis');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('libjaim', 'sha1:64000:18:F6FSExk6qPHHWVo0oJbFbYXzY5BSv9cb:LLFrrCJ9XXzmmtvJgpTHrMHR', 'Calle Baja, 14', '15701', 'Santiago de Compostela', 648572333, 200.0, 0.0, null, null,'P52ZHRYQK2VA7ZARQGJ34HBUKPZ5KRVW','urrs', false );
insert into empresa (usuario, cif, nombre)
values ('libjaim', 'P-19752648', 'Libros Jaime');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('zappaco', 'sha1:64000:18:OSzPTY/zeDfGLB2iz2qYB7cyTmISEryN:f1yCiK+grKN/F+BbiKn/QTq8', 'Calle Alta, 41', '17501', 'Santiago de Compostela', 648572333, 200.0, 0.0, null, null,'EXI2KZIUCCGMR4EGYXEJJEXDN2GNMIUD','ubicorp', true );
insert into empresa (usuario, cif, nombre)
values ('zappaco', 'B-32552648', 'Zapatos Paco');


insert into usuario(identificador,clave)
values('regulador','sha1:64000:18:uuqgRLKOUbYT/r2zESN5CWus/xOZWTM4:nyDuvYpnE7gFwOOHT1wJmDX8');
insert into regulador(usuario, comision)
values ('regulador',0.05);


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


insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-01-30 19:52:33', 'teceric', 'marcos', 60, 15, true, 0.05, 0);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-02-16 16:48:02', 'teceric', 'marcos', 100, 20, true, 0.05, 0);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-02-18 18:55:54', 'garcables', 'garcables', 50, 10, true, 0.05, 0);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-03-14 14:16:10', 'teceric', 'manuel', 70, 15, true, 0.05, 60);
insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-03-20 12:16:31', 'garcables', 'marcos', 80, 25, true, 0.1, 60);


insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
values ('2021-02-02 17:32:41', '2021-01-30 19:52:33', 'marcos', 'manuel', 30);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
values ('2021-02-17 11:15:03', '2021-02-16 16:48:02', 'marcos', 'manuel', 30);
insert into venta (fecha, ov_fecha, ov_usuario, usuario_compra, cantidad)
values ('2021-02-18 19:10:44', '2021-02-18 18:55:54', 'garcables', 'manuel', 30);
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
values ('sara', '2021-01-15 10:00:00', 'teceric', 380);
insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
values ('eva', '2021-01-15 10:00:00', 'teceric', 420);
insert into pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)
values ('pepe', '2021-01-15 10:00:00', 'teceric', 20);



insert into propuesta_compra(sociedad, fecha_inicio, cantidad, empresa, precio_max)
values('urrs', '2021-01-06 13:19:42', 20, 'fruluis', 50.0 );
insert into propuesta_compra(sociedad, fecha_inicio, cantidad, empresa, precio_max)
values('aai', '2021-01-15 10:00:00', 40, 'teceric', 68.40 );
insert into propuesta_compra(sociedad, fecha_inicio, cantidad, empresa, precio_max)
values('hampa','2021-01-14 10:00:00', 60, 'garcables', 78.50 );
insert into propuesta_compra(sociedad, fecha_inicio, cantidad, empresa, precio_max)
values('invemena', '2021-01-04 13:29:32', 2000, 'teceric', 3.80 );
insert into propuesta_compra(sociedad, fecha_inicio, cantidad, empresa, precio_max)
values('ubicorp', '2021-01-07 12:29:32', 4560, 'fruluis', 7.75 );


