create table member
(
    id bigint       not null auto_increment,
    click_count bigint not null,
    name     varchar(255) not null,
    profile_image_url  varchar(255),
    created_at datetime not null,
    last_modified_at datetime not null,
    primary key (id)
) engine = InnoDB default charset utf8mb4;

alter table member
    add constraint uk_member_name unique (name);
