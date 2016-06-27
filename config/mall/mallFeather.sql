#global
#select x1.mall_id,x1.groupId,0,log(x1.avgPrice+1),log(x1.score+1),log(x1.monthlyHits+1),x2.cat1,x2.cat2,x2.cat3
#from
#(
#select a.mall_id,b.shopId,a.area_id as groupId,
#case when avg(b.avgPrice) is null then 0 else CAST(avg(b.avgPrice)  as SIGNED) end as avgPrice,
#case when avg(b.score) is null then 0 else CAST(avg(b.score)  as SIGNED) end as score,
#case when avg(b.monthlyHits) is null then 0 else CAST(avg(b.monthlyHits) as SIGNED) end as monthlyHits
#from fangcheng_global.mall as a,BussinessInfo as b
#where a.spider_channel_web_id=b.shopId 
#group by b.shopId
#) as x1
#left join 
#(
#select b.mall_id,b.spider_channel_web_id,GROUP_CONCAT(DISTINCT c2.cat1) as cat1,GROUP_CONCAT(DISTINCT c2.cat2) as cat2,GROUP_CONCAT(DISTINCT c2.cat3) as cat3
#from fangcheng_global.fc_brand_shop as a,fangcheng_global.mall as b,
#(
#select b.spider_channel_web_id,a.category_one_id,count(1) from fangcheng_global.fc_brand_shop as a,
#fangcheng_global.mall as b
#where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
#group by b.spider_channel_web_id,a.category_one_id
#having count(1)>2
#) as c,BussinessInfo as c2
#where a.mall_id= b.mall_id
#and a.dp_id= c2.shopId
#and b.spider_channel_web_id=c.spider_channel_web_id
#and a.category_one_id=c.category_one_id
#and a.mall_id is not null 
#and a.dp_id is not null
#group by b.spider_channel_web_id
#order by  b.spider_channel_web_id
#) as x2
#on x1.shopId =x2.spider_channel_web_id
#local
#排除掉所有有id但是没有室内地图的mall
select y1.*,y2.brandMTendency
FROM
(
select x2.mall_id,x2.groupId,0,case when x1.avgPrice is null then 0 else x1.avgPrice end,
case when x1.score is null then 0 else x1.score end,
case when x1.monthlyHits is null then 0 else x1.monthlyHits end,x2.cat1,x2.cat2 from(
select a.mall_id,a.area_id as groupId,GROUP_CONCAT(distinct(b.category_one_id)) as cat1,GROUP_CONCAT(distinct(b.category_id)) as cat2
from fangcheng_global.mall as a,fangcheng_global.brand_shop as b
where a.mall_id=b.mall_id and a.mall_id is not null
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
where a.mall_id=b.mall_id and a.mall_id is not null
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
where b.brand_info_paizi_tendency <>"删除" and a.category_one_id is not null and a.mall_id=c.mall_id 
and a.brand_id=b.brand_id and a.mall_id is not null
group by a.mall_id
#order by a.mall_id
) as y2
on y1.mall_id =y2.mall_id