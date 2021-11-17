create table categories
(
    category_id bigint auto_increment
        primary key,
    title varchar(100) not null,
    image_url varchar(50) not null
);

create table users
(
    id bigint auto_increment
        primary key,
    email varchar(255) null,
    first_name varchar(255) null,
    last_name varchar(255) null,
    password varchar(255) null,
    role varchar(255) null
);

create table recipes
(
    recipe_id bigint auto_increment
        primary key,
    complexity bigint null,
    created datetime(6) null,
    description varchar(1500) null,
    image_url varchar(255) null,
    name varchar(255) null,
    time_to_cook bigint null,
    user_id bigint null,
    category_id bigint null,
    constraint FKlc3x6yty3xsupx80hqbj9ayos
        foreign key (user_id) references users (id)
);

create table ingredients
(
    recipe_id bigint not null,
    amount float null,
    name varchar(255) null,
    unit int null,
    constraint FK7p08vcn6wf7fd6qp79yy2jrwg
        foreign key (recipe_id) references recipes (recipe_id)
);

create table recipe_like
(
    user_id bigint not null,
    recipe_id bigint not null,
    primary key (user_id, recipe_id)
);





