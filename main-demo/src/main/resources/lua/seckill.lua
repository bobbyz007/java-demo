local resultFlag = "0"
local n = tonumber(ARGV[1])
local key = KEYS[1]
local goodsInfo = redis.call("HMGET",key,"totalCount","seckillCount")
local total = tonumber(goodsInfo[1])
local alloc = tonumber(goodsInfo[2])
if not total then
	return resultFlag
end
if total >= alloc + n then
	local ret = redis.call("HINCRBY",key,"seckillCount",n)
	return tostring(ret)
end
return resultFlag