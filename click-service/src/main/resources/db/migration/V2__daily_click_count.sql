create table daily_click_count
(
    id bigint       not null auto_increment,
    name     varchar(255) not null,
    date     date not null,
    click_count bigint not null,
    primary key (id)
) engine = InnoDB default charset utf8mb4;

alter table daily_click_count
    add constraint daily_click_count_duplicate unique (name, date);

create index idx_daily_click_count on daily_click_count (nickname, date);
