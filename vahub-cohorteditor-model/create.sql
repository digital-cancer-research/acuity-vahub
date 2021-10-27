/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table saved_filter (id number(19,0) not null, created_date timestamp, name varchar2(255 char), operator varchar2(255 char), owner varchar2(255 char), primary key (id))
create table saved_filter_dataset (id number(19,0) not null, dataset_class varchar2(255 char), dataset_id number(19,0), saved_filter_id number(19,0), primary key (id))
create table saved_filter_instance (id number(19,0) not null, filterView varchar2(255 char), json long, type varchar2(255 char), saved_filter_id number(19,0), primary key (id))
create table saved_filter_permission (id number(19,0) not null, prid varchar2(255 char), saved_filter_id number(19,0), primary key (id))
alter table saved_filter_dataset add constraint FK_a6gt8q1ukatsfo2uxf6ffww4c foreign key (saved_filter_id) references saved_filter
alter table saved_filter_instance add constraint FK_3qxthlwg45lsw20292cvs6ec5 foreign key (saved_filter_id) references saved_filter
alter table saved_filter_permission add constraint FK_qrhac6qaaav8dscwmqki5jv1r foreign key (saved_filter_id) references saved_filter
create sequence hibernate_sequence
