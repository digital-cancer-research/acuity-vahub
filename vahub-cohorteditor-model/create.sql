create table saved_filter (id number(19,0) not null, created_date timestamp, name varchar2(255 char), operator varchar2(255 char), owner varchar2(255 char), primary key (id))
create table saved_filter_dataset (id number(19,0) not null, dataset_class varchar2(255 char), dataset_id number(19,0), saved_filter_id number(19,0), primary key (id))
create table saved_filter_instance (id number(19,0) not null, filterView varchar2(255 char), json long, type varchar2(255 char), saved_filter_id number(19,0), primary key (id))
create table saved_filter_permission (id number(19,0) not null, prid varchar2(255 char), saved_filter_id number(19,0), primary key (id))
alter table saved_filter_dataset add constraint FK_a6gt8q1ukatsfo2uxf6ffww4c foreign key (saved_filter_id) references saved_filter
alter table saved_filter_instance add constraint FK_3qxthlwg45lsw20292cvs6ec5 foreign key (saved_filter_id) references saved_filter
alter table saved_filter_permission add constraint FK_qrhac6qaaav8dscwmqki5jv1r foreign key (saved_filter_id) references saved_filter
create sequence hibernate_sequence
