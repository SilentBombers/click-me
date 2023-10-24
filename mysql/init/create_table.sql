create table member
(
    id          bigint       not null auto_increment,
    nickname    varchar(255) not null,
    click_count bigint       not null,
    primary key (id)
) engine=InnoDB default charset utf8mb4;

alter table member
    add constraint uk_member_nickname unique (nickname);
