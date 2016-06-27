#dp城市 和平台城市id对应搞关系
select dpCityId,cityId from cityIdMappingInfo
where dpCityId is not null