create table users (
    id varchar(36) not null,
    email varchar(255),
    phone_number varchar(255),
    preference varchar(255) not null,
    password varchar(28) not null,
    primary key (id)
)