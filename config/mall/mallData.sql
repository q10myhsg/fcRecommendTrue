#获取平台 mallid  对应城市 mall的分类  品牌分类 品牌id 喜好程度
#global
#select a.mall_id,b.area_id,0,a.category_one_id,a.brand_id,1 
#from fangcheng_global.fc_brand_shop as a,fangcheng_global.mall as b,
#(
#select b.spider_channel_web_id,a.category_one_id,count(1) from fangcheng_global.fc_brand_shop as a,
#fangcheng_global.mall as b
#where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
#group by b.spider_channel_web_id,a.category_one_id
#having count(1)>2
#) as c
#where a.mall_id= b.mall_id
#and b.spider_channel_web_id=c.spider_channel_web_id
#and a.category_one_id=c.category_one_id
#and a.mall_id is not null 
#order by  b.spider_channel_web_id
#local
select a.mall_id,b.area_id,0,a.category_one_id,a.brand_id,1 
from fangcheng_global.brand_shop as a,fangcheng_global.mall as b,
(
select b.mall_id,a.category_one_id,count(1) from fangcheng_global.brand_shop as a,
fangcheng_global.mall as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
group by b.mall_id,a.category_one_id
having count(1)>2
) as c
where a.mall_id= b.mall_id
and b.mall_id=c.mall_id
and a.category_one_id=c.category_one_id
and a.mall_id is not null 
order by  b.mall_id