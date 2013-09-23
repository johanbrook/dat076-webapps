# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table invoice (
  id                        bigint not null,
  title                     varchar(255),
  invoice_date              timestamp,
  due_date                  timestamp,
  date_paid                 timestamp,
  is_paid                   boolean,
  owner_id                  bigint,
  constraint pk_invoice primary key (id))
;

create table user (
  id                        bigint not null,
  login                     varchar(255),
  password                  varchar(255),
  constraint uq_user_login unique (login),
  constraint pk_user primary key (id))
;

create sequence invoice_seq;

create sequence user_seq;

alter table invoice add constraint fk_invoice_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_invoice_owner_1 on invoice (owner_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists invoice;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists invoice_seq;

drop sequence if exists user_seq;

