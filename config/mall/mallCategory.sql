#mall对应的业态
#global
#select b.mall_id,a.category_one_id,count(1) from fangcheng_global.fc_brand_shop as a,
#fangcheng_global.mall as b
#where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
#group by b.spider_channel_web_id,a.category_one_id
#having count(1)>2;
#local
select a.mall_id,a.category_one_id,count(1) from fangcheng_global.brand_shop as a,
fangcheng_global.mall as b
where a.mall_id=b.mall_id and a.mall_id is not null and a.category_one_id is not null
group by b.spider_channel_web_id,a.category_one_id
having count(1)>2;