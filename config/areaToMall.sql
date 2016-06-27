#商圈对应的mall
#global
#select a.business_circle_id,a.mall_id from fangcheng_global.mall as a
#local
select a.business_circle_id,a.mall_id from fangcheng_global.mall as a
where mall_status =1 and business_circle_id is not null and a.business_circle_id <> 0;