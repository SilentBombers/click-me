create table click_count_history
(
    id bigint       not null auto_increment,
    name     varchar(255) not null,
    date     date not null,
    click_count bigint not null,
    primary key (id)
) engine = InnoDB default charset utf8mb4;

alter table click_count_history
    add constraint click_count_history_duplicate unique (name, date);

create index idx_click_count_history on click_count_history (name, date);
