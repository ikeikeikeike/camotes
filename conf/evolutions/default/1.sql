# --- !Ups

create table entries (
  id int NOT NULL AUTO_INCREMENT,
  url TEXT NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

drop table entries;
