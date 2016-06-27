select o.category_one_id,GROUP_CONCAT(o.cat2)
from
(
select x.category_one_id,y.cat2
from
(
select a.category_one_id,a.spider_channel_web_id from fangcheng_global.brand_shop as a
where a.mall_id is not null and a.category_one_id is not NULL and a.spider_channel_web_id is not null
group by  a.category_one_id,a.spider_channel_web_id
) as x,BussinessInfo as y
where x.spider_channel_web_id=y.shopId and cat2 is not null and cat2 <> "其他"
group by x.category_one_id,y.cat2
) as o
group by o.category_one_id