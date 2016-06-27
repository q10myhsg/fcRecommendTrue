select x.cate_name,@rownum:=@rownum+1
FROM
(
select cate_name from(
select distinct(b.category_id) as cate_name from
fangcheng_global.category as b
) as c
where MOD(cate_name,10000)<>0
) as x,(select @rownum:=-1) as t;