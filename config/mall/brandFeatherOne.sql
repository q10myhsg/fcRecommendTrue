#select y1.*,y2.brandMTendency
#from
#(
#select a.brand_id,a.area_id,a.category_one_id,
#case when avg(b.avgPrice) is null then 0 else CAST(log(avg(b.avgPrice)+1)  as SIGNED) end as avgPrice,
#case when avg(b.score) is null then 0 else CAST(log(avg(b.score)+1)  as SIGNED) end as score,
#case when avg(b.monthlyHits) is null then 0 else CAST(log(avg(b.monthlyHits)+1) as SIGNED) end as monthlyHits,
#GROUP_CONCAT(distinct(a.category_one_id)),b.cat2,b.cat3
#from fangcheng_global.brand_shop as a
#left join BussinessInfo as b
#on a.spider_channel_web_id=b.shopId
#where b.cat1 is not null and  a.category_one_id is not null and a.brand_id =?
#group by a.brand_id,a.area_id,a.category_one_id
##limit 10000
#) as y1
#left JOIN
#(
##获取mall对应的属性
#select a.brand_id,b.brandName,GROUP_CONCAT(distinct(b.brandMTendency)) as brandMTendency 
#from fangcheng_global.brand_shop as a, BrandPropertiyInfo as b
#where b.brandMtendency <>"删除" and a.category_one_id is not null and a.brand_id=b.brandId 
#and a.mall_id is not null and a.brand_id =?
#group by a.brand_id
#) as y2
#on y1.brand_id =y2.brand_id
#new
select y1.*,y2.brandMTendency
from
(
select a.brand_id,a.area_id,a.category_one_id,
case when avg(a.brand_shop_avg_price) is null then 0 else CAST(log(avg(a.brand_shop_avg_price+1))+1 as SIGNED) end,
CAST(log(avg(a.dp_score+1))+1 as SIGNED),CAST(log(avg(a.dp_index+1))+1 as SIGNED),GROUP_CONCAT(DISTINCT(a.category_one_id)),GROUP_CONCAT(DISTINCT(a.category_id))
from fangcheng_global.brand_shop as a
where a.area_id is not null and a.category_id is not null and a.brand_id=?
group by a.brand_id,a.area_id,a.category_one_id
)as y1
left JOIN
(
#获取mall对应的属性
select a.brand_id,a.brand_shop_name,GROUP_CONCAT(distinct(b.brand_info_paizi_tendency)) as brandMTendency 
from fangcheng_global.brand_shop as a, fangcheng_global.brand_info_paizi as b
where a.brand_id=b.brand_id and b.brand_info_paizi_tendency <>"删除" and a.category_one_id is not null and b.brand_id <>0 and a.mall_id is not null
and a.brand_id=?
group by a.brand_id
) as y2
on y1.brand_id =y2.brand_id