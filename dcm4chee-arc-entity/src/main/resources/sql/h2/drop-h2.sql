alter table content_item drop constraint FK_gudw6viy7lrf5t5hetw7mbgh5 if exists;
alter table content_item drop constraint FK_pyrd1nhijag5ct0ee9xqq4h78 if exists;
alter table content_item drop constraint FK_9kpe6whsov3ur9rph4ih2vi5a if exists;
alter table global_subscription drop constraint FK_f1l196ykcnh7s2pwo6qnmltw7 if exists;
alter table hl7psu_task drop constraint FK_pev4urgkk7id2h1ijhv8domjx if exists;
alter table ian_task drop constraint FK_1fuh251le2hid2byw90hd1mly if exists;
alter table instance drop constraint FK_jxfu47kwjk3kkkyrwewjw8a4n if exists;
alter table instance drop constraint FK_7w6f9bi2w91qr2abl6ddxnrwq if exists;
alter table instance drop constraint FK_s4tgrew4sh4qxoupmk3gihtrk if exists;
alter table instance_req drop constraint FK_cqmmps9maltjybl44t4cck404 if exists;
alter table instance_req drop constraint FK_47n586hkafgp9m1etqohgfybl if exists;
alter table location drop constraint FK_hjtlcwsvwihs4khh961d423e7 if exists;
alter table location drop constraint FK_bfk5vl6eoxaf0hhwiu3rbgmkn if exists;
alter table mpps drop constraint FK_o49fec996jvdo31o7ysmsn9s2 if exists;
alter table mpps drop constraint FK_nrigpgue611m33rao03nnfe5l if exists;
alter table mpps drop constraint FK_ofg0lvfxad6r5oigw3y6da27u if exists;
alter table mwl_item drop constraint FK_6qj8tkh6ib9w2pjqwvqe23ko if exists;
alter table mwl_item drop constraint FK_t4vpsywvy0axeutmdgc0ye3nk if exists;
alter table mwl_item drop constraint FK_hqecoo67sflk190dxyc0hnf0c if exists;
alter table mwl_item drop constraint FK_vkxtls2wr17wgxnxj7b2fe32 if exists;
alter table mwl_item drop constraint FK_44qwwvs50lgpog2cqmicxgt1f if exists;
alter table patient drop constraint FK_5lgndn3gn7iug3kuewiy9q124 if exists;
alter table patient drop constraint FK_sk77bwjgaoetvy1pbcgqf08g if exists;
alter table patient drop constraint FK_39gahcxyursxfxe2ucextr65s if exists;
alter table patient drop constraint FK_rj42ffdtimnrcwmqqlvj24gi2 if exists;
alter table patient drop constraint FK_56r2g5ggptqgcvb3hl11adke2 if exists;
alter table rejected_instance drop constraint FK_iafiq2ugv5rd6fonwd0vd7xdx if exists;
alter table rel_study_pcode drop constraint FK_fryhnb2ppb6fcop3jrrfwvnfy if exists;
alter table rel_study_pcode drop constraint FK_mnahh8fh77d365m6w2x4x3f4q if exists;
alter table rel_task_dicomattrs drop constraint FK_e0gtunmen48q8imxggunt7gt7 if exists;
alter table rel_task_dicomattrs drop constraint FK_pwaoih2f4ay4c00avvt79de7h if exists;
alter table rel_ups_perf_code drop constraint FK_6asj28yy5se9mp443b6ryefd2 if exists;
alter table rel_ups_perf_code drop constraint FK_6m06tt8ku376qxkro94xpteus if exists;
alter table rel_ups_station_class_code drop constraint FK_q26e06qk9gwviwe2ug0f86doa if exists;
alter table rel_ups_station_class_code drop constraint FK_e1ioaswm010jlsq6kl7y3um1c if exists;
alter table rel_ups_station_location_code drop constraint FK_kl60ab0k5c1p8qii9ya16424x if exists;
alter table rel_ups_station_location_code drop constraint FK_9f0l4glqwpq12d11w9osd475m if exists;
alter table rel_ups_station_name_code drop constraint FK_jtv4r8f88f6gfte0fa36w5y9o if exists;
alter table rel_ups_station_name_code drop constraint FK_8jf5xe8ot2yammv3ksd5xrgif if exists;
alter table series drop constraint FK_bdj2kuutidekc2en6dckev7l6 if exists;
alter table series drop constraint FK_oiq81nulcmtg6p85iu31igtf5 if exists;
alter table series drop constraint FK_avp2oeuufo8axv5j184cchrop if exists;
alter table series drop constraint FK_pu4p7k1o9hleuk9rmxvw2ybj6 if exists;
alter table series drop constraint FK_5n4bxxb2xa7bvvq26ao7wihky if exists;
alter table series drop constraint FK_1og1krtgxfh207rtqjg0r7pbd if exists;
alter table series_query_attrs drop constraint FK_eiwosf6pcc97n6y282cv1n54k if exists;
alter table series_req drop constraint FK_bcn0jtvurqutw865pwp34pejb if exists;
alter table series_req drop constraint FK_bdkjk6ww0ulrb0nhf41c7liwt if exists;
alter table soundex_code drop constraint FK_dh7lahwi99hk6bttrk75x4ckc if exists;
alter table sps_station_aet drop constraint FK_js5xqyw5qa9rpttwmck14duow if exists;
alter table study drop constraint FK_5w0oynbw061mwu1rr9mrb6kj4 if exists;
alter table study drop constraint FK_e3fdaqhw7u60trs5aspf4sna9 if exists;
alter table study drop constraint FK_49eet5qgcsb32ktsqrf1mj3x2 if exists;
alter table study_query_attrs drop constraint FK_sxccj81423w8o6w2tsb7nshy9 if exists;
alter table subscription drop constraint FK_jadcs2aho4ijh639r67qgk0g0 if exists;
alter table ups drop constraint FK_3frtpy5cstsoxk5jxw9cutr33 if exists;
alter table ups drop constraint FK_8xiqdli1p8cyw1y4hwyqhimcx if exists;
alter table ups drop constraint FK_1retecpk22a2tysmi5o6xcpbh if exists;
alter table ups_req drop constraint FK_kocdb2pxx2fymu1modhneb4xm if exists;
alter table ups_req drop constraint FK_7vt6m05r0hertks2fcngd5wn1 if exists;
alter table verify_observer drop constraint FK_105wt9hglqsmtnoxgma9x18vj if exists;
alter table verify_observer drop constraint FK_qjgrn9rfyyt6sv14utk9ijcfq if exists;
drop table code if exists;
drop table content_item if exists;
drop table dicomattrs if exists;
drop table global_subscription if exists;
drop table hl7psu_task if exists;
drop table ian_task if exists;
drop table id_sequence if exists;
drop table instance if exists;
drop table instance_req if exists;
drop table key_value2 if exists;
drop table location if exists;
drop table metadata if exists;
drop table mpps if exists;
drop table mwl_item if exists;
drop table patient if exists;
drop table patient_demographics if exists;
drop table patient_id if exists;
drop table person_name if exists;
drop table rejected_instance if exists;
drop table rel_study_pcode if exists;
drop table rel_task_dicomattrs if exists;
drop table rel_ups_perf_code if exists;
drop table rel_ups_station_class_code if exists;
drop table rel_ups_station_location_code if exists;
drop table rel_ups_station_name_code if exists;
drop table series if exists;
drop table series_query_attrs if exists;
drop table series_req if exists;
drop table soundex_code if exists;
drop table sps_station_aet if exists;
drop table stgcmt_result if exists;
drop table study if exists;
drop table study_query_attrs if exists;
drop table subscription if exists;
drop table task if exists;
drop table uidmap if exists;
drop table ups if exists;
drop table ups_req if exists;
drop table verify_observer if exists;
