#获取一级业态
select category_id,category_name from fangcheng_global.category
where MOD(category_id,10000)=0 and length(category_id)=5