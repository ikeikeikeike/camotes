# --- !Ups

create table entries (
  id         bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  title      varchar(255) NOT NULL,
  content    longtext DEFAULT NULL,
--  src        longtext DEFAULT NULL,
  src        varchar(255) DEFAULT NULL UNIQUE,
  dest       longtext DEFAULT NULL,
  duration   int(11)  DEFAULT NULL,
  img        longtext DEFAULT NULL,
  site       varchar(255) NOT NULL,
  tags       varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  updated_at datetime NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

drop table entries;
