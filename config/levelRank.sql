select x.cate_name,@rownum:=@rownum+1
FROM
(
select distinct(brand_info_paizi_tendency) as cate_name from fangcheng_global.brand_info_paizi as a
) as x,(select @rownum:=-1) as t;