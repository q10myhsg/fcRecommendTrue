select x.cate_name,@rownum:=@rownum+1
FROM
(
select distinct(b.category_id_parent) as cate_name from
fangcheng_global.category as b
) as x,(select @rownum:=-1) as t;