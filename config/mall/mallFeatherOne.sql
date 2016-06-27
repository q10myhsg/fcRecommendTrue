select y1.*,y2.brandMTendency
FROM
(
select x2.mall_id,x2.groupId,0,case when x1.avgPrice is null then 0 else x1.avgPrice end,
case when x1.score is null then 0 else x1.score end,
case when x1.monthlyHits is null then 0 else x1.monthlyHits end,x2.cat1,x2.cat2 from(
select a.mall_id,a.area_id as groupId,GROUP_CONCAT(distinct(b.category_one_id)) as cat1,GROUP_CONCAT(distinct(b.category_id)) as cat2
from fangcheng_global.mall as a,fangcheng_global.brand_shop as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.mall_id =?
group by a.mall_id,b.area_id
) as x2
left join (
select z1.mall_id,z1.groupId,CAST(log(z1.avgPrice+1)+1  as SIGNED) as avgPrice,CAST(log(z1.score+1)+1  as SIGNED) as score,
CAST(log(z1.monthlyHits+1)+1  as SIGNED) as monthlyHits from(
select a.mall_id,b.brand_id,a.area_id as groupId,
avg(b.brand_shop_avg_price) as avgPrice,
avg(b.dp_score) as score,
avg(b.dp_index) as monthlyHits
from fangcheng_global.mall as a,fangcheng_global.brand_shop as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.mall_id =?
group by a.mall_id,b.brand_id
having score <>0 and monthlyHits <>0 and avgPrice is not null
) as z1
group by z1.mall_id,z1.groupId
) as x1
on x1.mall_id=x2.mall_id and x1.groupId=x2.groupId
) as y1
left JOIN
(
#获取mall对应的属性
select a.mall_id,c.mall_name,GROUP_CONCAT(distinct(b.brand_info_paizi_tendency)) as brandMTendency 
from fangcheng_global.brand_shop as a, fangcheng_global.brand_info_paizi as b,
fangcheng_global.mall as c
where b.brand_info_paizi_tendency <>"删除" and a.category_one_id is not null and a.mall_id=c.mall_id and a.mall_id =?
and a.brand_id=b.brand_id and a.mall_id is not null
group by a.mall_id
#order by a.mall_id
) as y2
on y1.mall_id =y2.mall_id