# --- !Ups

create table entries (
  id         bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  title      varchar(255) NOT NULL,
  content    longtext DEFAULT NULL,
  src        longtext DEFAULT NULL,
  dest       longtext DEFAULT NULL,
  img        longtext DEFAULT NULL,
  alt        longtext DEFAULT NULL,
  site       varchar(255) NOT NULL,
  tags       varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  updated_at datetime NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

drop table entries;
