#品牌对应mall
#global
#select c.brand_id,b.area_id,a.category_one_id,a.category_one_id,c.mall_id,1 
#from fangcheng_global.fc_brand_shop as a,fangcheng_global.mall as b,
#(
#select a.brand_id,a.mall_id,b.spider_channel_web_id,a.category_one_id,count(1) from fangcheng_global.fc_brand_shop as a,
#fangcheng_global.mall as b
#where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
#group by a.brand_id,a.mall_id,a.category_one_id
#order by a.brand_id,a.category_one_id,a.mall_id
#) as c
#where a.mall_id= b.mall_id
#and b.spider_channel_web_id=c.spider_channel_web_id
#and a.category_one_id=c.category_one_id
#and a.mall_id is not null 
#order by  c.brand_id
#limit 2000;
#local 需要合并其他省市没有的品牌的
#是所有 mall的品牌
select c.brand_id,b.area_id,a.category_one_id,a.category_one_id,c.mall_id,1 
from fangcheng_global.brand_shop as a,fangcheng_global.mall as b,
(
select a.brand_id,a.mall_id,a.category_one_id,count(1) 
from fangcheng_global.brand_shop as a,
fangcheng_global.mall as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
group by a.brand_id,a.mall_id,a.category_one_id
#order by a.brand_id,a.category_one_id,a.mall_id
) as c
where a.mall_id= b.mall_id
and a.brand_id=c.brand_id
and b.mall_id=c.mall_id
and a.category_one_id=c.category_one_id
and a.mall_id is not null 
#order by  c.brand_id
#limit 1200;
UNION ALL
select a.brand_id,b.area_id,a.category_one_id,a.category_one_id,null,null
from(
#所有的品牌 及业态
select a.brand_id,a.category_one_id from fangcheng_global.brand_shop as a,
fangcheng_global.mall as b
where a.mall_id=b.mall_id and a.category_one_id is not null
group by a.brand_id,a.category_one_id) as a
left JOIN(select distinct(area_id) from fangcheng_global.brand_shop as a where area_id is not null) as b on 1=1