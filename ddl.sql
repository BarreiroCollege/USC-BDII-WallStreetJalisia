create table usuario
(
    identificador   varchar(16)                    not null
        constraint usuario_pk
            primary key,
    clave           varchar(128)                   not null,
    direccion       varchar(64),
    cp              varchar(10),
    localidad       varchar(16),
    telefono        integer,
    saldo           double precision               not null,
    saldo_bloqueado double precision default 0     not null,
    activo          boolean          default false not null
);

alter table usuario
    owner to postgres;

create table inversor
(
    usuario   varchar(16) not null
        constraint inversor_pk
            primary key
        constraint inversor_usuario_identificador_fk
            references usuario
            on update cascade,
    dni       varchar(16) not null,
    nombre    varchar(16) not null,
    apellidos varchar(32) not null
);

alter table inversor
    owner to postgres;

create table empresa
(
    usuario varchar(16) not null
        constraint empresa_pk
            primary key
        constraint empresa_usuario_identificador_fk
            references usuario
            on update cascade,
    cif     varchar(16) not null,
    nombre  varchar(32) not null
);

alter table empresa
    owner to postgres;

create table pago
(
    fecha                       timestamp default now() not null,
    empresa                     varchar(16)             not null
        constraint pago_empresa_identificador_fk
            references empresa
            on update cascade,
    beneficio_por_participacion double precision        not null,
    fecha_anuncio               timestamp,
    constraint pago_pk
        primary key (fecha, empresa)
);

alter table pago
    owner to postgres;

create table pago_usuario
(
    usuario             varchar(16) not null
        constraint pago_usuario_usuario_identificador_fk
            references usuario
            on update cascade,
    pago_fecha          timestamp   not null,
    pago_empresa        varchar(16) not null,
    num_participaciones integer     not null,
    constraint pago_usuario_pk
        primary key (usuario, pago_fecha, pago_empresa),
    constraint pago_usuario_pago_fecha_pago_empresa_fk
        foreign key (pago_fecha, pago_empresa) references pago
            on update cascade
);

alter table pago_usuario
    owner to postgres;

create table oferta_venta
(
    fecha               timestamp default now() not null,
    empresa             varchar(16)             not null
        constraint oferta_venta_empresa_identificador_fk
            references empresa
            on update cascade,
    usuario             varchar(16)             not null
        constraint oferta_venta_usuario_identificador_fk
            references usuario
            on update cascade,
    num_participaciones integer                 not null,
    precio_venta        double precision        not null,
    confirmado          boolean   default false not null,
    constraint oferta_venta_pk
        primary key (fecha, usuario)
);

alter table oferta_venta
    owner to postgres;

create table participacion
(
    usuario  varchar(16)       not null
        constraint poseer_participacion_usuario_identificador_fk
            references usuario
            on update cascade,
    empresa  varchar(16)       not null
        constraint poseer_participacion_empresa_identificador_fk
            references empresa
            on update cascade,
    cantidad integer default 0 not null,
    cantidad_bloqueada integer default 0 not null,
    constraint poseer_participacion_pk
        primary key (usuario, empresa)
);

alter table participacion
    owner to postgres;

create table compra
(
    fecha          timestamp        default now() not null,
    ov_fecha       timestamp                      not null,
    ov_usuario     varchar(16)                    not null,
    usuario_compra varchar(16)                    not null
        constraint comprar_usuario_identificador_fk
            references usuario
            on update cascade,
    cantidad       integer                        not null,
    comision       double precision default 0.05  not null,
    constraint comprar_pk
        primary key (fecha, ov_fecha, ov_usuario, usuario_compra),
    constraint comprar_oferta_venta_fecha_anuncio_usuario_fk
        foreign key (ov_fecha, ov_usuario) references oferta_venta
            on update cascade
);

alter table compra
    owner to postgres;


create or replace function participaciones_por_vender(ofert_ven_fecha timestamp, usuario_oferta varchar(16))
    returns integer as $restante$

declare restante integer;

begin
    select (num_participaciones - coalesce(
            (select sum(cantidad)
			from compra
			where ov_fecha = ofert_ven_fecha and ov_usuario = usuario_oferta),0))
			into restante
    from oferta_venta
    where usuario = usuario_oferta and fecha = ofert_ven_fecha;
    return restante;
end;
$restante$ language plpgsql;
