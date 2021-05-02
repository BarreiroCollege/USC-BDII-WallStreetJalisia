insert into superusuario(identificador) values('manuel');
insert into superusuario(identificador) values('marcos');

insert into superusuario(identificador) values('teceric');
insert into superusuario(identificador) values('garcables');


insert into superusuario(identificador) values ('regulador');

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('manuel', 'sha1:64000:18:F2U5YujAQE/zrtslV/iaAic4zEbdgvo9:MzOnoDFkQ4ZJBEgi/pNQqIWa', 'Calle del olmo, 27', '28012', 'Madrid', 617483290, 529.0, 0.0, null, null,'H3MSFZO7X7A3OKZOA4OYOJXBN3C4ED5Q',null, false );
insert into inversor (usuario, dni, nombre, apellidos)
values ('manuel', '14782689F', 'Manuel', 'Iglesias Suárez');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('marcos', 'sha1:64000:18:F2U5YujAQE/zrtslV/iaAic4zEbdgvo9:MzOnoDFkQ4ZJBEgi/pNQqIWa', 'Calle Abedul, 12', '28036', 'Madrid', 628714222, 1067.0, 0.0, null, null,'QZ3YM24E7KHVUIQTFNZIKZ2FIKNT6FTW',null, false);
insert into inversor (usuario, dni, nombre, apellidos)
values ('marcos', '47157847C', 'Marcos', 'Vázquez García');

insert into usuario(identificador, clave)
values ('regulador', 'sha1:64000:18:F2U5YujAQE/zrtslV/iaAic4zEbdgvo9:MzOnoDFkQ4ZJBEgi/pNQqIWa');
insert into regulador(usuario, comision, comision_sociedad)
values ('regulador', 0.05, 0.04);

insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('teceric', 'sha1:64000:18:F2U5YujAQE/zrtslV/iaAic4zEbdgvo9:MzOnoDFkQ4ZJBEgi/pNQqIWa', 'Calle 10 de Marzo', '36210', 'Vigo', 986331724, 7012.0, 0.0, null, null,'GWI7QDYNDQZQ3BZU7I6WTGPCIK5S5Q75',null, false );
insert into empresa (usuario, cif, nombre)
values ('teceric', 'B-76365789', 'Teclados Eric');
insert into usuario (identificador, clave, direccion, cp, localidad, telefono, saldo, saldo_bloqueado, alta, baja, otp, sociedad, lider)
values ('garcables', 'sha1:64000:18:F2U5YujAQE/zrtslV/iaAic4zEbdgvo9:MzOnoDFkQ4ZJBEgi/pNQqIWa', 'Avenida Ramón y Cajal, 79', '28016', 'Madrid', 677100888, 2410.0, 0.0, null, null,'UXIUSTUQRMMPN4UO2GNJIQWQPJENVLBM',null,false);
insert into empresa (usuario, cif, nombre)
values ('garcables', 'A-70012249', 'Garaje de cables');

insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
values ('teceric', 'teceric', 8000, 0);
insert into participacion (usuario, empresa, cantidad, cantidad_bloqueada)
values ('garcables', 'garcables', 5000, 0);
