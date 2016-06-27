#城市映射
select case when  area_name = "北京" then 2 
when area_name ="上海" then 1 
when area_name ="广州" then 4 
when area_name ="南京" then 5 
when area_name ="深圳" then 7 
when area_name ="天津" then 10
when area_name ="保定" then 29 end
from(
select distinct(area_name) from fangcheng_global.mall as a
left join fangcheng_global.area as b
on a.area_id=b.area_id
where area_name is not null
) as c