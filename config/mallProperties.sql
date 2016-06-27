#获取mall对应的业态分布
#global
#select b.mall_id,c.category_one_id,GROUP_CONCAT(DISTINCT c2.cat2) as cat2
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
#group by b.spider_channel_web_id,c.category_one_id
#order by  b.spider_channel_web_id
#local
select a.mall_id,c.category_one_id,GROUP_CONCAT(DISTINCT c2.cat2) as cat2
from fangcheng_global.brand_shop as a,fangcheng_global.mall as b,
(
select b.spider_channel_web_id,a.category_one_id,count(1) from fangcheng_global.brand_shop as a,
fangcheng_global.mall as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
group by b.spider_channel_web_id,a.category_one_id
having count(1)>2
) as c,BussinessInfo as c2
where a.mall_id= b.mall_id
and a.spider_channel_web_id= c2.shopId
and b.spider_channel_web_id=c.spider_channel_web_id
and a.category_one_id=c.category_one_id
and a.mall_id is not null 
and a.spider_channel_web_id is not null
group by b.mall_id,c.category_one_id
order by  b.mall_id