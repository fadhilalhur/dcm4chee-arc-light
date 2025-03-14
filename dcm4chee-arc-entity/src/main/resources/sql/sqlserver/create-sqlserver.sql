create table code (pk bigint identity not null, code_meaning varchar(255) not null, code_value varchar(255) not null, code_designator varchar(255) not null, code_version varchar(255) not null, primary key (pk));
create table content_item (pk bigint identity not null, rel_type varchar(255) not null, text_value varchar(255), code_fk bigint, name_fk bigint not null, instance_fk bigint, primary key (pk));
create table dicomattrs (pk bigint identity not null, attrs varbinary(MAX) not null, primary key (pk));
create table global_subscription (pk bigint identity not null, deletion_lock bit not null, subscriber_aet varchar(255) not null, matchkeys_fk bigint, primary key (pk));
create table hl7psu_task (pk bigint identity not null, accession_no varchar(255), aet varchar(255) not null, created_time datetime2 not null, device_name varchar(255) not null, pps_status int, scheduled_time datetime2, series_iuid varchar(255), study_iuid varchar(255), mpps_fk bigint, primary key (pk));
create table ian_task (pk bigint identity not null, calling_aet varchar(255) not null, device_name varchar(255) not null, ian_dests varchar(255) not null, scheduled_time datetime2, study_iuid varchar(255), mpps_fk bigint, primary key (pk));
create table id_sequence (name varchar(255) not null, next_value int not null, version bigint, primary key (name));
create table instance (pk bigint identity not null, availability int not null, sr_complete varchar(255) not null, content_date varchar(255) not null, content_time varchar(255) not null, created_time datetime2 not null, ext_retrieve_aet varchar(255), inst_custom1 varchar(255) not null, inst_custom2 varchar(255) not null, inst_custom3 varchar(255) not null, inst_no int, num_frames int, retrieve_aets varchar(255), sop_cuid varchar(255) not null, sop_iuid varchar(255) not null, updated_time datetime2 not null, sr_verified varchar(255) not null, version bigint, dicomattrs_fk bigint not null, srcode_fk bigint, series_fk bigint not null, primary key (pk));
create table instance_req (pk bigint identity not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), req_proc_id varchar(255) not null, req_service varchar(255) not null, sps_id varchar(255) not null, study_iuid varchar(255) not null, req_phys_name_fk bigint, instance_fk bigint, primary key (pk));
create table key_value2 (pk bigint identity not null, content_type varchar(255) not null, created_time datetime2 not null, key_name varchar(255) not null, updated_time datetime2 not null, username varchar(255), key_value varchar(4000) not null, primary key (pk));
create table location (pk bigint identity not null, created_time datetime2 not null, digest varchar(255), multi_ref int, object_type int not null, object_size bigint not null, status int not null, storage_id varchar(255) not null, storage_path varchar(255) not null, tsuid varchar(255), uidmap_fk bigint, instance_fk bigint, primary key (pk));
create table metadata (pk bigint identity not null, created_time datetime2 not null, digest varchar(255), object_size bigint not null, status int not null, storage_id varchar(255) not null, storage_path varchar(255) not null, primary key (pk));
create table mpps (pk bigint identity not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), created_time datetime2 not null, pps_start_date varchar(255) not null, pps_start_time varchar(255) not null, sop_iuid varchar(255) not null, pps_status int not null, study_iuid varchar(255) not null, updated_time datetime2 not null, version bigint, dicomattrs_fk bigint not null, discreason_code_fk bigint, patient_fk bigint not null, primary key (pk));
create table mwl_item (pk bigint identity not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), admission_id varchar(255) not null, admid_entity_id varchar(255), admid_entity_uid varchar(255), admid_entity_uid_type varchar(255), created_time datetime2 not null, institution varchar(255) not null, department varchar(255) not null, modality varchar(255) not null, req_proc_id varchar(255) not null, sps_id varchar(255) not null, sps_start_date varchar(255) not null, sps_start_time varchar(255) not null, sps_status int not null, study_iuid varchar(255) not null, updated_time datetime2 not null, version bigint, worklist_label varchar(255) not null, dicomattrs_fk bigint not null, inst_code_fk bigint, dept_code_fk bigint, patient_fk bigint not null, perf_phys_name_fk bigint, primary key (pk));
create table patient (pk bigint identity not null, created_time datetime2 not null, failed_verifications int not null, num_studies int not null, pat_birthdate varchar(255) not null, pat_custom1 varchar(255) not null, pat_custom2 varchar(255) not null, pat_custom3 varchar(255) not null, pat_sex varchar(255) not null, updated_time datetime2 not null, verification_status int not null, verification_time datetime2, version bigint, dicomattrs_fk bigint not null, merge_fk bigint, patient_id_fk bigint, pat_name_fk bigint, resp_person_fk bigint, primary key (pk));
create table patient_demographics (pat_id varchar(255) not null, pat_birthdate varchar(255), pat_name varchar(255), pat_sex varchar(255), primary key (pat_id));
create table patient_id (pk bigint identity not null, pat_id varchar(255) not null, pat_id_type_code varchar(255), entity_id varchar(255), entity_uid varchar(255), entity_uid_type varchar(255), version bigint, primary key (pk));
create table person_name (pk bigint identity not null, alphabetic_name varchar(255) not null, ideographic_name varchar(255) not null, phonetic_name varchar(255) not null, primary key (pk));
create table rejected_instance (pk bigint identity not null, created_time datetime2 not null, series_iuid varchar(255) not null, sop_cuid varchar(255) not null, sop_iuid varchar(255) not null, study_iuid varchar(255) not null, reject_code_fk bigint, primary key (pk));
create table rel_study_pcode (study_fk bigint not null, pcode_fk bigint not null);
create table rel_task_dicomattrs (task_fk bigint not null, dicomattrs_fk bigint not null);
create table rel_ups_perf_code (ups_fk bigint not null, perf_code_fk bigint not null);
create table rel_ups_station_class_code (ups_fk bigint not null, station_class_code_fk bigint not null);
create table rel_ups_station_location_code (ups_fk bigint not null, station_location_code_fk bigint not null);
create table rel_ups_station_name_code (ups_fk bigint not null, station_name_code_fk bigint not null);
create table series (pk bigint identity not null, body_part varchar(255) not null, completeness int not null, compress_failures int not null, compress_params varchar(255), compress_time datetime2, compress_tsuid varchar(255), created_time datetime2 not null, expiration_date varchar(255), expiration_exporter_id varchar(255), expiration_state int not null, ext_retrieve_aet varchar(255), failed_retrieves int not null, stgver_failures int not null, inst_purge_state int not null, inst_purge_time datetime2, institution varchar(255) not null, department varchar(255) not null, laterality varchar(255) not null, metadata_update_time datetime2, metadata_update_failures int not null, modality varchar(255) not null, modified_time datetime2 not null, pps_cuid varchar(255) not null, pps_iuid varchar(255) not null, pps_start_date varchar(255) not null, pps_start_time varchar(255) not null, receiving_aet varchar(255), receiving_hl7_app varchar(255), receiving_hl7_facility varchar(255), receiving_pres_addr varchar(255), rejection_state int not null, sending_aet varchar(255), sending_hl7_app varchar(255), sending_hl7_facility varchar(255), sending_pres_addr varchar(255), series_custom1 varchar(255) not null, series_custom2 varchar(255) not null, series_custom3 varchar(255) not null, series_desc varchar(255) not null, series_iuid varchar(255) not null, series_no int, series_size bigint not null, sop_cuid varchar(255) not null, station_name varchar(255) not null, stgver_time datetime2, tsuid varchar(255) not null, updated_time datetime2 not null, version bigint, dicomattrs_fk bigint not null, inst_code_fk bigint, dept_code_fk bigint, metadata_fk bigint, perf_phys_name_fk bigint, study_fk bigint not null, primary key (pk));
create table series_query_attrs (pk bigint identity not null, availability int, num_instances int, retrieve_aets varchar(255), cuids_in_series varchar(255), view_id varchar(255), series_fk bigint not null, primary key (pk));
create table series_req (pk bigint identity not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), req_proc_id varchar(255) not null, req_service varchar(255) not null, sps_id varchar(255) not null, study_iuid varchar(255) not null, req_phys_name_fk bigint, series_fk bigint, primary key (pk));
create table soundex_code (pk bigint identity not null, sx_code_value varchar(255) not null, sx_pn_comp_part int not null, sx_pn_comp int not null, person_name_fk bigint not null, primary key (pk));
create table sps_station_aet (mwl_item_fk bigint not null, station_aet varchar(255));
create table stgcmt_result (pk bigint identity not null, batch_id varchar(255), created_time datetime2 not null, device_name varchar(255) not null, exporter_id varchar(255), num_failures int, num_instances int, series_iuid varchar(255), sop_iuid varchar(255), stgcmt_status int not null, study_iuid varchar(255) not null, task_fk bigint, transaction_uid varchar(255) not null, updated_time datetime2 not null, primary key (pk));
create table study (pk bigint identity not null, access_control_id varchar(255) not null, access_time datetime2 not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), admission_id varchar(255) not null, admid_entity_id varchar(255), admid_entity_uid varchar(255), admid_entity_uid_type varchar(255), completeness int not null, created_time datetime2 not null, expiration_date varchar(255), expiration_exporter_id varchar(255), expiration_state int not null, ext_retrieve_aet varchar(255) not null, failed_retrieves int not null, modified_time datetime2 not null, rejection_state int not null, study_size bigint not null, storage_ids varchar(255), study_custom1 varchar(255) not null, study_custom2 varchar(255) not null, study_custom3 varchar(255) not null, study_date varchar(255) not null, study_desc varchar(255) not null, study_id varchar(255) not null, study_iuid varchar(255) not null, study_time varchar(255) not null, updated_time datetime2 not null, version bigint, dicomattrs_fk bigint not null, patient_fk bigint not null, ref_phys_name_fk bigint, primary key (pk));
create table study_query_attrs (pk bigint identity not null, availability int, mods_in_study varchar(255), num_instances int, num_series int, retrieve_aets varchar(255), cuids_in_study varchar(4000), view_id varchar(255), study_fk bigint not null, primary key (pk));
create table subscription (pk bigint identity not null, deletion_lock bit not null, subscriber_aet varchar(255) not null, ups_fk bigint not null, primary key (pk));
create table task (pk bigint identity not null, batch_id varchar(255), check_different bit, check_missing bit, compare_fields varchar(255), completed int, created_time datetime2 not null, destination_aet varchar(255), device_name varchar(255) not null, different int not null, error_comment varchar(255), error_msg varchar(255), exporter_id varchar(255), failed int, local_aet varchar(255), matches int not null, missing int not null, modalities varchar(255), num_failures int not null, num_instances int, outcome_msg varchar(255), payload varbinary(MAX), proc_end_time datetime2, proc_start_time datetime2, query_str varchar(255), queue_name varchar(255) not null, remaining int, remote_aet varchar(255), rq_uri varchar(4000), rq_host varchar(255), rq_user_id varchar(255), scheduled_time datetime2 not null, series_iuid varchar(255), sop_iuid varchar(255), task_status int not null, status_code int, storage_ids varchar(255), stgcmt_policy int, study_iuid varchar(255), task_type int not null, update_location_status bit, updated_time datetime2 not null, version bigint, warning int not null, primary key (pk));
create table uidmap (pk bigint identity not null, uidmap varbinary(MAX) not null, primary key (pk));
create table ups (pk bigint identity not null, admission_id varchar(255) not null, admid_entity_id varchar(255), admid_entity_uid varchar(255), admid_entity_uid_type varchar(255), created_time datetime2 not null, expected_end_date_time varchar(255) not null, input_readiness_state int not null, performer_aet varchar(255), ups_state int not null, replaced_iuid varchar(255) not null, expiration_date_time varchar(255) not null, start_date_time varchar(255) not null, transaction_iuid varchar(255), updated_time datetime2 not null, ups_iuid varchar(255) not null, ups_label varchar(255) not null, ups_priority int not null, version bigint, worklist_label varchar(255) not null, dicomattrs_fk bigint not null, patient_fk bigint not null, ups_code_fk bigint, primary key (pk));
create table ups_req (pk bigint identity not null, accession_no varchar(255) not null, accno_entity_id varchar(255), accno_entity_uid varchar(255), accno_entity_uid_type varchar(255), req_proc_id varchar(255) not null, req_service varchar(255) not null, study_iuid varchar(255) not null, req_phys_name_fk bigint, ups_fk bigint, primary key (pk));
create table verify_observer (pk bigint identity not null, verify_datetime varchar(255) not null, observer_name_fk bigint, instance_fk bigint, primary key (pk));
alter table code add constraint UK_sb4oc9lkns36wswku831c33w6  unique (code_value, code_designator, code_version);
create index UK_i715nk4mi378f9bxflvfroa5a on content_item (rel_type);
create index UK_6iism30y000w85v649ju968sv on content_item (text_value);
alter table global_subscription add constraint UK_4n26cxir6d3tksb2cd1kd86ch  unique (subscriber_aet);
alter table hl7psu_task add constraint UK_1t3jge4o2fl1byp3y8ljmkb3m  unique (study_iuid, pps_status);
create index UK_t0y05h07d9dagn9a4a9s4a5a4 on hl7psu_task (device_name);
alter table ian_task add constraint UK_dq88edcjjxh7h92f89y5ueast  unique (study_iuid);
create index UK_5shiir23exao1xpy2n5gvasrh on ian_task (device_name);
alter table instance add constraint UK_jxfu47kwjk3kkkyrwewjw8a4n  unique (dicomattrs_fk);
alter table instance add constraint UK_247lgirehl8i2vuanyfjnuyjb  unique (series_fk, sop_iuid);
create index UK_eg0khesxr81gdimwhjiyrylw7 on instance (sop_iuid);
create index UK_dglm8ndp9y9i0uo6fgaa5rhbb on instance (sop_cuid);
create index UK_ouh6caecancvsa05lknojy30j on instance (inst_no);
create index UK_5ikkfk17vijvsvtyied2xa225 on instance (content_date);
create index UK_pck1ovyd4t96mjkbbw6f8jiam on instance (content_time);
create index UK_qh8jqpe8ulsb5t7iv24scho00 on instance (sr_verified);
create index UK_gisd09x31lphi4437hwgh7ihg on instance (sr_complete);
create index UK_fncb1s641rrnoek7j47k0j06n on instance (inst_custom1);
create index UK_rr1ro1oxv6s4riib9hjkcuvuo on instance (inst_custom2);
create index UK_q5i0hxt1iyahxjiroux2h8imm on instance (inst_custom3);
create index UK_cqpv94ky100d0eguhrxpyplmv on instance_req (accession_no);
create index UK_n32ktg5h9xc1ex9x8g69w1s10 on instance_req (req_service);
create index UK_7pudwdgrg9wwc73wo65hpg517 on instance_req (req_proc_id);
create index UK_43h9ogidkcnex0e14q6u0c3jn on instance_req (sps_id);
create index UK_1typgaxhn4d0pt1f0vlp18wvb on instance_req (study_iuid);
alter table key_value2 add constraint UK_4gq7ksl296rsm6ap9hjrogv3g  unique (key_name);
create index UK_hy6xxbt6wi79kbxt6wsqhv77p on key_value2 (username);
create index UK_5gcbr7nnck6dxrmml1s3arpna on key_value2 (updated_time);
create index UK_r3oh859i9osv3aluoc8dcx9wk on location (storage_id, status);
create index UK_i1lnahmehau3r3j9pdyxg3p3y on location (multi_ref);
create index UK_f7c9hmq8pfypohkgkp5vkbhxp on metadata (storage_id, status);
alter table mpps add constraint UK_o49fec996jvdo31o7ysmsn9s2  unique (dicomattrs_fk);
alter table mpps add constraint UK_cyqglxijg7kebbj6oj821yx4d  unique (sop_iuid);
alter table mwl_item add constraint UK_6qj8tkh6ib9w2pjqwvqe23ko  unique (dicomattrs_fk);
alter table mwl_item add constraint UK_lerlqlaghhcs0oaj5irux4qig  unique (study_iuid, sps_id);
create index UK_d0v5hjn1crha2nqbws4wj0yoj on mwl_item (updated_time);
create index UK_88bqeff7thxsmgcmtrg5l3td on mwl_item (worklist_label);
create index UK_2odo3oah39o400thy9bf0rgv0 on mwl_item (sps_id);
create index UK_kedi0qimmvs83af3jxk471uxn on mwl_item (req_proc_id);
create index UK_fpfq8q514gsime2dl8oo773d4 on mwl_item (study_iuid);
create index UK_pw8h1b4sac2sr9estyqr82pcf on mwl_item (accession_no);
create index UK_tlkw80b7pbutfj19vh6et2vs7 on mwl_item (admission_id);
create index UK_8qkftk7n30hla3v1frep9vg2q on mwl_item (institution);
create index UK_ksy3uy0rvpis1sqqeojlet7lb on mwl_item (department);
create index UK_q28149iaxebyt3de2h5sm2bgl on mwl_item (modality);
create index UK_9oh3yd4prp9sfys4n0p2kd69y on mwl_item (sps_start_date);
create index UK_m20xnkg1iqetifvuegehbhekm on mwl_item (sps_start_time);
create index UK_3oigo76r1a7et491bkci96km8 on mwl_item (sps_status);
alter table patient add constraint UK_5lgndn3gn7iug3kuewiy9q124  unique (dicomattrs_fk);
alter table patient add constraint UK_39gahcxyursxfxe2ucextr65s  unique (patient_id_fk);
create index UK_e7rsyrt9n2mccyv1fcd2s6ikv on patient (verification_status);
create index UK_bay8wkvwegw3pmyeypv2v93k1 on patient (verification_time);
create index UK_296rccryifu6d8byisl2f4dvq on patient (num_studies);
create index UK_1ho1jyofty54ip8aqpuhi4mu1 on patient (pat_birthdate);
create index UK_545wp9un24fhgcy2lcfu1o04y on patient (pat_sex);
create index UK_9f2m2lkijm7wi0hpjsime069n on patient (pat_custom1);
create index UK_dwp6no1c4624yii6sbo59fedg on patient (pat_custom2);
create index UK_3ioui3yamjf01yny98bliqfgs on patient (pat_custom3);
create index UK_tkyjkkxxhnr0fem7m0h3844jk on patient_id (pat_id);
create index UK_d1sdyupb0vwvx23jownjnyy72 on patient_id (entity_id);
create index UK_m2jq6xe87vegohf6g10t5ptew on patient_id (entity_uid, entity_uid_type);
create index UK_gs2yshbwu0gkd33yxyv13keoh on person_name (alphabetic_name);
create index UK_ala4l4egord8i2tjvjidoqd1s on person_name (ideographic_name);
create index UK_9nr8ddkp8enufvbn72esyw3n1 on person_name (phonetic_name);
alter table rejected_instance add constraint UK_6liqevdmi0spifxf2vrh18wkp  unique (study_iuid, series_iuid, sop_iuid);
create index UK_owm55at56tdjitsncsrhr93xj on rejected_instance (created_time);
alter table rel_task_dicomattrs add constraint UK_e0gtunmen48q8imxggunt7gt7  unique (dicomattrs_fk);
alter table series add constraint UK_bdj2kuutidekc2en6dckev7l6  unique (dicomattrs_fk);
alter table series add constraint UK_83y2fx8cou17h3xggxspgikna  unique (study_fk, series_iuid);
create index UK_9fi64g5jjycg9dp24jjk5txg1 on series (series_iuid);
create index UK_jlgy9ifvqak4g2bxkchismw8x on series (rejection_state);
create index UK_75oc6w5ootkuwyvmrhe3tbown on series (series_no);
create index UK_b126hub0dc1o9dqp6awoispx2 on series (modality);
create index UK_mrn00m45lkq1xbehmbw5d9jbl on series (sop_cuid);
create index UK_tahx0q1ejidnsam40ans7oecx on series (tsuid);
create index UK_pq1yi70ftxhh391lhcq3e08nf on series (station_name);
create index UK_rvlxc150uexwmr1l9ojp8fgd on series (pps_start_date);
create index UK_amr00xwlatxewgj1sjp5mnf76 on series (pps_start_time);
create index UK_gwp46ofa26am9ohhccq1ohdj on series (body_part);
create index UK_tbdrfrmkmifsqhpf43065jrbs on series (laterality);
create index UK_achxn1rtfm3fbkkswlsyr75t0 on series (series_desc);
create index UK_82qea56c0kdhod3b1wu8wbrny on series (institution);
create index UK_bqu32v5v76p4qi0etptnrm0pc on series (department);
create index UK_hm39592a9n7m54dgso17irlhv on series (series_custom1);
create index UK_q3wayt2ke25fdcghaohhrjiu7 on series (series_custom2);
create index UK_d8b8irasiw8eh9tsigmwkbvae on series (series_custom3);
create index UK_b9e2bptvail8xnmb62h30h4d2 on series (sending_aet);
create index UK_lnck3a2qjo1vc430n1sy51vbr on series (receiving_aet);
create index UK_gxun7s005k8qf7qwhjhkkkkng on series (sending_pres_addr);
create index UK_e15a6qnq8jcq931agc2v48nvt on series (receiving_pres_addr);
create index UK_ffpftwfkijejj09tlbxr7u5g8 on series (sending_hl7_app);
create index UK_1e4aqxc5w1557hr3fb3lqm2qb on series (sending_hl7_facility);
create index UK_gj0bxgi55bhjic9s3i4dp2aee on series (receiving_hl7_app);
create index UK_pbay159cdhwbtjvlmel6d6em2 on series (receiving_hl7_facility);
create index UK_ih49lthl3udoca5opvgsdcerj on series (expiration_state);
create index UK_ta3pi6exqft5encv389hwjytw on series (expiration_date);
create index UK_j6aadbh7u93bpmv18s1inrl1r on series (failed_retrieves);
create index UK_4lnegvfs65fbkjn7nmg9s8usy on series (completeness);
create index UK_hwkcpd7yv0nca7o918wm4bn69 on series (metadata_update_time);
create index UK_6xqpk4cvy49wj41p2qwixro8w on series (metadata_update_failures);
create index UK_a8vyikwd972jomyb3f6brcfh5 on series (inst_purge_time);
create index UK_er4ife08f6eaki91gt3hxt5e on series (inst_purge_state);
create index UK_ftv3ijh2ud6ogoknneyqc6t9i on series (stgver_time);
create index UK_s1vceb8cu9c45j0q8tbldgol9 on series (stgver_failures);
create index UK_38mfgfnjhan2yhnwqtcrawe4 on series (compress_time);
create index UK_889438ocqfrvybu3k2eo65lpa on series (compress_failures);
alter table series_query_attrs add constraint UK_t1uhb1suiiqffhsv9eaopeevs  unique (view_id, series_fk);
create index UK_m4wanupyq3yldxgh3pbo7t68h on series_req (accession_no);
create index UK_l1fg3crmk6pjeu1x36e25h6p4 on series_req (req_service);
create index UK_p9w1wg4031w6y66w4xhx1ffay on series_req (req_proc_id);
create index UK_4uq79j30ind90jjs68gb24j6e on series_req (sps_id);
create index UK_crnpneoalwq25p795xtrhbx2 on series_req (study_iuid);
create index UK_fjwlo6vk0gxp78eh2i7j04a5t on soundex_code (sx_pn_comp);
create index UK_nlv8hnjxmb7pobdfl094ud14u on soundex_code (sx_pn_comp_part);
create index UK_3dxkqfajcytiwjjb5rgh4nu1l on soundex_code (sx_code_value);
create index UK_tm93u8kuxnasoguns5asgdx4a on sps_station_aet (station_aet);
alter table stgcmt_result add constraint UK_ey6qpep2qtiwayou7pd0vj22w  unique (transaction_uid);
create index UK_qko59fn9pb87j1eu070ilfkhm on stgcmt_result (updated_time);
create index UK_7ltjgxoijy15rrwihl8euv7vh on stgcmt_result (device_name);
create index UK_gu96kxnbf2p84d1katepo0btq on stgcmt_result (exporter_id);
create index UK_j292rvji1d7hintidhgkkcbpw on stgcmt_result (task_fk);
create index UK_f718gnu5js0mdg39q6j7fklia on stgcmt_result (batch_id);
create index UK_p65blcj4h0uh2itb0bp49mc07 on stgcmt_result (study_iuid);
create index UK_nyoefler7agcmxc8t8yfngq7e on stgcmt_result (stgcmt_status);
alter table study add constraint UK_5w0oynbw061mwu1rr9mrb6kj4  unique (dicomattrs_fk);
alter table study add constraint UK_pt5qn20x278wb1f7p2t3lcxv  unique (study_iuid);
create index UK_q8k2sl3kjl18qg1nr19l47tl1 on study (access_time);
create index UK_6ry2squ4qcv129lxpae1oy93m on study (created_time);
create index UK_24av2ewa70e7cykl340n63aqd on study (access_control_id);
create index UK_hwu9omd369ju3nufufxd3vof2 on study (rejection_state);
create index UK_fypbtohf5skbd3bkyd792a6dt on study (storage_ids);
create index UK_a1rewlmf8uxfgshk25f6uawx2 on study (study_date);
create index UK_16t2xvj9ttyvbwh1ijeve01ii on study (study_time);
create index UK_2ofn5q0fdfc6941e5j34bplmv on study (accession_no);
create index UK_n5froudmhk14pbhgors43xi68 on study (admission_id);
create index UK_j3q7fkhhiu4bolglyve3lv385 on study (study_desc);
create index UK_ksy103xef0hokd33y8ux7afxl on study (study_custom1);
create index UK_j63d3ip6x4xslkmyks1l89aay on study (study_custom2);
create index UK_8xolm3oljt08cuheepwq3fls7 on study (study_custom3);
create index UK_fyasyw3wco6hoj2entr7l6d09 on study (expiration_state);
create index UK_mlk5pdi8une92kru8g2ppappx on study (expiration_date);
create index UK_9qvng5j8xnli8yif7p0rjngb2 on study (failed_retrieves);
create index UK_gl5rq54a0tr8nreu27c2t04rb on study (completeness);
create index UK_cl9dmi0kb97ov1cjh7rn3dhve on study (ext_retrieve_aet);
create index UK_q7vxiaj1q6ojfxdq1g9jjxgqv on study (study_size);
alter table study_query_attrs add constraint UK_prn4qt6d42stw0gfi1yce1fap  unique (view_id, study_fk);
alter table subscription add constraint UK_co8q5hn46dehb35qsrtwyys96  unique (subscriber_aet, ups_fk);
create index UK_m47ruxpag7pq4gtn12lc63yfe on task (device_name);
create index UK_r2bcfyreh4n9h392iik1aa6sh on task (queue_name);
create index UK_a582by7kuyuhk8hi41tkelhrw on task (task_type);
create index UK_7y5ucdiygunyg2nh7qrs70e7k on task (task_status);
create index UK_76hkd9mjludoohse4g0ru1mg8 on task (created_time);
create index UK_9htwq4ofarp6m88r3ao0grt8j on task (updated_time);
create index UK_xwqht1afwe7k27iulvggnwwl on task (scheduled_time);
create index UK_k6dxmm1gu6u23xq03hbk80m4r on task (batch_id);
create index UK_17gcm1xo6fkujauguyjfxfb2k on task (local_aet);
create index UK_81xi6wnv5b10x3723fxt5bmew on task (remote_aet);
create index UK_f7c43c242ybnvcn3o50lrcpkh on task (destination_aet);
create index UK_pknlk8ggf8lnq38lq3gacvvpt on task (check_missing);
create index UK_1lchdfbbwkjbg7a6coy5t8iq7 on task (check_different);
create index UK_ow0nufrtniev7nkh7d0uv5mxe on task (compare_fields);
create index UK_6a0y0rsssms4mtm9bpkw8vgl6 on task (study_iuid, series_iuid, sop_iuid);
alter table ups add constraint UK_3frtpy5cstsoxk5jxw9cutr33  unique (dicomattrs_fk);
alter table ups add constraint UK_qck03rlxht9myv77sc79a480t  unique (ups_iuid);
create index UK_1umoxe7ig9n21q885mncxeq9 on ups (updated_time);
create index UK_kgwfwmxj3i0n7c404uvhsav1g on ups (ups_priority);
create index UK_d3ejkrtcim0q3cbwpq4n9skes on ups (ups_label);
create index UK_7e44lxlg0m2l2wfdo3k2tec7o on ups (worklist_label);
create index UK_kh194du6g35fi5l80vbj18nnp on ups (start_date_time);
create index UK_e57ifctiig366oq9mhab8law3 on ups (expiration_date_time);
create index UK_1hdr3ml1rwugwkmo3eks4no5p on ups (expected_end_date_time);
create index UK_brtgc3vpnoaq1xm80m568r16y on ups (input_readiness_state);
create index UK_sqoo5rr8pu2qe4gtdne3xh031 on ups (admission_id);
create index UK_crl67piqoxiccp3i6ckktphdd on ups (replaced_iuid);
create index UK_c8obxmqpdcy37r3pjga2pukac on ups (ups_state);
create index UK_rfium2ybikqm1f4xmi24mnv4u on ups_req (accession_no);
create index UK_emsk27nclko11ph2tcj5vk7hg on ups_req (req_service);
create index UK_524vr0q4c0kvyjwov74eru44x on ups_req (req_proc_id);
create index UK_hf0tly8umknn77civcsi0tdih on ups_req (study_iuid);
create index UK_5btv5autls384ulwues8lym4p on verify_observer (verify_datetime);
alter table content_item add constraint FK_gudw6viy7lrf5t5hetw7mbgh5 foreign key (code_fk) references code;
alter table content_item add constraint FK_pyrd1nhijag5ct0ee9xqq4h78 foreign key (name_fk) references code;
alter table content_item add constraint FK_9kpe6whsov3ur9rph4ih2vi5a foreign key (instance_fk) references instance;
alter table global_subscription add constraint FK_f1l196ykcnh7s2pwo6qnmltw7 foreign key (matchkeys_fk) references dicomattrs;
alter table hl7psu_task add constraint FK_pev4urgkk7id2h1ijhv8domjx foreign key (mpps_fk) references mpps;
alter table ian_task add constraint FK_1fuh251le2hid2byw90hd1mly foreign key (mpps_fk) references mpps;
alter table instance add constraint FK_jxfu47kwjk3kkkyrwewjw8a4n foreign key (dicomattrs_fk) references dicomattrs;
alter table instance add constraint FK_7w6f9bi2w91qr2abl6ddxnrwq foreign key (srcode_fk) references code;
alter table instance add constraint FK_s4tgrew4sh4qxoupmk3gihtrk foreign key (series_fk) references series;
alter table instance_req add constraint FK_cqmmps9maltjybl44t4cck404 foreign key (req_phys_name_fk) references person_name;
alter table instance_req add constraint FK_47n586hkafgp9m1etqohgfybl foreign key (instance_fk) references instance;
alter table location add constraint FK_hjtlcwsvwihs4khh961d423e7 foreign key (instance_fk) references instance;
alter table location add constraint FK_bfk5vl6eoxaf0hhwiu3rbgmkn foreign key (uidmap_fk) references uidmap;
alter table mpps add constraint FK_o49fec996jvdo31o7ysmsn9s2 foreign key (dicomattrs_fk) references dicomattrs;
alter table mpps add constraint FK_nrigpgue611m33rao03nnfe5l foreign key (discreason_code_fk) references code;
alter table mpps add constraint FK_ofg0lvfxad6r5oigw3y6da27u foreign key (patient_fk) references patient;
alter table mwl_item add constraint FK_6qj8tkh6ib9w2pjqwvqe23ko foreign key (dicomattrs_fk) references dicomattrs;
alter table mwl_item add constraint FK_t4vpsywvy0axeutmdgc0ye3nk foreign key (inst_code_fk) references code;
alter table mwl_item add constraint FK_hqecoo67sflk190dxyc0hnf0c foreign key (dept_code_fk) references code;
alter table mwl_item add constraint FK_vkxtls2wr17wgxnxj7b2fe32 foreign key (patient_fk) references patient;
alter table mwl_item add constraint FK_44qwwvs50lgpog2cqmicxgt1f foreign key (perf_phys_name_fk) references person_name;
alter table patient add constraint FK_5lgndn3gn7iug3kuewiy9q124 foreign key (dicomattrs_fk) references dicomattrs;
alter table patient add constraint FK_sk77bwjgaoetvy1pbcgqf08g foreign key (merge_fk) references patient;
alter table patient add constraint FK_39gahcxyursxfxe2ucextr65s foreign key (patient_id_fk) references patient_id;
alter table patient add constraint FK_rj42ffdtimnrcwmqqlvj24gi2 foreign key (pat_name_fk) references person_name;
alter table patient add constraint FK_56r2g5ggptqgcvb3hl11adke2 foreign key (resp_person_fk) references person_name;
alter table rejected_instance add constraint FK_iafiq2ugv5rd6fonwd0vd7xdx foreign key (reject_code_fk) references code;
alter table rel_study_pcode add constraint FK_fryhnb2ppb6fcop3jrrfwvnfy foreign key (pcode_fk) references code;
alter table rel_study_pcode add constraint FK_mnahh8fh77d365m6w2x4x3f4q foreign key (study_fk) references study;
alter table rel_task_dicomattrs add constraint FK_e0gtunmen48q8imxggunt7gt7 foreign key (dicomattrs_fk) references dicomattrs;
alter table rel_task_dicomattrs add constraint FK_pwaoih2f4ay4c00avvt79de7h foreign key (task_fk) references task;
alter table rel_ups_perf_code add constraint FK_6asj28yy5se9mp443b6ryefd2 foreign key (perf_code_fk) references code;
alter table rel_ups_perf_code add constraint FK_6m06tt8ku376qxkro94xpteus foreign key (ups_fk) references ups;
alter table rel_ups_station_class_code add constraint FK_q26e06qk9gwviwe2ug0f86doa foreign key (station_class_code_fk) references code;
alter table rel_ups_station_class_code add constraint FK_e1ioaswm010jlsq6kl7y3um1c foreign key (ups_fk) references ups;
alter table rel_ups_station_location_code add constraint FK_kl60ab0k5c1p8qii9ya16424x foreign key (station_location_code_fk) references code;
alter table rel_ups_station_location_code add constraint FK_9f0l4glqwpq12d11w9osd475m foreign key (ups_fk) references ups;
alter table rel_ups_station_name_code add constraint FK_jtv4r8f88f6gfte0fa36w5y9o foreign key (station_name_code_fk) references code;
alter table rel_ups_station_name_code add constraint FK_8jf5xe8ot2yammv3ksd5xrgif foreign key (ups_fk) references ups;
alter table series add constraint FK_bdj2kuutidekc2en6dckev7l6 foreign key (dicomattrs_fk) references dicomattrs;
alter table series add constraint FK_oiq81nulcmtg6p85iu31igtf5 foreign key (inst_code_fk) references code;
alter table series add constraint FK_avp2oeuufo8axv5j184cchrop foreign key (dept_code_fk) references code;
alter table series add constraint FK_pu4p7k1o9hleuk9rmxvw2ybj6 foreign key (metadata_fk) references metadata;
alter table series add constraint FK_5n4bxxb2xa7bvvq26ao7wihky foreign key (perf_phys_name_fk) references person_name;
alter table series add constraint FK_1og1krtgxfh207rtqjg0r7pbd foreign key (study_fk) references study;
alter table series_query_attrs add constraint FK_eiwosf6pcc97n6y282cv1n54k foreign key (series_fk) references series;
alter table series_req add constraint FK_bcn0jtvurqutw865pwp34pejb foreign key (req_phys_name_fk) references person_name;
alter table series_req add constraint FK_bdkjk6ww0ulrb0nhf41c7liwt foreign key (series_fk) references series;
alter table soundex_code add constraint FK_dh7lahwi99hk6bttrk75x4ckc foreign key (person_name_fk) references person_name;
alter table sps_station_aet add constraint FK_js5xqyw5qa9rpttwmck14duow foreign key (mwl_item_fk) references mwl_item;
alter table study add constraint FK_5w0oynbw061mwu1rr9mrb6kj4 foreign key (dicomattrs_fk) references dicomattrs;
alter table study add constraint FK_e3fdaqhw7u60trs5aspf4sna9 foreign key (patient_fk) references patient;
alter table study add constraint FK_49eet5qgcsb32ktsqrf1mj3x2 foreign key (ref_phys_name_fk) references person_name;
alter table study_query_attrs add constraint FK_sxccj81423w8o6w2tsb7nshy9 foreign key (study_fk) references study;
alter table subscription add constraint FK_jadcs2aho4ijh639r67qgk0g0 foreign key (ups_fk) references ups;
alter table ups add constraint FK_3frtpy5cstsoxk5jxw9cutr33 foreign key (dicomattrs_fk) references dicomattrs;
alter table ups add constraint FK_8xiqdli1p8cyw1y4hwyqhimcx foreign key (patient_fk) references patient;
alter table ups add constraint FK_1retecpk22a2tysmi5o6xcpbh foreign key (ups_code_fk) references code;
alter table ups_req add constraint FK_kocdb2pxx2fymu1modhneb4xm foreign key (req_phys_name_fk) references person_name;
alter table ups_req add constraint FK_7vt6m05r0hertks2fcngd5wn1 foreign key (ups_fk) references ups;
alter table verify_observer add constraint FK_105wt9hglqsmtnoxgma9x18vj foreign key (observer_name_fk) references person_name;
alter table verify_observer add constraint FK_qjgrn9rfyyt6sv14utk9ijcfq foreign key (instance_fk) references instance;
