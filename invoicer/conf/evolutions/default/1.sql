# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bank_account (
  id                        bigint not null,
  account_number            varchar(255) not null,
  bank                      varchar(255),
  iban                      varchar(255),
  bic                       varchar(255),
  account_type              integer not null,
  constraint ck_bank_account_account_type check (account_type in (0,1,2)),
  constraint pk_bank_account primary key (id))
;

create table client (
  id                        bigint not null,
  name                      varchar(255) not null,
  address                   varchar(255),
  postal_code               varchar(255),
  country                   varchar(255),
  org_number                varchar(255) not null,
  contact_person            varchar(255),
  email                     varchar(255),
  constraint uq_client_org_number unique (org_number),
  constraint pk_client primary key (id))
;

create table invoice (
  id                        bigint not null,
  title                     varchar(255),
  invoice_date              timestamp not null,
  due_date                  timestamp,
  date_paid                 timestamp,
  owner_id                  bigint,
  client_id                 bigint,
  constraint pk_invoice primary key (id))
;

create table user (
  id                        bigint not null,
  username                  varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  address                   varchar(255),
  postal_code               varchar(255),
  country                   varchar(255),
  organization_number       varchar(255),
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id))
;

create sequence bank_account_seq;

create sequence client_seq;

create sequence invoice_seq;

create sequence user_seq;

alter table invoice add constraint fk_invoice_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_invoice_owner_1 on invoice (owner_id);
alter table invoice add constraint fk_invoice_client_2 foreign key (client_id) references client (id) on delete restrict on update restrict;
create index ix_invoice_client_2 on invoice (client_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists bank_account;

drop table if exists client;

drop table if exists invoice;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists bank_account_seq;

drop sequence if exists client_seq;

drop sequence if exists invoice_seq;

drop sequence if exists user_seq;

