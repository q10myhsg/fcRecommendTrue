select
a.mall_id,b.brand_id,a.brand_shop_name,a.category_one_id,c.shopId,c.userId,c.tags,a.area_id
from
fangcheng_global.brand_shop as a, 
(
#获取一个用户在不同brand的信息
select b.userId,a.brand_id from fangcheng_global.brand_shop as a,BussinessInfoComment as b 
where a.spider_channel_web_id=b.shopId and b.shopCount>1 
and a.mall_id is not null and a.spider_channel_web_id is not null and a.mall_id is not null 
#and b.userId>=5666
group by b.userId,a.brand_id
) as b,BussinessInfoComment as c
where c.userId=b.userId and a.spider_channel_web_id=c.shopId and a.brand_id=b.brand_id and a.mall_id is not null 