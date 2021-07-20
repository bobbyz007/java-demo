local key = "rate.limit:" .. KEYS[1] --限流KEY（一秒一个）
local limit = tonumber(ARGV[1]) --限流大小
local expireTime = ARGV[2] --超时时间
local current = tonumber(redis.call('get', key) or "0")
if current + 1 > limit then --如果超出限流大小
    return 0
else --请求数+1，并设置过期时间
    redis.call("INCRBY", key, "1")
    redis.call("expire", key, expireTime)
    return 1
end