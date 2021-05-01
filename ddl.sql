create table superusuario
(
    identificador varchar(16) not null
        constraint superusuario_pk
            primary key
);

alter table superusuario
    owner to postgres;

create table sociedad
(
    identificador varchar(16)                not null
        constraint sociedad_pk
            primary key
        constraint sociedad_superusuario_identificador_fk
            references superusuario
            on update cascade,
    saldo_comunal double precision default 0 not null,
    tolerancia    integer          default 0 not null
);

alter table sociedad
    owner to postgres;

create table usuario
(
    identificador   varchar(16)                    not null
        constraint usuario_pk
            primary key
        constraint usuario_superusuario_identificador_fk
            references superusuario
            on update cascade,
    clave           varchar(128)                   not null,
    direccion       varchar(64),
    cp              varchar(10),
    localidad       varchar(32),
    telefono        integer,
    saldo           double precision default 0     not null,
    saldo_bloqueado double precision default 0     not null,
    alta            timestamp,
    baja            timestamp,
    otp             varchar(32)      default NULL::character varying,
    sociedad        varchar(16)      default NULL::character varying
        constraint usuario_sociedad_identificador_fk
            references sociedad
            on update cascade,
    lider           boolean          default false not null
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
    fecha                           timestamp        default now() not null,
    empresa                         varchar(16)                    not null
        constraint pago_empresa_identificador_fk
            references empresa
            on update cascade,
    beneficio_por_participacion     double precision default 0     not null,
    participacion_por_participacion double precision default 0     not null,
    fecha_anuncio                   timestamp,
    porcentaje_beneficio            double precision default 0     not null,
    porcentaje_participacion        double precision default 0     not null,
    constraint pago_pk
        primary key (fecha, empresa)
);

alter table pago
    owner to postgres;

create table pago_usuario
(
    usuario             varchar(16) not null
        constraint pago_usuario_superusuario_identificador_fk
            references superusuario
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
    fecha               timestamp        default now() not null,
    empresa             varchar(16)                    not null
        constraint oferta_venta_empresa_identificador_fk
            references empresa
            on update cascade,
    usuario             varchar(16)                    not null
        constraint oferta_venta_superusuario_identificador_fk
            references superusuario
            on update cascade,
    num_participaciones integer                        not null,
    precio_venta        double precision               not null,
    confirmado          boolean          default false not null,
    comision            double precision default 0.05  not null,
    restantes           integer                        not null,
    constraint oferta_venta_pk
        primary key (fecha, usuario)
);

alter table oferta_venta
    owner to postgres;

create table participacion
(
    usuario            varchar(16)       not null
        constraint poseer_participacion_usuario_identificador_fk
            references superusuario
            on update cascade,
    empresa            varchar(16)       not null
        constraint poseer_participacion_empresa_identificador_fk
            references empresa
            on update cascade,
    cantidad           integer default 0 not null,
    cantidad_bloqueada integer default 0 not null,
    constraint poseer_participacion_pk
        primary key (usuario, empresa)
);

alter table participacion
    owner to postgres;

create table venta
(
    fecha          timestamp default now() not null,
    ov_fecha       timestamp               not null,
    ov_usuario     varchar(16)             not null,
    usuario_compra varchar(16)             not null
        constraint comprar_usuario_identificador_fk
            references superusuario
            on update cascade,
    cantidad       integer                 not null,
    constraint comprar_pk
        primary key (fecha, ov_fecha, ov_usuario, usuario_compra),
    constraint comprar_oferta_venta_fecha_anuncio_usuario_fk
        foreign key (ov_fecha, ov_usuario) references oferta_venta
            on update cascade
);

alter table venta
    owner to postgres;

create table propuesta_compra
(
    sociedad     varchar(16)             not null
        constraint propuesta_compra_sociedad_identificador_fk
            references sociedad
            on update cascade,
    fecha_inicio timestamp default now() not null,
    cantidad     integer                 not null,
    precio_max   double precision,
    empresa      varchar(16)             not null
        constraint propuesta_compra_empresa_usuario_fk
            references empresa
            on update cascade,
    constraint propuesta_compra_pk
        primary key (sociedad, fecha_inicio)
);

alter table propuesta_compra
    owner to postgres;

create table regulador
(
    usuario  varchar(16)                   not null
        constraint regulador_pk
            primary key
        constraint regulador_usuario_identificador_fk
            references usuario
            on update cascade,
    comision double precision default 0.05 not null
);

alter table regulador
    owner to postgres;


create view empresas_inversores_usuarios as
select m.identificador, m.saldo, i.usuario as usuario_inversor, i.dni, i.nombre, i.apellidos, m.usuario as usuario_empresa, m.cif, m.nombre as nombre_comercial
from (empresa e RIGHT JOIN usuario u ON e.usuario = u.identificador) as m LEFT JOIN inversor i ON m.identificador = i.usuario
where (m.usuario is not null or i.usuario is not null) and m.alta is null;

-- text no limita el número de caracteres máximo, a diferencia de varchar
create or replace function dato_regulador(atributo text) returns text as $dr$
declare
    dato text;
begin
    -- Solo algunos datos del regulador son accesibles
	select case
	    when (atributo = 'identificador') then identificador
	    when (atributo = 'saldo') then cast(saldo as text)
	    when (atributo = 'comision') then cast(comision as text)
	    else null
	end into dato
	from regulador r JOIN usuario u ON r.usuario = u.identificador;
	return dato;
end;
$dr$ Language plpgsql;

