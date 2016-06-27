#品牌所属商圈
select z.area_id,z.brand_id,GROUP_CONCAT(z2.business_circle_id)
from
(
select x.brand_id ,x.area_id,count(1) as count
FROM
(
select a.brand_id,a.area_id,a.mall_id from fangcheng_global.brand_shop as a
where a.mall_id is not null
group by a.brand_id,a.area_id,a.mall_id
) as x
group by x.brand_id,x.area_id
#having count>3
) as z
left join fangcheng_global.brand_shop as z1
on z.brand_id=z1.brand_id
left join fangcheng_global.mall as z2
on z1.mall_id=z2.mall_id
where z1.mall_id is not null and z2.business_circle_id is not null
group by z.area_id,z.brand_id