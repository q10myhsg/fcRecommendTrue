#地区代码 对应城市名
select a.area_id,area_name 
from fangcheng_global.area as a,fangcheng_global.mall as b
where a.area_id=b.area_id
group by a.area_id,area_name 
union all
select 0,"全部";