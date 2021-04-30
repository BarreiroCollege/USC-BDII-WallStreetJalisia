insert into superusuario(identificador)
values ('sociedadCorp');
insert into superusuario(identificador)
values ('eva');
insert into superusuario(identificador)
values ('pepePhone');
insert into superusuario(identificador)
values ('regulador');

insert into sociedad(identificador, saldo_comunal, tolerancia)
values ('sociedadCorp', 500, 2);

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('eva', 'sha1:64000:18:fRyIy2KpkOhXIkFl9+OCu6BPq32xY2+Y:d5j3M7amOsYqisxLDk6VbSL3', 'Calle del mar, 78', '15701', 'Santiago de Compostela', 678910344, 1000, 0.0,  null, null,'SEBID4GBR4WBHL5TMS2KYQRZ735UECJS','sociedadCorp', true);
insert into inversor (usuario, dni, nombre, apellidos)
values ('eva', '10024875E', 'Eva', 'Doural Méndez');

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('pepePhone', 'sha1:64000:18:0krmbeQFU13IagFzaqTMnsU+gBUchFH6:GyWDhmCGNAmArXe7PyP9uZlW', 'Calle Alta, 41', '17501', 'Santiago de Compostela', 648572333, 1000, 0.0,  null, null,'EXI2KZIUCCGMR4EGYXEJJEXDN2GNMIUD',null, false );
insert into empresa (usuario, cif, nombre)
values ('pepePhone', 'B-32552648', 'Pepe Phone');

insert into usuario(identificador,clave)
values('regulador','sha1:64000:18:fRyIy2KpkOhXIkFl9+OCu6BPq32xY2+Y:d5j3M7amOsYqisxLDk6VbSL3');
insert into regulador(usuario, comision)
values ('regulador',0.05);


insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
values ('pepePhone', 'pepePhone', 8000, 0);

insert into oferta_venta (fecha, empresa, usuario, num_participaciones, precio_venta, confirmado, comision, restantes)
values ('2021-01-30 19:52:33', 'pepePhone', 'sociedadCorp', 300, 5, true, 0.05, 300);

insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
values ('sociedadCorp', 'pepePhone', 600, 300);