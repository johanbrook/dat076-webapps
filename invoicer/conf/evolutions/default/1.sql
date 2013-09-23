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
  constraint pk_invoice primary key (id))
;

create sequence invoice_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists invoice;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists invoice_seq;

