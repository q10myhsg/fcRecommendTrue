#城市对应商圈
#global
#select area_id,GROUP_CONCAT(business_circle_id) from fangcheng_global.business_circle
#group by area_id;
#local#存在mall的商圈
select a.area_id,GROUP_CONCAT(distinct(a.business_circle_id)) 
from fangcheng_global.mall as a
where a.area_id>0
group by a.area_id;