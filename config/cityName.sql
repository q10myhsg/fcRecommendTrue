#使用的mall对应的城市名
select distinct(area_name) from fangcheng_global.mall as a
left join fangcheng_global.area as b
on a.area_id=b.area_id
where area_name is not null